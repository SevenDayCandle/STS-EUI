package extendedui.configuration;

import com.google.gson.reflect.TypeToken;
import extendedui.EUIUtils;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class STSSerializedConfigItem<T> extends STSConfigItem<T>
{
    protected final TypeToken<T> TOKEN = new TypeToken<T>() {};

    public STSSerializedConfigItem(String Key, T defaultValue) {
        super(Key, defaultValue);
    }

    protected T ParseValue(String raw) {
        try {
            return (T) EUIUtils.Deserialize(raw, DefaultValue.getClass());
        }
        catch (Exception e) {
            EUIUtils.LogError(this, "Failed to load preference for " + Key + ", value was: " + raw);
            e.printStackTrace();
        }
        return DefaultValue;
    }

    protected String Serialize() {
        return EUIUtils.Serialize(Value);
    }
}
