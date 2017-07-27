package io.github.sskorol.utils;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.filters.position.FilterPattern;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.intellij.psi.util.PsiUtil.resolveClassInType;
import static java.util.Optional.ofNullable;

public class DataSupplierUtils {
    
    public static boolean isTestDisabled(PsiAnnotation annotation) {
        return ofNullable(annotation.findDeclaredAttributeValue("enabled"))
                .filter(val -> val.textMatches("false"))
                .isPresent();
    }
    
    public static PsiClass getDataProviderClass(final PsiElement element, final PsiClass topLevelClass) {
        return ofNullable(getParentOfType(element, PsiAnnotation.class))
                .map(a -> a.findDeclaredAttributeValue("dataProviderClass"))
                .filter(val -> val instanceof PsiClassObjectAccessExpression)
                .map(val -> resolveClassInType(((PsiClassObjectAccessExpression)val).getOperand().getType()))
                .orElse(topLevelClass);
    }

    public static PsiElementPattern.Capture<PsiLiteral> getDataProviderPattern() {
        return psiElement(PsiLiteral.class).and(new FilterPattern(new TestAnnotationFilter("dataProvider")));
    }

    private static class TestAnnotationFilter implements ElementFilter {

        private final String parameterName;

        TestAnnotationFilter(@NotNull @NonNls final String parameterName) {
            this.parameterName = parameterName;
        }

        public boolean isAcceptable(final Object element, final PsiElement context) {
            return ofNullable(getParentOfType(context, PsiNameValuePair.class, false, PsiMember.class, PsiStatement.class))
                    .filter(p -> parameterName.equals(p.getName()))
                    .map(p -> getParentOfType(p, PsiAnnotation.class))
                    .filter(a -> Test.class.getName().equals(a.getQualifiedName()))
                    .isPresent();
        }

        public boolean isClassAcceptable(final Class hintClass) {
            return PsiLiteral.class.isAssignableFrom(hintClass);
        }
    }
}
