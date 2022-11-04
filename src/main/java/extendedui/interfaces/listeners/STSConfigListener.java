package extendedui.interfaces.listeners;

public interface STSConfigListener<T>
{
    public void OnChange(T newValue);
    default public void OnInitialize(T newValue)
    {
        OnChange(newValue);
    }
}
