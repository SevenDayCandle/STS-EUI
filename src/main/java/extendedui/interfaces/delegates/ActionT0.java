package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static extendedui.utilities.EUIClassUtils.IMPL_LOOKUP;

public interface ActionT0 {
    void invoke();

    public static void doStatic(Class<?> invokeClass, String funcName) throws Throwable {
        ActionT0 fun = get(invokeClass, funcName);
        fun.invoke();
    }

    public static ActionT0 get(Class<?> invokeClass, String funcName) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        MethodType mType = MethodType.methodType(void.class);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(ActionT0.class),
                mType,
                lookup.findStatic(invokeClass, funcName, mType),
                mType
        );
        return (ActionT0) site.getTarget().invokeExact();
    }
}
