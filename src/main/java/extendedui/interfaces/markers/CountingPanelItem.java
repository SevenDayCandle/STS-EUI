package extendedui.interfaces.markers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.Comparator;

public interface CountingPanelItem extends Comparator<AbstractCard> {
    default int compare(AbstractCard c1, AbstractCard c2) {
        return getRank(c2) - getRank(c1);
    }

    int getRank(AbstractCard c);

    default Color getColor() {
        return Color.WHITE;
    }

    Texture getIcon();
}
