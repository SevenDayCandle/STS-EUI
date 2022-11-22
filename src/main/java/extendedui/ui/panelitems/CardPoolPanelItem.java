package extendedui.ui.panelitems;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.input.InputAction;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import eatyourbeets.interfaces.delegates.ActionT0;
import eatyourbeets.interfaces.delegates.FuncT0;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.configuration.EUIHotkeys;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;

import java.util.ArrayList;

public class CardPoolPanelItem extends PCLTopPanelItem
{
    public static final String ID = createFullID(CardPoolPanelItem.class);
    protected static FuncT0<String> additionalTextFunc;
    protected EUIContextMenu<ContextOption> contextMenu;

    public CardPoolPanelItem() {
        super(Loader.isModLoaded("PrideMod") ? EUIRM.Images.CardPool_Pride : EUIRM.Images.CardPool, ID);
        setTooltip(new EUITooltip(EUIRM.Strings.UIPool_ViewPool, EUIRM.Strings.UIPool_ViewPoolDescription));

        contextMenu = (EUIContextMenu<ContextOption>) new EUIContextMenu<ContextOption>(new AdvancedHitbox(0, 0, 0, 0), ContextOption::getDisplayName)
                .setOnChange(options -> {
                    for (ContextOption o : options)
                    {
                        o.onSelect.invoke();
                    }
                })
                .setFontForRows(EUIFontHelper.CardTooltipFont, 1f)
                .setItems(ContextOption.values())
                .setCanAutosizeButton(true);
    }

    @Override
    protected void onClick() {
        super.onClick();

        EUI.CardsScreen.open(AbstractDungeon.player, getAllCards());
    }

    @Override
    protected void onRightClick() {
        super.onRightClick();

        contextMenu.setPosition(InputHelper.mX > Settings.WIDTH * 0.75f ? InputHelper.mX - contextMenu.hb.width : InputHelper.mX, InputHelper.mY);
        contextMenu.refreshText();
        contextMenu.openOrCloseMenu();
    }

    @Override
    public void update() {
        super.update();
        if (this.tooltip != null && getHitbox().hovered) {
            tooltip.setText(EUIRM.Strings.UIPool_ViewPool + " (" + EUIHotkeys.openCardPool.getKeyString() + ")", getFullDescription());
            EUITooltip.queueTooltip(tooltip);
        }

        if (EUIHotkeys.openCardPool.isJustPressed() && EUI.CurrentScreen != EUI.CardsScreen) {
            EUI.CardsScreen.open(AbstractDungeon.player, getAllCards());
        }
        else if (EUIHotkeys.openRelicPool.isJustPressed() && EUI.CurrentScreen != EUI.RelicScreen) {
            EUI.RelicScreen.open(AbstractDungeon.player, getAllRelics());
        }

        contextMenu.tryUpdate();
    }

    @Override
    public void render(SpriteBatch sb)
    {
        super.render(sb);

        contextMenu.tryRender(sb);
    }

    public void setAdditionalStringFunction(FuncT0<String> func) {
        additionalTextFunc = func;
        update();
    }

    public String getFullDescription()
    {
        String base = EUIRM.Strings.UIPool_ViewPoolDescription;
        String addendum = additionalTextFunc != null ? additionalTextFunc.invoke() : null;
        return addendum != null ? base + " || " + addendum : base;
    }

    public static CardGroup getAllCards() {
        CardGroup cardGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        for (CardGroup cg: EUIGameUtils.getSourceCardPools()) {
            for (AbstractCard c : cg.group) {
                cardGroup.addToTop(c);
            }
        }

        return cardGroup;
    }

    public static ArrayList<AbstractRelic> getAllRelics() {
        ArrayList<AbstractRelic> newRelics = new ArrayList<>();
        for (String relicID : EUIGameUtils.getAllRelicIDs())
        {
            AbstractRelic original = RelicLibrary.getRelic(relicID);
            if (original instanceof Circlet)
            {
                original = BaseMod.getCustomRelic(relicID);
            }

            AbstractRelic newRelic = original.makeCopy();
            newRelic.hb = new Hitbox(80.0F * Settings.scale, 80.0F * Settings.scale);
            newRelic.isSeen = UnlockTracker.isRelicSeen(original.relicId);
            newRelics.add(newRelic);
        }
        return newRelics;
    }

    public enum ContextOption
    {
        CardPool(EUIRM.Strings.UIPool_ViewCardPool, EUIHotkeys.openCardPool, () -> EUI.CardsScreen.open(AbstractDungeon.player, getAllCards())),
        RelicPool(EUIRM.Strings.UIPool_ViewRelicPool, EUIHotkeys.openRelicPool, () -> EUI.RelicScreen.open(AbstractDungeon.player, getAllRelics()));

        public final String baseName;
        public final InputAction hotkey;
        public final ActionT0 onSelect;

        ContextOption(String name, InputAction hotkey, ActionT0 onSelect)
        {
            this.baseName = name;
            this.hotkey = hotkey;
            this.onSelect = onSelect;
        }

        public String getDisplayName()
        {
            return baseName + " (" + hotkey.getKeyString() + ")";
        }
    }
}
