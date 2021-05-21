package com.blamejared.crafttweaker_annotation_processors.processors.validation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes({"com.blamejared.crafttweaker.api.annotations.Preprocessor"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PreprocessorAnnotationValidationProcessor extends AbstractProcessor {
    
    private static final String preprocessorInterfaceCanonicalName = "com.blamejared.crafttweaker.api.zencode.IPreprocessor";
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        
        for(TypeElement annotation : annotations) {
            for(Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if(!(element instanceof TypeElement)) {
                    this.processingEnv.getMessager()
                            .printMessage(Diagnostic.Kind.ERROR, "How is this annotated?", element);
                    continue;
                }
                
                if(!this.processingEnv.getTypeUtils()
                        .isAssignable(element.asType(), this.processingEnv.getElementUtils()
                                .getTypeElement(preprocessorInterfaceCanonicalName)
                                .asType())) {
                    this.processingEnv.getMessager()
                            .printMessage(Diagnostic.Kind.ERROR, "Element is annotated as Preprocessor but is not assignable to " + preprocessorInterfaceCanonicalName + "!", element);
                }
                
                //Not sure if the implicit no-arg constructor is already present here,
                // so let's just check no other constructor was found
                boolean anyConstructorFound = false;
                boolean noArgPublicConstructorFound = false;
                boolean instanceFieldFound = false;
                
                for(Element enclosedElement : element.getEnclosedElements()) {
                    if(enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                        anyConstructorFound = true;
                        ExecutableElement constructor = (ExecutableElement) enclosedElement;
                        final boolean noArg = constructor.getParameters().isEmpty();
                        final boolean isPublic = constructor.getModifiers().contains(Modifier.PUBLIC);
                        if(noArg && isPublic) {
                            noArgPublicConstructorFound = true;
                            break;
                        }
                    }
                    if(enclosedElement.getKind() == ElementKind.FIELD) {
                        final VariableElement field = (VariableElement) enclosedElement;
                        if(!field.getModifiers().contains(Modifier.STATIC)
                                || !field.getModifiers().contains(Modifier.PUBLIC)) {
                            continue;
                        }
                        
                        if(field.asType().equals(element.asType())) {
                            instanceFieldFound = true;
                        }
                    }
                }
                
                if(!noArgPublicConstructorFound && anyConstructorFound && !instanceFieldFound) {
                    this.processingEnv.getMessager()
                            .printMessage(Diagnostic.Kind.ERROR, "Element is annotated as Preprocessor but has no INSTANCE field nor a public no-arg constructor!", element);
                }
            }
        }
        
        return false;
    }
    
}
