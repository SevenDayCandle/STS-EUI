package extendedui.configuration;

import basemod.*;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRM;
import extendedui.JavaUtils;

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
    private static int counter;
    public static boolean RequiresReload;

    public static String GetFullKey(String base) {
        return PREFIX + "_" + base;
    }

    private static final String USE_VANILLA_COMPENDIUM = GetFullKey("UseVanillaCompendium");
    private static final String DISABLE_EFFEKSEER = GetFullKey("DisableEffekseer");
    private static final String FLUSH_ON_GAME_START = GetFullKey("FlushOnGameStart");
    private static final String FLUSH_ON_ROOM_START = GetFullKey("FlushOnRoomStart");
    private static final String HIDE_TIP_DESCRIPTION = GetFullKey("HideTipDescription");

    public static STSConfigItem<Boolean> UseVanillaCompendium = new STSConfigItem<>(USE_VANILLA_COMPENDIUM, false);
    public static STSConfigItem<Boolean> DisableEffekseer = new STSConfigItem<>(DISABLE_EFFEKSEER, false);
    public static STSConfigItem<Boolean> FlushOnGameStart = new STSConfigItem<>(FLUSH_ON_GAME_START, false);
    public static STSConfigItem<Boolean> FlushOnRoomStart = new STSConfigItem<>(FLUSH_ON_ROOM_START, false);
    //public static STSConfigurationOption<Integer> MaxParticles = new STSConfigurationOption<Integer>(GetFullKey("MaxParticles"), BASE_SPRITES_DEFAULT);

    private static HashSet<String> tips = null;

    public static void Load() {
        try
        {
            config = new SpireConfig(PREFIX, PREFIX);
            final ModPanel panel = new ModPanel();
            UseVanillaCompendium.AddConfig(config);
            DisableEffekseer.AddConfig(config);
            FlushOnGameStart.AddConfig(config);
            FlushOnRoomStart.AddConfig(config);
            //MaxParticles.AddConfig(config);

            int yPos = BASE_OPTION_OFFSET_Y;

            yPos = AddToggle(panel, UseVanillaCompendium, EUIRM.Strings.Config_UseVanillaCompendium, yPos);
            yPos = AddToggle(panel, DisableEffekseer, EUIRM.Strings.Config_DisableEffekseer, yPos);
            yPos = AddToggle(panel, FlushOnGameStart, EUIRM.Strings.Config_FlushOnGameStart, yPos);
            yPos = AddToggle(panel, FlushOnRoomStart, EUIRM.Strings.Config_FlushOnRoomStart, yPos);
            //yPos = AddSlider(panel, MaxParticles, s.TEXT[2], yPos, BASE_SPRITES_MIN, BASE_SPRITES_MAX);
            BaseMod.registerModBadge(ImageMaster.loadImage("images/extendedui/modBadge.png"), PREFIX, "PinaColada, EatYourBeetS", "", panel);
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

    protected static int AddToggle(ModPanel panel, STSConfigItem<Boolean> option, String label, int ypos) {
        panel.addUIElement(new ModLabeledToggleButton(label, BASE_OPTION_OFFSET_X, ypos, Settings.CREAM_COLOR.cpy(), FontHelper.charDescFont, option.Get(), panel, (__) -> {
        }, (c) -> option.Set(c.enabled, true)));
        return ypos - BASE_OPTION_OPTION_HEIGHT;
    }

    protected static int AddSlider(ModPanel panel, STSConfigItem<Integer> option, String label, int ypos, int min, int max) {
        panel.addUIElement(new ModMinMaxSlider(label, BASE_OPTION_OFFSET_X, ypos, min, max, option.Get(), "%d", panel, (c) -> {
            option.Set(MathUtils.round(c.getValue()), true);
            RequiresReload = true;
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
