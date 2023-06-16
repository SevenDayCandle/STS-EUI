package extendedui.configuration;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModMinMaxSlider;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.settings.BasemodSettingsPage;
import extendedui.ui.settings.ExtraModSettingsPanel;
import extendedui.ui.settings.ModSettingsPathSelector;
import extendedui.ui.settings.ModSettingsToggle;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class EUIConfiguration {

    private static HashSet<String> tips = null;
    private static SpireConfig config;
    private static final ArrayList<STSConfigItem<?>> CONFIG_ITEMS = new ArrayList<>();
    private static final String BANNER_FONT = getFullKey("BannerFont");
    private static final String BUTTON_FONT = getFullKey("ButtonFont");
    private static final String CARD_DESC_FONT = getFullKey("CardDescFont");
    private static final String CARD_TITLE_FONT = getFullKey("CardTitleFont");
    private static final String DISABLE_COMPENDIUM_BUTTON = getFullKey("DisableCompendiumButton");
    private static final String DISABLE_EFFEKSEER = getFullKey("DisableEffekseer");
    private static final String ENABLE_CARD_POOL_DEBUG = getFullKey("EnableCardPoolDebug");
    private static final String ENABLE_DESCRIPTION_ICONS = getFullKey("EnableDescriptionIcons");
    private static final String ENERGY_FONT = getFullKey("EnergyFont");
    private static final String FLUSH_ON_GAME_START = getFullKey("FlushOnGameStart");
    private static final String FLUSH_ON_ROOM_START = getFullKey("FlushOnRoomStart");
    private static final String HIDE_TIP_DESCRIPTION = getFullKey("HideTipDescription");
    private static final String LAST_EXPORT_PATH = getFullKey("LastExportPath");
    private static final String OVERRIDE_GAME_FONT = getFullKey("OverrideGameFont");
    private static final String PREFIX = "EUI";
    private static final String SHOW_COUNTING_PANEL = getFullKey("ShowCountingPanel");
    private static final String SHOW_MOD_SETTINGS = getFullKey("ShowModSettings");
    private static final String TIP_DESC_FONT = getFullKey("TipDescFont");
    private static final String TITLE_TITLE_FONT = getFullKey("TipTitleFont");
    private static final String USE_SEPARATE_FONTS = getFullKey("UseSeparateFonts");
    private static final String USE_SNAP_SCROLLING = getFullKey("UseSnapScrolling");
    private static final String USE_VANILLA_COMPENDIUM = getFullKey("UseVanillaCompendium");
    private static final String[] FONT_EXTS = EUIUtils.array("otf", "ttf", "fnt");
    private static int counter;
    protected static BasemodSettingsPage settingsBlock;
    protected static ModPanel panel;
    public static ExtraModSettingsPanel.Category effekseerCategory;
    public static ExtraModSettingsPanel.Category fontCategory;
    public static STSConfigItem<Boolean> disableCompendiumButton = new STSConfigItem<>(DISABLE_COMPENDIUM_BUTTON, false);
    public static STSConfigItem<Boolean> disableEffekseer = new STSConfigItem<>(DISABLE_EFFEKSEER, false);
    public static STSConfigItem<Boolean> enableDescriptionIcons = new STSConfigItem<>(ENABLE_DESCRIPTION_ICONS, false);
    public static STSConfigItem<Boolean> enableCardPoolDebug = new STSConfigItem<>(ENABLE_CARD_POOL_DEBUG, false);
    public static STSConfigItem<Boolean> flushOnGameStart = new STSConfigItem<>(FLUSH_ON_GAME_START, false);
    public static STSConfigItem<Boolean> flushOnRoomStart = new STSConfigItem<>(FLUSH_ON_ROOM_START, false);
    public static STSConfigItem<Boolean> overrideGameFont = new STSConfigItem<>(OVERRIDE_GAME_FONT, false);
    public static STSConfigItem<Boolean> showCountingPanel = new STSConfigItem<Boolean>(SHOW_COUNTING_PANEL, false);
    public static STSConfigItem<Boolean> showModSettings = new STSConfigItem<>(SHOW_MOD_SETTINGS, false);
    public static STSConfigItem<Boolean> useSeparateFonts = new STSConfigItem<>(USE_SEPARATE_FONTS, false);
    public static STSConfigItem<Boolean> useSnapScrolling = new STSConfigItem<>(USE_SNAP_SCROLLING, false);
    public static STSConfigItem<Boolean> useVanillaCompendium = new STSConfigItem<>(USE_VANILLA_COMPENDIUM, false);
    public static STSStringConfigItem bannerFont = new STSStringConfigItem(BANNER_FONT, "");
    public static STSStringConfigItem buttonFont = new STSStringConfigItem(BUTTON_FONT, "");
    public static STSStringConfigItem cardDescFont = new STSStringConfigItem(CARD_DESC_FONT, "");
    public static STSStringConfigItem cardTitleFont = new STSStringConfigItem(CARD_TITLE_FONT, "");
    public static STSStringConfigItem energyFont = new STSStringConfigItem(ENERGY_FONT, "");
    public static STSStringConfigItem lastExportPath = new STSStringConfigItem(LAST_EXPORT_PATH, "");
    public static STSStringConfigItem tipDescFont = new STSStringConfigItem(TIP_DESC_FONT, "");
    public static STSStringConfigItem tipTitleFont = new STSStringConfigItem(TITLE_TITLE_FONT, "");
    public static boolean shouldReloadEffekseer;
    public static final int BASE_OPTION_OFFSET_X = 400;
    public static final int BASE_OPTION_OFFSET_X2 = 580;
    public static final int BASE_OPTION_OFFSET_Y = 720;
    public static final int BASE_OPTION_OPTION_HEIGHT = 32;
    public static final int BASE_SPRITES_DEFAULT = 6000;

    //public static STSConfigurationOption<Integer> MaxParticles = new STSConfigurationOption<Integer>(GetFullKey("MaxParticles"), BASE_SPRITES_DEFAULT);

    protected static int addGenericElement(int page, EUIHoverable renderable, int ypos) {
        settingsBlock.addUIElement(page, renderable);
        return (int) (ypos - renderable.hb.height);
    }

    protected static int addSlider(int page, STSConfigItem<Integer> option, String label, int ypos, int min, int max) {
        settingsBlock.addUIElement(page, new ModMinMaxSlider(label, BASE_OPTION_OFFSET_X, ypos, min, max, option.get(), "%d", panel, (c) -> {
            option.set(MathUtils.round(c.getValue()));
            shouldReloadEffekseer = true;
        }));
        return ypos - BASE_OPTION_OPTION_HEIGHT;
    }

    protected static int addToggle(int page, STSConfigItem<Boolean> option, int ypos, String label) {
        return addToggle(page, option, label, ypos, null);
    }

    protected static int addToggle(int page, STSConfigItem<Boolean> option, String label, int ypos, String tip) {
        settingsBlock.addUIElement(page, new ModLabeledToggleButton(label, tip, BASE_OPTION_OFFSET_X, ypos, Settings.CREAM_COLOR.cpy(), EUIFontHelper.cardDescriptionFontNormal, option.get(), panel, (__) -> {
        }, (c) -> option.set(c.enabled)));
        return ypos - BASE_OPTION_OPTION_HEIGHT;
    }

    public static String getFullKey(String base) {
        return PREFIX + "_" + base;
    }

    public static boolean getIsTipDescriptionHidden(String id) {
        verifyHideTipsList();
        return tips.contains(id);
    }

    public static boolean getIsTipDescriptionHiddenByName(String name) {
        EUITooltip tip = EUIKeywordTooltip.findByName(name);
        return tip != null && getIsTipDescriptionHidden(tip.ID);
    }

    public static void hideTipDescription(String id, boolean value, boolean flush) {
        verifyHideTipsList();

        if (value) {
            if (id != null) {
                tips.add(id);
            }
        }
        else {
            tips.remove(id);
        }

        config.setString(HIDE_TIP_DESCRIPTION, EUIUtils.joinStrings("|", tips));

        if (flush) {
            save();
        }

        EUIKeywordTooltip.setHideTooltip(id, value);
    }

    public static void load() {
        try {
            config = new SpireConfig(PREFIX, PREFIX);
            showCountingPanel.addConfig(config);
            useVanillaCompendium.addConfig(config);
            disableCompendiumButton.addConfig(config);
            enableDescriptionIcons.addConfig(config);
            disableEffekseer.addConfig(config);
            flushOnGameStart.addConfig(config);
            flushOnRoomStart.addConfig(config);
            showModSettings.addConfig(config);
            useSnapScrolling.addConfig(config);
            useSeparateFonts.addConfig(config);
            overrideGameFont.addConfig(config);
            cardDescFont.addConfig(config);
            cardTitleFont.addConfig(config);
            tipDescFont.addConfig(config);
            tipTitleFont.addConfig(config);
            buttonFont.addConfig(config);
            bannerFont.addConfig(config);
            energyFont.addConfig(config);
            enableCardPoolDebug.addConfig(config);
            lastExportPath.addConfig(config);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static EUILabel makeModLabel(ExtraModSettingsPanel.Category category, String label, BitmapFont font) {
        return ExtraModSettingsPanel.addLabel(category, label, font);
    }

    protected static ModSettingsPathSelector makeModPathSelection(ExtraModSettingsPanel.Category category, STSConfigItem<String> option, String label, String... exts) {
        return ExtraModSettingsPanel.addPathSelection(category, option, label, exts);
    }

    protected static ModSettingsToggle makeModToggle(ExtraModSettingsPanel.Category category, STSConfigItem<Boolean> option, String label) {
        return ExtraModSettingsPanel.addBoolean(category, option, label);
    }

    protected static ModSettingsToggle makeModToggle(ExtraModSettingsPanel.Category category, STSConfigItem<Boolean> option, String label, String tip) {
        ModSettingsToggle toggle = makeModToggle(category, option, label);
        if (toggle != null) {
            toggle.setTooltip(label, tip);
            toggle.tooltip.setAutoWidth();
        }
        return toggle;
    }

    public static void postInitialize() {
        settingsBlock = new BasemodSettingsPage();
        panel = new ModPanel();
        panel.addUIElement(settingsBlock);

        // Add EUI options
        effekseerCategory = new ExtraModSettingsPanel.Category(EUIRM.strings.misc_effekseerSettings);
        fontCategory = new ExtraModSettingsPanel.Category(EUIRM.strings.misc_fontSettings);

        ExtraModSettingsPanel.addCategory(effekseerCategory);
        ExtraModSettingsPanel.addCategory(fontCategory);
        makeModToggle(effekseerCategory, showCountingPanel, EUIRM.strings.config_showCountingPanel, EUIRM.strings.configdesc_showCountingPanel);
        makeModToggle(effekseerCategory, useSnapScrolling, EUIRM.strings.config_useSnapScrolling, EUIRM.strings.configdesc_useSnapScrolling);
        makeModToggle(effekseerCategory, disableCompendiumButton, EUIRM.strings.config_disableCompendiumButton, EUIRM.strings.configdesc_disableCompendiumButton);
        makeModToggle(effekseerCategory, disableEffekseer, EUIRM.strings.config_disableEffekseer, EUIRM.strings.configdesc_disableEffekseer);
        makeModToggle(effekseerCategory, enableDescriptionIcons, EUIRM.strings.config_enableDescriptionIcons, EUIRM.strings.configdesc_enableDescriptionIcons);
        makeModToggle(effekseerCategory, flushOnGameStart, EUIRM.strings.config_flushOnGameStart, EUIRM.strings.configdesc_flushEffekseer);
        makeModToggle(effekseerCategory, flushOnRoomStart, EUIRM.strings.config_flushOnRoomStart, EUIRM.strings.configdesc_flushEffekseer);
        makeModToggle(fontCategory, useSeparateFonts, EUIRM.strings.config_useSeparateFonts, EUIRM.strings.configdesc_useSeparateFonts + EUIUtils.SPLIT_LINE + EUIRM.strings.configdesc_restartRequired);
        makeModToggle(fontCategory, overrideGameFont, EUIRM.strings.config_overrideGameFont, EUIRM.strings.configdesc_overrideGameFont + EUIUtils.SPLIT_LINE + EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector cardDescFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, cardDescFont, EUIRM.strings.config_cardDescFont, FONT_EXTS).setTooltip(EUIRM.strings.config_cardDescFont, EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector cardTitleFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, cardTitleFont, EUIRM.strings.config_cardTitleFont, FONT_EXTS).setTooltip(EUIRM.strings.config_cardTitleFont, EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector tipDescFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, tipDescFont, EUIRM.strings.config_tipDescFont, FONT_EXTS).setTooltip(EUIRM.strings.config_tipDescFont, EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector tipTitleFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, tipTitleFont, EUIRM.strings.config_tipTitleFont, FONT_EXTS).setTooltip(EUIRM.strings.config_tipTitleFont, EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector buttonFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, buttonFont, EUIRM.strings.config_buttonFont, FONT_EXTS).setTooltip(EUIRM.strings.config_buttonFont, EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector bannerFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, bannerFont, EUIRM.strings.config_bannerFont, FONT_EXTS).setTooltip(EUIRM.strings.config_bannerFont, EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector energyFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, energyFont, EUIRM.strings.config_energyFont, FONT_EXTS).setTooltip(EUIRM.strings.config_energyFont, EUIRM.strings.configdesc_restartRequired);


        // Add basemod options
        int yPos = BASE_OPTION_OFFSET_Y;

        yPos = addToggle(0, showCountingPanel, EUIRM.strings.config_showCountingPanel, yPos, EUIRM.strings.configdesc_showCountingPanel);
        yPos = addToggle(0, useVanillaCompendium, EUIRM.strings.config_useVanillaCompendium, yPos, EUIRM.strings.configdesc_useVanillaCompendium);
        yPos = addToggle(0, useSnapScrolling, EUIRM.strings.config_useSnapScrolling, yPos, EUIRM.strings.configdesc_useSnapScrolling);
        yPos = addToggle(0, disableCompendiumButton, EUIRM.strings.config_disableCompendiumButton, yPos, EUIRM.strings.configdesc_disableCompendiumButton);
        yPos = addToggle(0, disableEffekseer, EUIRM.strings.config_disableEffekseer, yPos, EUIRM.strings.configdesc_disableEffekseer);
        yPos = addToggle(0, enableDescriptionIcons, EUIRM.strings.config_enableDescriptionIcons, yPos, EUIRM.strings.configdesc_enableDescriptionIcons);
        yPos = addToggle(0, flushOnGameStart, EUIRM.strings.config_flushOnGameStart, yPos, EUIRM.strings.configdesc_flushEffekseer);
        yPos = addToggle(0, flushOnRoomStart, EUIRM.strings.config_flushOnRoomStart, yPos, EUIRM.strings.configdesc_flushEffekseer);
        yPos = addToggle(0, showModSettings, EUIRM.strings.config_showModSettings, yPos, EUIRM.strings.configdesc_showModSettings);
        yPos = addToggle(0, enableCardPoolDebug, EUIRM.strings.config_enableDebug, yPos, EUIRM.strings.configdesc_enableDebug);

        yPos = BASE_OPTION_OFFSET_Y;
        yPos = addToggle(1, useSeparateFonts, EUIRM.strings.config_useSeparateFonts, yPos, EUIRM.strings.configdesc_useSeparateFonts + EUIUtils.LEGACY_DOUBLE_SPLIT_LINE + EUIRM.strings.configdesc_restartRequired);
        yPos = addToggle(1, overrideGameFont, EUIRM.strings.config_overrideGameFont, yPos, EUIRM.strings.configdesc_overrideGameFont + EUIUtils.LEGACY_DOUBLE_SPLIT_LINE + EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector cardDescFontSelector2 = (ModSettingsPathSelector) cardDescFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(1, cardDescFontSelector2, yPos) + 2;
        ModSettingsPathSelector cardTitleFontSelector2 = (ModSettingsPathSelector) cardTitleFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(1, cardTitleFontSelector2, yPos) + 2;
        ModSettingsPathSelector tipDescFontSelector2 = (ModSettingsPathSelector) tipDescFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(1, tipDescFontSelector2, yPos) + 2;
        ModSettingsPathSelector tipTitleFontSelector2 = (ModSettingsPathSelector) tipTitleFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(1, tipTitleFontSelector2, yPos) + 2;
        ModSettingsPathSelector buttonFontSelector2 = (ModSettingsPathSelector) buttonFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(1, buttonFontSelector2, yPos) + 2;
        ModSettingsPathSelector bannerFontSelector2 = (ModSettingsPathSelector) bannerFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(1, buttonFontSelector2, yPos) + 2;
        ModSettingsPathSelector energyFontSelector2 = (ModSettingsPathSelector) energyFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(1, buttonFontSelector2, yPos) + 2;
        BaseMod.registerModBadge(ImageMaster.loadImage("images/extendedui/modBadge.png"), PREFIX, "PinaColada, EatYourBeetS", "", panel);

        // Sub-font settings should only show up if UseSeparateFonts is true
        // Toggling the fonts icon requires us to update the visibility of all font selectors
        useSeparateFonts.addListener(newValue -> {
            bannerFontSelector.setActive(newValue);
            bannerFontSelector2.setActive(newValue);
            buttonFontSelector.setActive(newValue);
            buttonFontSelector2.setActive(newValue);
            energyFontSelector.setActive(newValue);
            energyFontSelector2.setActive(newValue);
            tipDescFontSelector.setActive(newValue);
            tipDescFontSelector2.setActive(newValue);
            tipTitleFontSelector.setActive(newValue);
            tipTitleFontSelector2.setActive(newValue);

            cardDescFontSelector.tooltip.setTitle(newValue ? EUIRM.strings.config_cardDescFont : EUIRM.strings.config_mainFont);
            cardDescFontSelector2.tooltip.setTitle(newValue ? EUIRM.strings.config_cardDescFont : EUIRM.strings.config_mainFont);
            cardTitleFontSelector.tooltip.setTitle(newValue ? EUIRM.strings.config_cardTitleFont : EUIRM.strings.config_boldFont);
            cardTitleFontSelector2.tooltip.setTitle(newValue ? EUIRM.strings.config_cardTitleFont : EUIRM.strings.config_boldFont);
            cardDescFontSelector.setHeaderText(cardDescFontSelector.tooltip.title);
            cardDescFontSelector2.setHeaderText(cardDescFontSelector2.tooltip.title);
            cardTitleFontSelector.setHeaderText(cardTitleFontSelector.tooltip.title);
            cardTitleFontSelector2.setHeaderText(cardTitleFontSelector2.tooltip.title);
        });

        // NOTE: DISABLE_COMPENDIUM_BUTTON, HIDE_TIP_DESCRIPTION, and USE_SMOOTH_SCROLLING listeners are added in EUI.initialize to avoid errors from initializing too early
    }

    public static void save() {
        try {
            config.save();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean triggerOnFirstView(STSConfigItem<Boolean> configItem) {
        if (!configItem.get()) {
            configItem.set(true);
            return true;
        }
        return false;
    }

    public static void verifyHideTipsList() {
        if (tips == null) {
            tips = new HashSet<>();

            if (config.has(HIDE_TIP_DESCRIPTION)) {
                Collections.addAll(tips, config.getString(HIDE_TIP_DESCRIPTION).split("\\|"));
            }
        }
    }
}
