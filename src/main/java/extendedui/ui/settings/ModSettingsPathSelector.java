package extendedui.ui.settings;

import extendedui.configuration.STSConfigItem;
import extendedui.interfaces.listeners.STSConfigListener;
import extendedui.ui.controls.EUIFileSelector;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class ModSettingsPathSelector extends EUIFileSelector implements STSConfigListener<String> {
    public final STSConfigItem<String> config;

    public ModSettingsPathSelector(EUIHitbox hb, STSConfigItem<String> config, String title) {
        super(hb);
        this.config = config;
        this.header.setLabel(title).setFont(EUIFontHelper.cardDescriptionFontNormal, 1f);
        this.filePath.setFont(EUIFontHelper.cardDescriptionFontNormal, 1f);
        this.config.addListener(this);
        setOnUpdate(this::onUpdateFile);
    }

    private void onUpdateFile(File file) {
        config.set(file != null && file.exists() ? file.getAbsolutePath() : "", true);
    }

    public ModSettingsPathSelector makeCopy() {
        ModSettingsPathSelector other = new ModSettingsPathSelector(new EUIHitbox(hb), config, this.header.text);
        other.extensionFilter = new FileNameExtensionFilter(this.extensionFilter.getDescription(), this.extensionFilter.getExtensions());
        other.tooltip = this.tooltip;
        return other;
    }

    @Override
    public void onChange(String newValue) {
        selectFile(new File(newValue), false);
    }

    public ModSettingsPathSelector setFileFilters(String... filters) {
        super.setFileFilters(filters);
        return this;
    }
}
