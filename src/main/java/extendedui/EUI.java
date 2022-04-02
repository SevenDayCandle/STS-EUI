package extendedui;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.localization.Keyword;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.AbstractScreen;
import extendedui.ui.GUI_Base;
import extendedui.ui.cardFilter.CardKeywordFilters;
import extendedui.ui.cardFilter.CardPoolScreen;
import extendedui.ui.cardFilter.CustomCardLibSortHeader;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.panelitems.CardPoolPanelItem;
import extendedui.ui.tooltips.EUITooltip;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EUI
{
    public static final ArrayList<GUI_Base> BattleSubscribers = new ArrayList<>();
    public static final ArrayList<GUI_Base> Subscribers = new ArrayList<>();
    protected static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> preRenderList = new ConcurrentLinkedQueue<>();
    protected static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> postRenderList = new ConcurrentLinkedQueue<>();
    protected static final ConcurrentLinkedQueue<ActionT1<SpriteBatch>> priorityPostRenderList = new ConcurrentLinkedQueue<>();
    protected static float delta = 0;
    protected static float timer = 0;
    protected static float dropdownX = 0;
    protected static float dropdownY = 0;
    protected static boolean isDragging;
    protected static Hitbox lastHovered;
    protected static Hitbox lastHoveredTemp;
    protected static GUI_Base activeElement;

    public static AbstractScreen CurrentScreen;
    public static CardPoolScreen CardsScreen;
    public static CardKeywordFilters CardFilters;
    public static CustomCardLibSortHeader CustomHeader;

    public static void Initialize()
    {
        CardsScreen = new CardPoolScreen();
        CardFilters = new CardKeywordFilters();
        CustomHeader = new CustomCardLibSortHeader(null);
        BaseMod.addTopPanelItem(new CardPoolPanelItem());

        for (String s : GameDictionary.parentWord.keySet()) {
            final String title = GameDictionary.parentWord.get(s);
            final String description = GameDictionary.keywords.get(s);

            EUITooltip tooltip = EUITooltip.FindByID(title);
            if (tooltip == null) {
                String newTitle = JavaUtils.Capitalize(title.substring(title.indexOf(":") + 1));
                tooltip = new EUITooltip(newTitle, description);
                EUITooltip.RegisterID(title, tooltip);
            }
            EUITooltip.RegisterName(s, tooltip);
        }
    }

    public static void Dispose()
    {
        if (CurrentScreen != null)
        {
            CurrentScreen.Dispose();
            activeElement = null;
        }

        CurrentScreen = null;
        lastHovered = null;
    }

    public static void PreUpdate()
    {
        delta = Gdx.graphics.getRawDeltaTime();
        timer += delta;
        isDragging = false;
        lastHoveredTemp = null;
    }

    public static void Update()
    {
        if (CurrentScreen != null)
        {
            CurrentScreen.Update();
        }

        for (GUI_Base s : BattleSubscribers) {
            s.TryUpdate();
        }
        for (GUI_Base s : Subscribers) {
            s.TryUpdate();
        }

    }

    public static void PostUpdate()
    {
        lastHovered = lastHoveredTemp;
    }

    public static void PreRender(SpriteBatch sb)
    {
        RenderImpl(sb, preRenderList.iterator());
    }

    public static void Render(SpriteBatch sb)
    {
        if (CurrentScreen != null)
        {
            CurrentScreen.Render(sb);
        }

        for (GUI_Base s : Subscribers) {
            s.TryRender(sb);
        }

    }

    public static void PostRender(SpriteBatch sb)
    {
        RenderImpl(sb, postRenderList.iterator());
    }

    public static void PriorityPostRender(SpriteBatch sb)
    {
        RenderImpl(sb, priorityPostRenderList.iterator());
    }

    public static void AddBattleSubscriber(GUI_Base element) {
        BattleSubscribers.add(element);
    }

    public static void AddSubscriber(GUI_Base element) {
        Subscribers.add(element);
    }

    private static void RenderImpl(SpriteBatch sb, Iterator<ActionT1<SpriteBatch>> i)
    {
        while (i.hasNext()) {
            ActionT1<SpriteBatch> toRender = i.next();
            toRender.Invoke(sb);
            i.remove();
        }
    }

    public static boolean IsDragging()
    {
        return isDragging;
    }

    public static boolean TryDragging()
    {
        final boolean drag = !CardCrawlGame.isPopupOpen && (CurrentScreen == null || !isDragging) && (isDragging = true);
        if (drag)
        {
            EUITooltip.CanRenderTooltips(false);
        }

        return drag;
    }

    public static boolean TryHover(Hitbox hitbox)
    {
        if (hitbox != null && hitbox.justHovered && hitbox != lastHovered)
        {
            hitbox.hovered = hitbox.justHovered = false;
            lastHoveredTemp = hitbox;
            return false;
        }

        if (hitbox == null || hitbox.hovered)
        {
            lastHoveredTemp = hitbox;
            return hitbox == lastHovered;
        }

        return false;
    }

    public static boolean TryToggleActiveElement(GUI_Base element, boolean setActive) {
        if (activeElement == null || activeElement == element) {
            activeElement = setActive ? element : null;
            return true;
        }
        return false;
    }

    public static boolean IsInActiveElement(AdvancedHitbox hb) {
        return activeElement == null || activeElement == hb.parentElement;
    }

    public static boolean DoesActiveElementExist() {
        return activeElement != null;
    }

    public static float Time_Sin(float distance, float speed)
    {
        return MathUtils.sin(timer * speed) * distance;
    }

    public static float Time_Cos(float distance, float speed)
    {
        return MathUtils.cos(timer * speed) * distance;
    }

    public static float Time_Multi(float value)
    {
        return timer * value;
    }

    public static float Time()
    {
        return timer;
    }

    public static float Delta()
    {
        return delta;
    }

    public static float Delta(float multiplier)
    {
        return delta * multiplier;
    }

    public static boolean Elapsed(float value)
    {
        return (delta >= value) || (((timer % value) - delta) < 0);
    }

    public static boolean Elapsed25()
    {
        return Elapsed(0.25f);
    }

    public static boolean Elapsed50()
    {
        return Elapsed(0.50f);
    }

    public static boolean Elapsed75()
    {
        return Elapsed(0.75f);
    }

    public static boolean Elapsed100()
    {
        return Elapsed(1.00f);
    }

    public static void AddPreRender(ActionT1<SpriteBatch> toRender)
    {
        preRenderList.add(toRender);
    }

    public static void AddPostRender(ActionT1<SpriteBatch> toRender)
    {
        postRenderList.add(toRender);
    }

    public static void AddPriorityPostRender(ActionT1<SpriteBatch> toRender)
    {
        priorityPostRenderList.add(toRender);
    }
}
