package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.TextInputProvider;
import extendedui.text.EUITextHelper;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;

public abstract class EUITextBoxReceiver<T> extends EUITextBox implements TextInputProvider {
    protected final StringBuilder buffer = new StringBuilder();
    protected ActionT1<T> onComplete;
    protected ActionT0 onTab;
    protected Color editTextColor;
    protected float headerSpacing = 0.6f;
    public EUILabel header;

    public EUITextBoxReceiver(Texture backgroundTexture, EUIHitbox hb) {
        super(backgroundTexture, hb);
        this.header = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(hb.x, hb.y + hb.height * headerSpacing, hb.width, hb.height)).setAlignment(0.5f, 0.0f, false);
        this.header.setActive(false);
        editTextColor = EUIColors.green(1).cpy();
    }

    @Override
    public boolean acceptCharacter(char c) {
        return label.font.getData().hasGlyph(c);
    }

    @Override
    public void cancel() {
        EUIInputManager.releaseType(this);
        EUI.popActiveElement(this);
    }

    @Override
    public void complete() {
        EUIInputManager.releaseType(this);
        EUI.popActiveElement(this);
        label.text = buffer.toString();
        if (onComplete != null) {
            onComplete.invoke(getValue(label.text));
        }
    }

    @Override
    public StringBuilder getBuffer() {
        return buffer;
    }

    @Override
    public boolean onPushTab() {
        complete();
        if (onTab != null) {
            onTab.invoke();
        }
        return true;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        if (isEditing()) {
            image.render(sb);
            label.renderImpl(sb, label.hb, editTextColor, buffer);
            hb.render(sb);
            renderUnderscore(sb);
        }
        else {
            image.render(sb);
            label.render(sb);
            hb.render(sb);
        }
        header.tryRender(sb);
    }

    protected void renderUnderscore(SpriteBatch sb) {
        if (isEditing()) {
            GlyphLayout.GlyphRun run = EUITextHelper.getLayoutRun(0);
            int pos = EUIInputManager.getPos() + 1;
            if (run != null && pos >= 0 && pos < run.xAdvances.size) {
                float extra = 0;
                for (int i = 0; i < pos; i++) {
                    extra += run.xAdvances.get(i);
                }
                float xOff = hb.x + extra + hb.width * label.horizontalRatio;
                float yOff = hb.y + run.y + hb.height * label.verticalRatio - scale(15);
                EUI.addPriorityPostRender(s ->
                        EUITextHelper.renderFontLeft(sb, label.font, "_", xOff, yOff, EUIColors.white(0.5f + EUI.timeCos(0.5f, 4f))));
                return;
            }
            float xOff = hb.x + EUITextHelper.getLayoutWidth() + hb.width * label.horizontalRatio;
            float yOff = hb.y + hb.height * label.verticalRatio- scale(15);
            EUI.addPriorityPostRender(s ->
                    EUITextHelper.renderFontLeft(sb, label.font, "_", xOff, yOff, EUIColors.white(0.5f + EUI.timeCos(0.5f, 4f))));
        }
    }

    public EUITextBoxReceiver<T> setColors(Color backgroundColor, Color textColor) {
        this.image.setColor(backgroundColor);
        this.label.setColor(textColor);

        return this;
    }

    public EUITextBoxReceiver<T> setFontColor(Color textColor) {
        this.label.setColor(textColor);

        return this;
    }

    public EUITextBoxReceiver<T> setFontColor(Color textColor, Color editTextColor) {
        setFontColor(textColor);
        this.editTextColor = editTextColor.cpy();

        return this;
    }

    public EUITextBoxReceiver<T> setHeader(BitmapFont font, float fontScale, Color textColor, String text) {
        return setHeader(font, fontScale, textColor, text, false);
    }

    public EUITextBoxReceiver<T> setHeader(BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.setFont(font, fontScale).setColor(textColor).setLabel(text).setSmartText(smartText).setActive(true);
        return this;
    }

    public EUITextBoxReceiver<T> setHeaderSpacing(float headerSpacing) {
        this.headerSpacing = headerSpacing;
        this.header.hb.move(hb.cX, hb.cY + hb.height * headerSpacing);
        return this;
    }

    public EUITextBoxReceiver<T> setOnComplete(ActionT1<T> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public EUITextBoxReceiver<T> setOnTab(ActionT0 onComplete) {
        this.onTab = onComplete;
        return this;
    }

    @Override
    public EUITextBoxReceiver<T> setPosition(float x, float y) {
        this.hb.move(x, y);
        this.header.hb.move(x, y + hb.height * headerSpacing);

        return this;
    }

    public void setTextAndCommit(String text) {
        this.label.setLabel(text);
        if (onComplete != null) {
            onComplete.invoke(getValue(text));
        }
    }

    public EUITextBoxReceiver<T> setTooltip(String name, String desc) {
        super.setTooltip(name, desc);
        this.header.setTooltip(this.tooltip);
        return this;
    }

    public EUITextBoxReceiver<T> setTooltip(EUITooltip tip) {
        super.setTooltip(tip);
        this.header.setTooltip(tip);
        return this;
    }

    public boolean start() {
        boolean val = EUIInputManager.tryStartType(this);
        if (val) {
            EUI.pushActiveElement(this);
            setBufferText(label.text);
        }
        return val;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        if (EUIInputManager.leftClick.isJustReleased()) {
            if (!isEditing() && (hb.hovered || hb.clicked) && EUI.tryClick(this.hb) && !EUITourTooltip.shouldBlockInteract(this.hb)) {
                start();
            }
            else if (isEditing() && !hb.hovered && !hb.clicked) {
                complete();
            }
        }

        header.tryUpdate();
    }

    abstract T getValue(String text);
}
