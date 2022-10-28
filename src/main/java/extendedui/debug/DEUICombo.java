package extendedui.debug;

import eatyourbeets.interfaces.delegates.FuncT1;
import imgui.ImGui;
import imgui.ImGuiTextFilter;
import imgui.type.ImInt;

import java.util.ArrayList;

public class DEUICombo<T> extends DEUIBaseT0
{
    protected T selected;
    protected ArrayList<T> items;
    protected FuncT1<String, T> stringFunc;

    public DEUICombo(String id, ArrayList<T> items, FuncT1<String, T> stringFunc)
    {
        super(id);
        this.items = items;
        this.stringFunc = stringFunc;
        if (this.items.size() > 0)
        {
            selected = items.get(0);
        }
    }

    public T Get()
    {
        return selected;
    }

    public void Render()
    {
        if (ImGui.beginCombo(ID, AsLabel())) {
            for (T item : items) {
                boolean isSelected = item.equals(selected);
                if (ImGui.selectable(stringFunc.Invoke(item), isSelected)) {
                    selected = item;
                }

                if (isSelected) {
                    ImGui.setItemDefaultFocus();
                }
            }
            ImGui.endCombo();
        }
    }

    protected String AsLabel()
    {
        if (selected == null)
        {
            return "##null";
        }
        return stringFunc.Invoke(selected);
    }
}
