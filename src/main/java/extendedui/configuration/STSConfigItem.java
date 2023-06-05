package extendedui.configuration;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import extendedui.EUIUtils;
import extendedui.interfaces.listeners.STSConfigListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class STSConfigItem<T> {
    protected static final HashMap<Class<?>, Method> METHOD_HASH_MAP = new HashMap<>();
    public final String key;
    protected final ArrayList<STSConfigListener<T>> listeners = new ArrayList<>();
    protected final T defaultValue;
    protected SpireConfig config;
    protected T value;

    public STSConfigItem(String Key, T defaultValue) {
        this.key = Key;
        this.defaultValue = value = defaultValue;
    }

    public final STSConfigItem<T> addConfig(SpireConfig Config) {
        this.config = Config;
        if (this.config.has(this.key)) {
            this.value = parseValue(this.config.getString(this.key));
        }
        return this;
    }

    protected T parseValue(String raw) {
        try {
            return (T) getMethod().invoke(null, raw);
        }
        catch (Exception e) {
            EUIUtils.logError(this, "Failed to load preference for " + key + ", value was: " + raw);
            e.printStackTrace();
        }
        return defaultValue;
    }

    protected Method getMethod() throws Exception {
        Class<?> valueClass = getConfigClass();
        if (METHOD_HASH_MAP.containsKey(valueClass)) {
            return METHOD_HASH_MAP.get(valueClass);
        }
        Method method = valueClass.getMethod("valueOf", String.class);
        METHOD_HASH_MAP.put(valueClass, method);
        return method;
    }

    public final Class<?> getConfigClass() {
        return defaultValue.getClass();
    }

    public final void addListener(STSConfigListener<T> listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
            listener.onInitialize(value);
        }
    }

    public final T get() {
        return value;
    }

    public final void removeListener(STSConfigListener<T> listener) {
        this.listeners.remove(listener);
    }

    public final T set(T Value) {
        this.value = Value;
        this.config.setString(this.key, serialize());
        save();
        for (STSConfigListener<T> listener : listeners) {
            listener.onChange(Value);
        }
        return Value;
    }

    protected String serialize() {
        return String.valueOf(value);
    }

    protected final void save() {
        try {
            this.config.save();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
