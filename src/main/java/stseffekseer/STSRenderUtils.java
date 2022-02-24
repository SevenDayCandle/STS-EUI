package stseffekseer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.github.tommyettinger.colorful.Shaders;
import com.github.tommyettinger.colorful.rgb.ColorTools;
import stseffekseer.interfaces.DrawFunction;

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

    protected static final String SHADER_GRAYSCALE_FRAGMENT = "shaders/grayscaleFragment.glsl";
    protected static final String SHADER_SEPIA_FRAGMENT = "shaders/sepiaFragment.glsl";
    protected static final String SHADER_VERTEX = "shaders/coloringVertex.glsl";

    protected static ShaderProgram BrighterShader;
    protected static ShaderProgram ColorizeShader;
    protected static ShaderProgram GrayscaleShader;
    protected static ShaderProgram SepiaShader;

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
}
