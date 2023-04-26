package extendedui.configuration;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class STSStringConfigItem extends STSConfigItem<String> {

    public STSStringConfigItem(String Key, String defaultValue) {
        super(Key, defaultValue);
    }

    protected String parseValue(String raw) {
        return raw != null ? raw : defaultValue;
    }

    protected String serialize() {
        return value;
    }
}
