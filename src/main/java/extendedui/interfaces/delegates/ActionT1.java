package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static extendedui.utilities.EUIClassUtils.IMPL_LOOKUP;

public interface ActionT1<T1> {
    static <P> ActionT1<P> get(Class<P> invokeClass, String funcName) throws Throwable {
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

    static <T1> ActionT1<T1> get(Class<?> invokeClass, String funcName, Class<T1> param1) throws Throwable {
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

    void invoke(T1 arg1);
}
