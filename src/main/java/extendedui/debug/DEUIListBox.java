package extendedui.debug;

import eatyourbeets.interfaces.delegates.FuncT1;
import imgui.ImGui;

import java.util.ArrayList;

import static extendedui.ui.EUIBase.Scale;

public class DEUIListBox<T> extends DEUIBaseT0
{
    protected T selected;
    protected ArrayList<T> items;
    protected FuncT1<String, T> stringFunc;
    protected float width = -1;
    protected float height = -1;

    public DEUIListBox(String id, ArrayList<T> items, FuncT1<String, T> stringFunc)
    {
        this(id, items, stringFunc, -1, Scale(200));
    }

    public DEUIListBox(String id, ArrayList<T> items, FuncT1<String, T> stringFunc, float width, float height)
    {
        super(id);
        this.items = items;
        this.stringFunc = stringFunc;
        this.width = width;
        this.height = height;
    }

    public T Get()
    {
        return selected;
    }

    public void Render()
    {
        Render(width, height);
    }

    public void Render(float width, float height)
    {
        if (ImGui.beginListBox(ID, width, height)) {
            for (T item : items) {
                boolean isSelected = item.equals(selected);
                if (ImGui.selectable(stringFunc.Invoke(item), isSelected)) {
                    selected = item;
                }

                if (isSelected) {
                    ImGui.setItemDefaultFocus();
                }
            }
            ImGui.endListBox();
        }
    }
}
