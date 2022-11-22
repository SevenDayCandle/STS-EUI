package extendedui.configuration;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import extendedui.EUIUtils;
import extendedui.interfaces.listeners.STSConfigListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class STSConfigItem<T>
{
    protected static final HashMap<Class<?>,Method> METHOD_HASH_MAP = new HashMap<>();
    protected final ArrayList<STSConfigListener<T>> Listeners = new ArrayList<>();

    public final String Key;
    protected SpireConfig Config;
    protected T Value;
    protected final T DefaultValue;

    public STSConfigItem(String Key, T defaultValue) {
        this.Key = Key;
        DefaultValue = Value = defaultValue;
    }

    public final STSConfigItem<T> addConfig(SpireConfig Config) {
        this.Config = Config;
        if (this.Config.has(this.Key)) {
            this.Value = parseValue(this.Config.getString(this.Key));
        }
        return this;
    }

    public final T get() {
        return Value;
    }

    public final T set(T Value) {
        return set(Value, true);
    }

    public final T set(T Value, boolean save) {
        this.Value = Value;
        this.Config.setString(this.Key, serialize());
        save();
        for (STSConfigListener<T> listener : Listeners)
        {
            listener.onChange(Value);
        }
        return Value;
    }

    public final void addListener(STSConfigListener<T> listener)
    {
        if (!this.Listeners.contains(listener))
        {
            this.Listeners.add(listener);
            listener.onInitialize(Value);
        }
    }

    public final void removeListener(STSConfigListener<T> listener)
    {
        this.Listeners.remove(listener);
    }

    public final Class<?> getConfigClass()
    {
        return DefaultValue.getClass();
    }

    protected final void save() {
        try {
            this.Config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected T parseValue(String raw) {
        try {
            return (T) getMethod().invoke(null, raw);
        } catch (Exception e) {
            EUIUtils.logError(this, "Failed to load preference for " + Key + ", value was: " + raw);
            e.printStackTrace();
        }
        return DefaultValue;
    }

    protected String serialize() {
        return String.valueOf(Value);
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
}
