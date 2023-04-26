package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

public class EUIFileSelector extends EUIHoverable {
    public boolean showFullPath;
    public EUILabel header;
    public EUITextBox filePath;
    public EUIButton selectButton;
    public EUIButton clearButton;
    protected File currentFile;
    protected FileNameExtensionFilter extensionFilter;
    protected ActionT1<File> onUpdate;
    protected float headerSpacing = 0.1f;

    public EUIFileSelector(EUIHitbox hb) {
        this(hb, EUIRM.images.panel.texture());
    }

    public EUIFileSelector(EUIHitbox hb, Texture texture) {
        super(hb);
        this.header = new EUILabel(EUIFontHelper.cardtitlefontSmall, hb).setAlignment(0.5f, 0.0f, false);
        this.filePath = new EUITextBox(texture, new RelativeHitbox(hb, hb.width, hb.height, hb.width * (1.5f + headerSpacing), hb.height * 0.5f));
        this.selectButton = new EUIButton(EUIRM.images.fileSelectButton.texture(), new RelativeHitbox(hb, hb.height, hb.height, hb.width * (2.1f + headerSpacing), hb.height * 0.5f)).setOnClick(this::chooseFile);
        this.clearButton = new EUIButton(EUIRM.images.x.texture(), new RelativeHitbox(selectButton.hb, hb.height, hb.height, selectButton.hb.width * 1.5f, hb.height * 0.5f)).setOnClick(() -> this.selectFile(null, true));
    }

    protected void chooseFile() {
        try {
            JFileChooser fc = new JFileChooser();
            if (extensionFilter != null) {
                fc.setFileFilter(extensionFilter);
            }
            fc.setDropTarget(new DropTarget() {
                public synchronized void drop(DropTargetDropEvent evt) {
                    try {
                        evt.acceptDrop(DnDConstants.ACTION_COPY);
                        Transferable t = evt.getTransferable();
                        if (t != null && t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                            for (File file : droppedFiles) {
                                fc.setSelectedFiles(droppedFiles.toArray(new File[]{}));
                            }
                            evt.dropComplete(true);
                        }
                        else {
                            evt.dropComplete(false);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        evt.dropComplete(false);
                    }
                }
            });

            if (currentFile != null && currentFile.exists()) {
                File cd = currentFile.getParentFile();
                if (cd != null && cd.isDirectory()) {
                    fc.setCurrentDirectory(cd);
                }
            }


            JFrame f = new JFrame();
            f.toFront();
            f.setAlwaysOnTop(true);
            f.setLocationRelativeTo(null);
            f.setPreferredSize(new Dimension(Settings.WIDTH / 2, Settings.HEIGHT / 2));
            fc.setPreferredSize(f.getPreferredSize());
            f.setVisible(true);

            int result = fc.showOpenDialog(f);
            f.setVisible(false);
            f.dispose();

            if (result == JFileChooser.APPROVE_OPTION) {
                selectFile(fc.getSelectedFile(), true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(this, "Failed to select file");
        }
    }

    protected void selectFile(File file, boolean shouldInvoke) {
        currentFile = file;
        if (currentFile != null) {
            if (showFullPath) {
                filePath.setLabel(currentFile.getAbsolutePath());
            }
            else {
                filePath.setLabel(currentFile.getName());
                filePath.tooltip = new EUITooltip("", currentFile.getAbsolutePath()).renderBackground(false);
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

    @Override
    public void renderImpl(SpriteBatch sb) {
        header.renderImpl(sb);
        filePath.renderImpl(sb);
        selectButton.renderImpl(sb);
        clearButton.renderImpl(sb);
    }

    public EUIFileSelector setFileFilters(String... filters) {
        this.extensionFilter = new FileNameExtensionFilter(EUIUtils.joinStrings(", ", EUIUtils.map(filters, f -> "*." + f)), filters);
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
