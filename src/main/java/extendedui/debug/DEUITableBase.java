package extendedui.debug;

import eatyourbeets.interfaces.delegates.ActionT0;
import imgui.ImGui;

public abstract class DEUITableBase extends DEUIBaseT0
{
    protected int columns;
    protected int flags;
    protected ActionT0 columnAction;


    public DEUITableBase(String id)
    {
        this(id, 0, 0);
    }

    public DEUITableBase(String id, int columns, int flags)
    {
        super(id);
        this.columns = columns;
        this.flags = flags;
    }

    public DEUITableBase SetColumnAction(ActionT0 columnAction)
    {
        this.columnAction = columnAction;
        return this;
    }

    public void Render()
    {
        if (ImGui.beginTable(ID, columns, flags))
        {
            if (this.columnAction != null)
            {
                columnAction.Invoke();
            }
            OnRender();
            ImGui.endTable();
        }
    }

    public abstract void OnRender();
}
