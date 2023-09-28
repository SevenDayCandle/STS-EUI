package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.ui.hitboxes.EUIHitbox;
import org.apache.commons.lang3.math.NumberUtils;

public class EUITextBoxNumericalInput extends EUITextBoxReceiver<Integer> {
    private int cachedValue;
    private int tempValue;
    private boolean hasEntered;
    protected int min = Integer.MIN_VALUE;
    protected int max = Integer.MAX_VALUE;
    public boolean showNegativeAsInfinity;

    public EUITextBoxNumericalInput(Texture backgroundTexture, EUIHitbox hb) {
        super(backgroundTexture, hb);
    }

    @Override
    public boolean acceptCharacter(char c) {
        return Character.isDigit(c) || c == '-';
    }

    @Override
    public void complete() {
        super.complete();
        cachedValue = tempValue = getValue(label.text);
        forceUpdateText();
    }

    public void forceSetValue(int value, boolean invoke) {
        cachedValue = tempValue = MathUtils.clamp(value, min, max);
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

    @Override
    public Integer getValue(String text) {
        try {
            return MathUtils.clamp(Integer.parseInt(label.text), min, max);
        }
        catch (Exception ignored) {
            return 0;
        }
    }

    @Override
    protected void renderUnderscore(SpriteBatch sb) {
        // Do not render
    }

    public EUITextBoxNumericalInput setLimits(int min, int max) {
        this.min = min;
        this.max = Math.max(max, min);
        return this;
    }

    @Override
    public void onUpdate(int pos, char keycode) {
        if (!hasEntered && keycode != EUIInputManager.DUMMY_PASTE) {
            getBuffer().setLength(0);
            if (keycode >= 32) {
                buffer.append(keycode);
            }
            EUIInputManager.resetPos();
        }
        else {
            tempValue = getValue(buffer.toString());
        }
        hasEntered = true;
    }

    protected void setTempValue(int value) {
        tempValue = MathUtils.clamp(value, min, max);
        hasEntered = true;
        setBufferText(String.valueOf(tempValue));
    }

    public EUITextBoxNumericalInput showNegativeAsInfinity(boolean val) {
        showNegativeAsInfinity = val;
        return this;
    }

    public boolean start() {
        if (isActive) {
            boolean val = EUIInputManager.tryStartType(this);
            if (val) {
                EUI.pushActiveElement(this);
                hasEntered = false;
                if (!NumberUtils.isCreatable(label.text)) {
                    clearBuffer();
                }
                else {
                    setBufferText(label.text);
                }
            }
            return val;
        }
        return false;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        if (isEditing()) {
            if (InputHelper.scrolledDown) {
                setTempValue(tempValue - 1);
            }
            else if (InputHelper.scrolledUp) {
                setTempValue(tempValue + 1);
            }
        }
    }
}
