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
        SetText(title);
        SetFont(EUIFontHelper.CardDescriptionFont_Normal, 1f);
        SetOnToggle(val -> Config.Set(val, true));
        this.Config.AddListener(this);
    }

    @Override
    public void OnChange(Boolean newValue)
    {
        SetToggle(newValue);
    }
}
