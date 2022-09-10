package extendedui.interfaces.markers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.configuration.STSConfigItem;

public interface ModSettingsProvider<T>
{
    public STSConfigItem<T> Config();
    public void UpdateProvider();
    public void RenderProvider(SpriteBatch sb);
}
