package extendedui.ui.cardFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUIRM;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.CountingPanelFilter;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.ui.EUIHoverable;
import extendedui.utilities.panels.card.CardCostPanelFilter;
import extendedui.utilities.panels.card.CardRarityPanelFilter;
import extendedui.utilities.panels.card.CardTypePanelFilter;
import extendedui.utilities.panels.card.CardUpgradePanelFilter;
import extendedui.utilities.panels.potion.PotionRarityPanelFilter;
import extendedui.utilities.panels.relic.RelicRarityPanelFilter;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUIHeaderlessTooltip;
import extendedui.utilities.PotionInfo;
import extendedui.utilities.RelicInfo;
import extendedui.utilities.RotatingList;

import java.util.ArrayList;

public class CountingPanel<T> extends EUIHoverable {
    private static final RotatingList<CountingPanelFilter<AbstractCard>> CARD_FILTERS = new RotatingList<CountingPanelFilter<AbstractCard>>(
            new CardTypePanelFilter(),
            new CardRarityPanelFilter(),
            new CardUpgradePanelFilter(),
            new CardCostPanelFilter()
    );
    private static final RotatingList<CountingPanelFilter<PotionInfo>> POTION_FILTERS = new RotatingList<CountingPanelFilter<PotionInfo>>(
            new PotionRarityPanelFilter()
    );
    private static final RotatingList<CountingPanelFilter<RelicInfo>> RELIC_FILTERS = new RotatingList<CountingPanelFilter<RelicInfo>>(
            new RelicRarityPanelFilter()
    );
    public static final float ICON_SIZE = scale(40);
    private long lastFrame;
    private ArrayList<? extends CountingPanelCounter<?, T>> counters;
    private ArrayList<? extends T> cards;
    private ActionT1<CountingPanelCounter<? extends CountingPanelItem<T>, T>> onClick;
    private final EUIButton swapButton;
    private final RotatingList<CountingPanelFilter<T>> filters;

    public CountingPanel(RotatingList<CountingPanelFilter<T>> filters) {
        super(new DraggableHitbox(screenW(0.025f), screenH(0.6f), scale(140), scale(50), false));
        this.filters = filters;
        swapButton = new EUIButton(EUIRM.images.swap.texture(), new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, 0, 0))
                .setOnClick(this::swap);
        swapButton.setLabel(new EUILabel(FontHelper.buttonLabelFont, hb, 0.8f, 0.5f, 2.41f, false))
                .setTooltip(new EUIHeaderlessTooltip(EUIRM.strings.misc_countPanelSwitch));
    }

    public static CountingPanel<AbstractCard> counterCards() {
        return new CountingPanel<AbstractCard>(CARD_FILTERS);
    }

    public static CountingPanel<PotionInfo> counterPotions() {
        return new CountingPanel<PotionInfo>(POTION_FILTERS);
    }

    public static CountingPanel<RelicInfo> counterRelics() {
        return new CountingPanel<RelicInfo>(RELIC_FILTERS);
    }

    public static void registerForCard(CountingPanelFilter<AbstractCard> filter) {
        CARD_FILTERS.add(filter);
    }

    public static void registerForPotion(CountingPanelFilter<PotionInfo> filter) {
        POTION_FILTERS.add(filter);
    }

    public static void registerForRelic(CountingPanelFilter<RelicInfo> filter) {
        RELIC_FILTERS.add(filter);
    }

    public void close() {
        setActive(false);
    }

    public <I extends CountingPanelItem<T>, J> void open(ArrayList<? extends T> cards, ActionT1<CountingPanelCounter<? extends CountingPanelItem<T>, T>> onClick) {
        this.cards = cards;
        this.onClick = onClick;
        openImpl(setFor(), onClick);
    }

    public <I extends CountingPanelItem<T>, J> void open(CountingPanelStats<I, J, T> stats, ActionT1<CountingPanelCounter<? extends CountingPanelItem<T>, T>> onClick) {
        this.cards = null;
        this.onClick = onClick;
        openImpl(stats.generateCounters(hb, onClick), onClick);
    }

    private <I extends CountingPanelItem<T>, J> void openImpl(ArrayList<? extends CountingPanelCounter<?, T>> counters, ActionT1<CountingPanelCounter<? extends CountingPanelItem<T>, T>> onClick) {
        this.isActive = EUIConfiguration.showCountingPanel.get() && !counters.isEmpty();

        if (isActive) {
            this.counters = counters;
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        long frame = Gdx.graphics.getFrameId();
        if (frame == lastFrame) {
            return;
        }
        hb.render(sb);
        swapButton.tryRender(sb);

        lastFrame = frame;
        if (counters != null) {
            for (CountingPanelCounter<?,?> c : counters) {
                c.tryRender(sb);
            }
        }
    }

    protected ArrayList<? extends CountingPanelCounter<?, T>> setFor() {
        CountingPanelFilter<T> filter = filters.current();
        if (filter != null) {
            swapButton.setText(filter.getTitle());
            return filter.generateCounters(cards, hb, onClick);
        }
        return null;
    }

    public void swap() {
        filters.next(true);
        counters = setFor();
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        swapButton.tryUpdate();
        if (counters != null) {
            for (CountingPanelCounter<?,?> c : counters) {
                c.tryUpdate();
            }
        }
    }
}
