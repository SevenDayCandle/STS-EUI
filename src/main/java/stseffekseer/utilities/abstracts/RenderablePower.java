package stseffekseer.utilities.abstracts;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.powers.AbstractPower;

public abstract class RenderablePower extends AbstractPower {
    public boolean canRenderFromCreature = true;
    public TextureAtlas.AtlasRegion powerIcon;

    public static boolean CanRenderFromCreature(AbstractPower p) {
        return !(p instanceof InvisiblePower) && (!(p instanceof RenderablePower) || ((RenderablePower) p).canRenderFromCreature);
    }
}
