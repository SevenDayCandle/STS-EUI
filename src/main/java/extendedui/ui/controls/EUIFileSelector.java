package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUIHeaderlessTooltip;
import extendedui.utilities.EUIFontHelper;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class EUIFileSelector extends EUIHoverable {
    protected File currentFile;
    protected FileNameExtensionFilter extensionFilter;
    protected ActionT1<File> onUpdate;
    protected float headerSpacing = 0.1f;
    public boolean showFullPath;
    public EUILabel header;
    public EUITextBox filePath;
    public EUIButton selectButton;
    public EUIButton clearButton;

    public EUIFileSelector(EUIHitbox hb) {
        this(hb, EUIRM.images.panel.texture());
    }

    public EUIFileSelector(EUIHitbox hb, Texture texture) {
        super(hb);
        this.header = new EUILabel(EUIFontHelper.cardTitleFontSmall, hb).setAlignment(0.5f, 0.0f, false);
        this.filePath = new EUITextBox(texture, new RelativeHitbox(hb, hb.width, hb.height, hb.width * (1.5f + headerSpacing), hb.height * 0.5f));
        this.filePath.setAlignment(0.5f, 0.1f);
        this.selectButton = new EUIButton(EUIRM.images.fileSelectButton.texture(), new RelativeHitbox(hb, hb.height, hb.height, hb.width * (2.1f + headerSpacing), hb.height * 0.5f)).setOnClick(this::chooseFile);
        this.clearButton = new EUIButton(EUIRM.images.xButton.texture(), new RelativeHitbox(selectButton.hb, hb.height, hb.height, selectButton.hb.width * 1.5f, hb.height * 0.5f)).setOnClick(() -> this.selectFile(null, true));
    }

    protected void chooseFile() {
        File file = EUIUtils.chooseFile(extensionFilter, currentFile);
        if (file != null) {
            selectFile(file, true);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        header.renderImpl(sb);
        filePath.renderImpl(sb);
        selectButton.renderImpl(sb);
        clearButton.renderImpl(sb);
    }

    protected void selectFile(File file, boolean shouldInvoke) {
        currentFile = file;
        if (currentFile != null) {
            if (showFullPath) {
                filePath.setLabel(currentFile.getAbsolutePath());
            }
            else {
                filePath.setLabel(currentFile.getName());
                filePath.tooltip = new EUIHeaderlessTooltip(currentFile.getAbsolutePath());
            }
        }
        else {
            filePath.setLabel("");
            filePath.tooltip = null;
        }

        if (shouldInvoke && onUpdate != null) {
            onUpdate.invoke(currentFile);
        }
    }

    public EUIFileSelector setFileFilters(String... filters) {
        this.extensionFilter = EUIUtils.getFileFilter(filters);
        return this;
    }

    public EUIFileSelector setHeader(BitmapFont font, float fontScale, Color textColor, String text) {
        return setHeader(font, fontScale, textColor, text, false);
    }

    public EUIFileSelector setHeader(BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.setFont(font, fontScale).setColor(textColor).setLabel(text).setSmartText(smartText).setActive(true);
        return this;
    }

    public EUIFileSelector setHeaderSpacing(float headerSpacing) {
        this.headerSpacing = headerSpacing;
        this.filePath.hb = new RelativeHitbox(hb, hb.width, hb.height, hb.width * (1 + headerSpacing), 0);
        return this;
    }

    public EUIFileSelector setHeaderText(String text) {
        this.header.setLabel(text);
        return this;
    }

    public EUIFileSelector setOnUpdate(ActionT1<File> onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    public EUIFileSelector setShowFullPath(boolean showFullPath) {
        this.showFullPath = showFullPath;
        return this;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        header.updateImpl();
        filePath.updateImpl();
        selectButton.updateImpl();
        clearButton.updateImpl();
    }
}
