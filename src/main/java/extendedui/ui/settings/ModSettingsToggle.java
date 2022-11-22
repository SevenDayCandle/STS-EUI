package extendedui.ui.settings;

import extendedui.configuration.STSConfigItem;
import extendedui.interfaces.listeners.STSConfigListener;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.utilities.EUIFontHelper;

public class ModSettingsToggle extends EUIToggle implements STSConfigListener<Boolean>
{
    public final STSConfigItem<Boolean> Config;

    public ModSettingsToggle(AdvancedHitbox hb, STSConfigItem<Boolean> config, String title)
    {
        super(hb);
        Config = config;
        setText(title);
        setFont(EUIFontHelper.CardDescriptionFont_Normal, 1f);
        setOnToggle(val -> Config.set(val, true));
        this.Config.addListener(this);
    }

    @Override
    public void onChange(Boolean newValue)
    {
        setToggle(newValue);
    }
}
