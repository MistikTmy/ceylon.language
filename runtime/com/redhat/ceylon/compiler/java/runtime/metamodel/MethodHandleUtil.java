package com.redhat.ceylon.compiler.java.runtime.metamodel;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import com.redhat.ceylon.compiler.java.Util;
import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;

public class MethodHandleUtil {

    public static MethodHandle insertReifiedTypeArguments(MethodHandle constructor, int insertAt, List<ProducedType> typeArguments) {
        Object[] typeDescriptors = new TypeDescriptor[typeArguments.size()];
        for(int i=0;i<typeDescriptors.length;i++){
            typeDescriptors[i] = Metamodel.getTypeDescriptorForProducedType(typeArguments.get(i));
        }
        return MethodHandles.insertArguments(constructor, insertAt, typeDescriptors);
    }

    public static MethodHandle unboxArguments(MethodHandle method, int skippedParameters, int filterIndex, 
            java.lang.Class<?>[] parameterTypes,
            List<ProducedType> producedTypes) {
        return unboxArguments(method, skippedParameters, filterIndex, parameterTypes, parameterTypes.length, producedTypes, false);
    }
    
    public static MethodHandle unboxArguments(MethodHandle method, int skippedParameters, int filterIndex, 
            java.lang.Class<?>[] parameterTypes,
            List<ProducedType> producedTypes,
            boolean variadic, boolean bindVariadicParameterToEmptyArray) {
        if(bindVariadicParameterToEmptyArray){
            // filter all but the last parameter
            MethodHandle ret = unboxArguments(method, skippedParameters, filterIndex, parameterTypes, parameterTypes.length-1,
                                              producedTypes, false); // do not consider it variadic because we're ignoring the last parameter
            // fix the last argument
            java.lang.Class<?> paramType = parameterTypes[parameterTypes.length-1];
            Object val;
            if(paramType == byte[].class)
                val = new byte[0];
            else if(paramType == short[].class)
                val = new short[0];
            else if(paramType == int[].class)
                val = new int[0];
            else if(paramType == long[].class)
                val = new long[0];
            else if(paramType == float[].class)
                val = new float[0];
            else if(paramType == double[].class)
                val = new double[0];
            else if(paramType == boolean[].class)
                val = new boolean[0];
            else if(paramType == char[].class)
                val = new char[0];
            else if(paramType == java.lang.Object[].class)
                val = new java.lang.Object[0];
            else
                val = Array.newInstance(paramType.getComponentType(), 0);
            return MethodHandles.insertArguments(ret, parameterTypes.length-1-skippedParameters, val);
        }else{
            return unboxArguments(method, skippedParameters, filterIndex, parameterTypes, parameterTypes.length,
                                  producedTypes, variadic);
        }
    }

    public static MethodHandle unboxArguments(MethodHandle method, int skippedParameters, int filterIndex, 
                                              java.lang.Class<?>[] parameterTypes, int parameterCount, 
                                              List<ProducedType> producedTypes,
                                              boolean variadic) {
        MethodHandle[] filters = new MethodHandle[parameterCount - skippedParameters];
        try {
            for(int i=0;i<filters.length;i++){
                java.lang.Class<?> paramType = parameterTypes[i + skippedParameters];
                ProducedType producedType = producedTypes.get(i);
                if(variadic && i == filters.length - 1){
                    // we need to convert our ArraySequence instance to a T[] or primitive array
                    String methodName = null;
                    Object empty = null;
                    java.lang.Class<?> initialParamType = null;
                    if(paramType == boolean[].class){
                        methodName = "toBooleanArray";
                        empty = new boolean[0];
                        initialParamType = paramType;
                    }else if(paramType == byte[].class){
                        methodName = "toByteArray";
                        empty = new long[0];
                        initialParamType = long[].class;
                    }else if(paramType == short[].class){
                        methodName = "toShortArray";
                        empty = new long[0];
                        initialParamType = long[].class;
                    }else if(paramType == int[].class){
                        methodName = "toIntArray";
                        empty = new long[0];
                        initialParamType = long[].class;
                    }else if(paramType == long[].class){
                        methodName = "toLongArray";
                        empty = new long[0];
                        initialParamType = long[].class;
                    }else if(paramType == float[].class){
                        methodName = "toFloatArray";
                        empty = new double[0];
                        initialParamType = double[].class;
                    }else if(paramType == double[].class){
                        methodName = "toDoubleArray";
                        empty = new double[0];
                        initialParamType = double[].class;
                    }else if(paramType == char[].class){
                        methodName = "toCharArray";
                        empty = new int[0];
                        initialParamType = int[].class;
                    }else if(paramType == java.lang.String[].class){
                        methodName = "toJavaStringArray";
                        empty = new java.lang.String[0];
                        initialParamType = paramType;
                    }
                    if(methodName != null && empty != null && initialParamType != null){
                        MethodHandle convert = MethodHandles.lookup().findStatic(Util.class, methodName, 
                                                                                 MethodType.methodType(paramType, 
                                                                                                       ceylon.language.List.class, 
                                                                                                       initialParamType));
                        // get rid of the second argument by fixing it to an empty array
                        convert = MethodHandles.insertArguments(convert, 1, empty);
                        filters[i] = convert.asType(MethodType.methodType(paramType, java.lang.Object.class));
                    }else{
                        // non-primitive object, use the array type
                        // if we have a T... and T has bounds "B0 & B1 ...", the underlying type will be B0[]
                        // if there are no bounds we will have Object[] so we're all good
                        MethodHandle convert = MethodHandles.lookup().findStatic(Util.class, "toArray", 
                                                                                 MethodType.methodType(java.lang.Object[].class,
                                                                                                       ceylon.language.List.class,
                                                                                                       java.lang.Class.class,
                                                                                                       java.lang.Object[].class));
                        // get rid of the second argument by fixing it to the array type
                        // get rid of the third argument by fixing it to an empty array
                        convert = MethodHandles.insertArguments(convert, 1, paramType.getComponentType(), new java.lang.Object[0]);
                        filters[i] = convert.asType(MethodType.methodType(paramType, java.lang.Object.class));
                    }
                }else if(paramType == java.lang.String.class){
                    // ((ceylon.language.String)obj).toString()
                    MethodHandle unbox = MethodHandles.lookup().findVirtual(ceylon.language.String.class, "toString", 
                                                                               MethodType.methodType(java.lang.String.class));
                    filters[i] = unbox.asType(MethodType.methodType(java.lang.String.class, java.lang.Object.class));
                }else if(paramType == byte.class 
                        || paramType == short.class
                        || (paramType == int.class && !isCeylonCharacter(producedType))){
                    // (paramType)((ceylon.language.Integer)obj).longValue()
                    MethodHandle unbox = MethodHandles.lookup().findVirtual(ceylon.language.Integer.class, "longValue", 
                                                                             MethodType.methodType(long.class));
                    filters[i] = MethodHandles.explicitCastArguments(unbox, MethodType.methodType(paramType, java.lang.Object.class));
                }else if(paramType == long.class){
                    // ((ceylon.language.Integer)obj).longValue()
                    MethodHandle unbox = MethodHandles.lookup().findVirtual(ceylon.language.Integer.class, "longValue", 
                                                                             MethodType.methodType(long.class));
                    filters[i] = unbox.asType(MethodType.methodType(long.class, java.lang.Object.class));
                }else if(paramType == float.class){
                    // (float)((ceylon.language.Float)obj).doubleValue()
                    MethodHandle unbox = MethodHandles.lookup().findVirtual(ceylon.language.Float.class, "doubleValue", 
                                                                             MethodType.methodType(double.class));
                    filters[i] = MethodHandles.explicitCastArguments(unbox, MethodType.methodType(float.class, java.lang.Object.class));
                }else if(paramType == double.class){
                    // ((ceylon.language.Float)obj).doubleValue()
                    MethodHandle unbox = MethodHandles.lookup().findVirtual(ceylon.language.Float.class, "doubleValue", 
                                                                             MethodType.methodType(double.class));
                    filters[i] = unbox.asType(MethodType.methodType(double.class, java.lang.Object.class));
                }else if(paramType == int.class && isCeylonCharacter(producedType)){
                    // ((ceylon.language.Character)obj).intValue()
                    MethodHandle unbox = MethodHandles.lookup().findVirtual(ceylon.language.Character.class, "intValue", 
                                                                             MethodType.methodType(int.class));
                    filters[i] = unbox.asType(MethodType.methodType(int.class, java.lang.Object.class));
                }else if(paramType == char.class){
                    // ((ceylon.language.Character)obj).charValue()
                    MethodHandle unbox = MethodHandles.lookup().findVirtual(ceylon.language.Character.class, "intValue", 
                                                                             MethodType.methodType(int.class));
                    filters[i] = MethodHandles.explicitCastArguments(unbox, MethodType.methodType(char.class, java.lang.Object.class));
                }else if(paramType == boolean.class){
                    // ((ceylon.language.Boolean)obj).booleanValue()
                    MethodHandle unbox = MethodHandles.lookup().findVirtual(ceylon.language.Boolean.class, "booleanValue", 
                                                                             MethodType.methodType(boolean.class));
                    filters[i] = unbox.asType(MethodType.methodType(boolean.class, java.lang.Object.class));
                }else if(paramType != java.lang.Object.class){
                    // just cast from Object to type
                    MethodHandle unbox = MethodHandles.identity(java.lang.Object.class);
                    filters[i] = unbox.asType(MethodType.methodType(paramType, java.lang.Object.class));
                }
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to filter parameter", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to filter parameter", e);
        }
        return MethodHandles.filterArguments(method, filterIndex, filters);
    }

    private static boolean isCeylonCharacter(ProducedType producedType) {
        if(producedType == null)
            return false;
        TypeDeclaration declaration = producedType.getDeclaration();
        if(declaration instanceof com.redhat.ceylon.compiler.typechecker.model.Class == false)
            return false;
        // this is probably the fastest check we can make
        return declaration.getQualifiedNameString().equals("ceylon.language::Character");
    }

    public static MethodHandle boxReturnValue(MethodHandle method, java.lang.Class<?> type, ProducedType producedType) {
        try {
            // FIXME: more boxing for interop
            if(type == java.lang.String.class){
                // ceylon.language.String.instance(obj)
                MethodHandle box = MethodHandles.lookup().findStatic(ceylon.language.String.class, "instance", 
                                                        MethodType.methodType(ceylon.language.String.class, java.lang.String.class));
                method = MethodHandles.filterReturnValue(method, box);
            }else if(type == int.class && isCeylonCharacter(producedType)){
                // ceylon.language.Character.instance(obj)
                MethodHandle box = MethodHandles.lookup().findStatic(ceylon.language.Character.class, "instance", 
                                                                     MethodType.methodType(ceylon.language.Character.class, int.class));
                return MethodHandles.filterReturnValue(method, box);
            }else if(type == byte.class || type == int.class || type == short.class){
                // ceylon.language.Integer.instance((long)obj)
                MethodHandle box = MethodHandles.lookup().findStatic(ceylon.language.Integer.class, "instance", 
                                                                     MethodType.methodType(ceylon.language.Integer.class, long.class));
                box = box.asType(MethodType.methodType(ceylon.language.Integer.class, type));
                return MethodHandles.filterReturnValue(method, box);
            }else if(type == long.class){
                // ceylon.language.Integer.instance(obj)
                MethodHandle box = MethodHandles.lookup().findStatic(ceylon.language.Integer.class, "instance", 
                                                                     MethodType.methodType(ceylon.language.Integer.class, long.class));
                return MethodHandles.filterReturnValue(method, box);
            }else if(type == double.class){
                // ceylon.language.Float.instance(obj)
                MethodHandle box = MethodHandles.lookup().findStatic(ceylon.language.Float.class, "instance", 
                                                                     MethodType.methodType(ceylon.language.Float.class, double.class));
                return MethodHandles.filterReturnValue(method, box);
            }else if(type == float.class){
                // ceylon.language.Float.instance((double)obj)
                MethodHandle box = MethodHandles.lookup().findStatic(ceylon.language.Float.class, "instance", 
                                                                     MethodType.methodType(ceylon.language.Float.class, double.class));
                box = box.asType(MethodType.methodType(ceylon.language.Float.class, type));
                return MethodHandles.filterReturnValue(method, box);
            }else if(type == char.class){
                // ceylon.language.Character.instance((int)obj)
                MethodHandle box = MethodHandles.lookup().findStatic(ceylon.language.Character.class, "instance", 
                                                                     MethodType.methodType(ceylon.language.Character.class, int.class));
                box = box.asType(MethodType.methodType(ceylon.language.Character.class, type));
                return MethodHandles.filterReturnValue(method, box);
            }else if(type == boolean.class){
                // ceylon.language.Boolean.instance(obj)
                MethodHandle box = MethodHandles.lookup().findStatic(ceylon.language.Boolean.class, "instance", 
                                                                     MethodType.methodType(ceylon.language.Boolean.class, boolean.class));
                return MethodHandles.filterReturnValue(method, box);
            }
            return method;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to filter return value", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to filter return value", e);
        }
    }

    public static boolean isReifiedTypeSupported(Object methodOrConstructor, boolean skipFirstParameter) {
        int tpCount;
        Class<?>[] parameterTypes;
        Annotation[][] annotations;
        boolean checkIgnoreAnnotations;
        if(methodOrConstructor instanceof Method){
            Method method = (Method) methodOrConstructor;
            tpCount = method.getTypeParameters().length;
            parameterTypes = method.getParameterTypes();
            annotations = method.getParameterAnnotations();
            checkIgnoreAnnotations = method.getAnnotation(Ignore.class) == null;
        }else{
            Constructor<?> constructor = (Constructor<?>) methodOrConstructor;
            tpCount = constructor.getTypeParameters().length;
            parameterTypes = constructor.getParameterTypes();
            annotations = constructor.getParameterAnnotations();
            checkIgnoreAnnotations = constructor.getAnnotation(Ignore.class) == null;
        }
        int start = skipFirstParameter ? 1 : 0;
        // without the instance parameter, does it have enough parameters for the type descriptors?
        if(tpCount > parameterTypes.length - start)
            return false;
        // BEWARE: if getParameterTypes has same length as getParameterAnnotations then synthetic parameters are included
        // but if they differ, they are not included
        int annotationOffset = annotations.length - parameterTypes.length;
        for(int i = 0 ; i < tpCount ; i++ ){
            if(parameterTypes[i + start] != TypeDescriptor.class)
                return false;
            // if its container is not marked with @Ignore, the parameter must also be marked with @Ignore
            if(checkIgnoreAnnotations && !hasAnnotation(Ignore.class, annotations[i + start - annotationOffset]))
                return false;
        }
        // all good
        return true;
    }

    private static boolean hasAnnotation(Class<?> toFind, Annotation[] annotations) {
        for(Annotation annotation : annotations){
            if(toFind == annotation.annotationType())
                return true;
        }
        return false;
    }

    public static boolean isVariadicMethodOrConstructor(Object found) {
        if(found instanceof java.lang.reflect.Constructor){
            return ((java.lang.reflect.Constructor<?>)found).isVarArgs();
        }else{
            return ((java.lang.reflect.Method)found).isVarArgs();
        }
    }
}
