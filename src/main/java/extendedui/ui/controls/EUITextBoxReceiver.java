package extendedui.ui.controls;

import basemod.interfaces.TextReceiver;
import basemod.patches.com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor.TextInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;

public abstract class EUITextBoxReceiver<T> extends EUITextBox implements TextReceiver {
    protected ActionT1<T> onComplete;
    protected ActionT1<T> onUpdate;
    protected Color editTextColor;
    protected Color originalTextColor;
    protected String originalValue;
    protected boolean isEditing;
    protected float headerSpacing = 0.6f;
    public EUILabel header;

    public EUITextBoxReceiver(Texture backgroundTexture, EUIHitbox hb) {
        super(backgroundTexture, hb);
        this.header = new EUILabel(EUIFontHelper.cardtitlefontSmall,
                new EUIHitbox(hb.x, hb.y + hb.height * headerSpacing, hb.width, hb.height)).setAlignment(0.5f,0.0f,false);
        this.header.setActive(false);
        editTextColor = EUIColors.green(1).cpy();
        originalTextColor = this.label.textColor.cpy();
    }

    public EUITextBoxReceiver<T> setHeader(BitmapFont font, float fontScale, Color textColor, String text) {
        return setHeader(font,fontScale,textColor,text,false);
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

    public EUITextBoxReceiver<T> setOnUpdate(ActionT1<T> onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    public EUITextBox setColors(Color backgroundColor, Color textColor)
    {
        this.image.setColor(backgroundColor);
        this.label.setColor(textColor);
        originalTextColor = textColor;

        return this;
    }

    public EUITextBox setFontColor(Color textColor)
    {
        this.label.setColor(textColor);
        originalTextColor = textColor.cpy();

        return this;
    }

    public EUITextBox setFontColor(Color textColor, Color editTextColor)
    {
        setFontColor(textColor);
        this.editTextColor = editTextColor.cpy();

        return this;
    }

    @Override
    public EUITextBox setPosition(float x, float y)
    {
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

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        if (EUIInputManager.leftClick.isJustReleased()) {
            if (!isEditing && (hb.hovered || hb.clicked) && EUI.tryClick(this.hb)) {
                start();
            }
            else if (isEditing && !hb.hovered && !hb.clicked) {
                end(true);
            }
        }
        else if (isEditing && Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            end(false);
        }

        header.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);
        float cur_x = FontHelper.layout.width;
        header.tryRender(sb);
        renderUnderscore(sb, cur_x);
    }

    protected void renderUnderscore(SpriteBatch sb, float cur_x) {
        if (isEditing) {
            EUI.addPriorityPostRender(s ->
                    FontHelper.renderFontLeft(sb, label.font, "_", hb.x + cur_x + hb.width * label.horizontalRatio, hb.y + hb.height / 2, EUIColors.white(0.5f + EUI.timeCos(0.5f, 4f))));
        }
    }

    @Override
    public String getCurrentText() {
        return label.text;
    }

    @Override
    public void setText(String s) {
        label.text = s;
        if (onUpdate != null) {
            onUpdate.invoke(getValue(label.text));
        }
    }

    @Override
    public boolean isDone() {
        return !isEditing;
    }

    @Override
    public boolean acceptCharacter(char c) {
        return label.font.getData().hasGlyph(c);
    }

    @Override
    public boolean onPushEnter() {
        end(true);
        return true;
    }

    public void start() {
        isEditing = true;
        EUI.setActiveElement(this);
        TextInput.startTextReceiver(this);
        label.setColor(editTextColor);
        originalValue = label.text;
    }

    public void end(boolean commit) {
        isEditing = false;
        EUI.setActiveElement(null);
        TextInput.stopTextReceiver(this);
        label.setColor(originalTextColor);
        commit(commit);
    }

    protected void commit(boolean commit)
    {
        if (commit) {
            if (onComplete != null) {
                onComplete.invoke(getValue(label.text));
            }
        }
        else {
            label.text = originalValue;
        }
    }

    abstract T getValue(String text);
}
