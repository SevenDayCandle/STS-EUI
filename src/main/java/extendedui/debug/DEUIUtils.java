package extendedui.debug;

import extendedui.interfaces.delegates.ActionT0;
import imgui.ImGui;

public class DEUIUtils
{
    public static void disabledIf(boolean disabled, ActionT0 onRender)
    {
        ImGui.beginDisabled(disabled);
        onRender.invoke();
        ImGui.endDisabled();
    }

    public static String getNameWithID(String name, String id)
    {
        return name + "###" + id;
    }

    public static void inlineText(String text)
    {
        ImGui.text(text);
        ImGui.sameLine();
    }

    public static void withFullWidth(ActionT0 onRender)
    {
        withWidth(-1, onRender);
    }

    public static void withWidth(int width, ActionT0 onRender)
    {
        ImGui.pushItemWidth(90);
        onRender.invoke();
        ImGui.popItemWidth();
    }
}
