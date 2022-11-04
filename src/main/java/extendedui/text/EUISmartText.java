package extendedui.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.TupleT2;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class EUISmartText
{
    public static final Color ORANGE_TEXT_COLOR = new Color(1.0F, 0.5F, 0.25F, 1F);
    public static final Color INDIGO_TEXT_COLOR = new Color(0.65F, 0.37F, 1.F, 1F);
    public static final Color PINK_TEXT_COLOR = new Color(1.0F, 0.37F, 0.65F, 1F);
    public static final float CARD_ENERGY_IMG_WIDTH = 26.0F * Settings.scale;
    private static final StringBuilder builder = new StringBuilder();
    private static final GlyphLayout layout = new GlyphLayout();
    private static Character currentChar;
    private static String currentText;
    private static Color blockColor;
    private static Color wordColor;
    private static Color mainColor = Color.WHITE;
    private static BitmapFont currentFont;
    private static float currentFontScale;
    private static float spaceWidth;
    private static float curWidth;
    private static float curHeight;
    private static int index;

    private static boolean GetAndMove()
    {
        if (index < currentText.length())
        {
            currentChar = currentText.charAt(index);
            index++;
            return true;
        }
        return false;
    }

    // Possible color formats:
    // #c: Single color
    // #AABBCCDD: RGBA
    public static Color GetColor(String s)
    {
        if (s.length() == 1)
        {
            return GetColor(s.charAt(0));
        }
        try
        {
            return Color.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            EUIUtils.LogWarning(EUIRenderHelpers.class, "Invalid color: #" + s);
            return mainColor;
        }
    }

    public static Color GetColor(Character c)
    {
        switch (c)
        {
            case 'c':
                return Settings.CREAM_COLOR;
            case 'e':
                return Color.GRAY;
            case 'b':
                return Settings.BLUE_TEXT_COLOR;
            case 'g':
                return Settings.GREEN_TEXT_COLOR;
            case 'p':
                return Settings.PURPLE_COLOR;
            case 'r':
                return Settings.RED_TEXT_COLOR;
            case 'y':
                return Settings.GOLD_COLOR;
            case 'o':
                return ORANGE_TEXT_COLOR;
            case 'i':
                return INDIGO_TEXT_COLOR;
            case 'k':
                return PINK_TEXT_COLOR;
            case 'l':
                return Color.LIME;
            case '#':
                return mainColor;
            default:
                EUIUtils.LogWarning(EUIRenderHelpers.class, "Unknown color: #" + c);
                return mainColor;
        }
    }

    public static TextureRegion GetSmallIcon(String id)
    {
        switch (id)
        {
            case "E":
                return AbstractDungeon.player != null ? AbstractDungeon.player.getOrb() : AbstractCard.orb_red;
            case "CARD":
                return AbstractCard.orb_card;
            case "POTION":
                return AbstractCard.orb_potion;
            case "RELIC":
                return AbstractCard.orb_relic;
            case "SPECIAL":
                return AbstractCard.orb_special;

            default:
                EUITooltip tooltip = EUITooltip.FindByID(id);
                return (tooltip != null) ? tooltip.icon : null;
        }
    }

    public static float GetSmartHeight(BitmapFont font, String text, float lineWidth)
    {
        return GetSmartHeight(font, text, lineWidth, font.getLineHeight());
    }

    public static float GetSmartHeight(BitmapFont font, String text, float lineWidth, float lineSpacing)
    {
        return Write(null, font, text, 0, 0, lineWidth, lineSpacing, Color.WHITE).V2;
    }

    public static float GetSmartWidth(BitmapFont font, String text)
    {
        return GetSmartWidth(font, text, Integer.MAX_VALUE, font.getLineHeight());
    }

    public static float GetSmartWidth(BitmapFont font, String text, float lineSpacing)
    {
        return GetSmartWidth(font, text, Integer.MAX_VALUE, lineSpacing);
    }

    public static float GetSmartWidth(BitmapFont font, String text, float lineWidth, float lineSpacing)
    {
        return Write(null, font, text, 0, 0, lineWidth, lineSpacing, Color.WHITE).V1;
    }

    private static Color GetTooltipBackgroundColor(String id)
    {
        EUITooltip tooltip = EUITooltip.FindByID(id);
        return (tooltip != null) ? tooltip.backgroundColor : null;
    }

    private static boolean HasNext(int offset, char target)
    {
        offset += index;
        if (offset < currentText.length())
        {
            return currentText.charAt(offset) == target;
        }
        return false;
    }

    private static boolean HasWhitespace(int offset)
    {
        offset += index;
        if (offset < currentText.length())
        {
            return Character.isWhitespace(currentText.charAt(offset));
        }
        return false;
    }

    private static void ObtainBlockColor()
    {
        if (GetAndMove() && currentChar == '#')
        {
            StringBuilder subBuilder = new StringBuilder();
            while (GetAndMove() && currentChar != ':')
            {
                subBuilder.append(currentChar);
            }
            blockColor = GetColor(EUIUtils.PopBuilder(subBuilder));
        }
        else if (currentChar != null)
        {
            blockColor = Settings.GOLD_COLOR;
            builder.append(currentChar);
        }
    }

    private static void ObtainWordColor()
    {
        if (GetAndMove())
        {
            wordColor = GetColor(currentChar);
        }
    }

    /* Interprets the string as a logic statement and returns the subset that matches the evaluated value
     *
     * A: Integer value to be evaluated.
     * @B=: Defines a condition block. Conditions start with @ and end with =. Subsequent text will be returned if the condition B is satisfied
     *
     *
     * Example: A@>1:times@:time
     * */
    public static String ParseLogicString(String input)
    {
        int evaluated = 0;
        ArrayList<LogicBlock> conditions = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        LogicBlock currentBlock = null;
        LogicCondition current = null;

        for (int i = 0; i < input.length(); i++)
        {
            char c = input.charAt(i);

            switch (c)
            {
                // < > signals start of condition. If there already was one, add it to the block and make a new one
                case '<':
                case '>':
                case '%':
                case '&':
                case '!':
                case '?':
                    if (currentBlock == null)
                    {
                        currentBlock = new LogicBlock();
                    }
                    else
                    {
                        String condOutput = EUIUtils.PopBuilder(buffer);
                        current.value = StringUtils.isNumeric(condOutput) ? Integer.parseInt(condOutput) : evaluated;
                        currentBlock.conditions.add(current);
                    }
                    current = new LogicCondition(LogicComparison.TypeFor(c));
                    break;
                // : signals end of condition definition. An empty condition means that this block always returns
                case ':':
                    if (currentBlock == null)
                    {
                        currentBlock = new LogicBlock();
                    }
                    if (current == null)
                    {
                        current = new LogicCondition(LogicComparison.True);
                    }
                    String condOutput = EUIUtils.PopBuilder(buffer);
                    current.value = StringUtils.isNumeric(condOutput) ? Integer.parseInt(condOutput) : evaluated;
                    currentBlock.conditions.add(current);
                    break;
                // @ $ signals end of block. If there was no conditionBlock, this goes into evaluated
                case '$':
                case '@':
                    String output = EUIUtils.PopBuilder(buffer);
                    if (currentBlock != null)
                    {
                        currentBlock.text = output;
                        conditions.add(currentBlock);
                        currentBlock = null;
                        current = null;
                    }
                    else if (StringUtils.isNumeric(output))
                    {
                        evaluated = Integer.parseInt(output);
                    }
                    break;
                default:
                    buffer.append(c);
            }
        }

        // Return the first block that passes
        for (LogicBlock block : conditions)
        {
            if (block.Evaluate(evaluated))
            {
                return block.text;
            }
        }

        return "";
    }

    public static TupleT2<Float, Float> Write(SpriteBatch sb, BitmapFont font, String text, float x, float y, float lineWidth, Color baseColor)
    {
        return Write(sb, font, text, x, y, lineWidth, font.getLineHeight() * Settings.scale, baseColor);
    }

    public static TupleT2<Float, Float> Write(SpriteBatch sb, BitmapFont font, String text, float x, float y, float lineWidth, float lineSpacing, Color baseColor)
    {
        return Write(sb, font, text, x, y, lineWidth, lineSpacing, baseColor, false);
    }

    public static TupleT2<Float, Float> Write(SpriteBatch sb, BitmapFont font, String text, float x, float y, float lineWidth, float lineSpacing, Color baseColor, boolean applyFontScaling)
    {
        index = 0;
        if (text != null)
        {
            builder.setLength(0);
            curWidth = 0f;
            curHeight = 0f;
            mainColor = baseColor;
            currentText = text;

            final float fontScale = font.getScaleX();
            if (currentFont != font || currentFontScale != fontScale)
            {
                currentFontScale = fontScale;
                currentFont = font;
                layout.setText(font, " ");
                spaceWidth = layout.width;
            }

            final float sizeMultiplier = applyFontScaling ? (0.667f + (currentFont.getScaleX() / 3f)) : 1;
            final float imgSize = sizeMultiplier * CARD_ENERGY_IMG_WIDTH;

            while (GetAndMove())
            {
                switch (currentChar)
                {
                    // Symbol
                    case '[':
                        WriteToken(sb, x, y, lineWidth, lineSpacing, sizeMultiplier, imgSize);
                        break;
                    // Color
                    case '#':
                        ObtainWordColor();
                        break;
                    // Logic Block
                    case '$':
                        WriteLogic(sb, x, y, lineWidth, lineSpacing);
                        break;
                    // Color Block
                    case '{':
                        ObtainBlockColor();
                        break;
                    // End Color Block
                    case '}':
                        String o = EUIUtils.PopBuilder(builder);
                        if (!o.isEmpty())
                        {
                            WriteWord(sb, o, x, y, lineWidth, lineSpacing);
                        }
                        blockColor = null;
                        break;
                    // Newline
                    case '|':
                        WriteNewline(lineSpacing);
                        break;
                    // Tab
                    case '^':
                        WriteTab(lineSpacing);
                        break;
                    default:
                        if (Character.isWhitespace(currentChar))
                        {
                            String output = EUIUtils.PopBuilder(builder);
                            if (!output.isEmpty())
                            {
                                WriteWord(sb, output, x, y, lineWidth, lineSpacing);
                            }
                            // Base game newlines
                            if (HasNext(1, 'N') && HasNext(2, 'L') && HasWhitespace(3))
                            {
                                WriteNewline(lineSpacing);
                                index += 3;
                            }
                        }
                        else
                        {
                            builder.append(currentChar);
                        }
                }
            }
            String output = EUIUtils.PopBuilder(builder);
            if (!output.isEmpty())
            {
                WriteWord(sb, output, x, y, lineWidth, lineSpacing);
            }

            return new TupleT2<>(curWidth, curHeight);
        }
        return new TupleT2<>(0f, 0f);
    }

    private static void WriteLogic(SpriteBatch sb, float x, float y, float lineWidth, float lineSpacing)
    {
        StringBuilder subBuilder = new StringBuilder();
        while (GetAndMove())
        {
            subBuilder.append(currentChar);
            if (currentChar == '$')
            {
                break;
            }
        }
        String output = ParseLogicString(EUIUtils.PopBuilder(subBuilder));
        if (output != null && !output.isEmpty())
        {
            WriteWord(sb, output, x, y, lineWidth, lineSpacing);
        }
    }

    private static void WriteNewline(float lineSpacing)
    {
        curWidth = 0f;
        curHeight -= lineSpacing;
    }

    private static void WriteTab(float spaceWidth)
    {
        curWidth += spaceWidth * 5.0f;
    }

    private static void WriteToken(SpriteBatch sb, float x, float y, float lineWidth, float lineSpacing, float iconScaling, float imageSize)
    {
        StringBuilder subBuilder = new StringBuilder();
        while (GetAndMove() && currentChar != ']')
        {
            subBuilder.append(currentChar);
        }

        String iconID = EUIUtils.PopBuilder(subBuilder);
        Color backgroundColor = GetTooltipBackgroundColor(iconID);
        TextureRegion icon = GetSmallIcon(iconID);
        if (icon != null)
        {
            final float orbWidth = icon.getRegionWidth();
            final float orbHeight = icon.getRegionHeight();
            final float scaleX = imageSize / orbWidth;
            final float scaleY = imageSize / orbHeight;

            //sb.setColor(1f, 1f, 1f, baseColor.a);
            if (curWidth + imageSize > lineWidth)
            {
                curHeight -= lineSpacing;
                if (sb != null)
                {
                    if (backgroundColor != null)
                    {
                        sb.setColor(backgroundColor);
                        sb.draw(EUIRM.Images.Base_Badge.Texture(), x - orbWidth / 2f + iconScaling * 13f * Settings.scale, y + curHeight - iconScaling * orbHeight / 2f - 8f * Settings.scale,
                                orbWidth / 2f, orbHeight / 2f,
                                orbWidth, orbHeight, scaleX, scaleY, 0f,
                                icon.getRegionX(), icon.getRegionY(), icon.getRegionWidth(),
                                icon.getRegionHeight(), false, false);
                    }
                    sb.setColor(mainColor);
                    sb.draw(icon, x - orbWidth / 2f + 13f * Settings.scale, y + curHeight - orbHeight / 2f - 8f * Settings.scale, orbWidth / 2f, orbHeight / 2f, orbWidth, orbHeight, scaleX, scaleY, 0f);
                }
                curWidth = imageSize + spaceWidth;
            }
            else
            {
                if (sb != null)
                {
                    if (backgroundColor != null)
                    {
                        sb.setColor(backgroundColor);
                        sb.draw(EUIRM.Images.Base_Badge.Texture(), x + curWidth - orbWidth / 2f + iconScaling * 13f * Settings.scale, y + curHeight - iconScaling * orbHeight / 2f - 8f * Settings.scale,
                                orbWidth / 2f, orbHeight / 2f, orbWidth, orbHeight, scaleX, scaleY, 0f,
                                icon.getRegionX(), icon.getRegionY(), icon.getRegionWidth(),
                                icon.getRegionHeight(), false, false);
                    }
                    sb.setColor(mainColor);
                    sb.draw(icon, x + curWidth - orbWidth / 2f + 13f * Settings.scale, y + curHeight - orbHeight / 2f - 8f * Settings.scale, orbWidth / 2f, orbHeight / 2f, orbWidth, orbHeight, scaleX, scaleY, 0f);
                }

                curWidth += imageSize + spaceWidth;
            }
        }
    }

    private static void WriteWord(SpriteBatch sb, String word, float x, float y, float lineWidth, float lineSpacing)
    {
        if (wordColor != null)
        {
            currentFont.setColor(wordColor);
            wordColor = null;
        }
        else if (blockColor != null)
        {
            currentFont.setColor(blockColor);
        }
        else
        {
            currentFont.setColor(mainColor);
        }

        layout.setText(currentFont, word);
        if (curWidth + layout.width > lineWidth)
        {
            curHeight -= lineSpacing;
            if (sb != null)
            {
                currentFont.draw(sb, word, x, y + curHeight);
            }
            curWidth = layout.width + spaceWidth;
        }
        else
        {
            if (sb != null)
            {
                currentFont.draw(sb, word, x + curWidth, y + curHeight);
            }
            curWidth += layout.width + spaceWidth;
        }
    }

    public enum LogicComparison
    {
        Greater,
        Less,
        Modulo,
        EndsWith,
        Unequal,
        Equal,
        True;

        public static LogicComparison TypeFor(char c)
        {
            switch (c)
            {
                case '>':
                    return LogicComparison.Greater;
                case '<':
                    return LogicComparison.Less;
                case '%':
                    return LogicComparison.Modulo;
                case '&':
                    return LogicComparison.EndsWith;
                case '!':
                    return LogicComparison.Unequal;
                case '?':
                    return LogicComparison.Equal;
            }
            return LogicComparison.True;
        }
    }

    public static class LogicBlock
    {
        final public ArrayList<LogicCondition> conditions;
        public String text;

        public LogicBlock()
        {
            conditions = new ArrayList<>();
        }

        public boolean Evaluate(int input)
        {
            return EUIUtils.Any(conditions, block -> block.Evaluate(input));
        }
    }

    public static class LogicCondition
    {
        public int value = 0;
        public LogicComparison type;

        public LogicCondition(LogicComparison type)
        {
            this.type = type;
        }

        public boolean Evaluate(int input)
        {
            switch (type)
            {
                case Greater:
                    return input > value;
                case Less:
                    return input < value;
                case Modulo:
                    return input % value == 0;
                case EndsWith:
                    int denominator = 10;
                    while (denominator < value)
                    {
                        denominator *= 10;
                    }
                    return input % denominator == value;
                case Unequal:
                    return input != value;
                case Equal:
                    return input == value;
            }
            return true;
        }
    }
}
