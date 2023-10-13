package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public interface FuncT2<Result, T1, T2> extends IDelegate {
    static <Result, P, T1> FuncT2<Result, P, T1> get(Class<Result> retType, Class<P> invokeClass, String funcName, Class<T1> param1) throws Throwable {
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

    static <Result, T1, T2> FuncT2<Result, T1, T2> get(Class<Result> retType, Class<?> invokeClass, String funcName, Class<T1> param1, Class<T2> param2) throws Throwable {
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

    Result invoke(T1 param1, T2 param2);
}