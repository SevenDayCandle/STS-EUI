package extendedui.debug;

import extendedui.interfaces.delegates.ActionT0;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

import static extendedui.ui.EUIBase.scale;

public class DEUIWindow extends DEUIBaseT1<ActionT0> {
    protected float width;
    protected float height;
    protected float x;
    protected float y;
    protected int setMode = ImGuiCond.FirstUseEver;
    protected int windowMode = ImGuiWindowFlags.None;

    public DEUIWindow(String id) {
        this(id, 0, 0, scale(100), scale(100), ImGuiCond.FirstUseEver, ImGuiWindowFlags.None);
    }

    public DEUIWindow(String id, float x, float y, float width, float height, int setMode, int windowMode) {
        super(id);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.setMode = setMode;
        this.windowMode = windowMode;
    }

    public void render(ActionT0 onRender) {
        render(onRender, x, y, width, height, setMode, windowMode);
    }

    public void render(ActionT0 onRender, float x, float y, float width, float height, int setMode, int windowMode) {
        ImVec2 wPos = ImGui.getMainViewport().getPos();
        ImGui.setNextWindowPos(wPos.x + x, wPos.y + y, setMode);
        ImGui.setNextWindowSize(width, height, setMode);
        if (ImGui.begin(ID, windowMode)) {
            onRender.invoke();
        }
        ImGui.end();
    }

    public DEUIWindow setDimensions(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    public DEUIWindow setSetMode(int mode) {
        this.setMode = mode;
        return this;
    }

    public DEUIWindow setWindowMode(int mode) {
        this.windowMode = mode;
        return this;
    }
}
