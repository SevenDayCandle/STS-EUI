package extendedui.debug;

import eatyourbeets.interfaces.delegates.ActionT1;
import eatyourbeets.interfaces.delegates.FuncT1;
import imgui.ImGui;

public class DEUIDynamicActionTable<T> extends DEUIDynamicTable<T>
{
    protected ActionT1<T> clickFunc;
    protected String clickText = "";

    public DEUIDynamicActionTable(String id, int columns)
    {
        this(id, columns, 0);
    }

    public DEUIDynamicActionTable(String id, int columns, int flags)
    {
        super(id, columns, flags);
    }

    public DEUIDynamicActionTable<T> setClick(ActionT1<T> clickFunc, String text)
    {
        this.clickFunc = clickFunc;
        this.clickText = text;
        return this;
    }

    public DEUIDynamicActionTable<T> setItems(Iterable<? extends T> items, FuncT1<String[], T> renderFunc)
    {
        super.setItems(items, renderFunc);
        return this;
    }

    public void onRender()
    {
        if (this.items != null && this.stringsFunc != null)
        {
            int j = 0;
            for (T item : items)
            {
                String[] labels = stringsFunc.invoke(item);
                ImGui.tableNextRow();
                int i = 0;
                for (i = 0; i < Math.min(labels.length, columns); i++)
                {
                    ImGui.tableSetColumnIndex(i);
                    ImGui.text(labels[i]);
                }
                ImGui.tableSetColumnIndex(columns - 1);
                if (ImGui.button(clickText + "##act" + j) && clickFunc != null) {
                    clickFunc.invoke(item);
                }
                j += 1;
            }
        }
    }
}
