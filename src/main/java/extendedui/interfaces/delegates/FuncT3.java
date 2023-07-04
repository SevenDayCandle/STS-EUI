package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static extendedui.utilities.EUIClassUtils.IMPL_LOOKUP;

public interface FuncT3<Result, T1, T2, T3> {
    static <Result, P, T1, T2> FuncT3<Result, P, T1, T2> get(Class<Result> retType, Class<P> invokeClass, String funcName, Class<T1> param1, Class<T2> param2) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(FuncT2.class),
                MethodType.methodType(Object.class, Object.class, Object.class),
                lookup.findVirtual(invokeClass, funcName, MethodType.methodType(retType, param1, param2)),
                MethodType.methodType(retType, invokeClass, param1, param2)
        );
        return (FuncT3<Result, P, T1, T2>) site.getTarget().invokeExact();
    }

    static <Result, T1, T2, T3> FuncT3<Result, T1, T2, T3> get(Class<Result> retType, Class<?> invokeClass, String funcName, Class<T1> param1, Class<T2> param2, Class<T3> param3) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        MethodType mType = MethodType.methodType(retType, param1, param2, param3);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(FuncT2.class),
                MethodType.methodType(Object.class, Object.class, Object.class),
                lookup.findStatic(invokeClass, funcName, mType),
                mType
        );
        return (FuncT3<Result, T1, T2, T3>) site.getTarget().invokeExact();
    }

    Result invoke(T1 param1, T2 param2, T3 param3);
}