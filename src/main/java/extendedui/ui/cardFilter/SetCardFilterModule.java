package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.CustomCardFilterModule;
import extendedui.ui.EUIBase;
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

public class SetCardFilterModule extends EUIBase implements CustomCardFilterModule {
    private final EUISearchableDropdown<String> seriesDropdown;
    private FuncT1<String, AbstractCard> nameFunc;
    public HashSet<String> currentSeries = new HashSet<>();

    public SetCardFilterModule(FuncT1<String, AbstractCard> nameFunc) {
        this.nameFunc = nameFunc;
        seriesDropdown = (EUISearchableDropdown<String>) new EUISearchableDropdown<String>(new EUIHitbox(0, 0, scale(240), scale(48)), item -> StringUtils.isEmpty(item) ? EUIRM.strings.ui_na : item)
                .setOnOpenOrClose(isOpen -> {
                    CardCrawlGame.isPopupOpen = this.isActive;
                })
                .setOnChange(selectedSeries -> {
                    currentSeries.clear();
                    currentSeries.addAll(selectedSeries);
                    EUI.cardFilters.invoke(null);
                })
                .setHeader(EUIFontHelper.cardTitleFontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.ui_set)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
    }

    @Override
    public void initializeSelection(Collection<? extends AbstractCard> cards) {
        HashSet<String> availableSeries = new HashSet<>();
        for (AbstractCard card : cards) {
            availableSeries.add(nameFunc.invoke(card));
        }
        ArrayList<String> seriesItems = EUIUtils.filter(availableSeries, Objects::nonNull);
        seriesItems.sort(StringUtils::compare);
        seriesDropdown.setItems(seriesItems).setActive(seriesItems.size() > 1);
    }

    @Override
    public boolean isEmpty() {
        return currentSeries.isEmpty();
    }

    @Override
    public boolean isHovered() {
        return seriesDropdown.hb.hovered;
    }

    @Override
    public boolean isItemValid(AbstractCard c) {
        return currentSeries.isEmpty() || currentSeries.contains(nameFunc.invoke(c));
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        this.seriesDropdown.tryRender(sb);
    }

    @Override
    public void reset() {
        currentSeries.clear();
        seriesDropdown.setSelectionIndices((int[]) null, false);
    }

    public SetCardFilterModule setNameFunc(FuncT1<String, AbstractCard> nameFunc) {
        this.nameFunc = nameFunc;
        return this;
    }

    @Override
    public void updateImpl() {
        this.seriesDropdown.setPosition(EUI.cardFilters.seenDropdown.hb.x + EUI.cardFilters.seenDropdown.hb.width + SPACING * 2, DRAW_START_Y + EUI.cardFilters.getScrollDelta()).tryUpdate();
    }
}
