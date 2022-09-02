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
import extendedui.JavaUtils;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;

import java.util.ArrayList;

public class RelicPoolScreen extends AbstractScreen
{
    public static CustomRelicPoolModule CustomModule;

    public GUI_RelicGrid relicGrid;
    private final GUI_Button swapScreen;

    public RelicPoolScreen()
    {
        relicGrid = (GUI_RelicGrid) new GUI_StaticRelicGrid()
                .SetOnRelicRightClick(c -> {
                    c.hb.unhover();
                    CardCrawlGame.relicPopup.open(c);
                })
                .SetVerticalStart(Settings.HEIGHT * 0.74f)
                .ShowScrollbar(true);

        this.swapScreen = new GUI_Button(EUIRM.Images.HexagonalButton.Texture(),
                new AdvancedHitbox(Scale(210), Scale(43)))
                .SetPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.88f)
                .SetFont(FontHelper.buttonLabelFont, 0.8f)
                .SetColor(Color.GRAY)
                .SetBorder(EUIRM.Images.HexagonalButtonBorder.Texture(), Color.GRAY)
                .SetOnClick(() -> EUI.CardsScreen.Open(AbstractDungeon.player, CardPoolPanelItem.GetAllCards()))
                .SetText(EUIRM.Strings.UIPool_ViewCardPool);
    }

    public void Open(AbstractPlayer player, ArrayList<AbstractRelic> relics)
    {
        super.Open(false, true);

        relicGrid.Clear();
        if (relics.isEmpty())
        {
            AbstractDungeon.closeCurrentScreen();
            return;
        }

        relicGrid.SetRelics(relics);
        EUI.RelicHeader.SetGrid(relicGrid).SnapToGroup(false);
        EUI.RelicFilters.Initialize(__ -> {
            EUI.RelicHeader.UpdateForFilters();
            if (CustomModule != null) {
                CustomModule.Open(EUI.RelicHeader.GetRelics());
            }
        }, EUI.RelicHeader.GetOriginalRelics(), player != null ? player.getCardColor() : AbstractCard.CardColor.COLORLESS, true);
        EUI.RelicHeader.UpdateForFilters();

        if (EUIGameUtils.InGame())
        {
            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
        }

        CustomModule = EUI.GetCustomRelicPoolModule(player);
        if (CustomModule != null) {
            CustomModule.SetActive(true);
            CustomModule.Open(JavaUtils.Map(relicGrid.relicGroup, r -> r.relic));
        }

    }

    @Override
    public void Reopen()
    {
        if (EUIGameUtils.InGame())
        {
            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
        }
    }

    @Override
    public void Update()
    {
        if (!EUI.RelicFilters.TryUpdate() && !CardCrawlGame.isPopupOpen) {
            relicGrid.TryUpdate();
            swapScreen.Update();
            EUI.RelicHeader.Update();
            EUI.OpenRelicFiltersButton.TryUpdate();
            if (CustomModule != null) {
                CustomModule.TryUpdate();
            }
        }
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        relicGrid.TryRender(sb);
        swapScreen.Render(sb);
        EUI.RelicHeader.Render(sb);
        if (!EUI.RelicFilters.isActive) {
            EUI.OpenRelicFiltersButton.TryRender(sb);
        }
        if (CustomModule != null) {
            CustomModule.TryRender(sb);
        }
    }
}