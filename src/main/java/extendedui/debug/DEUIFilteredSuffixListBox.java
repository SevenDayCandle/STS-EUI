package extendedui.debug;

import eatyourbeets.interfaces.delegates.FuncT1;
import imgui.ImGui;
import imgui.ImVec2;

import java.util.ArrayList;

import static extendedui.ui.EUIBase.Scale;

public class DEUIFilteredSuffixListBox<T> extends DEUISuffixListBox<T>
{
    protected FuncT1<Boolean, T> evalFunc;

    public DEUIFilteredSuffixListBox(String id, ArrayList<T> items, FuncT1<String, T> stringFunc, FuncT1<String, T> suffixFunc, FuncT1<Boolean, T> evalFunc)
    {
        this(id, items, stringFunc, suffixFunc, evalFunc, -1, Scale(200));
    }

    public DEUIFilteredSuffixListBox(String id, ArrayList<T> items, FuncT1<String, T> stringFunc, FuncT1<String, T> suffixFunc, FuncT1<Boolean, T> evalFunc, float width, float height)
    {
        super(id, items, stringFunc, suffixFunc, width, height);
        this.evalFunc = evalFunc;
    }

    public void Render()
    {
        Render(width, height);
    }

    public void Render(float width, float height)
    {
        if (ImGui.beginListBox(ID, width, height)) {
            for (T item : items) {
                if (evalFunc.Invoke(item))
                {
                    boolean isSelected = item.equals(selected);
                    if (ImGui.selectable(stringFunc.Invoke(item), isSelected)) {
                        selected = item;
                    }

                    String text = suffixFunc.Invoke(item);
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
