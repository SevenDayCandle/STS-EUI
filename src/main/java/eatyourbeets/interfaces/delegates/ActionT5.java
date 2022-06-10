package eatyourbeets.interfaces.delegates;

public interface ActionT5<T1, T2, T3, T4, T5> {
    void Invoke(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5);

    default void CastAndInvoke(Object arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5) {
        this.Invoke((T1) arg1, arg2, arg3, arg4, arg5);
    }
}
