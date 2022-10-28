package extendedui.debug;

import eatyourbeets.interfaces.delegates.FuncT1;
import imgui.ImGui;
import imgui.ImVec2;

import java.util.ArrayList;

import static extendedui.ui.EUIBase.Scale;

public class DEUISuffixListBox<T> extends DEUIListBox<T>
{
    protected FuncT1<String, T> suffixFunc;
    protected final ImVec2 textSize = new ImVec2();

    public DEUISuffixListBox(String id, ArrayList<T> items, FuncT1<String, T> stringFunc, FuncT1<String, T> suffixFunc)
    {
        this(id, items, stringFunc, suffixFunc, -1, Scale(200));
    }

    public DEUISuffixListBox(String id, ArrayList<T> items, FuncT1<String, T> stringFunc, FuncT1<String, T> suffixFunc, float width, float height)
    {
        super(id, items, stringFunc, width, height);
        this.suffixFunc = suffixFunc;
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

                String text = suffixFunc.Invoke(item);
                ImGui.calcTextSize(textSize, text);
                ImGui.sameLine(ImGui.getWindowContentRegionMaxX() - textSize.x);
                ImGui.text(text);

                if (isSelected) {
                    ImGui.setItemDefaultFocus();
                }
            }
            ImGui.endListBox();
        }
    }
}
