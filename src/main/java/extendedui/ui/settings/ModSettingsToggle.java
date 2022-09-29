package extendedui.ui.settings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.configuration.STSConfigItem;
import extendedui.interfaces.markers.ModSettingsProvider;
import extendedui.ui.controls.GUI_Toggle;
import extendedui.ui.hitboxes.AdvancedHitbox;

public class ModSettingsToggle extends GUI_Toggle implements ModSettingsProvider<Boolean>
{
    public final STSConfigItem<Boolean> Config;

    public ModSettingsToggle(AdvancedHitbox hb, STSConfigItem<Boolean> config, String title)
    {
        super(hb);
        Config = config;
        SetText(title);
        SetOnToggle(val -> Config.Set(val, true));
    }

    @Override
    public STSConfigItem<Boolean> Config()
    {
        return Config;
    }

    @Override
    public void Set(Boolean value)
    {
        SetToggle(value);
    }

    @Override
    public void UpdateProvider()
    {
        super.Update();
    }

    @Override
    public void RenderProvider(SpriteBatch sb)
    {
        super.Render(sb);
    }
}
