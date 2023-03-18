package extendedui.interfaces.markers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public interface CountingPanelItem
{
    Texture getIcon();
    default Color getColor()
    {
        return Color.WHITE;
    }
}
