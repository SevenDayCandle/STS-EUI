package extendedui;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import com.evacipated.cardcrawl.mod.stslib.icons.CustomIconHelper;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.Keyword;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.patches.EUIKeyword;
import extendedui.ui.AbstractScreen;
import extendedui.ui.GUI_Base;
import extendedui.ui.cardFilter.*;
import extendedui.ui.controls.GUI_Button;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EUI
{
    private static final String[] ENERGY_STRINGS = {"[E]", "[R]", "[G]", "[B]", "[W]"};

    public static final ArrayList<GUI_Base> BattleSubscribers = new ArrayList<>();
    public static final ArrayList<GUI_Base> Subscribers = new ArrayList<>();
    private static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> preRenderList = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> postRenderList = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> priorityPostRenderList = new ConcurrentLinkedQueue<>();
    private static final HashMap<AbstractCard.CardColor, CustomCardFilterModule> customFilters = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomCardPoolModule> customLibraryModules = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomCardPoolModule> customPoolModules = new HashMap<>();
    private static float delta = 0;
    private static float timer = 0;
    private static boolean isDragging;
    private static Hitbox lastHovered;
    private static Hitbox lastHoveredTemp;
    protected static GUI_Base activeElement;
    public static AbstractCard.CardColor ActingColor;
    public static AbstractScreen CurrentScreen;
    public static CardKeywordFilters CardFilters;
    public static CardPoolScreen CardsScreen;
    public static CustomCardLibSortHeader CustomHeader;
    public static CustomCardLibraryScreen CustomLibraryScreen;
    public static GUI_Button OpenCardFiltersButton;

    public static boolean IsLoaded() {
        return CardsScreen != null; // This will be null before the UI has loaded
    }

    public static void Initialize()
    {
        CardsScreen = new CardPoolScreen();
        CardFilters = new CardKeywordFilters();
        CustomHeader = new CustomCardLibSortHeader(null);
        CustomLibraryScreen = new CustomCardLibraryScreen();
        BaseMod.addTopPanelItem(new CardPoolPanelItem());

        OpenCardFiltersButton = new GUI_Button(EUIRM.Images.HexagonalButton.Texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).SetIsPopupCompatible(true))
            .SetBorder(EUIRM.Images.HexagonalButtonBorder.Texture(), Color.WHITE)
            .SetPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.05f).SetText(EUIRM.Strings.UI_Filters)
            .SetOnClick(CardKeywordFilters::ToggleFilters)
            .SetColor(Color.GRAY);
    }

    /* Add grammar rules to existing tooltips */
    public static void RegisterGrammar(Map<String, EUIKeyword> keywords) {
        for (Map.Entry<String, EUIKeyword> entry : keywords.entrySet()) {
            EUIKeyword grammar = entry.getValue();
            EUITooltip existing = EUITooltip.FindByID(entry.getKey());
            if (existing != null) {
                existing.past = grammar.PAST;
                existing.plural = grammar.PLURAL;
                existing.present = grammar.PRESENT;
            }
        }
    }

    // Add CommonKeywordIcon pictures to keywords
    public static void RegisterKeywordIcons() {
        for (EUITooltip tooltip : JavaUtils.Map(EUITooltip.GetEntries(), Map.Entry::getValue)) {
            String title = tooltip.title;
            // Add CommonKeywordIcon pictures to keywords
            if (title.equals(GameDictionary.INNATE.NAMES[0])) {
                tooltip.SetIcon(StSLib.BADGE_INNATE);
            }
            else if (title.equals(GameDictionary.ETHEREAL.NAMES[0]))
            {
                tooltip.SetIcon(StSLib.BADGE_ETHEREAL);
            }
            else if (title.equals(GameDictionary.RETAIN.NAMES[0]))
            {
                tooltip.SetIcon(StSLib.BADGE_RETAIN);
            }
            else if (title.equals(GameDictionary.EXHAUST.NAMES[0]))
            {
                tooltip.SetIcon(StSLib.BADGE_EXHAUST);
            }
            else {
                // Add Custom Icons
                AbstractCustomIcon icon = CustomIconHelper.getIcon(title);
                if (icon != null) {
                    tooltip.SetIcon(icon.region);
                }
            }
        }
    }

    /* Create EUITooltips for all basegame keywords
    *  For basegame keywords, we use the properly capitalized version of its key as its ID and the first name value as its name
    *  Modded keywords are added via BasemodPatches
    * */
    public static void RegisterBasegameKeywords() {

        // Energy tooltips are not present in GameDictionary
        EUITooltip energyTooltip = TryRegisterTooltip("E", TipHelper.TEXT[0], GameDictionary.TEXT[0], ENERGY_STRINGS).SetIconFunc(EUI::GetEnergyIcon);
        EUITooltip.RegisterName(StringUtils.lowerCase(TipHelper.TEXT[0]), energyTooltip);

        // Read directly from fields to obtain the actual IDs to use, which are language-invariant
        for (Field field : GameDictionary.class.getDeclaredFields())
        {
            if (field.getType() == com.megacrit.cardcrawl.localization.Keyword.class)
            {
                try
                {
                    final com.megacrit.cardcrawl.localization.Keyword k = (Keyword) field.get(null);
                    String id = JavaUtils.Capitalize(field.getName());
                    TryRegisterTooltip(JavaUtils.Capitalize(id), k.DESCRIPTION, k.NAMES);
                }
                catch (IllegalAccessException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static EUITooltip TryRegisterTooltip(String id, String description, String[] names) {
        return TryRegisterTooltip(id, names[0], description, names);
    }

    /* Register a tooltip with the given parameters. If grammar exists, its contents will be merged with this tooltip
     * */
    public static EUITooltip TryRegisterTooltip(String id, String title, String description, String[] names) {
        EUITooltip tooltip = EUITooltip.FindByID(id);
        if (tooltip == null) {
            String newTitle = JavaUtils.Capitalize(title);
            tooltip = new EUITooltip(newTitle, description);
            EUITooltip.RegisterID(id, tooltip);
            for (String subName : names) {
                EUITooltip.RegisterName(subName, tooltip);
            }
        }
        return tooltip;
    }

    public static void Dispose()
    {
        if (CurrentScreen != null)
        {
            CurrentScreen.Dispose();
            activeElement = null;
        }

        CurrentScreen = null;
        lastHovered = null;
    }

    public static void PreUpdate()
    {
        delta = Gdx.graphics.getRawDeltaTime();
        timer += delta;
        isDragging = false;
        lastHoveredTemp = null;
    }

    public static void Update()
    {
        if (AbstractDungeon.screen == AbstractScreen.EUI_SCREEN && CurrentScreen != null)
        {
            CurrentScreen.Update();
        }

        for (GUI_Base s : BattleSubscribers) {
            s.TryUpdate();
        }
        for (GUI_Base s : Subscribers) {
            s.TryUpdate();
        }

    }

    public static void PostUpdate()
    {
        lastHovered = lastHoveredTemp;
    }

    public static void PreRender(SpriteBatch sb)
    {
        if (AbstractDungeon.screen == AbstractScreen.EUI_SCREEN && CurrentScreen != null)
        {
            CurrentScreen.PreRender(sb);
        }

        RenderImpl(sb, preRenderList.iterator());
    }

    public static void Render(SpriteBatch sb)
    {
        if (AbstractDungeon.screen == AbstractScreen.EUI_SCREEN && CurrentScreen != null)
        {
            CurrentScreen.Render(sb);
        }

        for (GUI_Base s : Subscribers) {
            s.TryRender(sb);
        }

        // Battle subscribers are rendered in the energy panel patch
    }

    public static void PostRender(SpriteBatch sb)
    {
        RenderImpl(sb, postRenderList.iterator());
    }

    public static void PriorityPostRender(SpriteBatch sb)
    {
        RenderImpl(sb, priorityPostRenderList.iterator());
    }

    public static void AddBattleSubscriber(GUI_Base element) {
        BattleSubscribers.add(element);
    }

    public static void AddSubscriber(GUI_Base element) {
        Subscribers.add(element);
    }

    public static void SetCustomCardFilter(AbstractCard.CardColor cardColor, CustomCardFilterModule element) {
        customFilters.put(cardColor, element);
    }

    public static void SetCustomCardLibraryModule(AbstractCard.CardColor cardColor, CustomCardPoolModule element) {
        customLibraryModules.put(cardColor, element);
    }

    public static void SetCustomCardPoolModule(AbstractCard.CardColor cardColor, CustomCardPoolModule element) {
        customPoolModules.put(cardColor, element);
    }

    public static CustomCardFilterModule GetCustomCardFilter(AbstractPlayer player) {
        return player != null ? GetCustomCardFilter(player.getCardColor()) : null;
    }

    public static CustomCardFilterModule GetCustomCardFilter(AbstractCard.CardColor cardColor) {
        return customFilters.get(cardColor);
    }

    public static CustomCardPoolModule GetCustomCardLibraryModule(AbstractCard.CardColor cardColor) {
        return customLibraryModules.get(cardColor);
    }

    public static CustomCardPoolModule GetCustomCardPoolModule(AbstractPlayer player) {
        return player != null ? GetCustomCardPoolModule(player.getCardColor()) : null;
    }

    public static CustomCardPoolModule GetCustomCardPoolModule(AbstractCard.CardColor cardColor) {
        return customPoolModules.get(cardColor);
    }

    public static void ToggleViewUpgrades(boolean value)
    {
        SingleCardViewPopup.isViewingUpgrade = value;
    }

    private static void RenderImpl(SpriteBatch sb, Iterator<ActionT1<SpriteBatch>> i)
    {
        while (i.hasNext()) {
            ActionT1<SpriteBatch> toRender = i.next();
            toRender.Invoke(sb);
            i.remove();
        }
    }

    public static boolean IsDragging()
    {
        return isDragging;
    }

    public static boolean TryDragging()
    {
        final boolean drag = !CardCrawlGame.isPopupOpen && (CurrentScreen == null || !isDragging) && (isDragging = true);
        if (drag)
        {
            EUITooltip.CanRenderTooltips(false);
        }

        return drag;
    }

    public static boolean TryHover(Hitbox hitbox)
    {
        if (hitbox != null && hitbox.justHovered && hitbox != lastHovered)
        {
            hitbox.hovered = hitbox.justHovered = false;
            lastHoveredTemp = hitbox;
            return false;
        }

        if (hitbox == null || hitbox.hovered)
        {
            lastHoveredTemp = hitbox;
            return hitbox == lastHovered;
        }

        return false;
    }

    public static boolean TryToggleActiveElement(GUI_Base element, boolean setActive) {
        if (activeElement == null || activeElement == element) {
            activeElement = setActive ? element : null;
            return true;
        }
        return false;
    }

    public static boolean IsInActiveElement(AdvancedHitbox hb) {
        return activeElement == null || activeElement == hb.parentElement;
    }

    public static boolean DoesActiveElementExist() {
        return activeElement != null;
    }

    public static float Time_Sin(float distance, float speed)
    {
        return MathUtils.sin(timer * speed) * distance;
    }

    public static float Time_Cos(float distance, float speed)
    {
        return MathUtils.cos(timer * speed) * distance;
    }

    public static float Time_Multi(float value)
    {
        return timer * value;
    }

    public static float Time()
    {
        return timer;
    }

    public static float Delta()
    {
        return delta;
    }

    public static float Delta(float multiplier)
    {
        return delta * multiplier;
    }

    public static boolean Elapsed(float value)
    {
        return (delta >= value) || (((timer % value) - delta) < 0);
    }

    public static boolean Elapsed25()
    {
        return Elapsed(0.25f);
    }

    public static boolean Elapsed50()
    {
        return Elapsed(0.50f);
    }

    public static boolean Elapsed75()
    {
        return Elapsed(0.75f);
    }

    public static boolean Elapsed100()
    {
        return Elapsed(1.00f);
    }

    public static void AddPreRender(ActionT1<SpriteBatch> toRender)
    {
        preRenderList.add(toRender);
    }

    public static void AddPostRender(ActionT1<SpriteBatch> toRender)
    {
        postRenderList.add(toRender);
    }

    public static void AddPriorityPostRender(ActionT1<SpriteBatch> toRender)
    {
        priorityPostRenderList.add(toRender);
    }

    public static TextureRegion GetEnergyIcon() {
        AbstractCard.CardColor color = AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : ActingColor;
        if (color == null) {
            return AbstractCard.orb_red;
        }
        switch (color) {
            case RED:
            case COLORLESS:
            case CURSE:
                return AbstractCard.orb_red;
            case GREEN:
                return AbstractCard.orb_green;
            case BLUE:
                return AbstractCard.orb_blue;
            case PURPLE:
                return AbstractCard.orb_purple;
            default:
                return BaseMod.getCardEnergyOrbAtlasRegion(color);
        }
    }
}
