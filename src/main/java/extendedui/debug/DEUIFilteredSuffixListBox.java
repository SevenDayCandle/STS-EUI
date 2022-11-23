package extendedui.debug;

import extendedui.interfaces.delegates.FuncT1;
import imgui.ImGui;

import java.util.ArrayList;

import static extendedui.ui.EUIBase.scale;

public class DEUIFilteredSuffixListBox<T> extends DEUISuffixListBox<T>
{
    protected FuncT1<Boolean, T> evalFunc;

    public DEUIFilteredSuffixListBox(String id, ArrayList<T> items, FuncT1<String, T> stringFunc, FuncT1<String, T> suffixFunc, FuncT1<Boolean, T> evalFunc)
    {
        this(id, items, stringFunc, suffixFunc, evalFunc, -1, scale(200));
    }

    public DEUIFilteredSuffixListBox(String id, ArrayList<T> items, FuncT1<String, T> stringFunc, FuncT1<String, T> suffixFunc, FuncT1<Boolean, T> evalFunc, float width, float height)
    {
        super(id, items, stringFunc, suffixFunc, width, height);
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

                    String text = suffixFunc.invoke(item);
                    ImGui.calcTextSize(textSize, text);
                    ImGui.sameLine(ImGui.getWindowContentRegionMaxX() - textSize.x);
                    ImGui.text(text);

                    if (isSelected) {
                        ImGui.setItemDefaultFocus();
                    }
                }
            }
            ImGui.endListBox();
        }
    }
}
