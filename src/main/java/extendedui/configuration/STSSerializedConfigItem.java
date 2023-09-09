package extendedui.configuration;

import com.google.gson.reflect.TypeToken;
import extendedui.EUIUtils;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class STSSerializedConfigItem<T> extends STSConfigItem<T> {
    public STSSerializedConfigItem(String Key, T defaultValue) {
        super(Key, defaultValue);
    }

    protected T parseValue(String raw) {
        try {
            return (T) EUIUtils.deserialize(raw, defaultValue.getClass());
        }
        catch (Exception e) {
            EUIUtils.logError(this, "Failed to load preference for " + key + ", value was: " + raw);
            e.printStackTrace();
        }
        return defaultValue;
    }

    protected String serialize() {
        return EUIUtils.serialize(value);
    }
}
