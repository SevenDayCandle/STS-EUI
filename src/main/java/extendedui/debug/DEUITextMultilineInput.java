package extendedui.debug;

import imgui.ImGui;
import imgui.type.ImString;

public class DEUITextMultilineInput extends DEUIBaseT0
{
    protected ImString value;
    protected int flags;
    protected int width;
    protected int height;

    public DEUITextMultilineInput(String id)
    {
        this(id, 0, 0, ImString.DEFAULT_LENGTH, 0);
    }

    public DEUITextMultilineInput(String id, int width, int height)
    {
        this(id, width, height, ImString.DEFAULT_LENGTH, 0);
    }

    public DEUITextMultilineInput(String id, int width, int height, int maxlen)
    {
        this(id, width, height, maxlen,0);
    }

    public DEUITextMultilineInput(String id, int width, int height, int maxlen, int flags)
    {
        super(id);
        value = new ImString(maxlen);
        this.width = width;
        this.height = height;
        this.flags = flags;
    }

    public String get()
    {
        return value.get();
    }

    public void render()
    {
        ImGui.inputTextMultiline(ID, value, width, height, flags);
    }
}
