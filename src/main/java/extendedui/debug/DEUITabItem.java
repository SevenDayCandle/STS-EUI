package extendedui.debug;

import eatyourbeets.interfaces.delegates.ActionT0;
import imgui.ImGui;

public class DEUITabItem extends DEUIBaseT1<ActionT0>
{
    public DEUITabItem(String id)
    {
        super(id);
    }

    public void render(ActionT0 onRender)
    {
        render(ID, onRender);
    }

    public static void render(String id, ActionT0 onRender)
    {
        if (ImGui.beginTabItem(id))
        {
            onRender.invoke();
            ImGui.endTabItem();
        }
    }
}
