package extendedui.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;

import java.util.HashMap;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

//TODO add support for more languages
public class EUIFontHelper
{
    protected static FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
    protected static FreeTypeFontGenerator.FreeTypeBitmapFontData data = new FreeTypeFontGenerator.FreeTypeBitmapFontData();
    protected static HashMap<String, FreeTypeFontGenerator> generators = new HashMap<>();
    protected static FileHandle fontFile = null;
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
        fontFile = GetFontFileForLanguage(Settings.language);

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
        //
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

    private static FileHandle GetFontFileForLanguage(Settings.GameLanguage language) {
        switch (language) {
            case JPN:
                return Gdx.files.internal("font/jpn/NotoSansCJKjp-Regular.otf");
            case KOR:
                return Gdx.files.internal("font/kor/GyeonggiCheonnyeonBatangBold.ttf");
            case ZHS:
                return Gdx.files.internal("font/zhs/NotoSansMonoCJKsc-Regular.otf");
            case ZHT:
                return Gdx.files.internal("font/zht/NotoSansCJKtc-Regular.otf");
            case RUS:
                return Gdx.files.internal("font/rus/FiraSansExtraCondensed-Regular.ttf");
            default:
                return Gdx.files.internal("font/Kreon-Regular.ttf");
        }
    }

    public static BitmapFont CreateFontForLanguage(Settings.GameLanguage language, boolean isLinearFiltering, float size, float borderWidth, Color borderColor, float shadowOffset, Color shadowColor) {
        FileHandle file = GetFontFileForLanguage(language);
        BitmapFont preppedFont = PrepFont(GetGenerator(file), size, isLinearFiltering);
        return PrepFont(preppedFont, size, borderWidth, borderColor, shadowOffset, shadowColor);
    }
}