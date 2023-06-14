package extendedui.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.TupleT2;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class EUISmartText {
    private static final String NEWLINE = "NL";
    private static final StringBuilder builder = new StringBuilder();
    private static final GlyphLayout layout = new GlyphLayout();
    public static final Color ORANGE_TEXT_COLOR = new Color(1.0F, 0.5F, 0.25F, 1F);
    public static final Color INDIGO_TEXT_COLOR = new Color(0.65F, 0.37F, 1.F, 1F);
    public static final Color PINK_TEXT_COLOR = new Color(1.0F, 0.37F, 0.65F, 1F);
    public static final float CARD_ENERGY_IMG_WIDTH = 26.0F * Settings.scale;
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

    private static boolean getAndMove() {
        if (index < currentText.length()) {
            currentChar = currentText.charAt(index);
            index++;
            return true;
        }
        return false;
    }

    public static Color getColor(Character c) {
        switch (c) {
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
                EUIUtils.logWarning(EUIRenderHelpers.class, "Unknown color: #" + c);
                return mainColor;
        }
    }

    // Possible color formats:
    // #c: Single color
    // #AABBCCDD: RGBA
    public static Color getColor(String s) {
        if (s.length() == 1) {
            return getColor(s.charAt(0));
        }
        try {
            return Color.valueOf(s);
        }
        catch (NumberFormatException e) {
            EUIUtils.logWarning(EUIRenderHelpers.class, "Invalid color: #" + s);
            return mainColor;
        }
    }

    public static TextureRegion getSmallIcon(String id, boolean force) {
        switch (id) {
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
                EUIKeywordTooltip tooltip = EUIKeywordTooltip.findByID(id);
                return (tooltip != null && (force || tooltip.forceIcon)) ? tooltip.icon : null;
        }
    }

    public static float getSmartHeight(BitmapFont font, String text, float lineWidth) {
        return getSmartHeight(font, text, lineWidth, font.getLineHeight());
    }

    public static float getSmartHeight(BitmapFont font, String text, float lineWidth, float lineSpacing) {
        return getSmartSize(font, text, lineWidth, lineSpacing).v2;
    }

    public static TupleT2<Float, Float> getSmartSize(BitmapFont font, String text, float lineWidth, float lineSpacing) {
        return write(null, font, text, 0, 0, lineWidth, lineSpacing, Color.WHITE);
    }

    public static float getSmartWidth(BitmapFont font, String text) {
        return getSmartWidth(font, text, Integer.MAX_VALUE, font.getLineHeight());
    }

    public static float getSmartWidth(BitmapFont font, String text, float lineWidth, float lineSpacing) {
        return getSmartSize(font, text, lineWidth, lineSpacing).v1;
    }

    public static float getSmartWidth(BitmapFont font, String text, float lineSpacing) {
        return getSmartWidth(font, text, Integer.MAX_VALUE, lineSpacing);
    }

    private static Color getTooltipBackgroundColor(String id) {
        EUIKeywordTooltip tooltip = EUIKeywordTooltip.findByID(id);
        return (tooltip != null) ? tooltip.backgroundColor : null;
    }

    private static void obtainBlockColor() {
        if (getAndMove() && currentChar == '#') {
            StringBuilder subBuilder = new StringBuilder();
            while (getAndMove() && currentChar != ':') {
                subBuilder.append(currentChar);
            }
            blockColor = getColor(EUIUtils.popBuilder(subBuilder));
        }
        else if (currentChar != null) {
            blockColor = Settings.GOLD_COLOR;
            builder.append(currentChar);
        }
    }

    private static void obtainWordColor() {
        if (getAndMove()) {
            wordColor = getColor(currentChar);
        }
    }

    public static String parseKeywordLogicWithAmount(String logicString, int amount) {
        return parseLogicString(EUIUtils.format(logicString.substring(1), amount));
    }

    /* Interprets the string as a logic statement and returns the subset that matches the evaluated value
     *
     * A: Integer value to be evaluated.
     * @B=: Defines a condition block. Conditions start with @ and end with =. Subsequent text will be returned if the condition B is satisfied
     *
     *
     * Example: A@>1:times@:time
     * */
    public static String parseLogicString(String input) {
        int evaluated = 0;
        ArrayList<LogicBlock> conditions = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        LogicBlock currentBlock = null;
        LogicCondition current = null;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            switch (c) {
                // < > signals start of condition. If there already was one, add it to the block and make a new one
                case '<':
                case '>':
                case '%':
                case '&':
                case '!':
                case '?':
                    if (currentBlock == null) {
                        currentBlock = new LogicBlock();
                    }
                    else {
                        String condOutput = EUIUtils.popBuilder(buffer);
                        current.value = EUIUtils.parseInt(condOutput, evaluated);
                        currentBlock.conditions.add(current);
                    }
                    current = new LogicCondition(LogicComparison.typeFor(c));
                    break;
                // : signals end of condition definition. An empty condition means that this block always returns
                case ':':
                    if (currentBlock == null) {
                        currentBlock = new LogicBlock();
                    }
                    if (current == null) {
                        current = new LogicCondition(LogicComparison.True);
                    }
                    String condOutput = EUIUtils.popBuilder(buffer);
                    current.value = EUIUtils.parseInt(condOutput, evaluated);
                    currentBlock.conditions.add(current);
                    break;
                // @ $ signals end of block. If there was no conditionBlock, this goes into evaluated
                case '$':
                case '@':
                    String output = EUIUtils.popBuilder(buffer);
                    if (currentBlock != null) {
                        currentBlock.text = output;
                        conditions.add(currentBlock);
                        currentBlock = null;
                        current = null;
                    }
                    else if (StringUtils.isNumeric(output)) {
                        evaluated = Integer.parseInt(output);
                    }
                    break;
                default:
                    buffer.append(c);
            }
        }

        // Return the first block that passes
        for (LogicBlock block : conditions) {
            if (block.evaluate(evaluated)) {
                return block.text;
            }
        }

        return "";
    }

    public static TupleT2<Float, Float> write(SpriteBatch sb, BitmapFont font, String text, float x, float y, float lineWidth, float lineSpacing, Color baseColor) {
        return write(sb, font, text, x, y, lineWidth, lineSpacing, baseColor, false);
    }

    public static TupleT2<Float, Float> write(SpriteBatch sb, BitmapFont font, String text, float x, float y, float lineWidth, float lineSpacing, Color baseColor, boolean applyFontScaling) {
        index = 0;
        if (text != null) {
            builder.setLength(0);
            curWidth = 0f;
            curHeight = 0f;
            mainColor = baseColor;
            currentText = text;

            float fontScale = 0;
            try {
                fontScale = font.getScaleX();
            }
            catch (Exception e) {
                EUIUtils.logError(null, "OH NOES, " + text + " with font " + font);
                throw e;
            }


            if (currentFont != font || currentFontScale != fontScale) {
                currentFontScale = fontScale;
                currentFont = font;
                layout.setText(font, " ");
                spaceWidth = layout.width;
            }

            final float sizeMultiplier = applyFontScaling ? (0.667f + (currentFont.getScaleX() / 3f)) : 1;
            final float imgSize = sizeMultiplier * CARD_ENERGY_IMG_WIDTH;

            while (getAndMove()) {
                switch (currentChar) {
                    // Symbol
                    case '[':
                        writeToken(sb, x, y, lineWidth, lineSpacing, sizeMultiplier, imgSize, EUIConfiguration.enableDescriptionIcons.get());
                        break;
                    // Force symbol, ignoring user config settings
                    case '†':
                        writeToken(sb, x, y, lineWidth, lineSpacing, sizeMultiplier, imgSize, true);
                        break;
                    // End of symbol, needs to be manually ignored when icons are disabled
                    case ']':
                        break;
                    // Color
                    case '#':
                        obtainWordColor();
                        break;
                    // Logic Block
                    case '$':
                        writeLogic(sb, x, y, lineWidth, lineSpacing);
                        break;
                    // Color Block
                    case '{':
                        obtainBlockColor();
                        break;
                    // End Color Block
                    case '}':
                        String o = EUIUtils.popBuilder(builder);
                        if (!o.isEmpty()) {
                            writeWord(sb, o, x, y, lineWidth, lineSpacing);
                        }
                        blockColor = null;
                        break;
                    // Newline
                    case '|':
                        writeNewline(lineSpacing);
                        break;
                    // Tab
                    case '^':
                        writeTab(lineSpacing);
                        break;
                    default:
                        if (Character.isWhitespace(currentChar)) {
                            String output = EUIUtils.popBuilder(builder);
                            if (!output.isEmpty()) {
                                // Base game newlines
                                if (NEWLINE.equals(output)) {
                                    writeNewline(lineSpacing);
                                }
                                else {
                                    writeWord(sb, output, x, y, lineWidth, lineSpacing);
                                }
                            }
                        }
                        else {
                            builder.append(currentChar);
                        }
                }
            }
            String output = EUIUtils.popBuilder(builder);
            if (!output.isEmpty()) {
                writeWord(sb, output, x, y, lineWidth, lineSpacing);
            }

            return new TupleT2<>(curWidth, curHeight);
        }
        return new TupleT2<>(0f, 0f);
    }

    public static TupleT2<Float, Float> write(SpriteBatch sb, BitmapFont font, String text, float x, float y, float lineWidth, Color baseColor) {
        return write(sb, font, text, x, y, lineWidth, font.getLineHeight() * Settings.scale, baseColor);
    }

    private static void writeLogic(SpriteBatch sb, float x, float y, float lineWidth, float lineSpacing) {
        StringBuilder subBuilder = new StringBuilder();
        while (getAndMove()) {
            subBuilder.append(currentChar);
            if (currentChar == '$') {
                break;
            }
        }
        String output = parseLogicString(EUIUtils.popBuilder(subBuilder));
        if (output != null && !output.isEmpty()) {
            writeWord(sb, output, x, y, lineWidth, lineSpacing);
        }
    }

    private static void writeNewline(float lineSpacing) {
        curWidth = 0f;
        curHeight -= lineSpacing;
    }

    private static void writeTab(float spaceWidth) {
        curWidth += spaceWidth * 5.0f;
    }

    private static void writeToken(SpriteBatch sb, float x, float y, float lineWidth, float lineSpacing, float iconScaling, float imageSize, boolean force) {
        StringBuilder subBuilder = new StringBuilder();
        while (getAndMove() && currentChar != ']') {
            subBuilder.append(currentChar);
        }

        String iconID = EUIUtils.popBuilder(subBuilder);
        Color backgroundColor = getTooltipBackgroundColor(iconID);
        TextureRegion icon = getSmallIcon(iconID, force);
        if (icon != null) {
            final float orbWidth = icon.getRegionWidth();
            final float orbHeight = icon.getRegionHeight();
            final float halfOrbWidth = orbWidth / 2f;
            final float halfOrbHeight = orbHeight / 2f;
            final float scaleX = imageSize / orbWidth;
            final float scaleY = imageSize / orbHeight;

            //sb.setColor(1f, 1f, 1f, baseColor.a);
            if (curWidth + imageSize > lineWidth) {
                curHeight -= lineSpacing;
                if (sb != null) {
                    if (backgroundColor != null) {
                        sb.setColor(backgroundColor);
                        sb.draw(EUIRM.images.baseBadge.texture(), x - halfOrbWidth + iconScaling * 13f * Settings.scale, y + curHeight - iconScaling * halfOrbHeight - 8f * Settings.scale,
                                halfOrbWidth, halfOrbHeight,
                                orbWidth, orbHeight, scaleX, scaleY, 0f,
                                icon.getRegionX(), icon.getRegionY(), icon.getRegionWidth(),
                                icon.getRegionHeight(), false, false);
                    }
                    sb.setColor(mainColor);
                    sb.draw(icon, x - halfOrbWidth + 13f * Settings.scale, y + curHeight - halfOrbHeight - 8f * Settings.scale, halfOrbWidth, halfOrbHeight, orbWidth, orbHeight, scaleX, scaleY, 0f);
                }
                curWidth = imageSize + spaceWidth;
            }
            else {
                if (sb != null) {
                    if (backgroundColor != null) {
                        sb.setColor(backgroundColor);
                        sb.draw(EUIRM.images.baseBadge.texture(), x + curWidth - halfOrbWidth + iconScaling * 13f * Settings.scale, y + curHeight - iconScaling * halfOrbHeight - 8f * Settings.scale,
                                halfOrbWidth, halfOrbHeight, orbWidth, orbHeight, scaleX, scaleY, 0f,
                                icon.getRegionX(), icon.getRegionY(), icon.getRegionWidth(),
                                icon.getRegionHeight(), false, false);
                    }
                    sb.setColor(mainColor);
                    sb.draw(icon, x + curWidth - halfOrbWidth + 13f * Settings.scale, y + curHeight - halfOrbHeight - 8f * Settings.scale, halfOrbWidth, halfOrbHeight, orbWidth, orbHeight, scaleX, scaleY, 0f);
                }

                curWidth += imageSize + spaceWidth;
            }
        }
        else {
            wordColor = Settings.GOLD_COLOR;
            writeWord(sb, iconID, x, y, lineWidth, lineSpacing);
        }
    }

    private static void writeWord(SpriteBatch sb, String word, float x, float y, float lineWidth, float lineSpacing) {
        if (wordColor != null) {
            currentFont.setColor(wordColor);
            wordColor = null;
        }
        else if (blockColor != null) {
            currentFont.setColor(blockColor);
        }
        else {
            currentFont.setColor(mainColor);
        }

        layout.setText(currentFont, word);
        if (curWidth + layout.width > lineWidth) {
            curHeight -= lineSpacing;
            if (sb != null) {
                currentFont.draw(sb, word, x, y + curHeight);
            }
            curWidth = layout.width + spaceWidth;
        }
        else {
            if (sb != null) {
                currentFont.draw(sb, word, x + curWidth, y + curHeight);
            }
            curWidth += layout.width + spaceWidth;
        }
    }

    public enum LogicComparison {
        Greater,
        Less,
        Modulo,
        EndsWith,
        Unequal,
        Equal,
        True;

        public static LogicComparison typeFor(char c) {
            switch (c) {
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

    public static class LogicBlock {
        final public ArrayList<LogicCondition> conditions;
        public String text;

        public LogicBlock() {
            conditions = new ArrayList<>();
        }

        public boolean evaluate(int input) {
            return EUIUtils.any(conditions, block -> block.evaluate(input));
        }
    }

    public static class LogicCondition {
        public int value = 0;
        public LogicComparison type;

        public LogicCondition(LogicComparison type) {
            this.type = type;
        }

        public boolean evaluate(int input) {
            switch (type) {
                case Greater:
                    return input > value;
                case Less:
                    return input < value;
                case Modulo:
                    return input % value == 0;
                case EndsWith:
                    int denominator = 10;
                    while (denominator < value) {
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
