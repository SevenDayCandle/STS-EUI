package extendedui.configuration;

import basemod.BaseMod;
import basemod.ModMinMaxSlider;
import basemod.ModPanel;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.utilities.EUITextHelper;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.settings.BasemodSettingsPage;
import extendedui.ui.settings.ExtraModSettingsPanel;
import extendedui.ui.settings.ModSettingsPathSelector;
import extendedui.ui.settings.ModSettingsToggle;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class EUIConfiguration {

    // @Formatter: Off
    private static final String PREFIX = "EUI";
    private static final String[] FONT_EXTS = EUIUtils.array("otf", "ttf", "ttc", "fnt");
    private static final int BASE_OPTION_OFFSET_X = 380;
    private static final int BASE_OPTION_OFFSET_Y = 720;
    // @Formatter: On
    private static HashSet<String> tips = null;
    private static SpireConfig config;
    private static int counter;
    private static BasemodSettingsPage settingsBlock;
    private static EUITooltip reenableTip;
    private static ModPanel panel;
    public static STSConfigItem<Boolean> disableCompendiumButton = new STSConfigItem<>(getFullKey("DisableCompendiumButton"), false);
    public static STSConfigItem<Boolean> disableEffekseer = new STSConfigItem<>(getFullKey("DisableEffekseer"), false);
    public static STSConfigItem<Boolean> enableCardPoolDebug = new STSConfigItem<>(getFullKey("EnableCardPoolDebug"), false);
    public static STSConfigItem<Boolean> enableDescriptionIcons = new STSConfigItem<>(getFullKey("EnableDescriptionIcons"), false);
    public static STSConfigItem<Boolean> enableExpandTooltips = new STSConfigItem<>(getFullKey("EnableExpandTooltips"), false);
    public static STSConfigItem<Boolean> flushOnGameStart = new STSConfigItem<>(getFullKey("FlushOnGameStart"), true);
    public static STSConfigItem<Boolean> flushOnRoomStart = new STSConfigItem<>(getFullKey("FlushOnRoomStart"), false);
    public static STSConfigItem<Boolean> forceLinearFiltering = new STSConfigItem<>(getFullKey("ForceLinearFiltering"), true);
    public static STSConfigItem<Boolean> hideInfo = new STSConfigItem<>(getFullKey("HideInfo"), false);
    public static STSConfigItem<Boolean> instantFade = new STSConfigItem<>(getFullKey("InstantFade"), false);
    public static STSConfigItem<Boolean> overrideGameFont = new STSConfigItem<>(getFullKey("OverrideGameFont"), false);
    public static STSConfigItem<Boolean> saveFilterChoices = new STSConfigItem<Boolean>(getFullKey("SaveFilterChoices"), false);
    public static STSConfigItem<Boolean> showCountingPanel = new STSConfigItem<Boolean>(getFullKey("ShowCountingPanel"), false);
    public static STSConfigItem<Boolean> showModSettings = new STSConfigItem<>(getFullKey("ShowModSettings"), false);
    public static STSConfigItem<Boolean> useEUITooltips = new STSConfigItem<>(getFullKey("UseEUITooltips"), false);
    public static STSConfigItem<Boolean> useSeparateFonts = new STSConfigItem<>(getFullKey("UseSeparateFonts"), false);
    public static STSConfigItem<Boolean> useVanillaCompendium = new STSConfigItem<>(getFullKey("UseVanillaCompendium"), false);
    public static STSStringConfigItem bannerFont = new STSStringConfigItem(getFullKey("BannerFont"), "");
    public static STSStringConfigItem buttonFont = new STSStringConfigItem(getFullKey("ButtonFont"), "");
    public static STSStringConfigItem cardDescFont = new STSStringConfigItem(getFullKey("CardDescFont"), "");
    public static STSStringConfigItem cardTitleFont = new STSStringConfigItem(getFullKey("CardTitleFont"), "");
    public static STSStringConfigItem energyFont = new STSStringConfigItem(getFullKey("EnergyFont"), "");
    public static STSStringConfigItem lastExportPath = new STSStringConfigItem(getFullKey("LastExportPath"), "");
    public static STSStringConfigItem tipDescFont = new STSStringConfigItem(getFullKey("TipDescFont"), "");
    public static STSStringConfigItem tipTitleFont = new STSStringConfigItem(getFullKey("TipTitleFont"), "");
    public static ExtraModSettingsPanel.Category effekseerCategory;
    public static ExtraModSettingsPanel.Category euiCategory;
    public static ExtraModSettingsPanel.Category fontCategory;
    public static boolean shouldReloadEffekseer;

    //public static STSConfigurationOption<Integer> MaxParticles = new STSConfigurationOption<Integer>(GetFullKey("MaxParticles"), BASE_SPRITES_DEFAULT);

    private static float addGenericElement(int page, EUIHoverable renderable, float ypos) {
        settingsBlock.addUIElement(page, renderable);
        return (ypos - renderable.hb.height);
    }

    private static float addSlider(int page, STSConfigItem<Integer> option, String label, float ypos, int min, int max) {
        settingsBlock.addUIElement(page, new ModMinMaxSlider(label, BASE_OPTION_OFFSET_X, ypos, min, max, option.get(), "%d", panel, (c) -> {
            option.set(MathUtils.round(c.getValue()));
        }));
        return ypos - ExtraModSettingsPanel.OPTION_SIZE;
    }

    private static float addToggle(int page, STSConfigItem<Boolean> option, float ypos, String label) {
        return addToggle(page, option, label, ypos, null);
    }

    private static float addToggle(int page, STSConfigItem<Boolean> option, String label, float ypos, String tip) {
        settingsBlock.addUIElement(page, createToggle(option, label, ypos, tip));
        return ypos - ExtraModSettingsPanel.OPTION_SIZE;
    }

    public static boolean canSkipFade() {
        return instantFade.get();
    }

    public static void clearHiddenTips(boolean flush) {
        tips = new HashSet<>();
        config.setString(getFullKey("HideTipDescription"), EUIUtils.joinStrings("|", tips));

        if (flush) {
            save();
        }

        EUIKeywordTooltip.clearHidden();
        updateReenableTooltip();
    }

    public static ModSettingsToggle createToggle(STSConfigItem<Boolean> option, String label, float ypos, String tip) {
        float baseWidth = EUITextHelper.getSmartWidth(FontHelper.cardDescFont_N, label);
        ModSettingsToggle toggle = new ModSettingsToggle(new EUIHitbox(BASE_OPTION_OFFSET_X * Settings.scale, ypos, ExtraModSettingsPanel.OPTION_SIZE * 2f + baseWidth, ExtraModSettingsPanel.OPTION_SIZE), option, label);
        toggle.setTooltip(new EUITooltip(label, tip));
        toggle.tooltip.setAutoWidth();
        return toggle;
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

        config.setString(getFullKey("HideTipDescription"), EUIUtils.joinStrings("|", tips));

        if (flush) {
            save();
        }

        EUIKeywordTooltip.setHideTooltip(id, value);
        updateReenableTooltip();
    }

    public static void load() {
        try {
            config = new SpireConfig(PREFIX, PREFIX);
            showCountingPanel.addConfig(config);
            useVanillaCompendium.addConfig(config);
            disableCompendiumButton.addConfig(config);
            disableEffekseer.addConfig(config);
            enableDescriptionIcons.addConfig(config);
            enableExpandTooltips.addConfig(config);
            flushOnGameStart.addConfig(config);
            flushOnRoomStart.addConfig(config);
            forceLinearFiltering.addConfig(config);
            hideInfo.addConfig(config);
            instantFade.addConfig(config);
            saveFilterChoices.addConfig(config);
            showModSettings.addConfig(config);
            useEUITooltips.addConfig(config);
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

    protected static EUIButton makeModButton(ExtraModSettingsPanel.Category category, String label, ActionT0 onClick) {
        return ExtraModSettingsPanel.addButton(category, label, onClick);
    }

    protected static EUIButton makeModButton(ExtraModSettingsPanel.Category category, String label, ActionT0 onClick, float off) {
        return ExtraModSettingsPanel.addButton(category, label, onClick, off);
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

        reenableTip = new EUITooltip(EUIRM.strings.config_reenableTooltips, EUIRM.strings.configdesc_reenableTooltips);

        // Add EUI options
        euiCategory = new ExtraModSettingsPanel.Category(EUIRM.strings.misc_euiSettings);
        effekseerCategory = new ExtraModSettingsPanel.Category(EUIRM.strings.misc_effekseerSettings);
        fontCategory = new ExtraModSettingsPanel.Category(EUIRM.strings.misc_fontSettings);

        ExtraModSettingsPanel.addCategory(euiCategory);
        ExtraModSettingsPanel.addCategory(fontCategory);
        ExtraModSettingsPanel.addCategory(effekseerCategory);
        makeModToggle(euiCategory, showCountingPanel, EUIRM.strings.config_showCountingPanel, EUIRM.strings.configdesc_showCountingPanel);
        makeModToggle(euiCategory, disableCompendiumButton, EUIRM.strings.config_disableCompendiumButton, EUIRM.strings.configdesc_disableCompendiumButton);
        makeModToggle(euiCategory, enableDescriptionIcons, EUIRM.strings.config_enableDescriptionIcons, EUIRM.strings.configdesc_enableDescriptionIcons);
        makeModToggle(euiCategory, enableExpandTooltips, EUIRM.strings.config_enableExpandTooltips, EUIRM.strings.configdesc_enableExpandTooltips);
        makeModToggle(euiCategory, useEUITooltips, EUIRM.strings.config_useEUITooltips, EUIRM.strings.configdesc_useEUITooltips);
        makeModToggle(euiCategory, saveFilterChoices, EUIRM.strings.config_saveFilterChoices, EUIRM.strings.configdesc_saveFilterChoices);
        makeModToggle(euiCategory, instantFade, EUIRM.strings.config_instantFade, EUIRM.strings.configdesc_instantFade);
        makeModToggle(euiCategory, hideInfo, EUIRM.strings.config_hideInfo, EUIRM.strings.configdesc_hideInfo);
        makeModToggle(effekseerCategory, disableEffekseer, EUIRM.strings.config_disableEffekseer, EUIRM.strings.configdesc_disableEffekseer);
        makeModToggle(effekseerCategory, flushOnGameStart, EUIRM.strings.config_flushOnGameStart, EUIRM.strings.configdesc_flushEffekseer);
        makeModToggle(effekseerCategory, flushOnRoomStart, EUIRM.strings.config_flushOnRoomStart, EUIRM.strings.configdesc_flushEffekseer);
        makeModToggle(fontCategory, useSeparateFonts, EUIRM.strings.config_useSeparateFonts, EUIRM.strings.configdesc_useSeparateFonts + EUIUtils.SPLIT_LINE + EUIRM.strings.configdesc_restartRequired);
        makeModToggle(fontCategory, forceLinearFiltering, EUIRM.strings.config_forceLinearFiltering, EUIRM.strings.configdesc_forceLinearFiltering + EUIUtils.SPLIT_LINE + EUIRM.strings.configdesc_restartRequired);
        EUIButton clearButton = makeModButton(euiCategory, EUIRM.strings.config_reenableTooltips, () -> clearHiddenTips(true), ExtraModSettingsPanel.OPTION_SIZE);
        clearButton.setTooltip(reenableTip);
        ModSettingsPathSelector cardDescFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, cardDescFont, EUIRM.strings.config_cardDescFont, FONT_EXTS).setTooltip(EUIRM.strings.config_cardDescFont, EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector cardTitleFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, cardTitleFont, EUIRM.strings.config_cardTitleFont, FONT_EXTS).setTooltip(EUIRM.strings.config_cardTitleFont, EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector tipDescFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, tipDescFont, EUIRM.strings.config_tipDescFont, FONT_EXTS).setTooltip(EUIRM.strings.config_tipDescFont, EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector tipTitleFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, tipTitleFont, EUIRM.strings.config_tipTitleFont, FONT_EXTS).setTooltip(EUIRM.strings.config_tipTitleFont, EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector buttonFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, buttonFont, EUIRM.strings.config_buttonFont, FONT_EXTS).setTooltip(EUIRM.strings.config_buttonFont, EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector bannerFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, bannerFont, EUIRM.strings.config_bannerFont, FONT_EXTS).setTooltip(EUIRM.strings.config_bannerFont, EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector energyFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, energyFont, EUIRM.strings.config_energyFont, FONT_EXTS).setTooltip(EUIRM.strings.config_energyFont, EUIRM.strings.configdesc_restartRequired);

        verifyHideTipsList();
        updateReenableTooltip();

        // Add basemod options
        float yPos = BASE_OPTION_OFFSET_Y * Settings.scale;

        yPos = addToggle(0, showCountingPanel, EUIRM.strings.config_showCountingPanel, yPos, EUIRM.strings.configdesc_showCountingPanel);
        yPos = addToggle(0, useVanillaCompendium, EUIRM.strings.config_useVanillaCompendium, yPos, EUIRM.strings.configdesc_useVanillaCompendium);
        yPos = addToggle(0, disableCompendiumButton, EUIRM.strings.config_disableCompendiumButton, yPos, EUIRM.strings.configdesc_disableCompendiumButton);
        yPos = addToggle(0, enableDescriptionIcons, EUIRM.strings.config_enableDescriptionIcons, yPos, EUIRM.strings.configdesc_enableDescriptionIcons);
        yPos = addToggle(0, enableExpandTooltips, EUIRM.strings.config_enableExpandTooltips, yPos, EUIRM.strings.configdesc_enableExpandTooltips);
        yPos = addToggle(0, useEUITooltips, EUIRM.strings.config_useEUITooltips, yPos, EUIRM.strings.configdesc_useEUITooltips);
        yPos = addToggle(0, saveFilterChoices, EUIRM.strings.config_saveFilterChoices, yPos, EUIRM.strings.configdesc_saveFilterChoices);
        yPos = addToggle(0, instantFade, EUIRM.strings.config_instantFade, yPos, EUIRM.strings.configdesc_instantFade);
        yPos = addToggle(0, hideInfo, EUIRM.strings.config_hideInfo, yPos, EUIRM.strings.configdesc_hideInfo);
        yPos = addToggle(0, showModSettings, EUIRM.strings.config_showModSettings, yPos, EUIRM.strings.configdesc_showModSettings);
        yPos = addToggle(0, enableCardPoolDebug, EUIRM.strings.config_enableDebug, yPos, EUIRM.strings.configdesc_enableDebug);

        float xPos = (BASE_OPTION_OFFSET_X + 40) * Settings.scale;
        EUIButton clearButton2 = new EUIButton(EUIRM.images.rectangularButton.texture(), new EUIHitbox(clearButton.hb))
                .setLabel(clearButton.label.text)
                .setOnClick(() -> clearHiddenTips(true));
        clearButton2.translate(xPos, yPos - ExtraModSettingsPanel.OPTION_SIZE * 2f);
        yPos = addGenericElement(0, clearButton2, clearButton2.hb.y);

        yPos = BASE_OPTION_OFFSET_Y * Settings.scale;
        yPos = addToggle(1, disableEffekseer, EUIRM.strings.config_disableEffekseer, yPos, EUIRM.strings.configdesc_disableEffekseer);
        yPos = addToggle(1, flushOnGameStart, EUIRM.strings.config_flushOnGameStart, yPos, EUIRM.strings.configdesc_flushEffekseer);
        yPos = addToggle(1, flushOnRoomStart, EUIRM.strings.config_flushOnRoomStart, yPos, EUIRM.strings.configdesc_flushEffekseer);

        xPos = (BASE_OPTION_OFFSET_X + 290) * Settings.scale;
        yPos = BASE_OPTION_OFFSET_Y * Settings.scale;
        yPos = addToggle(2, useSeparateFonts, EUIRM.strings.config_useSeparateFonts, yPos, EUIRM.strings.configdesc_useSeparateFonts + EUIUtils.LEGACY_DOUBLE_SPLIT_LINE + EUIRM.strings.configdesc_restartRequired);
        yPos = addToggle(2, forceLinearFiltering, EUIRM.strings.config_forceLinearFiltering, yPos, EUIRM.strings.configdesc_forceLinearFiltering + EUIUtils.LEGACY_DOUBLE_SPLIT_LINE + EUIRM.strings.configdesc_restartRequired);
        ModSettingsPathSelector cardDescFontSelector2 = new ModSettingsPathSelector(cardDescFontSelector).translate(xPos, yPos);
        yPos = addGenericElement(2, cardDescFontSelector2, yPos) + 2;
        ModSettingsPathSelector cardTitleFontSelector2 = new ModSettingsPathSelector(cardTitleFontSelector).translate(xPos, yPos);
        yPos = addGenericElement(2, cardTitleFontSelector2, yPos) + 2;
        ModSettingsPathSelector tipDescFontSelector2 = new ModSettingsPathSelector(tipDescFontSelector).translate(xPos, yPos);
        yPos = addGenericElement(2, tipDescFontSelector2, yPos) + 2;
        ModSettingsPathSelector tipTitleFontSelector2 = new ModSettingsPathSelector(tipTitleFontSelector).translate(xPos, yPos);
        yPos = addGenericElement(2, tipTitleFontSelector2, yPos) + 2;
        ModSettingsPathSelector buttonFontSelector2 = new ModSettingsPathSelector(buttonFontSelector).translate(xPos, yPos);
        yPos = addGenericElement(2, buttonFontSelector2, yPos) + 2;
        ModSettingsPathSelector bannerFontSelector2 = new ModSettingsPathSelector(bannerFontSelector).translate(xPos, yPos);
        yPos = addGenericElement(2, buttonFontSelector2, yPos) + 2;
        ModSettingsPathSelector energyFontSelector2 = new ModSettingsPathSelector(energyFontSelector).translate(xPos, yPos);
        yPos = addGenericElement(2, buttonFontSelector2, yPos) + 2;

        ModInfo euiInfo = EUIGameUtils.getModInfo(EUI.class);
        BaseMod.registerModBadge(ImageMaster.loadImage("images/extendedui/modBadge.png"), PREFIX, EUIUtils.joinStrings(",", euiInfo), "", panel);

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

        hideInfo.addListener(newValue -> {
            CardCrawlGame.displayVersion = !newValue;
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

    public static void updateReenableTooltip() {
        reenableTip.setDescription(tips == null || tips.isEmpty() ? EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, EUIRM.strings.configdesc_reenableTooltips, EUIRM.strings.configdesc_reenableTooltipsNone) : EUIRM.strings.configdesc_reenableTooltips);
    }

    public static void verifyHideTipsList() {
        if (tips == null) {
            tips = new HashSet<>();

            if (config.has(getFullKey("HideTipDescription"))) {
                Collections.addAll(tips, config.getString(getFullKey("HideTipDescription")).split("\\|"));
            }
        }
    }
}
