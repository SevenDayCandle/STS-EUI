package stseffekseer.interfaces.delegates;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public interface FuncT1<Result, T1>
{
    Result Invoke(T1 param);

    default Result CastAndInvoke(Object param1)
    {
        return Invoke((T1)param1);
    }
}