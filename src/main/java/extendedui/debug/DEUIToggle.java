package extendedui.debug;

import imgui.ImGui;
import imgui.type.ImBoolean;

public class DEUIToggle extends DEUIBaseT0
{
    protected ImBoolean value;

    public DEUIToggle(String id)
    {
        this(id, false);
    }

    public DEUIToggle(String id, boolean defaultValue)
    {
        super(id);
        value = new ImBoolean(defaultValue);
    }

    public boolean Get()
    {
        return value.get();
    }

    public void Render()
    {
        ImGui.checkbox(ID, value);
    }
}
