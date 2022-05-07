package extendedui.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface FuncT4<Result, T1, T2, T3, T4>
{
    Result Invoke(T1 param1, T2 param2, T3 param3, T4 param4);

    default Result CastAndInvoke(Object param1, T2 param2, T3 param3, T4 param4)
    {
        return Invoke((T1)param1, param2, param3, param4);
    }
}