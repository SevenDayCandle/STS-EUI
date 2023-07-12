package extendedui;

import basemod.BaseMod;
import basemod.devcommands.ConsoleCommand;
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
import com.megacrit.cardcrawl.blights.AbstractBlight;
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
import extendedui.commands.ExportCommand;
import extendedui.configuration.EUIConfiguration;
import extendedui.exporter.EUIExporter;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.CustomCardFilterModule;
import extendedui.interfaces.markers.CustomCardPoolModule;
import extendedui.interfaces.markers.CustomFilterModule;
import extendedui.interfaces.markers.CustomPoolModule;
import extendedui.patches.EUIKeyword;
import extendedui.patches.game.TooltipPatches;
import extendedui.patches.screens.MenuPanelScreenPatches;
import extendedui.text.EUISmartText;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.EUIBase;
import extendedui.ui.cardFilter.*;
import extendedui.ui.cardFilter.filters.BlightKeywordFilters;
import extendedui.ui.cardFilter.filters.CardKeywordFilters;
import extendedui.ui.cardFilter.filters.PotionKeywordFilters;
import extendedui.ui.cardFilter.filters.RelicKeywordFilters;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.ui.screens.*;
import extendedui.ui.settings.ExtraModSettingsPanel;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.PotionInfo;
import extendedui.utilities.RelicInfo;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

public class EUI {
    private static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> preRenderList = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> postRenderList = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> priorityPostRenderList = new ConcurrentLinkedQueue<>();
    public static final ArrayList<EUIBase> battleSubscribers = new ArrayList<>();
    public static final ArrayList<EUIBase> subscribers = new ArrayList<>();
    public static final String[] ENERGY_STRINGS = {"[E]", "[R]", "[G]", "[B]", "[W]"};
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
    public static final HashMap<AbstractCard.CardColor, CustomCardFilterModule> customCardFilters = new HashMap<>();
    public static final HashMap<AbstractCard.CardColor, CustomCardPoolModule> customCardLibraryModules = new HashMap<>();
    public static final HashMap<AbstractCard.CardColor, CustomCardPoolModule> customCardPoolModules = new HashMap<>();
    public static final HashMap<AbstractCard.CardColor, CustomFilterModule<PotionInfo>> customPotionFilters = new HashMap<>();
    public static final HashMap<AbstractCard.CardColor, CustomPoolModule<PotionInfo>> customPotionPoolModules = new HashMap<>();
    public static final HashMap<AbstractCard.CardColor, CustomFilterModule<RelicInfo>> customRelicFilters = new HashMap<>();
    public static final HashMap<AbstractCard.CardColor, CustomPoolModule<RelicInfo>> customRelicPoolModules = new HashMap<>();
    private static float delta = 0;
    private static float timer = 0;
    private static int imguiIndex = 0;
    private static boolean isDragging;
    private static Hitbox lastClicked;
    protected static EUIBase activeElement;
    public static AbstractCard.CardColor actingColor;
    public static BlightKeywordFilters blightFilters;
    public static BlightLibraryScreen blightLibraryScreen;
    public static BlightSortHeader blightHeader;
    public static AbstractMenuScreen currentScreen;
    public static CardKeywordFilters cardFilters;
    public static CardPoolScreen cardsScreen;
    public static CountingPanel countingPanel;
    public static CustomCardLibSortHeader customHeader;
    public static CustomCardLibraryScreen customLibraryScreen;
    public static EUIButton openBlightFiltersButton;
    public static EUIButton openCardFiltersButton;
    public static EUIButton openPotionFiltersButton;
    public static EUIButton openRelicFiltersButton;
    public static EUITutorialScreen tutorialScreen;
    public static FakeFtueScreen ftueScreen;
    public static ExtraModSettingsPanel modSettingsScreen;
    public static PotionKeywordFilters potionFilters;
    public static PotionPoolScreen potionScreen;
    public static PotionSortHeader potionHeader;
    public static RelicKeywordFilters relicFilters;
    public static RelicPoolScreen relicScreen;
    public static RelicSortHeader relicHeader;
    public static CardPoolPanelItem compendiumButton;
    public static boolean disableInteract;

    public static void addBattleSubscriber(EUIBase element) {
        battleSubscribers.add(element);
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

    public static float delta() {
        return delta;
    }

    public static float delta(float multiplier) {
        return delta * multiplier;
    }

    public static void dispose() {
        if (currentScreen != null) {
            currentScreen.dispose();
        }

        activeElement = null;
        currentScreen = null;
        lastClicked = null;
        TooltipPatches.clearTips();
        EUITourTooltip.clearTutorialQueue();
    }

    public static boolean doesActiveElementExist() {
        return activeElement != null;
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

    public static int getImguiIndex() {
        return imguiIndex++;
    }

    public static void initialize() {
        // Set UI theming for file selector
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Save custom mod color names and class mappings for playerclasses
        for (AbstractPlayer p : CardCrawlGame.characterManager.getAllCharacters()) {
            EUIGameUtils.registerCustomColorName(p.getCardColor(), p.getLocalizedCharacterName());
            EUIGameUtils.registerColorPlayer(p.getCardColor(), p.chosenClass);
        }

        blightFilters = new BlightKeywordFilters();
        blightLibraryScreen = new BlightLibraryScreen();
        blightHeader = new BlightSortHeader(null);
        cardsScreen = new CardPoolScreen();
        cardFilters = new CardKeywordFilters();
        countingPanel = new CountingPanel();
        customHeader = new CustomCardLibSortHeader(null);
        customLibraryScreen = new CustomCardLibraryScreen();
        tutorialScreen = new EUITutorialScreen();
        ftueScreen = new FakeFtueScreen();
        modSettingsScreen = new ExtraModSettingsPanel();
        modSettingsScreen.setActive(false);
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

        EUITooltip tip = new EUITooltip(EUIRM.strings.ui_filters, EUIRM.strings.ui_filterExplanation);
        openBlightFiltersButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.05f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.ui_filters)
                .setOnClick(() -> EUI.blightFilters.toggleFilters())
                .setTooltip(tip)
                .setColor(Color.MAROON);
        openCardFiltersButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.05f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.ui_filters)
                .setOnClick(() -> EUI.cardFilters.toggleFilters())
                .setTooltip(tip)
                .setColor(Color.MAROON);
        openPotionFiltersButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.05f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.ui_filters)
                .setOnClick(() -> EUI.potionFilters.toggleFilters())
                .setTooltip(tip)
                .setColor(Color.MAROON);
        openRelicFiltersButton = new EUIButton(EUIRM.images.hexagonalButton.texture(), new DraggableHitbox(0, 0, Settings.WIDTH * 0.07f, Settings.HEIGHT * 0.07f, false).setIsPopupCompatible(true))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setPosition(Settings.WIDTH * 0.96f, Settings.HEIGHT * 0.05f)
                .setLabel(EUIFontHelper.buttonFont, 0.8f, EUIRM.strings.ui_filters)
                .setOnClick(() -> EUI.relicFilters.toggleFilters())
                .setTooltip(tip)
                .setColor(Color.MAROON);

        EUIExporter.initialize();
        MenuPanelScreenPatches.initialize();

        // Toggling smooth scrolling requires updating the library and card pool screens
        EUIConfiguration.useSnapScrolling.addListener(newValue -> {
            EUI.customLibraryScreen.resetGrid();
            EUI.cardsScreen.resetGrid();
        });

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
    }

    public static boolean isActiveElement(EUIBase element) {
        return activeElement == element;
    }

    public static boolean isDragging() {
        return isDragging;
    }

    public static boolean isInActiveElement(EUIHitbox hb) {
        return activeElement == null || activeElement == hb.parentElement;
    }

    public static boolean isLoaded() {
        return cardsScreen != null; // This will be null before the UI has loaded
    }

    public static Map<String, EUIKeyword> loadKeywords(FileHandle handle) {
        if (handle.exists()) {
            return EUIUtils.deserialize(handle.readString(String.valueOf(StandardCharsets.UTF_8)), new TypeToken<Map<String, EUIKeyword>>() {
            }.getType());
        }
        return new HashMap<>();
    }

    public static void postDispose() {
        activeElement = null;
        currentScreen = null;
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
        renderImpl(sb, priorityPostRenderList.iterator());
        EUITourTooltip.render(sb);
    }

    /* Create EUITooltips for all basegame keywords
     *  For basegame keywords, we use the properly capitalized version of its key as its ID and the first name value as its name
     *  Modded keywords are added via BasemodPatches
     * */
    public static void registerBasegameKeywords() {

        // Energy tooltips are not present in GameDictionary
        EUIKeywordTooltip energyTooltip = tryRegisterTooltip("E", null, TipHelper.TEXT[0], GameDictionary.TEXT[0], ENERGY_STRINGS).setIconFunc(EUI::getEnergyIcon);
        EUIKeywordTooltip.registerName(StringUtils.lowerCase(TipHelper.TEXT[0]), energyTooltip);

        // Read directly from fields to obtain the actual IDs to use, which are language-invariant
        for (Field field : GameDictionary.class.getDeclaredFields()) {
            if (field.getType() == com.megacrit.cardcrawl.localization.Keyword.class) {
                try {
                    final com.megacrit.cardcrawl.localization.Keyword k = (Keyword) field.get(null);
                    String id = EUIUtils.capitalize(field.getName());
                    tryRegisterTooltip(EUIUtils.capitalize(id), null, k.DESCRIPTION, k.NAMES);
                }
                catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /* Register a tooltip with the given parameters. If grammar exists, its contents will be merged with this tooltip
     * */

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

    // Add CommonKeywordIcon pictures to keywords. This REQUIRES stslib to run
    public static void registerKeywordIcons() {
        if (Loader.isModLoaded("stslib")) {
            for (EUIKeywordTooltip tooltip : EUIUtils.map(EUIKeywordTooltip.getEntries(), Map.Entry::getValue)) {
                String title = tooltip.title;
                // Add CommonKeywordIcon pictures to keywords
                if (title.equals(GameDictionary.INNATE.NAMES[0])) {
                    tooltip.setIcon(StSLib.BADGE_INNATE);
                }
                else if (title.equals(GameDictionary.ETHEREAL.NAMES[0])) {
                    tooltip.setIcon(StSLib.BADGE_ETHEREAL);
                }
                else if (title.equals(GameDictionary.RETAIN.NAMES[0])) {
                    tooltip.setIcon(StSLib.BADGE_RETAIN);
                }
                else if (title.equals(GameDictionary.EXHAUST.NAMES[0])) {
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
                EUIKeywordTooltip.registerName(EUISmartText.parseKeywordLogicWithAmount(keyword.PLURAL, 2).toLowerCase(), tooltip);
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

        // Battle subscribers are rendered in the energy panel patch
    }

    private static void renderImpl(SpriteBatch sb, Iterator<ActionT1<SpriteBatch>> i) {
        while (i.hasNext()) {
            ActionT1<SpriteBatch> toRender = i.next();
            toRender.invoke(sb);
            i.remove();
        }
    }

    public static void setActiveElement(EUIBase element) {
        activeElement = element;
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
