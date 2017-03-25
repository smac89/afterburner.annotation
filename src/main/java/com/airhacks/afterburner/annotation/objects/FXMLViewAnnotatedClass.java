package com.airhacks.afterburner.annotation.objects;

import com.airhacks.afterburner.annotation.FXMLView;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.BiConsumer;

/**
 * Created by smac89 on 3/24/2017.
 */
public class FXMLViewAnnotatedClass {

    private TypeElement annotatedClassElement;
    private String      presenterName;
    private URI         cssLocation;
    private URI         fxmlLocation;

    public FXMLViewAnnotatedClass(TypeElement classElement, Messager messager) {
        annotatedClassElement = classElement;
        FXMLView annotation = classElement.getAnnotation(FXMLView.class);

        try {
            Class<?> clazz = annotation.presenter();
            presenterName = clazz.getCanonicalName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            presenterName = ((TypeElement) classTypeMirror.asElement()).getQualifiedName().toString();
        }

        if (!annotation.css().isEmpty()) {
            cssLocation = loadFile(annotation.css(),
                    (kind, msg) -> messager.printMessage(kind, msg, classElement));
        }

        if (!annotation.fxml().isEmpty()) {
            fxmlLocation = loadFile(annotation.fxml(),
                    (kind, msg) -> messager.printMessage(kind, msg, classElement));
        }
    }

    public TypeElement getAnnotatedClassElement() {
        return annotatedClassElement;
    }

    /**
     * Get the fully qualified name of the presenter specified in {@link FXMLView#presenter()}
     *
     * @return qualified name
     */
    public String getPresenterName() {
        return presenterName;
    }

    /**
     * Get the URI representing the location of the css file for this view specified in {@link FXMLView#css()}
     *
     * @return css URI or null if not found
     */
    public URI getCssLocation() {
        return cssLocation;
    }

    /**
     * Get the URI representing the location of the fxml file specified in {@link FXMLView#fxml()}
     *
     * @return fxml URI or null if not found
     */
    public URI getFxmlLocation() {
        return fxmlLocation;
    }

    /**
     * Utility method to attempt to load resources
     *
     * @param path   The path for the resource
     * @param logger The logger to be used to reporting errors
     * @return The URI of the path or null if not found
     */
    private URI loadFile(String path, BiConsumer<Diagnostic.Kind, String> logger) {
        try {
            return ClassLoader.getSystemResource(path).toURI();
        } catch (URISyntaxException | NullPointerException ignored) {
        }
        try {
            return URI.create(ClassLoader.getSystemResource("/" + path).toExternalForm());
        } catch (IllegalArgumentException | NullPointerException e) {
            logger.accept(Diagnostic.Kind.ERROR,
                    String.format("Could not load the css file: %s", path));
        }
        return null;
    }
}
