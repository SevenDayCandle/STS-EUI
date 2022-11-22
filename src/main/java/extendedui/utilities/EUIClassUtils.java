package extendedui.utilities;

import basemod.ReflectionHacks;
import eatyourbeets.interfaces.delegates.FuncT1;
import extendedui.EUIUtils;

import java.lang.reflect.Field;
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

    public static <T> T invoke(Class<?> className, String methodName, Object... parameters)
    {
        return ReflectionHacks.privateStaticMethod(className, methodName, EUIUtils.map(parameters, Object::getClass).toArray(new Class<?>[]{})).invoke(parameters);
    }

    public static <T> T invoke(Object o, String methodName, Object... parameters)
    {
        return getMethod(o, methodName, EUIUtils.map(parameters, Object::getClass).toArray(new Class<?>[]{})).invoke(o, parameters);
    }

    public static boolean isFieldFinal(Field f) {
        return Modifier.isFinal(f.getModifiers());
    }

    public static boolean isFieldStatic(Field f) {
        return Modifier.isStatic(f.getModifiers());
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
