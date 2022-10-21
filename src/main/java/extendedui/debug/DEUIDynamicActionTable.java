package extendedui.debug;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import eatyourbeets.interfaces.delegates.ActionT1;
import eatyourbeets.interfaces.delegates.FuncT1;
import imgui.ImGui;

public class DEUIDynamicActionTable<K, T extends Iterable<K>> extends DEUIDynamicTable<K, T>
{
    protected ActionT1<K> clickFunc;
    protected String clickText = "";

    public DEUIDynamicActionTable(String id)
    {
        this(id, 0, 0);
    }

    public DEUIDynamicActionTable(String id, int columns, int flags)
    {
        super(id, columns, flags);
    }

    public DEUIDynamicActionTable<K, T> SetClick(ActionT1<K> clickFunc, String text)
    {
        this.clickFunc = clickFunc;
        this.clickText = text;
        return this;
    }

    public DEUIDynamicActionTable<K, T> SetItems(T items, FuncT1<String[], K> renderFunc)
    {
        super.SetItems(items, renderFunc);
        return this;
    }

    public void OnRender()
    {
        if (this.items != null && this.stringsFunc != null)
        {
            int j = 0;
            for (K item : items)
            {
                String[] labels = stringsFunc.Invoke(item);
                ImGui.tableNextRow();
                int i = 0;
                for (i = 0; i < labels.length; i++)
                {
                    ImGui.tableSetColumnIndex(i);
                    ImGui.text(labels[i]);
                }
                ImGui.tableSetColumnIndex(i);
                if (ImGui.button(clickText + "##act" + j) && clickFunc != null) {
                    clickFunc.Invoke(item);
                }
                j += 1;
            }
        }
    }
}
