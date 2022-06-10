package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.ui.GUI_Hoverable;
import extendedui.ui.controls.GUI_Button;
import extendedui.ui.controls.GUI_Label;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;

import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMX;
import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMY;
import static extendedui.EUIRenderHelpers.CARD_ENERGY_IMG_WIDTH;

public class CardKeywordButton extends GUI_Hoverable
{
    public static final float ICON_SIZE = Scale(40);
    private static final Color ACTIVE_COLOR = new Color(0.76f, 0.76f, 0.76f, 1f);
    private static final Color NEGATE_COLOR = new Color(0.62f, 0.32f, 0.28f, 1f);
    private static final Color PANEL_COLOR = new Color(0.3f, 0.3f, 0.3f, 1f);
    private ActionT1<CardKeywordButton> onClick;

    public final EUITooltip Tooltip;
    public final float baseCountOffset = -0.17f;
    public final float baseImageOffsetX = -0.27f;
    public final float baseImageOffsetY = -0.2f;
    public final float baseTextOffsetX = -0.10f;
    public final float baseTextOffsetY = 0f;
    public int CardCount = -1;

    public GUI_Button background_button;
    public GUI_Label title_text;
    public GUI_Label count_text;

    protected float offX;
    protected float offY;

    public CardKeywordButton(AdvancedHitbox hb, EUITooltip tooltip)
    {
        super(hb);

        Tooltip = tooltip;

        background_button = new GUI_Button(EUIRM.Images.Panel_Rounded_Half_H.Texture(), new RelativeHitbox(hb, 1, 1, 0.5f, 0).SetIsPopupCompatible(true))
                .SetClickDelay(0.01f)
        .SetColor(CardKeywordFilters.CurrentFilters.contains(Tooltip) ? ACTIVE_COLOR
                : CardKeywordFilters.CurrentNegateFilters.contains(Tooltip) ? NEGATE_COLOR : PANEL_COLOR)
        .SetText("")
                .SetOnClick(button -> {
                    if (CardKeywordFilters.CurrentFilters.contains(Tooltip))
                    {
                        CardKeywordFilters.CurrentFilters.remove(Tooltip);
                        background_button.SetColor(PANEL_COLOR);
                        title_text.SetColor(Color.WHITE);
                    }
                    else
                    {
                        //CardKeywordFilters.CurrentNegateFilters.remove(Tooltip);
                        CardKeywordFilters.CurrentFilters.add(Tooltip);
                        background_button.SetColor(ACTIVE_COLOR);
                        title_text.SetColor(Color.DARK_GRAY);
                    }

                    if (this.onClick != null) {
                        this.onClick.Invoke(this);
                    }
                })
                .SetOnRightClick(button -> {
                    if (CardKeywordFilters.CurrentNegateFilters.contains(Tooltip))
                    {
                        CardKeywordFilters.CurrentNegateFilters.remove(Tooltip);
                        background_button.SetColor(PANEL_COLOR);
                        title_text.SetColor(Color.WHITE);
                    }
                    else
                    {
                        //CardKeywordFilters.CurrentFilters.remove(Tooltip);
                        CardKeywordFilters.CurrentNegateFilters.add(Tooltip);
                        background_button.SetColor(NEGATE_COLOR);
                        title_text.SetColor(Color.WHITE);
                    }

                    if (this.onClick != null) {
                        this.onClick.Invoke(this);
                    }
                });

        title_text = new GUI_Label(EUIFontHelper.CardTooltipFont,
        new RelativeHitbox(hb, 0.5f, 1, baseTextOffsetX, baseTextOffsetY))
                .SetFont(EUIFontHelper.CardTooltipFont, 0.8f)
                .SetColor(CardKeywordFilters.CurrentFilters.contains(Tooltip) ? Color.DARK_GRAY : Color.WHITE)
        .SetAlignment(0.5f, 0.49f) // 0.1f
        .SetText(Tooltip.title);

        count_text = new GUI_Label(EUIFontHelper.CardDescriptionFont_Normal,
                new RelativeHitbox(hb, 0.28f, 1, baseCountOffset, 0f))
                .SetFont(EUIFontHelper.CardDescriptionFont_Normal, 0.8f)
                .SetAlignment(0.5f, 0.51f) // 0.1f
                .SetColor(Settings.GOLD_COLOR)
                .SetText(CardKeywordFilters.CurrentNegateFilters.contains(Tooltip) ? "X" : CardCount);
    }

    public CardKeywordButton SetIndex(int index)
    {
        offX = (index % CardKeywordFilters.ROW_SIZE) * 1.06f;
        offY = -(Math.floorDiv(index,CardKeywordFilters.ROW_SIZE)) * 0.85f;
        RelativeHitbox.SetPercentageOffset(background_button.hb, offX, offY);
        RelativeHitbox.SetPercentageOffset(title_text.hb, offX + baseTextOffsetX, offY + baseTextOffsetY);
        RelativeHitbox.SetPercentageOffset(count_text.hb, offX + baseCountOffset, offY);


        return this;
    }

    public CardKeywordButton SetCardCount(int count)
    {
        this.CardCount = count;
        boolean isNegate = CardKeywordFilters.CurrentNegateFilters.contains(Tooltip);
        boolean isContains = CardKeywordFilters.CurrentFilters.contains(Tooltip);
        count_text
                .SetText(isNegate ? "X" : count).SetColor(count > 0 ? Settings.GOLD_COLOR : Color.DARK_GRAY);
        title_text.SetColor((!isContains && count > 0) || isNegate ? Color.WHITE : Color.DARK_GRAY);
        background_button
                .SetColor(isContains ? ACTIVE_COLOR
                        : isNegate ? NEGATE_COLOR : PANEL_COLOR)
                .SetInteractable(count != 0 || isNegate);

        return this;
    }

    public CardKeywordButton SetOnClick(ActionT1<CardKeywordButton> onClick)
    {
        this.onClick = onClick;
        return this;
    }

    @Override
    public void Update()
    {
        background_button.Update();
        title_text.Update();
        count_text.Update();
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        background_button.Render(sb);

        if (Tooltip.icon != null) {
            if (CardCount != 0 || CardKeywordFilters.CurrentNegateFilters.contains(Tooltip)) {
                RenderTooltipImage(sb);
            }
            else {
                EUIRenderHelpers.DrawGrayscale(sb, this::RenderTooltipImage);
            }
        }


        title_text.Render(sb);
        if (CardCount >= 0) {
            count_text.Render(sb);
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


            EUITooltip.QueueTooltip(Tooltip, actualMX + 20 * Settings.scale, actualMY + 20 * Settings.scale);
        }
    }

    private void RenderTooltipImage(SpriteBatch sb) {
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
