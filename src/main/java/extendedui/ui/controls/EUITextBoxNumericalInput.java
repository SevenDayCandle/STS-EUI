package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.ui.hitboxes.AdvancedHitbox;
import org.apache.commons.lang3.math.NumberUtils;

public class EUITextBoxNumericalInput extends EUITextBoxInput
{

    protected ActionT1<Integer> onUpdateNumber;
    protected ActionT1<Integer> onCompleteNumber;

    public EUITextBoxNumericalInput(Texture backgroundTexture, AdvancedHitbox hb) {
        super(backgroundTexture, hb);
    }

    public EUITextBoxNumericalInput SetOnCompleteNumber(ActionT1<Integer> onComplete) {
        this.onCompleteNumber = onComplete;
        return this;
    }

    public EUITextBoxNumericalInput SetOnUpdateNumber(ActionT1<Integer> onUpdate) {
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
    protected void RenderUnderscore(SpriteBatch sb, float cur_x) {
        // Do not render
    }

    @Override
    public void End(boolean commit) {
        super.End(commit);
        if (commit && onCompleteNumber != null && !label.text.isEmpty()) {
            onCompleteNumber.Invoke(Integer.parseInt(label.text));
        }
    }
}
