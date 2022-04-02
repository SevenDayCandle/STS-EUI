package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface ActionT3<T1, T2, T3>
{
    void Invoke(T1 arg1, T2 arg2, T3 arg3);

    default void CastAndInvoke(Object arg1, T2 arg2, T3 arg3)
    {
        Invoke((T1)arg1, arg2, arg3);
    }
}
