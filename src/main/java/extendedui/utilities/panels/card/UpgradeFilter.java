package extendedui.utilities.panels.card;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public enum UpgradeFilter implements CountingPanelItem<AbstractCard> {
    Upgrade0,
    Upgrade1,
    Upgrade2;

    private static EUIKeywordTooltip UPGRADE_TIP;

    public static UpgradeFilter get(int upgradeLevel) {
        switch (upgradeLevel) {
            case 0:
                return Upgrade0;
            case 1:
                return Upgrade1;
        }
        return Upgrade2;
    }

    public static ArrayList<String> getUpgradeRangeStrings(List<UpgradeFilter> costs) {
        ArrayList<String> ranges = new ArrayList<>();
        if (costs == null) {
            return ranges;
        }
        boolean[] range = new boolean[7];
        for (UpgradeFilter cost : costs) {
            if (cost != null) {
                if (cost.getValue() < 0) {
                    ranges.add(cost.toString());
                }
                else {
                    range[cost.getValue()] = true;
                }
            }
        }
        Integer lo = null;
        Integer hi = null;
        for (int i = 0; i < range.length; i++) {
            if (range[i]) {
                if (lo == null) {
                    lo = i;
                }
                hi = i;
            }
            else if (hi != null) {
                ranges.add(i == range.length - 1 ? lo + "+" :
                        !lo.equals(hi) ? lo + "-" + hi : String.valueOf(lo));
                lo = hi = null;
            }
        }
        return ranges;
    }

    public static EUIKeywordTooltip getUpgradeTip() {
        if (UPGRADE_TIP == null) {
            UPGRADE_TIP = EUIKeywordTooltip.findByID("Upgrade");
            if (UPGRADE_TIP == null) {
                UPGRADE_TIP = new EUIKeywordTooltip("Upgrade");
            }
        }
        return UPGRADE_TIP;
    }

    public boolean check(AbstractCard c) {
        if (this == UpgradeFilter.Upgrade2) {
            return c.timesUpgraded >= 2;
        }
        return (c.cost == getValue());
    }

    @Override
    public Texture getIcon() {
        return getValue() > 0 ? EUIRM.images.typeUpgrade.texture() : EUIRM.images.typeDowngrade.texture();
    }

    @Override
    public EUITooltip getTipForButton() {
        return new EUITooltip(toString(), EUIRM.strings.misc_countPanelItem);
    }

    @Override
    public int getRank(AbstractCard c) {
        return c.timesUpgraded >= getValue() ? c.timesUpgraded + 1000 : c.timesUpgraded;
    }

    public int getValue() {
        switch (this) {
            case Upgrade0:
                return 0;
            case Upgrade1:
                return 1;
            case Upgrade2:
                return 2;
        }
        return -1;
    }

    @Override
    public String toString() {
        if (this == UpgradeFilter.Upgrade2) {
            return EUIRM.strings.numNoun(getValue() + "+", getUpgradeTip().title);
        }
        return EUIRM.strings.numNoun(getValue(), getUpgradeTip().title);
    }
}
