package extendedui.debug;

import com.badlogic.gdx.math.MathUtils;
import imgui.ImGui;
import imgui.type.ImInt;

public class DEUIIntInput extends DEUIBaseT0 {
    protected ImInt value;
    protected int step = 1;
    protected int stepFast = 100;
    protected int min = Integer.MIN_VALUE;
    protected int max = Integer.MAX_VALUE;

    public DEUIIntInput(String id) {
        this(id, 0);
    }

    public DEUIIntInput(String id, int defaultValue) {
        this(id, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, 100);
    }

    public DEUIIntInput(String id, int defaultValue, int min, int max, int step, int stepFast) {
        super(id);
        value = new ImInt(defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.stepFast = stepFast;
    }

    public DEUIIntInput(String id, int defaultValue, int min, int max) {
        this(id, defaultValue, min, max, 1, 100);
    }

    public int get() {
        return value.get();
    }

    public void render() {
        ImGui.inputInt(ID, value, step, stepFast);
        int result = value.get();
        if (result < min) {
            value.set(min);
        }
        else if (result > max) {
            value.set(max);
        }
    }

    public void set(int value) {
        this.value.set(MathUtils.clamp(value, min, max));
    }
}
