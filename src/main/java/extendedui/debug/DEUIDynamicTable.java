package extendedui.debug;

import eatyourbeets.interfaces.delegates.ActionT1;
import eatyourbeets.interfaces.delegates.FuncT1;
import imgui.ImGui;

public class DEUIDynamicTable<T> extends DEUITableBase
{
    protected Iterable<? extends T> items;
    protected FuncT1<String[], T> stringsFunc;

    public DEUIDynamicTable(String id, int columns)
    {
        this(id, columns, 0);
    }

    public DEUIDynamicTable(String id, int columns, int flags)
    {
        super(id, columns, flags);
    }

    public DEUIDynamicTable<T> SetItems(Iterable<? extends T> items, FuncT1<String[], T> renderFunc)
    {
        this.items = items;
        this.stringsFunc = renderFunc;
        return this;
    }

    public void OnRender()
    {
        if (this.items != null && this.stringsFunc != null)
        {
            for (T item : items)
            {
                String[] labels = stringsFunc.Invoke(item);
                ImGui.tableNextRow();
                for (int i = 0; i < Math.min(labels.length, columns); i++)
                {
                    ImGui.tableSetColumnIndex(i);
                    ImGui.text(labels[i]);
                }
            }
        }
    }
}
