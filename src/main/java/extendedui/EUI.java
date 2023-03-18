package extendedui;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import com.evacipated.cardcrawl.mod.stslib.icons.CustomIconHelper;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.google.gson.reflect.TypeToken;
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
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.*;
import extendedui.patches.EUIKeyword;
import extendedui.text.EUISmartText;
import extendedui.ui.AbstractScreen;
import extendedui.ui.EUIBase;
import extendedui.ui.cardFilter.*;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.ui.settings.ModSettingsScreen;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EUI
{
    public static final ArrayList<EUIBase> BattleSubscribers = new ArrayList<>();
    public static final ArrayList<EUIBase> Subscribers = new ArrayList<>();
    private static final String[] ENERGY_STRINGS = {"[E]", "[R]", "[G]", "[B]", "[W]"};
    private static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> preRenderList = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> postRenderList = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> priorityPostRenderList = new ConcurrentLinkedQueue<>();
    private static final HashMap<AbstractCard.CardColor, CustomCardFilterModule> customCardFilters = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomCardPoolModule> customCardLibraryModules = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomCardPoolModule> customCardPoolModules = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomPotionFilterModule> customPotionFilters = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomPotionPoolModule> customPotionLibraryModules = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomPotionPoolModule> customPotionPoolModules = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomRelicFilterModule> customRelicFilters = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomRelicPoolModule> customRelicLibraryModules = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomRelicPoolModule> customRelicPoolModules = new HashMap<>();
    public static AbstractCard.CardColor actingColor;
    public static AbstractScreen currentScreen;
    public static CardKeywordFilters cardFilters;
    public static CardPoolScreen cardsScreen;
    public static CountingPanel countingPanel;
    public static CustomCardLibSortHeader customHeader;
    public static CustomCardLibraryScreen customLibraryScreen;
    public static EUIButton openCardFiltersButton;
    public static EUIButton openPotionFiltersButton;
    public static EUIButton openRelicFiltersButton;
    public static ModSettingsScreen modSettingsScreen;
    public static PotionKeywordFilters potionFilters;
    public static PotionPoolScreen potionScreen;
    public static PotionSortHeader potionHeader;
    public static RelicKeywordFilters relicFilters;
    public static RelicPoolScreen relicScreen;
    public static RelicSortHeader relicHeader;
    public static CardPoolPanelItem compendiumButton;
    protected static EUIBase activeElement;
    private static float delta = 0;
    private static float timer = 0;
    private static int imguiIndex = 0;
    private static boolean isDragging;
    private static Hitbox lastClicked;

    public static void addBattleSubscriber(EUIBase element) {
        BattleSubscribers.add(element);
    }

    public static void addPostRender(ActionT1<SpriteBatch> toRender)
    {
        postRenderList.add(toRender);
    }

    public static void addPreRender(ActionT1<SpriteBatch> toRender)
    {
        preRenderList.add(toRender);
    }

    public static void addPriorityPostRender(ActionT1<SpriteBatch> toRender)
    {
        priorityPostRenderList.add(toRender);
    }

    public static void addSubscriber(EUIBase element) {
        Subscribers.add(element);
    }

    public static float delta()
    {
        return delta;
    }

    public static float delta(float multiplier)
    {
        return delta * multiplier;
    }

    public static void dispose()
    {
        if (currentScreen != null)
        {
            currentScreen.dispose();
            activeElement = null;
        }

        currentScreen = null;
        lastClicked = null;
    }

    public static boolean doesActiveElementExist() {
        return activeElement != null;
    }

    public static boolean elapsed100()
    {
        return elapsed(1.00f);
    }

    public static boolean elapsed(float value)
    {
        return (delta >= value) || (((timer % value) - delta) < 0);
    }

    public static boolean elapsed25()
    {
        return elapsed(0.25f);
    }

    public static boolean elapsed50()
    {
        return elapsed(0.50f);
    }

    public static boolean elapsed75()
    {
        return elapsed(0.75f);
    }

    public static CustomCardFilterModule getCustomCardFilter(AbstractPlayer player) {
        return player != null ? getCustomCardFilter(player.getCardColor()) : null;
    }

    public static CustomCardFilterModule getCustomCardFilter(AbstractCard.CardColor cardColor) {
        return customCardFilters.get(cardColor);
    }

    public static CustomCardPoolModule getCustomCardLibraryModule(AbstractCard.CardColor cardColor) {
        return customCardLibraryModules.get(cardColor);
    }

    public static CustomCardPoolModule getCustomCardPoolModule(AbstractPlayer player) {
        return player != null ? getCustomCardPoolModule(player.getCardColor()) : null;
    }

    public static CustomCardPoolModule getCustomCardPoolModule(AbstractCard.CardColor cardColor) {
        return customCardPoolModules.get(cardColor);
    }

    public static CustomPotionFilterModule getCustomPotionFilter(AbstractPlayer player) {
        return player != null ? getCustomPotionFilter(player.getCardColor()) : null;
    }

    public static CustomPotionFilterModule getCustomPotionFilter(AbstractCard.CardColor cardColor) {
        return customPotionFilters.get(cardColor);
    }

    public static CustomPotionPoolModule getCustomPotionLibraryModule(AbstractCard.CardColor cardColor) {
        return customPotionLibraryModules.get(cardColor);
    }

    public static CustomPotionPoolModule getCustomPotionPoolModule(AbstractPlayer player) {
        return player != null ? getCustomPotionPoolModule(player.getCardColor()) : null;
    }

    public static CustomPotionPoolModule getCustomPotionPoolModule(AbstractCard.CardColor cardColor) {
        return customPotionPoolModules.get(cardColor);
    }

    public static CustomRelicFilterModule getCustomRelicFilter(AbstractPlayer player) {
        return player != null ? getCustomRelicFilter(player.getCardColor()) : null;
    }

    public static CustomRelicFilterModule getCustomRelicFilter(AbstractCard.CardColor cardColor) {
        return customRelicFilters.get(cardColor);
    }

    public static CustomRelicPoolModule getCustomRelicLibraryModule(AbstractCard.CardColor cardColor) {
        return customRelicLibraryModules.get(cardColor);
    }

    public static CustomRelicPoolModule getCustomRelicPoolModule(AbstractPlayer player) {
        return player != null ? getCustomRelicPoolModule(player.getCardColor()) : null;
    }

    public static CustomRelicPoolModule getCustomRelicPoolModule(AbstractCard.CardColor cardColor) {
        return customRelicPoolModules.get(cardColor);
    }

    public static int getImguiIndex()
    {
        return imguiIndex++;
    }

    public static void initialize()
    {
        // Set UI theming for file selector
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Save custom mod color names and class mappings for playerclasses
        for (AbstractPlayer p : CardCrawlGame.characterManager.getAllCharacters()) {
            EUIGameUtils.registerCustomColorName(p.getCardColor(), p.getLocalizedCharacterName());
            EUIGameUtils.registerColorPlayer(p.getCardColor(), p.chosenClass);
        }

        cardsScreen = new CardPoolScreen();
        cardFilters = new CardKeywordFilters();
        countingPanel = new CountingPanel();
        customHeader = new CustomCardLibSortHeader(null);
        customLibraryScreen = new CustomCardLibraryScreen();
        modSettingsScreen = new ModSettingsScreen();
        potionFilters = new PotionKeywordFilters();
        potionHeader = new PotionSortHeader(null);
        potionScreen = new PotionPoolScreen();
        relicFilters = new RelicKeywordFilters();
        relicHeader = new RelicSortHeader(null);
        relicScreen = new RelicPoolScreen();
        compendiumButton = new CardPoolPanelItem();

        // Toggling the compendium button requires us to immediately update the top panel
        // This needs to be added here instead of the config initialization because we would throw in a null item into the top panel if we did the latter
        EUIConfiguration.disableCompendiumButton.addListener(EUI::toggleCompendiumButton);

        openCardFiltersButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
            .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
            .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.05f).setText(EUIRM.strings.uiFilters)
            .setOnClick(() -> EUI.cardFilters.toggleFilters())
            .setColor(Color.GRAY);
        openPotionFiltersButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.05f).setText(EUIRM.strings.uiFilters)
                .setOnClick(() -> EUI.potionFilters.toggleFilters())
                .setColor(Color.GRAY);
        openRelicFiltersButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.05f).setText(EUIRM.strings.uiFilters)
                .setOnClick(() -> EUI.relicFilters.toggleFilters())
                .setColor(Color.GRAY);
    }

    public static void toggleCompendiumButton(boolean hide)
    {
        if (hide)
        {
            BaseMod.removeTopPanelItem(compendiumButton);
        }
        else
        {
            BaseMod.addTopPanelItem(compendiumButton);
        }
    }

    public static boolean isDragging()
    {
        return isDragging;
    }

    public static boolean isActiveElement(EUIBase element) {
        return activeElement == element;
    }

    public static boolean isInActiveElement(EUIHitbox hb) {
        return activeElement == null || activeElement == hb.parentElement;
    }

    public static boolean isLoaded() {
        return cardsScreen != null; // This will be null before the UI has loaded
    }

    public static void postDispose()
    {
        activeElement = null;
        currentScreen = null;
    }

    public static void postRender(SpriteBatch sb)
    {
        renderImpl(sb, postRenderList.iterator());
    }

    private static void renderImpl(SpriteBatch sb, Iterator<ActionT1<SpriteBatch>> i)
    {
        while (i.hasNext()) {
            ActionT1<SpriteBatch> toRender = i.next();
            toRender.invoke(sb);
            i.remove();
        }
    }

    public static void preRender(SpriteBatch sb)
    {
        if (AbstractDungeon.screen == AbstractScreen.EUI_SCREEN && currentScreen != null)
        {
            currentScreen.preRender(sb);
        }

        renderImpl(sb, preRenderList.iterator());
    }

    public static void preUpdate()
    {
        delta = Gdx.graphics.getRawDeltaTime();
        timer += delta;
        isDragging = false;
        lastClicked = null;
    }

    public static void priorityPostRender(SpriteBatch sb)
    {
        renderImpl(sb, priorityPostRenderList.iterator());
    }

    /* Create EUITooltips for all basegame keywords
    *  For basegame keywords, we use the properly capitalized version of its key as its ID and the first name value as its name
    *  Modded keywords are added via BasemodPatches
    * */
    public static void registerBasegameKeywords() {

        // Energy tooltips are not present in GameDictionary
        EUITooltip energyTooltip = tryRegisterTooltip("E", TipHelper.TEXT[0], GameDictionary.TEXT[0], ENERGY_STRINGS).setIconFunc(EUI::getEnergyIcon);
        EUITooltip.registerName(StringUtils.lowerCase(TipHelper.TEXT[0]), energyTooltip);

        // Read directly from fields to obtain the actual IDs to use, which are language-invariant
        for (Field field : GameDictionary.class.getDeclaredFields())
        {
            if (field.getType() == com.megacrit.cardcrawl.localization.Keyword.class)
            {
                try
                {
                    final com.megacrit.cardcrawl.localization.Keyword k = (Keyword) field.get(null);
                    String id = EUIUtils.capitalize(field.getName());
                    tryRegisterTooltip(EUIUtils.capitalize(id), k.DESCRIPTION, k.NAMES);
                }
                catch (IllegalAccessException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /* Register a tooltip with the given parameters. If grammar exists, its contents will be merged with this tooltip
     * */
    public static EUITooltip tryRegisterTooltip(String id, String title, String description, String[] names) {
        EUITooltip tooltip = EUITooltip.findByID(id);
        if (tooltip == null) {
            String newTitle = EUIUtils.capitalize(title);
            tooltip = new EUITooltip(newTitle, description);
            EUITooltip.registerID(id, tooltip);
            for (String subName : names) {
                EUITooltip.registerName(subName, tooltip);
            }
        }
        return tooltip;
    }

    public static TextureRegion getEnergyIcon() {
        AbstractCard.CardColor color = AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : actingColor;
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

    public static EUITooltip tryRegisterTooltip(String id, String description, String[] names) {
        return tryRegisterTooltip(id, names[0], description, names);
    }

    /* Add grammar rules to existing tooltips */
    public static void registerGrammar(Map<String, EUIKeyword> keywords) {
        for (Map.Entry<String, EUIKeyword> entry : keywords.entrySet()) {
            EUIKeyword grammar = entry.getValue();
            EUITooltip existing = EUITooltip.findByID(entry.getKey());
            if (existing != null) {
                existing.past = grammar.PAST;
                existing.plural = grammar.PLURAL;
                existing.present = grammar.PRESENT;
                existing.progressive = grammar.PROGRESSIVE;
            }
        }
    }

    // Add CommonKeywordIcon pictures to keywords. This REQUIRES stslib to run
    public static void registerKeywordIcons() {
        if (Loader.isModLoaded("stslib"))
        {
            for (EUITooltip tooltip : EUIUtils.map(EUITooltip.getEntries(), Map.Entry::getValue)) {
                String title = tooltip.title;
                // Add CommonKeywordIcon pictures to keywords
                if (title.equals(GameDictionary.INNATE.NAMES[0])) {
                    tooltip.setIcon(StSLib.BADGE_INNATE);
                }
                else if (title.equals(GameDictionary.ETHEREAL.NAMES[0]))
                {
                    tooltip.setIcon(StSLib.BADGE_ETHEREAL);
                }
                else if (title.equals(GameDictionary.RETAIN.NAMES[0]))
                {
                    tooltip.setIcon(StSLib.BADGE_RETAIN);
                }
                else if (title.equals(GameDictionary.EXHAUST.NAMES[0]))
                {
                    tooltip.setIcon(StSLib.BADGE_EXHAUST);
                }
                else {
                    // Add Custom Icons
                    AbstractCustomIcon icon = CustomIconHelper.getIcon(title);
                    if (icon != null) {
                        tooltip.setIcon(icon.region);
                    }
                }
            }
        }
    }

    public static Map<String, EUIKeyword> loadKeywords(FileHandle handle) {
        if (handle.exists()) {
            return EUIUtils.deserialize(handle.readString(String.valueOf(StandardCharsets.UTF_8)), new TypeToken<Map<String, EUIKeyword>>(){}.getType());
        }
        return new HashMap<>();
    }

    public static ArrayList<EUITooltip> registerKeywords(FileHandle handle)
    {
        return registerKeywords(loadKeywords(handle));
    }

    public static ArrayList<EUITooltip> registerKeywords(Map<String, EUIKeyword> keywords)
    {
        ArrayList<EUITooltip> tooltips = new ArrayList<>();
        for (Map.Entry<String, EUIKeyword> pair : keywords.entrySet()) {
            EUIKeyword keyword = pair.getValue();
            EUITooltip tooltip = new EUITooltip(keyword);
            EUITooltip.registerID(pair.getKey(), tooltip);
            EUITooltip.registerName(keyword.NAME.toLowerCase(), tooltip);
            if (keyword.PLURAL != null)
            {
                // Emulate a plural parsing
                // TODO account for languages that have multiple plural forms
                EUITooltip.registerName(EUISmartText.parseKeywordLogicWithAmount(keyword.PLURAL, 2).toLowerCase(), tooltip);
            }
            if (keyword.PAST != null)
            {
                EUITooltip.registerName(keyword.PAST.toLowerCase(), tooltip);
            }
            if (keyword.PRESENT != null)
            {
                EUITooltip.registerName(keyword.PRESENT.toLowerCase(), tooltip);
            }
            if (keyword.PROGRESSIVE != null)
            {
                EUITooltip.registerName(keyword.PROGRESSIVE.toLowerCase(), tooltip);
            }
            tooltips.add(tooltip);
        }
        return tooltips;
    }

    public static void render(SpriteBatch sb)
    {
        if (AbstractDungeon.screen == AbstractScreen.EUI_SCREEN && currentScreen != null)
        {
            currentScreen.renderImpl(sb);
        }

        for (EUIBase s : Subscribers) {
            s.tryRender(sb);
        }

        // Battle subscribers are rendered in the energy panel patch
    }

    public static void setCustomCardFilter(AbstractCard.CardColor cardColor, CustomCardFilterModule element) {
        customCardFilters.put(cardColor, element);
    }

    public static void setCustomCardLibraryModule(AbstractCard.CardColor cardColor, CustomCardPoolModule element) {
        customCardLibraryModules.put(cardColor, element);
    }

    public static void setCustomCardPoolModule(AbstractCard.CardColor cardColor, CustomCardPoolModule element) {
        customCardPoolModules.put(cardColor, element);
    }

    public static void setCustomPotionFilter(AbstractCard.CardColor cardColor, CustomPotionFilterModule element) {
        customPotionFilters.put(cardColor, element);
    }

    public static void setCustomPotionLibraryModule(AbstractCard.CardColor cardColor, CustomPotionPoolModule element) {
        customPotionLibraryModules.put(cardColor, element);
    }

    public static void setCustomPotionPoolModule(AbstractCard.CardColor cardColor, CustomPotionPoolModule element) {
        customPotionPoolModules.put(cardColor, element);
    }

    public static void setCustomRelicFilter(AbstractCard.CardColor cardColor, CustomRelicFilterModule element) {
        customRelicFilters.put(cardColor, element);
    }

    public static void setCustomRelicLibraryModule(AbstractCard.CardColor cardColor, CustomRelicPoolModule element) {
        customRelicLibraryModules.put(cardColor, element);
    }

    public static void setCustomRelicPoolModule(AbstractCard.CardColor cardColor, CustomRelicPoolModule element) {
        customRelicPoolModules.put(cardColor, element);
    }

    public static float time()
    {
        return timer;
    }

    public static float timeCos(float distance, float speed)
    {
        return MathUtils.cos(timer * speed) * distance;
    }

    public static float timeMulti(float value)
    {
        return timer * value;
    }

    public static float timeSin(float distance, float speed)
    {
        return MathUtils.sin(timer * speed) * distance;
    }

    public static void toggleViewUpgrades(boolean value)
    {
        SingleCardViewPopup.isViewingUpgrade = value;
    }

    public static boolean tryClick(Hitbox hitbox)
    {
        if (lastClicked == null || lastClicked == hitbox)
        {
            lastClicked = hitbox;
            return true;
        }
        return false;
    }

    public static boolean tryDragging()
    {
        final boolean drag = !CardCrawlGame.isPopupOpen && (currentScreen == null || !isDragging) && (isDragging = true);
        if (drag)
        {
            EUITooltip.canRenderTooltips(false);
        }

        return drag;
    }

    public static void setActiveElement(EUIBase element) {
        activeElement = element;
    }

    public static void update()
    {
        if (AbstractDungeon.screen == AbstractScreen.EUI_SCREEN && currentScreen != null)
        {
            currentScreen.updateImpl();
        }

        for (EUIBase s : BattleSubscribers) {
            s.tryUpdate();
        }
        for (EUIBase s : Subscribers) {
            s.tryUpdate();
        }

    }
}
