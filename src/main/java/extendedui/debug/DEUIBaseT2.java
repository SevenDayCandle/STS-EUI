package extendedui.debug;

import imgui.ImGui;

public abstract class DEUIBaseT2<T1, T2> extends DEUIBase
{
    public DEUIBaseT2(String id)
    {
        super(id);
    }

    public void renderInline(T1 onClick1, T2 onClick2)
    {
        render(onClick1, onClick2);
        ImGui.sameLine();
    }

    public abstract void render(T1 onClick1, T2 onClick2);
}
