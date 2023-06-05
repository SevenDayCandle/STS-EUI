package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static extendedui.utilities.EUIClassUtils.IMPL_LOOKUP;

public interface ActionT1<T1> {
    void invoke(T1 arg1);

    public static <P> void doFor(P invoker, String funcName) throws Throwable {
        ActionT1<P> fun = get((Class<P>) invoker.getClass(), funcName);
        fun.invoke(invoker);
    }

    public static <T1> void doStatic(Class<?> invokeClass, String funcName, T1 param) throws Throwable {
        ActionT1<T1> fun = get(invokeClass, funcName, (Class<T1>) param.getClass());
        fun.invoke(param);
    }

    public static <P> ActionT1<P> get(Class<P> invokeClass, String funcName) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(ActionT1.class),
                MethodType.methodType(void.class, Object.class),
                lookup.findVirtual(invokeClass, funcName, MethodType.methodType(void.class)),
                MethodType.methodType(void.class, invokeClass)
        );
        return (ActionT1<P>) site.getTarget().invokeExact();
    }

    public static <T1> ActionT1<T1> get(Class<?> invokeClass, String funcName, Class<T1> param1) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        MethodType mType = MethodType.methodType(void.class, param1);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(ActionT1.class),
                MethodType.methodType(void.class, Object.class),
                lookup.findStatic(invokeClass, funcName, mType),
                mType
        );
        return (ActionT1<T1>) site.getTarget().invokeExact();
    }
}
