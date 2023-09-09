package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.ui.hitboxes.EUIHitbox;

public class EUIBorderedImage extends EUIImage {
    private Texture borderB = EUIRM.images.panelBorderB.texture();
    private Texture borderL = EUIRM.images.panelBorderL.texture();
    private Texture borderR = EUIRM.images.panelBorderR.texture();
    private Texture borderT = EUIRM.images.panelBorderT.texture();
    private Texture cornerBL = EUIRM.images.panelCornerBL.texture();
    private Texture cornerBR = EUIRM.images.panelCornerBR.texture();
    private Texture cornerTL = EUIRM.images.panelCornerTL.texture();
    private Texture cornerTR = EUIRM.images.panelCornerTR.texture();
    public float borderScale = 1;

    public EUIBorderedImage(Texture texture) {
        super(texture);
    }

    public EUIBorderedImage(Texture texture, Color color) {
        super(texture, color);
    }

    public EUIBorderedImage(Texture texture, EUIHitbox hb, Color color) {
        super(texture, hb, color);
    }

    public EUIBorderedImage(Texture texture, EUIHitbox hb) {
        super(texture, hb);
    }

    public EUIBorderedImage(EUIImage other) {
        super(other);
    }

    protected void renderCenteredImpl(SpriteBatch sb, float x, float y, float width, float height, Color targetColor) {
        float texWidth = cornerTL.getWidth() * Settings.scale;
        float texHeight = cornerBR.getWidth() * Settings.scale;
        x += texWidth;
        y += texHeight;
        width = width - 2 * texWidth;
        height = height - 2 * texHeight;
        super.renderCenteredImpl(sb, x, y, width, height, targetColor);
        renderCorners(sb, x, y, width, height, texWidth, texHeight);
    }

    protected void renderCorners(SpriteBatch sb, float x, float y, float width, float height, float texWidth, float texHeight) {
        sb.draw(cornerTL, x - texWidth, y + height, 0, 0, texWidth, texHeight, scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, cornerTL.getWidth(), cornerTL.getHeight(), flipX, flipY);
        sb.draw(cornerTR, x + width, y + height, 0, 0, texWidth, texHeight, scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, cornerTR.getWidth(), cornerTR.getHeight(), flipX, flipY);
        sb.draw(cornerBL, x - texWidth, y - texHeight, 0, 0, texWidth, texHeight, scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, cornerBL.getWidth(), cornerBL.getHeight(), flipX, flipY);
        sb.draw(cornerBR, x + width, y - texHeight, 0, 0, texWidth, texHeight, scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, cornerBR.getWidth(), cornerBR.getHeight(), flipX, flipY);

        sb.draw(borderB, x, y - texHeight, 0, 0, width, texHeight, scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, borderB.getWidth(), borderB.getHeight(), flipX, flipY);
        sb.draw(borderL, x - texHeight, y, 0, 0, texWidth, height, scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, borderL.getWidth(), borderL.getHeight(), flipX, flipY);
        sb.draw(borderR, x + width, y, 0, 0, texWidth, height, scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, borderR.getWidth(), borderR.getHeight(), flipX, flipY);
        sb.draw(borderT, x, y + height, 0, 0, width, texHeight, scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, borderT.getWidth(), borderT.getHeight(), flipX, flipY);
    }

    protected void renderImpl(SpriteBatch sb, float x, float y, float width, float height, Color targetColor) {
        float texWidth = cornerTL.getWidth() * Settings.scale;
        float texHeight = cornerBR.getWidth() * Settings.scale;
        x += texWidth;
        y += texHeight;
        width = width - 2 * texWidth;
        height = height - 2 * texHeight;
        super.renderImpl(sb, x, y, width, height, targetColor);
        renderCorners(sb, x, y, width, height, texWidth, texHeight);
    }

    public EUIBorderedImage setBorder(Texture borderB, Texture borderL, Texture borderR, Texture borderT, Texture cornerBL, Texture cornerBR, Texture cornerTL, Texture cornerTR) {
        this.borderB = borderB;
        this.borderL = borderL;
        this.borderR = borderR;
        this.borderT = borderT;
        this.cornerBL = cornerBL;
        this.cornerBR = cornerBR;
        this.cornerTL = cornerTL;
        this.cornerTR = cornerTR;
        return this;
    }

    public EUIBorderedImage setBorderScale(float borderScale) {
        this.borderScale = borderScale;
        return this;
    }
}
