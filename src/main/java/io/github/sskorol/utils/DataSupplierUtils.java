package io.github.sskorol.utils;

import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.filters.position.FilterPattern;
import io.vavr.Tuple;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.psi.search.GlobalSearchScope.moduleScope;
import static com.intellij.psi.search.searches.ClassInheritorsSearch.search;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.intellij.psi.util.PsiUtil.resolveClassInType;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

public class DataSupplierUtils {

    public static final String TEST_ANNOTATION_PATH = "org.testng.annotations.Test";
    public static final String FACTORY_ANNOTATION_PATH = "org.testng.annotations.Factory";
    public static final String DATA_SUPPLIER_ANNOTATION_PATH = "io.github.sskorol.core.DataSupplier";

    public static boolean isTestDisabled(PsiAnnotation annotation) {
        return ofNullable(annotation.findDeclaredAttributeValue("enabled"))
                .filter(val -> val.textMatches("false"))
                .isPresent();
    }

    public static PsiClass getDataProviderClass(final PsiElement element, final PsiClass topLevelClass) {
        var cl = ofNullable(getParentOfType(element, PsiAnnotation.class))
                .flatMap(toDataProviderClass())
                .orElse(getDataProviderClass(topLevelClass));
        return cl;
    }

    public static PsiClass getDataProviderClass(final PsiClass topLevelClass) {
        var cl = ofNullable(topLevelClass)
                .map(topClass -> Tuple.of(topClass, ProjectRootManager.getInstance(topLevelClass.getProject())
                        .getFileIndex()
                        .getModuleForFile(topLevelClass.getContainingFile().getVirtualFile())))
                .filter(t -> Objects.nonNull(t._2))
                .map(t -> search(t._1, moduleScope(t._2), true))
                .flatMap(query -> StreamEx.of(query.findAll())
                        .flatArray(PsiModifierListOwner::getAnnotations)
                        .findFirst(annotation -> nonNull(toDataProviderAttribute().apply(annotation))))
                .flatMap(toDataProviderClass())
                .orElse(topLevelClass);
        return cl;
    }

    public static Function<PsiAnnotation, Optional<PsiClass>> toDataProviderClass() {
        return annotation -> ofNullable(annotation)
                .map(toDataProviderAttribute())
                .filter(val -> val instanceof PsiClassObjectAccessExpression)
                .map(val -> resolveClassInType(((PsiClassObjectAccessExpression) val).getOperand().getType()));
    }

    public static Function<PsiAnnotation, PsiAnnotationMemberValue> toDataProviderAttribute() {
        return annotation -> annotation.findDeclaredAttributeValue("dataProviderClass");
    }

    public static PsiElementPattern.Capture<PsiLiteral> getDataProviderPattern() {
        return psiElement(PsiLiteral.class).and(new FilterPattern(new AnnotationFilter("dataProvider")));
    }

    private record AnnotationFilter(String parameterName) implements ElementFilter {
        private AnnotationFilter(@NotNull @NonNls final String parameterName) {
            this.parameterName = parameterName;
        }

        public boolean isAcceptable(final Object element, final PsiElement context) {
            return ofNullable(getParentOfType(context, PsiNameValuePair.class, false, PsiMember.class, PsiStatement.class))
                    .filter(p -> parameterName.equals(p.getName()))
                    .map(p -> getParentOfType(p, PsiAnnotation.class))
                    .map(PsiAnnotation::getQualifiedName)
                    .filter(name -> TEST_ANNOTATION_PATH.equals(name) || FACTORY_ANNOTATION_PATH.equals(name))
                    .isPresent();
        }

        public boolean isClassAcceptable(final Class hintClass) {
            return PsiLiteral.class.isAssignableFrom(hintClass);
        }
    }
}
