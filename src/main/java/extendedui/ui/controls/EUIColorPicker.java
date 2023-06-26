package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.github.tommyettinger.colorful.rgb.GradientTools;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIColors;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class EUIColorPicker extends EUIHoverable {
    protected static final float HUE_WIDTH = scale(26);

    private final ShapeRenderer renderer;
    private Color topRight = Color.RED.cpy();
    private float hue;
    private float sat;
    private float val;
    private float alpha;
    protected Color returnColor = Color.WHITE.cpy();
    protected ActionT1<EUIColorPicker> onChange;
    protected EUIHitbox hueHb;
    protected EUIHitbox alphaHb;

    public EUIColorPicker(EUIHitbox hb) {
        super(hb);
        renderer = new ShapeRenderer();
        hueHb = new RelativeHitbox(hb, HUE_WIDTH, hb.height, hb.width + scale(30), hb.height * 0.5f);
        alphaHb = new RelativeHitbox(hb, HUE_WIDTH, hb.height, hb.width + HUE_WIDTH + scale(40), hb.height * 0.5f);
    }

    public Color getReturnColor() {
        return returnColor;
    }

    public float getAlpha() {return alpha;}

    public float getHue() {return hue;}

    public float getSat() {
        return sat;
    }

    public float getVal() {
        return val;
    }

    // Adapted from https://www.niwa.nu/2013/05/math-behind-colorspace-conversions-rgb-hsl/
    // Divide resulting hue by 6 because we want to map it to the range 0, 1
    public EUIColorPicker setColor(Color color) {
        returnColor = color.cpy();

        float cmax = Math.max(color.r, Math.max(color.g, color.b));
        float cmin = Math.min(color.r, Math.min(color.g, color.b));
        float diff = cmax - cmin;

        if (diff == 0) {
            updateHueImpl(0);
        }
        else if (cmax == color.r) {
            updateHueImpl(((color.g - color.b) / (diff)) / 6);
        }
        else if (cmax == color.g) {
            updateHueImpl((2f + (color.b - color.r) / (diff)) / 6);
        }
        else if (cmax == color.b) {
            updateHueImpl((4f + (color.r - color.g) / (diff)) / 6);
        }

        if (cmax == 0)
            sat = 0;
        else
            sat = (diff / cmax);

        val = cmax;

        alpha = returnColor.a;

        return this;
    }

    protected void updateHueImpl(float hue) {
        if (hue < 0) {
            hue += 1;
        }
        else if (hue > 1) {
            hue -= 1;
        }
        int i = MathUtils.floor(hue * 6);
        float f = hue * 6 - i;

        switch (i % 6) {
            case 0: topRight.r = 1; topRight.g = f; topRight.b = 0; break;
            case 1: topRight.r = (1 - f); topRight.g = 1; topRight.b = 0; break;
            case 2: topRight.r = 0; topRight.g = 1; topRight.b = f; break;
            case 3: topRight.r = 0; topRight.g = (1 - f); topRight.b = 1; break;
            case 4: topRight.r = f; topRight.g = 0; topRight.b = 1; break;
            case 5: topRight.r = 1; topRight.g = 0; topRight.b = (1 - f); break;
        }

        this.hue = hue;
    }

    public EUIColorPicker setAlpha(float val) {
        this.alpha = val;
        updateReturnColor();
        return this;
    }

    // Assuming Saturation = 1, Value = 1
    public EUIColorPicker setHue(float hue) {
        updateHueImpl(hue);
        updateReturnColor();
        return this;
    }

    public EUIColorPicker setSat(float sat) {
        this.sat = sat;
        updateReturnColor();
        return this;
    }

    public EUIColorPicker setVal(float val) {
        this.val = val;
        updateReturnColor();
        return this;
    }

    public EUIColorPicker setOnChange(ActionT1<EUIColorPicker> onChange) {
        this.onChange = onChange;
        return this;
    }

    protected void setAlphaFromMouse(float my) {
        setAlpha((my - alphaHb.y) / alphaHb.height);
    }

    protected void setHueFromMouse(float my) {
        setHue((my - hueHb.y) / hueHb.height);
    }

    protected void updateSatValFromMouse(float mx, float my) {
        sat = (mx - hb.x) / hb.width;
        val = (my - hb.y) / hb.height;
        updateReturnColor();
    }

    protected void updateReturnColor() {
        returnColor.r = MathUtils.lerp(1, topRight.r, sat);
        returnColor.g = MathUtils.lerp(1, topRight.g, sat);
        returnColor.b = MathUtils.lerp(1, topRight.b, sat);
        returnColor.r = MathUtils.lerp(0, returnColor.r, val);
        returnColor.g = MathUtils.lerp(0, returnColor.g, val);
        returnColor.b = MathUtils.lerp(0, returnColor.b, val);
        returnColor.a = alpha;
        if (onChange != null) {
            onChange.invoke(this);
        }
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        hueHb.update();
        alphaHb.update();
        if (hb.hovered && EUIInputManager.leftClick.isPressed()) {
            updateSatValFromMouse(InputHelper.mX, InputHelper.mY);
        }
        else if (hueHb.hovered && EUIInputManager.leftClick.isPressed()) {
            setHueFromMouse(InputHelper.mY);
        }
        else if (alphaHb.hovered && EUIInputManager.leftClick.isPressed()) {
            setAlphaFromMouse(InputHelper.mY);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        sb.end();
        renderer.setProjectionMatrix(sb.getProjectionMatrix());
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.rect(hb.x, hb.y, hb.width, hb.height, Color.BLACK, Color.BLACK, topRight, Color.WHITE);
        renderer.rect(alphaHb.x, alphaHb.y, alphaHb.width, alphaHb.height, Color.GRAY, Color.GRAY, Color.WHITE, Color.WHITE);
        renderer.end();
        sb.begin();
        sb.setColor(Color.WHITE);
        EUIRenderHelpers.drawRainbowVertical(sb, 0f, 0.9f, 1, 1f, s ->  {
            s.draw(ImageMaster.WHITE_SQUARE_IMG, hueHb.x, hueHb.y, hueHb.width, hueHb.height);
        });
    }
}
