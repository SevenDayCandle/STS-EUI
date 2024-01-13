package extendedui.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import com.evacipated.cardcrawl.mod.stslib.icons.CustomIconHelper;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.TupleT2;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class EUITextHelper {
    private static final StringBuilder builder = new StringBuilder();
    private static final GlyphLayout layout = new GlyphLayout();
    private static final TupleT2<Float, Float> size = new TupleT2<>();
    public static final String NEWLINE = "NL";
    public static final Color ORANGE_TEXT_COLOR = new Color(1.0F, 0.5F, 0.25F, 1F);
    public static final Color INDIGO_TEXT_COLOR = new Color(0.65F, 0.47F, 1.F, 1F);
    public static final Color PINK_TEXT_COLOR = new Color(1.0F, 0.37F, 0.65F, 1F);
    public static final float CARD_ENERGY_IMG_WIDTH = 26.0F * Settings.scale;
    private static Character currentChar;
    private static CharSequence currentText;
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

    public static GlyphLayout.GlyphRun getLayoutRun(int index) {
        return index < layout.runs.size ? layout.runs.get(index) : null;
    }

    public static int getLayoutRunLength() {
        return layout.runs.size;
    }

    public static float getLayoutWidth() {
        return layout.width;
    }

    public static float getSmartHeight(BitmapFont font, String text, float lineWidth) {
        return getSmartHeight(font, text, lineWidth, font.getLineHeight());
    }

    public static float getSmartHeight(BitmapFont font, String text, float lineWidth, float lineSpacing) {
        return getSmartSize(font, text, lineWidth, lineSpacing).v2;
    }

    public static TupleT2<Float, Float> getSmartSize(BitmapFont font, String text, float lineWidth, float lineSpacing) {
        return renderSmart(null, font, text, 0, 0, lineWidth, lineSpacing, Color.WHITE);
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

    public static float getTextWidth(BitmapFont font, String text) {
        layout.setText(font, text);
        return layout.width;
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

    public static void renderFont(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, Color c) {
        font.setColor(c);
        font.draw(sb, msg, x, y);
    }

    public static void renderFont(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, float targetWidth, int halign, boolean wrap, Color c) {
        font.setColor(c);
        font.draw(sb, msg, x, y, targetWidth, halign, wrap);
    }

    public static void renderFontCentered(SpriteBatch sb, BitmapFont font, CharSequence text, Hitbox hb, Color color) {
        renderFontCentered(sb, font, text, hb.cX, hb.cY, color);
    }

    public static void renderFontCentered(SpriteBatch sb, BitmapFont font, CharSequence text, Hitbox hb, Color color, float scale) {
        renderFontCentered(sb, font, text, hb.cX, hb.cY, color, scale);
    }

    public static void renderFontCentered(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, Color c) {
        layout.setText(font, msg);
        renderFont(sb, font, msg, x - layout.width / 2.0F, y + layout.height / 2.0F, c);
    }

    public static void renderFontCentered(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, Color c, float scale) {
        font.getData().setScale(scale);
        layout.setText(font, msg);
        renderFont(sb, font, msg, x - layout.width / 2.0F, y + layout.height / 2.0F, c);
        font.getData().setScale(1.0F);
    }

    public static void renderFontCentered(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y) {
        layout.setText(font, msg);
        renderFont(sb, font, msg, x - layout.width / 2.0F, y + layout.height / 2.0F, Color.WHITE);
    }

    public static void renderFontCenteredHeight(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, float lineWidth, Color c) {
        layout.setText(font, msg, c, lineWidth, 1, true);
        font.setColor(c);
        font.draw(sb, msg, x, y + layout.height / 2.0F, lineWidth, 1, true);
    }

    public static void renderFontCenteredHeight(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, float lineWidth, Color c, float scale) {
        font.getData().setScale(scale);
        layout.setText(font, msg, c, lineWidth, 1, true);
        font.setColor(c);
        font.draw(sb, msg, x, y + layout.height / 2.0F, lineWidth, 1, true);
        font.getData().setScale(1.0F);
    }

    public static void renderFontCenteredHeight(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, Color c) {
        layout.setText(font, msg);
        renderFont(sb, font, msg, x, y + layout.height / 2.0F, c);
    }

    public static void renderFontCenteredHeight(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y) {
        layout.setText(font, msg);
        renderFont(sb, font, msg, x, y + layout.height / 2.0F, Color.WHITE);
    }

    public static void renderFontCenteredTopAligned(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, Color c) {
        layout.setText(font, "lL");
        font.setColor(c);
        font.draw(sb, msg, x, y + layout.height / 2.0F, 0.0F, 1, false);
    }

    public static void renderFontCenteredWidth(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, Color c) {
        layout.setText(font, msg);
        renderFont(sb, font, msg, x - layout.width / 2.0F, y, c);
    }

    public static void renderFontCenteredWidth(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y) {
        layout.setText(font, msg);
        renderFont(sb, font, msg, x - layout.width / 2.0F, y, Color.WHITE);
    }

    public static void renderFontLeft(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, Color c) {
        layout.setText(font, msg);
        renderFont(sb, font, msg, x, y + layout.height / 2.0F, c);
    }

    public static void renderFontLeft(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, float targetWidth, int halign, boolean wrap, Color c) {
        layout.setText(font, msg, c, targetWidth, halign, wrap);
        renderFont(sb, font, msg, x, y, targetWidth, halign, wrap, c);
    }

    public static void renderFontLeftDownAligned(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, Color c) {
        layout.setText(font, msg);
        renderFont(sb, font, msg, x, y + layout.height, c);
    }

    public static void renderFontLeftTopAligned(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, Color c) {
        layout.setText(font, msg);
        renderFont(sb, font, msg, x, y, c);
    }

    public static void renderFontRightAligned(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, Color c) {
        layout.setText(font, msg);
        renderFont(sb, font, msg, x - layout.width, y + layout.height / 2.0F, c);
    }

    public static void renderFontRightAligned(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, float targetWidth, int halign, boolean wrap, Color c) {
        layout.setText(font, msg, c, targetWidth, halign, wrap);
        renderFont(sb, font, msg, x - layout.width, y, targetWidth, halign, wrap, c);
    }

    public static void renderFontRightToLeft(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, Color c) {
        layout.setText(font, msg, c, 1.0F, 18, false);
        font.setColor(c);
        font.draw(sb, msg, x - layout.width, y);
    }

    public static void renderFontRightTopAligned(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, Color c) {
        layout.setText(font, msg);
        renderFont(sb, font, msg, x - layout.width, y, c);
    }

    public static void renderFontRightTopAligned(SpriteBatch sb, BitmapFont font, CharSequence msg, float x, float y, float scale, Color c) {
        font.getData().setScale(1.0F);
        layout.setText(font, msg);
        float offsetX = layout.width / 2.0F;
        float offsetY = layout.height;
        font.getData().setScale(scale);
        layout.setText(font, msg);
        renderFont(sb, font, msg, x - layout.width / 2.0F - offsetX, y + layout.height / 2.0F + offsetY, c);
    }

    public static TupleT2<Float, Float> renderSmart(SpriteBatch sb, BitmapFont font, CharSequence text, float x, float y, float lineWidth, float lineSpacing, Color baseColor) {
        return renderSmart(sb, font, text, x, y, lineWidth, lineSpacing, baseColor, false);
    }

    public static TupleT2<Float, Float> renderSmart(SpriteBatch sb, BitmapFont font, CharSequence text, float x, float y, float lineWidth, float lineSpacing, Color baseColor, boolean applyFontScaling) {
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
                        writeToken(sb, x, y, lineWidth, lineSpacing, sizeMultiplier, imgSize, spaceWidth, EUIConfiguration.enableDescriptionIcons.get());
                        break;
                    // Force symbol, ignoring user config settings
                    case '†':
                        writeToken(sb, x, y, lineWidth, lineSpacing, sizeMultiplier, imgSize, spaceWidth, true);
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
                            writeWord(sb, o, x, y, lineWidth, lineSpacing, spaceWidth);
                        }
                        blockColor = null;
                        break;
                    // Newline
                    case '|':
                        writeNewline(lineSpacing);
                        break;
                    // Tab
                    case '`':
                        writeTab(lineSpacing);
                        break;
                    case '。':
                        builder.append(currentChar);
                        String op = EUIUtils.popBuilder(builder); // This will never be empty/newline
                        writeWord(sb, op, x, y, lineWidth, lineSpacing, spaceWidth);
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
                                    writeWord(sb, output, x, y, lineWidth, lineSpacing, spaceWidth);
                                }
                            }
                        }
                        // Ideographs in Chinese might not be separated by spaces
                        else if (Character.isIdeographic(currentChar)) {
                            builder.append(currentChar);
                            String output = EUIUtils.popBuilder(builder); // This will never be empty/newline
                            writeWord(sb, output, x, y, lineWidth, lineSpacing, 0); // Do not add space
                        }
                        else {
                            builder.append(currentChar);
                        }
                }
            }
            String output = EUIUtils.popBuilder(builder);
            if (!output.isEmpty()) {
                writeWord(sb, output, x, y, lineWidth, lineSpacing, spaceWidth);
            }

            size.v1 = curWidth;
            size.v2 = curHeight;
            return size;
        }
        size.v1 = 0f;
        size.v2 = 0f;
        return size;
    }

    public static TupleT2<Float, Float> renderSmart(SpriteBatch sb, BitmapFont font, CharSequence text, float x, float y, float lineWidth, Color baseColor) {
        return renderSmart(sb, font, text, x, y, lineWidth, font.getLineHeight() * Settings.scale, baseColor);
    }

    public static String replaceLogic(String baseString) {
        if (baseString == null) {
            return EUIUtils.EMPTY_STRING;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < baseString.length(); i++) {
            char c = baseString.charAt(i);
            if (c == '$') {
                StringBuilder sub = new StringBuilder();
                while (i + 1 < baseString.length()) {
                    i += 1;
                    c = baseString.charAt(i);
                    sub.append(c);
                    if (c == '$') {
                        break;
                    }
                }
                sb.append(EUITextHelper.parseLogicString(sub.toString()));
                break;
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
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
            writeWord(sb, output, x, y, lineWidth, lineSpacing, spaceWidth);
        }
    }

    private static void writeNewline(float lineSpacing) {
        curWidth = 0f;
        curHeight -= lineSpacing;
    }

    private static void writeTab(float spaceWidth) {
        curWidth += spaceWidth * 5.0f;
    }

    private static void writeToken(SpriteBatch sb, float x, float y, float lineWidth, float lineSpacing, float iconScaling, float imageSize, float spaceWidth, boolean force) {
        StringBuilder subBuilder = new StringBuilder();
        while (getAndMove() && currentChar != ']') {
            subBuilder.append(currentChar);
        }

        String iconID = EUIUtils.popBuilder(subBuilder);
        EUIKeywordTooltip tooltip = EUIKeywordTooltip.findByIDTemp(iconID);
        Color backgroundColor = null;
        TextureRegion icon = null;
        if (tooltip != null) {
            backgroundColor = tooltip.badgeColor;
            icon = (force || tooltip.forceIcon) ? tooltip.icon : null;
        }
        else {
            // Check for custom keyword icons
            String iconName = '[' + iconID + ']';
            AbstractCustomIcon customIcon = CustomIconHelper.getIcon(iconName);
            if (customIcon != null) {
                // TODO get the actual corresponding keyword name if keyword icons are turned off
                icon = customIcon.region;
            }
        }

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
            writeWord(sb, tooltip != null ? tooltip.title : iconID, x, y, lineWidth, lineSpacing, spaceWidth);
        }
    }

    private static void writeWord(SpriteBatch sb, String word, float x, float y, float lineWidth, float lineSpacing, float spaceWidth) {
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
