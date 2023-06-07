package extendedui.interfaces.delegates;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static extendedui.utilities.EUIClassUtils.IMPL_LOOKUP;

public interface ActionT4<T1, T2, T3, T4> {
    void invoke(T1 var1, T2 var2, T3 var3, T4 var4);

    public static <P, T1, T2, T3> ActionT4<P, T1, T2, T3> get(Class<P> invokeClass, String funcName, Class<T1> param1, Class<T2> param2, Class<T3> param3) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(ActionT4.class),
                MethodType.methodType(void.class, Object.class, Object.class),
                lookup.findVirtual(invokeClass, funcName, MethodType.methodType(void.class, param1, param2, param3)),
                MethodType.methodType(void.class, invokeClass, param1, param2, param3)
        );
        return (ActionT4<P, T1, T2, T3>) site.getTarget().invokeExact();
    }

    public static <T1, T2, T3, T4> ActionT4<T1, T2, T3, T4> get(Class<?> invokeClass, String funcName, Class<T1> param1, Class<T2> param2, Class<T3> param3, Class<T4> param4) throws Throwable {
        MethodHandles.Lookup lookup = IMPL_LOOKUP.in(invokeClass);
        MethodType mType = MethodType.methodType(void.class, param1, param2, param3, param4);
        CallSite site = LambdaMetafactory.metafactory(lookup,
                "invoke",
                MethodType.methodType(ActionT4.class),
                MethodType.methodType(void.class, Object.class, Object.class),
                lookup.findStatic(invokeClass, funcName, mType),
                mType
        );
        return (ActionT4<T1, T2, T3, T4>) site.getTarget().invokeExact();
    }
}
