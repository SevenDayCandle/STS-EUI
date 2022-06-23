package extendedui.ui.cardFilter;

import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.EverythingFix;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.leaderboards.LeaderboardScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.JavaUtils;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.ClassUtils;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.Mathf;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class CustomCardLibraryScreen extends AbstractScreen
{
    protected static final float ICON_SIZE = 40f * Settings.scale;
    public static final int VISIBLE_BUTTONS = 14;

    public static CustomCardPoolModule CustomModule;
    public static AbstractCard.CardColor CurrentColor = AbstractCard.CardColor.COLORLESS;
    public static final HashMap<AbstractCard.CardColor, CardGroup> CardLists = new HashMap<>();
    public static final HashMap<AbstractCard.CardColor, String> CustomColorNames = new HashMap<>();
    protected static boolean Initialized;
    protected final GUI_Button upButton;
    protected final GUI_Button downButton;
    protected final MenuCancelButton button;
    protected final GUI_CardGrid cardGrid;
    protected final ArrayList<GUI_Button> colorButtons = new ArrayList<>();
    protected final GUI_Toggle upgradeToggle;
    protected int topButtonIndex;
    protected float barY;

    public CustomCardLibraryScreen() {
        final float y = Settings.HEIGHT * 0.92f - (VISIBLE_BUTTONS + 1) * Scale(48);

        cardGrid = new GUI_StaticCardGrid()
                .ShowScrollbar(true)
                .CanRenderUpgrades(true)
                .SetVerticalStart(Settings.HEIGHT * 0.65f)
                .SetCardScale(0.6f, 0.75f);
        cardGrid.SetOnCardRightClick(c -> CardCrawlGame.cardPopup.open(c, cardGrid.cards));
        upgradeToggle = new GUI_Toggle(new AdvancedHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .SetPosition(1450.0F * Settings.xScale, Settings.HEIGHT * 0.8f)
                .SetFont(FontHelper.topPanelInfoFont, 1f)
                .SetText(CardLibraryScreen.TEXT[7])
                .SetOnToggle(EUI::ToggleViewUpgrades);
        button = new MenuCancelButton();
        upButton = new GUI_Button(ImageMaster.CF_LEFT_ARROW, new AdvancedHitbox(Settings.WIDTH * 0.05f, y, ICON_SIZE, ICON_SIZE))
                .SetOnClick(__ -> SetTopButtonIndex(topButtonIndex - 1))
                .SetText(null);
        upButton.background.SetRotation(-90);
        downButton = new GUI_Button(ImageMaster.CF_RIGHT_ARROW, new AdvancedHitbox(upButton.hb.cX + Scale(40), y, ICON_SIZE, ICON_SIZE))
                .SetOnClick(__ -> SetTopButtonIndex(topButtonIndex + 1))
                .SetText(null);
        downButton.background.SetRotation(-90);
    }

    public void Initialize(CardLibraryScreen screen) {
        if (!Initialized) {
            // Let's just re-use the hard sorting work that basemod and the base game has done for us :)
            CardLists.put(AbstractCard.CardColor.RED, ClassUtils.GetField(screen, "redCards"));
            CardLists.put(AbstractCard.CardColor.GREEN, ClassUtils.GetField(screen, "greenCards"));
            CardLists.put(AbstractCard.CardColor.BLUE, ClassUtils.GetField(screen, "blueCards"));
            CardLists.put(AbstractCard.CardColor.PURPLE, ClassUtils.GetField(screen, "purpleCards"));
            CardLists.put(AbstractCard.CardColor.CURSE, ClassUtils.GetField(screen, "curseCards"));
            CardLists.put(AbstractCard.CardColor.COLORLESS, ClassUtils.GetField(screen, "colorlessCards"));
            CardLists.putAll(EverythingFix.Fields.cardGroupMap);

            // Save custom mod color names
            for (AbstractPlayer p : CardCrawlGame.characterManager.getAllCharacters()) {
                CustomColorNames.put(p.getCardColor(), p.getLocalizedCharacterName());
            }

            // Add custom buttons. Base game colors come first.
            colorButtons.add(MakeColorButton(AbstractCard.CardColor.COLORLESS));
            colorButtons.add(MakeColorButton(AbstractCard.CardColor.CURSE));
            colorButtons.add(MakeColorButton(AbstractCard.CardColor.RED));
            colorButtons.add(MakeColorButton(AbstractCard.CardColor.GREEN));
            colorButtons.add(MakeColorButton(AbstractCard.CardColor.BLUE));
            colorButtons.add(MakeColorButton(AbstractCard.CardColor.PURPLE));

            // Mod colors are sorted alphabetically
            ArrayList<GUI_Button> moddedButtons = JavaUtils.Map(BaseMod.getCardColors(), this::MakeColorButton);
            moddedButtons.sort(Comparator.comparing(a -> a.text));
            colorButtons.addAll(moddedButtons);

            // Initialize button positions to be at the top
            SetTopButtonIndex(0);

            //boolean showButtons = colorButtons.size() > VISIBLE_BUTTONS;
            //upButton.SetActive(showButtons);
            //downButton.SetActive(showButtons);
        }
        Initialized = true;
    }

    public void Open() {
        super.Open();
        SingleCardViewPopup.isViewingUpgrade = false;
        EUI.CustomHeader.SetupButtons();
        SetActiveColor(CurrentColor);
        this.button.show(CardLibraryScreen.TEXT[0]);
    }

    public void SetActiveColor(AbstractCard.CardColor color) {
        CardGroup cards = CardLists.getOrDefault(color, new CardGroup(CardGroup.CardGroupType.UNSPECIFIED));
        CurrentColor = color;
        cardGrid.Clear();
        cardGrid.SetCardGroup(cards);
        EUI.CustomHeader.setGroup(cards);
        EUI.CardFilters.Initialize(__ -> {
            cardGrid.MoveToTop();
            EUI.CustomHeader.UpdateForFilters();
            if (CustomModule != null) {
                CustomModule.Open(EUI.CustomHeader.group.group);
            }
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
        if (!EUI.CardFilters.TryUpdate()) {
            EUI.OpenCardFiltersButton.TryUpdate();
            for (GUI_Button b : colorButtons) {
                b.TryUpdate();
            }
            upButton.TryUpdate();
            downButton.TryUpdate();
            EUI.CustomHeader.update();
            barY = EUI.CustomHeader.GetCenterY();
            upgradeToggle.SetPosition(upgradeToggle.hb.cX, barY).SetToggle(SingleCardViewPopup.isViewingUpgrade).Update();
            cardGrid.TryUpdate();
            if (CustomModule != null) {
                CustomModule.TryUpdate();
            }
            button.update();
            if (this.button.hb.clicked || InputHelper.pressedEscape) {
                InputHelper.pressedEscape = false;
                this.button.hb.clicked = false;
                this.button.hide();
                CardCrawlGame.mainMenuScreen.panelScreen.refresh();
                Dispose();
            }
        }
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        for (GUI_Button b : colorButtons) {
            b.TryRender(sb);
        }
        upButton.TryRenderCentered(sb);
        downButton.TryRenderCentered(sb);
        sb.setColor(GetColorColor(CurrentColor));
        sb.draw(ImageMaster.COLOR_TAB_BAR, (float) Settings.WIDTH / 2.0F - 667.0F, barY - 51.0F, 667.0F, 51.0F, 1334.0F, 102.0F, Settings.xScale, Settings.scale, 0.0F, 0, 0, 1334, 102, false, false);
        sb.setColor(Color.WHITE);
        upgradeToggle.Render(sb);
        EUI.CustomHeader.render(sb);
        cardGrid.TryRender(sb);
        if (!EUI.CardFilters.TryRender(sb)) {
            EUI.OpenCardFiltersButton.TryRender(sb);
        }
        if (CustomModule != null) {
            CustomModule.TryRender(sb);
        }
        button.render(sb);
    }

    public void SetTopButtonIndex(int index) {
        topButtonIndex = Math.max(0, index);
        int lastButtonIndex = topButtonIndex + VISIBLE_BUTTONS;

        for (int i = 0; i < colorButtons.size(); i++) {
            if (i >= topButtonIndex && i < lastButtonIndex) {
                colorButtons.get(i).SetPosition(Scale(120), Settings.HEIGHT * 0.92f - (i - topButtonIndex) * Scale(48)).SetActive(true);
            }
            else {
                colorButtons.get(i).SetActive(false);
            }
        }

        upButton.SetActive(topButtonIndex > 0);
        downButton.SetActive(topButtonIndex < colorButtons.size() - VISIBLE_BUTTONS);
    }

    protected GUI_Button MakeColorButton(AbstractCard.CardColor co) {
        return new GUI_Button(ImageMaster.COLOR_TAB_BAR, new AdvancedHitbox(235.0F, 102.0F))
                .SetOnClick(__ -> SetActiveColor(co))
                .SetText(GetColorName(co))
                .SetFont(FontHelper.buttonLabelFont, 0.85f)
                .SetColor(GetColorColor(co));
    }

    public static Color GetColorColor(AbstractCard.CardColor co){
        switch (co) {
            case RED:
                return new Color(0.5F, 0.1F, 0.1F, 1.0F);
            case GREEN:
                return new Color(0.25F, 0.55F, 0.0F, 1.0F);
            case BLUE:
                return new Color(0.01F, 0.34F, 0.52F, 1.0F);
            case PURPLE:
                return new Color(0.37F, 0.22F, 0.49F, 1.0F);
            case COLORLESS:
                return new Color(0.4F, 0.4F, 0.4F, 1.0F);
            case CURSE:
                return new Color(0.18F, 0.18F, 0.16F, 1.0F);
            default:
                return BaseMod.getTrailVfxColor(co);
        }
    }

    public static String GetColorName(AbstractCard.CardColor co) {
        switch (co) {
            case RED:
                return CardLibraryScreen.TEXT[1];
            case GREEN:
                return CardLibraryScreen.TEXT[2];
            case BLUE:
                return CardLibraryScreen.TEXT[3];
            case PURPLE:
                return CardLibraryScreen.TEXT[8];
            case CURSE:
                return CardLibraryScreen.TEXT[5];
            case COLORLESS:
                return CardLibraryScreen.TEXT[4];
            default:
                return CustomColorNames.getOrDefault(co, JavaUtils.Capitalize(co.toString()));
        }
    }
}
