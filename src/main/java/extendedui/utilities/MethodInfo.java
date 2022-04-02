package extendedui.utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class MethodInfo
{
    private final Method method;

    private Object InvokeInternal(Object instance, Object... args) throws RuntimeException
    {
        try
        {
            return method.invoke(instance, args);
        }
        catch (InvocationTargetException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    public MethodInfo(String methodName, Class<?> type, Class<?>... params) throws RuntimeException
    {
        try
        {
            method = type.getDeclaredMethod(methodName, params);
            method.setAccessible(true);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static class T0<Result> extends MethodInfo
    {
        public Result Invoke(Object instance)
        {
            return (Result) super.InvokeInternal(instance);
        }

        public T0(String methodName, Class<?> type)
        {
            super(methodName, type);
        }
    }

    public static class T1<Result, P1> extends MethodInfo
    {
        public Result Invoke(Object instance, P1 p1)
        {
            return (Result) super.InvokeInternal(instance, p1);
        }

        public T1(String methodName, Class<?> type, Class<P1> p1)
        {
            super(methodName, type, p1);
        }
    }

    public static class T2<Result, P1, P2> extends MethodInfo
    {
        public Result Invoke(Object instance, P1 p1, P2 p2)
        {
            return (Result) super.InvokeInternal(instance, p1, p2);
        }

        public T2(String methodName, Class<?> type, Class<P1> p1, Class<P2> p2)
        {
            super(methodName, type, p1, p2);
        }
    }

    public static class T3<Result, P1, P2, P3> extends MethodInfo
    {
        public Result Invoke(Object instance, P1 p1, P2 p2, P3 p3)
        {
            return (Result) super.InvokeInternal(instance, p1, p2, p3);
        }

        public T3(String methodName, Class<?> type, Class<P1> p1, Class<P2> p2, Class<P3> p3)
        {
            super(methodName, type, p1, p2, p3);
        }
    }

    public boolean IsStatic() {
        return Modifier.isStatic(this.method.getModifiers());
    }
}