package extendedui.ui;

import com.badlogic.gdx.graphics.Texture;
import extendedui.EUIRM;

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

    public String Path() {
        return this.path;
    }

    public Texture Texture(boolean refresh) {
        if (refresh || this.texture == null) {
            this.texture = EUIRM.GetTexture(this.path, this.mipmap, refresh);
            if (this.texture == null) {
                this.texture = EUIRM.GetLocalTexture(this.path, this.mipmap, refresh);
            }
        }

        return this.texture;
    }

    public Texture Texture() {
        return this.Texture(false);
    }
}
