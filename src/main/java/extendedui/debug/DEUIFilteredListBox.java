package extendedui.debug;

import extendedui.interfaces.delegates.FuncT1;
import imgui.ImGui;

import java.util.ArrayList;

import static extendedui.ui.EUIBase.scale;

public class DEUIFilteredListBox<T> extends DEUIListBox<T>
{
    protected FuncT1<Boolean, T> evalFunc;

    public DEUIFilteredListBox(String id, ArrayList<T> items, FuncT1<String, T> stringFunc, FuncT1<Boolean, T> evalFunc)
    {
        this(id, items, stringFunc, evalFunc, -1, scale(200));
    }

    public DEUIFilteredListBox(String id, ArrayList<T> items, FuncT1<String, T> stringFunc, FuncT1<Boolean, T> evalFunc, float width, float height)
    {
        super(id, items, stringFunc, width, height);
        this.evalFunc = evalFunc;
    }

    public void render()
    {
        render(width, height);
    }

    public void render(float width, float height)
    {
        if (ImGui.beginListBox(ID, width, height)) {
            for (T item : items) {
                if (evalFunc.invoke(item))
                {
                    boolean isSelected = item.equals(selected);
                    if (ImGui.selectable(stringFunc.invoke(item), isSelected)) {
                        selected = item;
                    }

                    if (isSelected) {
                        ImGui.setItemDefaultFocus();
                    }
                }
            }
            ImGui.endListBox();
        }
    }
}
