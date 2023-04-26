package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Texture;
import extendedui.ui.hitboxes.EUIHitbox;

public class EUIDialogYesNo extends EUIDialog<Boolean> {
    public EUIDialogYesNo(String headerText) {
        super(headerText, "");
    }

    public EUIDialogYesNo(String headerText, String descriptionText) {
        super(headerText, descriptionText);
    }

    public EUIDialogYesNo(String headerText, String descriptionText, float w, float h) {
        super(headerText, descriptionText, w, h);
    }

    public EUIDialogYesNo(EUIHitbox hb, Texture backgroundTexture, String headerText, String descriptionText) {
        super(hb, backgroundTexture, headerText, descriptionText);
    }

    @Override
    public Boolean getConfirmValue() {
        return true;
    }

    @Override
    public Boolean getCancelValue() {
        return false;
    }
}
