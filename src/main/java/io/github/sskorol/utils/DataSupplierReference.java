package io.github.sskorol.utils;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import io.github.sskorol.dataprovider.DataSupplier;
import io.vavr.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

import static com.intellij.codeInsight.AnnotationUtil.findAnnotation;
import static com.intellij.openapi.util.text.StringUtil.unquoteString;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.intellij.psi.util.PsiUtil.getTopLevelClass;
import static io.github.sskorol.utils.DataSupplierUtils.getDataProviderClass;
import static java.util.Optional.ofNullable;

public class DataSupplierReference extends PsiReferenceBase<PsiLiteral> {

    public DataSupplierReference(PsiLiteral element) {
        super(element, false);
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return element instanceof PsiMethod
                ? handleElementRename(((PsiMethod) element).getName())
                : super.bindToElement(element);
    }

    @Nullable
    public PsiElement resolve() {
        return ofNullable(getDataProviderClass(getElement(), getTopLevelClass(getElement())))
                .flatMap(c -> Stream.of(c.getAllMethods())
                                    .map(m -> Tuple.of(m, findAnnotation(m, DataSupplier.class.getName())))
                                    .filter(t -> Objects.nonNull(t._2))
                                    .map(t -> Tuple.of(t._1, t._2.findDeclaredAttributeValue("name")))
                                    .filter(t -> isResolvable(t._2, t._1.getName()))
                                    .map(t -> t._1)
                                    .findFirst())
                .orElse(null);
    }

    @NotNull
    public Object[] getVariants() {
        return ofNullable(getDataProviderClass(getElement(), getTopLevelClass(getElement())))
                .map(cls -> Stream.of(cls.getAllMethods())
                                  .filter(m -> ofNullable(getParentOfType(getElement(), PsiMethod.class))
                                          .filter(c -> m.getName().equals(c.getName())).isPresent())
                                  .filter(m -> cls == m.getContainingClass() && m.hasModifierProperty(PsiModifier.PUBLIC))
                                  .map(m -> Tuple.of(m, findAnnotation(m, DataSupplier.class.getName())))
                                  .filter(t -> Objects.nonNull(t._2))
                                  .map(t -> Tuple.of(t._1, t._2.findDeclaredAttributeValue("name")))
                                  .map(t -> Objects.nonNull(t._2)
                                          ? LookupElementBuilder.create(unquoteString(t._2.getText()))
                                          : LookupElementBuilder.create(t._1.getName()))
                                  .toArray())
                .orElse(new Object[0]);
    }

    private boolean isResolvable(final PsiAnnotationMemberValue dataProviderMethodName, final String methodName) {
        return dataProviderMethodName != null && getValue().equals(unquoteString(dataProviderMethodName.getText()))
                || dataProviderMethodName == null && getValue().equals(methodName);
    }
}