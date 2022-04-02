package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Texture;
import org.apache.commons.lang3.math.NumberUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.hitboxes.AdvancedHitbox;

public class GUI_TextBoxNumericalInput extends GUI_TextBoxInput {

    protected ActionT1<Integer> onUpdateNumber;
    protected ActionT1<Integer> onCompleteNumber;

    public GUI_TextBoxNumericalInput(Texture backgroundTexture, AdvancedHitbox hb) {
        super(backgroundTexture, hb);
    }

    public GUI_TextBoxNumericalInput SetOnCompleteNumber(ActionT1<Integer> onComplete) {
        this.onCompleteNumber = onComplete;
        return this;
    }

    public GUI_TextBoxNumericalInput SetOnUpdateNumber(ActionT1<Integer> onUpdate) {
        this.onUpdateNumber = onUpdate;
        return this;
    }

    @Override
    public boolean acceptCharacter(char c) {
        return label.font.getData().hasGlyph(c) && Character.isDigit(c);
    }

    @Override
    public void Start() {
        super.Start();
        if (!NumberUtils.isCreatable(label.text)) {
            label.text = "";
        }
    }

    @Override
    public void End(boolean commit) {
        super.End(commit);
        if (commit && onCompleteNumber != null && !label.text.isEmpty()) {
            onCompleteNumber.Invoke(Integer.parseInt(label.text));
        }
    }
}
