package extendedui.debug;

import eatyourbeets.interfaces.delegates.ActionT0;
import imgui.ImGui;

public class DEUIButton extends DEUIBaseT1<ActionT0>
{
    public DEUIButton(String id)
    {
        super(id);
    }

    public void Render(ActionT0 onClick)
    {
        if (ImGui.button(ID))
        {
            onClick.Invoke();
        }
    }
}
