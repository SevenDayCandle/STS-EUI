package extendedui.ui;

import com.badlogic.gdx.graphics.Texture;
import extendedui.EUIRM;
import extendedui.EUIUtils;

public class TextureCache {
    private final String path;
    private final boolean mipmap;
    private Texture texture;

    public TextureCache(String path) {
        this(path, false);
    }

    public TextureCache(String path, boolean mipmap) {
        this.path = path;
        this.mipmap = mipmap;
    }

    public String path() {
        return this.path;
    }

    public Texture reload() {
        this.texture = EUIRM.getTexture(this.path, this.mipmap, false);
        if (this.texture == null) {
            EUIUtils.logError(EUIRM.class, "Texture does not exist: " + this.path());
        }
        return this.texture;
    }

    public Texture texture() {
        if (this.texture == null) {
            this.texture = EUIRM.getTexture(this.path, this.mipmap, false);
            if (this.texture == null) {
                EUIUtils.logError(EUIRM.class, "Texture does not exist: " + this.path());
            }
        }
        return this.texture;
    }
}
