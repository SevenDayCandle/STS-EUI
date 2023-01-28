package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.controls.EUISearchableDropdown;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static extendedui.ui.cardFilter.GenericFilters.DRAW_START_Y;
import static extendedui.ui.cardFilter.GenericFilters.SPACING;

public class SimpleCardFilterModule<T> extends CustomCardFilterModule
{
    public HashSet<T> currentSeries = new HashSet<>();
    public EUISearchableDropdown<T> seriesDropdown;
    protected FuncT1<String, T> nameFunc;
    protected FuncT1<T, AbstractCard> objectFunc;

    public SimpleCardFilterModule(CardKeywordFilters filters, String title, FuncT1<String, T> nameFunc, FuncT1<T, AbstractCard> objectFunc)
    {
        super(filters);
        this.nameFunc = nameFunc;
        this.objectFunc = objectFunc;
        seriesDropdown = (EUISearchableDropdown<T>) new EUISearchableDropdown<T>(new EUIHitbox(0, 0, scale(240), scale(48)), nameFunc)
                .setOnOpenOrClose(isOpen -> {
                    CardCrawlGame.isPopupOpen = this.isActive;
                })
                .setOnChange(selectedSeries -> {
                    currentSeries.clear();
                    currentSeries.addAll(selectedSeries);
                    filters.invoke(null);
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, title)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
    }

    @Override
    public boolean isCardValid(AbstractCard c)
    {
        return currentSeries.contains(objectFunc.invoke(c));
    }

    @Override
    public boolean isEmpty()
    {
        return currentSeries.isEmpty();
    }

    @Override
    public boolean isHovered()
    {
        return seriesDropdown.hb.hovered;
    }

    @Override
    public void initializeSelection(Collection<AbstractCard> cards)
    {
        HashSet<T> availableSeries = new HashSet<>();
        for (AbstractCard card : cards)
        {
            availableSeries.add(objectFunc.invoke(card));
        }
        ArrayList<T> seriesItems = EUIUtils.filter(availableSeries, Objects::nonNull);
        seriesItems.sort((a, b) -> StringUtils.compare(nameFunc.invoke(a), nameFunc.invoke(b)));
        seriesDropdown.setItems(seriesItems).setActive(seriesItems.size() > 0);
    }

    @Override
    public void reset()
    {
        currentSeries.clear();
        seriesDropdown.setSelectionIndices((int[]) null, false);
    }

    @Override
    public void updateImpl()
    {
        this.seriesDropdown.setPosition(filters.typesDropdown.hb.x + filters.typesDropdown.hb.width + SPACING * 2, DRAW_START_Y + filters.getScrollDelta()).tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        this.seriesDropdown.tryRender(sb);
    }
}
