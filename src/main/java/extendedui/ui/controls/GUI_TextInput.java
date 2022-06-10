package extendedui.ui.controls;

import basemod.interfaces.TextReceiver;
import basemod.patches.com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor.TextInput;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.ui.hitboxes.AdvancedHitbox;

public class GUI_TextInput extends GUI_Label implements TextReceiver {

    protected boolean isEditing;
    protected ActionT1<String> onUpdate;
    protected ActionT1<String> onComplete;

    public GUI_TextInput(BitmapFont font) {
        super(font);
    }

    public GUI_TextInput(BitmapFont font, AdvancedHitbox hb) {
        super(font, hb);
    }

    public GUI_TextInput SetOnComplete(ActionT1<String> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public GUI_TextInput SetOnUpdate(ActionT1<String> onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    @Override
    public String getCurrentText() {
        return text;
    }

    @Override
    public void setText(String s) {
        text = s;
        if (onUpdate != null) {
            onUpdate.Invoke(text);
        }
    }

    @Override
    public boolean isDone() {
        return !isEditing;
    }

    @Override
    public boolean acceptCharacter(char c) {
        return font.getData().hasGlyph(c);
    }

    @Override
    public boolean onPushEnter() {
        End();
        return true;
    }

    public void Start() {
        isEditing = true;
        TextInput.startTextReceiver(this);
    }

    public void End() {
        isEditing = false;
        TextInput.stopTextReceiver(this);
        if (onComplete != null) {
            onComplete.Invoke(text);
        }
    }
}
