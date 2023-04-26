package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface ActionT3<T1, T2, T3> {
    default void castAndInvoke(Object arg1, T2 arg2, T3 arg3) {
        invoke((T1) arg1, arg2, arg3);
    }

    void invoke(T1 arg1, T2 arg2, T3 arg3);
}
