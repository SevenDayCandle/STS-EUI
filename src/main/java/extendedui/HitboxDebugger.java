package extendedui;

import basemod.BaseMod;
import basemod.interfaces.ImGuiSubscriber;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.debug.DEUIFloatInput;
import extendedui.debug.DEUIToggle;
import extendedui.debug.DEUIWindow;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import imgui.ImGui;

public class HitboxDebugger implements ImGuiSubscriber
{
    protected static HitboxDebugger instance;
    protected static final String WINDOW_ID = "Hitbox";
    protected static final String HITBOX_ADDRESS = "Address";
    protected static final String HITBOX_X_ID = "X##hbX";
    protected static final String HITBOX_Y_ID = "Y##hbY";
    protected static final String HITBOX_W_ID = "W##hbW";
    protected static final String HITBOX_H_ID = "H##hbH";
    protected static final String HITBOX_PERCENTAGE = "Is Percentage";

    private final DEUIWindow effectWindow;
    private final DEUIFloatInput hbX;
    private final DEUIFloatInput hbY;
    private final DEUIFloatInput hbW;
    private final DEUIFloatInput hbH;
    private final DEUIToggle hbPercentage;
    private EUIHitbox current;

    private HitboxDebugger()
    {
        effectWindow = new DEUIWindow(WINDOW_ID);

        hbX = new DEUIFloatInput(HITBOX_X_ID, 0);
        hbY = new DEUIFloatInput(HITBOX_Y_ID, 0);
        hbW = new DEUIFloatInput(HITBOX_W_ID, 0);
        hbH = new DEUIFloatInput(HITBOX_H_ID, 0);
        hbPercentage = new DEUIToggle(HITBOX_PERCENTAGE);
    }

    public static void tryRegister(EUIHitbox current)
    {
        if (instance != null)
        {
            instance.register(current);
        }
    }

    public static void initialize() {
        try
        {
            instance = new HitboxDebugger();
            BaseMod.subscribe(instance);
        }
        catch (Exception e)
        {
            EUIUtils.logInfoIfDebug(HitboxDebugger.class, "Unable to load hitbox debugger");
        }
    }

    @Override
    public void receiveImGui()
    {
        effectWindow.render(() -> {
            ImGui.text(HITBOX_ADDRESS + ": " + (current != null ? current.toString() : ""));
            hbX.renderInline();
            hbY.renderInline();
            hbW.renderInline();
            hbH.render();
            if (current instanceof RelativeHitbox)
            {
                hbPercentage.render();
            }
        });
    }

    public void register(EUIHitbox current)
    {
        this.current = current;
        if (current instanceof RelativeHitbox)
        {
            hbX.set(((RelativeHitbox) current).offsetCx);
            hbY.set(((RelativeHitbox) current).offsetCy);
            hbW.set(unapplyScale(current.width));
            hbH.set(unapplyScale(current.height));
            hbPercentage.set(((RelativeHitbox) current).percentageOffset);
        }
        else if (current != null)
        {
            hbX.set(unapplyScale(current.x));
            hbY.set(unapplyScale(current.y));
            hbW.set(unapplyScale(current.width));
            hbH.set(unapplyScale(current.height));
        }
    }

    public void updateHitbox()
    {
        if (this.current instanceof RelativeHitbox)
        {
            ((RelativeHitbox) this.current).setOffset(hbX.get(), hbY.get(), hbPercentage.get());
            this.current.width = applyScale(hbW.get());
            this.current.height = applyScale(hbW.get());
        }
        else if (this.current != null)
        {
            this.current.translate(hbX.get(), hbY.get());
            this.current.width = applyScale(hbW.get());
            this.current.height = applyScale(hbW.get());
        }
    }

    protected float applyScale(float v) {return v * Settings.scale;}
    protected float unapplyScale(float v) {return v / Settings.scale;}
}
