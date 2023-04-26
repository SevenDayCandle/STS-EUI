package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface FuncT3<Result, T1, T2, T3> {
    default Result castAndInvoke(Object param1, T2 param2, T3 param3) {
        return invoke((T1) param1, param2, param3);
    }

    Result invoke(T1 param1, T2 param2, T3 param3);
}