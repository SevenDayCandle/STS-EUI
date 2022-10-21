package extendedui.debug;

import eatyourbeets.interfaces.delegates.ActionT0;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;

import static extendedui.ui.EUIBase.Scale;

public class DEUIWindow extends DEUIBaseT1<ActionT0>
{
    protected float width;
    protected float height;
    protected float x;
    protected float y;
    protected int setMode = ImGuiCond.FirstUseEver;

    public DEUIWindow(String id)
    {
        this(id, 0, 0, Scale(100), Scale(100), ImGuiCond.FirstUseEver);
    }

    public DEUIWindow(String id, float x, float y, float width, float height, int setMode)
    {
        super(id);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.setMode = setMode;
    }

    public DEUIWindow SetDimensions(float x, float y, float width, float height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    public DEUIWindow SetSetMode(int mode)
    {
        this.setMode = mode;
        return this;
    }

    public void Render(ActionT0 onRender)
    {
        Render(onRender, x, y, width, height, setMode);
    }

    public void Render(ActionT0 onRender, float x, float y, float width, float height, int setMode)
    {
        ImVec2 wPos = ImGui.getMainViewport().getPos();
        ImGui.setNextWindowPos(wPos.x + x, wPos.y + y, setMode);
        ImGui.setNextWindowSize(width, height, setMode);
        if (ImGui.begin(ID))
        {
            onRender.Invoke();
        }
        ImGui.end();
    }
}
