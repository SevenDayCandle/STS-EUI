package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface FuncT2<Result, T1, T2>
{
    Result invoke(T1 param1, T2 param2);

    default Result castAndInvoke(Object param1, T2 param2)
    {
        return invoke((T1)param1, param2);
    }
}