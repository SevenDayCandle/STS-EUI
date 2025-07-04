package extendedui.ui.panelitems;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.actions.common.TransformCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.input.InputAction;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIHotkeys;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.ui.controls.EUIContextMenu;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.screens.CardPoolScreen;
import extendedui.ui.screens.PotionPoolScreen;
import extendedui.ui.screens.RelicPoolScreen;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;

import java.util.ArrayList;

public class CardPoolPanelItem extends PCLTopPanelItem {
    public static final String ID = createFullID(CardPoolPanelItem.class);
    protected static FuncT0<String> additionalTextFunc;
    protected EUIContextMenu<ContextOption> contextMenu;

    public CardPoolPanelItem() {
        super(Loader.isModLoaded("PrideMod") ? EUIRM.images.cardpoolPride : EUIRM.images.cardPool, ID);
        setTooltip(new EUITooltip(EUIRM.strings.uipool_viewPool, EUIRM.strings.uipool_viewPoolDescription));

        contextMenu = (EUIContextMenu<ContextOption>) new EUIContextMenu<ContextOption>(new EUIHitbox(0, 0, 0, 0), ContextOption::getDisplayName)
                .setOnChange(options -> {
                    for (ContextOption o : options) {
                        o.onSelect();
                    }
                })
                .setFontForRows(EUIFontHelper.tooltipFont, 1f)
                .setItems(ContextOption.values())
                .setCanAutosizeButton(true);
    }

    protected static int compareCardFromAllColors(AbstractCard c1, AbstractCard c2) {
        int c1val = c1.color.ordinal() * 100 + c1.rarity.ordinal();
        int c2val = c2.color.ordinal() * 100 + c2.rarity.ordinal();
        return c2val - c1val;
    }

    public static CardGroup getAllCards() {
        CardGroup cardGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        if (EUIGameUtils.canReceiveAnyColorCard()) {
            cardGroup.group = EUIGameUtils.getEveryColorCardForPoolDisplay();
            cardGroup.group.sort(CardPoolPanelItem::compareCardFromAllColors);
        }
        else {
            for (CardGroup cg : EUIGameUtils.getGameCardPools()) {
                for (AbstractCard c : cg.group) {
                    cardGroup.addToTop(c);
                }
            }
        }

        return cardGroup;
    }

    public static ArrayList<AbstractPotion> getAllPotions() {
        return EUIUtils.mapAsNonnull(PotionHelper.getPotions(AbstractDungeon.player != null ? AbstractDungeon.player.chosenClass : null, false), PotionHelper::getPotion);
    }

    public static ArrayList<AbstractRelic> getAllRelics() {
        ArrayList<AbstractRelic> newRelics = new ArrayList<>();
        for (String relicID : EUIGameUtils.getInGameRelicIDs()) {
            AbstractRelic original = RelicLibrary.getRelic(relicID);
            if (original instanceof Circlet) {
                original = BaseMod.getCustomRelic(relicID);
            }

            AbstractRelic newRelic = original.makeCopy();
            if (newRelic != null) {
                newRelic.hb = new Hitbox(80.0F * Settings.scale, 80.0F * Settings.scale);
                newRelic.isSeen = UnlockTracker.isRelicSeen(original.relicId) || original.isSeen;
                newRelics.add(newRelic);
            }
            else {
                EUIUtils.logError(CardPoolPanelItem.class, "WTF relic did not copy " + relicID);
            }

        }
        return newRelics;
    }

    public String getFullDescription() {
        String base = EUIRM.strings.uipool_viewPoolDescription;
        String addendum = additionalTextFunc != null ? additionalTextFunc.invoke() : null;
        return addendum != null ? base + " || " + addendum : base;
    }

    @Override
    protected void onClick() {
        super.onClick();

        EUI.cardsScreen.openScreen(AbstractDungeon.player, getAllCards());
    }

    @Override
    protected void onRightClick() {
        super.onRightClick();

        contextMenu.setPosition(InputHelper.mX > Settings.WIDTH * 0.75f ? InputHelper.mX - contextMenu.hb.width : InputHelper.mX, InputHelper.mY);
        contextMenu.refreshText();
        contextMenu.openOrCloseMenu();
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);

        contextMenu.tryRender(sb);
    }

    public void setAdditionalStringFunction(FuncT0<String> func) {
        additionalTextFunc = func;
        update();
    }

    @Override
    public void update() {
        super.update();
        if (this.tooltip != null && getHitbox().hovered) {
            tooltip.setText(EUIRM.strings.uipool_viewPool + " (" + EUIHotkeys.openCardPool.getKeyString() + ")", getFullDescription());
            EUITooltip.queueTooltip(tooltip);
        }

        if (EUIHotkeys.openCardPool.isJustPressed()) {
            if (AbstractDungeon.screen == CardPoolScreen.CARD_POOL_SCREEN) {
                AbstractDungeon.closeCurrentScreen();
            }
            else {
                EUI.cardsScreen.openScreen(AbstractDungeon.player, getAllCards());
            }
        }
        else if (EUIHotkeys.openRelicPool.isJustPressed()) {
            if (AbstractDungeon.screen == RelicPoolScreen.RELIC_POOL_SCREEN) {
                AbstractDungeon.closeCurrentScreen();
            }
            else {
                EUI.relicScreen.openScreen(AbstractDungeon.player, getAllRelics());
            }
        }
        else if (EUIHotkeys.openPotionPool.isJustPressed()) {
            if (AbstractDungeon.screen == PotionPoolScreen.POTION_POOL_SCREEN) {
                AbstractDungeon.closeCurrentScreen();
            }
            else {
                EUI.potionScreen.openScreen(AbstractDungeon.player, getAllPotions());
            }
        }

        contextMenu.tryUpdate();
    }

    public enum ContextOption {
        CardPool(EUIRM.strings.uipool_viewCardPool, EUIHotkeys.openCardPool),
        RelicPool(EUIRM.strings.uipool_viewRelicPool, EUIHotkeys.openRelicPool),
        PotionPool(EUIRM.strings.uipool_viewPotionPool, EUIHotkeys.openPotionPool);

        public final String baseName;
        public final InputAction hotkey;

        ContextOption(String name, InputAction hotkey) {
            this.baseName = name;
            this.hotkey = hotkey;
        }

        public String getDisplayName() {
            return baseName + " (" + hotkey.getKeyString() + ")";
        }

        public void onSelect() {
            switch (this) {
                case CardPool:
                    EUI.cardsScreen.openScreen(AbstractDungeon.player, getAllCards());
                    break;
                case RelicPool:
                    EUI.relicScreen.openScreen(AbstractDungeon.player, getAllRelics());
                    break;
                case PotionPool:
                    EUI.potionScreen.openScreen(AbstractDungeon.player, getAllPotions());
                    break;
            }
        }
    }
}
