package extendedui.utilities;

import extendedui.JavaUtils;
import eatyourbeets.interfaces.delegates.FuncT0;
import eatyourbeets.interfaces.delegates.FuncT1;
import eatyourbeets.interfaces.delegates.FuncT2;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class GenericCondition<T>
{
    protected Object state;
    protected FuncT0<Boolean> conditionT0;
    protected FuncT1<Boolean, T> conditionT1;
    protected FuncT2<Boolean, ?, T> conditionT2;

    public static <T> GenericCondition<T> FromT0(FuncT0<Boolean> condition)
    {
        return new GenericCondition<>(condition);
    }

    public static <T> GenericCondition<T> FromT1(FuncT1<Boolean, T> condition)
    {
        return new GenericCondition<>(condition);
    }

    public static <T, S> GenericCondition<T> FromT2(FuncT2<Boolean, S, T> condition, S state)
    {
        return new GenericCondition<>(condition, state);
    }

    private <S> GenericCondition(FuncT2<Boolean, S, T> condition, S state)
    {
        this.state = state;
        this.conditionT2 = condition;
    }

    private GenericCondition(FuncT1<Boolean, T> condition)
    {
        this.conditionT1 = condition;
    }

    private GenericCondition(FuncT0<Boolean> condition)
    {
        this.conditionT0 = condition;
    }

    public boolean Check(T result)
    {
        if (conditionT2 != null)
        {
            return conditionT2.CastAndInvoke(state, result);
        }
        if (conditionT1 != null)
        {
            return conditionT1.Invoke(result);
        }
        if (conditionT0 != null)
        {
            return conditionT0.Invoke();
        }

        JavaUtils.LogWarning(this, "No Condition found: " + getClass().getName());
        return true;
    }
}
