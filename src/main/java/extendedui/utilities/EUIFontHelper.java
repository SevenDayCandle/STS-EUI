package extendedui.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.STSStringConfigItem;

import java.util.HashMap;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

//TODO add support for more languages
public class EUIFontHelper
{
    public static final String TINY_NUMBERS_FONT = "font/04b03.ttf";
    public static final String ENG_DEFAULT_FONT = "font/Kreon-Regular.ttf";
    public static final String ENG_BOLD_FONT = "font/Kreon-Bold.ttf";
    public static final String ENG_ITALIC_FONT = "font/ZillaSlab-RegularItalic.otf";
    public static final String ENG_DRAMATIC_FONT = "font/FeDPrm27C.otf";
    public static final String ZHS_DEFAULT_FONT = "font/zhs/NotoSansMonoCJKsc-Regular.otf";
    public static final String ZHS_BOLD_FONT = "font/zhs/SourceHanSerifSC-Bold.otf";
    public static final String ZHS_ITALIC_FONT = "font/zhs/SourceHanSerifSC-Medium.otf";
    public static final String ZHT_DEFAULT_FONT = "font/zht/NotoSansCJKtc-Regular.otf";
    public static final String ZHT_BOLD_FONT = "font/zht/NotoSansCJKtc-Bold.otf";
    public static final String ZHT_ITALIC_FONT = "font/zht/NotoSansCJKtc-Medium.otf";
    public static final String EPO_DEFAULT_FONT = "font/epo/Andada-Regular.otf";
    public static final String EPO_BOLD_FONT = "font/epo/Andada-Bold.otf";
    public static final String EPO_ITALIC_FONT = "font/epo/Andada-Italic.otf";
    public static final String GRE_DEFAULT_FONT = "font/gre/Roboto-Regular.ttf";
    public static final String GRE_BOLD_FONT = "font/gre/Roboto-Bold.ttf";
    public static final String GRE_ITALIC_FONT = "font/gre/Roboto-Italic.ttf";
    public static final String JPN_DEFAULT_FONT = "font/jpn/NotoSansCJKjp-Regular.otf";
    public static final String JPN_BOLD_FONT = "font/jpn/NotoSansCJKjp-Bold.otf";
    public static final String JPN_ITALIC_FONT = "font/jpn/NotoSansCJKjp-Medium.otf";
    public static final String KOR_DEFAULT_FONT = "font/kor/GyeonggiCheonnyeonBatangBold.ttf";
    public static final String KOR_BOLD_FONT = "font/kor/GyeonggiCheonnyeonBatangBold.ttf";
    public static final String KOR_ITALIC_FONT = "font/kor/GyeonggiCheonnyeonBatangBold.ttf";
    public static final String RUS_DEFAULT_FONT = "font/rus/FiraSansExtraCondensed-Regular.ttf";
    public static final String RUS_BOLD_FONT = "font/rus/FiraSansExtraCondensed-Bold.ttf";
    public static final String RUS_ITALIC_FONT = "font/rus/FiraSansExtraCondensed-Italic.ttf";
    public static final String SRB_DEFAULT_FONT = "font/srb/InfluBG.otf";
    public static final String SRB_BOLD_FONT = "font/srb/InfluBG-Bold.otf";
    public static final String SRB_ITALIC_FONT = "font/srb/InfluBG-Italic.otf";
    public static final String THA_DEFAULT_FONT = "font/tha/CSChatThaiUI.ttf";
    public static final String THA_BOLD_FONT = "font/tha/CSChatThaiUI.ttf";
    public static final String THA_ITALIC_FONT = "font/tha/CSChatThaiUI.ttf";
    public static final String VIE_DEFAULT_FONT = "font/vie/Grenze-Regular.ttf";
    public static final String VIE_BOLD_FONT = "font/vie/Grenze-SemiBold.ttf";
    public static final String VIE_DRAMATIC_FONT = "font/vie/Grenze-Black.ttf";
    public static final String VIE_ITALIC_FONT = "font/vie/Grenze-RegularItalic.ttf";

    protected static FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
    protected static FreeTypeFontGenerator.FreeTypeBitmapFontData data = new FreeTypeFontGenerator.FreeTypeBitmapFontData();
    protected static HashMap<String, FreeTypeFontGenerator> generators = new HashMap<>();
    protected static FileHandle fontFile = null;
    protected static FileHandle fontFileBold = null;
    protected static FileHandle fontFileItalic = null;
    protected static BitmapFont cardDescFont;
    protected static BitmapFont cardDescFont_L;
    protected static BitmapFont cardTipFont;
    protected static BitmapFont cardTitleFont;

    public static BitmapFont CardTitleFont_Small;
    public static BitmapFont CardTitleFont_Normal;
    public static BitmapFont CardTitleFont_Large;
    public static BitmapFont CardTooltipFont;
    public static BitmapFont CardTypeFont;
    public static BitmapFont CardDescriptionFont_Normal;
    public static BitmapFont CardDescriptionFont_Large;
    public static BitmapFont CardIconFont_Small;
    public static BitmapFont CardIconFont_Large;
    public static BitmapFont CardIconFont_VeryLarge;

    public static void Initialize()
    {
        generators.clear();
        data.xChars = new char[]{'动'};
        data.capChars = new char[]{'动'};
        fontFile = GetFontDefaultFile(Settings.language);

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
        EUIFontHelper.cardDescFont = PrepFont(24.0F, true);

        param.shadowOffsetX = Math.round(3.0F * Settings.scale);
        param.shadowOffsetY = Math.round(3.0F * Settings.scale);
        param.borderWidth = 2.0F * Settings.scale;
        EUIFontHelper.cardTitleFont = PrepFont(27.0F, true);

        param.borderWidth = 0.0F;
        param.shadowColor = Settings.QUARTER_TRANSPARENT_BLACK_COLOR.cpy();
        param.shadowOffsetX = Math.round(4.0F * Settings.scale);
        param.shadowOffsetY = Math.round(3.0F * Settings.scale);
        EUIFontHelper.cardDescFont_L = PrepFont(48.0F, true);

        param.shadowColor = Settings.QUARTER_TRANSPARENT_BLACK_COLOR.cpy();
        param.shadowOffsetX = (int) (3.0F * Settings.scale);
        param.shadowOffsetY = (int) (3.0F * Settings.scale);
        param.gamma = 0.9F;
        param.borderGamma = 0.9F;
        param.borderColor = new Color(0.4F, 0.1F, 0.1F, 1.0F);
        param.borderWidth = 0.0F;
        EUIFontHelper.cardTipFont = PrepFont(22.0F, true);

        Color bc1 = new Color(0.35F, 0.35F, 0.35F, 1.0F);
        Color sc1 = new Color(0, 0, 0, 0.25f);
        EUIFontHelper.CardTitleFont_Small = PrepFont(cardTitleFont, 25, 2f, bc1, 3f, sc1);
        EUIFontHelper.CardTitleFont_Normal = PrepFont(cardTitleFont, 27, 2f, bc1, 3f, sc1);
        EUIFontHelper.CardTitleFont_Large = PrepFont(cardTitleFont, 46, 4f, bc1, 3f, sc1);
        EUIFontHelper.CardTypeFont = PrepFont(cardDescFont, 17f, 0, null, 1f, sc1);
        EUIFontHelper.CardDescriptionFont_Normal = PrepFont(cardDescFont, 23, 0, 1f);
        EUIFontHelper.CardDescriptionFont_Large = PrepFont(cardDescFont_L, 46, 0, 2f);
        EUIFontHelper.CardIconFont_VeryLarge = PrepFont(cardDescFont, 76, 4.5f, 1.4f);
        EUIFontHelper.CardIconFont_Large = PrepFont(cardDescFont, 38, 2.25f, 0.7f);
        EUIFontHelper.CardIconFont_Small = PrepFont(cardDescFont, 19, 1f, 0.3f);
        EUIFontHelper.CardTooltipFont = PrepFont(cardTipFont, 19, 0f, 2f);
    }

    private static FreeTypeFontGenerator GetGenerator(FileHandle fontFile)
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

    private static BitmapFont PrepFont(float size, boolean isLinearFiltering)
    {
        return PrepFont(GetGenerator(fontFile), size, isLinearFiltering);
    }

    private static BitmapFont PrepFont(FreeTypeFontGenerator g, float size, boolean isLinearFiltering)
    {
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
        font.getData().fontFile = fontFile;
        font.getData().markupEnabled = true;
        if (LocalizedStrings.break_chars != null)
        {
            font.getData().breakChars = LocalizedStrings.break_chars.toCharArray();
        }

        return font;
    }

    private static BitmapFont PrepFont(BitmapFont source, float size, float borderWidth, float shadowOffset)
    {
        return PrepFont(source, size, borderWidth, new Color(0f, 0f, 0f, 1f), shadowOffset, new Color(0f, 0f, 0f, 0.5f));
    }

    private static BitmapFont PrepFont(BitmapFont source, float size, float borderWidth, Color borderColor, float shadowOffset, Color shadowColor)
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

        FreeTypeFontGenerator generator = GetGenerator(source.getData().fontFile);
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

    private static FileHandle GetFontDefaultFile(Settings.GameLanguage language) {
        STSStringConfigItem config = GetFontDefaultConfig(language);
        String value = config.Get();
        if (value != null && !value.isEmpty()) {
            FileHandle custom = Gdx.files.external(value);
            if (custom.exists()) {
                return custom;
            }
        }

        return Gdx.files.internal(GetFontDefaultPath(language));
    }

    private static FileHandle GetFontBoldFile(Settings.GameLanguage language) {
        STSStringConfigItem config = GetFontDefaultConfig(language);
        String value = config.Get();
        if (value != null && !value.isEmpty()) {
            FileHandle custom = Gdx.files.external(value);
            if (custom.exists()) {
                return custom;
            }
        }

        return Gdx.files.internal(GetFontDefaultPath(language));
    }

    private static STSStringConfigItem GetFontDefaultConfig(Settings.GameLanguage language)
    {
        switch (language)
        {
            case JPN:
                return EUIConfiguration.CustomJPNDefaultFont;
            case KOR:
                return EUIConfiguration.CustomKORDefaultFont;
            case ZHS:
                return EUIConfiguration.CustomZHSDefaultFont;
            case ZHT:
                return EUIConfiguration.CustomZHTDefaultFont;
            case POL:
            case UKR:
            case RUS:
                return EUIConfiguration.CustomRUSDefaultFont;
            case EPO:
                return EUIConfiguration.CustomEPODefaultFont;
            case GRE:
                return EUIConfiguration.CustomGREDefaultFont;
            case SRP:
            case SRB:
                return EUIConfiguration.CustomSRBDefaultFont;
            case THA:
                return EUIConfiguration.CustomTHADefaultFont;
            default:
                return EUIConfiguration.CustomENGDefaultFont;
        }
    }

    private static STSStringConfigItem GetFontBoldConfig(Settings.GameLanguage language)
    {
        switch (language)
        {
            case JPN:
                return EUIConfiguration.CustomJPNBoldFont;
            case KOR:
                return EUIConfiguration.CustomKORBoldFont;
            case ZHS:
                return EUIConfiguration.CustomZHSBoldFont;
            case ZHT:
                return EUIConfiguration.CustomZHTBoldFont;
            case POL:
            case UKR:
            case RUS:
                return EUIConfiguration.CustomRUSBoldFont;
            case EPO:
                return EUIConfiguration.CustomEPOBoldFont;
            case GRE:
                return EUIConfiguration.CustomGREBoldFont;
            case SRP:
            case SRB:
                return EUIConfiguration.CustomSRBBoldFont;
            case THA:
                return EUIConfiguration.CustomTHABoldFont;
            default:
                return EUIConfiguration.CustomENGBoldFont;
        }
    }

    private static String GetFontDefaultPath(Settings.GameLanguage language)
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
                return EPO_DEFAULT_FONT;
            case SRP:
            case SRB:
                return SRB_DEFAULT_FONT;
            case THA:
                return ENG_DEFAULT_FONT;
            default:
                return ENG_DEFAULT_FONT;
        }
    }

    private static String GetFontBoldPath(Settings.GameLanguage language)
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
                return EPO_BOLD_FONT;
            case SRP:
            case SRB:
                return SRB_BOLD_FONT;
            case THA:
                return ENG_BOLD_FONT;
            default:
                return ENG_BOLD_FONT;
        }
    }

    public static BitmapFont CreateFontForLanguage(Settings.GameLanguage language, boolean isLinearFiltering, float size, float borderWidth, Color borderColor, float shadowOffset, Color shadowColor) {
        FileHandle file = GetFontDefaultFile(language);
        BitmapFont preppedFont = PrepFont(GetGenerator(file), size, isLinearFiltering);
        return PrepFont(preppedFont, size, borderWidth, borderColor, shadowOffset, shadowColor);
    }

    public static boolean HasGlyph(BitmapFont font, char c)
    {
        return font.getData().hasGlyph(c);
    }
}