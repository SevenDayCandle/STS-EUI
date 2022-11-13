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
import com.megacrit.cardcrawl.cards.blue.Rainbow;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import eatyourbeets.interfaces.delegates.ActionT1;
import eatyourbeets.interfaces.delegates.ActionT2;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.text.EUISmartText;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.AdvancedTexture;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod and https://github.com/SevenDayCandle/STS-FoolMod

public class EUIRenderHelpers
{
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
    protected static ShaderProgram BlurShader;
    protected static ShaderProgram BrighterShader;
    protected static ShaderProgram ColorizeShader;
    protected static ShaderProgram GlitchShader;
    protected static ShaderProgram GrayscaleShader;
    protected static ShaderProgram InvertShader;
    protected static ShaderProgram RainbowShader;
    protected static ShaderProgram SepiaShader;
    private static FrameBuffer MaskBuffer;

    public static void InitializeBuffers() {
        MaskBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false);
    }

    public static void DrawBlended(SpriteBatch sb, BlendingMode mode, ActionT1<SpriteBatch> drawFunc) {
        sb.setBlendFunction(mode.srcFunc,mode.dstFunc);
        drawFunc.Invoke(sb);
        sb.setBlendFunction(BlendingMode.Normal.srcFunc,BlendingMode.Normal.dstFunc);
    }

    public static void DrawBlended(PolygonSpriteBatch sb, BlendingMode mode, ActionT1<PolygonSpriteBatch> drawFunc) {
        sb.setBlendFunction(mode.srcFunc,mode.dstFunc);
        drawFunc.Invoke(sb);
        sb.setBlendFunction(BlendingMode.Normal.srcFunc,BlendingMode.Normal.dstFunc);
    }

    public static void DrawBlendedWithShader(SpriteBatch sb, BlendingMode mode, ShaderMode shaderMode, ActionT1<SpriteBatch> drawFunc) {
        DrawWithShader(sb, shaderMode, s -> DrawBlended(s, mode, drawFunc));
    }

    public static void DrawBlendedWithShader(PolygonSpriteBatch sb, BlendingMode mode, ShaderMode shaderMode, ActionT1<PolygonSpriteBatch> drawFunc) {
        DrawWithShader(sb, shaderMode, s -> DrawBlended(s, mode, drawFunc));
    }

    public static void DrawBlur(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        DrawBlur(sb, 2, 1, 1, 1, drawFunc);
    }

    public static void DrawBlur(SpriteBatch sb, float radius, float resolution, float xDir, float yDir, ActionT1<SpriteBatch> drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        ShaderProgram bs = GetBlurShader();
        sb.setShader(bs);
        bs.setUniformf("u_radius", radius);
        bs.setUniformf("u_resolution", resolution);
        bs.setUniform2fv("u_dir", new float[] {xDir, yDir}, 0, 2);
        drawFunc.Invoke(sb);
        sb.setShader(defaultShader);
    }

    public static void DrawBrighter(SpriteBatch sb, Color color, ActionT1<SpriteBatch> drawFunc) {
        DrawColoredWithShader(sb, GetBrightShader(), ColorTools.fromColor(color), drawFunc);
    }

    public static void DrawBrighter(SpriteBatch sb, float color, ActionT1<SpriteBatch> drawFunc) {
        DrawColoredWithShader(sb, GetBrightShader(), color, drawFunc);
    }

    public static void DrawBrighter(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        DrawWithShader(sb, GetBrightShader(), drawFunc);
    }

    public static void DrawBrighter(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawFunc) {
        DrawWithShader(sb, GetBrightShader(), drawFunc);
    }

    public static void DrawColored(SpriteBatch sb, Color color, ActionT1<SpriteBatch> drawFunc) {
        sb.setColor(color);
        drawFunc.Invoke(sb);
        sb.setColor(Color.WHITE);
    }

    public static void DrawColored(SpriteBatch sb, float color, ActionT1<SpriteBatch> drawFunc) {
        sb.setColor(color);
        drawFunc.Invoke(sb);
        sb.setColor(Color.WHITE);
    }

    public static void DrawColoredWithShader(SpriteBatch sb, ShaderProgram shader, float colorfulColor, ActionT1<SpriteBatch> drawFunc) {
        DrawWithShader(sb, shader, (s) -> DrawColored(s, colorfulColor, drawFunc));
    }

    public static void DrawColorized(SpriteBatch sb, Color color, ActionT1<SpriteBatch> drawFunc) {
        DrawColoredWithShader(sb, GetColorizeShader(), ColorTools.fromColor(color), drawFunc);
    }

    public static void DrawColorized(SpriteBatch sb, float color, ActionT1<SpriteBatch> drawFunc) {
        DrawColoredWithShader(sb, GetColorizeShader(), color, drawFunc);
    }

    public static void DrawColorized(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        DrawWithShader(sb, GetColorizeShader(), drawFunc);
    }

    public static void DrawColorized(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawFunc) {
        DrawWithShader(sb, GetColorizeShader(), drawFunc);
    }

    public static void DrawGlitched(SpriteBatch sb, ActionT1<SpriteBatch>  drawFunc) {
        DrawGlitched(sb, EUI.Time(), drawFunc);
    }

    public static void DrawGlitched(SpriteBatch sb, float xOffset, ActionT1<SpriteBatch> drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        ShaderProgram rs = GetGlitchShader();
        sb.setShader(rs);
        SetGlitchShader(rs, xOffset);
        drawFunc.Invoke(sb);
        sb.setShader(defaultShader);
    }

    public static void DrawGlitched(PolygonSpriteBatch pb, ActionT1<PolygonSpriteBatch>  drawFunc) {
        DrawGlitched(pb, EUI.Time(), drawFunc);
    }

    public static void DrawGlitched(PolygonSpriteBatch pb, float xOffset, ActionT1<PolygonSpriteBatch> drawFunc) {
        ShaderProgram defaultShader = pb.getShader();
        ShaderProgram rs = GetGlitchShader();
        pb.setShader(rs);
        SetGlitchShader(rs, xOffset);
        drawFunc.Invoke(pb);
        pb.setShader(defaultShader);
    }

    public static void DrawGlowing(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        DrawBlended(sb, BlendingMode.Glowing, drawFunc);
    }

    public static void DrawGrayscale(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        DrawWithShader(sb, GetGrayscaleShader(), drawFunc);
    }

    public static void DrawGrayscale(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawFunc) {
        DrawWithShader(sb, GetGrayscaleShader(), drawFunc);
    }

    public static void DrawInverted(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        DrawWithShader(sb, GetInvertShader(), drawFunc);
    }

    public static void DrawInverted(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawFunc) {
        DrawWithShader(sb, GetInvertShader(), drawFunc);
    }

    public static void DrawOverlay(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        DrawBlended(sb, BlendingMode.Overlay, drawFunc);
    }

    public static void DrawRainbow(SpriteBatch sb, ActionT1<SpriteBatch>  drawFunc) {
        DrawRainbow(sb, EUI.Time(), 1, 1, 0.5f, drawFunc);
    }

    public static void DrawRainbow(SpriteBatch sb, float xOffset, float saturation, float brightness, float opacity, ActionT1<SpriteBatch> drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        ShaderProgram rs = GetRainbowShader();
        sb.setShader(rs);
        SetRainbowShader(rs, xOffset, saturation, brightness, opacity);
        drawFunc.Invoke(sb);
        sb.setShader(defaultShader);
    }

    public static void DrawRainbow(PolygonSpriteBatch pb, ActionT1<PolygonSpriteBatch>  drawFunc) {
        DrawRainbow(pb, EUI.Time(), 1, 1, 0.5f, drawFunc);
    }

    public static void DrawRainbow(PolygonSpriteBatch pb, float xOffset, float saturation, float brightness, float opacity, ActionT1<PolygonSpriteBatch> drawFunc) {
        ShaderProgram defaultShader = pb.getShader();
        ShaderProgram rs = GetRainbowShader();
        pb.setShader(rs);
        SetRainbowShader(rs, xOffset, saturation, brightness, opacity);
        drawFunc.Invoke(pb);
        pb.setShader(defaultShader);
    }

    public static void DrawScreen(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        DrawBlended(sb, BlendingMode.Screen, drawFunc);
    }

    public static void DrawSepia(SpriteBatch sb, ActionT1<SpriteBatch> drawFunc) {
        DrawWithShader(sb, GetSepiaShader(), drawFunc);
    }

    public static void DrawSepia(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawFunc) {
        DrawWithShader(sb, GetSepiaShader(), drawFunc);
    }

    public static void DrawWithShader(SpriteBatch sb, ShaderMode shader, ActionT1<SpriteBatch> drawFunc) {
        if (shader != null)
        {
            shader.Draw(sb, drawFunc);
        }
        else
        {
            drawFunc.Invoke(sb);
        }
    }

    public static void DrawWithShader(PolygonSpriteBatch sb, ShaderMode shader, ActionT1<PolygonSpriteBatch> drawFunc) {
        if (shader != null)
        {
            shader.Draw(sb, drawFunc);
        }
        else
        {
            drawFunc.Invoke(sb);
        }
    }

    public static void DrawWithShader(SpriteBatch sb, ShaderProgram shader, ActionT1<SpriteBatch> drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        sb.setShader(shader);
        drawFunc.Invoke(sb);
        sb.setShader(defaultShader);
    }

    public static void DrawWithShader(PolygonSpriteBatch pb, ShaderProgram shader, ActionT1<PolygonSpriteBatch> drawFunc) {
        ShaderProgram defaultShader = pb.getShader();
        pb.setShader(shader);
        drawFunc.Invoke(pb);
        pb.setShader(defaultShader);
    }

    // Not public because blur needs parameters to use properly
    protected static ShaderProgram GetBlurShader() {
        if (BlurShader == null) {
            BlurShader = InitializeShader(SHADER_VERTEX, SHADER_BLUR_FRAGMENT);
        }
        return BlurShader;
    }

    public static ShaderProgram GetBrightShader() {
        if (BrighterShader == null) {
            BrighterShader = Shaders.makeRGBAShader();
        }
        return BrighterShader;
    }

    public static ShaderProgram GetColorizeShader() {
        if (ColorizeShader == null) {
            ColorizeShader = new ShaderProgram(Shaders.vertexShader, Shaders.fragmentShaderColorize);
        }
        return ColorizeShader;
    }

    protected static ShaderProgram GetGlitchShader() {
        if (GlitchShader == null) {
            GlitchShader = InitializeShader(SHADER_VERTEX, SHADER_GLITCH_FRAGMENT);
        }
        return GlitchShader;
    }

    protected static ShaderProgram SetGlitchShader(ShaderProgram rs, float xOffset) {
        rs.setUniformf("u_time", xOffset);
        return rs;
    }

    public static ShaderProgram GetGrayscaleShader() {
        if (GrayscaleShader == null) {
            GrayscaleShader = InitializeShader(SHADER_VERTEX, SHADER_GRAYSCALE_FRAGMENT);
        }
        return GrayscaleShader;
    }

    public static ShaderProgram GetInvertShader() {
        if (InvertShader == null) {
            InvertShader = InitializeShader(SHADER_VERTEX, SHADER_INVERT_FRAGMENT);
        }
        return InvertShader;
    }

    public static Pixmap GetPixmapFromBufferedImage(BufferedImage image) {
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

    protected static ShaderProgram GetRainbowShader() {
        if (RainbowShader == null) {
            RainbowShader = InitializeShader(SHADER_VERTEX, SHADER_RAINBOW_FRAGMENT);
        }
        return RainbowShader;
    }

    protected static ShaderProgram SetRainbowShader(ShaderProgram rs, float xOffset, float saturation, float brightness, float opacity) {
        rs.setUniformf("u_time", xOffset);
        rs.setUniformf("u_saturation", saturation);
        rs.setUniformf("u_brightness", brightness);
        rs.setUniformf("u_opacity", opacity);
        return rs;
    }

    public static ShaderProgram GetSepiaShader() {
        if (SepiaShader == null) {
            SepiaShader = InitializeShader(SHADER_VERTEX, SHADER_SEPIA_FRAGMENT);
        }
        return SepiaShader;
    }

    public static ShaderProgram InitializeShader(String vShaderPath, String fShaderPath) {
        FileHandle fShader = Gdx.files.internal(fShaderPath);
        FileHandle vShader = Gdx.files.internal(vShaderPath);
        String fShaderString = fShader.readString();
        String vShaderString = vShader.readString();
        return new ShaderProgram(vShaderString, fShaderString);
    }
    //

    public static void ResetFont(BitmapFont font)
    {
        font.getData().setScale(1);
    }

    public static BitmapFont GetDescriptionFont(AbstractCard card, float scaleModifier)
    {
        BitmapFont result;
        if (card instanceof TooltipProvider && ((TooltipProvider) card).IsPopup())
        {
            result = EUIFontHelper.CardDescriptionFont_Large;
            result.getData().setScale(card.drawScale * scaleModifier * 0.5f);
        }
        else
        {
            result = EUIFontHelper.CardDescriptionFont_Normal;
            result.getData().setScale(card.drawScale * scaleModifier);
        }

        return result;
    }

    public static BitmapFont GetTitleFont(AbstractCard card)
    {
        BitmapFont result;
        final float scale = 1 / (Math.max(14f, card.name.length()) / 14f);
        if (card instanceof TooltipProvider && ((TooltipProvider) card).IsPopup())
        {
            result = EUIFontHelper.CardTitleFont_Large;
            result.getData().setScale(card.drawScale * 0.5f * scale);
        }
        else
        {
            result = EUIFontHelper.CardTitleFont_Normal;
            result.getData().setScale(card.drawScale * scale);
        }

        return result;
    }

    public static void DrawWithMask(SpriteBatch sb, ActionT1<SpriteBatch> maskFunc, ActionT1<SpriteBatch> drawFunc) {
        sb.end();

        MaskBuffer.begin();
        Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glColorMask(false, false, false, true);
        sb.begin();
        DrawBlended(sb, BlendingMode.Mask, maskFunc);
        Gdx.gl20.glColorMask(true, true, true, true);
        DrawBlended(sb, BlendingMode.MaskBlend, drawFunc);
        sb.end();
        MaskBuffer.end();

        sb.begin();
        TextureRegion t = new TextureRegion(MaskBuffer.getColorBufferTexture());
        t.flip(false, true);
        sb.draw(t, 0, 0, 0, 0, MaskBuffer.getWidth(), MaskBuffer.getHeight(), 1f, 1f, 0f);
    }

    public static void DrawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, TextureAtlas.AtlasRegion img, float drawX, float drawY)
    {
        sb.setColor(color);
        sb.draw(img, drawX + img.offsetX - img.originalWidth / 2f, drawY + img.offsetY - img.originalHeight / 2f,
                img.originalWidth / 2f - img.offsetX, img.originalHeight / 2f - img.offsetY,
                img.packedWidth, img.packedHeight, card.drawScale * Settings.scale, card.drawScale * Settings.scale, card.angle);
    }

    public static void DrawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY)
    {
        final int width = img.getWidth();
        final int height = img.getHeight();

        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height,
                card.drawScale * Settings.scale, card.drawScale * Settings.scale,
                card.angle, 0, 0, width, height, false, false);
    }

    public static void DrawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, TextureRegion img, float drawX, float drawY, float width, float height, float imgScale)
    {
        final float scale = card.drawScale * Settings.scale * imgScale;

        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height, scale, scale, card.angle);
    }

    public static void DrawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY, float width, float height, float imgScale)
    {
        DrawOnCardCentered(sb, card, color, img, drawX, drawY, width, height, imgScale, 0);
    }

    public static void DrawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY, float width, float height, float imgScale, float imgRotation)
    {
        DrawOnCardCentered(sb, card, color, img, drawX, drawY, width, height, imgScale, imgRotation, false, false);
    }

    public static void DrawOnCardCentered(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY, float width, float height, float imgScale, float imgRotation, boolean flipX, boolean flipY)
    {
        if (img == null) {
            EUIUtils.LogWarning(card, "Image was null:");
            return;
        }
        final float scale = card.drawScale * Settings.scale * imgScale;

        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height,
                scale, scale, card.angle + imgRotation, 0, 0, img.getWidth(), img.getHeight(), flipX, flipY);
    }

    public static void DrawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, Color color, float drawX, float drawY, float width, float height)
    {
        DrawOnCardAuto(sb, card, img, new Vector2(drawX, drawY), width, height, color, color.a, 1, 0);
    }

    public static void DrawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, float drawX, float drawY, float width, float height)
    {
        DrawOnCardAuto(sb, card, img, new Vector2(drawX, drawY), width, height, Color.WHITE, card.transparency, 1, 0);
    }

    public static void DrawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, Vector2 offset, float width, float height)
    {
        DrawOnCardAuto(sb, card, img, offset, width, height, Color.WHITE, card.transparency, 1, 0);
    }

    public static void DrawOnCardAuto(SpriteBatch sb, AbstractCard card, AdvancedTexture img, float drawX, float drawY, float width, float height)
    {
        DrawOnCardAuto(sb, card, img.texture, new Vector2(drawX, drawY), width, height, img.color, img.color.a * card.transparency, 1, 0);
    }

    public static void DrawOnCardAuto(SpriteBatch sb, AbstractCard card, AdvancedTexture img, float drawX, float drawY, float width, float height, float scale)
    {
        DrawOnCardAuto(sb, card, img.texture, new Vector2(drawX, drawY), width, height, img.color, img.color.a * card.transparency, scale, 0);
    }

    public static void DrawOnCardAuto(SpriteBatch sb, AbstractCard card, AdvancedTexture img, Vector2 offset, float width, float height)
    {
        DrawOnCardAuto(sb, card, img.texture, offset, width, height, img.color, img.color.a * card.transparency, 1, 0);
    }

    public static void DrawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, Vector2 offset, float width, float height, Color color, float alpha, float imgScale)
    {
        DrawOnCardAuto(sb, card, img, offset, width, height, color, alpha, imgScale, 0f);
    }

    public static void DrawOnCardAuto(SpriteBatch sb, AbstractCard card, TextureRegion img, Vector2 offset, float width, float height, Color color, float alpha, float imgScale)
    {
        if (card.angle != 0)
        {
            offset.rotate(card.angle);
        }

        offset.scl(Settings.scale * card.drawScale);

        DrawOnCardCentered(sb, card, new Color(color.r, color.g, color.b, alpha), img, card.current_x + offset.x, card.current_y + offset.y, width, height, imgScale);
    }

    public static void DrawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, Vector2 offset, float width, float height, Color color, float alpha, float imgScale, float imgRotation)
    {
        if (card.angle != 0)
        {
            offset.rotate(card.angle);
        }

        offset.scl(Settings.scale * card.drawScale);

        DrawOnCardCentered(sb, card, new Color(color.r, color.g, color.b, alpha), img, card.current_x + offset.x, card.current_y + offset.y, width, height, imgScale, imgRotation);
    }

    public static void DrawOnCardAuto(SpriteBatch sb, AbstractCard card, Texture img, Vector2 offset, float width, float height, Color color, float alpha, float imgScale, float imgRotation, boolean flipX, boolean flipY)
    {
        if (card.angle != 0)
        {
            offset.rotate(card.angle);
        }

        offset.scl(Settings.scale * card.drawScale);

        DrawOnCardCentered(sb, card, new Color(color.r, color.g, color.b, alpha), img, card.current_x + offset.x, card.current_y + offset.y, width, height, imgScale, imgRotation, flipX, flipY);
    }

    public static void DrawOnCard(SpriteBatch sb, AbstractCard card, Texture img, float drawX, float drawY, float size)
    {
        DrawOnCard(sb, card, EUIColors.White(card.transparency), img, drawX, drawY, size, size);
    }

    public static void DrawOnCard(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY)
    {
        DrawOnCard(sb, card, color, img, drawX, drawY, img.getWidth(), img.getHeight());
    }

    public static void DrawOnCard(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY, float size)
    {
        DrawOnCard(sb, card, color, img, drawX, drawY, size, size);
    }

    public static void DrawOnCard(SpriteBatch sb, AbstractCard card, Color color, Texture img, float drawX, float drawY, float width, float height)
    {
        final int srcWidth = img.getWidth();
        final int srcHeight = img.getHeight();

        sb.setColor(color);
        sb.draw(img, drawX, drawY, 0, 0, width, height,
                card.drawScale * Settings.scale, card.drawScale * Settings.scale,
                card.angle, 0, 0, srcWidth, srcHeight, false, false);
    }

    public static void DrawCentered(SpriteBatch sb, Color color, Texture img, float drawX, float drawY, float width, float height, float imgScale, float imgRotation)
    {
        DrawCentered(sb, color, img, drawX, drawY, width, height, imgScale, imgRotation, false, false);
    }

    public static void DrawCentered(SpriteBatch sb, Color color, Texture img, float drawX, float drawY, float width, float height, float imgScale, float imgRotation, boolean flipX, boolean flipY)
    {
        final float scale = Settings.scale * imgScale;

        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height,
                scale, scale, imgRotation, 0, 0, img.getWidth(), img.getHeight(), flipX, flipY);
    }

    public static void DrawCentered(SpriteBatch sb, Color color, TextureRegion img, float drawX, float drawY, float width, float height, float imgScale, float imgRotation)
    {
        final float scale = Settings.scale * imgScale;

        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height, scale, scale, imgRotation);
    }

    public static void DrawCentered(SpriteBatch sb, Color color, TextureRegion img, float drawX, float drawY, float width, float height, float imgScale, float imgRotation, boolean flipX, boolean flipY)
    {
        final float scale = Settings.scale * imgScale;

        img.flip(flipX, flipY);
        sb.setColor(color);
        sb.draw(img, drawX - (width / 2f), drawY - (height / 2f), width / 2f, height / 2f, width, height, scale, scale, imgRotation);
        img.flip(flipX, flipY);
    }

    public static void Draw(SpriteBatch sb, Texture img, float drawX, float drawY, float size)
    {
        Draw(sb, img, Color.WHITE, drawX, drawY, size, size);
    }

    public static void Draw(SpriteBatch sb, Texture img, float x, float y, float width, float height)
    {
        Draw(sb, img, Color.WHITE, x, y, width, height);
    }

    public static void Draw(SpriteBatch sb, Texture img, Color color, float x, float y, float width, float height)
    {
        final int srcWidth = img.getWidth();
        final int srcHeight = img.getHeight();

        sb.setColor(color);
        sb.draw(img, x, y, 0, 0, width, height, Settings.scale, Settings.scale, 0, 0, 0,
                srcWidth, srcHeight, false, false);
    }

    public static void WriteOnCard(SpriteBatch sb, AbstractCard card, BitmapFont font, String text, float x, float y, Color color)
    {
        WriteOnCard(sb, card, font, text, x, y, color, false);
    }

    public static void WriteOnCard(SpriteBatch sb, AbstractCard card, BitmapFont font, String text, float x, float y, Color color, boolean roundY)
    {
        final float scale = card.drawScale * Settings.scale;

        color = EUIColors.Copy(color, color.a * card.transparency);
        FontHelper.renderRotatedText(sb, font, text, card.current_x, card.current_y, x * scale, y * scale, card.angle, roundY, color);
    }

    public static void WriteCentered(SpriteBatch sb, BitmapFont font, String text, float cX, float cY, Color color)
    {
        FontHelper.renderFontCentered(sb, font, text, cX, cY, color);
    }

    public static void WriteCentered(SpriteBatch sb, BitmapFont font, String text, Hitbox hb, Color color)
    {
        FontHelper.renderFontCentered(sb, font, text, hb.cX, hb.cY, color);
    }

    public static void WriteCentered(SpriteBatch sb, BitmapFont font, String text, Hitbox hb, Color color, float scale)
    {
        FontHelper.renderFontCentered(sb, font, text, hb.cX, hb.cY, color, scale);
    }

    public static EUIImage ForTexture(Texture texture)
    {
        return ForTexture(texture, Color.WHITE);
    }

    public static EUIImage ForTexture(Texture texture, AdvancedHitbox hb, Color color)
    {
        return new EUIImage(texture, hb, color);
    }

    public static EUIImage ForTexture(Texture texture, Color color)
    {
        return new EUIImage(texture, color);
    }

    public static Color CopyColor(Color color, float a)
    {
        return new Color(color.r, color.g, color.b, a);
    }

    public static Color CopyColor(AbstractCard card, Color color)
    {
        return new Color(color.r, color.g, color.b, card.transparency);
    }

    private static BitmapFont GenerateFont(BitmapFont source, float size, float borderWidth, float shadowOffset)
    {
        return GenerateFont(source, size, borderWidth, new Color(0f, 0f, 0f, 1f), shadowOffset, new Color(0f, 0f, 0f, 0.5f));
    }

    private static BitmapFont GenerateFont(BitmapFont source, float size, float borderWidth, Color borderColor, float shadowOffset, Color shadowColor)
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
        FreeTypeFontGenerator g = new FreeTypeFontGenerator(source.getData().fontFile); // TitleFontSize.fontFile
        g.scaleForPixelHeight(param.size);
        BitmapFont font = g.generateFont(param);
        font.setUseIntegerPositions(false);
        font.getData().markupEnabled = false;
        if (LocalizedStrings.break_chars != null)
        {
            font.getData().breakChars = LocalizedStrings.break_chars.toCharArray();
        }

        return font;
    }

    public static boolean IsCharAt(String s, int i, char c) {
        return i < s.length() && c == s.charAt(i);
    }

    public static float GetTooltipHeight(EUITooltip tip)
    {
        return -EUISmartText.GetSmartHeight(EUIFontHelper.CardTooltipFont, tip.Description(), BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - 7.0F * Settings.scale;
    }

    public static float CalculateAdditionalOffset(ArrayList<EUITooltip> tips, float hb_cY)
    {
        return tips.isEmpty() ? 0f : (1f - hb_cY / (float) Settings.HEIGHT) * GetTallestOffset(tips) - (GetTooltipHeight(tips.get(0)) + BOX_EDGE_H * 3.15f) * 0.5f;
    }

    public static float CalculateToAvoidOffscreen(ArrayList<EUITooltip> tips, float hb_cY)
    {
        return tips.isEmpty() ? 0f : Math.max(0.0F, GetTallestOffset(tips) - hb_cY);
    }

    private static float GetTallestOffset(ArrayList<EUITooltip> tips)
    {
        float currentOffset = 0f;
        float maxOffset = 0f;

        for (EUITooltip p : tips)
        {
            float offsetChange = GetTooltipHeight(p) + BOX_EDGE_H * 3.15F;
            if ((currentOffset + offsetChange) >= (float) Settings.HEIGHT * 0.7F)
            {
                currentOffset = 0f;
            }

            currentOffset += offsetChange;
            if (currentOffset > maxOffset)
            {
                maxOffset = currentOffset;
            }
        }

        return maxOffset;
    }

    public static float LerpScale(float initial, float target) {
        if (initial > target) {
            initial = MathUtils.lerp(initial, 1.0F, Gdx.graphics.getDeltaTime() * 10.0F);
            if (initial - target < 0.05F) {
                initial = target;
            }
        }
        return initial;
    }

    public static TextureRegion GetCroppedRegion(Texture texture, int div)
    {
        final int w = texture.getWidth();
        final int h = texture.getHeight();
        final int half_div = div / 2;
        return new TextureRegion(texture, w / div, h / div, w - (w / half_div), h - (h / half_div));
    }

    public static TextureAtlas.AtlasRegion GenerateIcon(Texture texture) {
        final int h = texture.getHeight();
        final int w = texture.getWidth();
        final int section = h / 2;
        return new TextureAtlas.AtlasRegion(texture, (w - section) / 2, 0, section, section);
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

        public void Draw(SpriteBatch sb, ActionT1<SpriteBatch> drawImpl) {
            switch (this) {
                case Glitch:
                    EUIRenderHelpers.DrawGlitched(sb, drawImpl);
                case Rainbow:
                    EUIRenderHelpers.DrawRainbow(sb, drawImpl);
                case Grayscale:
                case Invert:
                case Sepia:
                case Bright:
                case Colorize:
                    EUIRenderHelpers.DrawWithShader(sb, GetShaderProgram(), drawImpl);
                    return;
            }
            drawImpl.Invoke(sb);
        }

        public void Draw(PolygonSpriteBatch sb, ActionT1<PolygonSpriteBatch> drawImpl) {
            switch (this) {
                case Glitch:
                    EUIRenderHelpers.DrawGlitched(sb, drawImpl);
                case Rainbow:
                    EUIRenderHelpers.DrawRainbow(sb, drawImpl);
                case Grayscale:
                case Invert:
                case Sepia:
                case Bright:
                case Colorize:
                    EUIRenderHelpers.DrawWithShader(sb, GetShaderProgram(), drawImpl);
                    return;
            }
            drawImpl.Invoke(sb);
        }

        public ShaderProgram GetShaderProgram()
        {
            switch (this)
            {
                case Grayscale:
                    return GetGrayscaleShader();
                case Invert:
                    return GetInvertShader();
                case Sepia:
                    return GetSepiaShader();
                case Bright:
                    return GetBrightShader();
                case Colorize:
                    return GetColorizeShader();
            }
            return null;
        }
    }
}