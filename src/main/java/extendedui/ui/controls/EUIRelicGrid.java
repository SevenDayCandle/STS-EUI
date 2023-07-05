package extendedui.ui.controls;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
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
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);

        if (hovered != null) {
            hovered.relic.renderTip(sb);
        }
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

    @Override
    public void updateItemPosition(RelicInfo relic, float x, float y) {
        relic.relic.targetX = x;
        relic.relic.targetY = y;
        relic.relic.currentX = EUIUtils.lerpSnap(relic.relic.currentX, relic.relic.targetX, LERP_SPEED);
        relic.relic.currentY = EUIUtils.lerpSnap(relic.relic.currentY, relic.relic.targetY, LERP_SPEED);
    }

    @Override
    public Hitbox getHitbox(RelicInfo item) {
        return item.relic.hb;
    }

    @Override
    public void forceUpdateItemPosition(RelicInfo relic, float x, float y) {
        relic.relic.currentX = relic.relic.targetX = x;
        relic.relic.currentY = relic.relic.targetY = y;
        relic.relic.hb.update();
        relic.relic.hb.move(relic.relic.currentX, relic.relic.currentY);
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
    protected void renderItem(SpriteBatch sb, RelicInfo relic) {
        if (relic.faded) {
            EUIRenderHelpers.drawBlendedWithShader(sb, EUIRenderHelpers.BlendingMode.Multiply, EUIRenderHelpers.ShaderMode.Grayscale, s -> renderRelicImpl(s, relic));
        }
        else {
            renderRelicImpl(sb, relic);
        }
    }

    protected void renderRelicImpl(SpriteBatch sb, RelicInfo relic) {
        if (relic.locked) {
            switch (relic.relicColor) {
                case RED:
                    relic.relic.renderLock(sb, Settings.RED_RELIC_COLOR);
                    break;
                case GREEN:
                    relic.relic.renderLock(sb, Settings.GREEN_RELIC_COLOR);
                    break;
                case BLUE:
                    relic.relic.renderLock(sb, Settings.BLUE_RELIC_COLOR);
                    break;
                case PURPLE:
                    relic.relic.renderLock(sb, Settings.PURPLE_RELIC_COLOR);
                    break;
                default:
                    relic.relic.renderLock(sb, Settings.TWO_THIRDS_TRANSPARENT_BLACK_COLOR);
            }
        }
        else {
            switch (relic.relicColor) {
                case RED:
                    relic.relic.render(sb, false, Settings.RED_RELIC_COLOR);
                    break;
                case GREEN:
                    relic.relic.render(sb, false, Settings.GREEN_RELIC_COLOR);
                    break;
                case BLUE:
                    relic.relic.render(sb, false, Settings.BLUE_RELIC_COLOR);
                    break;
                case PURPLE:
                    relic.relic.render(sb, false, Settings.PURPLE_RELIC_COLOR);
                    break;
                default:
                    relic.relic.render(sb, false, Settings.TWO_THIRDS_TRANSPARENT_BLACK_COLOR);
            }
        }
    }

    public EUIRelicGrid remove(AbstractRelic relic) {
        group.group.removeIf(rInfo -> rInfo.relic == relic);

        return this;
    }
}
