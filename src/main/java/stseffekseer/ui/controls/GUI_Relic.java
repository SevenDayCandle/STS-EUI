package stseffekseer.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import stseffekseer.ui.hitboxes.AdvancedHitbox;

public class GUI_Relic extends GUI_Image
{
    private AbstractRelic relic;

    public GUI_Relic(AbstractRelic relic, AdvancedHitbox hb)
    {
        super(relic.img, hb, Color.WHITE);

        this.relic = relic;
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        super.Render(sb);

        if (hb.hovered)
        {
            relic.renderTip(sb);
        }
    }
}
