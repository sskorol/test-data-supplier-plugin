package io.github.sskorol.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;
import com.intellij.psi.xml.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

import static com.intellij.psi.search.GlobalSearchScope.moduleScope;
import static java.util.Optional.ofNullable;

public class TestCaseReference extends PsiReferenceBase<XmlAttributeValue> {

    public TestCaseReference(final XmlAttributeValue element) {
        super(element, false);
    }

    @Override
    public PsiElement bindToElement(final @NotNull PsiElement element) throws IncorrectOperationException {
        return element instanceof PsiMethod
                ? handleElementRename(((PsiMethod) element).getName())
                : super.bindToElement(element);
    }

    @Nullable
    public PsiElement resolve() {
        return Stream.iterate(getElement().getParent(), element -> element instanceof XmlTag || element instanceof XmlAttribute, PsiElement::getParent)
                .filter(element -> element instanceof XmlTag)
                .map(element -> (XmlTag) element)
                .filter(tag -> tag.getLocalName().equals("class"))
                .map(tag -> tag.getAttribute("name"))
                .filter(Objects::nonNull)
                .map(XmlAttribute::getValue)
                .filter(Objects::nonNull)
                .map(this::findMethod)
                .findFirst()
                .orElse(getElement());
    }

    private PsiElement findMethod(final String className) {
        final PsiElement parent = getElement().getParent();
        final String methodName = ofNullable(((XmlAttribute) parent).getValue()).orElse("");
        final Project project = parent.getProject();
        final Module module = ProjectRootManager.getInstance(project)
                .getFileIndex()
                .getModuleForFile(parent.getContainingFile().getVirtualFile());

        if (module == null) {
            return getElement();
        }

        return ofNullable(JavaPsiFacade.getInstance(project).findClass(className, moduleScope(module)))
                .map(psiClass -> psiClass.findMethodsByName(methodName, false))
                .filter(methods -> methods.length > 0)
                .map(methods -> (PsiElement) methods[0])
                .orElse(getElement());
    }
}
