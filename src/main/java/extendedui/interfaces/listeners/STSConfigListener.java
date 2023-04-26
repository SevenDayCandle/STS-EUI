package extendedui.interfaces.listeners;

public interface STSConfigListener<T> {
    default void onInitialize(T newValue) {
        onChange(newValue);
    }

    void onChange(T newValue);
}
