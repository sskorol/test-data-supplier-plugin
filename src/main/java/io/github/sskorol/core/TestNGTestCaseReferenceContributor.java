package io.github.sskorol.core;

import com.intellij.patterns.XmlAttributeValuePattern;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ProcessingContext;
import io.github.sskorol.utils.TestCaseReference;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.XmlPatterns.*;

public class TestNGTestCaseReferenceContributor extends PsiReferenceContributor {

    private static final XmlAttributeValuePattern TEST_CASE_PATTERN =
            xmlAttributeValue(xmlAttribute("name")
                    .withParent(xmlTag().withName("include")
                            .withParent(xmlTag().withName("methods")
                                    .withParent(xmlTag().withName("class")
                                            .withParent(xmlTag().withName("classes")
                                                    .withParent(xmlTag().withName("test")
                                                            .withParent(xmlTag().withName("suite"))))))));

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(TEST_CASE_PATTERN, new PsiReferenceProvider() {
            @NotNull
            public PsiReference @NotNull [] getReferencesByElement(
                    @NotNull final PsiElement element,
                    @NotNull final ProcessingContext context
            ) {
                return new TestCaseReference[]{new TestCaseReference((XmlAttributeValue) element)};
            }
        });
    }
}
