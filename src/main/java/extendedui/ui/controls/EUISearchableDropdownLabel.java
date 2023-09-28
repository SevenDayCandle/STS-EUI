package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIInputManager;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.TextInputProvider;
import extendedui.ui.hitboxes.EUIHitbox;

public class EUISearchableDropdownLabel extends EUILabel implements TextInputProvider {
    protected final StringBuilder buffer = new StringBuilder();
    protected ActionT1<CharSequence> onUpdate;
    protected ActionT1<String> onComplete;

    public EUISearchableDropdownLabel(BitmapFont font) {
        super(font);
    }

    public EUISearchableDropdownLabel(BitmapFont font, EUIHitbox hb) {
        super(font, hb);
    }

    @Override
    public boolean acceptCharacter(char c) {
        return font.getData().hasGlyph(c);
    }

    @Override
    public void complete() {
        EUIInputManager.releaseType(this);
        text = buffer.toString();
        if (onComplete != null) {
            onComplete.invoke(text);
        }
    }

    @Override
    public StringBuilder getBuffer() {
        return buffer;
    }

    @Override
    public void onUpdate(int pos, char keycode) {
        if (onUpdate != null) {
            onUpdate.invoke(buffer);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        if (isEditing()) {
            renderImpl(sb, hb, textColor, buffer);
        }
        else {
            renderImpl(sb, hb);
        }
        hb.render(sb);
    }

    public EUISearchableDropdownLabel setOnComplete(ActionT1<String> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public EUISearchableDropdownLabel setOnUpdate(ActionT1<CharSequence> onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }

    public boolean start() {
        boolean val = EUIInputManager.tryStartType(this);
        if (val) {
            setBufferText(text);
        }
        return val;
    }
}
