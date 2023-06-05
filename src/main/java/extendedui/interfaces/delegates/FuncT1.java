package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static extendedui.utilities.EUIClassUtils.IMPL_LOOKUP;

public interface FuncT1<Result, T1> {
    Result invoke(T1 param);

    public static <Result, P> Result doFor(Class<Result> retType, P invoker, String funcName) throws Throwable {
        FuncT1<Result, P> fun = get(retType, (Class<P>) invoker.getClass(), funcName);
        return fun.invoke(invoker);
    }

    public static <Result, T1> Result doStatic(Class<Result> retType, Class<?> invokeClass, String funcName, T1 param) throws Throwable {
        FuncT1<Result, T1> fun = get(retType, invokeClass, funcName, (Class<T1>) param.getClass());
        return fun.invoke(param);
    }

    public static <Result, P> FuncT1<Result, P> get(Class<Result> retType, Class<P> invokeClass, String funcName) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(FuncT1.class),
                MethodType.methodType(Object.class, Object.class),
                lookup.findVirtual(invokeClass, funcName, MethodType.methodType(retType)),
                MethodType.methodType(retType, invokeClass)
        );
        return (FuncT1<Result, P>) site.getTarget().invokeExact();
    }

    public static <Result, T1> FuncT1<Result, T1> get(Class<Result> retType, Class<?> invokeClass, String funcName, Class<T1> param1) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        MethodType mType = MethodType.methodType(retType, param1);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(FuncT1.class),
                MethodType.methodType(Object.class, Object.class),
                lookup.findStatic(invokeClass, funcName, mType),
                mType
        );
        return (FuncT1<Result, T1>) site.getTarget().invokeExact();
    }
}