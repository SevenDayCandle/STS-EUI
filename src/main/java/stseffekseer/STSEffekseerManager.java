package stseffekseer;

import Effekseer.swig.EffekseerBackendCore;
import Effekseer.swig.EffekseerEffectCore;
import Effekseer.swig.EffekseerManagerCore;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;

import static stseffekseer.STSEffekSeerUtils.LoadEffect;

public class STSEffekseerManager {
    protected static final float BASE_ANIMATION_SPEED = 60f;
    protected static final int MAX_SPRITES = 3000;
    protected static final HashMap<String, EffekseerEffectCore> ParticleEffects = new HashMap<>();
    protected static EffekseerManagerCore ManagerCore;
    protected static FrameBuffer Buffer;
    protected static float AnimationSpeed = BASE_ANIMATION_SPEED;

    public static void Initialize() {
        try {
            STSEffekSeerUtils.LoadLibraryFromJar();
            EffekseerBackendCore.InitializeAsOpenGL();
            ManagerCore = new EffekseerManagerCore();
            ManagerCore.Initialize(MAX_SPRITES);
            Buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer Play(String key, Vector2 position) {
        return Play(key, position, null);
    }

    public static Integer Play(String key, Vector2 position, Vector3 rotation) {
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
                return handle;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static boolean Modify(int handle, Vector2 position, Vector3 rotation) {
        if (ManagerCore.Exists(handle)) {
            if (position != null)         {
                ManagerCore.SetEffectPosition(handle, position.x, position.y, 0);
            }
            if (rotation != null) {
                ManagerCore.SetEffectRotation(handle, rotation.x, rotation.y, rotation.z);
            }
            return true;
        }
        return false;
    }

    public static boolean Exists(int handle){
        return ManagerCore.Exists(handle);
    }

    public static void SetAnimationSpeed(float speed) {
        AnimationSpeed = Math.max(0, speed);
    }

    public static void Update() {
        ManagerCore.SetViewProjectionMatrixWithSimpleWindow(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ManagerCore.Update(Gdx.graphics.getDeltaTime() * AnimationSpeed);
        Buffer.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
        ManagerCore.DrawBack();
        ManagerCore.DrawFront();
        Buffer.end();
    }

    public static void Render(SpriteBatch sb) {
        TextureRegion t = new TextureRegion(Buffer.getColorBufferTexture());
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        sb.draw(t, 0, 0, 0, 0, Buffer.getWidth(), Buffer.getHeight(), 1f, 1f, 0f);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void End() {
        ManagerCore.delete();
        for (EffekseerEffectCore effect : ParticleEffects.values()) {
            effect.delete();
        }
        EffekseerBackendCore.Terminate();

    }
}
