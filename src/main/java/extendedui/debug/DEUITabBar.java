package extendedui.debug;

import eatyourbeets.interfaces.delegates.ActionT0;
import imgui.ImGui;

public class DEUITabBar extends DEUIBaseT1<ActionT0>
{
    public DEUITabBar(String id)
    {
        super(id);
    }

    public void Render(ActionT0 onRender)
    {
        Render(ID, onRender);
    }

    public static void Render(String id, ActionT0 onRender)
    {
        if (ImGui.beginTabBar(id))
        {
            onRender.Invoke();
            ImGui.endTabBar();
        }
    }
}
