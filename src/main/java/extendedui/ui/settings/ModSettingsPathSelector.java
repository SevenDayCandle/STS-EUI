package extendedui.ui.settings;

import extendedui.configuration.STSConfigItem;
import extendedui.interfaces.listeners.STSConfigListener;
import extendedui.ui.controls.EUIFileSelector;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.utilities.EUIFontHelper;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class ModSettingsPathSelector extends EUIFileSelector implements STSConfigListener<String>
{
    public final STSConfigItem<String> Config;

    public ModSettingsPathSelector(AdvancedHitbox hb, STSConfigItem<String> config, String title)
    {
        super(hb);
        Config = config;
        this.header.SetText(title).SetFont(EUIFontHelper.CardDescriptionFont_Normal, 1f);
        this.filePath.SetFont(EUIFontHelper.CardDescriptionFont_Normal, 1f);
        this.Config.AddListener(this);
        SetOnUpdate(this::OnUpdateFile);
    }

    public ModSettingsPathSelector SetFileFilters(String... filters) {
        super.SetFileFilters(filters);
        return this;
    }

    public ModSettingsPathSelector MakeCopy()
    {
        ModSettingsPathSelector other = new ModSettingsPathSelector(new AdvancedHitbox(hb), Config, this.header.text);
        other.extensionFilter = new FileNameExtensionFilter(this.extensionFilter.getDescription(), this.extensionFilter.getExtensions());
        other.tooltip = this.tooltip;
        return other;
    }

    @Override
    public void OnChange(String newValue)
    {
        SelectFile(new File(newValue), false);
    }

    private void OnUpdateFile(File file)
    {
        Config.Set(file != null && file.exists() ? file.getAbsolutePath() : "", true);
    }
}
