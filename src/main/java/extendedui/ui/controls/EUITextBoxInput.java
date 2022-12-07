package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Texture;
import extendedui.ui.hitboxes.EUIHitbox;

public class EUITextBoxInput extends EUITextBoxReceiver<String> {
    public EUITextBoxInput(Texture backgroundTexture, EUIHitbox hb) {
        super(backgroundTexture, hb);
    }

    @Override
    String getValue(String text)
    {
        return text;
    }
}
