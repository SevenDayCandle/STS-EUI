package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.CardObject;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;

public class EUIDropdownRow<T> {
    public static final float LABEL_OFFSET = 50;
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
        this.checkbox = new EUIImage(ImageMaster.COLOR_TAB_BOX_UNTICKED, new RelativeHitbox(hb, 48f, 48f, 0f, -EUIDropdown.TOGGLE_OFFSET));
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
        else if (item instanceof TooltipProvider) {
            EUITooltip.queueTooltips(((TooltipProvider) item).getTips());
        }
        else if (item instanceof CardObject) {
            renderCard(((CardObject) item).getCard());
        }
        else if (item instanceof AbstractCard) {
            renderCard((AbstractCard) item);
        }
    }

    public String getText() {
        return this.label.text;
    }

    public String getTextForWidth() {
        return this.label.text;
    }

    public void move(float x, float y) {
        this.hb.translate(x, y);
        this.checkbox.hb.translate(x, y - EUIDropdown.TOGGLE_OFFSET);
        this.label.hb.translate(x + (dr.isMultiSelect ? LABEL_OFFSET : LABEL_OFFSET / 2), y);
    }

    private void renderCard(AbstractCard card) {
        card.current_x = card.target_x = card.hb.x = InputHelper.mX + EUIDropdown.PREVIEW_OFFSET_X;
        card.current_y = card.target_y = card.hb.y = InputHelper.mY;
        card.update();
        card.updateHoverLogic();
        card.drawScale = card.targetDrawScale = 0.75f;
        EUI.addPriorityPostRender(card::render);
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
        if (this.hb.hovered) {
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
