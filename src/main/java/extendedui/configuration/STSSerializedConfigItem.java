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
            JavaUtils.Deserialize(raw, TOKEN.getType());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return DefaultValue;
    };

    protected String Serialize() {
        return JavaUtils.Serialize(Value);
    };
}
