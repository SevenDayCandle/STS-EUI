package extendedui.ui.cardFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.CountingPanelCardFilter;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.interfaces.markers.CustomCardPoolModule;
import extendedui.ui.EUIHoverable;
import extendedui.ui.cardFilter.filters.CardRarityPaneFilter;
import extendedui.ui.cardFilter.filters.CardTypePaneFilter;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.RotatingList;

import java.util.ArrayList;

public class CountingPanel extends EUIHoverable implements CustomCardPoolModule
{
    public static final float ICON_SIZE = scale(40);
    protected static final RotatingList<CountingPanelCardFilter> FILTERS = new RotatingList<>(new CardTypePaneFilter(), new CardRarityPaneFilter());
    protected ArrayList<? extends CountingPanelCounter<?>> counters;
    protected ArrayList<AbstractCard> cards;
    protected EUIButton swapButton;
    private long lastFrame;

    public static void register(CountingPanelCardFilter filter)
    {
        FILTERS.add(filter);
    }

    public CountingPanel()
    {
        super(new DraggableHitbox(screenW(0.025f), screenH(0.65f), scale(140), scale(50), false));
        swapButton = new EUIButton(EUIRM.images.swap.texture(), new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, ICON_SIZE, 0))
                .setOnClick(this::swap);
    }

    public void swap()
    {
        FILTERS.next(true);
        reset();
    }

    public void close()
    {
        setActive(false);
    }

    public void open(ArrayList<AbstractCard> cards)
    {
        isActive = EUIConfiguration.showCountingPanel.get() && cards != null;
        this.cards = cards;
        swapButton.setActive(true);

        if (isActive)
        {
            reset();
        }
    }

    public <T extends CountingPanelItem, J, K> void openManual(CountingPanelStats<T, J, K> stats, ActionT1<CountingPanelCounter<T>> onClick, boolean force)
    {
        isActive = EUIConfiguration.showCountingPanel.get() && stats.size() > 0;
        this.cards = null;
        swapButton.setActive(false);

        if (isActive)
        {
            counters = stats.generateCounters(hb, onClick);
        }
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        swapButton.tryUpdate();
        if (counters != null)
        {
            for (CountingPanelCounter<?> c : counters)
            {
                c.tryUpdate();
            }
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        long frame = Gdx.graphics.getFrameId();
        if (frame == lastFrame)
        {
            return;
        }
        hb.render(sb);
        swapButton.tryRender(sb);

        lastFrame = frame;
        if (counters != null)
        {
            for (CountingPanelCounter<?> c : counters)
            {
                c.tryRender(sb);
            }
        }
    }

    protected void reset()
    {
        if (cards != null)
        {
            CountingPanelCardFilter filter = FILTERS.current();
            if (filter != null)
            {
                counters = filter.generateCounters(cards, hb);
            }
        }
    }
}
