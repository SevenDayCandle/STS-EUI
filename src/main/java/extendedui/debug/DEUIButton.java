package extendedui.debug;

import extendedui.interfaces.delegates.ActionT0;
import imgui.ImGui;

public class DEUIButton extends DEUIBaseT1<ActionT0>
{
    public DEUIButton(String id)
    {
        super(id);
    }

    public void render(ActionT0 onClick)
    {
        render(ID, onClick);
    }

    public static void render(String id, ActionT0 onClick)
    {
        if (ImGui.button(id))
        {
            onClick.invoke();
        }
    }
}
