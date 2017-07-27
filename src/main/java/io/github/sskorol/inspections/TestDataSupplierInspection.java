package io.github.sskorol.inspections;

import com.intellij.codeInspection.BaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import io.github.sskorol.utils.DataSupplierReference;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import static io.github.sskorol.utils.DataSupplierUtils.isTestDisabled;
import static java.util.Optional.ofNullable;

public class TestDataSupplierInspection extends BaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, final boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitAnnotation(final PsiAnnotation annotation) {
                ofNullable(annotation)
                        .filter(a -> Test.class.getName().equals(a.getQualifiedName()) && !isTestDisabled(a))
                        .map(a -> a.findDeclaredAttributeValue("dataProvider"))
                        .ifPresent(provider -> highlightError(provider, holder));
            }
        };
    }

    private void highlightError(final PsiAnnotationMemberValue provider, final ProblemsHolder holder) {
        StreamEx.of(provider.getReferences())
                .findFirst(ref -> ref instanceof DataSupplierReference && !(ref.resolve() instanceof PsiMethod))
                .ifPresent(ref -> holder.registerProblem(provider, "Data supplier does not exist", ProblemHighlightType.ERROR));
    }
}
