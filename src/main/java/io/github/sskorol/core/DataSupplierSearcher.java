package io.github.sskorol.core;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

import static com.intellij.codeInsight.AnnotationUtil.findAnnotation;
import static com.intellij.openapi.util.text.StringUtil.unquoteString;
import static com.intellij.psi.search.UsageSearchContext.IN_STRINGS;
import static java.util.Optional.ofNullable;

public class DataSupplierSearcher extends QueryExecutorBase<PsiReference, MethodReferencesSearch.SearchParameters> {

    public DataSupplierSearcher() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull final MethodReferencesSearch.SearchParameters queryParameters,
                             @NotNull final Processor<PsiReference> consumer) {
        ofNullable(findAnnotation(queryParameters.getMethod(), DataSupplier.class.getName()))
                .map(a -> a.findDeclaredAttributeValue("name"))
                .map(n -> unquoteString(n.getText()))
                .ifPresent(name -> queryParameters.getOptimizer().searchWord(
                        name,
                        queryParameters.getEffectiveSearchScope(),
                        IN_STRINGS,
                        true,
                        queryParameters.getMethod()));
    }
}
