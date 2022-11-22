package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;

import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMX;
import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMY;
import static extendedui.text.EUISmartText.CARD_ENERGY_IMG_WIDTH;

public class FilterKeywordButton extends EUIHoverable
{
    public static final float ICON_SIZE = scale(40);
    private static final Color ACTIVE_COLOR = new Color(0.76f, 0.76f, 0.76f, 1f);
    private static final Color NEGATE_COLOR = new Color(0.62f, 0.32f, 0.28f, 1f);
    private static final Color PANEL_COLOR = new Color(0.3f, 0.3f, 0.3f, 1f);
    private ActionT1<FilterKeywordButton> onClick;

    public final EUITooltip Tooltip;
    public final GenericFilters<?> Filters;
    public final float baseCountOffset = -0.17f;
    public final float baseImageOffsetX = -0.27f;
    public final float baseImageOffsetY = -0.2f;
    public final float baseTextOffsetX = -0.10f;
    public final float baseTextOffsetY = 0f;
    public int CardCount = -1;

    public EUIButton background_button;
    public EUILabel title_text;
    public EUILabel count_text;

    protected float offX;
    protected float offY;

    public FilterKeywordButton(GenericFilters<?> filters, EUITooltip tooltip)
    {
        super(filters.hb);

        Filters = filters;
        Tooltip = tooltip;

        background_button = new EUIButton(EUIRM.Images.Panel_Rounded_Half_H.texture(), new RelativeHitbox(hb, 1, 1, 0.5f, 0).setIsPopupCompatible(true))
                .setClickDelay(0.01f)
        .setColor(Filters.CurrentFilters.contains(Tooltip) ? ACTIVE_COLOR
                : Filters.CurrentNegateFilters.contains(Tooltip) ? NEGATE_COLOR : PANEL_COLOR)
                .setOnClick(button -> {
                    if (Filters.CurrentFilters.contains(Tooltip))
                    {
                        Filters.CurrentFilters.remove(Tooltip);
                        background_button.setColor(PANEL_COLOR);
                        title_text.setColor(Color.WHITE);
                    }
                    else
                    {
                        Filters.CurrentFilters.add(Tooltip);
                        background_button.setColor(ACTIVE_COLOR);
                        title_text.setColor(Color.DARK_GRAY);
                    }

                    if (this.onClick != null) {
                        this.onClick.invoke(this);
                    }
                })
                .setOnRightClick(button -> {
                    if (Filters.CurrentNegateFilters.contains(Tooltip))
                    {
                        Filters.CurrentNegateFilters.remove(Tooltip);
                        background_button.setColor(PANEL_COLOR);
                    }
                    else
                    {
                        //CardKeywordFilters.CurrentFilters.remove(Tooltip);
                        Filters.CurrentNegateFilters.add(Tooltip);
                        background_button.setColor(NEGATE_COLOR);
                    }
                    title_text.setColor(Color.WHITE);

                    if (this.onClick != null) {
                        this.onClick.invoke(this);
                    }
                });

        title_text = new EUILabel(EUIFontHelper.CardTooltipFont,
        new RelativeHitbox(hb, 0.5f, 1, baseTextOffsetX, baseTextOffsetY))
                .setFont(EUIFontHelper.CardTooltipFont, 0.8f)
                .setColor(Filters.CurrentFilters.contains(Tooltip) ? Color.DARK_GRAY : Color.WHITE)
        .setAlignment(0.5f, 0.49f) // 0.1f
        .setLabel(Tooltip.title);

        count_text = new EUILabel(EUIFontHelper.CardDescriptionFont_Normal,
                new RelativeHitbox(hb, 0.28f, 1, baseCountOffset, 0f))
                .setFont(EUIFontHelper.CardDescriptionFont_Normal, 0.8f)
                .setAlignment(0.5f, 0.51f) // 0.1f
                .setColor(Settings.GOLD_COLOR)
                .setLabel(Filters.CurrentNegateFilters.contains(Tooltip) ? "X" : CardCount);
    }

    public FilterKeywordButton setIndex(int index)
    {
        offX = (index % CardKeywordFilters.ROW_SIZE) * 1.06f;
        offY = -(Math.floorDiv(index,CardKeywordFilters.ROW_SIZE)) * 0.85f;
        RelativeHitbox.setPercentageOffset(background_button.hb, offX, offY);
        RelativeHitbox.setPercentageOffset(title_text.hb, offX + baseTextOffsetX, offY + baseTextOffsetY);
        RelativeHitbox.setPercentageOffset(count_text.hb, offX + baseCountOffset, offY);


        return this;
    }

    public FilterKeywordButton setCardCount(int count)
    {
        this.CardCount = count;
        boolean isNegate = Filters.CurrentNegateFilters.contains(Tooltip);
        boolean isContains = Filters.CurrentFilters.contains(Tooltip);
        count_text
                .setLabel(isNegate ? "X" : count).setColor(count > 0 ? Settings.GOLD_COLOR : Color.DARK_GRAY);
        title_text.setColor((!isContains && count > 0) || isNegate ? Color.WHITE : Color.DARK_GRAY);
        background_button
                .setColor(isContains ? ACTIVE_COLOR
                        : isNegate ? NEGATE_COLOR : PANEL_COLOR)
                .setInteractable(count != 0 || isNegate);

        return this;
    }

    public FilterKeywordButton setOnClick(ActionT1<FilterKeywordButton> onClick)
    {
        this.onClick = onClick;
        return this;
    }

    @Override
    public void updateImpl()
    {
        background_button.updateImpl();
        title_text.updateImpl();
        count_text.updateImpl();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        background_button.renderImpl(sb);

        if (Tooltip.icon != null) {
            if (CardCount != 0 || Filters.CurrentNegateFilters.contains(Tooltip)) {
                renderTooltipImage(sb);
            }
            else {
                EUIRenderHelpers.drawGrayscale(sb, this::renderTooltipImage);
            }
        }


        title_text.renderImpl(sb);
        if (CardCount >= 0) {
            count_text.renderImpl(sb);
        }
        if (background_button.hb.hovered) {
            float actualMX;
            float actualMY;
            if (CardCrawlGame.isPopupOpen) {
                actualMX = popupMX;
                actualMY = popupMY;
            }
            else {
                actualMX = InputHelper.mX;
                actualMY = InputHelper.mY;
            }


            EUITooltip.queueTooltip(Tooltip, actualMX + 20 * Settings.scale, actualMY + 20 * Settings.scale);
        }
    }

    private void renderTooltipImage(SpriteBatch sb) {
        Tooltip.renderTipEnergy(sb, Tooltip.icon,
                hb.x + (offX + baseImageOffsetX) * hb.width,
                hb.y + (offY + baseImageOffsetY) * hb.height,
                28 * Tooltip.iconMulti_W,
                28 * Tooltip.iconMulti_H,
                CARD_ENERGY_IMG_WIDTH / Tooltip.icon.getRegionWidth(),
                CARD_ENERGY_IMG_WIDTH / Tooltip.icon.getRegionHeight(),
                CardCount == 0 ? Color.DARK_GRAY : Color.WHITE);
    }
}
