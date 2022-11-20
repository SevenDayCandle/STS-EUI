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
    protected static final float ICON_SIZE = Scale(40);
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
        final float y = Settings.HEIGHT * 0.92f - (VISIBLE_BUTTONS + 1) * Scale(48);

        cardGrid = new EUIStaticCardGrid()
                .ShowScrollbar(true)
                .CanRenderUpgrades(true)
                .SetVerticalStart(Settings.HEIGHT * 0.65f)
                .SetCardScale(0.6f, 0.75f);
        cardGrid.SetOnCardRightClick(c -> {
            c.unhover();
            CardCrawlGame.cardPopup.open(c, cardGrid.cards);
        });
        upgradeToggle = new EUIToggle(new AdvancedHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .SetPosition(1450.0F * Settings.xScale, Settings.HEIGHT * 0.8f)
                .SetFont(EUIFontHelper.CardTooltipTitleFont_Large, 1f)
                .SetText(CardLibraryScreen.TEXT[7])
                .SetOnToggle(EUI::ToggleViewUpgrades);
        cancelButton = new MenuCancelButton();

        quickSearch = (EUITextBoxInput) new EUITextBoxInput(EUIRM.Images.RectangularButton.Texture(),
                new AdvancedHitbox(Settings.WIDTH * 0.42f, Settings.HEIGHT * 0.92f, Scale(280), Scale(48)))
                .SetOnComplete((v) -> EUI.CardFilters.NameInput.SetTextAndCommit(v))
                .SetHeader(EUIFontHelper.CardTitleFont_Small, 0.7f, Settings.GOLD_COLOR, EUIRM.Strings.UI_NameSearch)
                .SetColors(Color.GRAY, Settings.CREAM_COLOR)
                .SetAlignment(0.5f, 0.1f)
                .SetFont(EUIFontHelper.CardTitleFont_Small, 0.7f)
                .SetBackgroundTexture(EUIRM.Images.RectangularButton.Texture())
                .SetText("");
        quickSearch.header.SetAlignment(0f, -0.51f);
    }

    public void Initialize(CardLibraryScreen screen) {
        if (!Initialized) {
            // Let's just re-use the hard sorting work that basemod and the base game has done for us :)
            CardLists.put(AbstractCard.CardColor.RED, EUIClassUtils.GetField(screen, "redCards"));
            CardLists.put(AbstractCard.CardColor.GREEN, EUIClassUtils.GetField(screen, "greenCards"));
            CardLists.put(AbstractCard.CardColor.BLUE, EUIClassUtils.GetField(screen, "blueCards"));
            CardLists.put(AbstractCard.CardColor.PURPLE, EUIClassUtils.GetField(screen, "purpleCards"));
            CardLists.put(AbstractCard.CardColor.CURSE, EUIClassUtils.GetField(screen, "curseCards"));
            CardLists.put(AbstractCard.CardColor.COLORLESS, EUIClassUtils.GetField(screen, "colorlessCards"));
            CardLists.putAll(EverythingFix.Fields.cardGroupMap);

            // Add custom buttons. Base game colors come first.
            MakeColorButton(AbstractCard.CardColor.COLORLESS);
            MakeColorButton(AbstractCard.CardColor.CURSE);
            MakeColorButton(AbstractCard.CardColor.RED);
            MakeColorButton(AbstractCard.CardColor.GREEN);
            MakeColorButton(AbstractCard.CardColor.BLUE);
            MakeColorButton(AbstractCard.CardColor.PURPLE);

            // Mod colors are sorted alphabetically
            BaseMod.getCardColors().stream().sorted(Comparator.comparing(EUIGameUtils::GetColorName)).forEach(this::MakeColorButton);
        }
        Initialized = true;
    }

    public void Open() {
        super.Open();
        OpenImpl();
    }

    public void OpenImpl()
    {
        SingleCardViewPopup.isViewingUpgrade = false;
        EUI.CustomHeader.SetupButtons();
        SetActiveColor(CurrentColor);
        this.cancelButton.show(CardLibraryScreen.TEXT[0]);
    }

    public void SetActiveColor(AbstractCard.CardColor color) {
        SetActiveColor(color, CardLists.getOrDefault(color, new CardGroup(CardGroup.CardGroupType.UNSPECIFIED)));
    }

    public void SetActiveColor(AbstractCard.CardColor color, CardGroup cards) {
        EUI.ActingColor = CurrentColor = color;
        cardGrid.Clear();
        cardGrid.SetCardGroup(cards);
        EUI.CustomHeader.setGroup(cards);
        EUI.CardFilters.Initialize(__ -> {
            cardGrid.MoveToTop();
            quickSearch.SetText(EUI.CardFilters.CurrentName != null ? EUI.CardFilters.CurrentName : "");
            EUI.CustomHeader.UpdateForFilters();
            if (CustomModule != null) {
                CustomModule.Open(EUI.CustomHeader.group.group);
            }
            cardGrid.ForceUpdateCardPositions();
        }, EUI.CustomHeader.originalGroup, color, false);
        EUI.CustomHeader.UpdateForFilters();

        CustomModule = EUI.GetCustomCardLibraryModule(color);
        if (CustomModule != null) {
            CustomModule.SetActive(true);
            CustomModule.Open(cardGrid.cards.group);
        }
    }

    @Override
    public void Update()
    {
        boolean shouldDoStandardUpdate = !EUI.CardFilters.TryUpdate() && !CardCrawlGame.isPopupOpen;
        if (shouldDoStandardUpdate) {
            EUI.OpenCardFiltersButton.TryUpdate();
            colorButtons.TryUpdate();
            EUI.CustomHeader.update();
            barY = EUI.CustomHeader.GetCenterY();
            upgradeToggle.SetPosition(upgradeToggle.hb.cX, barY).SetToggle(SingleCardViewPopup.isViewingUpgrade).Update();
            quickSearch.TryUpdate();
            cardGrid.TryUpdate();
            cancelButton.update();
            if (this.cancelButton.hb.clicked || InputHelper.pressedEscape) {
                InputHelper.pressedEscape = false;
                this.cancelButton.hb.clicked = false;
                this.cancelButton.hide();
                CardCrawlGame.mainMenuScreen.panelScreen.refresh();
                if (EUI.CurrentScreen == this)
                {
                    Dispose();
                }
            }
        }
        if (CustomModule != null) {
            CustomModule.TryUpdate(shouldDoStandardUpdate);
        }
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        colorButtons.TryRender(sb);
        sb.setColor(EUIGameUtils.GetColorColor(CurrentColor));
        sb.draw(ImageMaster.COLOR_TAB_BAR, (float) Settings.WIDTH / 2.0F - 667.0F, barY - 51.0F, 667.0F, 51.0F, 1334.0F, 102.0F, Settings.xScale, Settings.scale, 0.0F, 0, 0, 1334, 102, false, false);
        sb.setColor(Color.WHITE);
        upgradeToggle.Render(sb);
        quickSearch.TryRender(sb);
        EUI.CustomHeader.render(sb);
        cardGrid.TryRender(sb);
        if (CustomModule != null) {
            CustomModule.TryRender(sb);
        }
        if (!EUI.CardFilters.isActive) {
            EUI.OpenCardFiltersButton.TryRender(sb);
        }
        cancelButton.render(sb);
    }

    protected void MakeColorButton(AbstractCard.CardColor co) {
        colorButtons.AddButton(button -> SetActiveColor(co), EUIGameUtils.GetColorName(co))
                .SetColor(EUIGameUtils.GetColorColor(co));
    }

}
