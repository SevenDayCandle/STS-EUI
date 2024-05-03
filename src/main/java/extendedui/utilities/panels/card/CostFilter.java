package extendedui.utilities.panels.card;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.EUIRM;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public enum CostFilter implements CountingPanelItem<AbstractCard> {
    CostX,
    Cost0,
    Cost1,
    Cost2,
    Cost3,
    Cost4,
    Cost5,
    Unplayable;

    public static CostFilter get(int upgradeLevel) {
        switch (upgradeLevel) {
            case 0:
                return Cost0;
            case 1:
                return Cost1;
            case 2:
                return Cost2;
            case 3:
                return Cost3;
            case 4:
                return Cost4;
            case -1:
                return CostX;
            case -2:
                return Unplayable;
        }
        return Cost5;
    }

    public static ArrayList<String> getCostRangeStrings(List<CostFilter> costs) {
        ArrayList<String> ranges = new ArrayList<>();
        if (costs == null) {
            return ranges;
        }
        boolean[] range = new boolean[7];
        for (CostFilter cost : costs) {
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

    public boolean check(AbstractCard c) {
        switch (this) {
            case Unplayable:
                return c.cost <= -2;
            case Cost5:
                return c.cost >= 5;
        }
        return (c.cost == getValue());
    }

    @Override
    public Texture getIcon() {
        switch (this) {
            case Cost0:
                return EUIRM.images.cost0.texture();
            case Cost1:
                return EUIRM.images.cost1.texture();
            case Cost2:
                return EUIRM.images.cost2.texture();
            case Cost3:
                return EUIRM.images.cost3.texture();
            case Cost4:
                return EUIRM.images.cost4.texture();
            case Cost5:
                return EUIRM.images.cost5.texture();
            case CostX:
                return EUIRM.images.costX.texture();
        }
        return EUIRM.images.costU.texture();
    }

    @Override
    public EUITooltip getTipForButton() {
        if (this == Unplayable) {
            return new EUITooltip(toString());
        }
        return new EUITooltip(EUIRM.strings.numNoun(toString(), CardLibSortHeader.TEXT[3]));
    }

    @Override
    public int getRank(AbstractCard c) {
        return c.cost >= getValue() ? c.timesUpgraded + 1000 : c.timesUpgraded;
    }

    public int getValue() {
        switch (this) {
            case Cost0:
                return 0;
            case Cost1:
                return 1;
            case Cost2:
                return 2;
            case Cost3:
                return 3;
            case Cost4:
                return 4;
            case Cost5:
                return 5;
            case CostX:
                return -1;
        }
        return -2;
    }

    @Override
    public String toString() {
        switch (this) {
            case CostX:
                return "X";
            case Cost5:
                return "5+";
            case Unplayable:
                return EUIRM.strings.ui_na;
        }
        return String.valueOf(getValue());
    }
}
