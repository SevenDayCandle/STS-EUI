package extendedui.debug;

import eatyourbeets.interfaces.delegates.FuncT1;
import imgui.ImGui;

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

    public T get()
    {
        return selected;
    }

    public void set(T item)
    {
        if (items.contains(item))
        {
            selected = item;
        }
    }

    public void render()
    {
        if (ImGui.beginCombo(ID, asLabel())) {
            for (T item : items) {
                boolean isSelected = item.equals(selected);
                if (ImGui.selectable(stringFunc.invoke(item), isSelected)) {
                    selected = item;
                }

                if (isSelected) {
                    ImGui.setItemDefaultFocus();
                }
            }
            ImGui.endCombo();
        }
    }

    protected String asLabel()
    {
        if (selected == null)
        {
            if (this.items.size() > 0)
            {
                selected = items.get(0);
            }
            else
            {
                return "##null";
            }
        }
        return stringFunc.invoke(selected);
    }
}
