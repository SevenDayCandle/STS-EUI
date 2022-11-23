package extendedui.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.GenericCondition;

import java.util.ArrayList;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod and https://github.com/SevenDayCandle/STS-FoolMod

public class GridCardSelectScreenHelper
{
    private static final CardGroup mergedGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    private static final ArrayList<CardGroup> cardGroups = new ArrayList<>();
    private static final EUITextBox dynamicLabel = new EUITextBox(ImageMaster.WHITE_SQUARE_IMG,
            new AdvancedHitbox(Settings.WIDTH / 4.0F, 96.0F * Settings.scale, Settings.WIDTH / 2.0F, 48.0F * Settings.scale))
            .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
            .setAlignment(0.7f, 0.15f, true, false)
            .setFont(EUIFontHelper.carddescriptionfontNormal, 1f);
    private static GenericCondition<ArrayList<AbstractCard>> condition;
    private static FuncT1<String, ArrayList<AbstractCard>> dynamicString;
    private static ActionT3<CardGroup, ArrayList<AbstractCard>, AbstractCard> onClickCard;
    private static boolean enabled = false;

    public static void clear(boolean clearFunctions)
    {
        cardGroups.clear();
        mergedGroup.clear();
        if (clearFunctions) {
            condition = null;
            dynamicString = null;
            onClickCard = null;
        }
    }

    public static void setCondition(GenericCondition<ArrayList<AbstractCard>> newCondition) {
        condition = newCondition;
    }

    public static void setDynamicLabel(FuncT1<String, ArrayList<AbstractCard>> stringFunc) {
        dynamicString = stringFunc;
        if (dynamicString != null) {
            dynamicLabel
                    .setLabel(dynamicString.invoke(AbstractDungeon.gridSelectScreen.selectedCards))
                    .autosize(1f, null)
                    .setPosition((Settings.WIDTH / 2.0F) - dynamicLabel.hb.width / 8, 96.0F * Settings.scale);
        }
    }

    public static void setOnClickCard(ActionT3<CardGroup, ArrayList<AbstractCard>, AbstractCard> newOnClickCard) {
        onClickCard = newOnClickCard;
    }

    public static void addGroup(CardGroup cardGroup)
    {
        if (!cardGroup.isEmpty())
        {
            cardGroups.add(cardGroup);
            mergedGroup.group.addAll(cardGroup.group);
        }

        enabled = !mergedGroup.isEmpty();
    }

    public static CardGroup getCardGroup()
    {
        return mergedGroup;
    }

    public static void open(GridCardSelectScreen selectScreen)
    {
        if (!enabled)
        {
            clear(false);
        }
        else
        {
            if (cardGroups.size() == 1)
            {
                EUIClassUtils.setField(selectScreen, "targetGroup", cardGroups.get(0));
                clear(false);
            }

            enabled = false;
        }
    }

    public static boolean isConditionMet() {
        return condition == null || condition.check(AbstractDungeon.gridSelectScreen.selectedCards);
    }

    public static boolean updateCardPositionAndHover(GridCardSelectScreen selectScreen)
    {
        if (cardGroups.isEmpty())
        {
            return false;
        }

        float lineNum = 0;

        float drawStartX = EUIClassUtils.getField(selectScreen, "drawStartX");
        float drawStartY = EUIClassUtils.getField(selectScreen, "drawStartY");
        float padX = EUIClassUtils.getField(selectScreen, "padX");
        float padY = EUIClassUtils.getField(selectScreen, "padY");
        float currentDiffY = EUIClassUtils.getField(selectScreen, "currentDiffY");

        EUIClassUtils.setField(selectScreen, "hoveredCard", null);

        for (CardGroup cardGroup : cardGroups)
        {
            ArrayList<AbstractCard> cards = cardGroup.group;
            for (int i = 0; i < cards.size(); ++i)
            {
                int mod = i % 5;
                if (mod == 0 && i != 0)
                {
                    lineNum += 1;
                }

                AbstractCard card = cards.get(i);

                card.target_x = drawStartX + (float) mod * padX;
                card.target_y = drawStartY + currentDiffY - lineNum * padY;
                card.fadingOut = false;
                card.stopGlowing();
                card.update();
                card.updateHoverLogic();

                if (card.hb.hovered)
                {
                    EUIClassUtils.setField(selectScreen, "hoveredCard", card);
                }
            }

            lineNum += 1.3f;
        }

        return true;
    }

    public static boolean calculateScrollBounds(GridCardSelectScreen instance)
    {
        if (cardGroups.isEmpty())
        {
            return false;
        }

        float padY = EUIClassUtils.getField(instance, "padY");
        CardGroup targetCardGroup = EUIClassUtils.getField(instance, "targetGroup");

        float scrollTmp = (mergedGroup.size() + 2.6f * cardGroups.size()) / 5f - 2;
        if (targetCardGroup.size() % 5 != 0)
        {
            scrollTmp += 1;
        }

        EUIClassUtils.setField(instance, "scrollUpperBound", Settings.DEFAULT_SCROLL_LIMIT + scrollTmp * padY);
        EUIClassUtils.setField(instance, "prevDeckSize", targetCardGroup.size());

        return true;
    }

    public static void invokeOnClick(GridCardSelectScreen selectScreen){
        if (onClickCard != null) {
            onClickCard.invoke(mergedGroup, AbstractDungeon.gridSelectScreen.selectedCards, EUIClassUtils.getField(selectScreen, "hoveredCard"));
        }
    }

    public static void updateDynamicString() {
        if (dynamicString != null) {
            dynamicLabel.setLabel(dynamicString.invoke(AbstractDungeon.gridSelectScreen.selectedCards)).autosize(1f, null);
        }
    }

    public static void renderDynamicString(SpriteBatch sb) {
        if (dynamicString != null) {
            //dynamicLabel.SetText(dynamicString.Invoke(AbstractDungeon.gridSelectScreen.selectedCards));
            dynamicLabel.renderImpl(sb);
        }
    }
}
