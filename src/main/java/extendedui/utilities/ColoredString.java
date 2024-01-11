package extendedui.utilities;

import com.badlogic.gdx.graphics.Color;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class ColoredString {
    public Color color;
    public String text;

    public ColoredString() {
        this("");
    }

    public ColoredString(Object text) {
        this(text, Color.WHITE);
    }

    public ColoredString(Object text, Color color) {
        this.text = String.valueOf(text);
        this.color = color;
    }

    public ColoredString setColor(Color color) {
        this.color = color;

        return this;
    }

    public ColoredString setText(Object text) {
        this.text = String.valueOf(text);

        return this;
    }

    public ColoredString setText(String text) {
        this.text = text;

        return this;
    }
}
