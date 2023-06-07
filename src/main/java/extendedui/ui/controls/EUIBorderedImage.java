package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.ColoredTexture;

public class EUIBorderedImage extends EUIImage {
    public Texture borderB = EUIRM.images.panelBorderB.texture();
    public Texture borderL = EUIRM.images.panelBorderL.texture();
    public Texture borderR = EUIRM.images.panelBorderR.texture();
    public Texture borderT = EUIRM.images.panelBorderT.texture();
    public Texture cornerBL = EUIRM.images.panelCornerBL.texture();
    public Texture cornerBR = EUIRM.images.panelCornerBR.texture();
    public Texture cornerTL = EUIRM.images.panelCornerTL.texture();
    public Texture cornerTR = EUIRM.images.panelCornerTR.texture();
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

    public EUIBorderedImage setBorder(Texture borderB, Texture borderL, Texture borderR, Texture borderT, Texture cornerBL, Texture cornerBR, Texture cornerTL, Texture cornerTR)
    {
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

    public EUIBorderedImage setBorderScale(float borderScale)
    {
        this.borderScale = borderScale;
        return this;
    }

    protected void renderCenteredImpl(SpriteBatch sb, float x, float y, float width, float height, Color targetColor) {
        x += cornerTL.getWidth();
        y += cornerBR.getHeight();
        width = width - cornerTL.getWidth() - cornerBR.getWidth();
        height = height - cornerTL.getHeight() - cornerBR.getHeight();
        super.renderCenteredImpl(sb, x, y, width, height, targetColor);
        renderCorners(sb, x, y, width, height);
    }

    protected void renderImpl(SpriteBatch sb, float x, float y, float width, float height, Color targetColor) {
        x += cornerTL.getWidth();
        y += cornerBR.getHeight();
        width = width - cornerTL.getWidth() - cornerBR.getWidth();
        height = height - cornerTL.getHeight() - cornerBR.getHeight();
        super.renderImpl(sb, x, y, width, height, targetColor);
        renderCorners(sb, x, y, width, height);
    }

    protected void renderCorners(SpriteBatch sb, float x, float y, float width, float height) {
        sb.draw(cornerTL, x - cornerTL.getWidth(), y + height, 0, 0, cornerTL.getWidth(), cornerTL.getHeight(), scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, cornerTL.getWidth(), cornerTL.getHeight(), flipX, flipY);
        sb.draw(cornerTR, x + width, y + height, 0, 0, cornerTR.getWidth(), cornerTR.getHeight(), scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, cornerTR.getWidth(), cornerTR.getHeight(), flipX, flipY);
        sb.draw(cornerBL, x - cornerBL.getWidth(), y - cornerBL.getHeight(), 0, 0, cornerBL.getWidth(), cornerBL.getHeight(), scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, cornerBL.getWidth(), cornerBL.getHeight(), flipX, flipY);
        sb.draw(cornerBR, x + width, y - cornerBR.getHeight(), 0, 0, cornerBR.getWidth(), cornerBR.getHeight(), scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, cornerBR.getWidth(), cornerBR.getHeight(), flipX, flipY);

        sb.draw(borderB, x, y - cornerBL.getHeight(), 0, 0, width, borderB.getHeight(), scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, borderB.getWidth(), borderB.getHeight(), flipX, flipY);
        sb.draw(borderL, x - cornerBL.getWidth(), y, 0, 0, borderL.getWidth(), height, scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, borderL.getWidth(), borderL.getHeight(), flipX, flipY);
        sb.draw(borderR, x + width, y, 0, 0, borderR.getWidth(), height, scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, borderR.getWidth(), borderR.getHeight(), flipX, flipY);
        sb.draw(borderT, x, y + height, 0, 0, width, borderT.getHeight(), scaleX * borderScale, scaleY * borderScale, rotation, 0, 0, borderT.getWidth(), borderT.getHeight(), flipX, flipY);
    }
}
