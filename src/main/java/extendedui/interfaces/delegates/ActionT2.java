package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public interface ActionT2<T1, T2> extends IDelegate {
    static <P, T1> ActionT2<P, T1> get(Class<P> invokeClass, String funcName, Class<T1> param1) throws Throwable {
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

    static <T1, T2> ActionT2<T1, T2> get(Class<?> invokeClass, String funcName, Class<T1> param1, Class<T2> param2) throws Throwable {
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

    void invoke(T1 arg1, T2 arg2);
}
