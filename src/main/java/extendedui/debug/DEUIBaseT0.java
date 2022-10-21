package extendedui.debug;

import imgui.ImGui;

public abstract class DEUIBaseT0 extends DEUIBase
{
    public DEUIBaseT0(String id)
    {
        super(id);
    }

    public void RenderInline()
    {
        Render();
        ImGui.sameLine();
    }

    public abstract void Render();
}
