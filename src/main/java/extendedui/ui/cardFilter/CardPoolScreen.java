package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUICardGrid;
import extendedui.ui.controls.EUIStaticCardGrid;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.utilities.EUIFontHelper;

public class CardPoolScreen extends AbstractScreen
{
    public static CustomCardPoolModule CustomModule;

    private final EUIToggle upgradeToggle;
    private final EUIToggle colorlessToggle;
    private final EUIButton swapScreen;
    public EUICardGrid cardGrid;

    public CardPoolScreen()
    {
        cardGrid = new EUIStaticCardGrid()
                .showScrollbar(true)
                .canRenderUpgrades(true)
                .setOnCardRightClick(c -> {
                    c.unhover();
                    CardCrawlGame.cardPopup.open(c, cardGrid.cards);
                })
                .setVerticalStart(Settings.HEIGHT * 0.66f);

        upgradeToggle = new EUIToggle(new AdvancedHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setBackground(EUIRM.Images.panel.texture(), Color.DARK_GRAY)
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.8f)
                .setFont(EUIFontHelper.CardDescriptionFont_Large, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setOnToggle(EUI::toggleViewUpgrades);

        colorlessToggle = new EUIToggle(new AdvancedHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setBackground(EUIRM.Images.panel.texture(), Color.DARK_GRAY)
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.75f)
                .setFont(EUIFontHelper.CardDescriptionFont_Large, 0.5f)
                .setText(EUIRM.Strings.uicardpoolShowcolorless)
                .setOnToggle(val -> {
                    EUI.CardFilters.colorsDropdown.toggleSelection(AbstractCard.CardColor.COLORLESS, val, true);
                    EUI.CardFilters.colorsDropdown.toggleSelection(AbstractCard.CardColor.CURSE, val, true);
                });

        this.swapScreen = new EUIButton(EUIRM.Images.hexagonalButton.texture(),
                new AdvancedHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.88f)
                .setFont(FontHelper.buttonLabelFont, 0.8f)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.Images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.RelicScreen.open(AbstractDungeon.player, CardPoolPanelItem.getAllRelics()))
                .setText(EUIRM.Strings.uipoolViewrelicpool);
    }

    public void open(AbstractPlayer player, CardGroup cards)
    {
        super.open(false, true);

        cardGrid.clear();
        colorlessToggle.setToggle(false);
        if (cards.isEmpty())
        {
            AbstractDungeon.closeCurrentScreen();
            return;
        }

        cardGrid.setCardGroup(cards);
        EUI.CustomHeader.setGroup(cards);
        EUI.CustomHeader.setupButtons();
        EUI.CardFilters.initialize(__ -> {
            EUI.CustomHeader.updateForFilters();
            if (CustomModule != null) {
                CustomModule.open(EUI.CustomHeader.group.group);
            }
            cardGrid.forceUpdateCardPositions();
        }, EUI.CustomHeader.originalGroup, player != null ? player.getCardColor() : AbstractCard.CardColor.COLORLESS, true);
        EUI.CustomHeader.updateForFilters();

        if (EUIGameUtils.inGame())
        {
            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
        }

        CustomModule = EUI.getCustomCardPoolModule(player);
        if (CustomModule != null) {
            CustomModule.setActive(true);
            CustomModule.open(cardGrid.cards.group);
        }

    }

    @Override
    public void reopen()
    {
        if (EUIGameUtils.inGame())
        {
            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
        }
    }

    @Override
    public void updateImpl()
    {
        if (!EUI.CardFilters.tryUpdate() && !CardCrawlGame.isPopupOpen) {
            cardGrid.tryUpdate();
            upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade).updateImpl();
            colorlessToggle.updateImpl();
            swapScreen.updateImpl();
            EUI.CustomHeader.update();
            EUI.OpenCardFiltersButton.tryUpdate();
            if (CustomModule != null) {
                CustomModule.tryUpdate();
            }
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        cardGrid.tryRender(sb);
        upgradeToggle.renderImpl(sb);
        colorlessToggle.renderImpl(sb);
        swapScreen.renderImpl(sb);
        EUI.CustomHeader.render(sb);
        if (!EUI.CardFilters.isActive) {
            EUI.OpenCardFiltersButton.tryRender(sb);
        }
        if (CustomModule != null) {
            CustomModule.tryRender(sb);
        }
    }
}