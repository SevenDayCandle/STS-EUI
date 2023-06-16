package extendedui.interfaces.markers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.Comparator;

public interface CountingPanelItem extends Comparator<AbstractCard> {
    default int compare(AbstractCard c1, AbstractCard c2) {
        return getRank(c2) - getRank(c1);
    }
    default Color getColor() {
        return Color.WHITE;
    }
    int getRank(AbstractCard c);
    Texture getIcon();
}
