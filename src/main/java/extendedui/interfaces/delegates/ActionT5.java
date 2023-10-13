package extendedui.interfaces.delegates;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public interface ActionT5<T1, T2, T3, T4, T5> extends IDelegate {
    static <P, T1, T2, T3, T4> ActionT5<P, T1, T2, T3, T4> get(Class<P> invokeClass, String funcName, Class<T1> param1, Class<T2> param2, Class<T3> param3, Class<T4> param4) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(ActionT4.class),
                MethodType.methodType(void.class, Object.class, Object.class),
                lookup.findVirtual(invokeClass, funcName, MethodType.methodType(void.class, param1, param2, param3, param4)),
                MethodType.methodType(void.class, invokeClass, param1, param2, param3, param4)
        );
        return (ActionT5<P, T1, T2, T3, T4>) site.getTarget().invokeExact();
    }

    static <T1, T2, T3, T4, T5> ActionT5<T1, T2, T3, T4, T5> get(Class<?> invokeClass, String funcName, Class<T1> param1, Class<T2> param2, Class<T3> param3, Class<T4> param4, Class<T5> param5) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        MethodType mType = MethodType.methodType(void.class, param1, param2, param3, param4, param5);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(ActionT4.class),
                MethodType.methodType(void.class, Object.class, Object.class),
                lookup.findStatic(invokeClass, funcName, mType),
                mType
        );
        return (ActionT5<T1, T2, T3, T4, T5>) site.getTarget().invokeExact();
    }

    void invoke(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5);
}
