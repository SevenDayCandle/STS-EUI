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
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.TooltipProvider;
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
    protected static final String SHADER_BLUR_FRAGMENT = "shaders/blurFragment.glsl";
    protected static final String SHADER_BRIGHTER_FRAGMENT = "shaders/brighterFragment.glsl";
    protected static final String SHADER_COLORIZE_FRAGMENT = "shaders/colorizeFragment.glsl";
    protected static final String SHADER_COLORIZE_CRT_FRAGMENT = "shaders/colorizeCrtFragment.glsl";
    protected static final String SHADER_CRT_FRAGMENT = "shaders/crtFragment.glsl";
    protected static final String SHADER_GLITCH_FRAGMENT = "shaders/glitchFragment.glsl";
    protected static final String SHADER_GRAYSCALE_FRAGMENT = "shaders/grayscaleFragment.glsl";
    protected static final String SHADER_INVERT_FRAGMENT = "shaders/invertFragment.glsl";
    protected static final String SHADER_RAINBOW_FRAGMENT = "shaders/rainbowFragment.glsl";
    protected static final String SHADER_RAINBOW_VERTICAL_FRAGMENT = "shaders/rainbowVerticalFragment.glsl";
    protected static final String SHADER_SEPIA_FRAGMENT = "shaders/sepiaFragment.glsl";
    protected static final String SHADER_VERTEX = "shaders/coloringVertex.glsl";
    public static final Color DARKENED_SCREEN = new Color(0.0F, 0.0F, 0.0F, 0.4F);
    private static FrameBuffer maskBuffer;
    protected static ShaderProgram blurShader;
    protected static ShaderProgram brighterShader;
    protected static ShaderProgram colorizeShader;
    protected static ShaderProgram colorizeCrtShader;
    protected static ShaderProgram crtShader;
    protected static ShaderProgram glitchShader;
    protected static ShaderProgram grayscaleShader;
    protected static ShaderProgram invertShader;
    protected static ShaderProgram rainbowShader;
    protected static ShaderProgram rainbowVerticalShader;
    protected static ShaderProgram sepiaShader;

    public static float calculateToAvoidOffscreen(ArrayList<EUITooltip> tips, float hb_cY) {
        return tips.isEmpty() ? 0f : Math.max(0.0F, EUITooltip.getTallestOffset(tips) - hb_cY);
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

    public static void drawBlended(SpriteBatch sb, BlendingMode mode, ActionT1<SpriteBatch> drawFunc) {
        sb.setBlendFunction(mode.srcFunc, mode.dstFunc);
        drawFunc.invoke(sb);
        sb.setBlendFunction(BlendingMode.Normal.srcFunc, BlendingMode.Normal.dstFunc);
    }

    public static void drawBlended(PolygonSpriteBatch sb, BlendingMode mode, ActionT1<PolygonSpriteBatch> drawFunc) {
        sb.setBlendFunction(mode.srcFunc, mode.dstFunc);
        drawFunc.invoke(sb);
        sb.setBlendFunction(BlendingMode.Normal.srcFunc, BlendingMode.Normal.dstFunc);
    }

    public static void drawBlendedWithShader(SpriteBatch sb, BlendingMode mode, ShaderMode shaderMode, ActionT1<SpriteBatch> drawFunc) {
        drawWithShader(sb, shaderMode, s -> drawBlended(s, mode, drawFunc));
    }

    public static void drawBlendedWithShader(PolygonSpriteBatch sb, BlendingMode mode, ShaderMode shaderMode, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawWithShader(sb, shaderMode, s -> drawBlended(s, mode, drawFunc));
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

    public static void drawBrighter(SpriteBatch sb, Color color, ActionT1<SpriteBatch> drawFunc) {
        drawColoredWithShader(sb, getBrightShader(), color, drawFunc);
    }

    public static void drawBrighter(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawWithShader(sb, getBrightShader(), drawFunc);
    }

    public static void drawBrighter(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawWithShader(sb, getBrightShader(), drawFunc);
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

    public static void drawColoredWithShader(SpriteBatch sb, ShaderProgram shader, Color color, ActionT1<SpriteBatch> drawFunc) {
        drawWithShader(sb, shader, (s) -> drawColored(s, color, drawFunc));
    }

    public static void drawColorized(SpriteBatch sb, Color color, ActionT1<SpriteBatch> drawFunc) {
        drawColoredWithShader(sb, getColorizeShader(), color, drawFunc);
    }

    public static void drawColorized(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawWithShader(sb, getColorizeShader(), drawFunc);
    }

    public static void drawColorized(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawWithShader(sb, getColorizeShader(), drawFunc);
    }

    public static void drawColorizedCRT(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawColorizedCRT(sb, EUI.time(), 1.4f, 0.1f, 0.3f, drawFunc);
    }

    public static void drawColorizedCRT(SpriteBatch sb, float xOffset, float xFuzz, float rgbOffset, float vertJerk, ActionT1<SpriteBatch> drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        ShaderProgram rs = getColorizeCRTShader();
        sb.setShader(rs);
        setCRTShader(rs, xOffset, xFuzz, rgbOffset, vertJerk);
        drawFunc.invoke(sb);
        sb.setShader(defaultShader);
    }

    public static void drawColorizedCRT(PolygonSpriteBatch pb, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawColorizedCRT(pb, EUI.time(), 1.4f, 0.1f, 0.3f, drawFunc);
    }

    public static void drawColorizedCRT(PolygonSpriteBatch pb, float xOffset, float xFuzz, float rgbOffset, float vertJerk, ActionT1<PolygonSpriteBatch> drawFunc) {
        ShaderProgram defaultShader = pb.getShader();
        ShaderProgram rs = getColorizeCRTShader();
        pb.setShader(rs);
        setCRTShader(rs, xOffset, xFuzz, rgbOffset, vertJerk);
        drawFunc.invoke(pb);
        pb.setShader(defaultShader);
    }

    public static void drawCRT(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawCRT(sb, EUI.time(), 1.4f, 0.1f, 0.3f, drawFunc);
    }

    public static void drawCRT(SpriteBatch sb, float xOffset, float xFuzz, float rgbOffset, float vertJerk, ActionT1<SpriteBatch> drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        ShaderProgram rs = getCRTShader();
        sb.setShader(rs);
        setCRTShader(rs, xOffset, xFuzz, rgbOffset, vertJerk);
        drawFunc.invoke(sb);
        sb.setShader(defaultShader);
    }

    public static void drawCRT(PolygonSpriteBatch pb, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawCRT(pb, EUI.time(), 1.4f, 0.1f, 0.3f, drawFunc);
    }

    public static void drawCRT(PolygonSpriteBatch pb, float xOffset, float xFuzz, float rgbOffset, float vertJerk, ActionT1<PolygonSpriteBatch> drawFunc) {
        ShaderProgram defaultShader = pb.getShader();
        ShaderProgram rs = getCRTShader();
        pb.setShader(rs);
        setCRTShader(rs, xOffset, xFuzz, rgbOffset, vertJerk);
        drawFunc.invoke(pb);
        pb.setShader(defaultShader);
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

    public static void drawGrayscale(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawWithShader(sb, getGrayscaleShader(), drawFunc);
    }

    public static void drawInverted(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawWithShader(sb, getInvertShader(), drawFunc);
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

    public static void drawOnCard(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY) {
        drawOnCard(sb, card, color, img, drawX, drawY, img.getWidth(), img.getHeight());
    }

    public static void drawOnCard(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY, float size) {
        drawOnCard(sb, card, color, img, drawX, drawY, size, size);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, Color color, float drawX, float drawY, float width, float height) {
        drawOnCardAuto(sb, card, img, drawX, drawY, width, height, color, color.a, 1, 0);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, float drawX, float drawY, float width, float height, Color color, float alpha, float imgScale, float imgRotation) {
        if (card.angle != 0) {
            float radians = card.angle * MathUtils.degRad;
            float cos = (float) Math.cos(radians);
            float sin = (float) Math.sin(radians);
            float newX = drawX * cos - drawY * sin;
            float newY = drawX * sin + drawY * cos;
            drawX = newX;
            drawY = newY;
        }

        float scl = Settings.scale * card.drawScale;
        drawX = drawX * scl;
        drawY = drawY * scl;

        drawOnCardCentered(sb, card, new Color(color.r, color.g, color.b, alpha), img, card.current_x + drawX, card.current_y + drawY, width, height, imgScale, imgRotation);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, float drawX, float drawY, float width, float height) {
        drawOnCardAuto(sb, card, img, drawX, drawY, width, height, Color.WHITE, card.transparency, 1, 0);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, ColoredTexture img, float drawX, float drawY, float width, float height) {
        drawOnCardAuto(sb, card, img.texture, drawX, drawY, width, height, img.color, img.color.a * card.transparency, 1, 0);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, ColoredTexture img, float drawX, float drawY, float width, float height, float scale) {
        drawOnCardAuto(sb, card, img.texture, drawX, drawY, width, height, img.color, img.color.a * card.transparency, scale, 0);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, float drawX, float drawY, float width, float height, Color color, float alpha, float imgScale) {
        drawOnCardAuto(sb, card, img, drawX, drawY, width, height, color, alpha, imgScale, 0f);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, TextureRegion img, float drawX, float drawY, float width, float height) {
        drawOnCardAuto(sb, card, img, drawX, drawY, width, height, Color.WHITE, card.transparency, 1);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, TextureRegion img, float drawX, float drawY, float width, float height, Color color, float alpha, float imgScale) {
        if (card.angle != 0) {
            float radians = card.angle * MathUtils.degRad;
            float cos = (float) Math.cos(radians);
            float sin = (float) Math.sin(radians);
            float newX = drawX * cos - drawY * sin;
            float newY = drawX * sin + drawY * cos;
            drawX = newX;
            drawY = newY;
        }

        float scl = Settings.scale * card.drawScale;
        drawX = drawX * scl;
        drawY = drawY * scl;

        drawOnCardCentered(sb, card, new Color(color.r, color.g, color.b, alpha), img, card.current_x + drawX, card.current_y + drawY, width, height, imgScale);
    }

    public static void drawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, float drawX, float drawY, float width, float height, Color color, float alpha, float imgScale, float imgRotation, boolean flipX, boolean flipY) {
        if (card.angle != 0) {
            float radians = card.angle * MathUtils.degRad;
            float cos = (float) Math.cos(radians);
            float sin = (float) Math.sin(radians);
            float newX = drawX * cos - drawY * sin;
            float newY = drawX * sin + drawY * cos;
            drawX = newX;
            drawY = newY;
        }

        float scl = Settings.scale * card.drawScale;
        drawX = drawX * scl;
        drawY = drawY * scl;

        drawOnCardCentered(sb, card, new Color(color.r, color.g, color.b, alpha), img, card.current_x + drawX, card.current_y + drawY, width, height, imgScale, imgRotation, flipX, flipY);
    }
    //

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

    public static void drawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, TextureRegion img, float drawX, float drawY, float width, float height, float imgScale) {
        final float scale = card.drawScale * Settings.scale * imgScale;

        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height, scale, scale, card.angle);
        sb.setColor(Color.WHITE);
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

    public static void drawRainbowVertical(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawRainbowVertical(sb, EUI.time(), 1, 1, 0.5f, drawFunc);
    }

    public static void drawRainbowVertical(SpriteBatch sb, float xOffset, float saturation, float brightness, float opacity, ActionT1<SpriteBatch> drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        ShaderProgram rs = getRainbowVerticalShader();
        sb.setShader(rs);
        setRainbowVerticalShader(rs, xOffset, saturation, brightness, opacity);
        drawFunc.invoke(sb);
        sb.setShader(defaultShader);
    }

    public static void drawRainbowVertical(PolygonSpriteBatch pb, ActionT1<PolygonSpriteBatch> drawFunc) {
        drawRainbowVertical(pb, EUI.time(), 1, 1, 0.5f, drawFunc);
    }

    public static void drawRainbowVertical(PolygonSpriteBatch pb, float xOffset, float saturation, float brightness, float opacity, ActionT1<PolygonSpriteBatch> drawFunc) {
        ShaderProgram defaultShader = pb.getShader();
        ShaderProgram rs = getRainbowVerticalShader();
        pb.setShader(rs);
        setRainbowVerticalShader(rs, xOffset, saturation, brightness, opacity);
        drawFunc.invoke(pb);
        pb.setShader(defaultShader);
    }

    public static void drawScreen(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawBlended(sb, BlendingMode.Screen, drawFunc);
    }

    public static void drawSepia(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        drawWithShader(sb, getSepiaShader(), drawFunc);
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

    public static void drawWithShader(SpriteBatch sb, ShaderMode shader, ActionT1<SpriteBatch> drawFunc) {
        if (shader != null) {
            shader.draw(sb, drawFunc);
        }
        else {
            drawFunc.invoke(sb);
        }
    }

    public static void drawWithShader(PolygonSpriteBatch sb, ShaderMode shader, ActionT1<PolygonSpriteBatch> drawFunc) {
        if (shader != null) {
            shader.draw(sb, drawFunc);
        }
        else {
            drawFunc.invoke(sb);
        }
    }

    public static void drawWithShader(SpriteBatch sb, ShaderProgram shader, ActionT1<SpriteBatch> drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        sb.setShader(shader);
        drawFunc.invoke(sb);
        sb.setShader(defaultShader);
    }

    public static void drawWithShader(PolygonSpriteBatch pb, ShaderProgram shader, ActionT1<PolygonSpriteBatch> drawFunc) {
        ShaderProgram defaultShader = pb.getShader();
        pb.setShader(shader);
        drawFunc.invoke(pb);
        pb.setShader(defaultShader);
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
        return new TextureAtlas.AtlasRegion(texture, (w - h) / 2, 0, h, h);
    }

    public static float getAngleDegrees(float aX, float aY, float bX, float bY) {
        return MathUtils.radiansToDegrees * (float) Math.atan2(bY - aY, bX - aX);
    }

    public static float getAngleRadians(float aX, float aY, float bX, float bY) {
        return (float) Math.atan2(bY - aY, bX - aX);
    }

    // Not public because blur needs parameters to use properly
    protected static ShaderProgram getBlurShader() {
        if (blurShader == null) {
            blurShader = initializeShader(SHADER_VERTEX, SHADER_BLUR_FRAGMENT);
        }
        return blurShader;
    }

    public static ShaderProgram getBrightShader() {
        if (brighterShader == null) {
            brighterShader = initializeShader(SHADER_VERTEX, SHADER_BRIGHTER_FRAGMENT);
        }
        return brighterShader;
    }

    public static ShaderProgram getColorizeShader() {
        if (colorizeShader == null) {
            colorizeShader = initializeShader(SHADER_VERTEX, SHADER_COLORIZE_FRAGMENT);
        }
        return colorizeShader;
    }

    protected static ShaderProgram getColorizeCRTShader() {
        if (crtShader == null) {
            crtShader = initializeShader(SHADER_VERTEX, SHADER_COLORIZE_CRT_FRAGMENT);
        }
        return crtShader;
    }

    protected static ShaderProgram getCRTShader() {
        if (crtShader == null) {
            crtShader = initializeShader(SHADER_VERTEX, SHADER_CRT_FRAGMENT);
        }
        return crtShader;
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

    protected static ShaderProgram getGlitchShader() {
        if (glitchShader == null) {
            glitchShader = initializeShader(SHADER_VERTEX, SHADER_GLITCH_FRAGMENT);
        }
        return glitchShader;
    }

    public static ShaderProgram getGrayscaleShader() {
        if (grayscaleShader == null) {
            grayscaleShader = initializeShader(SHADER_VERTEX, SHADER_GRAYSCALE_FRAGMENT);
        }
        return grayscaleShader;
    }

    public static ShaderProgram getInvertShader() {
        if (invertShader == null) {
            invertShader = initializeShader(SHADER_VERTEX, SHADER_INVERT_FRAGMENT);
        }
        return invertShader;
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

    protected static ShaderProgram getRainbowShader() {
        if (rainbowShader == null) {
            rainbowShader = initializeShader(SHADER_VERTEX, SHADER_RAINBOW_FRAGMENT);
        }
        return rainbowShader;
    }

    protected static ShaderProgram getRainbowVerticalShader() {
        if (rainbowVerticalShader == null) {
            rainbowVerticalShader = initializeShader(SHADER_VERTEX, SHADER_RAINBOW_VERTICAL_FRAGMENT);
        }
        return rainbowVerticalShader;
    }

    public static ShaderProgram getSepiaShader() {
        if (sepiaShader == null) {
            sepiaShader = initializeShader(SHADER_VERTEX, SHADER_SEPIA_FRAGMENT);
        }
        return sepiaShader;
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

    public static ShaderProgram initializeShader(String vShaderPath, String fShaderPath) {
        FileHandle fShader = Gdx.files.internal(fShaderPath);
        FileHandle vShader = Gdx.files.internal(vShaderPath);
        String fShaderString = fShader.readString();
        String vShaderString = vShader.readString();
        return new ShaderProgram(vShaderString, fShaderString);
    }

    public static boolean isCharAt(String s, int i, char c) {
        return i < s.length() && c == s.charAt(i);
    }

    // SuperFastMode patches MathHelper's methods so we have to use our own version
    public static float lerp(float fromValue, float toValue, float progress) {
        return fromValue + (toValue - fromValue) * progress;
    }

    public static float lerpScale(float startX, float targetX) {
        return lerpSnap(startX, targetX, 10f, 0.05f);
    }

    public static float lerpSnap(float startX, float targetX, float rate) {
        return lerpSnap(startX, targetX, rate, Settings.CARD_SNAP_THRESHOLD);
    }

    public static float lerpSnap(float startX, float targetX, float rate, float threshold) {
        if (startX != targetX) {
            startX = lerp(startX, targetX, Gdx.graphics.getDeltaTime() * rate);
            if (Math.abs(startX - targetX) < threshold) {
                startX = targetX;
            }
        }

        return startX;
    }

    public static void resetFont(BitmapFont font) {
        font.getData().setScale(1);
    }

    protected static ShaderProgram setCRTShader(ShaderProgram rs, float xOffset, float xFuzz, float rgbOffset, float jerk) {
        rs.setUniformf("u_time", xOffset);
        rs.setUniformf("u_horzFuzz", xFuzz);
        rs.setUniformf("u_rgbOffset", rgbOffset);
        rs.setUniformf("u_vertJerk", jerk);
        return rs;
    }

    protected static ShaderProgram setGlitchShader(ShaderProgram rs, float xOffset) {
        rs.setUniformf("u_time", xOffset);
        return rs;
    }

    protected static ShaderProgram setRainbowShader(ShaderProgram rs, float xOffset, float saturation, float brightness, float opacity) {
        rs.setUniformf("u_time", xOffset);
        rs.setUniformf("u_saturation", saturation);
        rs.setUniformf("u_brightness", brightness);
        rs.setUniformf("u_opacity", opacity);
        return rs;
    }

    protected static ShaderProgram setRainbowVerticalShader(ShaderProgram rs, float yOffset, float saturation, float brightness, float opacity) {
        rs.setUniformf("u_time", yOffset);
        rs.setUniformf("u_saturation", saturation);
        rs.setUniformf("u_brightness", brightness);
        rs.setUniformf("u_opacity", opacity);
        return rs;
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
        writeOnCard(sb, card, font, text, x, y, card.drawScale * Settings.scale, color, false);
    }

    public static void writeOnCard(SpriteBatch sb, AbstractCard card, BitmapFont font, String text, float x, float y, float scale, Color color, boolean roundY) {
        writeOnCard(sb, card, font, text, x, y, scale, card.angle, color, false);
    }

    public static void writeOnCard(SpriteBatch sb, AbstractCard card, BitmapFont font, String text, float x, float y, float scale, float angle, Color color, boolean roundY) {
        color = EUIColors.copy(color, color.a * card.transparency);
        FontHelper.renderRotatedText(sb, font, text, card.current_x, card.current_y, x * scale, y * scale, angle, roundY, color);
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

        public void draw(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawImpl) {
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
        Rainbow,
        RainbowVertical,
        CRT,
        ColorizeCRT;

        public void draw(SpriteBatch sb, ActionT1<SpriteBatch> drawImpl) {
            switch (this) {
                case Glitch:
                    EUIRenderHelpers.drawGlitched(sb, drawImpl);
                    return;
                case Rainbow:
                    EUIRenderHelpers.drawRainbow(sb, drawImpl);
                    return;
                case RainbowVertical:
                    EUIRenderHelpers.drawRainbowVertical(sb, drawImpl);
                    return;
                case CRT:
                    EUIRenderHelpers.drawCRT(sb, drawImpl);
                    return;
                case ColorizeCRT:
                    EUIRenderHelpers.drawColorizedCRT(sb, drawImpl);
                    return;
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

        public void draw(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawImpl) {
            switch (this) {
                case Glitch:
                    EUIRenderHelpers.drawGlitched(sb, drawImpl);
                    return;
                case Rainbow:
                    EUIRenderHelpers.drawRainbow(sb, drawImpl);
                    return;
                case RainbowVertical:
                    EUIRenderHelpers.drawRainbowVertical(sb, drawImpl);
                    return;
                case CRT:
                    EUIRenderHelpers.drawCRT(sb, drawImpl);
                    return;
                case ColorizeCRT:
                    EUIRenderHelpers.drawColorizedCRT(sb, drawImpl);
                    return;
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
    }
}