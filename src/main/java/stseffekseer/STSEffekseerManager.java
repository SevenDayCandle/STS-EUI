package stseffekseer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import stseffekseer.swig.EffekseerBackendCore;
import stseffekseer.swig.EffekseerEffectCore;
import stseffekseer.swig.EffekseerManagerCore;

import java.util.HashMap;

import static stseffekseer.STSEffekSeerUtils.LoadEffect;
import static stseffekseer.configuration.EUIConfiguration.BASE_SPRITES_DEFAULT;

public class STSEffekseerManager {
    public static final float BASE_ANIMATION_SPEED = 60f;
    protected static float AnimationSpeed = BASE_ANIMATION_SPEED;
    private static final HashMap<String, EffekseerEffectCore> ParticleEffects = new HashMap<>();
    private static EffekseerManagerCore ManagerCore;
    private static FrameBuffer Buffer;
    private static boolean Enabled = false;

    /**
     Attempts to initialize the Effekseer system for the current OS and set up the buffer used for rendering
     */
    public static void Initialize() {
        try {
            STSEffekSeerUtils.LoadLibraryFromJar();
            EffekseerBackendCore.InitializeAsOpenGL();
            ManagerCore = new EffekseerManagerCore();
            ManagerCore.Initialize(BASE_SPRITES_DEFAULT);
            Buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, false);
            Enabled = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer Play(String key, Vector2 position) {
        return Play(key, position, null, null, (float[]) null);
    }

    public static Integer Play(String key, Vector2 position, Vector3 rotation, Vector3 scale, Color color) {
        return Play(key, position, rotation, scale, STSEffekSeerUtils.ToEffekseerColor(color));
    }

    /**
     Start playing a new effect in Effekseer.
     Whenever a new effect type is loaded, its files are cached so that future calls to the same effect type do not need to load file data again
     */
    public static Integer Play(String key, Vector2 position, Vector3 rotation, Vector3 scale, float[] color) {
        if (Enabled) {
            try {
                EffekseerEffectCore effect = ParticleEffects.get(key);
                if (effect == null) {
                    effect = LoadEffect(key);
                    ParticleEffects.put(key, effect);
                }
                if (effect != null) {
                    int handle = ManagerCore.Play(effect);
                    ManagerCore.SetEffectPosition(handle, position.x, position.y, 0);
                    if (rotation != null) {
                        ManagerCore.SetEffectRotation(handle, rotation.x, rotation.y, rotation.z);
                    }
                    if (scale != null) {
                        ManagerCore.SetEffectScale(handle, scale.x, scale.y, scale.z);
                    }
                    if (color != null) {
                        ManagerCore.SetAllColor(handle, color[0], color[1], color[2], color[3]);
                    }
                    return handle;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static boolean Modify(int handle, Vector2 position, Vector3 rotation, Vector3 scale, Color color)  {
        return Modify(handle, position, rotation, scale, STSEffekSeerUtils.ToEffekseerColor(color));
    }

    /**
     Edit the attributes of an effect that is currently playing
     */
    public static boolean Modify(int handle, Vector2 position, Vector3 rotation, Vector3 scale, float[] color) {
        if (Enabled && ManagerCore.Exists(handle)) {
            if (position != null)         {
                ManagerCore.SetEffectPosition(handle, position.x, position.y, 0);
            }
            if (rotation != null) {
                ManagerCore.SetEffectRotation(handle, rotation.x, rotation.y, rotation.z);
            }
            if (scale != null) {
                ManagerCore.SetEffectScale(handle, scale.x, scale.y, scale.z);
            }
            if (color != null) {
                ManagerCore.SetAllColor(handle, color[0], color[1], color[2], color[3]);
            }
            return true;
        }
        return false;
    }

    /**
     Whether the effect with the given handle is currently playing
     */
    public static boolean Exists(int handle){
        return Enabled && ManagerCore.Exists(handle);
    }

    /**
     Set the global animation speed for ALL animations
     */
    public static void SetAnimationSpeed(float speed) {
        AnimationSpeed = Math.max(0, speed);
    }

    /**
     Advances the animations of all playing animations.
     Effekseer animations will only appear on the screen if the color/depth bits are clear. Any subsequent Spritebatch rendering calls will render the animations invisible.
     Thus, we must render the animations into a framebuffer to be rendered later
     */
    public static void Update() {
        if (Enabled) {
            ManagerCore.SetViewProjectionMatrixWithSimpleWindow(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            ManagerCore.Update(Gdx.graphics.getDeltaTime() * AnimationSpeed);
            Buffer.begin();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
            ManagerCore.DrawBack();
            ManagerCore.DrawFront();
            Buffer.end();
        }
    }

    /**
     Renders the effects captured in the framebuffer written to in Update()
     Because the OpenGL world space is flip
     */
    public static void Render(SpriteBatch sb) {
        if (Enabled) {
            TextureRegion t = new TextureRegion(Buffer.getColorBufferTexture());
            t.flip(false, true);
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            sb.draw(t, 0, 0, 0, 0, Buffer.getWidth(), Buffer.getHeight(), 1f, 1f, 0f);
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    /**
     These calls render ALL effects captured in the framebuffer using the same blending method or colorization.
     If you only want to colorize a particular effect, consider passing it through the Color parameter in Play/Modify instead
     */
    public static void RenderBrighter(SpriteBatch sb, Color color) {
        EUIRenderHelpers.DrawBrighter(sb, color, STSEffekseerManager::Render);
    }

    public static void RenderBrighter(SpriteBatch sb, Float color) {
        EUIRenderHelpers.DrawBrighter(sb, color, STSEffekseerManager::Render);
    }

    public static void RenderColored(SpriteBatch sb, Color color) {
        EUIRenderHelpers.DrawColored(sb, color, STSEffekseerManager::Render);
    }

    public static void RenderColored(SpriteBatch sb, Float color) {
        EUIRenderHelpers.DrawColored(sb, color, STSEffekseerManager::Render);
    }

    public static void RenderColorized(SpriteBatch sb, Color color) {
        EUIRenderHelpers.DrawColorized(sb, color, STSEffekseerManager::Render);
    }

    public static void RenderColorized(SpriteBatch sb, Float color) {
        EUIRenderHelpers.DrawColorized(sb, color, STSEffekseerManager::Render);
    }

    public static void Reset() {
        Clear();
        ManagerCore = new EffekseerManagerCore();
        ManagerCore.Initialize(BASE_SPRITES_DEFAULT);
    }

    public static void End() {
        if (Enabled) {
            Clear();
            EffekseerBackendCore.Terminate();
            Enabled = false;
        }
    }

    private static void Clear() {
        ManagerCore.delete();
        for (EffekseerEffectCore effect : ParticleEffects.values()) {
            effect.delete();
        }
        ParticleEffects.clear();
    }

}
