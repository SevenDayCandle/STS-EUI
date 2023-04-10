package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.controller.CInputHelper;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.*;
import extendedui.text.EUISmartText;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class EUIDropdown<T> extends EUIHoverable
{
    protected static final int DEFAULT_MAX_ROWS = 15;
    protected static final float ROW_WIDTH_MULT = 3.4028235E38F;
    protected static final float ARROW_ICON_W = 30.0F * Settings.scale;
    protected static final float ARROW_ICON_H = 30.0F * Settings.scale;
    protected static final float BORDER_SIZE = Settings.scale * 10.0F;
    protected static final float BOX_EDGE_H = 32.0F * Settings.scale;
    protected static final float BOX_BODY_H = 64.0F * Settings.scale;
    protected static final float ICON_WIDTH = 64.0F * Settings.scale;
    protected static final float SCROLLBAR_WIDTH = 24.0F * Settings.scale;
    protected static final float SCROLLBAR_PADDING = 8.0F * Settings.scale;
    protected static final float TOGGLE_OFFSET = 5f;
    protected static final float PREVIEW_OFFSET_X = AbstractCard.IMG_WIDTH * 0.6f;

    protected ActionT1<Boolean> onOpenOrClose;
    protected ActionT1<List<T>> onChange;
    protected ActionT1<List<T>> onClear;
    protected BitmapFont font;
    protected FuncT1<Color, List<T>> colorFunctionButton;
    protected FuncT3<Float, EUIDropdown<T>, BitmapFont, Float> rowHeightFunction;
    protected FuncT3<Float, EUIDropdown<T>, BitmapFont, Float> rowWidthFunction;
    protected FuncT4<EUIDropdownRow<T>, EUIDropdown<T>, EUIHitbox, T, Integer> rowFunction;
    protected FuncT2<String, List<T>, FuncT1<String, T>> labelFunctionButton;
    protected FuncT1<String, T> labelFunction;
    protected FuncT1<List<EUITooltip>, T> tooltipFunction;
    protected EUIHoverable headerRow;
    protected EUILabel header;
    protected EUIVerticalScrollBar scrollBar;
    protected boolean isOpen;
    protected boolean justOpened;
    protected boolean rowsHaveBeenPositioned;
    protected boolean shouldSnapCursorToSelectedIndex;
    protected boolean isOptionSmartText;
    protected boolean shouldPositionClearAtTop;
    protected boolean showClearForSingle;
    protected final EUIButton button;
    protected final EUIButton clearButton;
    protected final TreeSet<Integer> currentIndices = new TreeSet<>();
    protected float fontScale;
    protected float rowHeight;
    protected float rowWidth;
    protected float topY;
    protected float bottomY;
    protected int maxRows;
    protected int topVisibleRowIndex;
    public boolean canAutosizeButton;
    public boolean canAutosizeRows = true;
    public boolean isMultiSelect;
    public boolean showTooltipOnHover = true;
    public ArrayList<EUIDropdownRow<T>> rows = new ArrayList<>();

    public EUIDropdown(EUIHitbox hb) {
        this(hb, Object::toString, new ArrayList<>(), EUIFontHelper.cardTooltipFont, DEFAULT_MAX_ROWS, false);
    }

    public EUIDropdown(EUIHitbox hb, FuncT1<String, T> labelFunction) {
        this(hb, labelFunction, new ArrayList<>(), EUIFontHelper.cardTooltipFont, DEFAULT_MAX_ROWS, false);
    }

    public EUIDropdown(EUIHitbox hb, FuncT1<String, T> labelFunction, ArrayList<T> options) {
        this(hb, labelFunction, options, EUIFontHelper.cardTooltipFont, DEFAULT_MAX_ROWS, false);
    }

    public EUIDropdown(EUIHitbox hb, FuncT1<String, T> labelFunction, ArrayList <T> options, BitmapFont font, int maxRows, boolean canAutosizeButton) {
        this(hb, labelFunction, options, EUIFontHelper.cardTooltipFont, 1, DEFAULT_MAX_ROWS, false);
    }

    public EUIDropdown(EUIHitbox hb, FuncT1<String, T> labelFunction, ArrayList <T> options, BitmapFont font, float fontScale, int maxRows, boolean canAutosizeButton) {
        super(hb);
        this.hb
                .setIsPopupCompatible(true)
                .setParentElement(this);
        this.font = font;
        this.fontScale = fontScale;
        this.maxRows = maxRows;
        this.labelFunction = labelFunction;
        this.canAutosizeButton = canAutosizeButton;

        this.rowHeight = calculateRowHeight();
        for (int i = 0; i < options.size(); i++) {
            rows.add(makeRow(options, i));
        }
        this.scrollBar = new EUIVerticalScrollBar(
                new OriginRelativeHitbox(hb, SCROLLBAR_WIDTH, rowHeight * this.visibleRowCount(), hb.width - SCROLLBAR_PADDING, 0)
                .setIsPopupCompatible(true)
                .setParentElement(this))
                .setOnScroll(this::onScroll);
        this.button = new EUIButton(EUIRM.images.rectangularButton.texture(), this.hb)
                .setColor(Color.GRAY)
                .setFont(font, fontScale)
                .setText(currentIndices.size() + " " + EUIRM.strings.uiItemsselected)
                .setOnClick(this::openOrCloseMenu);
        this.clearButton = new EUIButton(EUIRM.images.x.texture(), new OriginRelativeHitbox(hb, hb.height, hb.height, hb.width, 0)
                .setIsPopupCompatible(true)
                .setParentElement(this))
                .setOnClick(this::clear);
        this.header = new EUILabel(EUIFontHelper.cardtitlefontSmall, new OriginRelativeHitbox(hb, hb.width, hb.height, 0, hb.height)).setAlignment(0.5f,0.0f,false);
        this.header.setActive(false);
    }

    public EUIDropdown<T> setCanAutosize(boolean canAutosizeButton, boolean canAutosizeRows) {
        this.canAutosizeButton = canAutosizeButton;
        this.canAutosizeRows = canAutosizeRows;
        autosize();

        return this;
    }

    public EUIDropdown<T> setCanAutosizeButton(boolean value) {
        this.canAutosizeButton = value;
        autosize();

        return this;
    }

    public EUIDropdown<T> setHeader(BitmapFont font, float fontScale, Color textColor, String text) {
        return setHeader(font,fontScale,textColor,text,false);
    }

    public EUIDropdown<T> setHeader(BitmapFont font, float fontScale, Color textColor, String text, boolean smartText) {
        this.header.setFont(font, fontScale).setColor(textColor).setLabel(text).setSmartText(smartText).setActive(true);

        return this;
    }

    public EUIDropdown<T> setHeaderRow(EUIHoverable headerRow) {
        this.headerRow = headerRow;

        return this;
    }

    public EUIDropdown<T> setIsMultiSelect(boolean value) {
        this.isMultiSelect = value;
        for (EUIDropdownRow<T> row : rows) {
            row.updateAlignment();
        }

        return this;
    }

    public EUIDropdown<T> setClearButtonOptions(boolean showClearForSingle, boolean shouldPositionClearAtTop) {
        this.showClearForSingle = showClearForSingle;
        this.shouldPositionClearAtTop = shouldPositionClearAtTop;
        positionClearButton();

        return this;
    }

    public EUIDropdown<T> setShowClearForSingle(boolean value) {
        this.showClearForSingle = value;

        return this;
    }

    public EUIDropdown<T> setShowTooltips(boolean value) {
        this.showTooltipOnHover = value;

        return this;
    }

    public EUIDropdown<T> setShouldPositionClearAtTop(boolean value) {
        this.shouldPositionClearAtTop = value;
        positionClearButton();

        return this;
    }

    public EUIDropdown<T> addItems(T... options) {
        return addItems(Arrays.asList(options));
    }

    public EUIDropdown<T> addItems(List<T> options) {
        int initialSize = rows.size();
        for (int i = 0; i < options.size(); i++) {
            rows.add(makeRow(options, i, initialSize));
        }
        autosize();

        return this;
    }

    public EUIDropdown<T> setItems(T... options) {
        return setItems(Arrays.asList(options));
    }

    public EUIDropdown<T> setItems(List<T> options) {
        this.currentIndices.clear();
        this.rows.clear();
        for (int i = 0; i < options.size(); i++) {
            rows.add(makeRow(options, i));
        }
        autosize();

        return this;
    }

    public EUIDropdown<T> setItems(Collection<T> options) {
        return setItems(new ArrayList<>(options));
    }

    public EUIDropdown<T> setLabelFunctionForButton(FuncT2<String, List<T>, FuncT1<String, T>> labelFunctionButton, FuncT1<Color, List<T>> colorFunctionButton, boolean isSmartText) {
        this.button.setSmartText(isSmartText);
        this.labelFunctionButton = labelFunctionButton;
        this.colorFunctionButton = colorFunctionButton;
        if (labelFunctionButton != null) {
            this.button.setText(labelFunctionButton.invoke(getCurrentItems(), labelFunction));
        }
        if (colorFunctionButton != null) {
            this.button.setTextColor(colorFunctionButton.invoke(getCurrentItems()));
        }
        return this;
    }

    public EUIDropdown<T> setLabelFunctionForOption(FuncT1<String, T> labelFunction, boolean isSmartText) {
        this.labelFunction = labelFunction;
        this.isOptionSmartText = isSmartText;
        for (EUIDropdownRow<T> row : rows) {
            row.setLabelFunction(labelFunction, isSmartText);
        }
        return this;
    }

    public EUIDropdown<T> setMaxRows(int maxRows) {
        this.maxRows = maxRows;
        autosize();
        return this;
    }

    public EUIDropdown<T> setRowFunction(FuncT4<EUIDropdownRow<T>, EUIDropdown<T>, EUIHitbox, T, Integer> rowFunction) {
        this.rowFunction = rowFunction;
        return this;
    }

    public EUIDropdown<T> setRowHeightFunction(FuncT3<Float, EUIDropdown<T>, BitmapFont, Float> rowHeightFunction) {
        this.rowHeightFunction = rowHeightFunction;
        return this;
    }

    public EUIDropdown<T> setRowWidthFunction(FuncT3<Float, EUIDropdown<T>, BitmapFont, Float> rowWidthFunction) {
        this.rowWidthFunction = rowWidthFunction;
        return this;
    }

    public EUIDropdown<T> setTooltipFunction(FuncT1<List<EUITooltip>, T> tooltipFunction) {
        this.tooltipFunction = tooltipFunction;
        return this;
    }

    public EUIDropdown<T> setFontForButton(BitmapFont font, float fontScale)
    {
        button.setFont(font, fontScale);
        autosize();

        return this;
    }

    public EUIDropdown<T> setFontForRows(BitmapFont font, float fontScale)
    {
        this.font = font;
        this.fontScale = fontScale;

        for (EUIDropdownRow<T> row : rows) {
            row.label.setFont(font, fontScale);
        }
        autosize();

        return this;
    }

    public EUIDropdown<T> setOffset(float x, float y) {
        this.hb.setOffset(x, y);
        positionClearButton();
        return this;
    }

    public EUIDropdown<T> setOffsetX(float x) {
        this.hb.setOffsetX(x);
        positionClearButton();
        return this;
    }

    public EUIDropdown<T> setOffsetY(float y) {
        this.hb.setOffsetY(y);
        positionClearButton();
        return this;
    }

    public EUIDropdown<T> setOnChange(ActionT1<List<T>> onChange) {
        this.onChange = onChange;
        return this;
    }

    public EUIDropdown<T> setOnClear(ActionT1<List<T>> onClear) {
        this.onClear = onClear;
        return this;
    }

    // If you're using this dropdown on a pop-up menu, you need to have this action set CardCrawlGame.isOpen or your pop-up menu won't work properly
    public EUIDropdown<T> setOnOpenOrClose(ActionT1<Boolean> onOpenOrClose) {
        this.onOpenOrClose = onOpenOrClose;

        return this;
    }

    public EUIDropdown<T> setPosition(float x, float y) {
        this.hb.translate(x, y);
        positionClearButton();
        updateRowPositions();
        return this;
    }

    public EUIDropdown<T> setSelectionIndices(int[] selection, boolean shouldInvoke) {
        this.currentIndices.clear();
        if (selection != null) {
            for (Integer i : selection) {
                if (i < rows.size() && i >= 0) {
                    currentIndices.add(i);
                }
            }
        }
        updateForSelection(shouldInvoke);
        return this;
    }

    public EUIDropdown<T> setSelectionIndices(Collection<Integer> selection, boolean shouldInvoke) {
        this.currentIndices.clear();
        if (selection != null) {
            for (Integer i : selection) {
                if (i < rows.size() && i >= 0) {
                    currentIndices.add(i);
                }
            }
        }
        updateForSelection(shouldInvoke);
        return this;
    }

    public EUIDropdown<T> setSelection(T selection, boolean shouldInvoke) {
        this.currentIndices.clear();
        if (selection != null) {
            for (int i = 0; i < rows.size(); i++) {
                if (selection.equals(rows.get(i).item)) {
                    currentIndices.add(i);
                    break;
                }
            }
        }
        updateForSelection(shouldInvoke);
        return this;
    }


    public EUIDropdown<T> setSelection(Collection<T> selection, boolean shouldInvoke) {
        this.currentIndices.clear();
        if (selection != null) {
            for (int i = 0; i < rows.size(); i++) {
                if (selection.contains(rows.get(i).item)) {
                    currentIndices.add(i);
                }
            }
        }
        updateForSelection(shouldInvoke);
        return this;
    }

    public <K> EUIDropdown<T> setSelection(Collection<K> selection, FuncT1<K, T> convertFunc, boolean shouldInvoke) {
        this.currentIndices.clear();
        if (selection != null) {
            for (int i = 0; i < rows.size(); i++) {
                if (selection.contains(convertFunc.invoke(rows.get(i).item))) {
                    currentIndices.add(i);
                }
            }
        }
        updateForSelection(shouldInvoke);
        return this;
    }

    public EUIDropdown<T> setTooltip(EUITooltip tip) {
        super.setTooltip(tip);
        this.header.setTooltip(tip);
        return this;
    }

    public EUIDropdown<T> setTooltip(String name, String desc) {
        super.setTooltip(name, desc);
        this.header.setTooltip(this.tooltip);
        return this;
    }

    public EUIDropdown<T> toggleSelection(T selection, boolean value, boolean shouldInvoke) {
        for (int i = 0; i < rows.size(); i++) {
            if (selection.equals(rows.get(i).item)) {
                if (value && !currentIndices.contains(i)) {
                    currentIndices.add(i);
                }
                else if (!value) {
                    currentIndices.remove(i);
                }

                break;
            }
        }
        updateForSelection(shouldInvoke);
        return this;
    }

    public EUIDropdownRow<T> makeRow(List<T> options, int index) {
        return makeRow(options, index, 0);
    }

    public EUIDropdownRow<T> makeRow(List<T> options, int index, int offset) {
        EUIHitbox rh = new RelativeHitbox(hb, hb.width, this.rowHeight, 0f, 0)
                .setIsPopupCompatible(true)
                .setParentElement(this);
        T rowItem = options.get(index);
        int ind = index + offset;
        return (rowFunction != null ? rowFunction.invoke(this, rh, rowItem, ind) : new EUIDropdownRow<T>(
                this,
                rh, rowItem, ind))
                .updateAlignment();
    }

    public int size()
    {
        return this.rows.size();
    }

    public void autosize() {

        this.rowWidth = rowWidthFunction != null ? rowWidthFunction.invoke(this, font, fontScale) : calculateRowWidth();
        this.rowHeight = rowHeightFunction != null ? rowHeightFunction.invoke(this, font, fontScale) : calculateRowHeight();
        if (canAutosizeButton) {
            hb.resize(rowWidth, hb.height);
            button.hb.resize(rowWidth, hb.height);
            this.header.hb.setOffset(0, hb.height);
            positionClearButton();
        }
        if (canAutosizeRows) {
            for (EUIDropdownRow<T> row : rows) {
                row.hb.resize(rowWidth, rowHeight);
                row.updateAlignment();
            }
        }
        else
        {
            for (EUIDropdownRow<T> row : rows) {
                row.updateAlignment();
                row.hb.update();
            }
        }
        this.scrollBar.hb.resize(SCROLLBAR_WIDTH, rowHeight * (this.visibleRowCount() - 1));
        this.scrollBar.hb.setOffsetX((canAutosizeRows ? rowWidth : hb.width) - SCROLLBAR_PADDING);
    }

    public boolean areAnyItemsHovered() {
        if (this.hb.hovered || (this.isMultiSelect && currentIndices.size() != 0 && this.clearButton.hb.hovered)) {
            return true;
        }
        for(int i = 0; i < this.visibleRowCount(); ++i) {
            if (this.rows.get(i + this.topVisibleRowIndex).hb.hovered) {
                return true;
            }
        }
        return false;
    }

    public void clear()
    {
        setSelectionIndices((int[]) null, true);
        if (onClear != null)
        {
            onClear.invoke(getAllItems());
        }
    }

    public float calculateRowWidth() {
        float w = 0;
        for (EUIDropdownRow<T> row : rows) {
            w = Math.max(w, EUISmartText.getSmartWidth(this.font, row.getTextForWidth(), ROW_WIDTH_MULT, ROW_WIDTH_MULT) + ICON_WIDTH);
        }
        return w;
    }

    public float calculateRowHeight() {
        float scaledHeight = font.getCapHeight() * fontScale;
        float extraSpace = Math.min(Math.max(scaledHeight, 15.0F) * Settings.scale, 15.0F);
        return scaledHeight + extraSpace;
    }

    public ArrayList<T> getAllItems() {
        return EUIUtils.map(this.rows, row -> row.item);
    }

    public ArrayList<T> getCurrentItems() {
        ArrayList<T> items = new ArrayList<>();
        for (Integer i : currentIndices) {
            items.add(this.rows.get(i).item);
        }
        return items;
    }

    public FuncT1<String, T> getOptionLabelFunction()
    {
        return labelFunction;
    }

    public int getCurrentIndex() {
        return currentIndices.isEmpty() || currentIndices.first() >= this.rows.size() ? 0 : currentIndices.first();
    }

    public float getRowHeight()
    {
        return rowHeight;
    }

    public void openOrCloseMenu() {
        if (this.isOpen) {
            EUI.setActiveElement(null);
            CardCrawlGame.isPopupOpen = false;
            this.isOpen = false;
        }
        else {
            EUI.setActiveElement(this);
            CardCrawlGame.isPopupOpen = true;
            this.isOpen = true;
            this.justOpened = true;
            this.updateNonMouseStartPosition();
        }

        if (this.onOpenOrClose != null) {
            this.onOpenOrClose.invoke(this.isOpen);
        }
    }

    protected void onScroll(float newPercent)
    {
        this.topVisibleRowIndex = (int) MathUtils.clamp(newPercent * (this.rows.size() - this.visibleRowCount()), 0, this.rows.size() - this.visibleRowCount());
        updateRowPositions();
    }

    public void refreshText()
    {
        if (labelFunction != null)
        {
            for (EUIDropdownRow<T> row : rows)
            {
                row.setLabelText(labelFunction);
            }
        }
        if (labelFunctionButton != null) {
            this.button.setText(labelFunctionButton.invoke(getCurrentItems(), labelFunction));
        }
        if (colorFunctionButton != null) {
            this.button.setTextColor(colorFunctionButton.invoke(getCurrentItems()));
        }
    }

    public void sortByLabel()
    {
        ArrayList<T> current = getCurrentItems();
        rows.sort((a, b) -> StringUtils.compare(a.label.text, b.label.text));
        setSelection(current, false);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        updateButtons();
        updateRows();
        justOpened = false;
    }

    protected void updateButtons()
    {
        this.button.updateImpl();
        this.header.tryUpdate();
        if ((this.isMultiSelect || this.showClearForSingle) && currentIndices.size() != 0) {
            this.clearButton.updateImpl();
        }
    }

    protected void updateRows()
    {
        if (this.rows.size() != 0 && this.isOpen) {
            updateRowPositions();
            if (EUI.isActiveElement(this))
            {
                boolean isHoveringOver = this.hb.hovered;
                this.updateNonMouseInput();

                if (this.isMultiSelect && this.clearButton.hb.hovered)
                {
                    isHoveringOver = true;
                }

                if (headerRow != null)
                {
                    headerRow.update();
                }

                for (int i = 0; i < rows.size(); ++i)
                {
                    if (this.rows.get(i).update(i >= topVisibleRowIndex && i < topVisibleRowIndex + visibleRowCount(), currentIndices.contains(i)))
                    {
                        this.setSelectedIndex(this.rows.get(i).index);
                        isHoveringOver = true;
                        CardCrawlGame.sound.play("UI_CLICK_2");
                        if (!this.isMultiSelect)
                        {
                            openOrCloseMenu();
                        }
                    }
                    else if (this.rows.get(i).hb.hovered)
                    {
                        isHoveringOver = true;
                    }
                }

                if (this.shouldShowSlider())
                {
                    this.scrollBar.tryUpdate();
                    isHoveringOver = isHoveringOver | this.scrollBar.hb.hovered;
                }

                if (InputHelper.scrolledDown)
                {
                    this.topVisibleRowIndex = Integer.min(this.topVisibleRowIndex + 1, this.rows.size() - this.visibleRowCount());
                    updateRowPositions();
                    this.scrollBar.scroll(this.scrollPercentForTopVisibleRowIndex(this.topVisibleRowIndex), false);
                }
                else if (InputHelper.scrolledUp)
                {
                    this.topVisibleRowIndex = Integer.max(0, this.topVisibleRowIndex - 1);
                    updateRowPositions();
                    this.scrollBar.scroll(this.scrollPercentForTopVisibleRowIndex(this.topVisibleRowIndex), false);
                }

                boolean shouldCloseMenu = (!justOpened && InputHelper.justClickedLeft && !isHoveringOver) || InputHelper.pressedEscape || CInputActionSet.cancel.isJustPressed();
                if (shouldCloseMenu)
                {
                    if (Settings.isControllerMode)
                    {
                        CInputActionSet.cancel.unpress();
                        CInputHelper.setCursor(this.hb);
                    }

                    openOrCloseMenu();
                }
            }
            else
            {
                for (int i = 0; i < rows.size(); ++i)
                {
                    this.rows.get(i).update(i >= topVisibleRowIndex && i < topVisibleRowIndex + visibleRowCount(), currentIndices.contains(i));
                }
            }
        }
    }

    protected boolean isUsingNonMouseControl() {
        return Settings.isControllerMode || InputActionSet.up.isJustPressed() || InputActionSet.down.isJustPressed();
    }

    protected void updateNonMouseStartPosition() {
        if (this.isUsingNonMouseControl()) {
            this.shouldSnapCursorToSelectedIndex = true;
        }
    }

    protected void updateRowPositions()
    {
        int rowCount = this.visibleRowCount();
        topY = this.yPositionForRowBelow(hb.y, -1);
        bottomY = this.yPositionForRowBelow(hb.y, rowCount);

        // If the rows would be below the screen, move them above the bar
        if (bottomY < 0) {
            float newOrigin = hb.y + topY - bottomY + hb.height;
            this.layoutRowsBelow(hb.x, newOrigin);
            topY = this.yPositionForRowBelow(newOrigin, -1);
            bottomY = this.yPositionForRowBelow(newOrigin, rowCount);
            this.scrollBar.hb.setOffsetY(this.hb.height * 2);
        }
        else {
            this.layoutRowsBelow(hb.x, hb.y);
            this.scrollBar.hb.setOffsetY(-this.visibleRowCount() * this.rowHeight - (headerRow != null ? headerRow.hb.height : 0));
        }
    }

    protected void updateNonMouseInput() {
        if (this.isUsingNonMouseControl()) {
            if (this.shouldSnapCursorToSelectedIndex && this.rowsHaveBeenPositioned) {
                CInputHelper.setCursor((this.rows.get(getCurrentIndex())).hb);
                this.shouldSnapCursorToSelectedIndex = false;
            } else {
                int hoveredIndex = -1;

                for(int i = this.topVisibleRowIndex; i < this.topVisibleRowIndex + this.visibleRowCount(); ++i) {
                    if (this.rows.get(i).hb.hovered) {
                        hoveredIndex = i;
                        break;
                    }
                }

                if (hoveredIndex >= 0) {
                    boolean didInputUp = EUIInputManager.didInputUp();
                    boolean didInputDown = EUIInputManager.didInputDown();
                    boolean isMoving = didInputUp || didInputDown;
                    if (isMoving) {
                        int targetHoverIndexOffset = didInputDown ? 1 : -1;
                        int targetHoverIndex = (hoveredIndex + targetHoverIndexOffset + this.rows.size()) % this.rows.size();
                        boolean isAboveTheTop = targetHoverIndex < this.topVisibleRowIndex;
                        boolean isBelowTheBottom = targetHoverIndex >= this.topVisibleRowIndex + this.visibleRowCount();
                        if (isAboveTheTop) {
                            if (didInputDown) {
                                CInputHelper.setCursor(this.rows.get(this.topVisibleRowIndex).hb);
                            }

                            this.topVisibleRowIndex = targetHoverIndex;
                        } else if (isBelowTheBottom) {
                            if (didInputUp) {
                                CInputHelper.setCursor(this.rows.get(this.topVisibleRowIndex + this.visibleRowCount() - 1).hb);
                                this.rows.get(targetHoverIndex).hb.hovered = true;
                            }

                            this.topVisibleRowIndex = targetHoverIndex - this.visibleRowCount() + 1;
                        } else {
                            CInputHelper.setCursor(this.rows.get(targetHoverIndex).hb);
                        }

                        if (this.shouldShowSlider()) {
                            this.scrollBar.scroll(this.scrollPercentForTopVisibleRowIndex(this.topVisibleRowIndex), false);
                        }

                    }
                }
            }
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {

        this.hb.render(sb);
        this.button.tryRender(sb);
        this.header.tryRender(sb);
        if ((this.isMultiSelect || this.showClearForSingle) && currentIndices.size() != 0) {
            this.clearButton.renderImpl(sb);
        }
        if (this.rows.size() > 0) {
            renderArrows(sb);
            if (this.isOpen)
            {
                EUI.addPostRender(this::renderRowContent);
            }
        }
    }

    protected void renderRowContent(SpriteBatch sb) {
        if (headerRow != null)
        {
            this.renderBorder(sb, hb.x, bottomY, hb.width, topY - bottomY + headerRow.hb.height);
            headerRow.renderImpl(sb);
        }
        else
        {
            this.renderBorder(sb, hb.x, bottomY, hb.width, topY - bottomY);
        }
        for(int i = 0; i < this.visibleRowCount(); ++i) {
            this.rows.get(i + this.topVisibleRowIndex).renderRow(sb);
        }
        if (this.shouldShowSlider()) {
            this.scrollBar.tryRender(sb);
        }
    }

    protected void renderArrows(SpriteBatch sb) {
        float arrowIconX = hb.x + hb.width - ARROW_ICON_W - Settings.scale * 10.0F;
        Texture dropdownArrowIcon = this.isOpen ? ImageMaster.OPTION_TOGGLE_ON : ImageMaster.FILTER_ARROW;
        sb.draw(dropdownArrowIcon, arrowIconX, hb.y + hb.height / 4, ARROW_ICON_W, ARROW_ICON_H);
    }

    protected void layoutRowsBelow(float originX, float originY) {
        for(int i = 0; i < this.visibleRowCount(); ++i) {
            if (this.topVisibleRowIndex + i < this.rows.size()) {
                this.rows.get(this.topVisibleRowIndex + i).move(originX, this.yPositionForRowBelow(originY, i + 1));
            }
        }
        this.rowsHaveBeenPositioned = true;
    }

    protected void renderBorder(SpriteBatch sb, float x, float bottom, float width, float height) {
        float BOX_W = width + 2.0F * BORDER_SIZE;
        float FRAME_X = x - BORDER_SIZE;
        sb.setColor(Color.WHITE);
        float bottomY = bottom - BORDER_SIZE;
        sb.draw(ImageMaster.KEYWORD_BOT, FRAME_X, bottomY, BOX_W, rowHeight);
        float middleHeight = height - 2.0F * rowHeight - BORDER_SIZE;
        sb.draw(ImageMaster.KEYWORD_BODY, FRAME_X, bottomY + rowHeight, BOX_W, middleHeight);
        sb.draw(ImageMaster.KEYWORD_TOP, FRAME_X, bottom + middleHeight + BORDER_SIZE, BOX_W, rowHeight);
    }

    protected void renderBorderFromTop(SpriteBatch sb, float x, float top, float width, float height) {
        float BORDER_TOP_Y = top - BOX_EDGE_H + BORDER_SIZE;
        float BOX_W = width + 2.0F * BORDER_SIZE;
        float FRAME_X = x - BORDER_SIZE;
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.KEYWORD_TOP, FRAME_X, BORDER_TOP_Y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, FRAME_X, BORDER_TOP_Y - height - BOX_EDGE_H, BOX_W, height + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, FRAME_X, BORDER_TOP_Y - height - BOX_BODY_H, BOX_W, BOX_EDGE_H);
    }

    public void setSelectedIndex(int i) {
        if (isMultiSelect) {
            if (currentIndices.contains(i)) {
                currentIndices.remove(i);
            }
            else {
                currentIndices.add(i);
            }
        }
        else if (!currentIndices.contains(i)) {
            currentIndices.clear();
            currentIndices.add(i);
        }

        updateForSelection(true);
    }

    public void updateForSelection(boolean shouldInvoke) {
        int temp = currentIndices.size() > 0 ? currentIndices.first() : 0;
        if (isMultiSelect) {
            this.button.text = labelFunctionButton != null ? labelFunctionButton.invoke(getCurrentItems(), labelFunction) : makeMultiSelectString();
        }
        else if (currentIndices.size() > 0) {
            this.topVisibleRowIndex = Math.min(temp, this.rows.size() - this.visibleRowCount());
            this.button.text = labelFunctionButton != null ? labelFunctionButton.invoke(getCurrentItems(), labelFunction) : rows.get(temp).label.text;
            if (colorFunctionButton != null) {
                this.button.setTextColor(colorFunctionButton.invoke(getCurrentItems()));
            }

            this.scrollBar.scroll(this.scrollPercentForTopVisibleRowIndex(this.topVisibleRowIndex), false);
        }
        if (shouldInvoke && onChange != null) {
            onChange.invoke(getCurrentItems());
        }
    }

    public float scrollPercentForTopVisibleRowIndex(int topIndex) {
        int maxRow = this.rows.size() - this.visibleRowCount();
        return (float)topIndex / (float)maxRow;
    }

    public String makeMultiSelectString() {
        return makeMultiSelectString(labelFunction);
    }

    public String makeMultiSelectString(FuncT1<String, T> optionFunc) {
        String prospective = StringUtils.join(EUIUtils.map(getCurrentItems(), optionFunc), ", ");
        float width = button.isSmartText ? EUISmartText.getSmartWidth(font, prospective) : FontHelper.getSmartWidth(font, prospective, Integer.MAX_VALUE, font.getLineHeight());
        return width > hb.width * 0.85f ? currentIndices.size() + " " + EUIRM.strings.uiItemsselected : prospective;
    }

    protected boolean shouldShowSlider() {
        return this.rows.size() > this.maxRows;
    }

    public int topVisibleRowIndexForScrollPercent(float percent) {
        int maxRow = this.rows.size() - this.visibleRowCount();
        return (int)((float)maxRow * percent);
    }

    protected int visibleRowCount() {
        return Math.min(this.rows.size(), this.maxRows);
    }

    protected float yPositionForRowBelow(float originY, int rowIndex) {
        float extraHeight = rowIndex > 0 ? BORDER_SIZE : 0.0F;
        if (headerRow != null)
        {
            extraHeight += headerRow.hb.height;
        }
        return originY - rowHeight * (float)rowIndex - extraHeight;
    }

    protected void positionClearButton() {
        if (shouldPositionClearAtTop) {
            this.clearButton.hb.setOffset(hb.width - clearButton.hb.width, hb.height);
        }
        else {
            this.clearButton.hb.setOffset(hb.width, 0);
        }
    }

    public EUIDropdown<T> makeCopy() {
        return makeCopy(new EUIHitbox(hb));
    }

    public EUIDropdown<T> makeCopy(EUIHitbox hb) {
        return new EUIDropdown<T>(hb, this.labelFunction, getAllItems(), this.font, this.maxRows, this.canAutosizeButton)
                .setHeader(this.font, this.fontScale, this.header.textColor, this.header.text, this.header.smartText)
                .setLabelFunctionForButton(this.labelFunctionButton, this.colorFunctionButton, this.button.isSmartText)
                .setLabelFunctionForOption(this.labelFunction, this.isOptionSmartText)
                .setCanAutosize(this.canAutosizeButton, this.canAutosizeRows)
                .setClearButtonOptions(this.showClearForSingle, this.shouldPositionClearAtTop)
                .setIsMultiSelect(this.isMultiSelect)
                .setOnChange(this.onChange)
                .setOnOpenOrClose(this.onOpenOrClose);
    }
}