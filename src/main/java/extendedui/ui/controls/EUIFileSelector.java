package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUIUtils;
import extendedui.EUIRM;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.AdvancedHitbox;
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

public class EUIFileSelector extends EUIHoverable
{
    protected File currentFile;
    protected FileNameExtensionFilter extensionFilter;
    protected ActionT1<File> onUpdate;
    protected float headerSpacing = 0.1f;
    public boolean showFullPath;
    public EUILabel header;
    public EUITextBox filePath;
    public EUIButton selectButton;
    public EUIButton clearButton;

    public EUIFileSelector(AdvancedHitbox hb)
    {
        this(hb, EUIRM.Images.Panel.Texture());
    }

    public EUIFileSelector(AdvancedHitbox hb, Texture texture)
    {
        super(hb);
        this.header = new EUILabel(EUIFontHelper.CardTitleFont_Small, hb).SetAlignment(0.5f,0.0f,false);
        this.filePath = new EUITextBox(texture, new RelativeHitbox(hb, hb.width, hb.height, hb.width * (1.5f + headerSpacing), hb.height * 0.5f, false));
        this.selectButton = new EUIButton(EUIRM.Images.FileSelectButton.Texture(), new RelativeHitbox(hb, hb.height, hb.height, hb.width * (2.1f + headerSpacing), hb.height * 0.5f, false)).SetOnClick(this::ChooseFile);
        this.clearButton = new EUIButton(EUIRM.Images.X.Texture(), new RelativeHitbox(selectButton.hb, hb.height, hb.height, selectButton.hb.width * 1.5f, hb.height * 0.5f,  false)).SetOnClick(() -> this.SelectFile(null, true));
    }

    public EUIFileSelector SetHeader(BitmapFont font, float fontScale, Color textColor, String text) {
        return SetHeader(font,fontScale,textColor,text,false);
    }

    public EUIFileSelector SetHeader(BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.SetFont(font, fontScale).SetColor(textColor).SetText(text).SetSmartText(smartText).SetActive(true);
        return this;
    }

    public EUIFileSelector SetHeaderSpacing(float headerSpacing) {
        this.headerSpacing = headerSpacing;
        this.filePath.hb = new RelativeHitbox(hb, hb.width, hb.height, hb.width * (1 + headerSpacing), 0, false);
        return this;
    }

    public EUIFileSelector SetHeaderText(String text) {
        this.header.SetText(text);
        return this;
    }

    public EUIFileSelector SetOnUpdate(ActionT1<File> onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    public EUIFileSelector SetShowFullPath(boolean showFullPath) {
        this.showFullPath = showFullPath;
        return this;
    }

    public EUIFileSelector SetFileFilters(String... filters) {
        this.extensionFilter = new FileNameExtensionFilter(EUIUtils.JoinStrings(", ", EUIUtils.Map(filters, f -> "*." + f)), filters);
        return this;
    }

    @Override
    public void Update()
    {
        super.Update();
        header.Update();
        filePath.Update();
        selectButton.Update();
        clearButton.Update();
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        header.Render(sb);
        filePath.Render(sb);
        selectButton.Render(sb);
        clearButton.Render(sb);
    }

    protected void ChooseFile()
    {
        try
        {
            JFileChooser fc = new JFileChooser();
            if (extensionFilter != null)
            {
                fc.setFileFilter(extensionFilter);
            }
            fc.setDropTarget(new DropTarget()
            {
                public synchronized void drop(DropTargetDropEvent evt)
                {
                    try
                    {
                        evt.acceptDrop(DnDConstants.ACTION_COPY);
                        Transferable t = evt.getTransferable();
                        if (t != null && t.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
                        {
                            List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                            for (File file : droppedFiles)
                            {
                                fc.setSelectedFiles(droppedFiles.toArray(new File[]{}));
                            }
                            evt.dropComplete(true);
                        }
                        else
                        {
                            evt.dropComplete(false);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        evt.dropComplete(false);
                    }
                }
            });

            if (currentFile != null && currentFile.exists())
            {
                File cd = currentFile.getParentFile();
                if (cd != null && cd.isDirectory())
                {
                    fc.setCurrentDirectory(cd);
                }
            }


            JFrame f = new JFrame();
            f.toFront();
            f.setLocationRelativeTo(null);
            f.setPreferredSize(new Dimension(Settings.WIDTH / 2, Settings.HEIGHT / 2));
            fc.setPreferredSize(f.getPreferredSize());
            f.setVisible(true);

            int result = fc.showOpenDialog(f);
            f.setVisible(false);
            f.dispose();

            if (result == JFileChooser.APPROVE_OPTION)
            {
                SelectFile(fc.getSelectedFile(), true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            EUIUtils.LogError(this, "Failed to select file");
        }
    }

    protected void SelectFile(File file, boolean shouldInvoke)
    {
        currentFile = file;
        if (currentFile != null)
        {
            if (showFullPath)
            {
                filePath.SetText(currentFile.getAbsolutePath());
            }
            else
            {
                filePath.SetText(currentFile.getName());
                filePath.tooltip = new EUITooltip("", currentFile.getAbsolutePath()).RenderBackground(false);
            }
        }
        else
        {
            filePath.SetText("");
            filePath.tooltip = null;
        }

        if (shouldInvoke && onUpdate != null)
        {
            onUpdate.Invoke(currentFile);
        }
    }
}
