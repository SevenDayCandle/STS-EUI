package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import eatyourbeets.interfaces.delegates.ActionT1;
import eatyourbeets.interfaces.delegates.FuncT1;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.JavaUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.controls.GUI_Dropdown;
import extendedui.ui.controls.GUI_RelicGrid;
import extendedui.ui.controls.GUI_TextBoxInput;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;

public class RelicKeywordFilters extends GenericFilters<AbstractRelic>
{
    public enum SeenValue
    {
        Seen(EUIRM.Strings.UI_Seen, UnlockTracker::isRelicSeen),
        Unseen(EUIRM.Strings.UI_Unseen, c -> !UnlockTracker.isRelicSeen(c) && !UnlockTracker.isRelicLocked(c)),
        Locked(SingleRelicViewPopup.TEXT[8], UnlockTracker::isRelicLocked);

        public final FuncT1<Boolean, String> evalFunc;
        public final String name;

        SeenValue(String name, FuncT1<Boolean, String> evalFunc)
        {
            this.evalFunc = evalFunc;
            this.name = name;
        }

        public boolean Evaluate(String relicID)
        {
            return evalFunc.Invoke(relicID);
        }
    }

    public static CustomRelicFilterModule CustomModule;
    public final HashSet<AbstractCard.CardColor> CurrentColors = new HashSet<>();
    public final HashSet<ModInfo> CurrentOrigins = new HashSet<>();
    public final HashSet<AbstractRelic.RelicTier> CurrentRarities = new HashSet<>();
    public final HashSet<SeenValue> CurrentSeen = new HashSet<>();
    public final GUI_Dropdown<ModInfo> OriginsDropdown;
    public final GUI_Dropdown<AbstractRelic.RelicTier> RaritiesDropdown;
    public final GUI_Dropdown<AbstractCard.CardColor> ColorsDropdown;
    public final GUI_Dropdown<SeenValue> SeenDropdown;
    public final GUI_TextBoxInput NameInput;
    public String CurrentName;

    public RelicKeywordFilters()
    {
        super();

        OriginsDropdown = new GUI_Dropdown<ModInfo>(new AdvancedHitbox(0, 0, Scale(240), Scale(48)), c -> c == null ? EUIRM.Strings.UI_BaseGame : c.Name)
                .SetOnOpenOrClose(this::UpdateActive)
                .SetOnChange(costs -> this.OnFilterChanged(CurrentOrigins, costs))
                .SetLabelFunctionForButton(this::FilterNameFunction, null, false)
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.UI_Origins)
                .SetIsMultiSelect(true)
                .SetCanAutosizeButton(true)
                .SetItems(Loader.MODINFOS);

        RaritiesDropdown = new GUI_Dropdown<AbstractRelic.RelicTier>(new AdvancedHitbox(0, 0, Scale(240), Scale(48))
                , EUIGameUtils::TextForRelicTier)
                .SetOnOpenOrClose(this::UpdateActive)
                .SetOnChange(costs -> this.OnFilterChanged(CurrentRarities, costs))
                .SetLabelFunctionForButton(this::FilterNameFunction, null, false)
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, CardLibSortHeader.TEXT[0])
                .SetIsMultiSelect(true)
                .SetCanAutosizeButton(true)
                .SetItems(AbstractRelic.RelicTier.values());

        ColorsDropdown = new GUI_Dropdown<AbstractCard.CardColor>(new AdvancedHitbox(0, 0, Scale(240), Scale(48))
                , EUIGameUtils::GetColorName)
                .SetOnOpenOrClose(this::UpdateActive)
                .SetOnChange(costs -> this.OnFilterChanged(CurrentColors, costs))
                .SetLabelFunctionForButton(this::FilterNameFunction, null, false)
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.UI_Colors)
                .SetIsMultiSelect(true)
                .SetCanAutosizeButton(true);
        SeenDropdown = new GUI_Dropdown<SeenValue>(new AdvancedHitbox(0, 0, Scale(240), Scale(48))
                , item -> item.name)
                .SetOnOpenOrClose(this::UpdateActive)
                .SetOnChange(costs -> this.OnFilterChanged(CurrentSeen, costs))
                .SetLabelFunctionForButton(this::FilterNameFunction, null, false)
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, EUIRM.Strings.UI_Seen)
                .SetItems(SeenValue.values())
                .SetIsMultiSelect(true)
                .SetCanAutosizeButton(true);
        NameInput = (GUI_TextBoxInput) new GUI_TextBoxInput(EUIRM.Images.RectangularButton.Texture(),
                new AdvancedHitbox(0, 0, Scale(240), Scale(40)).SetIsPopupCompatible(true))
                .SetOnComplete(s -> {
                    CurrentName = s;
                    if (onClick != null)
                    {
                        onClick.Invoke(null);
                    }
                })
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.8f, Settings.GOLD_COLOR, LeaderboardScreen.TEXT[7])
                .SetHeaderSpacing(1f)
                .SetColors(Color.GRAY, Settings.CREAM_COLOR)
                .SetAlignment(0.5f, 0.1f)
                .SetFont(EUIFontHelper.CardTitleFont_Small, 0.8f)
                .SetBackgroundTexture(EUIRM.Images.RectangularButton.Texture());
    }

    @Override
    public boolean AreFiltersEmpty()
    {
        return (CurrentName == null || CurrentName.isEmpty())
                && CurrentColors.isEmpty() && CurrentOrigins.isEmpty() && CurrentRarities.isEmpty() && CurrentSeen.isEmpty()
                && CurrentFilters.isEmpty() && CurrentNegateFilters.isEmpty() && (CustomModule != null && CustomModule.IsEmpty());
    }

    @Override
    protected void InitializeImpl(ActionT1<FilterKeywordButton> onClick, ArrayList<AbstractRelic> cards, AbstractCard.CardColor color, boolean isAccessedFromCardPool)
    {
        CustomModule = EUI.GetCustomRelicFilter(color);

        HashSet<ModInfo> availableMods = new HashSet<>();
        HashSet<AbstractCard.CardColor> availableColors = new HashSet<>();
        HashSet<AbstractRelic.RelicTier> availableRarities = new HashSet<>();
        if (referenceItems != null)
        {
            currentTotal = GetReferenceCount();
            for (AbstractRelic relic : referenceItems)
            {
                for (EUITooltip tooltip : GetAllTooltips(relic))
                {
                    if (tooltip.canFilter) {
                        CurrentFilterCounts.merge(tooltip, 1, Integer::sum);
                    }
                }

                availableMods.add(EUIGameUtils.GetModInfo(relic));
                availableRarities.add(relic.tier);
                availableColors.add(EUIGameUtils.GetRelicColor(relic.relicId));
            }
            if (CustomModule != null)
            {
                CustomModule.InitializeSelection(referenceItems);
            }
        }

        ArrayList<ModInfo> modInfos = new ArrayList<>(availableMods);
        modInfos.sort((a, b) -> a == null ? -1 : b == null ? 1 : StringUtils.compare(a.Name, b.Name));
        OriginsDropdown.SetItems(modInfos);

        ArrayList<AbstractRelic.RelicTier> rarityItems = new ArrayList<>(availableRarities);
        rarityItems.sort((a, b) -> a == null ? -1 : b == null ? 1 : a.ordinal() - b.ordinal());
        RaritiesDropdown.SetItems(rarityItems);

        ArrayList<AbstractCard.CardColor> colorsItems = new ArrayList<>(availableColors);
        colorsItems.sort((a, b) -> a == AbstractCard.CardColor.COLORLESS ? -1 : a == AbstractCard.CardColor.CURSE ? -2 : StringUtils.compare(a.name(), b.name()));
        ColorsDropdown.SetItems(colorsItems);
    }

    @Override
    public boolean IsHoveredImpl()
    {
        return OriginsDropdown.AreAnyItemsHovered()
                || RaritiesDropdown.AreAnyItemsHovered()
                || ColorsDropdown.AreAnyItemsHovered()
                || SeenDropdown.AreAnyItemsHovered()
                || NameInput.hb.hovered
                || (CustomModule != null && CustomModule.IsHovered());
    }

    @Override
    public void ClearImpl(boolean shouldInvoke, boolean shouldClearColors)
    {
        if (shouldClearColors)
        {
            CurrentColors.clear();
        }
        CurrentOrigins.clear();
        CurrentFilters.clear();
        CurrentNegateFilters.clear();
        CurrentRarities.clear();
        CurrentName = null;
        OriginsDropdown.SetSelectionIndices(null, false);
        RaritiesDropdown.SetSelectionIndices(null, false);
        ColorsDropdown.SetSelectionIndices(null, false);
        SeenDropdown.SetSelectionIndices(null, false);
        NameInput.SetText("");
        if (CustomModule != null)
        {
            CustomModule.Reset();
        }
    }

    @Override
    public void RenderImpl(SpriteBatch sb)
    {
        OriginsDropdown.TryRender(sb);
        RaritiesDropdown.TryRender(sb);
        ColorsDropdown.TryRender(sb);
        SeenDropdown.TryRender(sb);
        NameInput.TryRender(sb);

        if (CustomModule != null)
        {
            CustomModule.TryRender(sb);
        }
    }

    @Override
    public void UpdateImpl()
    {
        OriginsDropdown.SetPosition(hb.x - SPACING * 3, DRAW_START_Y + scrollDelta).TryUpdate();
        RaritiesDropdown.SetPosition(OriginsDropdown.hb.x + OriginsDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).TryUpdate();
        ColorsDropdown.SetPosition(RaritiesDropdown.hb.x + RaritiesDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).TryUpdate();
        SeenDropdown.SetPosition(ColorsDropdown.hb.x + ColorsDropdown.hb.width + SPACING * 3, DRAW_START_Y + scrollDelta).TryUpdate();
        NameInput.SetPosition(hb.x + SPACING * 2, DRAW_START_Y + scrollDelta - SPACING * 3).TryUpdate();

        if (CustomModule != null)
        {
            CustomModule.TryUpdate();
        }
    }

    public ArrayList<EUITooltip> GetAllTooltips(AbstractRelic c)
    {
        ArrayList<EUITooltip> dynamicTooltips = new ArrayList<>();
        TooltipProvider eC = JavaUtils.SafeCast(c, TooltipProvider.class);
        if (eC != null)
        {
            eC.GenerateDynamicTooltips(dynamicTooltips);
            for (EUITooltip tip : eC.GetTipsForFilters())
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
                EUITooltip tip = EUITooltip.FindByName(sk.header);
                if (tip != null && !dynamicTooltips.contains(tip))
                {
                    dynamicTooltips.add(tip);
                }
            }
        }
        return dynamicTooltips;
    }

    public ArrayList<AbstractRelic> ApplyFilters(ArrayList<AbstractRelic> input)
    {
        return JavaUtils.Filter(input, this::EvaluateRelic);
    }

    public ArrayList<GUI_RelicGrid.RelicInfo> ApplyInfoFilters(ArrayList<GUI_RelicGrid.RelicInfo> input)
    {
        return JavaUtils.Filter(input, info -> EvaluateRelic(info.relic));
    }

    protected boolean EvaluateRelic(AbstractRelic c)
    {
        //Name check
        if (CurrentName != null && !CurrentName.isEmpty()) {
            if (c.name == null || !c.name.toLowerCase().contains(CurrentName.toLowerCase())) {
                return false;
            }
        }

        //Colors check
        if (!EvaluateItem(CurrentColors, (opt) -> opt == EUIGameUtils.GetRelicColor(c.relicId)))
        {
            return false;
        }

        //Origin check
        if (!EvaluateItem(CurrentOrigins, (opt) -> EUIGameUtils.IsObjectFromMod(c, opt)))
        {
            return false;
        }

        //Seen check
        if (!EvaluateItem(CurrentSeen, (opt) -> opt.Evaluate(c.relicId)))
        {
            return false;
        }

        //Tooltips check
        if (!CurrentFilters.isEmpty() && (!GetAllTooltips(c).containsAll(CurrentFilters)))
        {
            return false;
        }

        //Negate Tooltips check
        if (!CurrentNegateFilters.isEmpty() && (JavaUtils.Any(GetAllTooltips(c), CurrentNegateFilters::contains)))
        {
            return false;
        }

        //Rarities check
        if (!CurrentRarities.isEmpty() && !CurrentRarities.contains(c.tier))
        {
            return false;
        }

        //Module check
        if (CustomModule != null && !CustomModule.IsRelicValid(c))
        {
            return false;
        }

        return true;
    }

    public void ToggleFilters()
    {
        if (EUI.RelicFilters.isActive)
        {
            EUI.RelicFilters.Close();
        }
        else
        {
            EUI.RelicFilters.Open();
        }
    }
}
