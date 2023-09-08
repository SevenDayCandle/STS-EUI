package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.utilities.RelicInfo;

public class EUIRelicGrid extends EUIItemGrid<RelicInfo> {

    public EUIRelicGrid() {
        this(0.5f, true);
    }

    public EUIRelicGrid(float horizontalAlignment, boolean autoShowScrollbar) {
        super(horizontalAlignment, autoShowScrollbar);
    }

    public EUIRelicGrid(float horizontalAlignment) {
        this(horizontalAlignment, true);
    }

    public static Color getOutlineColor(RelicInfo item) {
        switch (item.relicColor) {
            case RED:
                return Settings.RED_RELIC_COLOR;
            case GREEN:
                return Settings.GREEN_RELIC_COLOR;
            case BLUE:
                return Settings.BLUE_RELIC_COLOR;
            case PURPLE:
                return Settings.PURPLE_RELIC_COLOR;
        }
        return Settings.TWO_THIRDS_TRANSPARENT_BLACK_COLOR;
    }

    public EUIRelicGrid add(AbstractRelic relic) {
        group.add(new RelicInfo(relic));
        relic.scale = startingScale;

        return this;
    }

    public EUIRelicGrid add(RelicInfo relic) {
        super.add(relic);
        relic.relic.scale = startingScale;

        return this;
    }

    @Override
    public void forceUpdateItemPosition(RelicInfo relic, float x, float y) {
        relic.relic.currentX = relic.relic.targetX = x;
        relic.relic.currentY = relic.relic.targetY = y;
        relic.relic.hb.update();
        relic.relic.hb.move(relic.relic.currentX, relic.relic.currentY);
    }

    @Override
    public Hitbox getHitbox(RelicInfo item) {
        return item.relic.hb;
    }

    @Override
    protected float getScrollDistance(RelicInfo relic, int index) {
        float scrollDistance = 1f / getRowCount();
        if (relic.relic.targetY > drawTopY) {
            return -scrollDistance;
        }
        else if (relic.relic.targetY < 0) {
            return scrollDistance;
        }
        return 0;
    }

    public EUIRelicGrid remove(AbstractRelic relic) {
        group.group.removeIf(rInfo -> rInfo.relic == relic);

        return this;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);

        if (hovered != null) {
            hovered.relic.renderTip(sb);
        }
    }

    @Override
    protected void renderItem(SpriteBatch sb, RelicInfo relic) {
        if (relic.faded) {
            EUIRenderHelpers.drawWithShader(sb, EUIRenderHelpers.ShaderMode.Sepia, s -> renderRelicImpl(s, relic, Settings.TWO_THIRDS_TRANSPARENT_BLACK_COLOR));
            EUIRenderHelpers.drawBlendedWithShader(sb, EUIRenderHelpers.BlendingMode.Multiply, EUIRenderHelpers.ShaderMode.Grayscale, s -> renderRelicImpl(s, relic, Settings.TWO_THIRDS_TRANSPARENT_BLACK_COLOR));
            // We don't have to worry about offsetX/offsetY in the grid
            sb.setColor(Color.WHITE);
            sb.draw(EUIRM.images.xThin.texture(), relic.relic.hb.x + relic.relic.hb.width / 4, relic.relic.hb.y + relic.relic.hb.height / 4, 32, 32, 64, 64, relic.relic.scale, relic.relic.scale, 0, 0, 0, 100, 100, false, false);
        }
        else {
            renderRelicImpl(sb, relic, getOutlineColor(relic));
        }
    }

    protected void renderRelicImpl(SpriteBatch sb, RelicInfo relic, Color renderColor) {
        if (relic.locked) {
            relic.relic.renderLock(sb, renderColor);
        }
        else {
            relic.relic.render(sb, false, renderColor);
        }
    }

    @Override
    protected void updateHoverLogic(RelicInfo relic, int i) {
        relic.relic.hb.update();
        relic.relic.hb.move(relic.relic.currentX, relic.relic.currentY);

        if (relic.relic.hb.hovered) {
            hovered = relic;
            hoveredIndex = i;
            relic.relic.scale = MathHelper.scaleLerpSnap(relic.relic.scale, scale(hoveredScale));
        }
        else {
            relic.relic.scale = MathHelper.scaleLerpSnap(relic.relic.scale, scale(targetScale));
        }
    }

    @Override
    public void updateItemPosition(RelicInfo relic, float x, float y) {
        relic.relic.targetX = x;
        relic.relic.targetY = y;
        relic.relic.currentX = EUIRenderHelpers.lerpSnap(relic.relic.currentX, relic.relic.targetX, LERP_SPEED);
        relic.relic.currentY = EUIRenderHelpers.lerpSnap(relic.relic.currentY, relic.relic.targetY, LERP_SPEED);
    }
}
