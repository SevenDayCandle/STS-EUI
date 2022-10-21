package extendedui.debug;

import eatyourbeets.interfaces.delegates.ActionT0;
import imgui.ImGui;

public class DEUIDisabledSection extends DEUIBaseT2<Boolean, ActionT0>
{
    public DEUIDisabledSection(String id)
    {
        super(id);
    }

    public void Render(Boolean disabled, ActionT0 onRender)
    {
        ImGui.beginDisabled(disabled);
        onRender.Invoke();
        ImGui.endDisabled();
    }
}
