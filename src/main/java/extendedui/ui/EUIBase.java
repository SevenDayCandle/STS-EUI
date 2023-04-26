package extendedui.ui;

import basemod.IUIElement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIGameUtils;

public abstract class EUIBase implements IUIElement {
    public static final Color HOVER_BLEND_COLOR = new Color(1f, 1f, 1f, 0.3f);
    public static final Color TEXT_DISABLED_COLOR = new Color(0.6f, 0.6f, 0.6f, 1f);

    public boolean isActive = true;

    public static float scale(float value) {
        return EUIGameUtils.scale(value);
    }

    public static float screenH(float value) {
        return EUIGameUtils.screenH(value);
    }

    public static float screenW(float value) {
        return EUIGameUtils.screenW(value);
    }

    public void render(SpriteBatch sb) {
        tryRender(sb);
    }

    public boolean tryRender(SpriteBatch sb) {
        if (isActive) {
            renderImpl(sb);
        }

        return isActive;
    }

    public abstract void renderImpl(SpriteBatch sb);

    public void update() {
        tryUpdate();
    }

    public boolean tryUpdate() {
        if (isActive) {
            updateImpl();
        }

        return isActive;
    }

    public abstract void updateImpl();

    public int renderLayer() {
        return 1;
    }

    public int updateOrder() {
        return 1;
    }

    public EUIBase setActive(boolean active) {
        this.isActive = active;

        return this;
    }
}
