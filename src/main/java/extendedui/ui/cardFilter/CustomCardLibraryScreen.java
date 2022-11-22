package extendedui.ui.cardFilter;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.EverythingFix;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;

import java.util.Comparator;
import java.util.HashMap;

public class CustomCardLibraryScreen extends AbstractScreen
{
    protected static final float ICON_SIZE = scale(40);
    public static final int VISIBLE_BUTTONS = 14;
    public static AbstractCard.CardColor CurrentColor = AbstractCard.CardColor.COLORLESS;
    public static CustomCardPoolModule CustomModule;
    public static final HashMap<AbstractCard.CardColor, CardGroup> CardLists = new HashMap<>();

    public final EUICardGrid cardGrid;
    public final EUITextBoxInput quickSearch;
    public final EUIToggle upgradeToggle;
    public final MenuCancelButton cancelButton;
    protected final EUIButtonList colorButtons = new EUIButtonList();
    protected float barY;
    protected int topButtonIndex;
    protected static boolean Initialized;

    public CustomCardLibraryScreen() {
        final float y = Settings.HEIGHT * 0.92f - (VISIBLE_BUTTONS + 1) * scale(48);

        cardGrid = new EUIStaticCardGrid()
                .showScrollbar(true)
                .canRenderUpgrades(true)
                .setVerticalStart(Settings.HEIGHT * 0.65f)
                .setCardScale(0.6f, 0.75f);
        cardGrid.setOnCardRightClick(c -> {
            c.unhover();
            CardCrawlGame.cardPopup.open(c, cardGrid.cards);
        });
        upgradeToggle = new EUIToggle(new AdvancedHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setPosition(1450.0F * Settings.xScale, Settings.HEIGHT * 0.8f)
                .setFont(EUIFontHelper.CardTooltipTitleFont_Large, 1f)
                .setText(CardLibraryScreen.TEXT[7])
                .setOnToggle(EUI::toggleViewUpgrades);
        cancelButton = new MenuCancelButton();

        quickSearch = (EUITextBoxInput) new EUITextBoxInput(EUIRM.Images.RectangularButton.texture(),
                new AdvancedHitbox(Settings.WIDTH * 0.42f, Settings.HEIGHT * 0.92f, scale(280), scale(48)))
                .setOnComplete((v) -> EUI.CardFilters.NameInput.setTextAndCommit(v))
                .setHeader(EUIFontHelper.CardTitleFont_Small, 0.7f, Settings.GOLD_COLOR, EUIRM.Strings.UI_NameSearch)
                .setColors(Color.GRAY, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.1f)
                .setFont(EUIFontHelper.CardTitleFont_Small, 0.7f)
                .setBackgroundTexture(EUIRM.Images.RectangularButton.texture())
                .setLabel("");
        quickSearch.header.setAlignment(0f, -0.51f);
    }

    public void initialize(CardLibraryScreen screen) {
        if (!Initialized) {
            // Let's just re-use the hard sorting work that basemod and the base game has done for us :)
            CardLists.put(AbstractCard.CardColor.RED, EUIClassUtils.getField(screen, "redCards"));
            CardLists.put(AbstractCard.CardColor.GREEN, EUIClassUtils.getField(screen, "greenCards"));
            CardLists.put(AbstractCard.CardColor.BLUE, EUIClassUtils.getField(screen, "blueCards"));
            CardLists.put(AbstractCard.CardColor.PURPLE, EUIClassUtils.getField(screen, "purpleCards"));
            CardLists.put(AbstractCard.CardColor.CURSE, EUIClassUtils.getField(screen, "curseCards"));
            CardLists.put(AbstractCard.CardColor.COLORLESS, EUIClassUtils.getField(screen, "colorlessCards"));
            CardLists.putAll(EverythingFix.Fields.cardGroupMap);

            // Add custom buttons. Base game colors come first.
            makeColorButton(AbstractCard.CardColor.COLORLESS);
            makeColorButton(AbstractCard.CardColor.CURSE);
            makeColorButton(AbstractCard.CardColor.RED);
            makeColorButton(AbstractCard.CardColor.GREEN);
            makeColorButton(AbstractCard.CardColor.BLUE);
            makeColorButton(AbstractCard.CardColor.PURPLE);

            // Mod colors are sorted alphabetically
            BaseMod.getCardColors().stream().sorted(Comparator.comparing(EUIGameUtils::getColorName)).forEach(this::makeColorButton);
        }
        Initialized = true;
    }

    public void open() {
        super.open();
        openImpl();
    }

    public void openImpl()
    {
        SingleCardViewPopup.isViewingUpgrade = false;
        EUI.CustomHeader.setupButtons();
        setActiveColor(CurrentColor);
        this.cancelButton.show(CardLibraryScreen.TEXT[0]);
    }

    public void setActiveColor(AbstractCard.CardColor color) {
        setActiveColor(color, CardLists.getOrDefault(color, new CardGroup(CardGroup.CardGroupType.UNSPECIFIED)));
    }

    public void setActiveColor(AbstractCard.CardColor color, CardGroup cards) {
        EUI.ActingColor = CurrentColor = color;
        cardGrid.clear();
        cardGrid.setCardGroup(cards);
        EUI.CustomHeader.setGroup(cards);
        EUI.CardFilters.initialize(__ -> {
            cardGrid.moveToTop();
            quickSearch.setLabel(EUI.CardFilters.CurrentName != null ? EUI.CardFilters.CurrentName : "");
            EUI.CustomHeader.updateForFilters();
            if (CustomModule != null) {
                CustomModule.open(EUI.CustomHeader.group.group);
            }
            cardGrid.forceUpdateCardPositions();
        }, EUI.CustomHeader.originalGroup, color, false);
        EUI.CustomHeader.updateForFilters();

        CustomModule = EUI.getCustomCardLibraryModule(color);
        if (CustomModule != null) {
            CustomModule.setActive(true);
            CustomModule.open(cardGrid.cards.group);
        }
    }

    @Override
    public void updateImpl()
    {
        boolean shouldDoStandardUpdate = !EUI.CardFilters.tryUpdate() && !CardCrawlGame.isPopupOpen;
        if (shouldDoStandardUpdate) {
            EUI.OpenCardFiltersButton.tryUpdate();
            colorButtons.tryUpdate();
            EUI.CustomHeader.update();
            barY = EUI.CustomHeader.getCenterY();
            upgradeToggle.setPosition(upgradeToggle.hb.cX, barY).setToggle(SingleCardViewPopup.isViewingUpgrade).updateImpl();
            quickSearch.tryUpdate();
            cardGrid.tryUpdate();
            cancelButton.update();
            if (this.cancelButton.hb.clicked || InputHelper.pressedEscape) {
                InputHelper.pressedEscape = false;
                this.cancelButton.hb.clicked = false;
                this.cancelButton.hide();
                CardCrawlGame.mainMenuScreen.panelScreen.refresh();
                if (EUI.CurrentScreen == this)
                {
                    dispose();
                }
            }
        }
        if (CustomModule != null) {
            CustomModule.tryUpdate(shouldDoStandardUpdate);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        colorButtons.tryRender(sb);
        sb.setColor(EUIGameUtils.getColorColor(CurrentColor));
        sb.draw(ImageMaster.COLOR_TAB_BAR, (float) Settings.WIDTH / 2.0F - 667.0F, barY - 51.0F, 667.0F, 51.0F, 1334.0F, 102.0F, Settings.xScale, Settings.scale, 0.0F, 0, 0, 1334, 102, false, false);
        sb.setColor(Color.WHITE);
        upgradeToggle.renderImpl(sb);
        quickSearch.tryRender(sb);
        EUI.CustomHeader.render(sb);
        cardGrid.tryRender(sb);
        if (CustomModule != null) {
            CustomModule.tryRender(sb);
        }
        if (!EUI.CardFilters.isActive) {
            EUI.OpenCardFiltersButton.tryRender(sb);
        }
        cancelButton.render(sb);
    }

    protected void makeColorButton(AbstractCard.CardColor co) {
        colorButtons.addButton(button -> setActiveColor(co), EUIGameUtils.getColorName(co))
                .setColor(EUIGameUtils.getColorColor(co));
    }

}
