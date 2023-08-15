package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.controller.CInputAction;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.ui.hitboxes.EUIHitbox;

public class EUIControllerButton extends EUIButton {
    public CInputAction controllerKey;

    public EUIControllerButton(CInputAction controllerKey, Texture buttonTexture, float x, float y) {
        super(buttonTexture, x, y);
        this.controllerKey = controllerKey;
    }

    public EUIControllerButton(CInputAction controllerKey, Texture buttonTexture, EUIHitbox hitbox) {
        super(buttonTexture, hitbox);
        this.controllerKey = controllerKey;
    }

    protected boolean didClick() {
        return controllerKey.isJustPressed() || super.didClick();
    }

    protected void onLeftClick() {
        super.onLeftClick();
        controllerKey.unpress();
    }

    public void renderButton(SpriteBatch sb) {
        super.renderButton(sb, interactable);
        if (Settings.isControllerMode) {
            sb.draw(controllerKey.getKeyImg(), hb.cX - 32f, hb.cY - 32f + 100f * Settings.scale, 32f, 32f, 64f, 64f, Settings.scale, Settings.scale, 0f, 0, 0, 64, 64, false, false);
        }
    }

    public void renderButtonCentered(SpriteBatch sb) {
        super.renderButtonCentered(sb, interactable);
        if (Settings.isControllerMode) {
            sb.draw(controllerKey.getKeyImg(), hb.cX - 32f, hb.cY - 32f + 100f * Settings.scale, 32f, 32f, 64f, 64f, Settings.scale, Settings.scale, 0f, 0, 0, 64, 64, false, false);
        }
    }

    public EUIControllerButton setButtonFlip(boolean flipX, boolean flipY) {
        super.setButtonFlip(flipX, flipY);

        return this;
    }

    public EUIControllerButton setControllerKey(CInputAction key) {
        this.controllerKey = key;
        return this;
    }

    public EUIControllerButton setOnClick(ActionT0 onClick) {
        super.setOnClick(onClick);

        return this;
    }

    public EUIControllerButton setOnClick(ActionT1<EUIButton> onClick) {
        super.setOnClick(onClick);

        return this;
    }

    public <S> EUIControllerButton setOnClick(S item, ActionT1<S> onClick) {
        super.setOnClick(item, onClick);

        return this;
    }

    public <S> EUIControllerButton setOnClick(S item, ActionT2<S, EUIButton> onClick) {
        super.setOnClick(item, onClick);

        return this;
    }
}
