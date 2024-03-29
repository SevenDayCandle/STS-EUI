package extendedui.debug;

import extendedui.interfaces.delegates.ActionT0;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

import static extendedui.ui.EUIBase.scale;

public class DEUICloseableWindow extends DEUIWindow {
    protected ImBoolean value = new ImBoolean();

    public DEUICloseableWindow(String id) {
        this(id, 0, 0, scale(100), scale(100), ImGuiCond.FirstUseEver, ImGuiWindowFlags.None);
    }

    public DEUICloseableWindow(String id, float x, float y, float width, float height, int setMode, int windowMode) {
        super(id, x, y, width, height, setMode, windowMode);
    }

    public DEUICloseableWindow link(DEUIToggle toggle) {
        this.value = toggle.value;
        return this;
    }

    public void render(ActionT0 onRender) {
        if (value.get()) {
            ImVec2 wPos = ImGui.getMainViewport().getPos();
            ImGui.setNextWindowPos(wPos.x + x, wPos.y + y, setMode);
            ImGui.setNextWindowSize(width, height, setMode);
            if (ImGui.begin(ID, value)) {
                onRender.invoke();
            }
            ImGui.end();
        }
    }

    public DEUICloseableWindow setDimensions(float x, float y, float width, float height) {
        super.setDimensions(x, y, width, height);
        return this;
    }

    public DEUICloseableWindow setSetMode(int mode) {
        super.setSetMode(mode);
        return this;
    }
}
