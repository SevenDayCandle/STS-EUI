package extendedui.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.STSConfigItem;

import java.io.File;
import java.util.HashMap;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class EUIFontHelper
{
    public static final String TINY_NUMBERS_FONT = "font/04b03.ttf";
    public static final String ENG_DEFAULT_FONT = "font/Kreon-Regular.ttf";
    public static final String ENG_BOLD_FONT = "font/Kreon-Bold.ttf";
    public static final String ZHS_DEFAULT_FONT = "font/zhs/NotoSansMonoCJKsc-Regular.otf";
    public static final String ZHS_BOLD_FONT = "font/zhs/SourceHanSerifSC-Bold.otf";
    public static final String ZHT_DEFAULT_FONT = "font/zht/NotoSansCJKtc-Regular.otf";
    public static final String ZHT_BOLD_FONT = "font/zht/NotoSansCJKtc-Bold.otf";
    public static final String EPO_DEFAULT_FONT = "font/epo/Andada-Regular.otf";
    public static final String EPO_BOLD_FONT = "font/epo/Andada-Bold.otf";
    public static final String GRE_DEFAULT_FONT = "font/gre/Roboto-Regular.ttf";
    public static final String GRE_BOLD_FONT = "font/gre/Roboto-Bold.ttf";
    public static final String JPN_DEFAULT_FONT = "font/jpn/NotoSansCJKjp-Regular.otf";
    public static final String JPN_BOLD_FONT = "font/jpn/NotoSansCJKjp-Bold.otf";
    public static final String KOR_DEFAULT_FONT = "font/kor/GyeonggiCheonnyeonBatangBold.ttf";
    public static final String KOR_BOLD_FONT = "font/kor/GyeonggiCheonnyeonBatangBold.ttf";
    public static final String RUS_DEFAULT_FONT = "font/rus/FiraSansExtraCondensed-Regular.ttf";
    public static final String RUS_BOLD_FONT = "font/rus/FiraSansExtraCondensed-Bold.ttf";
    public static final String SRB_DEFAULT_FONT = "font/srb/InfluBG.otf";
    public static final String SRB_BOLD_FONT = "font/srb/InfluBG-Bold.otf";
    public static final String THA_DEFAULT_FONT = "font/tha/CSChatThaiUI.ttf";
    public static final String THA_BOLD_FONT = "font/tha/CSChatThaiUI.ttf";

    protected static FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
    protected static FreeTypeFontGenerator.FreeTypeBitmapFontData data = new FreeTypeFontGenerator.FreeTypeBitmapFontData();
    protected static HashMap<String, FreeTypeFontGenerator> generators = new HashMap<>();
    protected static FileHandle mainFont = null;
    public static BitmapFont buttonFont;
    public static BitmapFont buttonFontSmall;
    public static BitmapFont buttonFontLarge;
    public static BitmapFont cardDescFont;
    public static BitmapFont carddescfontL;
    public static BitmapFont cardTipBodyFont;
    public static BitmapFont cardTipTitleFont;
    public static BitmapFont cardTitleFont;
    public static BitmapFont cardtitlefontSmall;
    public static BitmapFont cardtitlefontNormal;
    public static BitmapFont cardtitlefontLarge;
    public static BitmapFont cardTooltipFont;
    public static BitmapFont cardtooltiptitlefontNormal;
    public static BitmapFont cardtooltiptitlefontLarge;
    public static BitmapFont cardTypeFont;
    public static BitmapFont carddescriptionfontNormal;
    public static BitmapFont carddescriptionfontLarge;
    public static BitmapFont cardiconfontSmall;
    public static BitmapFont cardiconfontLarge;
    public static BitmapFont cardiconfontVerylarge;

    /* Because EUIFontHelper creates its fonts separately from the base game, mods that alter the game's font will not affect it.
    * Thus, EUIFontHelper requires its own version of a font configuration to allow users to make changes to them */
    public static void initialize()
    {
        boolean useSeparateFonts = EUIConfiguration.useSeparateFonts.get();
        generators.clear();
        data.xChars = new char[]{'动'};
        data.capChars = new char[]{'动'};
        FileHandle fontFile = getDefaultFontFile(Settings.language);
        FileHandle fontFileBold = getBoldFontFile(Settings.language);
        mainFont = getCustomFont(EUIConfiguration.cardDescFont, fontFile);

        param.hinting = FreeTypeFontGenerator.Hinting.Slight;
        param.kerning = true;
        param.gamma = 0.9F;
        param.borderGamma = 0.9F;
        param.borderStraight = false;
        param.shadowColor = new Color(0.0F, 0.0F, 0.0F, 0.25F);
        param.borderColor = new Color(0.35F, 0.35F, 0.35F, 1.0F);
        param.borderWidth = 0.0F;
        param.shadowOffsetX = 1;
        param.shadowOffsetY = 1;
        param.spaceX = 0;
        EUIFontHelper.cardDescFont = prepFont(mainFont, 24.0F, true);

        param.shadowOffsetX = Math.round(3.0F * Settings.scale);
        param.shadowOffsetY = Math.round(3.0F * Settings.scale);
        param.borderWidth = 2.0F * Settings.scale;
        EUIFontHelper.cardTitleFont = prepFont(useSeparateFonts ? getCustomFont(EUIConfiguration.cardTitleFont, fontFile) : mainFont,27.0F, true);

        param.borderWidth = 0.0F;
        param.shadowColor = Settings.QUARTER_TRANSPARENT_BLACK_COLOR.cpy();
        param.shadowOffsetX = Math.round(4.0F * Settings.scale);
        param.shadowOffsetY = Math.round(3.0F * Settings.scale);
        EUIFontHelper.carddescfontL = prepFont(mainFont,48.0F, true);

        param.shadowColor = Settings.QUARTER_TRANSPARENT_BLACK_COLOR.cpy();
        param.shadowOffsetX = (int) (3.0F * Settings.scale);
        param.shadowOffsetY = (int) (3.0F * Settings.scale);
        param.gamma = 0.9F;
        param.borderGamma = 0.9F;
        param.borderColor = new Color(0.4F, 0.1F, 0.1F, 1.0F);
        param.borderWidth = 0.0F;
        EUIFontHelper.cardTipBodyFont = prepFont(useSeparateFonts ? getCustomFont(EUIConfiguration.tipDescFont, fontFile) : mainFont,22.0F, true);

        param.borderWidth = 4.0F * Settings.scale;
        param.spaceX = (int)(-2.5F * Settings.scale);
        param.borderColor = Settings.QUARTER_TRANSPARENT_BLACK_COLOR;
        EUIFontHelper.buttonFont = prepFont(useSeparateFonts ? getCustomFont(EUIConfiguration.buttonFont, fontFile) : mainFont,32.0F, true);

        param.shadowColor = new Color(0.0F, 0.0F, 0.0F, 0.33F);
        param.gamma = 2.0F;
        param.borderGamma = 2.0F;
        param.borderStraight = true;
        param.borderColor = Color.DARK_GRAY;
        param.borderWidth = 2.0F * Settings.scale;
        param.shadowOffsetX = 1;
        param.shadowOffsetY = 1;
        EUIFontHelper.cardTipTitleFont = prepFont(useSeparateFonts ? getCustomFont(EUIConfiguration.tipTitleFont, fontFileBold) : EUIConfiguration.cardDescFont.get().isEmpty() ? fontFileBold : mainFont,23, true);


        Color bc1 = new Color(0.35F, 0.35F, 0.35F, 1.0F);
        Color sc1 = new Color(0, 0, 0, 0.25f);
        EUIFontHelper.cardtitlefontSmall = prepFont(cardTitleFont, 25, 2f, bc1, 3f, sc1);
        EUIFontHelper.cardtitlefontNormal = prepFont(cardTitleFont, 27, 2f, bc1, 3f, sc1);
        EUIFontHelper.cardtitlefontLarge = prepFont(cardTitleFont, 46, 4f, bc1, 3f, sc1);
        EUIFontHelper.cardTypeFont = prepFont(cardDescFont, 17f, 0, null, 1f, sc1);
        EUIFontHelper.carddescriptionfontNormal = prepFont(cardDescFont, 23, 0, 1f);
        EUIFontHelper.carddescriptionfontLarge = prepFont(carddescfontL, 46, 0, 2f);
        EUIFontHelper.cardiconfontVerylarge = prepFont(cardDescFont, 76, 4.5f, 1.4f);
        EUIFontHelper.cardiconfontLarge = prepFont(cardDescFont, 38, 2.25f, 0.7f);
        EUIFontHelper.cardiconfontSmall = prepFont(cardDescFont, 19, 1f, 0.3f);
        EUIFontHelper.cardTooltipFont = prepFont(cardTipBodyFont, 19, 0f, 2f);
        EUIFontHelper.cardtooltiptitlefontNormal = prepFont(cardTipTitleFont, 23, 0f, 1f);
        EUIFontHelper.cardtooltiptitlefontLarge = prepFont(cardTipTitleFont, 26, 0f, 2f);
        EUIFontHelper.buttonFontSmall = prepFont(buttonFont, 20, 4f, 3f);
        EUIFontHelper.buttonFontLarge = prepFont(buttonFont, 46, 4f, 3f);
    }

    public static void overwriteBaseFonts()
    {
        FontHelper.cardDescFont_N = EUIFontHelper.carddescriptionfontNormal;
        FontHelper.cardDescFont_L = EUIFontHelper.carddescriptionfontLarge;
        FontHelper.cardTitleFont = EUIFontHelper.cardtitlefontNormal;
        FontHelper.cardTypeFont = EUIFontHelper.cardTypeFont;
        FontHelper.tipBodyFont = EUIFontHelper.cardTooltipFont;
        FontHelper.tipHeaderFont = EUIFontHelper.cardtooltiptitlefontNormal;
        FontHelper.topPanelInfoFont = EUIFontHelper.cardtooltiptitlefontLarge;
        FontHelper.buttonLabelFont = EUIFontHelper.buttonFont;
    }

    private static FreeTypeFontGenerator getGenerator(FileHandle fontFile)
    {
        FreeTypeFontGenerator generator;
        if (generators.containsKey(fontFile.path()))
        {
            generator = generators.get(fontFile.path());
        }
        else
        {
            generator = new FreeTypeFontGenerator(fontFile);
            generators.put(fontFile.path(), generator);
        }

        return generator;
    }

    private static BitmapFont prepFont(FileHandle file, float size, boolean isLinearFiltering)
    {
        final FreeTypeFontGenerator g = getGenerator(file);
        final float fontScale = 1.0F;
        final FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.characters = "";
        p.incremental = true;
        p.size = Math.round(size * fontScale * Settings.scale);
        p.gamma = param.gamma;
        p.spaceX = param.spaceX;
        p.spaceY = param.spaceY;
        p.borderColor = param.borderColor;
        p.borderStraight = param.borderStraight;
        p.borderWidth = param.borderWidth;
        p.borderGamma = param.borderGamma;
        p.shadowColor = param.shadowColor;
        p.shadowOffsetX = param.shadowOffsetX;
        p.shadowOffsetY = param.shadowOffsetY;
        if (isLinearFiltering)
        {
            p.minFilter = Texture.TextureFilter.Linear;
            p.magFilter = Texture.TextureFilter.Linear;
        }
        else
        {
            p.minFilter = Texture.TextureFilter.Nearest;
            p.magFilter = Texture.TextureFilter.MipMapLinearNearest;
        }

        g.scaleForPixelHeight(p.size);
        BitmapFont font = g.generateFont(p);
        font.setUseIntegerPositions(!isLinearFiltering);
        font.getData().fontFile = file;
        font.getData().markupEnabled = true;
        if (LocalizedStrings.break_chars != null)
        {
            font.getData().breakChars = LocalizedStrings.break_chars.toCharArray();
        }

        return font;
    }

    private static BitmapFont prepFont(BitmapFont source, float size, float borderWidth, float shadowOffset)
    {
        return prepFont(source, size, borderWidth, new Color(0f, 0f, 0f, 1f), shadowOffset, new Color(0f, 0f, 0f, 0.5f));
    }

    private static BitmapFont prepFont(BitmapFont source, float size, float borderWidth, Color borderColor, float shadowOffset, Color shadowColor)
    {
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        param.hinting = FreeTypeFontGenerator.Hinting.Slight;
        param.spaceX = 0;
        param.kerning = true;
        param.borderColor = borderColor;
        param.borderWidth = borderWidth * Settings.scale;
        param.gamma = 0.9f;
        param.borderGamma = 0.9f;
        param.shadowColor = shadowColor;
        param.shadowOffsetX = Math.round(shadowOffset * Settings.scale);
        param.shadowOffsetY = Math.round(shadowOffset * Settings.scale);
        param.borderStraight = false;
        param.characters = "";
        param.incremental = true;
        param.size = Math.round(size * Settings.scale);

        FreeTypeFontGenerator generator = getGenerator(source.getData().fontFile);
        generator.scaleForPixelHeight(param.size);
        BitmapFont font = generator.generateFont(param);
        font.setUseIntegerPositions(false);
        font.getData().markupEnabled = false;
        if (LocalizedStrings.break_chars != null)
        {
            font.getData().breakChars = LocalizedStrings.break_chars.toCharArray();
        }

        return font;
    }

    private static FileHandle getCustomFont(STSConfigItem<String> config, FileHandle fallback)
    {
        String value = config.get();
        if (value != null && !value.isEmpty()) {
            String trimmed = value.replace("\"","").trim();
            File file = new File(trimmed);
            if (file.exists()) {
                return new FileHandle(file);
            }
            else {
                EUIUtils.logWarning(EUIFontHelper.class, "Could not load external font for config " + config.key + ". Config value: " + trimmed + ". Actual path: " + file.getAbsolutePath());
            }
        }
        return fallback;
    }

    public static String getFontDefaultPath(Settings.GameLanguage language)
    {
        switch (language)
        {
            case JPN:
                return JPN_DEFAULT_FONT;
            case KOR:
                return KOR_DEFAULT_FONT;
            case ZHS:
                return ZHS_DEFAULT_FONT;
            case ZHT:
                return ZHT_DEFAULT_FONT;
            case POL:
            case UKR:
            case RUS:
                return RUS_DEFAULT_FONT;
            case EPO:
                return EPO_DEFAULT_FONT;
            case GRE:
                return GRE_DEFAULT_FONT;
            case SRP:
            case SRB:
                return SRB_DEFAULT_FONT;
            case THA:
                return THA_DEFAULT_FONT;
            default:
                return ENG_DEFAULT_FONT;
        }
    }

    public static String getFontBoldPath(Settings.GameLanguage language)
    {
        switch (language)
        {
            case JPN:
                return JPN_BOLD_FONT;
            case KOR:
                return KOR_BOLD_FONT;
            case ZHS:
                return ZHS_BOLD_FONT;
            case ZHT:
                return ZHT_BOLD_FONT;
            case POL:
            case UKR:
            case RUS:
                return RUS_BOLD_FONT;
            case EPO:
                return EPO_BOLD_FONT;
            case GRE:
                return GRE_BOLD_FONT;
            case SRP:
            case SRB:
                return SRB_BOLD_FONT;
            case THA:
                return THA_BOLD_FONT;
            default:
                return ENG_BOLD_FONT;
        }
    }

    public static FileHandle getDefaultFontFile(Settings.GameLanguage language)
    {
        return Gdx.files.internal(getFontDefaultPath(language));
    }

    public static FileHandle getBoldFontFile(Settings.GameLanguage language)
    {
        return Gdx.files.internal(getFontBoldPath(language));
    }

    public static FileHandle getCustomDefaultFontFile(Settings.GameLanguage language)
    {
        return getCustomFont(EUIConfiguration.cardDescFont, getDefaultFontFile(language));
    }

    public static FileHandle getCustomBoldFontFile(Settings.GameLanguage language)
    {
        if (EUIConfiguration.useSeparateFonts.get())
        {
            return getCustomFont(EUIConfiguration.tipTitleFont, getBoldFontFile(language));
        }
        return getCustomFont(EUIConfiguration.cardDescFont, getBoldFontFile(language));
    }

    public static BitmapFont createDefaultFont(Settings.GameLanguage language, boolean isLinearFiltering, float size, float borderWidth, Color borderColor, float shadowOffset, Color shadowColor) {
        FileHandle file = getCustomDefaultFontFile(language);
        BitmapFont preppedFont = prepFont(file, size, isLinearFiltering);
        return prepFont(preppedFont, size, borderWidth, borderColor, shadowOffset, shadowColor);
    }

    public static BitmapFont createBoldFont(Settings.GameLanguage language, boolean isLinearFiltering, float size, float borderWidth, Color borderColor, float shadowOffset, Color shadowColor) {
        FileHandle file = getCustomBoldFontFile(language);
        BitmapFont preppedFont = prepFont(file, size, isLinearFiltering);
        return prepFont(preppedFont, size, borderWidth, borderColor, shadowOffset, shadowColor);
    }

    public static boolean hasGlyph(BitmapFont font, char c)
    {
        return font.getData().hasGlyph(c);
    }
}