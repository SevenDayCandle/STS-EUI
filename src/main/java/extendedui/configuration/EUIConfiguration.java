package extendedui.configuration;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModMinMaxSlider;
import basemod.ModPanel;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.JavaUtils;
import extendedui.ui.settings.ModSettingsScreen;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class EUIConfiguration
{
    private static final int BASE_OPTION_OFFSET_X = 400;
    private static final int BASE_OPTION_OFFSET_Y = 700;
    private static final int BASE_OPTION_OPTION_HEIGHT = 50;
    public static final int BASE_SPRITES_DEFAULT = 6000;
    //public static final int BASE_SPRITES_MIN = 6000;
    //public static final int BASE_SPRITES_MAX = 6000;
    private static final String PREFIX = "EUI";
    private static SpireConfig config;
    private static ModInfo INFO;
    private static int counter;
    public static boolean ShouldReloadEffekseer;

    public static String GetFullKey(String base) {
        return PREFIX + "_" + base;
    }

    private static final String USE_VANILLA_COMPENDIUM = GetFullKey("UseVanillaCompendium");
    private static final String DISABLE_EFFEKSEER = GetFullKey("DisableEffekseer");
    private static final String FLUSH_ON_GAME_START = GetFullKey("FlushOnGameStart");
    private static final String FLUSH_ON_ROOM_START = GetFullKey("FlushOnRoomStart");
    private static final String HIDE_TIP_DESCRIPTION = GetFullKey("HideTipDescription");
    private static final String CUSTOM_ENG_DEFAULT_FONT = GetFullKey("CustomENGDefaultFont");
    private static final String CUSTOM_ENG_BOLD_FONT = GetFullKey("CustomENGBoldFont");
    private static final String CUSTOM_ENG_ITALIC_FONT = GetFullKey("CustomENGItalicFont");
    private static final String CUSTOM_ENG_DRAMATIC_FONT = GetFullKey("CustomENGDramaticFont");
    private static final String CUSTOM_ZHS_DEFAULT_FONT = GetFullKey("CustomZHSDefaultFont");
    private static final String CUSTOM_ZHS_BOLD_FONT = GetFullKey("CustomZHSBoldFont");
    private static final String CUSTOM_ZHS_ITALIC_FONT = GetFullKey("CustomZHSItalicFont");
    private static final String CUSTOM_ZHT_DEFAULT_FONT = GetFullKey("CustomZHTDefaultFont");
    private static final String CUSTOM_ZHT_BOLD_FONT = GetFullKey("CustomZHTBoldFont");
    private static final String CUSTOM_ZHT_ITALIC_FONT = GetFullKey("CustomZHTItalicFont");
    private static final String CUSTOM_EPO_DEFAULT_FONT = GetFullKey("CustomEPODefaultFont");
    private static final String CUSTOM_EPO_BOLD_FONT = GetFullKey("CustomEPOBoldFont");
    private static final String CUSTOM_EPO_ITALIC_FONT = GetFullKey("CustomEPOItalicFont");
    private static final String CUSTOM_GRE_DEFAULT_FONT = GetFullKey("CustomGREDefaultFont");
    private static final String CUSTOM_GRE_BOLD_FONT = GetFullKey("CustomGREBoldFont");
    private static final String CUSTOM_GRE_ITALIC_FONT = GetFullKey("CustomGREItalicFont");
    private static final String CUSTOM_JPN_DEFAULT_FONT = GetFullKey("CustomJPNDefaultFont");
    private static final String CUSTOM_JPN_BOLD_FONT = GetFullKey("CustomJPNBoldFont");
    private static final String CUSTOM_JPN_ITALIC_FONT = GetFullKey("CustomJPNItalicFont");
    private static final String CUSTOM_KOR_DEFAULT_FONT = GetFullKey("CustomKORDefaultFont");
    private static final String CUSTOM_KOR_BOLD_FONT = GetFullKey("CustomKORBoldFont");
    private static final String CUSTOM_KOR_ITALIC_FONT = GetFullKey("CustomKORItalicFont");
    private static final String CUSTOM_RUS_DEFAULT_FONT = GetFullKey("CustomRUSDefaultFont");
    private static final String CUSTOM_RUS_BOLD_FONT = GetFullKey("CustomRUSBoldFont");
    private static final String CUSTOM_RUS_ITALIC_FONT = GetFullKey("CustomRUSItalicFont");
    private static final String CUSTOM_SRB_DEFAULT_FONT = GetFullKey("CustomSRBDefaultFont");
    private static final String CUSTOM_SRB_BOLD_FONT = GetFullKey("CustomSRBBoldFont");
    private static final String CUSTOM_SRB_ITALIC_FONT = GetFullKey("CustomSRBItalicFont");
    private static final String CUSTOM_THA_DEFAULT_FONT = GetFullKey("CustomTHADefaultFont");
    private static final String CUSTOM_THA_BOLD_FONT = GetFullKey("CustomTHABoldFont");
    private static final String CUSTOM_THA_ITALIC_FONT = GetFullKey("CustomTHAItalicFont");

    public static STSConfigItem<Boolean> UseVanillaCompendium = new STSConfigItem<>(USE_VANILLA_COMPENDIUM, false);
    public static STSConfigItem<Boolean> DisableEffekseer = new STSConfigItem<>(DISABLE_EFFEKSEER, false);
    public static STSConfigItem<Boolean> FlushOnGameStart = new STSConfigItem<>(FLUSH_ON_GAME_START, false);
    public static STSConfigItem<Boolean> FlushOnRoomStart = new STSConfigItem<>(FLUSH_ON_ROOM_START, false);

    public static STSStringConfigItem CustomENGDefaultFont = new STSStringConfigItem(CUSTOM_ENG_DEFAULT_FONT,"");
    public static STSStringConfigItem CustomENGBoldFont = new STSStringConfigItem(CUSTOM_ENG_BOLD_FONT,"");
    public static STSStringConfigItem CustomENGItalicFont = new STSStringConfigItem(CUSTOM_ENG_ITALIC_FONT,"");
    public static STSStringConfigItem CustomENGDramaticFont = new STSStringConfigItem(CUSTOM_ENG_DRAMATIC_FONT,"");
    public static STSStringConfigItem CustomZHSDefaultFont = new STSStringConfigItem(CUSTOM_ZHS_DEFAULT_FONT,"");
    public static STSStringConfigItem CustomZHSBoldFont = new STSStringConfigItem(CUSTOM_ZHS_BOLD_FONT,"");
    public static STSStringConfigItem CustomZHSItalicFont = new STSStringConfigItem(CUSTOM_ZHS_ITALIC_FONT,"");
    public static STSStringConfigItem CustomZHTDefaultFont = new STSStringConfigItem(CUSTOM_ZHT_DEFAULT_FONT,"");
    public static STSStringConfigItem CustomZHTBoldFont = new STSStringConfigItem(CUSTOM_ZHT_BOLD_FONT,"");
    public static STSStringConfigItem CustomZHTItalicFont = new STSStringConfigItem(CUSTOM_ZHT_ITALIC_FONT,"");
    public static STSStringConfigItem CustomEPODefaultFont = new STSStringConfigItem(CUSTOM_EPO_DEFAULT_FONT,"");
    public static STSStringConfigItem CustomEPOBoldFont = new STSStringConfigItem(CUSTOM_EPO_BOLD_FONT,"");
    public static STSStringConfigItem CustomEPOItalicFont = new STSStringConfigItem(CUSTOM_EPO_ITALIC_FONT,"");
    public static STSStringConfigItem CustomGREDefaultFont = new STSStringConfigItem(CUSTOM_GRE_DEFAULT_FONT,"");
    public static STSStringConfigItem CustomGREBoldFont = new STSStringConfigItem(CUSTOM_GRE_BOLD_FONT,"");
    public static STSStringConfigItem CustomGREItalicFont = new STSStringConfigItem(CUSTOM_GRE_ITALIC_FONT,"");
    public static STSStringConfigItem CustomJPNDefaultFont = new STSStringConfigItem(CUSTOM_JPN_DEFAULT_FONT,"");
    public static STSStringConfigItem CustomJPNBoldFont = new STSStringConfigItem(CUSTOM_JPN_BOLD_FONT,"");
    public static STSStringConfigItem CustomJPNItalicFont = new STSStringConfigItem(CUSTOM_JPN_ITALIC_FONT,"");
    public static STSStringConfigItem CustomKORDefaultFont = new STSStringConfigItem(CUSTOM_KOR_DEFAULT_FONT,"");
    public static STSStringConfigItem CustomKORBoldFont = new STSStringConfigItem(CUSTOM_KOR_BOLD_FONT,"");
    public static STSStringConfigItem CustomKORItalicFont = new STSStringConfigItem(CUSTOM_KOR_ITALIC_FONT,"");
    public static STSStringConfigItem CustomRUSDefaultFont = new STSStringConfigItem(CUSTOM_RUS_DEFAULT_FONT,"");
    public static STSStringConfigItem CustomRUSBoldFont = new STSStringConfigItem(CUSTOM_RUS_BOLD_FONT,"");
    public static STSStringConfigItem CustomRUSItalicFont = new STSStringConfigItem(CUSTOM_RUS_ITALIC_FONT,"");
    public static STSStringConfigItem CustomSRBDefaultFont = new STSStringConfigItem(CUSTOM_SRB_DEFAULT_FONT,"");
    public static STSStringConfigItem CustomSRBBoldFont = new STSStringConfigItem(CUSTOM_SRB_BOLD_FONT,"");
    public static STSStringConfigItem CustomSRBItalicFont = new STSStringConfigItem(CUSTOM_SRB_ITALIC_FONT,"");
    public static STSStringConfigItem CustomTHADefaultFont = new STSStringConfigItem(CUSTOM_THA_DEFAULT_FONT,"");
    public static STSStringConfigItem CustomTHABoldFont = new STSStringConfigItem(CUSTOM_THA_BOLD_FONT,"");
    public static STSStringConfigItem CustomTHAItalicFont = new STSStringConfigItem(CUSTOM_THA_ITALIC_FONT,"");

    //public static STSConfigurationOption<Integer> MaxParticles = new STSConfigurationOption<Integer>(GetFullKey("MaxParticles"), BASE_SPRITES_DEFAULT);

    private static HashSet<String> tips = null;

    public static void Load() {
        try
        {
            config = new SpireConfig(PREFIX, PREFIX);
            INFO = EUIGameUtils.GetModInfo(EUIConfiguration.class);
            final ModPanel panel = new ModPanel();
            ModSettingsScreen.AddSubscriber(EUIGameUtils.GetModInfo(EUIConfiguration.class));
            UseVanillaCompendium.AddConfig(config);
            DisableEffekseer.AddConfig(config);
            FlushOnGameStart.AddConfig(config);
            FlushOnRoomStart.AddConfig(config);
            CustomENGDefaultFont.AddConfig(config);
            CustomENGBoldFont.AddConfig(config);
            CustomENGItalicFont.AddConfig(config);
            CustomENGDramaticFont.AddConfig(config);
            CustomZHSDefaultFont.AddConfig(config);
            CustomZHSBoldFont.AddConfig(config);
            CustomZHSItalicFont.AddConfig(config);
            CustomZHTDefaultFont.AddConfig(config);
            CustomZHTBoldFont.AddConfig(config);
            CustomZHTItalicFont.AddConfig(config);
            CustomEPODefaultFont.AddConfig(config);
            CustomEPOBoldFont.AddConfig(config);
            CustomEPOItalicFont.AddConfig(config);
            CustomGREDefaultFont.AddConfig(config);
            CustomGREBoldFont.AddConfig(config);
            CustomGREItalicFont.AddConfig(config);
            CustomJPNDefaultFont.AddConfig(config);
            CustomJPNBoldFont.AddConfig(config);
            CustomJPNItalicFont.AddConfig(config);
            CustomKORDefaultFont.AddConfig(config);
            CustomKORBoldFont.AddConfig(config);
            CustomKORItalicFont.AddConfig(config);
            CustomRUSDefaultFont.AddConfig(config);
            CustomRUSBoldFont.AddConfig(config);
            CustomRUSItalicFont.AddConfig(config);
            CustomSRBDefaultFont.AddConfig(config);
            CustomSRBBoldFont.AddConfig(config);
            CustomSRBItalicFont.AddConfig(config);
            CustomTHADefaultFont.AddConfig(config);
            CustomTHABoldFont.AddConfig(config);
            CustomTHAItalicFont.AddConfig(config);

            int yPos = BASE_OPTION_OFFSET_Y;

            yPos = AddToggle(panel, UseVanillaCompendium, EUIRM.Strings.Config_UseVanillaCompendium, yPos);
            yPos = AddToggle(panel, DisableEffekseer, EUIRM.Strings.Config_DisableEffekseer, yPos);
            yPos = AddToggle(panel, FlushOnGameStart, EUIRM.Strings.Config_FlushOnGameStart, yPos);
            yPos = AddToggle(panel, FlushOnRoomStart, EUIRM.Strings.Config_FlushOnRoomStart, yPos);
            //yPos = AddSlider(panel, MaxParticles, s.TEXT[2], yPos, BASE_SPRITES_MIN, BASE_SPRITES_MAX);
            BaseMod.registerModBadge(ImageMaster.loadImage("images/extendedui/modBadge.png"), PREFIX, "PinaColada, EatYourBeetS", "", panel);

            SubscribeToggle(DisableEffekseer, EUIRM.Strings.Config_DisableEffekseer);
            SubscribeToggle(FlushOnGameStart, EUIRM.Strings.Config_FlushOnGameStart);
            SubscribeToggle(FlushOnRoomStart, EUIRM.Strings.Config_FlushOnRoomStart);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
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

    protected static void SubscribeToggle(STSConfigItem<Boolean> option, String label)
    {
        ModSettingsScreen.SubscribeBoolean(INFO, option, label);
    }

    protected static int AddToggle(ModPanel panel, STSConfigItem<Boolean> option, String label, int ypos) {
        panel.addUIElement(new ModLabeledToggleButton(label, BASE_OPTION_OFFSET_X, ypos, Settings.CREAM_COLOR.cpy(), FontHelper.charDescFont, option.Get(), panel, (__) -> {
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

        config.setString(HIDE_TIP_DESCRIPTION, JavaUtils.JoinStrings("|", tips));

        if (flush)
        {
            Save();
        }
    }
}
