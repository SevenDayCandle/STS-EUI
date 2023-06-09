package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButton;
import com.megacrit.cardcrawl.screens.mainMenu.SortHeaderButtonListener;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import extendedui.utilities.RelicGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class RelicSortHeader extends EUIBase implements SortHeaderButtonListener {
    public static final float START_X = screenW(0.5f) - CardLibSortHeader.SPACE_X * 1.45f;
    public static RelicSortHeader instance;
    public SortHeaderButton[] buttons;
    public RelicGroup relicGroup;
    public ArrayList<RelicGroup.RelicInfo> originalGroup;
    protected boolean isAscending;
    protected boolean snapToGroup;
    protected float baseY = Settings.HEIGHT * 0.85f;
    protected SortHeaderButton rarityButton;
    protected SortHeaderButton nameButton;
    protected SortHeaderButton colorButton;
    protected SortHeaderButton seenButton;
    private SortHeaderButton lastUsedButton;

    public RelicSortHeader(RelicGroup relicGroup) {
        this.relicGroup = relicGroup;
        instance = this;
        float xPosition = START_X;
        this.rarityButton = new SortHeaderButton(CardLibSortHeader.TEXT[0], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.nameButton = new SortHeaderButton(CardLibSortHeader.TEXT[2], xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.colorButton = new SortHeaderButton(EUIRM.strings.uiColors, xPosition, 0.0F, this);
        xPosition += CardLibSortHeader.SPACE_X;
        this.seenButton = new SortHeaderButton(EUIRM.strings.uiSeen, xPosition, 0.0F, this);
        this.buttons = new SortHeaderButton[]{this.rarityButton, this.nameButton, this.colorButton, this.seenButton};
    }

    public ArrayList<AbstractRelic> getOriginalRelics() {
        return EUIUtils.map(originalGroup, r -> r.relic);
    }

    public ArrayList<AbstractRelic> getRelics() {
        return EUIUtils.map(relicGroup, r -> r.relic);
    }

    public RelicSortHeader setBaseY(float value) {
        this.baseY = value;
        return this;
    }

    public RelicSortHeader setGroup(RelicGroup relicGroup) {
        EUI.relicFilters.clear(false, true);
        this.relicGroup = relicGroup;
        this.originalGroup = new ArrayList<>(relicGroup.group);

        if (EUI.relicFilters.customModule != null) {
            EUI.relicFilters.customModule.processGroup(relicGroup);
        }
        for (SortHeaderButton button : buttons) {
            button.reset();
        }

        return this;
    }

    public RelicSortHeader snapToGroup(boolean value) {
        this.snapToGroup = value;
        return this;
    }

    public void updateForFilters() {
        if (this.relicGroup != null) {
            if (EUI.relicFilters.areFiltersEmpty()) {
                this.relicGroup.group = originalGroup;
            }
            else {
                this.relicGroup.group = EUI.relicFilters.applyInfoFilters(originalGroup);
            }
            didChangeOrder(lastUsedButton, isAscending);
            EUI.relicFilters.refresh(EUIUtils.map(relicGroup, group -> group.relic));
        }
    }

    @Override
    public void didChangeOrder(SortHeaderButton button, boolean isAscending) {
        if (relicGroup != null) {
            if (button == this.rarityButton) {
                this.relicGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.relic.tier.ordinal() - b.relic.tier.ordinal()) * (isAscending ? 1 : -1));
            }
            else if (button == this.nameButton) {
                this.relicGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : StringUtils.compare(a.relic.name, b.relic.name)) * (isAscending ? 1 : -1));
            }
            else if (button == this.colorButton) {
                this.relicGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.relicColor.ordinal() - b.relicColor.ordinal()) * (isAscending ? 1 : -1));
            }
            else if (button == this.seenButton) {
                this.relicGroup.sort((a, b) -> sortBySeen(a, b) * (isAscending ? 1 : -1));
            }
            else {
                this.relicGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.relicColor.ordinal() - b.relicColor.ordinal()) * (isAscending ? 1 : -1));
                this.relicGroup.sort((a, b) -> (a == null ? -1 : b == null ? 1 : a.relic.tier.ordinal() - b.relic.tier.ordinal()) * (isAscending ? 1 : -1));
                this.relicGroup.sort((a, b) -> sortBySeen(a, b) * (isAscending ? 1 : -1));
            }
        }
        for (SortHeaderButton eB : buttons) {
            eB.setActive(eB == button);
        }
    }

    protected int sortBySeen(RelicGroup.RelicInfo a, RelicGroup.RelicInfo b) {
        int aValue = a == null || a.locked ? 2 : a.relic.isSeen ? 1 : 0;
        int bValue = b == null || b.locked ? 2 : b.relic.isSeen ? 1 : 0;
        return aValue - bValue;
    }

    @Override
    public void updateImpl() {
        float scrolledY = snapToGroup && this.relicGroup != null && this.relicGroup.size() > 0 ? this.relicGroup.group.get(0).relic.hb.y + 230.0F * Settings.yScale : baseY;
        for (SortHeaderButton button : buttons) {
            button.update();
            button.updateScrollPosition(scrolledY);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        for (SortHeaderButton button : buttons) {
            button.render(sb);
        }
    }
}
