package extendedui.configuration;

import com.badlogic.gdx.Input;
import com.megacrit.cardcrawl.helpers.input.InputAction;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;

import java.util.HashMap;

public class EUIHotkeys {

    private static final String KEYMAP_CYCLE = EUIConfiguration.getFullKey("Cycle");
    private static final String KEYMAP_OPEN_CARD_POOL = EUIConfiguration.getFullKey("OpenCardPool");
    private static final String KEYMAP_OPEN_POTION_POOL = EUIConfiguration.getFullKey("OpenPotionPool");
    private static final String KEYMAP_OPEN_RELIC_POOL = EUIConfiguration.getFullKey("OpenRelicPool");
    private static final String KEYMAP_TOGGLE_FILTERS = EUIConfiguration.getFullKey("ToggleFilters");
    public static InputAction cycle;
    public static InputAction openCardPool;
    public static InputAction openPotionPool;
    public static InputAction openRelicPool;
    public static InputAction toggleFilters;

    public static void load() {
        cycle = new InputAction(InputActionSet.prefs.getInteger(KEYMAP_CYCLE, Input.Keys.CONTROL_LEFT));
        openCardPool = new InputAction(InputActionSet.prefs.getInteger(KEYMAP_OPEN_CARD_POOL, Input.Keys.P));
        openPotionPool = new InputAction(InputActionSet.prefs.getInteger(KEYMAP_OPEN_POTION_POOL, Input.Keys.LEFT_BRACKET));
        openRelicPool = new InputAction(InputActionSet.prefs.getInteger(KEYMAP_OPEN_RELIC_POOL, Input.Keys.O));
        toggleFilters = new InputAction(InputActionSet.prefs.getInteger(KEYMAP_TOGGLE_FILTERS, Input.Keys.N));
    }

    public static void resetToDefaults() {
        cycle.remap(Input.Keys.CONTROL_LEFT);
        openCardPool.remap(Input.Keys.P);
        openPotionPool.remap(Input.Keys.LEFT_BRACKET);
        openRelicPool.remap(Input.Keys.O);
        toggleFilters.remap(Input.Keys.N);
    }

    public static void save() {
        InputActionSet.prefs.putInteger(KEYMAP_CYCLE, cycle.getKey());
        InputActionSet.prefs.putInteger(KEYMAP_OPEN_CARD_POOL, openCardPool.getKey());
        InputActionSet.prefs.putInteger(KEYMAP_OPEN_POTION_POOL, openPotionPool.getKey());
        InputActionSet.prefs.putInteger(KEYMAP_OPEN_RELIC_POOL, openRelicPool.getKey());
        InputActionSet.prefs.putInteger(KEYMAP_TOGGLE_FILTERS, toggleFilters.getKey());
    }
}