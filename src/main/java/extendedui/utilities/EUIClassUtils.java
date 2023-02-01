package extendedui.utilities;

import basemod.ReflectionHacks;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class EUIClassUtils
{
    public static <T> void getAndSetField(Object o, String fieldName, FuncT1<T, T> valueFunc)
    {
        Class<?> c = o.getClass();
        ReflectionHacks.setPrivate(o, c, fieldName, valueFunc.invoke(ReflectionHacks.getPrivate(o, c, fieldName)));
    }

    // Enforces type of the field without using a variable declaration
    public static <T> T getFieldAsType(Object o, String fieldName, Class<T> type)
    {
        return getField(o, fieldName);
    }

    // DO NOT USE. Dangerous as this method masks subclasses
    @Deprecated
    public static <T> T getField(Object o, String fieldName)
    {
        return ReflectionHacks.getPrivate(o, o.getClass(), fieldName);
    }

    public static <T> T getFieldInheritedAsType(Object o, String fieldName, Class<T> type)
    {
        return ReflectionHacks.getPrivateInherited(o, o.getClass(), fieldName);
    }

    public static <T> T getFieldInherited(Object o, String fieldName)
    {
        return ReflectionHacks.getPrivateInherited(o, o.getClass(), fieldName);
    }

    // Enforces type of the field without using a variable declaration
    public static <T> T getFieldStatic(Class<?> c, String fieldName, Class<T> type)
    {
        return ReflectionHacks.getPrivateStatic(c, fieldName);
    }

    public static <T> ReflectionHacks.RMethod getMethod(Object o, String methodName, Class<?>... parameterTypes)
    {
        return ReflectionHacks.privateMethod(o.getClass(), methodName, parameterTypes);
    }

    public static <T> T getRField(String className, String fieldName, Object object) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException
    {
        return (T) Class.forName(className).getField(fieldName).get(object);
    }

    public static <T> T getRFieldStatic(String className, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException
    {
        return (T) Class.forName(className).getField(fieldName).get(null);
    }

    public static <T> T invoke(Class<?> className, String methodName, Object... parameters)
    {
        return ReflectionHacks.privateStaticMethod(className, methodName, EUIUtils.map(parameters, Object::getClass).toArray(new Class<?>[]{})).invoke(parameters);
    }

    public static <T> T invoke(Object o, String methodName, Object... parameters)
    {
        return getMethod(o, methodName, EUIUtils.map(parameters, Object::getClass).toArray(new Class<?>[]{})).invoke(o, parameters);
    }

    public static <T> T invokeR(String className, String methodName, Object object) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method m = Class.forName(className).getMethod(methodName);
        return (T) m.invoke(object);
    }

    public static <T> T invokeRStatic(String className, String methodName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method m = Class.forName(className).getMethod(methodName);
        return (T) m.invoke(null);
    }

    public static <T> T invokeRStatic(String className, String methodName, Object... parameters) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Class<?>[] classList = EUIUtils.arrayMap(parameters, Class.class, Object::getClass);
        Method m = Class.forName(className).getMethod(methodName, classList);
        return (T) m.invoke(null, parameters);
    }

    public static boolean isFieldFinal(Field f) {
        return Modifier.isFinal(f.getModifiers());
    }

    public static boolean isFieldStatic(Field f) {
        return Modifier.isStatic(f.getModifiers());
    }

    public static boolean isInstance(Object o, String className) throws ClassNotFoundException
    {
        return Class.forName(className).isInstance(o);
    }

    public static boolean isMethodFinal(Method m) {
        return Modifier.isFinal(m.getModifiers());
    }

    public static boolean isMethodStatic(Method m) {
        return Modifier.isStatic(m.getModifiers());
    }

    public static <T> void setField(Object o, String fieldName, T newValue)
    {
        ReflectionHacks.setPrivate(o, o.getClass(), fieldName, newValue);
    }
}
