package stseffekseer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.github.tommyettinger.colorful.Shaders;
import com.github.tommyettinger.colorful.rgb.ColorTools;
import org.imgscalr.Scalr;
import stseffekseer.interfaces.DrawFunction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.*;

public class STSRenderUtils
{
    public enum BlendingMode {
        Normal(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA),
        Glowing(GL20.GL_SRC_ALPHA, GL20.GL_ONE),
        Overlay(GL20.GL_DST_COLOR, GL20.GL_ONE),
        Screen(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);

        public final int srcFunc;
        public final int dstFunc;

        BlendingMode(int srcFunc, int dstFunc) {
            this.srcFunc = srcFunc;
            this.dstFunc = dstFunc;
        }
    }

    protected static final String SHADER_BLUR_FRAGMENT = "shaders/blurFragment.glsl";
    protected static final String SHADER_GRAYSCALE_FRAGMENT = "shaders/grayscaleFragment.glsl";
    protected static final String SHADER_RAINBOW_FRAGMENT = "shaders/rainbowFragment.glsl";
    protected static final String SHADER_SEPIA_FRAGMENT = "shaders/sepiaFragment.glsl";
    protected static final String SHADER_VERTEX = "shaders/coloringVertex.glsl";

    protected static ShaderProgram BlurShader;
    protected static ShaderProgram BrighterShader;
    protected static ShaderProgram ColorizeShader;
    protected static ShaderProgram GrayscaleShader;
    protected static ShaderProgram RainbowShader;
    protected static ShaderProgram SepiaShader;

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

    public static ShaderProgram GetGrayscaleShader() {
        if (GrayscaleShader == null) {
            GrayscaleShader = InitializeShader(SHADER_VERTEX, SHADER_GRAYSCALE_FRAGMENT);
        }
        return GrayscaleShader;
    }

    protected static ShaderProgram GetRainbowShader() {
        if (RainbowShader == null) {
            RainbowShader = InitializeShader(SHADER_VERTEX, SHADER_RAINBOW_FRAGMENT);
        }
        return RainbowShader;
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

    public static void DrawBlur(SpriteBatch sb, DrawFunction drawFunc) {
        DrawBlur(sb, 2, 1, 1, 1, drawFunc);
    }

    public static void DrawBlur(SpriteBatch sb, float radius, float resolution, float xDir, float yDir, DrawFunction drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        ShaderProgram bs = GetBlurShader();
        bs.setUniformf("u_radius", radius);
        bs.setUniformf("u_resolution", resolution);
        bs.setUniform2fv("u_dir", new float[] {xDir, yDir}, 0, 2);
        sb.setShader(bs);
        drawFunc.Invoke(sb);
        sb.setShader(defaultShader);
    }

    public static void DrawRainbow(SpriteBatch sb, DrawFunction drawFunc) {
        DrawRainbow(sb, 0, 1, 1, 0.5f, drawFunc);
    }

    public static void DrawRainbow(SpriteBatch sb, float xOffset, float saturation, float brightness, float opacity, DrawFunction drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        ShaderProgram rs = GetRainbowShader();
        rs.setUniformf("u_offset", xOffset);
        rs.setUniformf("u_saturation", saturation);
        rs.setUniformf("u_brightness", brightness);
        rs.setUniformf("u_opacity", opacity);
        sb.setShader(rs);
        drawFunc.Invoke(sb);
        sb.setShader(defaultShader);
    }

    public static void DrawGrayscale(SpriteBatch sb, DrawFunction drawFunc) {
        DrawWithShader(sb, GetGrayscaleShader(), drawFunc);
    }

    public static void DrawSepia(SpriteBatch sb, DrawFunction drawFunc) {
        DrawWithShader(sb, GetSepiaShader(), drawFunc);
    }

    public static void DrawWithShader(SpriteBatch sb, ShaderProgram shader, DrawFunction drawFunc) {
        ShaderProgram defaultShader = sb.getShader();
        sb.setShader(shader);
        drawFunc.Invoke(sb);
        sb.setShader(defaultShader);
    }

    public static void DrawGlowing(SpriteBatch sb, DrawFunction drawFunc) {
        DrawBlended(sb, BlendingMode.Glowing, drawFunc);
    }

    public static void DrawOverlay(SpriteBatch sb, DrawFunction drawFunc) {
        DrawBlended(sb, BlendingMode.Overlay, drawFunc);
    }

    public static void DrawScreen(SpriteBatch sb, DrawFunction drawFunc) {
        DrawBlended(sb, BlendingMode.Screen, drawFunc);
    }

    public static void DrawBlended(SpriteBatch sb, BlendingMode mode, DrawFunction drawFunc) {
        sb.setBlendFunction(mode.srcFunc,mode.dstFunc);
        drawFunc.Invoke(sb);
        sb.setBlendFunction(770,771);
    }

    public static void DrawColored(SpriteBatch sb, Color color, DrawFunction drawFunc) {
        sb.setColor(color);
        drawFunc.Invoke(sb);
        sb.setColor(Color.WHITE);
    }

    public static void DrawColored(SpriteBatch sb, float color, DrawFunction drawFunc) {
        sb.setColor(color);
        drawFunc.Invoke(sb);
        sb.setColor(Color.WHITE);
    }

    public static void DrawBrighter(SpriteBatch sb, Color color, DrawFunction drawFunc) {
        DrawColoredWithShader(sb, GetBrightShader(), ColorTools.fromColor(color), drawFunc);
    }

    public static void DrawBrighter(SpriteBatch sb, float color, DrawFunction drawFunc) {
        DrawColoredWithShader(sb, GetBrightShader(), color, drawFunc);
    }

    public static void DrawColorized(SpriteBatch sb, Color color, DrawFunction drawFunc) {
        DrawColoredWithShader(sb, GetColorizeShader(), ColorTools.fromColor(color), drawFunc);
    }

    public static void DrawColorized(SpriteBatch sb, float color, DrawFunction drawFunc) {
        DrawColoredWithShader(sb, GetColorizeShader(), color, drawFunc);
    }

    public static void DrawColoredWithShader(SpriteBatch sb, ShaderProgram shader, float colorfulColor, DrawFunction drawFunc) {
        DrawWithShader(sb, shader, (s) -> DrawColored(s, colorfulColor, drawFunc));
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

    public static BufferedImage ScalrScale(Texture image, float xScale, float yScale) {
        return ScalrScale(image, xScale, yScale, Scalr.Method.AUTOMATIC);
    }

    public static BufferedImage ScalrScale(Texture image, float xScale, float yScale, Scalr.Method scalingMethod, BufferedImageOp... ops) {
        if (!image.getTextureData().isPrepared()) {
            image.getTextureData().prepare();
        }
        return ScalrScale(image.getTextureData().consumePixmap(), xScale, yScale);
    }

    public static BufferedImage ScalrScale(Pixmap image, float xScale, float yScale) {
        return ScalrScale(image, xScale, yScale, Scalr.Method.AUTOMATIC);
    }

    public static BufferedImage ScalrScale(Pixmap image, float xScale, float yScale, Scalr.Method scalingMethod, BufferedImageOp... ops) {
        try {
            PixmapIO.PNG writer = new PixmapIO.PNG((int)((float)(image.getWidth() * image.getHeight()) * 1.5F));
            ByteArrayOutputStream  stream = new ByteArrayOutputStream();
            writer.setFlipY(false);
            writer.write(stream, image);
            writer.dispose();
            return ScalrScale(ImageIO.read(new ByteArrayInputStream(stream.toByteArray())), xScale, yScale, scalingMethod, ops);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage ScalrScale(BufferedImage image, float xScale, float yScale) {
        return ScalrScale(image, xScale, yScale, Scalr.Method.AUTOMATIC);
    }

    public static BufferedImage ScalrScale(BufferedImage image, float xScale, float yScale, Scalr.Method scalingMethod, BufferedImageOp... ops) {
        if (image == null) {
            return null;
        }
        return Scalr.resize(image, scalingMethod, (int) (image.getWidth() * xScale), (int) (image.getHeight() * yScale), ops);
    }

    public static Pixmap ScalrScaleAsPixmap(Texture image, float xScale, float yScale) {
        BufferedImage bi = ScalrScale(image, xScale, yScale);
        return bi != null ? GetPixmapFromBufferedImage(bi) : null;
    }

    public static Pixmap ScalrScaleAsPixmap(Pixmap image, float xScale, float yScale) {
        BufferedImage bi = ScalrScale(image, xScale, yScale);
        return bi != null ? GetPixmapFromBufferedImage(bi) : null;
    }

    public static Pixmap ScalrScaleAsPixmap(BufferedImage image, float xScale, float yScale) {
        return GetPixmapFromBufferedImage(image);
    }
}
