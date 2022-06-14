package extendedui.configuration;

import com.badlogic.gdx.utils.Base64Coder;
import com.google.gson.reflect.TypeToken;
import extendedui.JavaUtils;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class STSSerializedConfigItem<T> extends STSConfigItem<T>
{
    protected final TypeToken<T> TOKEN = new TypeToken<T>() {};

    public STSSerializedConfigItem(String Key, T defaultValue) {
        super(Key, defaultValue);
    }

    protected T ParseValue(String raw) {
        try {
            return (T) JavaUtils.Deserialize(raw, DefaultValue.getClass());
        }
        catch (Exception e) {
            JavaUtils.LogError(this, "Failed to load preference for " + Key + ", value was: " + raw);
            e.printStackTrace();
        }
        return DefaultValue;
    };

    protected String Serialize() {
        return JavaUtils.Serialize(Value);
    };
}
