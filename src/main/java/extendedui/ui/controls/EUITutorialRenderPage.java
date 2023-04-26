package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.interfaces.delegates.ActionT1;

public class EUITutorialRenderPage extends EUITutorialPage {
    protected ActionT1<SpriteBatch> postRender;

    public EUITutorialRenderPage(String title, String description, ActionT1<SpriteBatch> postRender) {
        super(title, description);
        this.postRender = postRender;
    }

    @Override
    public void updateImpl() {

    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        postRender.invoke(sb);
    }
}
