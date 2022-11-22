package extendedui.debug;

import imgui.ImGui;

public abstract class DEUIBaseT1<T> extends DEUIBase
{
    public DEUIBaseT1(String id)
    {
        super(id);
    }

    public void renderInline(T onClick)
    {
        render(onClick);
        ImGui.sameLine();
    }

    public abstract void render(T onClick);
}
