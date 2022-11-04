package extendedui.debug;

import eatyourbeets.interfaces.delegates.ActionT0;
import imgui.ImGui;

public class DEUIUtils
{
    public static void DisabledIf(boolean disabled, ActionT0 onRender)
    {
        ImGui.beginDisabled(disabled);
        onRender.Invoke();
        ImGui.endDisabled();
    }

    public static String GetNameWithID(String name, String id)
    {
        return name + "###" + id;
    }

    public static void InlineText(String text)
    {
        ImGui.text(text);
        ImGui.sameLine();
    }

    public static void WithFullWidth(ActionT0 onRender)
    {
        WithWidth(-1, onRender);
    }

    public static void WithWidth(int width, ActionT0 onRender)
    {
        ImGui.pushItemWidth(90);
        onRender.Invoke();
        ImGui.popItemWidth();
    }
}
