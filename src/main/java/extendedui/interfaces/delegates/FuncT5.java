package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface FuncT5<Result, T1, T2, T3, T4, T5> {
    default Result castAndInvoke(Object param1, T2 param2, T3 param3, T4 param4, T5 param5) {
        return invoke((T1) param1, param2, param3, param4, param5);
    }

    Result invoke(T1 param1, T2 param2, T3 param3, T4 param4, T5 param5);
}