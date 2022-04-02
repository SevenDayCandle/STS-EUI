package stseffekseer.utilities.abstracts;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import stseffekseer.EUIGameUtils;
import stseffekseer.interfaces.markers.TooltipProvider;
import stseffekseer.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public abstract class TooltipRelic extends CustomRelic implements TooltipProvider {

    public ArrayList<EUITooltip> tips;
    public EUITooltip mainTooltip;
    public AbstractPlayer.PlayerClass playerClass;

    public TooltipRelic(String id, Texture texture, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass playerClass) {
        super(id, texture, tier, sfx);
        this.playerClass = playerClass;
    }

    public TooltipRelic(String id, Texture texture, Texture outline, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass playerClass) {
        super(id, texture, outline, tier, sfx);
        this.playerClass = playerClass;
    }

    public TooltipRelic(String id, String imgName, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass playerClass) {
        super(id, imgName, tier, sfx);
        this.playerClass = playerClass;
    }

    @Override
    public final void updateDescription(AbstractPlayer.PlayerClass c)
    {
        this.description = getUpdatedDescription();
        this.mainTooltip.description = description;
    }

    @Override
    protected void initializeTips()
    {
        if (tips == null)
        {
            tips = new ArrayList<>();
        }
        else
        {
            tips.clear();
        }

        mainTooltip = new EUITooltip(name, description, this.playerClass);
        tips.add(mainTooltip);
        EUIGameUtils.ScanForTips(description, tips);
    }

    @Override
    public void renderBossTip(SpriteBatch sb)
    {
        EUITooltip.QueueTooltips(tips, Settings.WIDTH * 0.63F, Settings.HEIGHT * 0.63F);
    }

    @Override
    public void renderTip(SpriteBatch sb)
    {
        EUITooltip.QueueTooltips(this);
    }

    @Override
    public List<EUITooltip> GetTips() {
        return tips;
    }
}
