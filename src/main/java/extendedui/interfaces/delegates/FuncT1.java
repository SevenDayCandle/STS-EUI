package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface FuncT1<Result, T1>
{
    Result invoke(T1 param);

    default Result castAndInvoke(Object param1)
    {
        return invoke((T1)param1);
    }
}