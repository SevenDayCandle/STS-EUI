package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static extendedui.utilities.EUIClassUtils.IMPL_LOOKUP;

public interface ActionT2<T1, T2> {
    void invoke(T1 arg1, T2 arg2);

    public static <P, T1> void doFor(P invoker, String funcName, T1 param) throws Throwable {
        ActionT2<P, T1> fun = get((Class<P>) invoker.getClass(), funcName, (Class<T1>) param.getClass());
        fun.invoke(invoker, param);
    }

    public static <T1, T2> void doStatic(Class<?> invokeClass, String funcName, T1 param, T2 param2) throws Throwable {
        ActionT2<T1, T2> fun = get(invokeClass, funcName, (Class<T1>) param.getClass(), (Class<T2>) param2.getClass());
        fun.invoke(param, param2);
    }

    public static <P, T1> ActionT2<P, T1> get(Class<P> invokeClass, String funcName, Class<T1> param1) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(ActionT2.class),
                MethodType.methodType(void.class, Object.class, Object.class),
                lookup.findVirtual(invokeClass, funcName, MethodType.methodType(void.class, param1)),
                MethodType.methodType(void.class, invokeClass, param1)
        );
        return (ActionT2<P, T1>) site.getTarget().invokeExact();
    }

    public static <T1, T2> ActionT2<T1, T2> get(Class<?> invokeClass, String funcName, Class<T1> param1, Class<T2> param2) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        MethodType mType = MethodType.methodType(void.class, param1, param2);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(ActionT2.class),
                MethodType.methodType(void.class, Object.class, Object.class),
                lookup.findStatic(invokeClass, funcName, mType),
                mType
        );
        return (ActionT2<T1, T2>) site.getTarget().invokeExact();
    }
}
