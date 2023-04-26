package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface ActionT2<T1, T2> {
    default void castAndInvoke(Object arg1, T2 arg2) {
        invoke((T1) arg1, arg2);
    }

    void invoke(T1 arg1, T2 arg2);
}
