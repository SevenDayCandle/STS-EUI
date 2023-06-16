package extendedui.ui.cardFilter.panels;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;

import java.util.Collections;
import java.util.List;

public class CardUpgradePanelFilterItem implements CountingPanelItem, TooltipProvider {
    protected static EUIKeywordTooltip UPGRADE_TIP;
    protected static final CardUpgradePanelFilterItem UPGRADE = new CardUpgradePanelFilterItem(1);
    protected static final CardUpgradePanelFilterItem DOWNGRADE = new CardUpgradePanelFilterItem(0);

    public final int upgradeLevel;

    public CardUpgradePanelFilterItem(int upgradeLevel) {
        this.upgradeLevel = upgradeLevel;
    }

    public static CardUpgradePanelFilterItem get(int upgradeLevel) {
        return upgradeLevel > 0 ? UPGRADE : DOWNGRADE;
    }

    @Override
    public int getRank(AbstractCard c) {
        return c.timesUpgraded >= upgradeLevel ? c.timesUpgraded + 1000 : c.timesUpgraded;
    }

    @Override
    public Texture getIcon() {
        return upgradeLevel > 0 ? EUIRM.images.typeUpgrade.texture() : EUIRM.images.typeDowngrade.texture();
    }

    @Override
    public List<EUITooltip> getTips() {
        return Collections.singletonList(new EUITooltip(getUpgradeName()));
    }

    protected String getUpgradeName() {
        if (UPGRADE_TIP == null) {
            UPGRADE_TIP = EUIKeywordTooltip.findByID("Upgrade");
        }
        return UPGRADE_TIP != null ? EUIRM.strings.numNoun("+" + upgradeLevel, UPGRADE_TIP.title) : String.valueOf(upgradeLevel);
    }
}
