package extendedui.debug;

import eatyourbeets.interfaces.delegates.ActionT1;
import eatyourbeets.interfaces.delegates.FuncT1;
import imgui.ImGui;

public class DEUIDynamicTable<K, T extends Iterable<K>> extends DEUITableBase
{
    protected T items;
    protected FuncT1<String[], K> stringsFunc;

    public DEUIDynamicTable(String id)
    {
        this(id, 0, 0);
    }

    public DEUIDynamicTable(String id, int columns, int flags)
    {
        super(id, columns, flags);
    }

    public DEUIDynamicTable<K, T> SetItems(T items, FuncT1<String[], K> renderFunc)
    {
        this.items = items;
        this.stringsFunc = renderFunc;
        return this;
    }

    public void OnRender()
    {
        if (this.items != null && this.stringsFunc != null)
        {
            for (K item : items)
            {
                String[] labels = stringsFunc.Invoke(item);
                ImGui.tableNextRow();
                for (int i = 0; i < labels.length; i++)
                {
                    ImGui.tableSetColumnIndex(i);
                    ImGui.text(labels[i]);
                }
            }
        }
    }
}
