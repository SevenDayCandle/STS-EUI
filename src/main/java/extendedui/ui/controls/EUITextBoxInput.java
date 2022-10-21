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
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;

public class EUITextBoxInput extends EUITextBox implements TextReceiver {
    protected ActionT1<String> onComplete;
    protected ActionT1<String> onUpdate;
    protected Color editTextColor;
    protected Color originalTextColor;
    protected String originalValue;
    protected boolean isEditing;
    protected float headerSpacing = 0.6f;
    public EUILabel header;

    public EUITextBoxInput(Texture backgroundTexture, AdvancedHitbox hb) {
        super(backgroundTexture, hb);
        this.header = new EUILabel(EUIFontHelper.CardTitleFont_Small,
                new AdvancedHitbox(hb.x, hb.y + hb.height * headerSpacing, hb.width, hb.height)).SetAlignment(0.5f,0.0f,false);
        this.header.SetActive(false);
        editTextColor = EUIColors.Green(1).cpy();
        originalTextColor = this.label.textColor.cpy();
    }

    public EUITextBoxInput SetHeader(BitmapFont font, float fontScale, Color textColor, String text) {
        return SetHeader(font,fontScale,textColor,text,false);
    }

    public EUITextBoxInput SetHeader(BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.SetFont(font, fontScale).SetColor(textColor).SetText(text).SetSmartText(smartText).SetActive(true);
        return this;
    }

    public EUITextBoxInput SetHeaderSpacing(float headerSpacing) {
        this.headerSpacing = headerSpacing;
        this.header.hb.move(hb.cX, hb.cY + hb.height * headerSpacing);
        return this;
    }

    public EUITextBoxInput SetOnComplete(ActionT1<String> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public EUITextBoxInput SetOnUpdate(ActionT1<String> onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    public EUITextBox SetColors(Color backgroundColor, Color textColor)
    {
        this.image.SetColor(backgroundColor);
        this.label.SetColor(textColor);
        originalTextColor = textColor;

        return this;
    }

    public EUITextBox SetFontColor(Color textColor)
    {
        this.label.SetColor(textColor);
        originalTextColor = textColor.cpy();

        return this;
    }

    public EUITextBox SetFontColor(Color textColor, Color editTextColor)
    {
        SetFontColor(textColor);
        this.editTextColor = editTextColor.cpy();

        return this;
    }

    @Override
    public EUITextBox SetPosition(float x, float y)
    {
        this.hb.move(x, y);
        this.header.hb.move(x, y + hb.height * headerSpacing);

        return this;
    }

    public void SetTextAndCommit(String text) {
        this.label.SetText(text);
        if (onComplete != null) {
            onComplete.Invoke(text);
        }
    }

    @Override
    public void Update()
    {
        super.Update();
        if (EUIInputManager.LeftClick.IsJustReleased()) {
            if (!isEditing && (hb.hovered || hb.clicked)) {
                Start();
            }
            else if (isEditing && !hb.hovered && !hb.clicked) {
                End(true);
            }
        }
        else if (isEditing && Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            End(false);
        }

        header.TryUpdate();
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        super.Render(sb);
        float cur_x = FontHelper.layout.width;
        header.TryRender(sb);
        RenderUnderscore(sb, cur_x);
    }

    protected void RenderUnderscore(SpriteBatch sb, float cur_x) {
        if (isEditing) {
            EUI.AddPriorityPostRender(s ->
                    FontHelper.renderFontLeft(sb, label.font, "_", hb.x + cur_x + hb.width * label.horizontalRatio, hb.y + hb.height / 2, EUIColors.White(0.5f + EUI.Time_Cos(0.5f, 4f))));
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
            onUpdate.Invoke(label.text);
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
        End(true);
        return true;
    }

    public void Start() {
        isEditing = true;
        TextInput.startTextReceiver(this);
        label.SetColor(editTextColor);
        originalValue = label.text;
    }

    public void End(boolean commit) {
        isEditing = false;
        TextInput.stopTextReceiver(this);
        label.SetColor(originalTextColor);
        if (commit) {
            if (onComplete != null) {
                onComplete.Invoke(label.text);
            }
        }
        else {
            label.text = originalValue;
        }
    }
}
