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
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.utilities.EUIFontHelper;

public class CardPoolScreen extends AbstractScreen
{
    public static CustomCardPoolModule customModule;

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

        upgradeToggle = new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.8f)
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setOnToggle(EUI::toggleViewUpgrades);

        colorlessToggle = new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.75f)
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.5f)
                .setText(EUIRM.strings.uicardpoolShowcolorless)
                .setOnToggle(val -> {
                    EUI.cardFilters.colorsDropdown.toggleSelection(AbstractCard.CardColor.COLORLESS, val, true);
                    EUI.cardFilters.colorsDropdown.toggleSelection(AbstractCard.CardColor.CURSE, val, true);
                });

        this.swapScreen = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.88f)
                .setFont(FontHelper.buttonLabelFont, 0.8f)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.relicScreen.open(AbstractDungeon.player, CardPoolPanelItem.getAllRelics()))
                .setText(EUIRM.strings.uipoolViewrelicpool);
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
        EUI.customHeader.setGroup(cards);
        EUI.customHeader.setupButtons();
        EUI.cardFilters.initialize(__ -> {
            EUI.customHeader.updateForFilters();
            if (customModule != null) {
                customModule.open(EUI.customHeader.group.group);
            }
            cardGrid.forceUpdateCardPositions();
        }, EUI.customHeader.originalGroup, player != null ? player.getCardColor() : AbstractCard.CardColor.COLORLESS, true);
        EUI.customHeader.updateForFilters();

        if (EUIGameUtils.inGame())
        {
            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
        }

        customModule = EUI.getCustomCardPoolModule(player);
        if (customModule != null) {
            customModule.setActive(true);
            customModule.open(cardGrid.cards.group);
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
        if (!EUI.cardFilters.tryUpdate() && !CardCrawlGame.isPopupOpen) {
            cardGrid.tryUpdate();
            upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade).updateImpl();
            colorlessToggle.updateImpl();
            swapScreen.updateImpl();
            EUI.customHeader.update();
            EUI.openCardFiltersButton.tryUpdate();
            if (customModule != null) {
                customModule.tryUpdate();
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
        EUI.customHeader.render(sb);
        if (!EUI.cardFilters.isActive) {
            EUI.openCardFiltersButton.tryRender(sb);
        }
        if (customModule != null) {
            customModule.tryRender(sb);
        }
    }
}