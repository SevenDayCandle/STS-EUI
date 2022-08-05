package extendedui.ui.panelitems;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import eatyourbeets.interfaces.delegates.FuncT0;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.configuration.EUIHotkeys;
import extendedui.ui.tooltips.EUITooltip;

public class CardPoolPanelItem extends PCLTopPanelItem
{
    public static final String ID = CreateFullID(CardPoolPanelItem.class);
    protected static FuncT0<String> additionalTextFunc;
    protected CardGroup cardGroup;

    public CardPoolPanelItem() {
        super(Loader.isModLoaded("PrideMod") ? EUIRM.Images.CardPool_Pride : EUIRM.Images.CardPool, ID);
        SetTooltip(new EUITooltip(EUIRM.Strings.UI_ViewCardPool, EUIRM.Strings.UI_ViewCardPoolDescription));
    }

    @Override
    protected void onClick() {
        super.onClick();

        EUI.CardsScreen.Open(AbstractDungeon.player, GetAllCards());
    }

    protected CardGroup GetAllCards() {
        cardGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (CardGroup cg: EUIGameUtils.GetSourceCardPools()) {
            for (AbstractCard c : cg.group) {
                cardGroup.addToTop(c);
            }
        }

        return cardGroup;
    }

    @Override
    public void update() {
        super.update();
        if (this.tooltip != null && getHitbox().hovered) {
            tooltip.SetText(EUIRM.Strings.UI_ViewCardPool + " (" + EUIHotkeys.openCardPool.getKeyString() + ")", GetFullDescription());
            EUITooltip.QueueTooltip(tooltip);
        }
        if (EUIHotkeys.openCardPool.isJustPressed() && EUI.CurrentScreen != EUI.CardsScreen) {
            EUI.CardsScreen.Open(AbstractDungeon.player, GetAllCards());
        }
    }

    public void SetAdditionalStringFunction(FuncT0<String> func) {
        additionalTextFunc = func;
        update();
    }

    public String GetFullDescription()
    {
        String base = EUIRM.Strings.UI_ViewCardPoolDescription;
        String addendum = additionalTextFunc != null ? additionalTextFunc.Invoke() : null;
        return addendum != null ? base + " || " + addendum : base;
    }
}
