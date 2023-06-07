package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static extendedui.utilities.EUIClassUtils.IMPL_LOOKUP;

public interface ActionT3<T1, T2, T3> {
    void invoke(T1 arg1, T2 arg2, T3 arg3);

    public static <P, T1, T2> ActionT3<P, T1, T2> get(Class<P> invokeClass, String funcName, Class<T1> param1, Class<T2> param2) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(ActionT3.class),
                MethodType.methodType(void.class, Object.class, Object.class),
                lookup.findVirtual(invokeClass, funcName, MethodType.methodType(void.class, param1, param2)),
                MethodType.methodType(void.class, invokeClass, param1, param2)
        );
        return (ActionT3<P, T1, T2>) site.getTarget().invokeExact();
    }

    public static <T1, T2, T3> ActionT3<T1, T2, T3> get(Class<?> invokeClass, String funcName, Class<T1> param1, Class<T2> param2, Class<T3> param3) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        MethodType mType = MethodType.methodType(void.class, param1, param2, param3);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(ActionT3.class),
                MethodType.methodType(void.class, Object.class, Object.class),
                lookup.findStatic(invokeClass, funcName, mType),
                mType
        );
        return (ActionT3<T1, T2, T3>) site.getTarget().invokeExact();
    }
}
