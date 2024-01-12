package extendedui.ui.settings;

import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.configuration.STSConfigItem;
import extendedui.interfaces.listeners.STSConfigListener;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;

public class ModSettingsToggle extends EUIToggle implements STSConfigListener<Boolean> {
    public final STSConfigItem<Boolean> config;

    public ModSettingsToggle(EUIHitbox hb, STSConfigItem<Boolean> config, String title) {
        super(hb);
        this.config = config;
        setText(title);
        setFont(FontHelper.cardDescFont_N, 1f);
        setOnToggle(this.config::set);
        this.config.addListener(this);
    }

    @Override
    public void onChange(Boolean newValue) {
        setToggle(newValue);
    }
}
