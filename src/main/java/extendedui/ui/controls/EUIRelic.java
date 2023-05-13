package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.ColoredTexture;

public class EUIRelic extends EUIImage {
    private final AbstractRelic relic;
    public ColoredTexture foreground;

    public EUIRelic(AbstractRelic relic, EUIHitbox hb) {
        super(relic.img, hb, Color.WHITE);

        this.relic = relic;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);

        if (hb.hovered) {
            relic.renderTip(sb);
        }
    }

    public EUIImage setForegroundTexture(Texture texture) {
        setForegroundTexture(texture, null, 1);

        return this;
    }

    public EUIImage setForegroundTexture(Texture texture, Color color, float scale) {
        this.foreground = new ColoredTexture(texture);
        this.foreground.scale = scale;
        this.foreground.setColor(color);

        return this;
    }
}
