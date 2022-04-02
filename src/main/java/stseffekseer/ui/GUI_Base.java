package stseffekseer.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import stseffekseer.EUIGameUtils;

public abstract class GUI_Base
{
    public static final Color HOVER_BLEND_COLOR = new Color(1f, 1f, 1f, 0.3f);
    public static final Color TEXT_DISABLED_COLOR = new Color(0.6f, 0.6f, 0.6f, 1f);

    public boolean isActive = true;

    public abstract void Update();
    public abstract void Render(SpriteBatch sb);

    public boolean TryRender(SpriteBatch sb)
    {
        if (isActive)
        {
            Render(sb);
        }

        return isActive;
    }

    public boolean TryUpdate()
    {
        if (isActive)
        {
            Update();
        }

        return isActive;
    }

    public GUI_Base SetActive(boolean active)
    {
        this.isActive = active;

        return this;
    }

    public static float Scale(float value)
    {
        return EUIGameUtils.Scale(value);
    }

    public static float ScreenW(float value)
    {
        return EUIGameUtils.ScreenW(value);
    }

    public static float ScreenH(float value)
    {
        return EUIGameUtils.ScreenH(value);
    }
}
