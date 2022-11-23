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
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.EUI;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.CardObject;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.text.EUISmartText;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.AdvancedHitbox;
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
    protected BitmapFont font;
    protected FuncT1<Color, List<T>> colorFunctionButton;
    protected FuncT2<String, List<T>, FuncT1<String, T>> labelFunctionButton;
    protected FuncT1<String, T> labelFunction;
    protected FuncT1<List<EUITooltip>, T> tooltipFunction;
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
    protected int maxRows;
    protected int topVisibleRowIndex;
    public boolean canAutosizeButton;
    public boolean canAutosizeRows = true;
    public boolean isMultiSelect;
    public boolean showTooltipOnHover = true;
    public boolean wrapOpenDirection;
    public ArrayList<DropdownRow<T>> rows = new ArrayList<>();

    public EUIDropdown(AdvancedHitbox hb) {
        this(hb, Object::toString, new ArrayList<>(), EUIFontHelper.cardTooltipFont, DEFAULT_MAX_ROWS, false);
    }

    public EUIDropdown(AdvancedHitbox hb, FuncT1<String, T> labelFunction) {
        this(hb, labelFunction, new ArrayList<>(), EUIFontHelper.cardTooltipFont, DEFAULT_MAX_ROWS, false);
    }

    public EUIDropdown(AdvancedHitbox hb, FuncT1<String, T> labelFunction, ArrayList<T> options) {
        this(hb, labelFunction, options, EUIFontHelper.cardTooltipFont, DEFAULT_MAX_ROWS, false);
    }

    public EUIDropdown(AdvancedHitbox hb, FuncT1<String, T> labelFunction, ArrayList <T> options, BitmapFont font, int maxRows, boolean canAutosizeButton) {
        this(hb, labelFunction, options, EUIFontHelper.cardTooltipFont, 1, DEFAULT_MAX_ROWS, false);
    }

    public EUIDropdown(AdvancedHitbox hb, FuncT1<String, T> labelFunction, ArrayList <T> options, BitmapFont font, float fontScale, int maxRows, boolean canAutosizeButton) {
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
                new AdvancedHitbox(hb.x + hb.width - SCROLLBAR_PADDING, hb.y + calculateScrollbarOffset(), SCROLLBAR_WIDTH,rowHeight * this.visibleRowCount())
                .setIsPopupCompatible(true)
                .setParentElement(this))
                .setOnScroll(this::onScroll);
        this.button = new EUIButton(EUIRM.images.rectangularButton.texture(), this.hb)
                .setColor(Color.GRAY)
                .setFont(font, fontScale)
                .setText(currentIndices.size() + " " + EUIRM.strings.uiItemsselected)
                .setOnClick(this::openOrCloseMenu);
        //noinspection SuspiciousNameCombination
        this.clearButton = new EUIButton(EUIRM.images.x.texture(), new AdvancedHitbox(hb.x + hb.width, hb.y, hb.height, hb.height)
                .setIsPopupCompatible(true)
                .setParentElement(this))
                .setOnClick(() -> {
                    setSelectionIndices(new int[] {}, true);});
        this.header = new EUILabel(EUIFontHelper.cardtitlefontSmall, new AdvancedHitbox(hb.x, hb.y + hb.height, hb.width, hb.height)).setAlignment(0.5f,0.0f,false);
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

    public EUIDropdown<T> setIsMultiSelect(boolean value) {
        this.isMultiSelect = value;
        for (DropdownRow<T> row : rows) {
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
        for (DropdownRow<T> row : rows) {
            row.setLabelFunction(labelFunction, isSmartText);
        }
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

        for (DropdownRow<T> row : rows) {
            row.label.setFont(font, fontScale);
        }
        autosize();

        return this;
    }

    public EUIDropdown<T> setOnChange(ActionT1<List<T>> onChange) {
        this.onChange = onChange;
        return this;
    }

    // If you're using this dropdown on a pop-up menu, you need to have this action set CardCrawlGame.isOpen or your pop-up menu won't work properly
    public EUIDropdown<T> setOnOpenOrClose(ActionT1<Boolean> onOpenOrClose) {
        this.onOpenOrClose = onOpenOrClose;

        return this;
    }

    public EUIDropdown<T> setPosition(float x, float y) {
        this.hb.translate(x, y);
        this.button.hb.translate(x, y);
        this.scrollBar.hb.translate(x + hb.width - SCROLLBAR_PADDING, y + calculateScrollbarOffset());
        this.header.hb.translate(x, y + hb.height);
        positionClearButton();
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

    public DropdownRow<T> makeRow(List<T> options, int index) {
        return makeRow(options, index, 0);
    }

    public DropdownRow<T> makeRow(List<T> options, int index, int offset) {
        return new DropdownRow<T>(
                this,
                new RelativeHitbox(hb, hb.width, this.rowHeight, 0f, 0, false)
                        .setIsPopupCompatible(true)
                        .setParentElement(this)
                , options.get(index), labelFunction, font, fontScale, index + offset)
                .setLabelFunction(labelFunction, isOptionSmartText)
                .updateAlignment();
    }

    public int size()
    {
        return this.rows.size();
    }

    public void autosize() {

        this.rowWidth = calculateRowWidth();
        this.rowHeight = calculateRowHeight();
        if (canAutosizeButton) {
            hb.resize(rowWidth, hb.height);
            button.hb.resize(rowWidth, hb.height);
            this.header.hb.translate(hb.x, hb.y + hb.height);
            positionClearButton();
        }
        if (canAutosizeRows) {
            for (DropdownRow<T> row : rows) {
                row.hb.resize(rowWidth, rowHeight);
                row.updateAlignment();
            }
        }
        this.scrollBar.hb.resize(SCROLLBAR_WIDTH, rowHeight * (this.visibleRowCount() - 1));
        this.scrollBar.hb.translate(hb.x + (canAutosizeRows ? rowWidth : hb.width) - SCROLLBAR_PADDING, hb.y + calculateScrollbarOffset());
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

    private float calculateRowWidth() {
        float w = 0;
        for (DropdownRow<T> row : rows) {
            w = Math.max(w, EUISmartText.getSmartWidth(this.font, row.getTextForWidth(), ROW_WIDTH_MULT, ROW_WIDTH_MULT) + ICON_WIDTH);
        }
        return w;
    }

    public float calculateRowHeight() {
        float scaledHeight = this.font.getCapHeight() * this.fontScale;
        float extraSpace = Math.min(Math.max(scaledHeight, 15.0F) * Settings.scale, 15.0F);
        return scaledHeight + extraSpace;
    }

    public float calculateScrollbarOffset() {
        float bottomY = this.yPositionForRowBelow(hb.y, this.visibleRowCount());
        return bottomY < 0 ? this.hb.height * 2 : -this.visibleRowCount() * this.rowHeight;
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

    public void openOrCloseMenu() {
        if (this.isOpen) {
            EUI.tryToggleActiveElement(this, false);
            CardCrawlGame.isPopupOpen = false;
            this.isOpen = false;
        }
        else {
            EUI.tryToggleActiveElement(this, true);
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
    }

    public void refreshText()
    {
        if (labelFunction != null)
        {
            for (DropdownRow<T> row : rows)
            {
                row.refreshText(labelFunction);
            }
        }
        if (labelFunctionButton != null) {
            this.button.setText(labelFunctionButton.invoke(getCurrentItems(), labelFunction));
        }
        if (colorFunctionButton != null) {
            this.button.setTextColor(colorFunctionButton.invoke(getCurrentItems()));
        }
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        this.button.updateImpl();
        this.header.tryUpdate();
        if ((this.isMultiSelect || this.showClearForSingle) && currentIndices.size() != 0) {
            this.clearButton.updateImpl();
        }
        if (this.rows.size() != 0 && this.isOpen) {
                boolean isHoveringOver = this.hb.hovered;
                this.updateNonMouseInput();

                if (this.isMultiSelect && EUI.tryHover(this.clearButton.hb)) {
                    isHoveringOver = true;
                }

                for(int i = 0; i < rows.size(); ++i) {
                    if (this.rows.get(i).update(i >= topVisibleRowIndex && i < topVisibleRowIndex + visibleRowCount(), currentIndices.contains(i))) {
                        this.setSelectedIndex(this.rows.get(i).index);
                        isHoveringOver = true;
                        CardCrawlGame.sound.play("UI_CLICK_2");
                        if (!this.isMultiSelect) {
                            openOrCloseMenu();
                        }
                    }
                    else if (EUI.tryHover(this.rows.get(i).hb)) {
                        isHoveringOver = true;
                    }
                }

                if (this.shouldShowSlider()) {
                    this.scrollBar.tryUpdate();
                    isHoveringOver = isHoveringOver | this.scrollBar.hb.hovered;
                }

                if (InputHelper.scrolledDown) {
                    this.topVisibleRowIndex = Integer.min(this.topVisibleRowIndex + 1, this.rows.size() - this.visibleRowCount());
                    this.scrollBar.scroll(this.scrollPercentForTopVisibleRowIndex(this.topVisibleRowIndex), false);
                } else if (InputHelper.scrolledUp) {
                    this.topVisibleRowIndex = Integer.max(0, this.topVisibleRowIndex - 1);
                    this.scrollBar.scroll(this.scrollPercentForTopVisibleRowIndex(this.topVisibleRowIndex), false);
                }

                boolean shouldCloseMenu = (!justOpened && InputHelper.justClickedLeft && !isHoveringOver) || InputHelper.pressedEscape || CInputActionSet.cancel.isJustPressed();
                if (shouldCloseMenu) {
                    if (Settings.isControllerMode) {
                        CInputActionSet.cancel.unpress();
                        CInputHelper.setCursor(this.hb);
                    }

                    openOrCloseMenu();
                }
        }
        justOpened = false;

    }

    private boolean isUsingNonMouseControl() {
        return Settings.isControllerMode || InputActionSet.up.isJustPressed() || InputActionSet.down.isJustPressed();
    }

    private void updateNonMouseStartPosition() {
        if (this.isUsingNonMouseControl()) {
            this.shouldSnapCursorToSelectedIndex = true;
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
            EUI.addPostRender(this::renderRowContent);
            renderArrows(sb);
        }
    }

    protected void renderRowContent(SpriteBatch sb) {
        int rowCount = this.isOpen ? this.visibleRowCount() : 0;
        float topY = this.yPositionForRowBelow(hb.y, -1);
        float bottomY = this.yPositionForRowBelow(hb.y, rowCount);

        // If the rows would render below the screen, render them above the bar instead
        if (bottomY < 0) {
            float newOrigin = hb.y + topY - bottomY + hb.height;
            this.layoutRowsBelow(hb.x, newOrigin);
            topY = this.yPositionForRowBelow(newOrigin, -1);
            bottomY = this.yPositionForRowBelow(newOrigin, rowCount);
        }
        else {
            this.layoutRowsBelow(hb.x, hb.y);
        }

        if (this.isOpen) {
            this.renderBorder(sb, hb.x, bottomY, hb.width, topY - bottomY);
        }

        if (this.isOpen) {
            for(int i = 0; i < this.visibleRowCount(); ++i) {
                this.rows.get(i + this.topVisibleRowIndex).renderRow(sb);
            }

            if (this.shouldShowSlider()) {
                this.scrollBar.tryRender(sb);
            }
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
        return originY - rowHeight * (float)rowIndex - extraHeight;
    }

    protected void positionClearButton() {
        if (shouldPositionClearAtTop) {
            this.clearButton.hb.translate(hb.x + hb.width - clearButton.hb.width, hb.y + hb.height);
        }
        else {
            this.clearButton.hb.translate(hb.x + hb.width, hb.y);
        }
    }

    public EUIDropdown<T> makeCopy() {
        return makeCopy(new AdvancedHitbox(hb));
    }

    public EUIDropdown<T> makeCopy(AdvancedHitbox hb) {
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

    protected static class DropdownRow<T> {
        protected static final float LABEL_OFFSET = 50;
        public final EUIDropdown<T> dr;
        public T item;
        public AdvancedHitbox hb;
        public EUIImage checkbox;
        public EUILabel label;
        public int index;
        public boolean isSelected;

        public DropdownRow(EUIDropdown<T> dr, AdvancedHitbox hb, T item, FuncT1<String, T> labelFunction, BitmapFont font, float fontScale, int index) {
            this.dr = dr;
            this.hb = new RelativeHitbox(hb, 1f, 1f, 0f, 0f).setIsPopupCompatible(true).setParentElement(dr);
            this.item = item;
            this.index = index;
            this.checkbox = new EUIImage(ImageMaster.COLOR_TAB_BOX_UNTICKED,  new RelativeHitbox(hb, 48f, 48f, 0f, -TOGGLE_OFFSET, false));
            this.label = new EUILabel(font, new RelativeHitbox(hb, this.hb.width - (dr.isMultiSelect ? LABEL_OFFSET : LABEL_OFFSET / 2f), this.hb.height, dr.isMultiSelect ? LABEL_OFFSET : LABEL_OFFSET / 2, 0f, false))
                    .setFont(font, fontScale)
                    .setLabel(labelFunction.invoke(item))
                    .setAlignment(0.5f, 0f, dr.isOptionSmartText);
        }

        public DropdownRow<T> updateAlignment() {
            this.label.setAlignment(dr.isOptionSmartText ? 1f : 0.5f, 0f, dr.isOptionSmartText);
            this.label.setHitbox(new RelativeHitbox(hb, this.hb.width - (dr.isMultiSelect ? LABEL_OFFSET : LABEL_OFFSET / 2f), this.hb.height, dr.isMultiSelect ? LABEL_OFFSET : LABEL_OFFSET / 2, 0f, false));
            return this;
        }

        public DropdownRow<T> setLabelFunction(FuncT1<String, T> labelFunction, boolean isSmartText) {
            this.label.setSmartText(isSmartText);
            refreshText(labelFunction);
            return this;
        }

        public void refreshText(FuncT1<String, T> labelFunction)
        {
            this.label.setLabel(labelFunction.invoke(item));
        }

        public String getText() {
            return this.label.text;
        }

        public String getTextForWidth() {
            return this.label.text;
        }

        public void move(float x, float y) {
            this.hb.translate(x,y);
            this.checkbox.hb.translate(x,y - TOGGLE_OFFSET);
            this.label.hb.translate(x + (dr.isMultiSelect ? LABEL_OFFSET : LABEL_OFFSET / 2), y);
        }

        public boolean update(boolean isInRange, boolean isSelected) {
            this.hb.update();
            this.label.updateImpl();
            this.checkbox.updateImpl();
            this.isSelected = isSelected;
            if (!isInRange) {
                return false;
            }
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
                this.label.setColor(Color.WHITE);
                this.checkbox.setTexture(ImageMaster.COLOR_TAB_BOX_UNTICKED);
            }

            if ((this.hb.clicked) || (this.hb.hovered && CInputActionSet.select.isJustPressed())) {
                this.hb.clicked = false;
                this.checkbox.setTexture(isSelected ? ImageMaster.COLOR_TAB_BOX_UNTICKED : ImageMaster.COLOR_TAB_BOX_TICKED);
                return true;
            }
            return false;
        }

        public void renderRow(SpriteBatch sb) {
            this.hb.render(sb);
            this.label.tryRender(sb);
            if (dr.isMultiSelect) {
                this.checkbox.tryRender(sb);
            }
        }

        protected void addTooltip() {
            if (dr.tooltipFunction != null)
            {
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

        private void renderCard(AbstractCard card) {
            card.current_x = card.target_x = card.hb.x = InputHelper.mX + PREVIEW_OFFSET_X;
            card.current_y = card.target_y = card.hb.y = InputHelper.mY;
            card.update();
            card.updateHoverLogic();
            card.drawScale = card.targetDrawScale = 0.75f;
            EUI.addPriorityPostRender(card::render);
        }
    }
}