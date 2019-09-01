package io.github.sskorol.core;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

import static com.intellij.codeInsight.AnnotationUtil.findAnnotation;
import static com.intellij.openapi.util.text.StringUtil.unquoteString;
import static com.intellij.psi.search.UsageSearchContext.IN_STRINGS;
import static io.github.sskorol.utils.DataSupplierUtils.DATA_SUPPLIER_ANNOTATION_PATH;
import static java.util.Optional.ofNullable;

public class DataSupplierSearcher extends QueryExecutorBase<PsiReference, MethodReferencesSearch.SearchParameters> {

    public DataSupplierSearcher() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull final MethodReferencesSearch.SearchParameters queryParameters,
                             @NotNull final Processor<? super PsiReference> consumer) {
        ofNullable(findAnnotation(queryParameters.getMethod(), DATA_SUPPLIER_ANNOTATION_PATH))
                .map(a -> a.findDeclaredAttributeValue("name"))
                .map(n -> unquoteString(n.getText()))
                .ifPresent(name -> searchWord(name, queryParameters));
    }

    private void searchWord(final String name, final MethodReferencesSearch.SearchParameters queryParameters) {
        queryParameters.getOptimizer().searchWord(
                name,
                queryParameters.getEffectiveSearchScope(),
                IN_STRINGS,
                true,
                queryParameters.getMethod());
    }
}
