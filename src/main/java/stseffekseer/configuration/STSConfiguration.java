package stseffekseer.configuration;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModMinMaxSlider;
import basemod.ModPanel;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/* Adapted from https://github.com/EatYourBeetS/STS-AnimatorMod */

public class STSConfiguration
{
    private static final int BASE_OPTION_OFFSET_X = 400;
    private static final int BASE_OPTION_OFFSET_Y = 700;
    private static final int BASE_OPTION_OPTION_HEIGHT = 50;
    public static final int BASE_SPRITES_DEFAULT = 6000;
    //public static final int BASE_SPRITES_MIN = 6000;
    //public static final int BASE_SPRITES_MAX = 6000;
    private static final String MOD_ID = "stseffekseer";
    private static final String PREFIX = "STSEffekseer";
    private static SpireConfig config;
    private static int counter;
    public static boolean RequiresReload;

    public static String GetFullKey(String base) {
        return PREFIX + "_" + base;
    }

    public static STSConfigurationOption<Boolean> FlushOnGameStart = new STSConfigurationOption<Boolean>(GetFullKey("FlushOnGameStart"), false);
    public static STSConfigurationOption<Boolean> FlushOnRoomStart = new STSConfigurationOption<Boolean>(GetFullKey("FlushOnRoomStart"), false);
    //public static STSConfigurationOption<Integer> MaxParticles = new STSConfigurationOption<Integer>(GetFullKey("MaxParticles"), BASE_SPRITES_DEFAULT);

    public static void Load() {
        try
        {
            config = new SpireConfig(PREFIX, PREFIX);
            final UIStrings s = CardCrawlGame.languagePack.getUIString(MOD_ID);
            final ModPanel panel = new ModPanel();
            FlushOnGameStart.AddConfig(config);
            FlushOnRoomStart.AddConfig(config);
            //MaxParticles.AddConfig(config);

            int yPos = BASE_OPTION_OFFSET_Y;
            yPos = AddToggle(panel, FlushOnGameStart, s.TEXT[0], yPos);
            yPos = AddToggle(panel, FlushOnRoomStart, s.TEXT[1], yPos);
            //yPos = AddSlider(panel, MaxParticles, s.TEXT[2], yPos, BASE_SPRITES_MIN, BASE_SPRITES_MAX);
            BaseMod.registerModBadge(ImageMaster.loadImage("img/modBadge.png"), PREFIX, "PinaColada", "", panel);
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

    protected static int AddToggle(ModPanel panel, STSConfigurationOption<Boolean> option, String label, int ypos) {
        panel.addUIElement(new ModLabeledToggleButton(label, BASE_OPTION_OFFSET_X, ypos, Settings.CREAM_COLOR.cpy(), FontHelper.charDescFont, option.Get(), panel, (__) -> {
        }, (c) -> {
            option.Set(c.enabled, true);
        }));
        return ypos - BASE_OPTION_OPTION_HEIGHT;
    }

    protected static int AddSlider(ModPanel panel, STSConfigurationOption<Integer> option, String label, int ypos, int min, int max) {
        panel.addUIElement(new ModMinMaxSlider(label, BASE_OPTION_OFFSET_X, ypos, min, max, option.Get(), "%d", panel, (c) -> {
            option.Set(MathUtils.round(c.getValue()), true);
            RequiresReload = true;
        }));
        return ypos - BASE_OPTION_OPTION_HEIGHT;
    }
}
