package extendedui;

import basemod.BaseMod;
import basemod.devcommands.ConsoleCommand;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.localization.Keyword;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.commands.ExportCommand;
import extendedui.configuration.EUIConfiguration;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.CustomCardFilterModule;
import extendedui.interfaces.markers.CustomCardPoolModule;
import extendedui.interfaces.markers.CustomFilterModule;
import extendedui.interfaces.markers.CustomPoolModule;
import extendedui.patches.EUIKeyword;
import extendedui.patches.game.TooltipPatches;
import extendedui.patches.screens.MenuPanelScreenPatches;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.EUIBase;
import extendedui.ui.cardFilter.*;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.ui.screens.*;
import extendedui.ui.settings.ExtraModSettingsPanel;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUITextHelper;
import extendedui.utilities.PotionInfo;
import extendedui.utilities.RelicInfo;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

public class EUI {
    private static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> preRenderList = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> postRenderList = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> priorityPostRenderList = new ConcurrentLinkedQueue<>();
    private static final ArrayList<EUIBase> battleSubscribers = new ArrayList<>();
    private static final ArrayList<EUIBase> subscribers = new ArrayList<>();
    private static final Stack<EUIBase> activeElements = new Stack<>();
    private static final HashMap<AbstractCard.CardColor, CustomCardFilterModule> customCardFilters = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomCardPoolModule> customCardLibraryModules = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomCardPoolModule> customCardPoolModules = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomFilterModule<PotionInfo>> customPotionFilters = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomPoolModule<PotionInfo>> customPotionPoolModules = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomFilterModule<RelicInfo>> customRelicFilters = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, CustomPoolModule<RelicInfo>> customRelicPoolModules = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, FuncT1<String, AbstractCard>> SET_FUNCS = new HashMap<>();
    public static final ArrayList<CustomFilterModule<AbstractBlight>> globalCustomBlightFilters = new ArrayList<>();
    public static final ArrayList<CustomPoolModule<AbstractBlight>> globalCustomBlightLibraryModules = new ArrayList<>(); // TODO use this
    public static final ArrayList<CustomCardFilterModule> globalCustomCardFilters = new ArrayList<>();
    public static final ArrayList<CustomCardPoolModule> globalCustomCardLibraryModules = new ArrayList<>();
    public static final ArrayList<CustomCardPoolModule> globalCustomCardPoolModules = new ArrayList<>();
    public static final ArrayList<CustomFilterModule<PotionInfo>> globalCustomPotionFilters = new ArrayList<>();
    public static final ArrayList<CustomPoolModule<PotionInfo>> globalCustomPotionLibraryModules = new ArrayList<>(); // TODO use this
    public static final ArrayList<CustomPoolModule<PotionInfo>> globalCustomPotionPoolModules = new ArrayList<>();
    public static final ArrayList<CustomFilterModule<RelicInfo>> globalCustomRelicFilters = new ArrayList<>();
    public static final ArrayList<CustomPoolModule<RelicInfo>> globalCustomRelicLibraryModules = new ArrayList<>(); // TODO use this
    public static final ArrayList<CustomPoolModule<RelicInfo>> globalCustomRelicPoolModules = new ArrayList<>();
    public static final String ENERGY_ID = "E";
    public static final String ENERGY_TIP = "[E]";
    private static final String PLAYTESTER_ART = "Playtester Art";
    private static final String SLEEPY_TIME = "ZZZZZZZ"; // This is used in the packmaster for denoting cards with no packs
    public static final String[] ENERGY_STRINGS = {ENERGY_TIP, "[R]", "[G]", "[B]", "[W]"};
    private static float delta = 0;
    private static float timer = 0;
    private static boolean isDragging;
    private static boolean stslibActive;
    private static Hitbox lastClicked;
    public static AbstractCard.CardColor actingColor;
    public static BlightKeywordFilters blightFilters;
    public static BlightLibraryScreen blightLibraryScreen;
    public static AbstractMenuScreen currentScreen;
    public static CardKeywordFilters cardFilters;
    public static CardPoolScreen cardsScreen;
    public static CountingPanel<AbstractCard> cardCounters;
    public static CustomCardLibraryScreen customLibraryScreen;
    public static EUIButton openFiltersButton;
    public static EUITutorialScreen tutorialScreen;
    public static FilterSortHeader sortHeader;
    public static FakeFtueScreen ftueScreen;
    public static ExtraModSettingsPanel modSettingsScreen;
    public static CountingPanel<PotionInfo> potionCounters;
    public static PotionKeywordFilters potionFilters;
    public static PotionPoolScreen potionScreen;
    public static CountingPanel<RelicInfo> relicCounters;
    public static RelicKeywordFilters relicFilters;
    public static RelicPoolScreen relicScreen;
    public static CardPoolPanelItem compendiumButton;
    public static boolean disableInteract;

    public static void addBattleSubscriber(EUIBase element) {
        battleSubscribers.add(element);
    }

    public static void addCardSetFilter(AbstractCard.CardColor co, FuncT1<String, AbstractCard> stringFunc) {
        FuncT1<String, AbstractCard> origFunc = SET_FUNCS.get(co);
        FuncT1<String, AbstractCard> resFunc;
        if (origFunc != null) {
            resFunc = c -> {
                String res = stringFunc.invoke(c);
                return !StringUtils.isEmpty(res) ? res : origFunc.invoke(c);
            };
        }
        else {
            resFunc = stringFunc;
        }
        SET_FUNCS.put(co, resFunc);
        CustomCardFilterModule module = getCustomCardFilter(co);
        if (module instanceof SetCardFilterModule) {
            ((SetCardFilterModule) module).setNameFunc(resFunc);
        }
        else if (module == null) {
            setCustomCardFilter(co, new SetCardFilterModule(resFunc));
        }
    }

    public static void addGlobalCustomBlightFilter(CustomFilterModule<AbstractBlight> element) {
        globalCustomBlightFilters.add(element);
    }

    public static void addGlobalCustomBlightLibraryModule(CustomPoolModule<AbstractBlight> element) {
        globalCustomBlightLibraryModules.add(element);
    }

    public static void addGlobalCustomCardFilter(CustomCardFilterModule element) {
        globalCustomCardFilters.add(element);
    }

    public static void addGlobalCustomCardLibraryModule(CustomCardPoolModule element) {
        globalCustomCardLibraryModules.add(element);
    }

    public static void addGlobalCustomCardPoolModule(CustomCardPoolModule element) {
        globalCustomCardPoolModules.add(element);
    }

    public static void addGlobalCustomPotionFilter(CustomFilterModule<PotionInfo> element) {
        globalCustomPotionFilters.add(element);
    }

    public static void addGlobalCustomPotionLibraryModule(CustomPoolModule<PotionInfo> element) {
        globalCustomPotionLibraryModules.add(element);
    }

    public static void addGlobalCustomPotionPoolModule(CustomPoolModule<PotionInfo> element) {
        globalCustomPotionPoolModules.add(element);
    }

    public static void addGlobalCustomRelicFilter(CustomFilterModule<RelicInfo> element) {
        globalCustomRelicFilters.add(element);
    }

    public static void addGlobalCustomRelicLibraryModule(CustomPoolModule<RelicInfo> element) {
        globalCustomRelicLibraryModules.add(element);
    }

    public static void addGlobalCustomRelicPoolModule(CustomPoolModule<RelicInfo> element) {
        globalCustomRelicPoolModules.add(element);
    }

    public static void addPostRender(ActionT1<SpriteBatch> toRender) {
        postRenderList.add(toRender);
    }

    public static void addPreRender(ActionT1<SpriteBatch> toRender) {
        preRenderList.add(toRender);
    }

    public static void addPriorityPostRender(ActionT1<SpriteBatch> toRender) {
        priorityPostRenderList.add(toRender);
    }

    public static void addSubscriber(EUIBase element) {
        subscribers.add(element);
    }

    public static void clearActiveElements() {
        activeElements.clear();
    }

    public static float delta() {
        return delta;
    }

    public static float delta(float multiplier) {
        return delta * multiplier;
    }

    public static void dispose() {
        activeElements.clear();
        lastClicked = null;
        TooltipPatches.clearTips();
        EUITourTooltip.clearTutorialQueue();
        CardCrawlGame.isPopupOpen = false;
    }

    public static boolean doesActiveElementExist() {
        return !activeElements.isEmpty();
    }

    public static boolean elapsed(float value) {
        return (delta >= value) || (((timer % value) - delta) < 0);
    }

    public static boolean elapsed100() {
        return elapsed(1.00f);
    }

    public static boolean elapsed25() {
        return elapsed(0.25f);
    }

    public static boolean elapsed50() {
        return elapsed(0.50f);
    }

    public static boolean elapsed75() {
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

    public static CustomFilterModule<PotionInfo> getCustomPotionFilter(AbstractPlayer player) {
        return player != null ? getCustomPotionFilter(player.getCardColor()) : null;
    }

    public static CustomFilterModule<PotionInfo> getCustomPotionFilter(AbstractCard.CardColor cardColor) {
        return customPotionFilters.get(cardColor);
    }

    public static CustomPoolModule<PotionInfo> getCustomPotionPoolModule(AbstractPlayer player) {
        return player != null ? getCustomPotionPoolModule(player.getCardColor()) : null;
    }

    public static CustomPoolModule<PotionInfo> getCustomPotionPoolModule(AbstractCard.CardColor cardColor) {
        return customPotionPoolModules.get(cardColor);
    }

    public static CustomFilterModule<RelicInfo> getCustomRelicFilter(AbstractPlayer player) {
        return player != null ? getCustomRelicFilter(player.getCardColor()) : null;
    }

    public static CustomFilterModule<RelicInfo> getCustomRelicFilter(AbstractCard.CardColor cardColor) {
        return customRelicFilters.get(cardColor);
    }

    public static CustomPoolModule<RelicInfo> getCustomRelicPoolModule(AbstractPlayer player) {
        return player != null ? getCustomRelicPoolModule(player.getCardColor()) : null;
    }

    public static CustomPoolModule<RelicInfo> getCustomRelicPoolModule(AbstractCard.CardColor cardColor) {
        return customRelicPoolModules.get(cardColor);
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

    public static FuncT1<String, AbstractCard> getSetFunction(AbstractCard.CardColor c) {
        return SET_FUNCS.get(c);
    }

    public static boolean isActiveElement(EUIBase element) {
        return EUIUtils.any(activeElements, e -> e == element);
    }

    public static boolean isDragging() {
        return isDragging;
    }

    public static boolean isInTopActiveElement(EUIHitbox hb) {
        return activeElements.isEmpty() || activeElements.peek() == hb.parentElement;
    }

    public static boolean isLoaded() {
        return cardsScreen != null; // This will be null before the UI has loaded
    }

    public static boolean isPlaytesterArt() {
        return Settings.gamePref.getBoolean(PLAYTESTER_ART, false);
    }

    public static boolean isStsLib() {
        return stslibActive;
    }

    public static boolean isTopActiveElement(EUIBase element) {
        return !activeElements.isEmpty() && activeElements.peek() == element;
    }

    public static Map<String, EUIKeyword> loadKeywords(FileHandle handle) {
        if (handle.exists()) {
            return EUIUtils.deserialize(handle.readString(String.valueOf(StandardCharsets.UTF_8)), new TypeToken<Map<String, EUIKeyword>>() {
            }.getType());
        }
        return new HashMap<>();
    }

    public static void popActiveElement(EUIBase element) {
        while (!activeElements.isEmpty()) {
            EUIBase top = activeElements.pop();
            if (element == top) {
                return;
            }
        }
    }

    public static void postDispose() {
        activeElements.clear();
        currentScreen = null;
    }

    public static void postInitialize() {
        // Set UI theming for file selector
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Save custom mod color names and class mappings for playerclasses
        for (AbstractPlayer p : CardCrawlGame.characterManager.getAllCharacters()) {
            if (p != null) {
                AbstractCard.CardColor color = p.getCardColor();
                if (color != null) {
                    EUIGameUtils.registerCustomColorName(color, p.getLocalizedCharacterName());
                    EUIGameUtils.registerColorPlayer(color, p.chosenClass);
                }
                else {
                    EUIUtils.logWarning(EUI.class, "Tried to register a null player color, WTF: " + p);
                }
            }
            else {
                EUIUtils.logWarning(EUI.class, "Tried to register a null player, WTF");
            }
        }

        sortHeader = new FilterSortHeader();
        blightFilters = new BlightKeywordFilters();
        blightLibraryScreen = new BlightLibraryScreen();
        cardsScreen = new CardPoolScreen();
        cardFilters = new CardKeywordFilters();
        cardCounters = CountingPanel.counterCards();
        potionCounters = CountingPanel.counterPotions();
        relicCounters = CountingPanel.counterRelics();
        customLibraryScreen = new CustomCardLibraryScreen();
        tutorialScreen = new EUITutorialScreen();
        ftueScreen = new FakeFtueScreen();
        modSettingsScreen = new ExtraModSettingsPanel();
        modSettingsScreen.setActive(false);
        potionFilters = new PotionKeywordFilters();
        potionScreen = new PotionPoolScreen();
        relicFilters = new RelicKeywordFilters();
        relicScreen = new RelicPoolScreen();
        compendiumButton = new CardPoolPanelItem();

        // Toggling the compendium button requires us to immediately update the top panel
        // This needs to be added here instead of the config initialization because we would throw in a null item into the top panel if we did the latter
        EUIConfiguration.disableCompendiumButton.addListener(EUI::toggleCompendiumButton);

        EUITooltip tip = new EUITooltip(EUIRM.strings.ui_filters, EUIRM.strings.ui_filterExplanation);
        openFiltersButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.05f)
                .setLabel(FontHelper.buttonLabelFont, 0.8f, EUIRM.strings.ui_filters)
                .setTooltip(tip)
                .setColor(Color.MAROON);

        EUIExporter.initialize();
        MenuPanelScreenPatches.initialize();

        // Toggling keyword icons requires us to update keyword tooltip heights
        EUIConfiguration.enableDescriptionIcons.addListener(newValue -> {
            EUIKeywordTooltip.invalidateAllHeights();
        });

        // Register basemod screens
        BaseMod.addCustomScreen(cardsScreen);
        BaseMod.addCustomScreen(relicScreen);
        BaseMod.addCustomScreen(potionScreen);
        BaseMod.addCustomScreen(ftueScreen);

        // Commands
        ConsoleCommand.addCommand("export", ExportCommand.class);

        // Compatibility
        stslibActive = Loader.isModLoaded("stslib");
        tryGetPackmaster();
    }

    public static void postRender(SpriteBatch sb) {
        EUI.relicFilters.tryRender(sb);
        EUI.potionFilters.tryRender(sb);
        EUI.cardFilters.tryRender(sb);
        EUI.blightFilters.tryRender(sb);
        EUIExporter.exportDropdown.tryRender(sb);
        renderImpl(sb, postRenderList.iterator());
    }

    public static void preRender(SpriteBatch sb) {
        if (currentScreen != null) {
            currentScreen.preRender(sb);
        }

        renderImpl(sb, preRenderList.iterator());
    }

    public static void preUpdate() {
        delta = Gdx.graphics.getRawDeltaTime();
        timer += delta;
        isDragging = false;
        lastClicked = null;
    }

    public static void priorityPostRender(SpriteBatch sb) {
        EUITourTooltip.updateAndRender(sb);
        renderImpl(sb, priorityPostRenderList.iterator());
    }

    public static void pushActiveElement(EUIBase element) {
        activeElements.push(element);
    }

    /* Create EUITooltips for all basegame keywords
     *  For basegame keywords, we use the properly capitalized version of its key as its ID and the first name value as its name
     *  Modded keywords are added via BasemodPatches
     * */
    public static void registerBasegameKeywords() {

        // Energy tooltips are not present in GameDictionary
        EUIKeywordTooltip energyTooltip = tryRegisterTooltip(ENERGY_ID, null, TipHelper.TEXT[0], GameDictionary.TEXT[0], ENERGY_STRINGS)
                .setIconFunc(EUI::getEnergyIcon)
                .setCanAdd(false)
                .forceIcon(true);
        EUIKeywordTooltip.registerName(StringUtils.lowerCase(TipHelper.TEXT[0]), energyTooltip);

        // Read directly from fields to obtain the actual IDs to use, which are language-invariant
        for (Field field : GameDictionary.class.getDeclaredFields()) {
            if (field.getType() == com.megacrit.cardcrawl.localization.Keyword.class) {
                try {
                    final com.megacrit.cardcrawl.localization.Keyword k = (Keyword) field.get(null);
                    String fName = field.getName();
                    String id = fName.charAt(0) + fName.substring(1).toLowerCase(Locale.ROOT); // Locale-insensitive casing
                    tryRegisterTooltip(id, null, k.DESCRIPTION, k.NAMES);
                }
                catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                    EUIUtils.logError(EUI.class, "Failed to initialize tooltip for field " + String.valueOf(field) + ": " + ex.getLocalizedMessage());
                }
            }
        }
    }

    /* Add grammar rules to existing tooltips */
    public static void registerGrammar(Map<String, EUIKeyword> keywords) {
        for (Map.Entry<String, EUIKeyword> entry : keywords.entrySet()) {
            EUIKeyword grammar = entry.getValue();
            EUIKeywordTooltip existing = EUIKeywordTooltip.findByID(entry.getKey());
            if (existing != null) {
                existing.past = grammar.PAST;
                existing.plural = grammar.PLURAL;
                existing.present = grammar.PRESENT;
                existing.progressive = grammar.PROGRESSIVE;
            }
        }
    }

    public static ArrayList<EUIKeywordTooltip> registerKeywords(FileHandle handle) {
        return registerKeywords(loadKeywords(handle));
    }

    public static ArrayList<EUIKeywordTooltip> registerKeywords(Map<String, EUIKeyword> keywords) {
        ArrayList<EUIKeywordTooltip> tooltips = new ArrayList<>();
        for (Map.Entry<String, EUIKeyword> pair : keywords.entrySet()) {
            String key = pair.getKey();
            EUIKeyword keyword = pair.getValue();
            EUIKeywordTooltip tooltip = new EUIKeywordTooltip(keyword);

            String[] split = splitID(key);
            if (split.length > 1) {
                tooltip.setModID(split[0]);
            }

            EUIKeywordTooltip.registerID(key, tooltip);
            EUIKeywordTooltip.registerName(keyword.NAME.toLowerCase(), tooltip);
            if (keyword.PLURAL != null) {
                // Emulate a plural parsing
                // TODO account for languages that have multiple plural forms
                EUIKeywordTooltip.registerName(EUITextHelper.parseKeywordLogicWithAmount(keyword.PLURAL, 2).toLowerCase(), tooltip);
            }
            if (keyword.PAST != null) {
                EUIKeywordTooltip.registerName(keyword.PAST.toLowerCase(), tooltip);
            }
            if (keyword.PRESENT != null) {
                EUIKeywordTooltip.registerName(keyword.PRESENT.toLowerCase(), tooltip);
            }
            if (keyword.PROGRESSIVE != null) {
                EUIKeywordTooltip.registerName(keyword.PROGRESSIVE.toLowerCase(), tooltip);
            }
            tooltips.add(tooltip);
        }
        return tooltips;
    }

    public static void render(SpriteBatch sb) {
        if (currentScreen != null) {
            currentScreen.renderImpl(sb);
        }

        for (EUIBase s : subscribers) {
            s.tryRender(sb);
        }
    }

    public static void renderBattleSubscribers(SpriteBatch sb) {
        for (EUIBase s : battleSubscribers) {
            s.tryRender(sb);
        }
    }

    private static void renderImpl(SpriteBatch sb, Iterator<ActionT1<SpriteBatch>> i) {
        while (i.hasNext()) {
            ActionT1<SpriteBatch> toRender = i.next();
            toRender.invoke(sb);
            i.remove();
        }
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

    public static void setCustomPotionFilter(AbstractCard.CardColor cardColor, CustomFilterModule<PotionInfo> element) {
        customPotionFilters.put(cardColor, element);
    }

    public static void setCustomPotionPoolModule(AbstractCard.CardColor cardColor, CustomPoolModule<PotionInfo> element) {
        customPotionPoolModules.put(cardColor, element);
    }

    public static void setCustomRelicFilter(AbstractCard.CardColor cardColor, CustomFilterModule<RelicInfo> element) {
        customRelicFilters.put(cardColor, element);
    }

    public static void setCustomRelicPoolModule(AbstractCard.CardColor cardColor, CustomPoolModule<RelicInfo> element) {
        customRelicPoolModules.put(cardColor, element);
    }

    public static String[] splitID(String id) {
        return id.split(Pattern.quote(":"), 2);
    }

    public static float time() {
        return timer;
    }

    public static float timeCos(float distance, float speed) {
        return MathUtils.cos(timer * speed) * distance;
    }

    public static float timeMulti(float value) {
        return timer * value;
    }

    public static float timeSin(float distance, float speed) {
        return MathUtils.sin(timer * speed) * distance;
    }

    public static void toggleCompendiumButton(boolean hide) {
        if (hide) {
            BaseMod.removeTopPanelItem(compendiumButton);
        }
        else {
            BaseMod.addTopPanelItem(compendiumButton);
        }
    }

    public static void toggleBetaArt(boolean value) {
        Settings.PLAYTESTER_ART_MODE = value;
    }

    public static void toggleBetaArtReset() {
        toggleBetaArt(isPlaytesterArt());
    }

    public static void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = value;
    }

    public static boolean tryClick(Hitbox hitbox) {
        if (lastClicked == null || lastClicked == hitbox) {
            lastClicked = hitbox;
            return true;
        }
        return false;
    }

    public static boolean tryDragging() {
        final boolean drag = !CardCrawlGame.isPopupOpen && (currentScreen == null || !isDragging) && (isDragging = true);
        if (drag) {
            EUITooltip.blockTooltips();
        }

        return drag;
    }

    private static void tryGetPackmaster() {
        if (Loader.isModLoaded("anniv5")) {
            try {
                AbstractCard.CardColor packmasterColor = AbstractCard.CardColor.valueOf("PACKMASTER_RAINBOW");
                Class<?> targetClass = Class.forName("thePackmaster.patches.CompendiumPatches$CustomOrdering");
                FuncT1<String, Object> getPackmasterPack = FuncT1.get(String.class, targetClass, "getParnetNameFromObject", Object.class); // PAR NET
                FuncT1<String, AbstractCard> packmasterStringFunc = c -> {
                    String res = getPackmasterPack.invoke(c);
                    return !SLEEPY_TIME.equals(res) && !StringUtils.isEmpty(res) ? res : EUIUtils.EMPTY_STRING;
                };
                addCardSetFilter(packmasterColor, packmasterStringFunc);
                addCardSetFilter(AbstractCard.CardColor.COLORLESS, packmasterStringFunc);
            }
            catch (Throwable e) {
                e.printStackTrace();
                EUIUtils.logError(EUI.class, "Failed to get Packmaster color:" + e.getLocalizedMessage() + " " + e.getCause());
            }
        }
    }

    public static EUIKeywordTooltip tryRegisterTooltip(String id, String modID, String title, String description, String[] names) {
        EUIKeywordTooltip tooltip = EUIKeywordTooltip.findByID(id);
        if (tooltip == null) {
            String newTitle = EUIUtils.capitalize(title);
            tooltip = new EUIKeywordTooltip(newTitle, description, modID);
            EUIKeywordTooltip.registerID(id, tooltip);
            for (String subName : names) {
                EUIKeywordTooltip.registerName(subName, tooltip);
            }
        }
        return tooltip;
    }

    public static EUITooltip tryRegisterTooltip(String id, String modID, String description, String[] names) {
        return tryRegisterTooltip(id, modID, names[0], description, names);
    }

    public static void update() {
        if (currentScreen != null) {
            currentScreen.updateImpl();
        }

        for (EUIBase s : battleSubscribers) {
            s.tryUpdate();
        }
        for (EUIBase s : subscribers) {
            s.tryUpdate();
        }

    }
}
