package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface ActionT2<T1, T2>
{
    void Invoke(T1 arg1, T2 arg2);

    default void CastAndInvoke(Object arg1, T2 arg2)
    {
        Invoke((T1)arg1, arg2);
    }
}
