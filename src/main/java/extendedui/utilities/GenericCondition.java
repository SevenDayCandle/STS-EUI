package extendedui.utilities;

import extendedui.interfaces.delegates.FuncT0;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.EUIUtils;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class GenericCondition<T>
{
    protected Object state;
    protected FuncT0<Boolean> conditionT0;
    protected FuncT1<Boolean, T> conditionT1;
    protected FuncT2<Boolean, ?, T> conditionT2;

    public static <T> GenericCondition<T> fromT0(FuncT0<Boolean> condition)
    {
        return new GenericCondition<>(condition);
    }

    public static <T> GenericCondition<T> fromT1(FuncT1<Boolean, T> condition)
    {
        return new GenericCondition<>(condition);
    }

    public static <T, S> GenericCondition<T> fromT2(FuncT2<Boolean, S, T> condition, S state)
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

    public boolean check(T result)
    {
        if (conditionT2 != null)
        {
            return conditionT2.castAndInvoke(state, result);
        }
        if (conditionT1 != null)
        {
            return conditionT1.invoke(result);
        }
        if (conditionT0 != null)
        {
            return conditionT0.invoke();
        }

        EUIUtils.logWarning(this, "No Condition found: " + getClass().getName());
        return true;
    }
}
