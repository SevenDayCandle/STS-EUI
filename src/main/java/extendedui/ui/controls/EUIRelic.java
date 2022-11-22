package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.hitboxes.AdvancedHitbox;

public class EUIRelic extends EUIImage
{
    private final AbstractRelic relic;

    public EUIRelic(AbstractRelic relic, AdvancedHitbox hb)
    {
        super(relic.img, hb, Color.WHITE);

        this.relic = relic;
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);

        if (hb.hovered)
        {
            relic.renderTip(sb);
        }
    }
}
