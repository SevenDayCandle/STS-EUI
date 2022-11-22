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
        this.header.setLabel(title).setFont(EUIFontHelper.CardDescriptionFont_Normal, 1f);
        this.filePath.setFont(EUIFontHelper.CardDescriptionFont_Normal, 1f);
        this.Config.addListener(this);
        setOnUpdate(this::onUpdateFile);
    }

    public ModSettingsPathSelector setFileFilters(String... filters) {
        super.setFileFilters(filters);
        return this;
    }

    public ModSettingsPathSelector makeCopy()
    {
        ModSettingsPathSelector other = new ModSettingsPathSelector(new AdvancedHitbox(hb), Config, this.header.text);
        other.extensionFilter = new FileNameExtensionFilter(this.extensionFilter.getDescription(), this.extensionFilter.getExtensions());
        other.tooltip = this.tooltip;
        return other;
    }

    @Override
    public void onChange(String newValue)
    {
        selectFile(new File(newValue), false);
    }

    private void onUpdateFile(File file)
    {
        Config.set(file != null && file.exists() ? file.getAbsolutePath() : "", true);
    }
}
