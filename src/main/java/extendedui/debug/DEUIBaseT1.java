package extendedui.debug;

import eatyourbeets.interfaces.delegates.ActionT0;
import imgui.ImGui;

public abstract class DEUIBaseT1<T> extends DEUIBase
{
    public DEUIBaseT1(String id)
    {
        super(id);
    }

    public void RenderInline(T onClick)
    {
        Render(onClick);
        ImGui.sameLine();
    }

    public abstract void Render(T onClick);
}
