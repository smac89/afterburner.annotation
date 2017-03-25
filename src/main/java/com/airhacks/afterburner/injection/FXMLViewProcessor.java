package com.airhacks.afterburner.injection;

import com.airhacks.afterburner.annotation.FXMLView;
import com.airhacks.afterburner.annotation.objects.FXMLViewAnnotatedClass;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by smac89 on 3/24/2017.
 */
public class FXMLViewProcessor extends AbstractProcessor {

    private static Set<String> supportedAnnotations = Arrays.stream(new String[]{
            FXMLView.class.getCanonicalName()
    }).collect(Collectors.toSet());
    private Messager messager;

    /**
     * {@inheritDoc}
     *
     * @param annotations the annotations supported by this processor which were found
     * @param roundEnv
     * @return true if able to process the annotations
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(FXMLView.class)) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                errorMsg(annotatedElement, "Only classes can be annotated with @%s",
                        FXMLView.class.getSimpleName());
                return true;
            }

            FXMLViewAnnotatedClass annotatedClass = new FXMLViewAnnotatedClass((TypeElement) annotatedElement, messager);
            if (!isValidClass(annotatedClass)) {
                return true;
            }
        }

        return true;
    }

    private boolean isValidClass(FXMLViewAnnotatedClass item) {
        TypeElement classElement = item.getAnnotatedClassElement();

        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            errorMsg(classElement, "The class must be public");
            return false;
        }
        return true;
    }

    private void errorMsg(Element e, String msg, Object... args) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedAnnotations;
    }
}
