package extendedui.interfaces.listeners;

public interface STSConfigListener<T>
{
    public void onChange(T newValue);
    default public void onInitialize(T newValue)
    {
        onChange(newValue);
    }
}
