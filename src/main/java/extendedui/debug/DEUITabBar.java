package extendedui.debug;

import extendedui.interfaces.delegates.ActionT0;
import imgui.ImGui;

public class DEUITabBar extends DEUIBaseT1<ActionT0> {
    public DEUITabBar(String id) {
        super(id);
    }

    public static void render(String id, ActionT0 onRender) {
        if (ImGui.beginTabBar(id)) {
            onRender.invoke();
            ImGui.endTabBar();
        }
    }

    public void render(ActionT0 onRender) {
        render(ID, onRender);
    }
}
