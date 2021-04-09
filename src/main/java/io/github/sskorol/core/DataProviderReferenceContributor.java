package io.github.sskorol.core;

import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import io.github.sskorol.utils.DataSupplierReference;
import org.jetbrains.annotations.NotNull;

import static io.github.sskorol.utils.DataSupplierUtils.getDataProviderPattern;

public class DataProviderReferenceContributor extends PsiReferenceContributor {

    public void registerReferenceProviders(final @NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(getDataProviderPattern(), new PsiReferenceProvider() {
            public PsiReference @NotNull [] getReferencesByElement(@NotNull final PsiElement element, @NotNull final ProcessingContext context) {
                return new DataSupplierReference[]{new DataSupplierReference((PsiLiteral) element)};
            }
        });
    }
}
