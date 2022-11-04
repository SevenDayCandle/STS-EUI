package extendedui.configuration;

import extendedui.EUIUtils;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class STSStringConfigItem extends STSConfigItem<String>
{

    public STSStringConfigItem(String Key, String defaultValue) {
        super(Key, defaultValue);
    }

    protected String ParseValue(String raw) {
        return raw != null ? raw : DefaultValue;
    }

    protected String Serialize() {
        return Value;
    }
}
