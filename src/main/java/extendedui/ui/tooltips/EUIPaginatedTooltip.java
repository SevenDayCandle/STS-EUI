package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIHotkeys;
import extendedui.utilities.ColoredString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EUIPaginatedTooltip extends EUITooltip {
    protected int currentDesc;
    public ArrayList<String> descriptions = new ArrayList<>();

    public EUIPaginatedTooltip(String title, String... descriptions) {
        this(title, Arrays.asList(descriptions));
    }

    public EUIPaginatedTooltip(String title, List<String> descriptions) {
        super(title, descriptions.size() > 0 ? descriptions.get(0) : "");
        this.descriptions.addAll(descriptions);
        subText = new ColoredString(getCycleText(), Settings.PURPLE_COLOR);
    }

    public EUIPaginatedTooltip(EUITooltip other) {
        super(other);
    }

    public void cycleDescription() {
        if (descriptions.size() > 1) {
            setIndex((currentDesc + 1) % descriptions.size());
        }
    }

    public EUIPaginatedTooltip formatDescription(Object... items) {
        return formatDescription(currentDesc, items);
    }

    public EUIPaginatedTooltip formatDescription(int index, Object... items) {
        if (index < descriptions.size()) {
            String newDesc = EUIUtils.format(descriptions.get(index), items);
            descriptions.set(index, newDesc);
            updateText();
        }
        return this;
    }

    protected String getCycleText() {
        return EUIRM.strings.keyToCycle(EUIHotkeys.cycle.getKeyString()) + " (" + (currentDesc + 1) + "/" + descriptions.size() + ")";
    }

    public String getUpdatedDescription() {
        return currentDesc < descriptions.size() ? descriptions.get(currentDesc) : "";
    }

    @Override
    public float render(SpriteBatch sb, float x, float y, int index) {
        if (EUIHotkeys.cycle.isJustPressed()) {
            cycleDescription();
        }
        return super.render(sb, x, y, index);
    }

    public EUIPaginatedTooltip setDescription(String description) {
        return setDescription(0, description);
    }

    public EUIPaginatedTooltip setDescription(int index, String description) {
        if (this.descriptions.size() <= index) {
            this.descriptions.add(description);
        }
        else {
            this.descriptions.set(index, description);
        }
        updateText();

        return this;
    }

    public EUIPaginatedTooltip setDescriptions(String... descriptions) {
        return setDescriptions(Arrays.asList(descriptions));
    }

    public EUIPaginatedTooltip setDescriptions(List<String> descriptions) {
        this.descriptions.clear();
        this.descriptions.addAll(descriptions);
        currentDesc = 0;
        updateText();

        return this;
    }

    public EUIPaginatedTooltip setIndex(int index) {
        if (descriptions.size() > 0) {
            currentDesc = MathUtils.clamp(index, 0, descriptions.size() - 1);
            updateText();
        }

        return this;
    }

    public EUITooltip setText(String title, String... description) {
        return setText(title, Arrays.asList(description));
    }

    public EUITooltip setText(String title, List<String> description) {
        if (title != null) {
            setTitle(title);
        }
        if (description != null && description.size() > 0) {
            setDescriptions(description);
        }

        return this;
    }

    protected void updateText() {
        description = getUpdatedDescription();
        subText.setText(getCycleText());
        invalidateHeight();
    }
}
