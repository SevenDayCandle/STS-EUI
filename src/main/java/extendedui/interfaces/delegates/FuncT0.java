package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Function;

import static extendedui.utilities.EUIClassUtils.IMPL_LOOKUP;

public interface FuncT0<Result> {
    Result invoke();

    public static <Result> Result doStatic(Class<Result> retType, Class<?> invokeClass, String funcName) throws Throwable {
        FuncT0<Result> fun = get(retType, invokeClass, funcName);
        return fun.invoke();
    }

    public static <Result> FuncT0<Result> get(Class<Result> retType, Class<?> invokeClass, String funcName) throws Throwable {
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
}
