package stseffekseer.configuration;

import com.badlogic.gdx.Input;
import com.megacrit.cardcrawl.helpers.input.InputAction;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;

import java.util.HashMap;

public class EUIHotkeys
{

    public static final HashMap<Integer, Integer> EQUIVALENT_KEYS = new HashMap<>();

    private static final String KEYMAP_CYCLE = EUIConfiguration.GetFullKey("Cycle");
    private static final String KEYMAP_TOGGLE_FILTERS = EUIConfiguration.GetFullKey("ToggleFilters");

    static {
        EQUIVALENT_KEYS.put(Input.Keys.ALT_LEFT, Input.Keys.ALT_RIGHT);
        EQUIVALENT_KEYS.put(Input.Keys.ALT_RIGHT, Input.Keys.ALT_LEFT);
        EQUIVALENT_KEYS.put(Input.Keys.CONTROL_LEFT, Input.Keys.CONTROL_RIGHT);
        EQUIVALENT_KEYS.put(Input.Keys.CONTROL_RIGHT, Input.Keys.CONTROL_LEFT);
        EQUIVALENT_KEYS.put(Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT);
        EQUIVALENT_KEYS.put(Input.Keys.SHIFT_RIGHT, Input.Keys.SHIFT_LEFT);
    }

    public static InputAction cycle;
    public static InputAction toggleFilters;

    public static void load()
    {
        cycle = new InputAction(InputActionSet.prefs.getInteger(KEYMAP_CYCLE, Input.Keys.CONTROL_LEFT));
        toggleFilters = new InputAction(InputActionSet.prefs.getInteger(KEYMAP_TOGGLE_FILTERS, Input.Keys.N));
    }

    public static void save()
    {
        InputActionSet.prefs.putInteger(KEYMAP_CYCLE, cycle.getKey());
        InputActionSet.prefs.putInteger(KEYMAP_TOGGLE_FILTERS, toggleFilters.getKey());
    }

    public static void resetToDefaults()
    {
        cycle.remap(Input.Keys.CONTROL_LEFT);
        toggleFilters.remap(Input.Keys.N);
    }
}