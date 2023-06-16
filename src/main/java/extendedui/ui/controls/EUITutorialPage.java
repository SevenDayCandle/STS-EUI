package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.ui.EUIBase;

public class EUITutorialPage extends EUIBase {
    public String title;
    public String description;
    protected EUITutorial tutorial;

    public EUITutorialPage(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public boolean isHovered() {
        return false;
    }

    protected EUITutorialPage setTutorial(EUITutorial tutorial) {
        this.tutorial = tutorial;
        return this;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {

    }

    @Override
    public void updateImpl() {

    }
}
