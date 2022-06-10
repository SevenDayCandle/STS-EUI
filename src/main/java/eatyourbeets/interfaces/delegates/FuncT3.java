package eatyourbeets.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface FuncT3<Result, T1, T2, T3>
{
    Result Invoke(T1 param1, T2 param2, T3 param3);

    default Result CastAndInvoke(Object param1, T2 param2, T3 param3)
    {
        return Invoke((T1)param1, param2, param3);
    }
}