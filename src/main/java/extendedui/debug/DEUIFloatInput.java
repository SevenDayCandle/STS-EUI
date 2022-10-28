package extendedui.debug;

import imgui.ImGui;
import imgui.type.ImFloat;
import imgui.type.ImInt;

public class DEUIFloatInput extends DEUIBaseT0
{
    protected ImFloat value;
    protected float step = 1;
    protected float stepFast = 100;
    protected float min = Float.MIN_VALUE;
    protected float max = Float.MAX_VALUE;

    public DEUIFloatInput(String id)
    {
        this(id, 0);
    }

    public DEUIFloatInput(String id, float defaultValue)
    {
        this(id, defaultValue, Float.MIN_VALUE, Float.MAX_VALUE, 1, 100);
    }

    public DEUIFloatInput(String id, float defaultValue, float min, float max)
    {
        this(id, defaultValue, min, max, 1, 100);
    }

    public DEUIFloatInput(String id, float defaultValue, float min, float max, float step, float stepFast)
    {
        super(id);
        value = new ImFloat(defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.stepFast = stepFast;
    }

    public float Get()
    {
        return value.get();
    }

    public void Render()
    {
        ImGui.inputFloat(ID, value, step, stepFast);
        float result = value.get();
        if (result < min) {
            value.set(min);
        }
        else if (result > max) {
            value.set(max);
        }
    }
}
