package eatyourbeets.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface ActionT1<T1>
{
    void Invoke(T1 arg1);

    default void CastAndInvoke(Object arg1)
    {
        Invoke((T1)arg1);
    }
}
