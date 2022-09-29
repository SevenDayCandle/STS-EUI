package extendedui.interfaces.markers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.configuration.STSConfigItem;

public interface ModSettingsProvider<T>
{
    public STSConfigItem<T> Config();
    public void Set(T value);
    public void UpdateProvider();
    public void RenderProvider(SpriteBatch sb);
    default public void SetAndInvoke(T value)
    {
        Set(value);
        Config().Set(value, true);
    }
    default public void UpdateAndRefresh()
    {
        Set(Config().Get());
        UpdateProvider();
    }
}
