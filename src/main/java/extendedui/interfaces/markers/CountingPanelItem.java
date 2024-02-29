package extendedui.interfaces.markers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;

import java.util.Comparator;

// T is a game object to be counted (e.g. AbstractCard)
public interface CountingPanelItem<T> extends Comparator<T> {
    default int compare(T c1, T c2) {
        return getRank(c2) - getRank(c1);
    }

    default Color getColor() {
        return Color.WHITE;
    }

    int getRank(T c);

    Texture getIcon();

    EUITooltip getTipForButton();
}
