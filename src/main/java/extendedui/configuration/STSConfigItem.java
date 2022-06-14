package extendedui.configuration;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import extendedui.JavaUtils;

import java.lang.reflect.Method;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class STSConfigItem<T>
{
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
        return Value;
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
            Class<?> valueClass = DefaultValue.getClass();
            Method method = valueClass.getMethod("valueOf", String.class);
            return (T) method.invoke(raw);
        } catch (Exception e) {
            JavaUtils.LogError(this, "Failed to load preference for " + Key + ", value was: " + raw);
            e.printStackTrace();
        }
        return DefaultValue;
    };

    protected String Serialize() {
        return Value.toString();
    };
}
