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

    public EUITextBoxNumericalInput setOnCompleteNumber(ActionT1<Integer> onComplete) {
        this.onCompleteNumber = onComplete;
        return this;
    }

    public EUITextBoxNumericalInput setOnUpdateNumber(ActionT1<Integer> onUpdate) {
        this.onUpdateNumber = onUpdate;
        return this;
    }

    @Override
    public boolean acceptCharacter(char c) {
        return label.font.getData().hasGlyph(c) && Character.isDigit(c);
    }

    @Override
    public void start() {
        super.start();
        if (!NumberUtils.isCreatable(label.text)) {
            label.text = "";
        }
    }

    @Override
    protected void renderUnderscore(SpriteBatch sb, float cur_x) {
        // Do not render
    }

    @Override
    public void end(boolean commit) {
        super.end(commit);
        if (commit && onCompleteNumber != null && !label.text.isEmpty()) {
            onCompleteNumber.invoke(Integer.parseInt(label.text));
        }
    }
}
