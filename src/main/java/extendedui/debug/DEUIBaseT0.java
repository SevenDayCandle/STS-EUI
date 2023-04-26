package extendedui.debug;

import imgui.ImGui;

public abstract class DEUIBaseT0 extends DEUIBase {
    public DEUIBaseT0(String id) {
        super(id);
    }

    public void renderInline() {
        render();
        ImGui.sameLine();
    }

    public abstract void render();
}
