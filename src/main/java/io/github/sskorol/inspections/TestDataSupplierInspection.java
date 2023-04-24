package io.github.sskorol.inspections;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import io.github.sskorol.utils.DataSupplierReference;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.NotNull;

import static io.github.sskorol.utils.DataSupplierUtils.*;
import static java.util.Optional.of;

public class TestDataSupplierInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, final boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitAnnotation(final @NotNull PsiAnnotation annotation) {
                of(annotation)
                        .filter(a -> (TEST_ANNOTATION_PATH.equals(a.getQualifiedName()) && !isTestDisabled(a))
                                || FACTORY_ANNOTATION_PATH.equals(a.getQualifiedName()))
                        .map(a -> a.findDeclaredAttributeValue("dataProvider"))
                        .ifPresent(provider -> highlightError(provider, holder));
            }
        };
    }

    private void highlightError(final PsiAnnotationMemberValue provider, final ProblemsHolder holder) {
        StreamEx.of(provider.getReferences())
                .findFirst(ref -> ref instanceof DataSupplierReference && !(ref.resolve() instanceof PsiMethod))
                .ifPresent(ref -> holder.registerProblem(provider,
                        "Data supplier does not exist",
                        ProblemHighlightType.ERROR)
                );
    }
}
