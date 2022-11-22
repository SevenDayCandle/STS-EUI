package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIRelicGrid;
import extendedui.ui.controls.EUIStaticRelicGrid;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;

import java.util.ArrayList;

public class RelicPoolScreen extends AbstractScreen
{
    public static CustomRelicPoolModule CustomModule;

    public EUIRelicGrid relicGrid;
    private final EUIButton swapScreen;

    public RelicPoolScreen()
    {
        relicGrid = (EUIRelicGrid) new EUIStaticRelicGrid()
                .setOnRelicRightClick(c -> {
                    c.hb.unhover();
                    CardCrawlGame.relicPopup.open(c);
                })
                .setVerticalStart(Settings.HEIGHT * 0.74f)
                .showScrollbar(true);

        this.swapScreen = new EUIButton(EUIRM.Images.hexagonalButton.texture(),
                new AdvancedHitbox(scale(210), scale(43)))
                .setPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.88f)
                .setFont(FontHelper.buttonLabelFont, 0.8f)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.Images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> EUI.CardsScreen.open(AbstractDungeon.player, CardPoolPanelItem.getAllCards()))
                .setText(EUIRM.Strings.uipoolViewcardpool);
    }

    public void open(AbstractPlayer player, ArrayList<AbstractRelic> relics)
    {
        super.open(false, true);

        relicGrid.clear();
        if (relics.isEmpty())
        {
            AbstractDungeon.closeCurrentScreen();
            return;
        }

        relicGrid.setRelics(relics);
        EUI.RelicHeader.setGrid(relicGrid).snapToGroup(false);
        EUI.RelicFilters.initialize(__ -> {
            EUI.RelicHeader.updateForFilters();
            if (CustomModule != null) {
                CustomModule.open(EUI.RelicHeader.getRelics());
            }
            relicGrid.forceUpdateRelicPositions();
        }, EUI.RelicHeader.getOriginalRelics(), player != null ? player.getCardColor() : AbstractCard.CardColor.COLORLESS, true);
        EUI.RelicHeader.updateForFilters();

        if (EUIGameUtils.inGame())
        {
            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
        }

        CustomModule = EUI.getCustomRelicPoolModule(player);
        if (CustomModule != null) {
            CustomModule.setActive(true);
            CustomModule.open(EUIUtils.map(relicGrid.relicGroup, r -> r.relic));
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
        if (!EUI.RelicFilters.tryUpdate() && !CardCrawlGame.isPopupOpen) {
            relicGrid.tryUpdate();
            swapScreen.updateImpl();
            EUI.RelicHeader.updateImpl();
            EUI.OpenRelicFiltersButton.tryUpdate();
            if (CustomModule != null) {
                CustomModule.tryUpdate();
            }
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        relicGrid.tryRender(sb);
        swapScreen.renderImpl(sb);
        EUI.RelicHeader.renderImpl(sb);
        if (!EUI.RelicFilters.isActive) {
            EUI.OpenRelicFiltersButton.tryRender(sb);
        }
        if (CustomModule != null) {
            CustomModule.tryRender(sb);
        }
    }
}