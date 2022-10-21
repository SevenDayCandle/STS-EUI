package extendedui.debug;

import eatyourbeets.interfaces.delegates.ActionT0;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.type.ImBoolean;

import static extendedui.ui.EUIBase.Scale;

public class DEUICloseableWindow extends DEUIWindow
{
    protected ImBoolean value = new ImBoolean();

    public DEUICloseableWindow(String id)
    {
        this(id, 0, 0, Scale(100), Scale(100), ImGuiCond.FirstUseEver);
    }

    public DEUICloseableWindow(String id, float x, float y, float width, float height, int setMode)
    {
        super(id, x, y, width, height, setMode);
    }

    public DEUICloseableWindow SetDimensions(float x, float y, float width, float height)
    {
        super.SetDimensions(x, y, width, height);
        return this;
    }

    public DEUICloseableWindow SetSetMode(int mode)
    {
        super.SetSetMode(mode);
        return this;
    }

    public DEUICloseableWindow Link(DEUIToggle toggle)
    {
        this.value = toggle.value;
        return this;
    }

    public void Render(ActionT0 onRender)
    {
        if (value.get())
        {
            ImVec2 wPos = ImGui.getMainViewport().getPos();
            ImGui.setNextWindowPos(wPos.x + x, wPos.y + y, setMode);
            ImGui.setNextWindowSize(width, height, setMode);
            if (ImGui.begin(ID, value))
            {
                onRender.Invoke();
            }
            ImGui.end();
        }
    }
}
