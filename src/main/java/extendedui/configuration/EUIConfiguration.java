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
import extendedui.EUIUtils;
import extendedui.EUIRM;
import extendedui.interfaces.listeners.STSConfigListener;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.settings.ModSettingsPathSelector;
import extendedui.ui.settings.ModSettingsScreen;
import extendedui.ui.settings.ModSettingsToggle;
import extendedui.utilities.EUIFontHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class EUIConfiguration
{
    private static final int BASE_OPTION_OFFSET_X = 400;
    private static final int BASE_OPTION_OFFSET_X2 = 500;
    private static final int BASE_OPTION_OFFSET_Y = 700;
    private static final int BASE_OPTION_OPTION_HEIGHT = 32;
    public static final int BASE_SPRITES_DEFAULT = 6000;
    //public static final int BASE_SPRITES_MIN = 6000;
    //public static final int BASE_SPRITES_MAX = 6000;
    private static final String[] FONT_EXTS = EUIUtils.Array("otf", "ttf", "fnt");
    private static final String PREFIX = "EUI";
    private static SpireConfig config;
    private static int counter;
    public static ModSettingsScreen.Category EffekseerCategory;
    public static ModSettingsScreen.Category FontCategory;
    public static boolean ShouldReloadEffekseer;

    public static String GetFullKey(String base) {
        return PREFIX + "_" + base;
    }

    private static final String USE_VANILLA_COMPENDIUM = GetFullKey("UseVanillaCompendium");
    private static final String DISABLE_EFFEKSEER = GetFullKey("DisableEffekseer");
    private static final String FLUSH_ON_GAME_START = GetFullKey("FlushOnGameStart");
    private static final String FLUSH_ON_ROOM_START = GetFullKey("FlushOnRoomStart");
    private static final String SHOW_MOD_SETTINGS = GetFullKey("ShowModSettings");
    private static final String USE_SEPARATE_FONTS = GetFullKey("UseSeparateFonts");
    private static final String OVERRIDE_GAME_FONT = GetFullKey("OverrideGameFont");
    private static final String HIDE_TIP_DESCRIPTION = GetFullKey("HideTipDescription");
    private static final String CARD_DESC_FONT = GetFullKey("CardDescFont");
    private static final String CARD_TITLE_FONT = GetFullKey("CardTitleFont");
    private static final String TIP_DESC_FONT = GetFullKey("TipDescFont");
    private static final String TITLE_TITLE_FONT = GetFullKey("TipTitleFont");

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

    public static void Load() {
        try
        {
            config = new SpireConfig(PREFIX, PREFIX);
            UseVanillaCompendium.AddConfig(config);
            DisableEffekseer.AddConfig(config);
            FlushOnGameStart.AddConfig(config);
            FlushOnRoomStart.AddConfig(config);
            ShowModSettings.AddConfig(config);
            UseSeparateFonts.AddConfig(config);
            OverrideGameFont.AddConfig(config);
            CardDescFont.AddConfig(config);
            CardTitleFont.AddConfig(config);
            TipDescFont.AddConfig(config);
            TipTitleFont.AddConfig(config);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void PostInitialize()
    {

        // Add EUI options
        EffekseerCategory = new ModSettingsScreen.Category(EUIRM.Strings.Misc_EffekseerSettings);
        FontCategory = new ModSettingsScreen.Category(EUIRM.Strings.Misc_FontSettings);
        ModSettingsScreen.AddCategory(EffekseerCategory);
        ModSettingsScreen.AddCategory(FontCategory);
        MakeModToggle(EffekseerCategory, DisableEffekseer, EUIRM.Strings.Config_DisableEffekseer);
        MakeModToggle(EffekseerCategory, FlushOnGameStart, EUIRM.Strings.Config_FlushOnGameStart);
        MakeModToggle(EffekseerCategory, FlushOnRoomStart, EUIRM.Strings.Config_FlushOnRoomStart);
        EUILabel disclaimer = MakeModLabel(FontCategory, EUIRM.Strings.Misc_FontSettingDescription, EUIFontHelper.CardDescriptionFont_Normal);
        MakeModToggle(FontCategory, UseSeparateFonts, EUIRM.Strings.Config_UseSeparateFonts);
        MakeModToggle(FontCategory, OverrideGameFont, EUIRM.Strings.Config_OverrideGameFont);
        ModSettingsPathSelector cardDescFontSelector = MakeModPathSelection(FontCategory, CardDescFont, EUIRM.Strings.Config_CardDescFont, FONT_EXTS);
        ModSettingsPathSelector cardTitleFontSelector = MakeModPathSelection(FontCategory, CardTitleFont, EUIRM.Strings.Config_CardTitleFont, FONT_EXTS);
        ModSettingsPathSelector tipDescFontSelector = MakeModPathSelection(FontCategory, TipDescFont, EUIRM.Strings.Config_TipDescFont, FONT_EXTS);
        ModSettingsPathSelector tipTitleFontSelector = MakeModPathSelection(FontCategory, TipTitleFont, EUIRM.Strings.Config_TipTitleFont, FONT_EXTS);


        // Add basemod options
        int yPos = BASE_OPTION_OFFSET_Y;
        ModPanel panel = new ModPanel();

        yPos = AddToggle(panel, UseVanillaCompendium, EUIRM.Strings.Config_UseVanillaCompendium, yPos);
        yPos = AddToggle(panel, DisableEffekseer, EUIRM.Strings.Config_DisableEffekseer, yPos);
        yPos = AddToggle(panel, FlushOnGameStart, EUIRM.Strings.Config_FlushOnGameStart, yPos);
        yPos = AddToggle(panel, FlushOnRoomStart, EUIRM.Strings.Config_FlushOnRoomStart, yPos);
        yPos = AddToggle(panel, ShowModSettings, EUIRM.Strings.Config_ShowModSettings, yPos);
        yPos = AddToggle(panel, UseSeparateFonts, EUIRM.Strings.Config_UseSeparateFonts, yPos);
        yPos = AddToggle(panel, OverrideGameFont, EUIRM.Strings.Config_OverrideGameFont, yPos);

        yPos = (BASE_OPTION_OFFSET_Y + yPos) / 2;

        yPos = AddGenericElement(panel, disclaimer.MakeCopy().Translate(BASE_OPTION_OFFSET_X2, yPos), yPos);
        ModSettingsPathSelector cardDescFontSelector2 = (ModSettingsPathSelector) cardDescFontSelector.MakeCopy().Translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = AddGenericElement(panel, cardDescFontSelector2, yPos);
        ModSettingsPathSelector cardTitleFontSelector2 = (ModSettingsPathSelector) cardTitleFontSelector.MakeCopy().Translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = AddGenericElement(panel, cardTitleFontSelector2, yPos);
        ModSettingsPathSelector tipDescFontSelector2 = (ModSettingsPathSelector) tipDescFontSelector.MakeCopy().Translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = AddGenericElement(panel, tipDescFontSelector2, yPos);
        ModSettingsPathSelector tipTitleFontSelector2 = (ModSettingsPathSelector) tipTitleFontSelector.MakeCopy().Translate(BASE_OPTION_OFFSET_X2, yPos);
        yPos = AddGenericElement(panel, tipTitleFontSelector2, yPos);
        BaseMod.registerModBadge(ImageMaster.loadImage("images/extendedui/modBadge.png"), PREFIX, "PinaColada, EatYourBeetS", "", panel);

        // Sub-font settings should only show up if UseSeparateFonts is true
        boolean showOtherSelectors = UseSeparateFonts.Get();
        cardDescFontSelector2.SetHeaderText(showOtherSelectors ? EUIRM.Strings.Config_CardDescFont : EUIRM.Strings.Config_MainFont);
        cardTitleFontSelector.SetActive(showOtherSelectors);
        tipDescFontSelector.SetActive(showOtherSelectors);
        tipTitleFontSelector.SetActive(showOtherSelectors);
        cardTitleFontSelector2.SetActive(showOtherSelectors);
        tipDescFontSelector2.SetActive(showOtherSelectors);
        tipTitleFontSelector2.SetActive(showOtherSelectors);
        UseSeparateFonts.AddListener(new STSConfigListener<Boolean>()
        {
            @Override
            public void OnChange(Boolean newValue)
            {
                cardDescFontSelector2.SetHeaderText(newValue ? EUIRM.Strings.Config_CardDescFont : EUIRM.Strings.Config_MainFont);
                cardTitleFontSelector.SetActive(newValue);
                tipDescFontSelector.SetActive(newValue);
                tipTitleFontSelector.SetActive(newValue);
                cardTitleFontSelector2.SetActive(newValue);
                tipDescFontSelector2.SetActive(newValue);
                tipTitleFontSelector2.SetActive(newValue);
            }
        });
    }

    public static void Save() {
        try
        {
            config.save();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected static ModSettingsToggle MakeModToggle(ModSettingsScreen.Category category, STSConfigItem<Boolean> option, String label)
    {
        return ModSettingsScreen.AddBoolean(category, option, label);
    }

    protected static ModSettingsPathSelector MakeModPathSelection(ModSettingsScreen.Category category, STSConfigItem<String> option, String label, String... exts)
    {
        return ModSettingsScreen.AddPathSelection(category, option, label, exts);
    }

    protected static EUILabel MakeModLabel(ModSettingsScreen.Category category, String label, BitmapFont font)
    {
        return ModSettingsScreen.AddLabel(category, label, font);
    }

    protected static int AddToggle(ModPanel panel, STSConfigItem<Boolean> option, String label, int ypos) {
        panel.addUIElement(new ModLabeledToggleButton(label, BASE_OPTION_OFFSET_X, ypos, Settings.CREAM_COLOR.cpy(), EUIFontHelper.CardDescriptionFont_Normal, option.Get(), panel, (__) -> {
        }, (c) -> option.Set(c.enabled, true)));
        return ypos - BASE_OPTION_OPTION_HEIGHT;
    }

    protected static int AddSlider(ModPanel panel, STSConfigItem<Integer> option, String label, int ypos, int min, int max) {
        panel.addUIElement(new ModMinMaxSlider(label, BASE_OPTION_OFFSET_X, ypos, min, max, option.Get(), "%d", panel, (c) -> {
            option.Set(MathUtils.round(c.getValue()), true);
            ShouldReloadEffekseer = true;
        }));
        return ypos - BASE_OPTION_OPTION_HEIGHT;
    }

    protected static int AddGenericElement(ModPanel panel, EUIHoverable renderable, int ypos)
    {
        panel.addUIElement(renderable);
        return (int) (ypos - renderable.hb.height);
    }

    public static boolean HideTipDescription(String id)
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

    public static void HideTipDescription(String id, boolean value, boolean flush)
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

        config.setString(HIDE_TIP_DESCRIPTION, EUIUtils.JoinStrings("|", tips));

        if (flush)
        {
            Save();
        }
    }
}
