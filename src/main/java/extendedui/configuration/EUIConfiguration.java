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
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.settings.ModSettingsPathSelector;
import extendedui.ui.settings.ModSettingsScreen;
import extendedui.ui.settings.ModSettingsToggle;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class EUIConfiguration
{
    private static final int BASE_OPTION_OFFSET_X = 400;
    private static final int BASE_OPTION_OFFSET_X2 = 580;
    private static final int BASE_OPTION_OFFSET_Y = 700;
    private static final int BASE_OPTION_OPTION_HEIGHT = 32;
    public static final int BASE_SPRITES_DEFAULT = 6000;
    //public static final int BASE_SPRITES_MIN = 6000;
    //public static final int BASE_SPRITES_MAX = 6000;
    private static final String[] FONT_EXTS = EUIUtils.array("otf", "ttf", "fnt");
    private static final String PREFIX = "EUI";
    private static SpireConfig config;
    private static int counter;
    public static ModSettingsScreen.Category effekseerCategory;
    public static ModSettingsScreen.Category fontCategory;
    public static boolean shouldReloadEffekseer;

    public static String getFullKey(String base) {
        return PREFIX + "_" + base;
    }

    private static final String USE_VANILLA_COMPENDIUM = getFullKey("UseVanillaCompendium");
    private static final String DISABLE_DESCRIPTION_ICONS = getFullKey("DisableDescriptionIcons");
    private static final String DISABLE_EFFEKSEER = getFullKey("DisableEffekseer");
    private static final String FLUSH_ON_GAME_START = getFullKey("FlushOnGameStart");
    private static final String FLUSH_ON_ROOM_START = getFullKey("FlushOnRoomStart");
    private static final String SHOW_MOD_SETTINGS = getFullKey("ShowModSettings");
    private static final String USE_SEPARATE_FONTS = getFullKey("UseSeparateFonts");
    private static final String OVERRIDE_GAME_FONT = getFullKey("OverrideGameFont");
    private static final String HIDE_TIP_DESCRIPTION = getFullKey("HideTipDescription");
    private static final String CARD_DESC_FONT = getFullKey("CardDescFont");
    private static final String CARD_TITLE_FONT = getFullKey("CardTitleFont");
    private static final String TIP_DESC_FONT = getFullKey("TipDescFont");
    private static final String TITLE_TITLE_FONT = getFullKey("TipTitleFont");
    private static final String BUTTON_FONT = getFullKey("ButtonFont");
    private static final String BANNER_FONT = getFullKey("BannerFont");
    private static final String ENERGY_FONT = getFullKey("EnergyFont");

    public static STSConfigItem<Boolean> useVanillaCompendium = new STSConfigItem<>(USE_VANILLA_COMPENDIUM, false);
    public static STSConfigItem<Boolean> disableDescrptionIcons = new STSConfigItem<>(DISABLE_DESCRIPTION_ICONS, false);
    public static STSConfigItem<Boolean> disableEffekseer = new STSConfigItem<>(DISABLE_EFFEKSEER, false);
    public static STSConfigItem<Boolean> flushOnGameStart = new STSConfigItem<>(FLUSH_ON_GAME_START, false);
    public static STSConfigItem<Boolean> flushOnRoomStart = new STSConfigItem<>(FLUSH_ON_ROOM_START, false);
    public static STSConfigItem<Boolean> showModSettings = new STSConfigItem<>(SHOW_MOD_SETTINGS, false);
    public static STSConfigItem<Boolean> useSeparateFonts = new STSConfigItem<>(USE_SEPARATE_FONTS, false);
    public static STSConfigItem<Boolean> overrideGameFont = new STSConfigItem<>(OVERRIDE_GAME_FONT, false);

    public static STSStringConfigItem cardDescFont = new STSStringConfigItem(CARD_DESC_FONT,"");
    public static STSStringConfigItem cardTitleFont = new STSStringConfigItem(CARD_TITLE_FONT,"");
    public static STSStringConfigItem tipDescFont = new STSStringConfigItem(TIP_DESC_FONT,"");
    public static STSStringConfigItem tipTitleFont = new STSStringConfigItem(TITLE_TITLE_FONT,"");
    public static STSStringConfigItem buttonFont = new STSStringConfigItem(BUTTON_FONT,"");
    public static STSStringConfigItem bannerFont = new STSStringConfigItem(BANNER_FONT,"");
    public static STSStringConfigItem energyFont = new STSStringConfigItem(ENERGY_FONT,"");

    //public static STSConfigurationOption<Integer> MaxParticles = new STSConfigurationOption<Integer>(GetFullKey("MaxParticles"), BASE_SPRITES_DEFAULT);

    private static HashSet<String> tips = null;

    public static void load() {
        try
        {
            config = new SpireConfig(PREFIX, PREFIX);
            useVanillaCompendium.addConfig(config);
            disableDescrptionIcons.addConfig(config);
            disableEffekseer.addConfig(config);
            flushOnGameStart.addConfig(config);
            flushOnRoomStart.addConfig(config);
            showModSettings.addConfig(config);
            useSeparateFonts.addConfig(config);
            overrideGameFont.addConfig(config);
            cardDescFont.addConfig(config);
            cardTitleFont.addConfig(config);
            tipDescFont.addConfig(config);
            tipTitleFont.addConfig(config);
            buttonFont.addConfig(config);
            bannerFont.addConfig(config);
            energyFont.addConfig(config);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void postInitialize()
    {

        // Add EUI options
        effekseerCategory = new ModSettingsScreen.Category(EUIRM.strings.miscEffekseersettings);
        fontCategory = new ModSettingsScreen.Category(EUIRM.strings.miscFontsettings);
        ModSettingsScreen.addCategory(effekseerCategory);
        ModSettingsScreen.addCategory(fontCategory);
        makeModToggle(effekseerCategory, disableDescrptionIcons, EUIRM.strings.configDisableDescriptionIcons);
        makeModToggle(effekseerCategory, disableEffekseer, EUIRM.strings.configDisableEffekseer);
        makeModToggle(effekseerCategory, flushOnGameStart, EUIRM.strings.configFlushOnGameStart);
        makeModToggle(effekseerCategory, flushOnRoomStart, EUIRM.strings.configFlushOnRoomStart);
        makeModToggle(fontCategory, useSeparateFonts, EUIRM.strings.configUseSeparateFonts).setTooltip(EUIRM.strings.configUseSeparateFonts, EUIRM.strings.miscRestartrequired);
        makeModToggle(fontCategory, overrideGameFont, EUIRM.strings.configOverridegamefont).setTooltip(EUIRM.strings.configOverridegamefont,EUIRM.strings.miscFontdescription + " | " + EUIRM.strings.miscRestartrequired);
        ModSettingsPathSelector cardDescFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, cardDescFont, EUIRM.strings.configCarddescfont, FONT_EXTS).setTooltip(EUIRM.strings.configCarddescfont, EUIRM.strings.miscRestartrequired);
        ModSettingsPathSelector cardTitleFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, cardTitleFont, EUIRM.strings.configCardtitlefont, FONT_EXTS).setTooltip(EUIRM.strings.configCardtitlefont, EUIRM.strings.miscRestartrequired);
        ModSettingsPathSelector tipDescFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, tipDescFont, EUIRM.strings.configTipdescfont, FONT_EXTS).setTooltip(EUIRM.strings.configTipdescfont, EUIRM.strings.miscRestartrequired);
        ModSettingsPathSelector tipTitleFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, tipTitleFont, EUIRM.strings.configTiptitlefont, FONT_EXTS).setTooltip(EUIRM.strings.configTiptitlefont, EUIRM.strings.miscRestartrequired);
        ModSettingsPathSelector buttonFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, buttonFont, EUIRM.strings.configButtonfont, FONT_EXTS).setTooltip(EUIRM.strings.configButtonfont, EUIRM.strings.miscRestartrequired);
        ModSettingsPathSelector bannerFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, bannerFont, EUIRM.strings.configBannerFont, FONT_EXTS).setTooltip(EUIRM.strings.configBannerFont, EUIRM.strings.miscRestartrequired);
        ModSettingsPathSelector energyFontSelector = (ModSettingsPathSelector) makeModPathSelection(fontCategory, energyFont, EUIRM.strings.configEnergyFont, FONT_EXTS).setTooltip(EUIRM.strings.configEnergyFont, EUIRM.strings.miscRestartrequired);


        // Add basemod options
        int yPos = BASE_OPTION_OFFSET_Y;
        ModPanel panel = new ModPanel();

        yPos = addToggle(panel, useVanillaCompendium, EUIRM.strings.configUseVanillaCompendium, yPos);
        yPos = addToggle(panel, disableDescrptionIcons, EUIRM.strings.configDisableDescriptionIcons, yPos);
        yPos = addToggle(panel, disableEffekseer, EUIRM.strings.configDisableEffekseer, yPos);
        yPos = addToggle(panel, flushOnGameStart, EUIRM.strings.configFlushOnGameStart, yPos);
        yPos = addToggle(panel, flushOnRoomStart, EUIRM.strings.configFlushOnRoomStart, yPos);
        yPos = addToggle(panel, showModSettings, EUIRM.strings.configShowModSettings, yPos);
        yPos = addToggle(panel, useSeparateFonts, EUIRM.strings.configUseSeparateFonts, yPos, EUIRM.strings.miscRestartrequired);
        yPos = addToggle(panel, overrideGameFont, EUIRM.strings.configOverridegamefont, yPos, EUIRM.strings.miscFontdescription + " NL NL " + EUIRM.strings.miscRestartrequired);

        yPos = (BASE_OPTION_OFFSET_Y + yPos) / 2;
        ModSettingsPathSelector cardDescFontSelector2 = (ModSettingsPathSelector) cardDescFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(panel, cardDescFontSelector2, yPos);
        ModSettingsPathSelector cardTitleFontSelector2 = (ModSettingsPathSelector) cardTitleFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(panel, cardTitleFontSelector2, yPos);
        ModSettingsPathSelector tipDescFontSelector2 = (ModSettingsPathSelector) tipDescFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(panel, tipDescFontSelector2, yPos);
        ModSettingsPathSelector tipTitleFontSelector2 = (ModSettingsPathSelector) tipTitleFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(panel, tipTitleFontSelector2, yPos);
        ModSettingsPathSelector buttonFontSelector2 = (ModSettingsPathSelector) buttonFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(panel, buttonFontSelector2, yPos);
        ModSettingsPathSelector bannerFontSelector2 = (ModSettingsPathSelector) bannerFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(panel, buttonFontSelector2, yPos);
        ModSettingsPathSelector energyFontSelector2 = (ModSettingsPathSelector) energyFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(panel, buttonFontSelector2, yPos);
        BaseMod.registerModBadge(ImageMaster.loadImage("images/extendedui/modBadge.png"), PREFIX, "PinaColada, EatYourBeetS", "", panel);

        // Sub-font settings should only show up if UseSeparateFonts is true
        boolean showOtherSelectors = useSeparateFonts.get();
        cardDescFontSelector2.setHeaderText(showOtherSelectors ? EUIRM.strings.configCarddescfont : EUIRM.strings.configMainfont);
        cardTitleFontSelector.setActive(showOtherSelectors);
        tipDescFontSelector.setActive(showOtherSelectors);
        tipTitleFontSelector.setActive(showOtherSelectors);
        buttonFontSelector.setActive(showOtherSelectors);
        bannerFontSelector.setActive(showOtherSelectors);
        energyFontSelector.setActive(showOtherSelectors);
        cardTitleFontSelector2.setActive(showOtherSelectors);
        tipDescFontSelector2.setActive(showOtherSelectors);
        tipTitleFontSelector2.setActive(showOtherSelectors);
        buttonFontSelector2.setActive(showOtherSelectors);
        bannerFontSelector2.setActive(showOtherSelectors);
        energyFontSelector2.setActive(showOtherSelectors);
        useSeparateFonts.addListener(newValue -> {
            cardDescFontSelector2.setHeaderText(newValue ? EUIRM.strings.configCarddescfont : EUIRM.strings.configMainfont);
            cardTitleFontSelector.setActive(newValue);
            tipDescFontSelector.setActive(newValue);
            tipTitleFontSelector.setActive(newValue);
            buttonFontSelector.setActive(newValue);
            bannerFontSelector.setActive(newValue);
            energyFontSelector.setActive(newValue);
            cardTitleFontSelector2.setActive(newValue);
            tipDescFontSelector2.setActive(newValue);
            tipTitleFontSelector2.setActive(newValue);
            buttonFontSelector2.setActive(newValue);
            buttonFontSelector2.setActive(newValue);
            bannerFontSelector2.setActive(newValue);
            energyFontSelector2.setActive(newValue);
        });
    }

    public static void save() {
        try
        {
            config.save();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected static ModSettingsToggle makeModToggle(ModSettingsScreen.Category category, STSConfigItem<Boolean> option, String label)
    {
        return ModSettingsScreen.addBoolean(category, option, label);
    }

    protected static ModSettingsPathSelector makeModPathSelection(ModSettingsScreen.Category category, STSConfigItem<String> option, String label, String... exts)
    {
        return ModSettingsScreen.addPathSelection(category, option, label, exts);
    }

    protected static EUILabel makeModLabel(ModSettingsScreen.Category category, String label, BitmapFont font)
    {
        return ModSettingsScreen.addLabel(category, label, font);
    }

    protected static int addToggle(ModPanel panel, STSConfigItem<Boolean> option, String label, int ypos) {
        return addToggle(panel, option, label, ypos, null);
    }

    protected static int addToggle(ModPanel panel, STSConfigItem<Boolean> option, String label, int ypos, String tip) {
        panel.addUIElement(new ModLabeledToggleButton(label, tip, BASE_OPTION_OFFSET_X, ypos, Settings.CREAM_COLOR.cpy(), EUIFontHelper.carddescriptionfontNormal, option.get(), panel, (__) -> {
        }, (c) -> option.set(c.enabled, true)));
        return ypos - BASE_OPTION_OPTION_HEIGHT;
    }

    protected static int addSlider(ModPanel panel, STSConfigItem<Integer> option, String label, int ypos, int min, int max) {
        panel.addUIElement(new ModMinMaxSlider(label, BASE_OPTION_OFFSET_X, ypos, min, max, option.get(), "%d", panel, (c) -> {
            option.set(MathUtils.round(c.getValue()), true);
            shouldReloadEffekseer = true;
        }));
        return ypos - BASE_OPTION_OPTION_HEIGHT;
    }

    protected static int addGenericElement(ModPanel panel, EUIHoverable renderable, int ypos)
    {
        panel.addUIElement(renderable);
        return (int) (ypos - renderable.hb.height);
    }

    public static boolean hideTipDescription(String id)
    {
        if (tips == null)
        {
            tips = new HashSet<>();

            if (config.has(HIDE_TIP_DESCRIPTION))
            {
                Collections.addAll(tips, config.getString(HIDE_TIP_DESCRIPTION).split("\\|"));
            }
        }

        return tips.contains(id);
    }

    public static void hideTipDescription(String id, boolean value, boolean flush)
    {
        if (tips == null)
        {
            tips = new HashSet<>();
        }

        if (value)
        {
            if (id != null)
            {
                tips.add(id);
            }
        }
        else
        {
            tips.remove(id);
        }

        config.setString(HIDE_TIP_DESCRIPTION, EUIUtils.joinStrings("|", tips));

        if (flush)
        {
            save();
        }
    }
}
