package com.blamejared.crafttweaker.api.zencode.impl.native_types;

import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import org.objectweb.asm.Type;
import org.openzen.zencode.java.module.JavaNativeTypeConversionContext;
import org.openzen.zencode.java.module.converters.*;
import org.openzen.zencode.shared.CodePosition;
import org.openzen.zencode.shared.logging.IZSLogger;
import org.openzen.zenscript.codemodel.FunctionHeader;
import org.openzen.zenscript.codemodel.Modifiers;
import org.openzen.zenscript.codemodel.definition.ExpansionDefinition;
import org.openzen.zenscript.codemodel.member.MethodMember;
import org.openzen.zenscript.codemodel.type.BasicTypeID;
import org.openzen.zenscript.javashared.JavaClass;
import org.openzen.zenscript.javashared.JavaMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class CrTJavaNativeExpansionConverter extends JavaNativeExpansionConverter {

    private final JavaNativeHeaderConverter headerConverter;
    private final JavaNativeTypeConversionContext typeConversionContext;
    private final JavaNativePackageInfo packageInfo;
    
    public CrTJavaNativeExpansionConverter(JavaNativeTypeConverter typeConverter, IZSLogger logger, JavaNativePackageInfo packageInfo, JavaNativeMemberConverter memberConverter, JavaNativeTypeConversionContext typeConversionContext, JavaNativeHeaderConverter headerConverter) {
        super(typeConverter, logger, packageInfo, memberConverter, typeConversionContext, headerConverter);
        this.headerConverter = headerConverter;
        this.typeConversionContext = typeConversionContext;
        this.packageInfo = packageInfo;
    }
    
    @Override
    protected <T extends Annotation> T getMethodAnnotation(Method method, Class<T> annotationClass) {
        return super.getMethodAnnotation(method, annotationClass);
    }
    
    @Override
    protected String getExpandedName(Class<?> cls) {
        if(cls.isAnnotationPresent(NativeTypeRegistration.class)) {
            final NativeTypeRegistration annotation = cls.getAnnotation(NativeTypeRegistration.class);
            return annotation.zenCodeName();
        }
        return super.getExpandedName(cls);
    }
    
    @Override
    protected boolean doesClassNotHaveAnnotation(Class<?> cls) {
        return !cls.isAnnotationPresent(NativeTypeRegistration.class) && super.doesClassNotHaveAnnotation(cls);
    }

    public void addNativeMethodsToString() {
        final ExpansionDefinition expansion = new ExpansionDefinition(CodePosition.NATIVE, packageInfo.getModule(), packageInfo.getPkg(), Modifiers.PUBLIC, null);
        expansion.target = BasicTypeID.STRING;
        for (Method method : String.class.getDeclaredMethods()) {
            String methodName = method.getName();
            if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()) && isValidParametersForZCStringType(method.getParameters())) {
                FunctionHeader header = headerConverter.getHeader(typeConversionContext.context, method.getAnnotatedReturnType(), method.getParameters(), method.getTypeParameters(), method.getAnnotatedExceptionTypes());
                MethodMember methodMember = new MethodMember(CodePosition.NATIVE, expansion, headerConverter.getMethodModifiers(method), methodName, header, null);

                expansion.addMember(methodMember);
                typeConversionContext.compiled.setMethodInfo(methodMember, JavaMethod.getVirtual(JavaClass.STRING, methodName, Type.getMethodDescriptor(method), headerConverter.getMethodModifiers(method)));
            }
        }
        if (!expansion.members.isEmpty()) {
            typeConversionContext.compiled.setExpansionClassInfo(expansion, JavaClass.STRING);
            typeConversionContext.packageDefinitions.add(expansion);
        }
    }

    private boolean isValidParametersForZCStringType(Parameter[] parameters) {
        return Arrays.stream(parameters).map(Parameter::getType).allMatch(clazz -> clazz.isPrimitive() || clazz.isAssignableFrom(String.class) || clazz.isArray());
    }
}
