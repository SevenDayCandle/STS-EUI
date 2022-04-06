package extendedui.configuration;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class STSConfigurationOption<T>
{
    public final String Key;
    protected SpireConfig Config;
    protected T Value;
    protected final T DefaultValue;

    public STSConfigurationOption(String Key, T defaultValue) {
        this.Key = Key;
        DefaultValue = Value = defaultValue;
    }

    public STSConfigurationOption<T> AddConfig(SpireConfig Config) {
        this.Config = Config;
        if (this.Config.has(this.Key)) {
            this.Value = ParseValue(this.Config.getString(this.Key));
        }
        return this;
    }

    public T Get() {
        return Value;
    }

    public T Set(T Value) {
        return Set(Value, true);
    }

    public T Set(T Value, boolean save) {
        this.Value = Value;
        this.Config.setString(this.Key, Value.toString());
        Save();
        return Value;
    }

    protected void Save() {
        try {
            this.Config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected T ParseValue(String raw) {
        Class<?> valueClass = Value.getClass();
        Method method = null;
        try {
            method = valueClass.getMethod("valueOf", String.class);
        } catch (NoSuchMethodException | SecurityException ignored) {
        }
        if (method != null) {
            try
            {
                return (T) method.invoke(raw);
            }
            catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e)
            {
                e.printStackTrace();
            }
        }
        return DefaultValue;
    };

    protected String Serialize() {
        return Value.toString();
    };
}
