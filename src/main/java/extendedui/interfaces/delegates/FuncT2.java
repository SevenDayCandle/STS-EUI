package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static extendedui.utilities.EUIClassUtils.IMPL_LOOKUP;

public interface FuncT2<Result, T1, T2> {
    Result invoke(T1 param1, T2 param2);

    public static <Result, P, T1> Result doFor(Class<Result> retType, P invoker, String funcName, T1 param) throws Throwable {
        FuncT2<Result, P, T1> fun = get(retType, (Class<P>) invoker.getClass(), funcName, (Class<T1>) param.getClass());
        return fun.invoke(invoker, param);
    }

    public static <Result, T1, T2> Result doStatic(Class<Result> retType, Class<?> invokeClass, String funcName, T1 param, T2 param2) throws Throwable {
        FuncT2<Result, T1, T2> fun = get(retType, invokeClass, funcName, (Class<T1>) param.getClass(), (Class<T2>) param2.getClass());
        return fun.invoke(param, param2);
    }

    public static <Result, P, T1> FuncT2<Result, P, T1> get(Class<Result> retType, Class<P> invokeClass, String funcName, Class<T1> param1) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(FuncT2.class),
                MethodType.methodType(Object.class, Object.class, Object.class),
                lookup.findVirtual(invokeClass, funcName, MethodType.methodType(retType, param1)),
                MethodType.methodType(retType, invokeClass, param1)
        );
        return (FuncT2<Result, P, T1>) site.getTarget().invokeExact();
    }

    public static <Result, T1, T2> FuncT2<Result, T1, T2> get(Class<Result> retType, Class<?> invokeClass, String funcName, Class<T1> param1, Class<T2> param2) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        MethodType mType = MethodType.methodType(retType, param1, param2);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(FuncT2.class),
                MethodType.methodType(Object.class, Object.class, Object.class),
                lookup.findStatic(invokeClass, funcName, mType),
                mType
        );
        return (FuncT2<Result, T1, T2>) site.getTarget().invokeExact();
    }
}