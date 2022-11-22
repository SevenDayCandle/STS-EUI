package extendedui.debug;

import eatyourbeets.interfaces.delegates.ActionT0;
import imgui.ImGui;

public abstract class DEUITableBase extends DEUIBaseT0
{
    protected int columns;
    protected int flags;
    protected float outerSizeX;
    protected float outerSizeY;
    protected float innerWidth;

    protected ActionT0 columnAction;


    public DEUITableBase(String id, int columns)
    {
        this(id, columns, 0);
    }

    public DEUITableBase(String id, int columns, int flags)
    {
        super(id);
        this.columns = columns;
        this.flags = flags;
    }

    public DEUITableBase setSize(float outerSizeX, float outerSizeY, float innerWidth)
    {
        this.outerSizeX = outerSizeX;
        this.outerSizeY = outerSizeY;
        this.innerWidth = innerWidth;
        return this;
    }

    public DEUITableBase setColumnAction(ActionT0 columnAction)
    {
        this.columnAction = columnAction;
        return this;
    }

    public void render()
    {
        if (ImGui.beginTable(ID, columns, flags, outerSizeX, outerSizeY, innerWidth))
        {
            if (this.columnAction != null)
            {
                columnAction.invoke();
            }
            onRender();
            ImGui.endTable();
        }
    }

    public abstract void onRender();
}
