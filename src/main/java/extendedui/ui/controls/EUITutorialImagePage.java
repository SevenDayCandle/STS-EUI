package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.interfaces.delegates.ActionT1;

public class EUITutorialImagePage extends EUITutorialPage {
    protected EUIImage image;

    public EUITutorialImagePage(String title, String description, EUIImage image) {
        super(title, description);
        this.image = image;
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
