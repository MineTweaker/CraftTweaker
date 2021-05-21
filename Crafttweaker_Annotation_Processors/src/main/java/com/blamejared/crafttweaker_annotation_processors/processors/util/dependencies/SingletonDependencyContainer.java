package com.blamejared.crafttweaker_annotation_processors.processors.util.dependencies;

import javax.annotation.Nonnull;
import javax.annotation.processing.*;
import javax.tools.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

public class SingletonDependencyContainer implements DependencyContainer {
    
    private final Map<Class<?>, Object> alreadyExistingInstances = new HashMap<>();
    
    @Override
    public <Type> Type getInstanceOfClass(Class<Type> cls) {
        initializeInstanceIfNotExists(cls);
        return getInstance(cls);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <Type> void addInstance(Type instance) {
        addInstanceAs(instance, (Class<Type>) instance.getClass());
    }
    
    @Override
    public <Type, Instance extends Type> void addInstanceAs(Instance instance, Class<Type> as) {
        validateNoDuplicate(as);
        insertInstanceAs(instance, as);
    }
    
    @SuppressWarnings("unchecked")
    private <Type> Type getInstance(Class<Type> cls) {
        return (Type) alreadyExistingInstances.get(cls);
    }
    
    private <Type> void initializeInstanceIfNotExists(Class<Type> cls) {
        if(!instanceAlreadyExists(cls)) {
            verifyClassCanBeInitialized(cls);
            initializeInstance(cls);
            callPostInitSteps(cls);
        }
    }
    
    private <Type> void verifyClassCanBeInitialized(Class<Type> cls) {
        if(cls.isInterface()) {
            final String canonicalName = cls.getCanonicalName();
            throw new IllegalArgumentException("Cannot instantiate interface: " + canonicalName);
        }
        if(Modifier.isAbstract(cls.getModifiers())) {
            final String canonicalName = cls.getCanonicalName();
            throw new IllegalArgumentException("Cannot instantiate abstract class: " + canonicalName);
        }
    }
    
    private <Type> void callPostInitSteps(Class<Type> cls) {
        final Type instanceOfClass = getInstanceOfClass(cls);
        if(instanceOfClass instanceof IHasPostCreationCall) {
            ((IHasPostCreationCall) instanceOfClass).afterCreation();
        }
    }
    
    private <Type> void initializeInstance(Class<Type> cls) {
        final Optional<Type> instance = Arrays.stream(cls.getConstructors()).<Type> map(this::tryCreateInstanceWith)
                .filter(Objects::nonNull)
                .findFirst();
        
        if(instance.isPresent()) {
            insertInstanceAs(instance.get(), cls);
        } else {
            throw new IllegalArgumentException("Could not initialize class: " + cls.getCanonicalName());
        }
    }
    
    @SuppressWarnings("unchecked")
    private <Type> Type tryCreateInstanceWith(Constructor<?> constructor) {
        try {
            final Object[] arguments = getArguments(constructor);
            return (Type) constructor.newInstance(arguments);
        } catch(Exception e) {
            final Messager instanceOfClass = getInstanceOfClass(Messager.class);
            instanceOfClass.printMessage(Diagnostic.Kind.ERROR, String.format("Exception when trying to use %s: %s", constructor, e));
            return null;
        }
    }
    
    @Nonnull
    private Object[] getArguments(Constructor<?> constructor) {
        return Arrays.stream(constructor.getParameterTypes())
                .map(this::getInstanceOfClass)
                .toArray(Object[]::new);
    }
    
    private <Type> void insertInstanceAs(Type instance, Class<Type> as) {
        alreadyExistingInstances.put(as, instance);
    }
    
    private <T> void validateNoDuplicate(Class<T> instance) {
        if(instanceAlreadyExists(instance)) {
            throw new IllegalStateException("Trying to register duplicate!");
        }
    }
    
    private boolean instanceAlreadyExists(Class<?> instanceClass) {
        return alreadyExistingInstances.containsKey(instanceClass);
    }
}
