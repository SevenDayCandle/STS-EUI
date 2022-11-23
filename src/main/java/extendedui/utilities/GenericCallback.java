package extendedui.utilities;

import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class GenericCallback<T>
{
    protected Object state;
    protected ActionT0 actionT0;
    protected ActionT1<T> actionT1;
    protected ActionT2<?, T> actionT2;

    public static <T> GenericCallback<T> fromT0(ActionT0 onCompletion)
    {
        return new GenericCallback<>(onCompletion);
    }

    public static <T> GenericCallback<T> fromT1(ActionT1<T> onCompletion)
    {
        return new GenericCallback<>(onCompletion);
    }

    public static <S, T> GenericCallback<T> fromT2(ActionT2<S, T> onCompletion, S state)
    {
        return new GenericCallback<>(onCompletion, state);
    }

    private <S> GenericCallback(ActionT2<S, T> onCompletion, S state)
    {
        this.state = state;
        this.actionT2 = onCompletion;
    }

    private GenericCallback(ActionT1<T> onCompletion)
    {
        this.actionT1 = onCompletion;
    }

    private GenericCallback(ActionT0 onCompletion)
    {
        this.actionT0 = onCompletion;
    }

    public void complete(T result)
    {
        if (actionT2 != null)
        {
            actionT2.castAndInvoke(state, result);
        }

        if (actionT1 != null)
        {
            actionT1.invoke(result);
        }

        if (actionT0 != null)
        {
            actionT0.invoke();
        }
    }
}
