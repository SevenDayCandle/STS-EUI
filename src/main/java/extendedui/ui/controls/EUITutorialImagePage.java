package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.hitboxes.RelativeHitbox;

public class EUITutorialImagePage extends EUITutorialPage {
    protected EUIImage image;

    public EUITutorialImagePage(String title, String description, Texture image) {
        this(title, description, new EUIImage(image));
    }

    public EUITutorialImagePage(String title, String description, EUIImage image) {
        super(title, description);
        this.image = image;
    }

    protected EUITutorialPage setTutorial(EUITutorial tutorial) {
        super.setTutorial(tutorial);
        float w = this.image.hb.width;
        float h = this.image.hb.height;
        float hMax = tutorial.hb.height * 0.38f;
        if (h > hMax) {
            float ratio = hMax / h;
            h = hMax;
            w = w * ratio;
        }
        this.image.hb = new RelativeHitbox(tutorial.hb, w, h, tutorial.hb.width * 0.5f, tutorial.hb.height * 0.33f);
        return this;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        image.render(sb);
    }

    @Override
    public void updateImpl() {
        image.updateImpl();
    }
}
