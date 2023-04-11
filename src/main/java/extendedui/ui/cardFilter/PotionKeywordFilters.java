package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.CustomPotionFilterModule;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUIPotionGrid;
import extendedui.ui.controls.EUITextBoxInput;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;

public class PotionKeywordFilters extends GenericFilters<AbstractPotion>
{
    public static CustomPotionFilterModule customModule;
    public final HashSet<AbstractCard.CardColor> currentColors = new HashSet<>();
    public final HashSet<ModInfo> currentOrigins = new HashSet<>();
    public final HashSet<AbstractPotion.PotionRarity> currentRarities = new HashSet<>();
    public final EUIDropdown<ModInfo> originsDropdown;
    public final EUIDropdown<AbstractPotion.PotionRarity> raritiesDropdown;
    public final EUIDropdown<AbstractCard.CardColor> colorsDropdown;
    public final EUITextBoxInput nameInput;
    public String currentName;

    public PotionKeywordFilters()
    {
        super();

        originsDropdown = new EUIDropdown<ModInfo>(new EUIHitbox(0, 0, scale(240), scale(48)), c -> c == null ? EUIRM.strings.uiBasegame : c.Name)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentOrigins, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.uiOrigins)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(Loader.MODINFOS);

        raritiesDropdown = new EUIDropdown<AbstractPotion.PotionRarity>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::textForPotionRarity)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentRarities, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true)
                .setItems(AbstractPotion.PotionRarity.values());

        colorsDropdown = new EUIDropdown<AbstractCard.CardColor>(new EUIHitbox(0, 0, scale(240), scale(48))
                , EUIGameUtils::getColorName)
                .setOnOpenOrClose(this::updateActive)
                .setOnChange(costs -> this.onFilterChanged(currentColors, costs))
                .setLabelFunctionForButton(this::filterNameFunction, false)
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, EUIRM.strings.uiColors)
                .setIsMultiSelect(true)
                .setCanAutosizeButton(true);
        nameInput = (EUITextBoxInput) new EUITextBoxInput(EUIRM.images.rectangularButton.texture(),
                new EUIHitbox(0, 0, scale(240), scale(40)).setIsPopupCompatible(true))
                .setOnComplete(s -> {
                    currentName = s;
                    if (onClick != null)
                    {
                        onClick.invoke(null);
                    }
                })
                .setHeader(EUIFontHelper.cardtitlefontSmall, 0.8f, Settings.GOLD_COLOR, LeaderboardScreen.TEXT[7])
                .setHeaderSpacing(1f)
                .setColors(Color.GRAY, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(EUIFontHelper.cardtitlefontSmall, 0.8f)
                .setBackgroundTexture(EUIRM.images.rectangularButton.texture());
    }

    @Override
    public boolean areFiltersEmpty()
    {
        return (currentName == null || currentName.isEmpty())
                && currentColors.isEmpty() && currentOrigins.isEmpty() && currentRarities.isEmpty()
                && currentFilters.isEmpty() && currentNegateFilters.isEmpty() && (customModule != null && customModule.isEmpty());
    }

    @Override
    protected void initializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<AbstractPotion> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool)
    {
        customModule = EUI.getCustomPotionFilter(color);

        HashSet<ModInfo> availableMods = new HashSet<>();
        HashSet<AbstractCard.CardColor> availableColors = new HashSet<>();
        HashSet<AbstractPotion.PotionRarity> availableRarities = new HashSet<>();
        if (referenceItems != null)
        {
            currentTotal = getReferenceCount();
            for (AbstractPotion potion : referenceItems)
            {
                for (EUITooltip tooltip : getAllTooltips(potion))
                {
                    if (tooltip.canFilter) {
                        currentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.getModInfo(potion));
                availableRarities.add(potion.rarity);
                availableColors.add(EUIGameUtils.getPotionColor(potion.ID));
            }
            if (customModule != null)
            {
                customModule.initializeSelection(referenceItems);
            }
        }

        ArrayList<ModInfo> modInfos = new ArrayList<>(availableMods);
        modInfos.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.Name, b.Name));
        originsDropdown.setItems(modInfos);

        ArrayList<AbstractPotion.PotionRarity> rarityItems = new ArrayList<>(availableRarities);
        rarityItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : a.ordinal() - b.ordinal());
        raritiesDropdown.setItems(rarityItems);

        ArrayList<AbstractCard.CardColor> colorsItems = new ArrayList<>(availableColors);
        colorsItems.sort((a, b) -> a == AbstractCard.CardColor.COLORLESS ? -1 : a == AbstractCard.CardColor.CURSE ? -2 : StringUtils.compare(a.name(), b.name()));
        colorsDropdown.setItems(colorsItems);
    }

    @Override
    public boolean isHoveredImpl()
    {
        return originsDropdown.areAnyItemsHovered()
                || raritiesDropdown.areAnyItemsHovered()
                || colorsDropdown.areAnyItemsHovered()
                || nameInput.hb.hovered
                || (customModule != null && customModule.isHovered());
    }

    @Override
    public void clearFilters(boolean shouldInvoke, boolean shouldClearColors)
    {
        if (shouldClearColors)
        {
            currentColors.clear();
        }
        currentOrigins.clear();
        currentFilters.clear();
        currentNegateFilters.clear();
        currentRarities.clear();
        currentName = null;
        originsDropdown.setSelectionIndices((int[]) null, false);
        raritiesDropdown.setSelectionIndices((int[]) null, false);
        colorsDropdown.setSelectionIndices((int[]) null, false);
        nameInput.setLabel("");
        if (customModule != null)
        {
            customModule.reset();
        }
    }

    @Override
    public void renderFilters(SpriteBatch sb)
    {
        originsDropdown.tryRender(sb);
        raritiesDropdown.tryRender(sb);
        colorsDropdown.tryRender(sb);
        nameInput.tryRender(sb);

        if (customModule != null)
        {
            customModule.render(sb);
        }
    }

    @Override
    public void updateFilters()
    {
        originsDropdown.setPosition(hb.x - SPACING * 3, DRAW_START_Y + scrollDelta).tryUpdate();
        raritiesDropdown.setPosition(originsDropdown.hb.x + originsDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).tryUpdate();
        colorsDropdown.setPosition(raritiesDropdown.hb.x + raritiesDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).tryUpdate();
        nameInput.setPosition(hb.x + SPACING * 2, DRAW_START_Y + scrollDelta - SPACING * 3).tryUpdate();

        if (customModule != null)
        {
            customModule.update();
        }
    }

    public ArrayList<EUITooltip> getAllTooltips(AbstractPotion c)
    {
        ArrayList<EUITooltip> dynamicTooltips = new ArrayList<>();
        TooltipProvider eC = EUIUtils.safeCast(c, TooltipProvider.class);
        if (eC != null)
        {
            eC.generateDynamicTooltips(dynamicTooltips);
            for (EUITooltip tip : eC.getTipsForFilters())
            {
                if (!dynamicTooltips.contains(tip))
                {
                    dynamicTooltips.add(tip);
                }
            }
        }
        else
        {
            for (PowerTip sk : c.tips)
            {
                EUITooltip tip = EUITooltip.findByName(StringUtils.lowerCase(sk.header));
                if (tip != null && !dynamicTooltips.contains(tip))
                {
                    dynamicTooltips.add(tip);
                }
            }
        }
        return dynamicTooltips;
    }

    public ArrayList<AbstractPotion> applyFilters(ArrayList<AbstractPotion> input)
    {
        return EUIUtils.filter(input, this::evaluatePotion);
    }

    public ArrayList<EUIPotionGrid.PotionInfo> applyInfoFilters(ArrayList<EUIPotionGrid.PotionInfo> input)
    {
        return EUIUtils.filter(input, info -> evaluatePotion(info.potion));
    }

    protected boolean evaluatePotion(AbstractPotion c)
    {
        //Name check
        if (currentName != null && !currentName.isEmpty()) {
            if (c.name == null || !c.name.toLowerCase().contains(currentName.toLowerCase())) {
                return false;
            }
        }

        //Colors check
        if (!evaluateItem(currentColors, (opt) -> opt == EUIGameUtils.getPotionColor(c.ID)))
        {
            return false;
        }

        //Origin check
        if (!evaluateItem(currentOrigins, (opt) -> EUIGameUtils.isObjectFromMod(c, opt)))
        {
            return false;
        }

        //Tooltips check
        if (!currentFilters.isEmpty() && (!getAllTooltips(c).containsAll(currentFilters)))
        {
            return false;
        }

        //Negate Tooltips check
        if (!currentNegateFilters.isEmpty() && (EUIUtils.any(getAllTooltips(c), currentNegateFilters::contains)))
        {
            return false;
        }

        //Rarities check
        if (!currentRarities.isEmpty() && !currentRarities.contains(c.rarity))
        {
            return false;
        }

        //Module check
        return customModule == null || customModule.isPotionValid(c);
    }

    public void toggleFilters()
    {
        if (EUI.potionFilters.isActive)
        {
            EUI.potionFilters.close();
        }
        else
        {
            EUI.potionFilters.open();
        }
    }
}
