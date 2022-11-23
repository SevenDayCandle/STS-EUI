package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface ActionT1<T1>
{
    void invoke(T1 arg1);

    default void castAndInvoke(Object arg1)
    {
        invoke((T1)arg1);
    }
}
