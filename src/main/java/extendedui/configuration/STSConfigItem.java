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

    public final STSConfigItem<T> AddConfig(SpireConfig Config) {
        this.Config = Config;
        if (this.Config.has(this.Key)) {
            this.Value = ParseValue(this.Config.getString(this.Key));
        }
        return this;
    }

    public final T Get() {
        return Value;
    }

    public final T Set(T Value) {
        return Set(Value, true);
    }

    public final T Set(T Value, boolean save) {
        this.Value = Value;
        this.Config.setString(this.Key, Serialize());
        Save();
        for (STSConfigListener<T> listener : Listeners)
        {
            listener.OnChange(Value);
        }
        return Value;
    }

    public final void AddListener(STSConfigListener<T> listener)
    {
        if (!this.Listeners.contains(listener))
        {
            this.Listeners.add(listener);
            listener.OnInitialize(Value);
        }
    }

    public final void RemoveListener(STSConfigListener<T> listener)
    {
        this.Listeners.remove(listener);
    }

    public final Class<?> GetConfigClass()
    {
        return DefaultValue.getClass();
    }

    protected final void Save() {
        try {
            this.Config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected T ParseValue(String raw) {
        try {
            return (T) GetMethod().invoke(null, raw);
        } catch (Exception e) {
            EUIUtils.LogError(this, "Failed to load preference for " + Key + ", value was: " + raw);
            e.printStackTrace();
        }
        return DefaultValue;
    }

    protected String Serialize() {
        return String.valueOf(Value);
    }

    protected Method GetMethod() throws Exception {
        Class<?> valueClass = GetConfigClass();
        if (METHOD_HASH_MAP.containsKey(valueClass)) {
            return METHOD_HASH_MAP.get(valueClass);
        }
        Method method = valueClass.getMethod("valueOf", String.class);
        METHOD_HASH_MAP.put(valueClass, method);
        return method;
    }
}
