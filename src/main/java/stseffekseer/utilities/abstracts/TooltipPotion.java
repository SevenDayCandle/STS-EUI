package stseffekseer.utilities.abstracts;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import stseffekseer.EUIGameUtils;
import stseffekseer.JavaUtils;
import stseffekseer.interfaces.markers.TooltipProvider;
import stseffekseer.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public abstract class TooltipPotion extends AbstractPotion implements TooltipProvider {

    public final ArrayList<EUITooltip> pclTips = new ArrayList<>();
    public final String[] DESCRIPTIONS;

    // We deliberately avoid using initializeData because we need to load the PotionStrings after the super call
    public TooltipPotion(String id, PotionRarity rarity, PotionSize size, PotionEffect effect, Color liquidColor, Color hybridColor, Color spotsColor, AbstractPlayer.PlayerClass playerClass) {
        super("", id, rarity, size, effect, liquidColor.cpy(), hybridColor.cpy(), spotsColor.cpy());
        PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(id);
        name = potionStrings.NAME;
        DESCRIPTIONS = potionStrings.DESCRIPTIONS;
        this.potency = this.getPotency();
        this.description = JavaUtils.Format(DESCRIPTIONS[0], this.potency);
        this.isThrown = false;
        initializeTips(playerClass);
    }

    protected void initializeTips(AbstractPlayer.PlayerClass playerClass) {
        pclTips.clear();
        pclTips.add(new EUITooltip(name, description, playerClass));
        EUIGameUtils.ScanForTips(description, pclTips);
    }

    @Override
    public List<EUITooltip> GetTips() {
        return pclTips;
    }
}
