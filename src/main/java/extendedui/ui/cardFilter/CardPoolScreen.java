package extendedui.ui.cardFilter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.configuration.EUIHotkeys;
import extendedui.ui.AbstractScreen;
import extendedui.ui.controls.GUI_Button;
import extendedui.ui.controls.GUI_CardGrid;
import extendedui.ui.controls.GUI_Toggle;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.utilities.EUIFontHelper;

public class CardPoolScreen extends AbstractScreen
{
    public static CustomCardPoolModule CustomModule;

    private final GUI_Toggle upgradeToggle;
    private final GUI_Toggle colorlessToggle;
    public GUI_CardGrid cardGrid;

    public CardPoolScreen()
    {
        cardGrid = new GUI_CardGrid()
                .ShowScrollbar(true)
                .CanRenderUpgrades(true);

        upgradeToggle = new GUI_Toggle(new AdvancedHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .SetBackground(EUIRM.Images.Panel.Texture(), Color.DARK_GRAY)
                .SetPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.8f)
                .SetFont(EUIFontHelper.CardDescriptionFont_Large, 0.5f)
                .SetText(SingleCardViewPopup.TEXT[6])
                .SetOnToggle(CardPoolScreen::ToggleViewUpgrades);

        colorlessToggle = new GUI_Toggle(new AdvancedHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .SetBackground(EUIRM.Images.Panel.Texture(), Color.DARK_GRAY)
                .SetPosition(Settings.WIDTH * 0.075f, Settings.HEIGHT * 0.75f)
                .SetFont(EUIFontHelper.CardDescriptionFont_Large, 0.5f)
                .SetText(EUIRM.Strings.UI_ShowColorless)
                .SetOnToggle(val -> {
                    EUI.CardFilters.ColorsDropdown.ToggleSelection(AbstractCard.CardColor.COLORLESS, val, true);
                    EUI.CardFilters.ColorsDropdown.ToggleSelection(AbstractCard.CardColor.CURSE, val, true);
                });
    }

    public void Open(AbstractPlayer player, CardGroup cards)
    {
        super.Open();

        cardGrid.Clear();
        colorlessToggle.SetToggle(false);
        if (cards.isEmpty())
        {
            AbstractDungeon.closeCurrentScreen();
            return;
        }

        cardGrid.SetCardGroup(cards);
        EUI.CustomHeader.setGroup(cards);
        EUI.CustomHeader.SetupButtons();
        EUI.CardFilters.Initialize(__ -> {
            EUI.CustomHeader.UpdateForFilters();
            if (CustomModule != null) {
                CustomModule.Open(EUI.CustomHeader.group.group);
            }
        }, EUI.CustomHeader.originalGroup, player != null ? player.getCardColor() : AbstractCard.CardColor.COLORLESS, true);
        EUI.CustomHeader.UpdateForFilters();

        if (EUIGameUtils.InGame())
        {
            AbstractDungeon.overlayMenu.cancelButton.show(MasterDeckViewScreen.TEXT[1]);
        }

        CustomModule = EUI.GetCustomCardPoolModule(player);
        if (CustomModule != null) {
            CustomModule.SetActive(true);
            CustomModule.Open(cardGrid.cards.group);
        }

    }

    @Override
    public void Update()
    {
        cardGrid.TryUpdate();
        upgradeToggle.SetToggle(SingleCardViewPopup.isViewingUpgrade).Update();
        colorlessToggle.Update();
        EUI.CustomHeader.update();
        if (!EUI.CardFilters.TryUpdate()) {
            EUI.OpenCardFiltersButton.TryUpdate();
        }
        if (CustomModule != null) {
            CustomModule.TryUpdate();
        }
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        cardGrid.TryRender(sb);
        upgradeToggle.Render(sb);
        colorlessToggle.Render(sb);
        EUI.CustomHeader.render(sb);
        if (!EUI.CardFilters.TryRender(sb)) {
            EUI.OpenCardFiltersButton.TryRender(sb);
        }
        if (CustomModule != null) {
            CustomModule.TryRender(sb);
        }
    }

    private static void ToggleViewUpgrades(boolean value)
    {
        SingleCardViewPopup.isViewingUpgrade = value;
    }
}