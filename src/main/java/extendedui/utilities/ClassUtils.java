package extendedui.utilities;

import basemod.ReflectionHacks;
import eatyourbeets.interfaces.delegates.FuncT1;
import extendedui.JavaUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClassUtils
{
    public static <T> void GetAndSetField(Object o, String fieldName, FuncT1<T, T> valueFunc)
    {
        Class<?> c = o.getClass();
        ReflectionHacks.setPrivate(o, c, fieldName, valueFunc.Invoke(ReflectionHacks.getPrivate(o, c, fieldName)));
    }

    // Enforces type of the field without using a variable declaration
    public static <T> T GetFieldAsType(Object o, String fieldName, Class<T> type)
    {
        return GetField(o, fieldName);
    }

    public static <T> T GetField(Object o, String fieldName)
    {
        return ReflectionHacks.getPrivate(o, o.getClass(), fieldName);
    }

    public static <T> T GetFieldInheritedAsType(Object o, String fieldName, Class<T> type)
    {
        return ReflectionHacks.getPrivateInherited(o, o.getClass(), fieldName);
    }

    public static <T> T GetFieldInherited(Object o, String fieldName)
    {
        return ReflectionHacks.getPrivateInherited(o, o.getClass(), fieldName);
    }

    // Enforces type of the field without using a variable declaration
    public static <T> T GetFieldStatic(Class<?> c, String fieldName, Class<T> type)
    {
        return ReflectionHacks.getPrivateStatic(c, fieldName);
    }

    public static <T> ReflectionHacks.RMethod GetMethod(Object o, String methodName, Class<?>... parameterTypes)
    {
        return ReflectionHacks.privateMethod(o.getClass(), methodName, parameterTypes);
    }

    public static <T> T Invoke(Class<?> className, String methodName, Object... parameters)
    {
        return ReflectionHacks.privateStaticMethod(className, methodName, JavaUtils.Map(parameters, Object::getClass).toArray(new Class<?>[]{})).invoke(parameters);
    }

    public static <T> T Invoke(Object o, String methodName, Object... parameters)
    {
        return GetMethod(o, methodName, JavaUtils.Map(parameters, Object::getClass).toArray(new Class<?>[]{})).invoke(o, parameters);
    }

    public static boolean IsFieldFinal(Field f) {
        return Modifier.isFinal(f.getModifiers());
    }

    public static boolean IsFieldStatic(Field f) {
        return Modifier.isStatic(f.getModifiers());
    }

    public static boolean IsMethodFinal(Method m) {
        return Modifier.isFinal(m.getModifiers());
    }

    public static boolean IsMethodStatic(Method m) {
        return Modifier.isStatic(m.getModifiers());
    }

    public static <T> void SetField(Object o, String fieldName, T newValue)
    {
        ReflectionHacks.setPrivate(o, o.getClass(), fieldName, newValue);
    }
}
