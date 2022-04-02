package stseffekseer.interfaces.markers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import stseffekseer.JavaUtils;

public interface CustomFilter {
    public String GetTitle();
    public default boolean CardHas(AbstractCard card) {
        return GetFromCard(card) == this;
    }

    public static CustomFilter GetFromCard(AbstractCard card) {
        CustomFilterProvider provider = JavaUtils.SafeCast(card, CustomFilterProvider.class);
        if (provider != null) {
            return provider.GetCustomFilter();
        }
        return null;
    }

    public static String TitleOf(CustomFilter filter) {
        if (filter != null) {
            String name = filter.GetTitle();
            if (name != null) {
                return name;
            }
        }
        return "";
    }
}
