package extendedui.ui.settings;

import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.configuration.STSConfigItem;
import extendedui.interfaces.listeners.STSConfigListener;
import extendedui.ui.controls.EUIFileSelector;
import extendedui.ui.hitboxes.EUIHitbox;

import java.io.File;

public class ModSettingsPathSelector extends EUIFileSelector implements STSConfigListener<String> {
    public final STSConfigItem<String> config;

    public ModSettingsPathSelector(EUIHitbox hb, STSConfigItem<String> config, String title) {
        super(hb);
        this.config = config;
        this.header.setLabel(title).setFont(FontHelper.cardDescFont_N, 1f);
        this.filePath.setFont(FontHelper.cardDescFont_N, 1f);
        this.config.addListener(this);
        setOnUpdate(this::onUpdateFile);
    }

    public ModSettingsPathSelector(ModSettingsPathSelector other) {
        super(other);
        this.config = other.config;
        this.header.setLabel(other.header.text).setFont(FontHelper.cardDescFont_N, 1f);
        this.filePath.setFont(FontHelper.cardDescFont_N, 1f);
        this.config.addListener(this);
        setOnUpdate(this::onUpdateFile);
    }

    @Override
    public void onChange(String newValue) {
        selectFile(new File(newValue), false);
    }

    private void onUpdateFile(File file) {
        config.set(file != null && file.exists() ? file.getAbsolutePath() : "");
    }

    public ModSettingsPathSelector setFileFilters(String... filters) {
        super.setFileFilters(filters);
        return this;
    }

    public ModSettingsPathSelector translate(float x, float y) {
        super.translate(x, y);

        return this;
    }
}
