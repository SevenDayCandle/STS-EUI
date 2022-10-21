package extendedui.debug;

import imgui.ImGui;

public abstract class DEUIBaseT2<T1, T2> extends DEUIBase
{
    public DEUIBaseT2(String id)
    {
        super(id);
    }

    public void RenderInline(T1 onClick1, T2 onClick2)
    {
        Render(onClick1, onClick2);
        ImGui.sameLine();
    }

    public abstract void Render(T1 onClick1, T2 onClick2);
}
