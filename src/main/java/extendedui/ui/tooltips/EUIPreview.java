package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.configuration.EUIHotkeys;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.utilities.RotatingList;

public abstract class EUIPreview {

    public abstract boolean matches(String preview);

    public abstract void render(SpriteBatch sb, float x, float y, float scale, boolean upgraded);
}
