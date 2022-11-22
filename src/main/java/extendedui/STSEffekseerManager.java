package extendedui;

import basemod.BaseMod;
import basemod.interfaces.ImGuiSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.configuration.EUIConfiguration;
import extendedui.debug.*;
import extendedui.swig.EffekseerBackendCore;
import extendedui.swig.EffekseerEffectCore;
import extendedui.swig.EffekseerManagerCore;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static extendedui.configuration.EUIConfiguration.BASE_SPRITES_DEFAULT;

public class STSEffekseerManager implements ImGuiSubscriber
{
    public static final float BASE_ANIMATION_SPEED = 60f;
    protected static float AnimationSpeed = BASE_ANIMATION_SPEED;
    protected static float ZPOS = 99;
    protected static String WINDOW_ID = "Effekseer";
    protected static String WINDOW_TABLE_ID = "Playing Effects";
    protected static String EFFECT_LIST_ID = "Effect List";
    protected static String EFFECT_LIST_POS_X_ID = "X##posX";
    protected static String EFFECT_LIST_POS_Y_ID = "Y##posY";
    protected static String EFFECT_LIST_ROT_X_ID = "X##rotX";
    protected static String EFFECT_LIST_ROT_Y_ID = "Y##rotY";
    protected static String EFFECT_LIST_ROT_Z_ID = "Z##rotZ";
    protected static String EFFECT_LIST_SCALE_X_ID = "X##scaleX";
    protected static String EFFECT_LIST_SCALE_Y_ID = "Y##scaleY";
    protected static String EFFECT_LIST_SCALE_Z_ID = "Z##scaleZ";
    protected static String EFFECT_LIST_COLOR_R_ID = "R##colorR";
    protected static String EFFECT_LIST_COLOR_G_ID = "G##colorG";
    protected static String EFFECT_LIST_COLOR_B_ID = "B##colorB";
    protected static String EFFECT_LIST_COLOR_A_ID = "A##colorA";
    protected static String EFFECT_LIST_PLAY_ID = "Play";
    protected static String EFFECT_LIST_TOGGLE_ID = "Show Playing";
    protected static String TABLE_ID = "Current Playing Effects";
    protected static STSEffekseerManager Instance;
    protected static final ArrayList<String> AvailablePaths = new ArrayList<>();
    private static final ConcurrentLinkedQueue<Integer> PlayingHandles = new ConcurrentLinkedQueue<>();
    private static final HashMap<String, EffekseerEffectCore> ParticleEffects = new HashMap<>();
    private static EffekseerManagerCore ManagerCore;
    private static boolean Enabled = false;


    private final DEUIWindow EffectWindow;
    private final DEUIListBox<String> EffectList;
    private final DEUICloseableWindow HandleWindow;
    private final DEUIToggle HandleToggle;
    private final DEUIDynamicActionTable<Integer> HandleTable;
    private final DEUIFloatInput EffectPosX;
    private final DEUIFloatInput EffectPosY;
    private final DEUIFloatInput EffectRotX;
    private final DEUIFloatInput EffectRotY;
    private final DEUIFloatInput EffectRotZ;
    private final DEUIFloatInput EffectScaleX;
    private final DEUIFloatInput EffectScaleY;
    private final DEUIFloatInput EffectScaleZ;
    private final DEUIFloatInput EffectColorR;
    private final DEUIFloatInput EffectColorG;
    private final DEUIFloatInput EffectColorB;
    private final DEUIFloatInput EffectColorA;
    private final DEUIButton EffectPlay;

    private STSEffekseerManager()
    {
        EffectWindow = new DEUIWindow(WINDOW_ID);
        EffectList = new DEUIListBox<String>(EFFECT_LIST_ID, AvailablePaths, p -> p);
        EffectPosX = new DEUIFloatInput(EFFECT_LIST_POS_X_ID, Settings.WIDTH * 0.5f);
        EffectPosY = new DEUIFloatInput(EFFECT_LIST_POS_Y_ID, Settings.HEIGHT * 0.5f);
        EffectRotX = new DEUIFloatInput(EFFECT_LIST_ROT_X_ID);
        EffectRotY = new DEUIFloatInput(EFFECT_LIST_ROT_Y_ID);
        EffectRotZ = new DEUIFloatInput(EFFECT_LIST_ROT_Z_ID);
        EffectScaleX = new DEUIFloatInput(EFFECT_LIST_SCALE_X_ID, 1, 0, Float.MAX_VALUE, 1, 10);
        EffectScaleY = new DEUIFloatInput(EFFECT_LIST_SCALE_Y_ID, 1, 0, Float.MAX_VALUE, 1, 10);
        EffectScaleZ = new DEUIFloatInput(EFFECT_LIST_SCALE_Z_ID, 1, 0, Float.MAX_VALUE, 1, 10);
        EffectColorR = new DEUIFloatInput(EFFECT_LIST_COLOR_R_ID, 255, 0, 255, 1, 10);
        EffectColorG = new DEUIFloatInput(EFFECT_LIST_COLOR_G_ID, 255, 0, 255, 1, 10);
        EffectColorB = new DEUIFloatInput(EFFECT_LIST_COLOR_B_ID, 255, 0, 255, 1, 10);
        EffectColorA = new DEUIFloatInput(EFFECT_LIST_COLOR_A_ID, 255, 0, 255, 1, 10);
        EffectPlay = new DEUIButton(EFFECT_LIST_PLAY_ID);

        HandleToggle = new DEUIToggle(EFFECT_LIST_TOGGLE_ID);
        HandleWindow = new DEUICloseableWindow(WINDOW_TABLE_ID).link(HandleToggle);
        HandleTable = new DEUIDynamicActionTable<Integer>(TABLE_ID, 4);
        HandleTable.setItems(PlayingHandles, handle -> {
                    return EUIUtils.array(
                            String.valueOf(handle),
                            String.valueOf(ManagerCore.GetFrame(handle)),
                            String.valueOf(ManagerCore.GetInstanceCount(handle)),
                            String.valueOf(ManagerCore.GetLayer(handle))
                    );
                })
                .setClick(STSEffekseerManager::stop, "Stop")
                .setColumnAction(() -> {
                    ImGui.tableSetupColumn("Handle", ImGuiTableColumnFlags.WidthStretch);
                    ImGui.tableSetupColumn("Frame", ImGuiTableColumnFlags.WidthStretch);
                    ImGui.tableSetupColumn("Particles", ImGuiTableColumnFlags.WidthStretch);
                    ImGui.tableSetupColumn("Layer", ImGuiTableColumnFlags.WidthStretch);
                });
    }

    /**
     Attempts to initialize the Effekseer system for the current OS and set up the buffer used for rendering
     */
    public static void initialize() {
        try {
            STSEffekSeerUtils.loadLibraryFromJar();
            EffekseerBackendCore.InitializeWithOpenGL();
            ManagerCore = new EffekseerManagerCore();
            ManagerCore.Initialize(BASE_SPRITES_DEFAULT);
            Enabled = true;
            Instance = new STSEffekseerManager();
            BaseMod.subscribe(Instance);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     Effects can only play if the manager is loaded and if in-game effects are enabled
     */
    public static boolean canPlay() {
        return Enabled && !Settings.DISABLE_EFFECTS && !EUIConfiguration.DisableEffekseer.get();
    }

    public static Integer play(String key, Vector2 position) {
        return play(key, position, null, null, (float[]) null);
    }

    public static Integer play(String key, Vector2 position, Vector3 rotation, Vector3 scale, Color color) {
        return play(key, position, rotation, scale, STSEffekSeerUtils.toEffekseerColor(color));
    }

    /**
     Start playing a new effect in Effekseer.
     Whenever a new effect type is loaded, its files are cached so that future calls to the same effect type do not need to load file data again
     Note that rotations are in radians
     */
    public static Integer play(String key, Vector2 position, Vector3 rotation, Vector3 scale, float[] color) {
        if (canPlay()) {
            try {
                EffekseerEffectCore effect = ParticleEffects.get(key);
                if (effect == null) {
                    effect = STSEffekSeerUtils.loadEffect(key);
                    ParticleEffects.put(key, effect);
                }
                if (effect != null) {
                    int handle = ManagerCore.Play(effect);
                    ManagerCore.SetEffectPosition(handle, position.x, position.y, ZPOS);
                    if (rotation != null) {
                        ManagerCore.SetEffectRotation(handle, rotation.x, rotation.y, rotation.z);
                    }
                    if (scale != null) {
                        ManagerCore.SetEffectScale(handle, scale.x, scale.y, scale.z);
                    }
                    if (color != null) {
                        ManagerCore.SetAllColor(handle, color[0], color[1], color[2], color[3]);
                    }
                    PlayingHandles.add(handle);
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

    public static boolean modify(int handle, Vector2 position, Vector3 rotation, Vector3 scale, Color color)  {
        return modify(handle, position, rotation, scale, STSEffekSeerUtils.toEffekseerColor(color));
    }

    /**
     Edit the attributes of an effect that is currently playing
     */
    public static boolean modify(int handle, Vector2 position, Vector3 rotation, Vector3 scale, float[] color) {
        if (Enabled && ManagerCore.Exists(handle)) {
            if (position != null)         {
                ManagerCore.SetEffectPosition(handle, position.x, position.y, ZPOS);
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
    public static boolean exists(int handle){
        return Enabled && ManagerCore.Exists(handle);
    }

    /**
     Force an animation to stop playing
     */
    public static void stop(int handle){
        if (Enabled) {
            ManagerCore.Stop(handle);
        }
    }

    /**
     Force all animations to stop playing
     */
    public static void stopAll(){
        if (Enabled) {
            ManagerCore.StopAllEffects();
        }
    }

    /**
     Set the global animation speed for ALL animations
     */
    public static void setAnimationSpeed(float speed) {
        AnimationSpeed = Math.max(0, speed);
    }

    /**
     The color and depth bits must be cleared before every render in order for effekseer effects to work
     */
    public static void preUpdate() {
        PlayingHandles.removeIf(handle -> !exists(handle));
        if (!PlayingHandles.isEmpty())
        {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);
        }
    }

    /**
     Advances the animations of all playing animations.
     */
    public static void update() {
        if (canPlay() && !PlayingHandles.isEmpty()) {
            ManagerCore.SetViewProjectionMatrixWithSimpleWindowAndUpdate(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Gdx.graphics.getDeltaTime() * AnimationSpeed);
            ManagerCore.Draw();
        }
    }

    /* Add Effekseer file paths to the file selector */
    public static void register(Collection<String> effects)
    {
        AvailablePaths.addAll(effects);
    }

    public static void reset() {
        clear();
        ManagerCore = new EffekseerManagerCore();
        ManagerCore.Initialize(BASE_SPRITES_DEFAULT);
    }

    public static void end() {
        if (Enabled) {
            clear();
            EffekseerBackendCore.Terminate();
            Enabled = false;
        }
    }

    private static void clear() {
        ManagerCore.delete();
        for (EffekseerEffectCore effect : ParticleEffects.values()) {
            effect.delete();
        }
        ParticleEffects.clear();
    }

    @Override
    public void receiveImGui()
    {
        EffectWindow.render(() -> {
            HandleToggle.render();
            ImGui.separator();
            EffectList.render();
            DEUIUtils.withWidth(110, () -> {
                DEUIUtils.inlineText("Pos");
                EffectPosX.renderInline();
                EffectPosY.render();
                DEUIUtils.inlineText("Rot");
                EffectRotX.renderInline();
                EffectRotY.renderInline();
                EffectRotZ.render();
                DEUIUtils.inlineText("Scale");
                EffectScaleX.renderInline();
                EffectScaleY.renderInline();
                EffectScaleZ.render();
                DEUIUtils.inlineText("Color");
                EffectColorR.renderInline();
                EffectColorG.renderInline();
                EffectColorB.renderInline();
                EffectColorA.render();
            });
            EffectPlay.render(() -> {
                        String value = EffectList.get();
                        if (value != null)
                        {
                            play(EffectList.get(),
                                    new Vector2(EffectPosX.get(), EffectPosY.get()),
                                    new Vector3(EffectRotX.get(), EffectRotY.get(), EffectRotZ.get()),
                                    new Vector3(EffectScaleX.get(), EffectScaleY.get(), EffectScaleZ.get()),
                                    new float[]{EffectColorR.get(), EffectColorG.get(), EffectColorB.get(), EffectColorA.get()}
                            );
                        }
                    }
                );
            });
        HandleWindow.render(HandleTable::render);
    }
}
