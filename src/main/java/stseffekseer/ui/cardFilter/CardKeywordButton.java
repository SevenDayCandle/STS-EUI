package stseffekseer.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import stseffekseer.EUIRM;
import stseffekseer.EUIRenderHelpers;
import stseffekseer.interfaces.delegates.ActionT1;
import stseffekseer.ui.GUI_Hoverable;
import stseffekseer.ui.controls.GUI_Button;
import stseffekseer.ui.controls.GUI_Label;
import stseffekseer.ui.hitboxes.AdvancedHitbox;
import stseffekseer.ui.hitboxes.RelativeHitbox;
import stseffekseer.ui.tooltips.EUITooltip;
import stseffekseer.utilities.EUIFontHelper;

import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMX;
import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMY;

public class CardKeywordButton extends GUI_Hoverable
{
    public static final float ICON_SIZE = Scale(40);
    private static final Color ACTIVE_COLOR = new Color(0.76f, 0.76f, 0.76f, 1f);
    private static final Color PANEL_COLOR = new Color(0.3f, 0.3f, 0.3f, 1f);
    private ActionT1<CardKeywordButton> onClick;

    public final EUITooltip Tooltip;
    public final float baseCountOffset = -0.17f;
    public final float baseImageOffsetX = -0.27f;
    public final float baseImageOffsetY = 0.45f;
    public final float baseTextOffsetX = -0.10f;
    public final float baseTextOffsetY = 0f;
    public int CardCount = -1;

    public GUI_Button background_button;
    public GUI_Label tooltip_text;
    public GUI_Label title_text;
    public GUI_Label count_text;

    public CardKeywordButton(AdvancedHitbox hb, EUITooltip tooltip)
    {
        super(hb);

        Tooltip = tooltip;

        background_button = new GUI_Button(EUIRM.Images.Panel_Rounded_Half_H.Texture(), new RelativeHitbox(hb, 1, 1, 0.5f, 0).SetIsPopupCompatible(true))
        .SetColor(CardKeywordFilters.CurrentFilters.contains(Tooltip) ? ACTIVE_COLOR : PANEL_COLOR)
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
                        CardKeywordFilters.CurrentFilters.add(Tooltip);
                        background_button.SetColor(ACTIVE_COLOR);
                        title_text.SetColor(PANEL_COLOR);
                    }

                    if (this.onClick != null) {
                        this.onClick.Invoke(this);
                    }
                });

        tooltip_text = new GUI_Label(EUIFontHelper.CardTooltipFont,
                new RelativeHitbox(hb, 1.2f, 1, baseImageOffsetX, baseImageOffsetY))
                .SetAlignment(0.5f, 0.5f, true) // 0.1f
                .SetText(Tooltip.icon != null ? "[" + Tooltip.id + "]" : "");

        title_text = new GUI_Label(EUIFontHelper.CardTooltipFont,
        new RelativeHitbox(hb, 0.5f, 1, baseTextOffsetX, baseTextOffsetY))
                .SetFont(EUIFontHelper.CardTooltipFont, 0.8f)
                .SetColor(CardKeywordFilters.CurrentFilters.contains(Tooltip) ? PANEL_COLOR : Color.WHITE)
        .SetAlignment(0.5f, 0.49f) // 0.1f
        .SetText(Tooltip.title);

        count_text = new GUI_Label(EUIFontHelper.CardDescriptionFont_Normal,
                new RelativeHitbox(hb, 0.28f, 1, baseCountOffset, 0f))
                .SetFont(EUIFontHelper.CardDescriptionFont_Normal, 0.8f)
                .SetAlignment(0.5f, 0.51f) // 0.1f
                .SetColor(Settings.GOLD_COLOR)
                .SetText(CardCount);
    }

    public CardKeywordButton SetIndex(int index)
    {
        float x = (index % CardKeywordFilters.ROW_SIZE) * 1.06f;
        float y = -(Math.floorDiv(index,CardKeywordFilters.ROW_SIZE)) * 0.85f;
        RelativeHitbox.SetPercentageOffset(background_button.hb, x, y);
        RelativeHitbox.SetPercentageOffset(tooltip_text.hb, x + baseImageOffsetX, y + baseImageOffsetY);
        RelativeHitbox.SetPercentageOffset(title_text.hb, x + baseTextOffsetX, y + baseTextOffsetY);
        RelativeHitbox.SetPercentageOffset(count_text.hb, x + baseCountOffset, y);


        return this;
    }

    public CardKeywordButton SetCardCount(int count)
    {
        this.CardCount = count;
        count_text.SetText(count).SetColor(count > 0 ? Settings.GOLD_COLOR : Color.DARK_GRAY);
        title_text.SetColor(CardKeywordFilters.CurrentFilters.contains(Tooltip) ? PANEL_COLOR : count > 0 ? Color.WHITE : Color.DARK_GRAY);
        tooltip_text.SetColor(CardCount == 0 ? Color.DARK_GRAY : Color.WHITE);
        background_button.SetColor(CardKeywordFilters.CurrentFilters.contains(Tooltip) ? ACTIVE_COLOR : PANEL_COLOR).SetInteractable(count != 0);

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
        background_button.SetInteractable(CardCount != 0).Update();
        tooltip_text.SetColor(CardCount == 0 ? Color.DARK_GRAY : Color.WHITE).Update();
        title_text.Update();
        count_text.Update();
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        background_button.Render(sb);

        //if (Tooltip.icon != null) {
            //final float orbWidth = Tooltip.icon.getRegionWidth();
            //final float orbHeight = Tooltip.icon.getRegionHeight();
            //final float scaleX = 26.0F * Settings.scale / orbWidth;
            //final float scaleY = 26.0F * Settings.scale / orbHeight;
            //sb.setColor(Color.WHITE.cpy());
            //sb.draw(Tooltip.icon, background_button.hb.x + 30f * Settings.scale, background_button.hb.y, orbWidth / 2f, orbHeight / 2f, orbWidth, orbHeight, scaleX, scaleY, 0f);
        //}
        if (CardCount != 0) {
            tooltip_text.Render(sb);
        }
        else {
            EUIRenderHelpers.DrawGrayscale(sb, tooltip_text::Render);
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
}
