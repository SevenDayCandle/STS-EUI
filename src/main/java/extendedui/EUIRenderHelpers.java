package extendedui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.tommyettinger.colorful.Shaders;
import com.github.tommyettinger.colorful.rgb.ColorTools;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.text.EUISmartText;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredTexture;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod and https://github.com/SevenDayCandle/STS-FoolMod

public class EUIRenderHelpers {
    public static final Color DARKENED_SCREEN = new Color(0.0F, 0.0F, 0.0F, 0.5F);
    protected static final String SHADER_BLUR_FRAGMENT = "shaders/blurFragment.glsl";
    protected static final String SHADER_GLITCH_FRAGMENT = "shaders/glitchFragment.glsl";
    protected static final String SHADER_GRAYSCALE_FRAGMENT = "shaders/grayscaleFragment.glsl";
    protected static final String SHADER_INVERT_FRAGMENT = "shaders/invertFragment.glsl";
    protected static final String SHADER_RAINBOW_FRAGMENT = "shaders/rainbowFragment.glsl";
    protected static final String SHADER_SEPIA_FRAGMENT = "shaders/sepiaFragment.glsl";
    protected static final String SHADER_VERTEX = "shaders/coloringVertex.glsl";

    //copied from TipHelper
    private static final float CARD_TIP_PAD = 12.0F * Settings.scale;
    private static final float SHADOW_DIST_Y = 14.0F * Settings.scale;
    private static final float SHADOW_DIST_X = 9.0F * Settings.scale;
    private static final float BOX_EDGE_H = 32.0F * Settings.scale;
    private static final float BOX_BODY_H = 64.0F * Settings.scale;
    private static final float BOX_W = 320.0F * Settings.scale;
    private static final float TEXT_OFFSET_X = 22.0F * Settings.scale;
    private static final float HEADER_OFFSET_Y = 12.0F * Settings.scale;
    private static final float ORB_OFFSET_Y = -8.0F * Settings.scale;
    private static final float BODY_OFFSET_Y = -20.0F * Settings.scale;
    private static final float BODY_TEXT_WIDTH = 280.0F * Settings.scale;
    private static final float TIP_DESC_LINE_SPACING = 26.0F * Settings.scale;
    private static final float POWER_ICON_OFFSET_X = 40.0F * Settings.scale;
    protected static ShaderProgram blurShader;
    protected static ShaderProgram brighterShader;
    protected static ShaderProgram colorizeShader;
    protected static ShaderProgram glitchShader;
    protected static ShaderProgram grayscaleShader;
    protected static ShaderProgram invertShader;
    protected static ShaderProgram rainbowShader;
    protected static ShaderProgram sepiaShader;
    private static FrameBuffer maskBuffer;

    public static float calculateAdditionalOffset(ArrayList<EUITooltip> tips, float hb_cY) {
        return tips.isEmpty() ? 0f : (1f - hb_cY / (float) Settings.HEIGHT) * getTallestOffset(tips) - (getTooltipHeight(tips.get(0)) + BOX_EDGE_H * 3.15f) * 0.5f;
    }

    private static float getTallestOffset(ArrayList<EUITooltip> tips) {
        float currentOffset = 0f;
        float maxOffset = 0f;

        for (EUITooltip p : tips) {
            float offsetChange = getTooltipHeight(p) + BOX_EDGE_H * 3.15F;
            if ((currentOffset + offsetChange) >= (float) Settings.HEIGHT * 0.7F) {
                currentOffset = 0f;
            }

            currentOffset += offsetChange;
            if (currentOffset > maxOffset) {
                maxOffset = currentOffset;
            }
        }

        return maxOffset;
    }

    public static float getTooltipHeight(EUITooltip tip) {
        return -EUISmartText.getSmartHeight(EUIFontHelper.cardTooltipFont, tip.description(), BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - 7.0F * Settings.scale;
    }

    public static float calculateToAvoidOffscreen(ArrayList<EUITooltip> tips, float hb_cY) {
        return tips.isEmpty() ? 0f : Math.max(0.0F, getTallestOffset(tips) - hb_cY);
    }

    public static Color copyColor(Color color, float a) {
        return new Color(color.r, color.g, color.b, a);
    }

    public static Color copyColor(AbstractCard card, Color color) {
        return new Color(color.r, color.g, color.b, card.transparency);
    }

    public static void draw(SpriteBatch sb, Texture img, float drawX, float drawY, float size) {
        draw(sb, img, Color.WHITE, drawX, drawY, size, size);
    }

    public static void draw(SpriteBatch sb, Texture img, Color color, float x, float y, float width, float height) {
        final int srcWidth = img.getWidth();
        final int srcHeight = img.getHeight();

        sb.setColor(color);
        sb.draw(img, x, y, 0, 0, width, height, Settings.scale, Settings.scale, 0, 0, 0,
                srcWidth, srcHeight, false, false);
    }

    public static void draw(SpriteBatch sb, Texture img, float x, float y, float width, float height) {
        draw(sb, img, Color.WHITE, x, y, width, height);
    }

    public static void drawBlendedWithShader(SpriteBatch sb, BlendingMode mode, ShaderMode shaderMode, ActionT1<SpriteBatch> drawFunc) {
        drawWithShader(sb, shaderMode, s -> drawBlended(s, mode, drawFunc));
    }

    public static void drawWithShader(SpriteBatch sb, ShaderMode shader, ActionT1<SpriteBatch> drawFunc) {
        if (shader != null) {
            shader.draw(sb, drawFunc);
        }
        else {
            drawFunc.invoke(sb);
        }
    }

    public static void drawBlended(SpriteBatch sb, BlendingMode mode, ActionT1<SpriteBatch> drawFunc) {
        sb.setBlendFunction(mode.srcFunc, mode.dstFunc);
        drawFunc.invoke(sb);
        sb.setBlendFunction(BlendingMode.Normal.srcFunc, BlendingMode.Normal.dstFunc);
    }

    public static void drawBlendedWithShader(PolygonSpriteBatch sb, BlendingMode mode, ShaderMode shaderMode, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawWithShader(sb, shaderMode, s -> drawBlended(s, mode, drawFunc));
    }

    public static void drawWithShader(PolygonSpriteBatch sb, ShaderMode shader, ActionT1<PolygonSpriteBatch> drawFunc) {
        if (shader != null) {
            shader.draw(sb, drawFunc);
        }
        else {
            drawFunc.invoke(sb);
        }
    }

    public static void drawBlended(PolygonSpriteBatch sb, BlendingMode mode, ActionT1<PolygonSpriteBatch> drawFunc) {
        sb.setBlendFunction(mode.srcFunc, mode.dstFunc);
        drawFunc.invoke(sb);
        sb.setBlendFunction(BlendingMode.Normal.srcFunc, BlendingMode.Normal.dstFunc);
    }

    public static void drawBlur(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawBlur(sb, 2, 1, 1, 1, drawFunc);
    }

    public static void drawBlur(SpriteBatch sb, float radius, float resolution, float xDir, float yDir, ActionT1<SpriteBatch> drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        ShaderProgram bs = getBlurShader();
        sb.setShader(bs);
        bs.setUniformf("u_radius", radius);
        bs.setUniformf("u_resolution", resolution);
        bs.setUniform2fv("u_dir", new float[]{xDir, yDir}, 0, 2);
        drawFunc.invoke(sb);
        sb.setShader(defaultShader);
    }

    // Not public because blur needs parameters to use properly
    protected static ShaderProgram getBlurShader() {
        if (blurShader == null) {
            blurShader = initializeShader(SHADER_VERTEX, SHADER_BLUR_FRAGMENT);
        }
        return blurShader;
    }

    public static ShaderProgram initializeShader(String vShaderPath, String fShaderPath) {
        FileHandle fShader = Gdx.files.internal(fShaderPath);
        FileHandle vShader = Gdx.files.internal(vShaderPath);
        String fShaderString = fShader.readString();
        String vShaderString = vShader.readString();
        return new ShaderProgram(vShaderString, fShaderString);
    }

    public static void drawBrighter(SpriteBatch sb, Color color, ActionT1<SpriteBatch> drawFunc) {
        drawColoredWithShader(sb, getBrightShader(), ColorTools.fromColor(color), drawFunc);
    }

    public static void drawColoredWithShader(SpriteBatch sb, ShaderProgram shader, float colorfulColor, ActionT1<SpriteBatch> drawFunc) {
        drawWithShader(sb, shader, (s) -> drawColored(s, colorfulColor, drawFunc));
    }

    public static ShaderProgram getBrightShader() {
        if (brighterShader == null) {
            brighterShader = Shaders.makeRGBAShader();
        }
        return brighterShader;
    }

    public static void drawWithShader(SpriteBatch sb, ShaderProgram shader, ActionT1<SpriteBatch> drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        sb.setShader(shader);
        drawFunc.invoke(sb);
        sb.setShader(defaultShader);
    }

    public static void drawColored(SpriteBatch sb, float color, ActionT1<SpriteBatch> drawFunc) {
        sb.setColor(color);
        drawFunc.invoke(sb);
        sb.setColor(Color.WHITE);
    }

    public static void drawBrighter(SpriteBatch sb, float color, ActionT1<SpriteBatch> drawFunc) {
        drawColoredWithShader(sb, getBrightShader(), color, drawFunc);
    }

    public static void drawBrighter(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawWithShader(sb, getBrightShader(), drawFunc);
    }

    public static void drawBrighter(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawWithShader(sb, getBrightShader(), drawFunc);
    }

    public static void drawWithShader(PolygonSpriteBatch pb, ShaderProgram shader, ActionT1<PolygonSpriteBatch> drawFunc) {
        ShaderProgram defaultShader = pb.getShader();
        pb.setShader(shader);
        drawFunc.invoke(pb);
        pb.setShader(defaultShader);
    }

    public static void drawCentered(SpriteBatch sb, Color color, Texture img, float drawX, float drawY, float width, float height, float imgScale, float imgRotation) {
        drawCentered(sb, color, img, drawX, drawY, width, height, imgScale, imgRotation, false, false);
    }

    public static void drawCentered(SpriteBatch sb, Color color, Texture img, float drawX, float drawY, float width, float height, float imgScale, float imgRotation, boolean flipX, boolean flipY) {
        final float scale = Settings.scale * imgScale;

        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height,
                scale, scale, imgRotation, 0, 0, img.getWidth(), img.getHeight(), flipX, flipY);
        sb.setColor(Color.WHITE);
    }

    public static void drawCentered(SpriteBatch sb, Color color, TextureRegion img, float drawX, float drawY, float width, float height, float imgScale, float imgRotation) {
        final float scale = Settings.scale * imgScale;

        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height, scale, scale, imgRotation);
        sb.setColor(Color.WHITE);
    }

    public static void drawCentered(SpriteBatch sb, Color color, TextureRegion img, float drawX, float drawY, float width, float height, float imgScale, float imgRotation, boolean flipX, boolean flipY) {
        final float scale = Settings.scale * imgScale;

        img.flip(flipX, flipY);
        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height, scale, scale, imgRotation);
        img.flip(flipX, flipY);
        sb.setColor(Color.WHITE);
    }

    public static void drawColored(SpriteBatch sb, Color color, ActionT1<SpriteBatch> drawFunc) {
        sb.setColor(color);
        drawFunc.invoke(sb);
        sb.setColor(Color.WHITE);
    }

    public static void drawColorized(SpriteBatch sb, Color color, ActionT1<SpriteBatch> drawFunc) {
        drawColoredWithShader(sb, getColorizeShader(), ColorTools.fromColor(color), drawFunc);
    }

    public static ShaderProgram getColorizeShader() {
        if (colorizeShader == null) {
            colorizeShader = new ShaderProgram(Shaders.vertexShader, Shaders.fragmentShaderColorize);
        }
        return colorizeShader;
    }

    public static void drawColorized(SpriteBatch sb, float color, ActionT1<SpriteBatch> drawFunc) {
        drawColoredWithShader(sb, getColorizeShader(), color, drawFunc);
    }

    public static void drawColorized(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawWithShader(sb, getColorizeShader(), drawFunc);
    }

    public static void drawColorized(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawWithShader(sb, getColorizeShader(), drawFunc);
    }

    public static void drawGlitched(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawGlitched(sb, EUI.time(), drawFunc);
    }

    public static void drawGlitched(SpriteBatch sb, float xOffset, ActionT1<SpriteBatch> drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        ShaderProgram rs = getGlitchShader();
        sb.setShader(rs);
        setGlitchShader(rs, xOffset);
        drawFunc.invoke(sb);
        sb.setShader(defaultShader);
    }

    protected static ShaderProgram getGlitchShader() {
        if (glitchShader == null) {
            glitchShader = initializeShader(SHADER_VERTEX, SHADER_GLITCH_FRAGMENT);
        }
        return glitchShader;
    }

    protected static ShaderProgram setGlitchShader(ShaderProgram rs, float xOffset) {
        rs.setUniformf("u_time", xOffset);
        return rs;
    }

    public static void drawGlitched(PolygonSpriteBatch pb, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawGlitched(pb, EUI.time(), drawFunc);
    }

    public static void drawGlitched(PolygonSpriteBatch pb, float xOffset, ActionT1<PolygonSpriteBatch> drawFunc) {
        ShaderProgram defaultShader = pb.getShader();
        ShaderProgram rs = getGlitchShader();
        pb.setShader(rs);
        setGlitchShader(rs, xOffset);
        drawFunc.invoke(pb);
        pb.setShader(defaultShader);
    }

    public static void drawGlowing(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawBlended(sb, BlendingMode.Glowing, drawFunc);
    }

    public static void drawGrayscale(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawWithShader(sb, getGrayscaleShader(), drawFunc);
    }

    public static ShaderProgram getGrayscaleShader() {
        if (grayscaleShader == null) {
            grayscaleShader = initializeShader(SHADER_VERTEX, SHADER_GRAYSCALE_FRAGMENT);
        }
        return grayscaleShader;
    }

    public static void drawGrayscale(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawWithShader(sb, getGrayscaleShader(), drawFunc);
    }

    public static void drawInverted(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawWithShader(sb, getInvertShader(), drawFunc);
    }

    public static ShaderProgram getInvertShader() {
        if (invertShader == null) {
            invertShader = initializeShader(SHADER_VERTEX, SHADER_INVERT_FRAGMENT);
        }
        return invertShader;
    }

    public static void drawInverted(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawWithShader(sb, getInvertShader(), drawFunc);
    }

    public static void drawOnCard(SpriteBatch sb, AbstractCard card, Texture img, float drawX, float drawY, float size) {
        drawOnCard(sb, card, EUIColors.white(card.transparency), img, drawX, drawY, size, size);
    }

    public static void drawOnCard(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY, float width, float height) {
        final int srcWidth = img.getWidth();
        final int srcHeight = img.getHeight();

        sb.setColor(color);
        sb.draw(img, drawX, drawY, 0, 0, width, height,
                card.drawScale * Settings.scale, card.drawScale * Settings.scale,
                card.angle, 0, 0, srcWidth, srcHeight, false, false);
    }
    //

    public static void drawOnCard(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY) {
        drawOnCard(sb, card, color, img, drawX, drawY, img.getWidth(), img.getHeight());
    }

    public static void drawOnCard(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY, float size) {
        drawOnCard(sb, card, color, img, drawX, drawY, size, size);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, Color color, float drawX, float drawY, float width, float height) {
        drawOnCardAuto(sb, card, img, new Vector2(drawX, drawY), width, height, color, color.a, 1, 0);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, Vector2 offset, float width, float height, Color color, float alpha, float imgScale, float imgRotation) {
        if (card.angle != 0) {
            offset.rotate(card.angle);
        }

        offset.scl(Settings.scale * card.drawScale);

        drawOnCardCentered(sb, card, new Color(color.r, color.g, color.b, alpha), img, card.current_x + offset.x, card.current_y + offset.y, width, height, imgScale, imgRotation);
    }

    public static void drawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY, float width, float height, float imgScale, float imgRotation) {
        drawOnCardCentered(sb, card, color, img, drawX, drawY, width, height, imgScale, imgRotation, false, false);
    }

    public static void drawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY, float width, float height, float imgScale, float imgRotation, boolean flipX, boolean flipY) {
        if (img == null) {
            EUIUtils.logWarning(card, "Image was null:");
            return;
        }
        final float scale = card.drawScale * Settings.scale * imgScale;

        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height,
                scale, scale, card.angle + imgRotation, 0, 0, img.getWidth(), img.getHeight(), flipX, flipY);
        sb.setColor(Color.WHITE);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, float drawX, float drawY, float width, float height) {
        drawOnCardAuto(sb, card, img, new Vector2(drawX, drawY), width, height, Color.WHITE, card.transparency, 1, 0);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, Vector2 offset, float width, float height) {
        drawOnCardAuto(sb, card, img, offset, width, height, Color.WHITE, card.transparency, 1, 0);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, ColoredTexture img, float drawX, float drawY, float width, float height) {
        drawOnCardAuto(sb, card, img.texture, new Vector2(drawX, drawY), width, height, img.color, img.color.a * card.transparency, 1, 0);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, ColoredTexture img, float drawX, float drawY, float width, float height, float scale) {
        drawOnCardAuto(sb, card, img.texture, new Vector2(drawX, drawY), width, height, img.color, img.color.a * card.transparency, scale, 0);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, ColoredTexture img, Vector2 offset, float width, float height) {
        drawOnCardAuto(sb, card, img.texture, offset, width, height, img.color, img.color.a * card.transparency, 1, 0);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, Vector2 offset, float width, float height, Color color, float alpha, float imgScale) {
        drawOnCardAuto(sb, card, img, offset, width, height, color, alpha, imgScale, 0f);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, TextureRegion img, float drawX, float drawY, float width, float height) {
        drawOnCardAuto(sb, card, img, new Vector2(drawX, drawY), width, height, Color.WHITE, card.transparency, 1);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, TextureRegion img, Vector2 offset, float width, float height, Color color, float alpha, float imgScale) {
        if (card.angle != 0) {
            offset.rotate(card.angle);
        }

        offset.scl(Settings.scale * card.drawScale);

        drawOnCardCentered(sb, card, new Color(color.r, color.g, color.b, alpha), img, card.current_x + offset.x, card.current_y + offset.y, width, height, imgScale);
    }

    public static void drawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, TextureRegion img, float drawX, float drawY, float width, float height, float imgScale) {
        final float scale = card.drawScale * Settings.scale * imgScale;

        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height, scale, scale, card.angle);
        sb.setColor(Color.WHITE);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, TextureRegion img, Vector2 offset, float width, float height) {
        drawOnCardAuto(sb, card, img, offset, width, height, Color.WHITE, card.transparency, 1);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, Vector2 offset, float width, float height, Color color, float alpha, float imgScale, float imgRotation, boolean flipX, boolean flipY) {
        if (card.angle != 0) {
            offset.rotate(card.angle);
        }

        offset.scl(Settings.scale * card.drawScale);

        drawOnCardCentered(sb, card, new Color(color.r, color.g, color.b, alpha), img, card.current_x + offset.x, card.current_y + offset.y, width, height, imgScale, imgRotation, flipX, flipY);
    }

    public static void drawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, TextureAtlas.AtlasRegion img, float drawX, float drawY) {
        sb.setColor(color);
        sb.draw(img, drawX + img.offsetX - img.originalWidth / 2f, drawY + img.offsetY - img.originalHeight / 2f,
                img.originalWidth / 2f - img.offsetX, img.originalHeight / 2f - img.offsetY,
                img.packedWidth, img.packedHeight, card.drawScale * Settings.scale, card.drawScale * Settings.scale, card.angle);
        sb.setColor(Color.WHITE);
    }

    public static void drawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY) {
        final int width = img.getWidth();
        final int height = img.getHeight();

        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height,
                card.drawScale * Settings.scale, card.drawScale * Settings.scale,
                card.angle, 0, 0, width, height, false, false);
        sb.setColor(Color.WHITE);
    }

    public static void drawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY, float width, float height, float imgScale) {
        drawOnCardCentered(sb, card, color, img, drawX, drawY, width, height, imgScale, 0);
    }

    public static void drawOverlay(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawBlended(sb, BlendingMode.Overlay, drawFunc);
    }

    public static void drawRainbow(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawRainbow(sb, EUI.time(), 1, 1, 0.5f, drawFunc);
    }

    public static void drawRainbow(SpriteBatch sb, float xOffset, float saturation, float brightness, float opacity, ActionT1<SpriteBatch> drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        ShaderProgram rs = getRainbowShader();
        sb.setShader(rs);
        setRainbowShader(rs, xOffset, saturation, brightness, opacity);
        drawFunc.invoke(sb);
        sb.setShader(defaultShader);
    }

    protected static ShaderProgram getRainbowShader() {
        if (rainbowShader == null) {
            rainbowShader = initializeShader(SHADER_VERTEX, SHADER_RAINBOW_FRAGMENT);
        }
        return rainbowShader;
    }

    protected static ShaderProgram setRainbowShader(ShaderProgram rs, float xOffset, float saturation, float brightness, float opacity) {
        rs.setUniformf("u_time", xOffset);
        rs.setUniformf("u_saturation", saturation);
        rs.setUniformf("u_brightness", brightness);
        rs.setUniformf("u_opacity", opacity);
        return rs;
    }

    public static void drawRainbow(PolygonSpriteBatch pb, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawRainbow(pb, EUI.time(), 1, 1, 0.5f, drawFunc);
    }

    public static void drawRainbow(PolygonSpriteBatch pb, float xOffset, float saturation, float brightness, float opacity, ActionT1<PolygonSpriteBatch> drawFunc) {
        ShaderProgram defaultShader = pb.getShader();
        ShaderProgram rs = getRainbowShader();
        pb.setShader(rs);
        setRainbowShader(rs, xOffset, saturation, brightness, opacity);
        drawFunc.invoke(pb);
        pb.setShader(defaultShader);
    }

    public static void drawScreen(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawBlended(sb, BlendingMode.Screen, drawFunc);
    }

    public static void drawSepia(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawWithShader(sb, getSepiaShader(), drawFunc);
    }

    public static ShaderProgram getSepiaShader() {
        if (sepiaShader == null) {
            sepiaShader = initializeShader(SHADER_VERTEX, SHADER_SEPIA_FRAGMENT);
        }
        return sepiaShader;
    }

    public static void drawSepia(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawWithShader(sb, getSepiaShader(), drawFunc);
    }

    public static void drawWithMask(SpriteBatch sb, ActionT1<SpriteBatch> maskFunc, ActionT1<SpriteBatch> drawFunc) {
        sb.end();

        maskBuffer.begin();
        Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glColorMask(false, false, false, true);
        sb.begin();
        drawBlended(sb, BlendingMode.Mask, maskFunc);
        Gdx.gl20.glColorMask(true, true, true, true);
        drawBlended(sb, BlendingMode.MaskBlend, drawFunc);
        sb.end();
        maskBuffer.end();

        sb.begin();
        TextureRegion t = new TextureRegion(maskBuffer.getColorBufferTexture());
        t.flip(false, true);
        sb.draw(t, 0, 0, 0, 0, maskBuffer.getWidth(), maskBuffer.getHeight(), 1f, 1f, 0f);
    }

    private static BitmapFont generateFont(BitmapFont source, float size, float borderWidth, float shadowOffset) {
        return generateFont(source, size, borderWidth, new Color(0f, 0f, 0f, 1f), shadowOffset, new Color(0f, 0f, 0f, 0.5f));
    }

    private static BitmapFont generateFont(BitmapFont source, float size, float borderWidth, Color borderColor, float shadowOffset, Color shadowColor) {
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
        FreeTypeFontGenerator g = new FreeTypeFontGenerator(source.getData().fontFile); // TitleFontSize.fontFile
        g.scaleForPixelHeight(param.size);
        BitmapFont font = g.generateFont(param);
        font.setUseIntegerPositions(false);
        font.getData().markupEnabled = false;
        if (LocalizedStrings.break_chars != null) {
            font.getData().breakChars = LocalizedStrings.break_chars.toCharArray();
        }

        return font;
    }

    public static TextureAtlas.AtlasRegion generateIcon(Texture texture) {
        final int h = texture.getHeight();
        final int w = texture.getWidth();
        final int section = h / 2;
        return new TextureAtlas.AtlasRegion(texture, (w - section) / 2, 0, section, section);
    }

    public static float getAngleDegrees(float aX, float aY, float bX, float bY) {
        return MathUtils.radiansToDegrees * (float) Math.atan2(bY - aY, bX - aX);
    }

    public static float getAngleRadians(float aX, float aY, float bX, float bY) {
        return (float) Math.atan2(bY - aY, bX - aX);
    }

    public static TextureRegion getCroppedRegion(Texture texture, int div) {
        final int w = texture.getWidth();
        final int h = texture.getHeight();
        final int half_div = div / 2;
        return new TextureRegion(texture, w / div, h / div, w - (w / half_div), h - (h / half_div));
    }

    public static BitmapFont getDescriptionFont(AbstractCard card, float scaleModifier) {
        BitmapFont result;
        if (card instanceof TooltipProvider && ((TooltipProvider) card).isPopup()) {
            result = EUIFontHelper.cardDescriptionFontLarge;
            result.getData().setScale(card.drawScale * scaleModifier * 0.5f);
        }
        else {
            result = EUIFontHelper.cardDescriptionFontNormal;
            result.getData().setScale(card.drawScale * scaleModifier);
        }

        return result;
    }

    public static Pixmap getPixmapFromBufferedImage(BufferedImage image) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            byte[] bytes = stream.toByteArray();
            return new Pixmap(bytes, 0, bytes.length);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BitmapFont getTitleFont(AbstractCard card) {
        BitmapFont result;
        final float scale = 1 / (Math.max(14f, card.name.length()) / 14f);
        if (card instanceof TooltipProvider && ((TooltipProvider) card).isPopup()) {
            result = EUIFontHelper.cardTitleFontLarge;
            result.getData().setScale(card.drawScale * 0.5f * scale);
        }
        else {
            result = EUIFontHelper.cardTitleFontNormal;
            result.getData().setScale(card.drawScale * scale);
        }

        return result;
    }

    public static void initializeBuffers() {
        maskBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false);
    }

    public static boolean isCharAt(String s, int i, char c) {
        return i < s.length() && c == s.charAt(i);
    }

    public static float lerpScale(float initial, float target) {
        if (initial > target) {
            initial = MathUtils.lerp(initial, 1.0F, Gdx.graphics.getDeltaTime() * 10.0F);
            if (initial - target < 0.05F) {
                initial = target;
            }
        }
        return initial;
    }

    public static void resetFont(BitmapFont font) {
        font.getData().setScale(1);
    }

    public static void writeCentered(SpriteBatch sb, BitmapFont font, String text, float cX, float cY, Color color) {
        FontHelper.renderFontCentered(sb, font, text, cX, cY, color);
    }

    public static void writeCentered(SpriteBatch sb, BitmapFont font, String text, Hitbox hb, Color color) {
        FontHelper.renderFontCentered(sb, font, text, hb.cX, hb.cY, color);
    }

    public static void writeCentered(SpriteBatch sb, BitmapFont font, String text, Hitbox hb, Color color, float scale) {
        FontHelper.renderFontCentered(sb, font, text, hb.cX, hb.cY, color, scale);
    }

    public static void writeOnCard(SpriteBatch sb, AbstractCard card, BitmapFont font, String text, float x, float y, Color color) {
        writeOnCard(sb, card, font, text, x, y, color, false);
    }

    public static void writeOnCard(SpriteBatch sb, AbstractCard card, BitmapFont font, String text, float x, float y, Color color, boolean roundY) {
        final float scale = card.drawScale * Settings.scale;

        color = EUIColors.copy(color, color.a * card.transparency);
        FontHelper.renderRotatedText(sb, font, text, card.current_x, card.current_y, x * scale, y * scale, card.angle, roundY, color);
    }

    public enum BlendingMode {
        Normal(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA),
        NormalPreMultiplied(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA),
        Glowing(GL20.GL_SRC_ALPHA, GL20.GL_ONE),
        GlowingPreMultiplied(GL20.GL_ONE, GL20.GL_ONE),
        Overlay(GL20.GL_DST_COLOR, GL20.GL_ONE),
        Multiply(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA),
        Screen(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_COLOR),
        Mask(GL20.GL_ONE, GL20.GL_ZERO),
        MaskBlend(GL20.GL_DST_ALPHA, GL20.GL_ZERO);

        public final int srcFunc;
        public final int dstFunc;

        BlendingMode(int srcFunc, int dstFunc) {
            this.srcFunc = srcFunc;
            this.dstFunc = dstFunc;
        }

        public void apply(SpriteBatch sb) {
            sb.setBlendFunction(srcFunc, dstFunc);
        }

        public void apply(PolygonSpriteBatch sb) {
            sb.setBlendFunction(srcFunc, dstFunc);
        }

        public void draw(SpriteBatch sb, ActionT1<SpriteBatch> drawImpl) {
            EUIRenderHelpers.drawBlended(sb, this, drawImpl);
        }
    }

    public enum ShaderMode {
        Normal,
        Grayscale,
        Invert,
        Sepia,
        Bright,
        Colorize,
        Glitch,
        Rainbow;

        public void draw(SpriteBatch sb, ActionT1<SpriteBatch> drawImpl) {
            switch (this) {
                case Glitch:
                    EUIRenderHelpers.drawGlitched(sb, drawImpl);
                case Rainbow:
                    EUIRenderHelpers.drawRainbow(sb, drawImpl);
                case Grayscale:
                case Invert:
                case Sepia:
                case Bright:
                case Colorize:
                    EUIRenderHelpers.drawWithShader(sb, getShaderProgram(), drawImpl);
                    return;
            }
            drawImpl.invoke(sb);
        }

        public ShaderProgram getShaderProgram() {
            switch (this) {
                case Grayscale:
                    return getGrayscaleShader();
                case Invert:
                    return getInvertShader();
                case Sepia:
                    return getSepiaShader();
                case Bright:
                    return getBrightShader();
                case Colorize:
                    return getColorizeShader();
            }
            return null;
        }

        public void draw(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawImpl) {
            switch (this) {
                case Glitch:
                    EUIRenderHelpers.drawGlitched(sb, drawImpl);
                case Rainbow:
                    EUIRenderHelpers.drawRainbow(sb, drawImpl);
                case Grayscale:
                case Invert:
                case Sepia:
                case Bright:
                case Colorize:
                    EUIRenderHelpers.drawWithShader(sb, getShaderProgram(), drawImpl);
                    return;
            }
            drawImpl.invoke(sb);
        }
    }
}