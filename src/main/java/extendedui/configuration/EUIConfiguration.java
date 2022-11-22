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
import extendedui.interfaces.listeners.STSConfigListener;
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
    public static ModSettingsScreen.Category EffekseerCategory;
    public static ModSettingsScreen.Category FontCategory;
    public static boolean ShouldReloadEffekseer;

    public static String getFullKey(String base) {
        return PREFIX + "_" + base;
    }

    private static final String USE_VANILLA_COMPENDIUM = getFullKey("UseVanillaCompendium");
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

    public static STSConfigItem<Boolean> UseVanillaCompendium = new STSConfigItem<>(USE_VANILLA_COMPENDIUM, false);
    public static STSConfigItem<Boolean> DisableEffekseer = new STSConfigItem<>(DISABLE_EFFEKSEER, false);
    public static STSConfigItem<Boolean> FlushOnGameStart = new STSConfigItem<>(FLUSH_ON_GAME_START, false);
    public static STSConfigItem<Boolean> FlushOnRoomStart = new STSConfigItem<>(FLUSH_ON_ROOM_START, false);
    public static STSConfigItem<Boolean> ShowModSettings = new STSConfigItem<>(SHOW_MOD_SETTINGS, false);
    public static STSConfigItem<Boolean> UseSeparateFonts = new STSConfigItem<>(USE_SEPARATE_FONTS, false);
    public static STSConfigItem<Boolean> OverrideGameFont = new STSConfigItem<>(OVERRIDE_GAME_FONT, false);

    public static STSStringConfigItem CardDescFont = new STSStringConfigItem(CARD_DESC_FONT,"");
    public static STSStringConfigItem CardTitleFont = new STSStringConfigItem(CARD_TITLE_FONT,"");
    public static STSStringConfigItem TipDescFont = new STSStringConfigItem(TIP_DESC_FONT,"");
    public static STSStringConfigItem TipTitleFont = new STSStringConfigItem(TITLE_TITLE_FONT,"");

    //public static STSConfigurationOption<Integer> MaxParticles = new STSConfigurationOption<Integer>(GetFullKey("MaxParticles"), BASE_SPRITES_DEFAULT);

    private static HashSet<String> tips = null;

    public static void load() {
        try
        {
            config = new SpireConfig(PREFIX, PREFIX);
            UseVanillaCompendium.addConfig(config);
            DisableEffekseer.addConfig(config);
            FlushOnGameStart.addConfig(config);
            FlushOnRoomStart.addConfig(config);
            ShowModSettings.addConfig(config);
            UseSeparateFonts.addConfig(config);
            OverrideGameFont.addConfig(config);
            CardDescFont.addConfig(config);
            CardTitleFont.addConfig(config);
            TipDescFont.addConfig(config);
            TipTitleFont.addConfig(config);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void postInitialize()
    {

        // Add EUI options
        EUITooltip restartTip = new EUITooltip(EUIRM.Strings.Misc_RestartRequired).setHeaderFont(EUIFontHelper.CardDescriptionFont_Normal);
        EffekseerCategory = new ModSettingsScreen.Category(EUIRM.Strings.Misc_EffekseerSettings);
        FontCategory = new ModSettingsScreen.Category(EUIRM.Strings.Misc_FontSettings);
        ModSettingsScreen.addCategory(EffekseerCategory);
        ModSettingsScreen.addCategory(FontCategory);
        makeModToggle(EffekseerCategory, DisableEffekseer, EUIRM.Strings.Config_DisableEffekseer);
        makeModToggle(EffekseerCategory, FlushOnGameStart, EUIRM.Strings.Config_FlushOnGameStart);
        makeModToggle(EffekseerCategory, FlushOnRoomStart, EUIRM.Strings.Config_FlushOnRoomStart);
        makeModToggle(FontCategory, UseSeparateFonts, EUIRM.Strings.Config_UseSeparateFonts).setTooltip(restartTip);
        makeModToggle(FontCategory, OverrideGameFont, EUIRM.Strings.Config_OverrideGameFont).setTooltip(new EUITooltip(EUIRM.Strings.Misc_FontDescription + " | " + EUIRM.Strings.Misc_RestartRequired));
        ModSettingsPathSelector cardDescFontSelector = (ModSettingsPathSelector) makeModPathSelection(FontCategory, CardDescFont, EUIRM.Strings.Config_CardDescFont, FONT_EXTS).setTooltip(restartTip);
        ModSettingsPathSelector cardTitleFontSelector = (ModSettingsPathSelector) makeModPathSelection(FontCategory, CardTitleFont, EUIRM.Strings.Config_CardTitleFont, FONT_EXTS).setTooltip(restartTip);
        ModSettingsPathSelector tipDescFontSelector = (ModSettingsPathSelector) makeModPathSelection(FontCategory, TipDescFont, EUIRM.Strings.Config_TipDescFont, FONT_EXTS).setTooltip(restartTip);
        ModSettingsPathSelector tipTitleFontSelector = (ModSettingsPathSelector) makeModPathSelection(FontCategory, TipTitleFont, EUIRM.Strings.Config_TipTitleFont, FONT_EXTS).setTooltip(restartTip);


        // Add basemod options
        int yPos = BASE_OPTION_OFFSET_Y;
        ModPanel panel = new ModPanel();

        yPos = addToggle(panel, UseVanillaCompendium, EUIRM.Strings.Config_UseVanillaCompendium, yPos);
        yPos = addToggle(panel, DisableEffekseer, EUIRM.Strings.Config_DisableEffekseer, yPos);
        yPos = addToggle(panel, FlushOnGameStart, EUIRM.Strings.Config_FlushOnGameStart, yPos);
        yPos = addToggle(panel, FlushOnRoomStart, EUIRM.Strings.Config_FlushOnRoomStart, yPos);
        yPos = addToggle(panel, ShowModSettings, EUIRM.Strings.Config_ShowModSettings, yPos);
        yPos = addToggle(panel, UseSeparateFonts, EUIRM.Strings.Config_UseSeparateFonts, yPos, EUIRM.Strings.Misc_RestartRequired);
        yPos = addToggle(panel, OverrideGameFont, EUIRM.Strings.Config_OverrideGameFont, yPos, EUIRM.Strings.Misc_FontDescription + " NL NL " + EUIRM.Strings.Misc_RestartRequired);

        yPos = (BASE_OPTION_OFFSET_Y + yPos) / 2;
        ModSettingsPathSelector cardDescFontSelector2 = (ModSettingsPathSelector) cardDescFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(panel, cardDescFontSelector2, yPos);
        ModSettingsPathSelector cardTitleFontSelector2 = (ModSettingsPathSelector) cardTitleFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(panel, cardTitleFontSelector2, yPos);
        ModSettingsPathSelector tipDescFontSelector2 = (ModSettingsPathSelector) tipDescFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(panel, tipDescFontSelector2, yPos);
        ModSettingsPathSelector tipTitleFontSelector2 = (ModSettingsPathSelector) tipTitleFontSelector.makeCopy().translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = addGenericElement(panel, tipTitleFontSelector2, yPos);
        BaseMod.registerModBadge(ImageMaster.loadImage("images/extendedui/modBadge.png"), PREFIX, "PinaColada, EatYourBeetS", "", panel);

        // Sub-font settings should only show up if UseSeparateFonts is true
        boolean showOtherSelectors = UseSeparateFonts.get();
        cardDescFontSelector2.setHeaderText(showOtherSelectors ? EUIRM.Strings.Config_CardDescFont : EUIRM.Strings.Config_MainFont);
        cardTitleFontSelector.setActive(showOtherSelectors);
        tipDescFontSelector.setActive(showOtherSelectors);
        tipTitleFontSelector.setActive(showOtherSelectors);
        cardTitleFontSelector2.setActive(showOtherSelectors);
        tipDescFontSelector2.setActive(showOtherSelectors);
        tipTitleFontSelector2.setActive(showOtherSelectors);
        UseSeparateFonts.addListener(new STSConfigListener<Boolean>()
        {
            @Override
            public void onChange(Boolean newValue)
            {
                cardDescFontSelector2.setHeaderText(newValue ? EUIRM.Strings.Config_CardDescFont : EUIRM.Strings.Config_MainFont);
                cardTitleFontSelector.setActive(newValue);
                tipDescFontSelector.setActive(newValue);
                tipTitleFontSelector.setActive(newValue);
                cardTitleFontSelector2.setActive(newValue);
                tipDescFontSelector2.setActive(newValue);
                tipTitleFontSelector2.setActive(newValue);
            }
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
        panel.addUIElement(new ModLabeledToggleButton(label, tip, BASE_OPTION_OFFSET_X, ypos, Settings.CREAM_COLOR.cpy(), EUIFontHelper.CardDescriptionFont_Normal, option.get(), panel, (__) -> {
        }, (c) -> option.set(c.enabled, true)));
        return ypos - BASE_OPTION_OPTION_HEIGHT;
    }

    protected static int addSlider(ModPanel panel, STSConfigItem<Integer> option, String label, int ypos, int min, int max) {
        panel.addUIElement(new ModMinMaxSlider(label, BASE_OPTION_OFFSET_X, ypos, min, max, option.get(), "%d", panel, (c) -> {
            option.set(MathUtils.round(c.getValue()), true);
            ShouldReloadEffekseer = true;
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
