package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUI;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.CardObject;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.*;

public class EUIDropdownRow<T> {
    private static final float TOGGLE_OFFSET = Settings.scale * 5f;
    private static final float TOGGLE_SIZE = Settings.scale * 40;
    private static final float LABEL_OFFSET = Settings.scale * 44;
    private static EUIPreview preview;
    public final EUIDropdown<T> dr;
    public T item;
    public EUIHitbox hb;
    public EUIImage checkbox;
    public EUILabel label;
    public int index;
    public boolean isSelected;

    public EUIDropdownRow(EUIDropdown<T> dr, EUIHitbox hb, T item, int index) {
        this.dr = dr;
        this.hb = hb;
        this.item = item;
        this.index = index;
        this.checkbox = new EUIImage(ImageMaster.COLOR_TAB_BOX_UNTICKED, new RelativeHitbox(hb, TOGGLE_SIZE, TOGGLE_SIZE, 0f, -TOGGLE_OFFSET));
        this.label = new EUILabel(dr.font, new RelativeHitbox(hb, this.hb.width - (dr.isMultiSelect ? LABEL_OFFSET : LABEL_OFFSET / 2f), this.hb.height, dr.isMultiSelect ? LABEL_OFFSET : LABEL_OFFSET / 2, 0f))
                .setFont(dr.font, dr.fontScale)
                .setLabel(dr.labelFunction.invoke(item))
                .setAlignment(dr.isOptionSmartText ? 0.79f : 0.54f, 0f, dr.isOptionSmartText);
        if (dr.colorFunctionOption != null) {
            this.label.setColor(dr.colorFunctionOption.invoke(item));
        }
    }

    protected void addTooltip() {
        if (dr.tooltipFunction != null) {
            EUITooltip.queueTooltips(dr.tooltipFunction.invoke(item));
        }
        else if (item instanceof CardObject) {
            renderCard(((CardObject) item).getCard());
        }
        else if (item instanceof AbstractCard) {
            renderCard((AbstractCard) item);
        }
        else if (item instanceof AbstractRelic) {
            renderRelic((AbstractRelic) item);
        }
        else if (item instanceof AbstractPotion) {
            renderPotion((AbstractPotion) item);
        }
        else if (item instanceof AbstractBlight) {
            renderBlight((AbstractBlight) item);
        }
        else if (item instanceof TooltipProvider) {
            EUITooltip.queueTooltips(((TooltipProvider) item).getTips());
        }
    }

    public String getText() {
        return this.label.text;
    }

    public String getTextForWidth() {
        return this.label.text;
    }

    protected boolean isComponentHovered() {
        return this.hb.hovered;
    }

    public void move(float x, float y) {
        this.hb.translate(x, y);
        this.checkbox.hb.translate(x, y - TOGGLE_OFFSET);
        this.label.hb.translate(x + (dr.isMultiSelect ? LABEL_OFFSET : LABEL_OFFSET / 2), y);
    }

    private void renderBlight(AbstractBlight blight) {
        if (!(preview instanceof EUIBlightPreview)) {
            preview = new EUIBlightPreview(blight);
        }
        else {
            ((EUIBlightPreview) preview).preview = blight;
        }
        renderPreview(1f);
    }

    private void renderCard(AbstractCard card) {
        if (!(preview instanceof EUICardPreview)) {
            preview = new EUICardPreview(card);
        }
        else {
            ((EUICardPreview) preview).defaultPreview = card;
        }
        renderPreview(0.75f);
    }

    private void renderPotion(AbstractPotion potion) {
        if (!(preview instanceof EUIPotionPreview)) {
            preview = new EUIPotionPreview(potion);
        }
        else {
            ((EUIPotionPreview) preview).preview = potion;
        }
        renderPreview(1f);
    }

    private void renderPreview(float scale) {
        float x = hb.x + (hb.x < AbstractCard.IMG_WIDTH * 1.5f ? hb.width + EUIDropdown.PREVIEW_OFFSET_X : -EUIDropdown.PREVIEW_OFFSET_X);
        EUI.addPriorityPostRender(s -> preview.render(s, x, hb.y, scale, false));
    }

    private void renderRelic(AbstractRelic relic) {
        if (!(preview instanceof EUIRelicPreview)) {
            preview = new EUIRelicPreview(relic);
        }
        else {
            ((EUIRelicPreview) preview).preview = relic;
        }
        renderPreview(1f);
    }

    public void renderRow(SpriteBatch sb) {
        this.hb.render(sb);
        this.label.tryRender(sb);
        if (dr.isMultiSelect) {
            this.checkbox.tryRender(sb);
        }
    }

    public void setLabelColor(FuncT1<Color, T> colorFunction) {
        this.label.setColor(colorFunction.invoke(item));
    }

    public EUIDropdownRow<T> setLabelFont(BitmapFont font, float fontScale) {
        this.label.setFont(font, fontScale);
        return this;
    }

    public EUIDropdownRow<T> setLabelFunction(FuncT1<String, T> labelFunction, boolean isSmartText) {
        this.label.setSmartText(isSmartText);
        setLabelText(labelFunction);
        return this;
    }

    public void setLabelText(FuncT1<String, T> labelFunction) {
        this.label.setLabel(labelFunction.invoke(item));
    }

    protected boolean tryHover(boolean isSelected) {
        if (isComponentHovered()) {
            this.label.setColor(Settings.GREEN_TEXT_COLOR);
            if (InputHelper.justClickedLeft) {
                this.hb.clickStarted = true;
            }
            if (dr.showTooltipOnHover) {
                addTooltip();
            }
        }
        else if (isSelected) {
            this.label.setColor(Settings.GOLD_COLOR);
            this.checkbox.setTexture(ImageMaster.COLOR_TAB_BOX_TICKED);
        }
        else {
            this.label.setColor(dr.colorFunctionOption != null ? dr.colorFunctionOption.invoke(this.item) : Color.WHITE);
            this.checkbox.setTexture(ImageMaster.COLOR_TAB_BOX_UNTICKED);
        }

        if (((this.hb.clicked) || (this.hb.hovered && CInputActionSet.select.isJustPressed())) && EUI.tryClick(this.hb)) {
            this.hb.clicked = false;
            this.checkbox.setTexture(isSelected ? ImageMaster.COLOR_TAB_BOX_UNTICKED : ImageMaster.COLOR_TAB_BOX_TICKED);
            return true;
        }
        return false;
    }

    public boolean update(boolean isInRange, boolean isSelected) {
        this.hb.update();
        this.label.updateImpl();
        this.checkbox.updateImpl();
        this.isSelected = isSelected;
        if (!isInRange) {
            return false;
        }
        return tryHover(isSelected);
    }

    public EUIDropdownRow<T> updateAlignment() {
        this.label.setAlignment(dr.isOptionSmartText ? 0.79f : 0.54f, 0f, dr.isOptionSmartText);
        this.label.setHitbox(new RelativeHitbox(hb, this.hb.width - (dr.isMultiSelect ? LABEL_OFFSET : LABEL_OFFSET / 2f), this.hb.height, dr.isMultiSelect ? LABEL_OFFSET : LABEL_OFFSET / 2, 0f));
        return this;
    }
}
