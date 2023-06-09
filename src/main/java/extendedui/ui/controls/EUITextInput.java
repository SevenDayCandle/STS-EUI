package extendedui.ui.controls;

import basemod.interfaces.TextReceiver;
import basemod.patches.com.megacrit.cardcrawl.helpers.input.ScrollInputProcessor.TextInput;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.hitboxes.EUIHitbox;

public class EUITextInput extends EUILabel implements TextReceiver {

    protected boolean isEditing;
    protected ActionT1<String> onUpdate;
    protected ActionT1<String> onComplete;

    public EUITextInput(BitmapFont font) {
        super(font);
    }

    public EUITextInput(BitmapFont font, EUIHitbox hb) {
        super(font, hb);
    }

    public void end() {
        isEditing = false;
        TextInput.stopTextReceiver(this);
        if (onComplete != null) {
            onComplete.invoke(text);
        }
    }

    @Override
    public String getCurrentText() {
        return text;
    }

    @Override
    public void setText(String s) {
        text = s;
        if (onUpdate != null) {
            onUpdate.invoke(text);
        }
    }

    @Override
    public boolean isDone() {
        return !isEditing;
    }

    @Override
    public boolean onPushEnter() {
        end();
        return true;
    }

    @Override
    public boolean acceptCharacter(char c) {
        return font.getData().hasGlyph(c);
    }

    public EUITextInput setOnComplete(ActionT1<String> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public EUITextInput setOnUpdate(ActionT1<String> onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    public void start() {
        isEditing = true;
        TextInput.startTextReceiver(this);
    }
}
