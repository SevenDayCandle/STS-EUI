package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface FuncT2<Result, T1, T2> {
    Result invoke(T1 param1, T2 param2);
}