package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class EUIPreview {

    public abstract boolean matches(String preview);

    public abstract void render(SpriteBatch sb, float x, float y, float scale, boolean upgraded);
}
