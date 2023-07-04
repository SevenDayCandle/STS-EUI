package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.ui.hitboxes.EUIHitbox;
import org.apache.commons.lang3.math.NumberUtils;

public class EUITextBoxNumericalInput extends EUITextBoxReceiver<Integer> {
    protected int cachedValue;
    protected int min = Integer.MIN_VALUE;
    protected int max = Integer.MAX_VALUE;
    public boolean hasEntered;
    public boolean showNegativeAsInfinity;
    public boolean clearOnInitialEntry = true;

    public EUITextBoxNumericalInput(Texture backgroundTexture, EUIHitbox hb) {
        super(backgroundTexture, hb);
    }

    @Override
    protected void commit(boolean commit) {
        if (commit) {
            if (onComplete != null) {
                onComplete.invoke(cachedValue);
            }
            forceUpdateText();
        }
        else {
            label.text = originalValue;
        }
    }

    @Override
    public String getCurrentText() {
        return !hasEntered && clearOnInitialEntry ? "" : label.text;
    }

    @Override
    public void setText(String s) {
        hasEntered = true;
        label.text = s;
        cachedValue = getValue(label.text);
        if (onUpdate != null) {
            onUpdate.invoke(cachedValue);
        }
    }

    @Override
    public boolean acceptCharacter(char c) {
        return Character.isDigit(c) || c == '-';
    }

    @Override
    protected void renderUnderscore(SpriteBatch sb, float cur_x) {
        // Do not render
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        if (isEditing) {
            if (InputHelper.scrolledDown) {
                setValue(cachedValue - 1);
            }
            else if (InputHelper.scrolledUp) {
                setValue(cachedValue + 1);
            }
        }
    }

    @Override
    public void start() {
        super.start();
        if (!NumberUtils.isCreatable(label.text)) {
            label.text = "";
            cachedValue = 0;
        }
        else {
            cachedValue = getValue(label.text);
        }
        hasEntered = false;
    }

    @Override
    public Integer getValue(String text) {
        try {
            return MathUtils.clamp(Integer.parseInt(label.text), min, max);
        }
        catch (Exception ignored) {
            return 0;
        }
    }

    public void forceSetValue(int value, boolean invoke) {
        cachedValue = MathUtils.clamp(value, min, max);
        forceUpdateText();
        if (invoke && onComplete != null) {
            onComplete.invoke(cachedValue);
        }
    }

    public void forceUpdateText() {
        if (showNegativeAsInfinity && cachedValue < 0) {
            label.text = label.font.getData().hasGlyph('∞') ? "∞" : "Inf";
        }
        else {
            // Ensure parity between label and cachedValue, in case getText is overwritten
            label.text = String.valueOf(cachedValue);
        }
    }

    public int getCachedValue() {
        return cachedValue;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public EUITextBoxNumericalInput setLimits(int min, int max) {
        this.min = min;
        this.max = Math.max(max, min);
        return this;
    }

    protected void setValue(int value) {
        setText(String.valueOf(MathUtils.clamp(value, min, max)));
    }

    public EUITextBoxNumericalInput showNegativeAsInfinity(boolean val) {
        showNegativeAsInfinity = val;
        return this;
    }
}
