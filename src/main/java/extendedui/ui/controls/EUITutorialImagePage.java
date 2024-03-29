package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    @Override
    public void renderImpl(SpriteBatch sb) {
        image.render(sb);
    }

    protected EUITutorialPage setTutorial(EUITutorial tutorial) {
        super.setTutorial(tutorial);
        float w = this.image.hb.width;
        float h = this.image.hb.height;
        float hMax = tutorial.hb.height * 0.49f;
        if (h > hMax) {
            float ratio = hMax / h;
            h = hMax;
            w = w * ratio;
        }
        this.image.hb = new RelativeHitbox(tutorial.hb, w, h, tutorial.hb.width * 0.5f, h / 2 + scale(60));
        return this;
    }

    @Override
    public void updateImpl() {
        image.updateImpl();
    }
}
