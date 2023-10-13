package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public interface FuncT0<Result> extends IDelegate {
    static <Result> FuncT0<Result> get(Class<Result> retType, Class<?> invokeClass, String funcName) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        MethodType mType = MethodType.methodType(retType);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(FuncT0.class),
                MethodType.methodType(Object.class),
                lookup.findStatic(invokeClass, funcName, mType),
                mType
        );
        return (FuncT0<Result>) site.getTarget().invokeExact();
    }

    Result invoke();
}
