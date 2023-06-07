package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;

import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMX;
import static com.megacrit.cardcrawl.core.CardCrawlGame.popupMY;
import static extendedui.text.EUISmartText.CARD_ENERGY_IMG_WIDTH;
import static extendedui.ui.tooltips.EUIKeywordTooltip.BASE_ICON_SIZE;

public class FilterKeywordButton extends EUIHoverable {
    public static final float ICON_SIZE = scale(40);
    private static final Color ACTIVE_COLOR = new Color(0.76f, 0.76f, 0.76f, 1f);
    private static final Color NEGATE_COLOR = new Color(0.62f, 0.32f, 0.28f, 1f);
    private static final Color PANEL_COLOR = new Color(0.3f, 0.3f, 0.3f, 1f);
    private static final float BASE_TITLE_OFFSET_X = -0.15f;
    private static final float BASE_OFFSET_Y = -0.8f;
    public static final float BASE_COUNT_OFFSET_X = -0.21f;
    public static final float BASE_IMAGE_OFFSET_X = -0.29f;
    public static final float BASE_IMAGE_OFFSET_Y = -0.16f;
    public final GenericFilters<?> filters;
    public int cardCount = -1;
    public EUIButton backgroundButton;
    public EUILabel titleText;
    public EUILabel countText;
    public EUIKeywordTooltip keywordTooltip;
    protected float offX;
    protected float offY;
    private ActionT1<FilterKeywordButton> onToggle;
    private ActionT1<FilterKeywordButton> onRightClick;

    public FilterKeywordButton(GenericFilters<?> filters, EUIKeywordTooltip tooltip) {
        super(filters.hb);

        this.filters = filters;
        this.tooltip = this.keywordTooltip = tooltip;

        backgroundButton = new EUIButton(EUIRM.images.panelRoundedHalfH.texture(), RelativeHitbox.fromPercentages(filters.hb, 1, 1, 0.5f, BASE_OFFSET_Y).setIsPopupCompatible(true))
                .setClickDelay(0.01f)
                .setColor(this.filters.currentFilters.contains(this.keywordTooltip) ? ACTIVE_COLOR
                        : this.filters.currentNegateFilters.contains(this.keywordTooltip) ? NEGATE_COLOR : PANEL_COLOR)
                .setOnClick(button -> {
                    doToggle();
                })
                .setOnRightClick(button -> {
                    if (this.onRightClick != null) {
                        this.onRightClick.invoke(this);
                    }
                });

        titleText = new EUILabel(EUIFontHelper.cardTooltipFont,
                RelativeHitbox.fromPercentages(hb, 0.5f, 1, BASE_TITLE_OFFSET_X, BASE_OFFSET_Y))
                .setFont(EUIFontHelper.cardTooltipFont, 0.8f)
                .setColor(this.filters.currentFilters.contains(this.keywordTooltip) ? Color.DARK_GRAY : Color.WHITE)
                .setAlignment(0.5f, 0.49f) // 0.1f
                .setLabel(this.tooltip.title);

        countText = new EUILabel(EUIFontHelper.cardDescriptionFontNormal,
                RelativeHitbox.fromPercentages(hb, 0.28f, 1, BASE_COUNT_OFFSET_X, 0f))
                .setFont(EUIFontHelper.cardDescriptionFontNormal, 0.8f)
                .setAlignment(0.5f, 0.51f) // 0.1f
                .setColor(Settings.GOLD_COLOR)
                .setLabel(this.filters.currentNegateFilters.contains(this.keywordTooltip) ? "X" : cardCount);
    }

    public void doToggle() {
        if (EUIInputManager.isHoldingPeek()) {
            reverseToggle();
        }
        else {
            normalToggle();
        }
    }

    public void reverseToggle() {
        this.filters.currentFilters.remove(this.keywordTooltip);
        if (this.filters.currentNegateFilters.contains(this.keywordTooltip)) {
            this.filters.currentNegateFilters.remove(this.keywordTooltip);
            backgroundButton.setColor(PANEL_COLOR);
        }
        else {
            this.filters.currentNegateFilters.add(this.keywordTooltip);
            backgroundButton.setColor(NEGATE_COLOR);
        }
        titleText.setColor(Color.WHITE);

        if (this.onToggle != null) {
            this.onToggle.invoke(this);
        }
    }

    public void normalToggle() {
        this.filters.currentNegateFilters.remove(this.keywordTooltip);
        if (this.filters.currentFilters.contains(this.keywordTooltip)) {
            this.filters.currentFilters.remove(this.keywordTooltip);
            backgroundButton.setColor(PANEL_COLOR);
            titleText.setColor(Color.WHITE);
        }
        else {
            this.filters.currentFilters.add(this.keywordTooltip);
            backgroundButton.setColor(ACTIVE_COLOR);
            titleText.setColor(Color.DARK_GRAY);
        }

        if (this.onToggle != null) {
            this.onToggle.invoke(this);
        }
    }

    // TODO
    public void afterToggleRight() {

    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        backgroundButton.renderImpl(sb);

        if (keywordTooltip.icon != null) {
            if (cardCount != 0 || filters.currentNegateFilters.contains(keywordTooltip)) {
                renderTooltipImage(sb);
            }
            else {
                EUIRenderHelpers.drawGrayscale(sb, this::renderTooltipImage);
            }
        }


        titleText.renderImpl(sb);
        if (cardCount >= 0) {
            countText.renderImpl(sb);
        }
        if (backgroundButton.hb.hovered) {
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


            EUITooltip.queueTooltip(tooltip, actualMX + 20 * Settings.scale, actualMY + 20 * Settings.scale);
        }
    }

    private void renderTooltipImage(SpriteBatch sb) {
        keywordTooltip.renderTipEnergy(sb, keywordTooltip.icon,
                hb.x + (offX + BASE_IMAGE_OFFSET_X) * hb.width,
                hb.y + (offY + BASE_IMAGE_OFFSET_Y) * hb.height,
                BASE_ICON_SIZE * keywordTooltip.iconmultiW,
                BASE_ICON_SIZE * keywordTooltip.iconmultiH,
                Settings.scale * 0.75f,
                Settings.scale * 0.75f,
                cardCount == 0 ? Color.DARK_GRAY : Color.WHITE);
    }

    public FilterKeywordButton setCardCount(int count) {
        this.cardCount = count;
        boolean isNegate = filters.currentNegateFilters.contains(keywordTooltip);
        boolean isContains = filters.currentFilters.contains(keywordTooltip);
        countText
                .setLabel(isNegate ? "X" : count).setColor(count > 0 ? Settings.GOLD_COLOR : Color.DARK_GRAY);
        titleText.setColor((!isContains && count > 0) || isNegate ? Color.WHITE : Color.DARK_GRAY);
        backgroundButton
                .setColor(isContains ? ACTIVE_COLOR
                        : isNegate ? NEGATE_COLOR : PANEL_COLOR)
                .setInteractable(count != 0 || isNegate);

        return this;
    }

    public FilterKeywordButton setIndex(int index) {
        offX = (index % CardKeywordFilters.ROW_SIZE) * 1.06f;
        offY = BASE_OFFSET_Y - (Math.floorDiv(index, CardKeywordFilters.ROW_SIZE)) * 0.85f;
        backgroundButton.hb.setOffset(filters.hb.width * offX, hb.height * offY);
        titleText.hb.setOffset(hb.width * (offX + BASE_TITLE_OFFSET_X), hb.height * offY);
        countText.hb.setOffset(hb.width * (offX + BASE_COUNT_OFFSET_X), hb.height * offY);

        return this;
    }

    public FilterKeywordButton setOnRightClick(ActionT1<FilterKeywordButton> onToggle) {
        this.onRightClick = onToggle;
        return this;
    }

    public FilterKeywordButton setOnToggle(ActionT1<FilterKeywordButton> onToggle) {
        this.onToggle = onToggle;
        return this;
    }

    @Override
    public void updateImpl() {
        backgroundButton.updateImpl();
        titleText.updateImpl();
        countText.updateImpl();
    }
}
