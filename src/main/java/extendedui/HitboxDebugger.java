package extendedui;

import basemod.BaseMod;
import basemod.interfaces.ImGuiSubscriber;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.debug.DEUIFloatInput;
import extendedui.debug.DEUIUtils;
import extendedui.debug.DEUIWindow;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import imgui.ImGui;

public class HitboxDebugger implements ImGuiSubscriber {
    private static final String WINDOW_ID = "Hitbox";
    private static final String HITBOX_ADDRESS = "Address";
    private static final String HITBOX_X_ID = "X##hbX";
    private static final String HITBOX_Y_ID = "Y##hbY";
    private static final String HITBOX_W_ID = "W##hbW";
    private static final String HITBOX_H_ID = "H##hbH";
    private static HitboxDebugger instance;
    private final DEUIWindow effectWindow;
    private final DEUIFloatInput hbX;
    private final DEUIFloatInput hbY;
    private final DEUIFloatInput hbW;
    private final DEUIFloatInput hbH;
    private EUIHitbox current;

    private HitboxDebugger() {
        effectWindow = new DEUIWindow(WINDOW_ID);

        hbX = new DEUIFloatInput(HITBOX_X_ID, 0);
        hbY = new DEUIFloatInput(HITBOX_Y_ID, 0);
        hbW = new DEUIFloatInput(HITBOX_W_ID, 0);
        hbH = new DEUIFloatInput(HITBOX_H_ID, 0);
    }

    public static void initialize() {
        try {
            instance = new HitboxDebugger();
            BaseMod.subscribe(instance);
        }
        catch (Exception e) {
            EUIUtils.logInfoIfDebug(HitboxDebugger.class, "Unable to load hitbox debugger");
        }
    }

    public static void tryRegister(EUIHitbox current) {
        if (instance != null) {
            instance.register(current);
        }
    }

    protected float applyScale(float v) {
        return v * Settings.scale;
    }

    @Override
    public void receiveImGui() {
        effectWindow.render(() -> {
            ImGui.text(HITBOX_ADDRESS + ": " + (current != null ? current.toString() : ""));
            DEUIUtils.withWidth(90, hbX::renderInline);
            DEUIUtils.withWidth(90, hbY::renderInline);
            DEUIUtils.withWidth(90, hbW::renderInline);
            DEUIUtils.withWidth(90, hbH::render);
        });
    }

    public void register(EUIHitbox current) {
        this.current = current;
        if (current instanceof RelativeHitbox) {
            hbX.set(((RelativeHitbox) current).offsetX);
            hbY.set(((RelativeHitbox) current).offsetY);
            hbW.set(unapplyScale(current.width));
            hbH.set(unapplyScale(current.height));
        }
        else if (current != null) {
            hbX.set(unapplyScale(current.x));
            hbY.set(unapplyScale(current.y));
            hbW.set(unapplyScale(current.width));
            hbH.set(unapplyScale(current.height));
        }
    }

    protected float unapplyScale(float v) {
        return v / Settings.scale;
    }

    public void updateHitbox() {
        if (this.current instanceof RelativeHitbox) {
            this.current.setOffset(hbX.get(), hbY.get());
            this.current.width = applyScale(hbW.get());
            this.current.height = applyScale(hbW.get());
        }
        else if (this.current != null) {
            this.current.translate(hbX.get(), hbY.get());
            this.current.width = applyScale(hbW.get());
            this.current.height = applyScale(hbW.get());
        }
    }
}
