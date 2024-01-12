package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRM;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;

import java.util.Arrays;
import java.util.Collection;

import static extendedui.EUIRenderHelpers.DARKENED_SCREEN;

public class EUITutorial extends EUIHoverable {
    private static final float ICON_SIZE = 128f * Settings.scale;
    private int page;
    private EUITutorialPage current;
    protected EUISearchableDropdown<EUITutorialPage> tutorials;
    protected EUIButton next;
    protected EUIButton prev;
    protected EUILabel description;
    protected EUIImage backgroundImage;

    public EUITutorial(EUITutorialPage... descriptions) {
        this(Arrays.asList(descriptions));
    }

    public EUITutorial(Collection<EUITutorialPage> descriptions) {
        this(defaultHitbox(), EUIRM.images.greySquare.texture(), descriptions);
    }

    public EUITutorial(EUIHitbox hb, Texture backgroundTexture, Collection<EUITutorialPage> tutorials) {
        super(hb);
        this.backgroundImage = new EUIBorderedImage(backgroundTexture, hb);

        this.tutorials = (EUISearchableDropdown<EUITutorialPage>) new EUISearchableDropdown<EUITutorialPage>(new RelativeHitbox(hb, hb.width, scale(53), hb.width * 0.5f, hb.height - scale(120)), p -> p.title)
                .setFontForButton(FontHelper.tipHeaderFont, 1f)
                .setOnOpenOrClose(isOpen -> {
                    CardCrawlGame.isPopupOpen = isOpen;
                })
                .setCanAutosizeButton(true);
        this.tutorials.setOnChange(selectedSeries -> {
                    this.tutorials.forceClose();
                    if (selectedSeries.size() > 0) {
                        setPage(selectedSeries.get(0));
                    }
                }
        );
        this.description = new EUILabel(EUIFontHelper.tooltipFont,
                new RelativeHitbox(hb, hb.width * 0.8f, hb.height, hb.width * 0.1f, hb.height - scale(200)))
                .setAlignment(0.5f, 0.5f, true)
                .setSmartText(true, false);

        this.tutorials.setItems(tutorials);
        for (EUITutorialPage page : tutorials) {
            page.setTutorial(this);
        }
        changePage(0);

        this.next = new EUIButton(ImageMaster.POPUP_ARROW,
                new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, hb.width * 1.1f, hb.height * 0.5f))
                .setOnClick(() -> changePage(page >= this.tutorials.size() - 1 ? 0 : page + 1));
        this.next.background.setFlipping(true, false);

        this.prev = new EUIButton(ImageMaster.POPUP_ARROW,
                new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, hb.width * -0.1f, hb.height * 0.5f))
                .setOnClick(() -> changePage(page <= 0 ? this.tutorials.size() - 1 : page - 1));

        this.next.setActive(this.tutorials.size() > 1);
        this.prev.setActive(this.tutorials.size() > 1);
    }

    public EUITutorial(EUIHitbox hb, Texture backgroundTexture, EUITutorialPage... descriptions) {
        this(hb, backgroundTexture, Arrays.asList(descriptions));
    }

    protected static EUIHitbox defaultHitbox() {
        float x = scale(1000);
        float y = scale(700);
        return new EUIHitbox(Settings.WIDTH * 0.5f - x / 2, Settings.HEIGHT * 0.5f - y / 2, x, y);
    }

    protected void changePage(int newPage) {
        this.page = newPage;
        this.tutorials.setSelectedIndex(newPage);
    }

    public void close() {
        this.tutorials.forceClose();
    }

    public boolean isHovered() {
        return hb.hovered || next.hb.hovered || prev.hb.hovered || tutorials.areAnyItemsHovered() || current != null && current.isHovered();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        sb.setColor(DARKENED_SCREEN);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        sb.setColor(Color.WHITE);
        this.backgroundImage.tryRender(sb);

        this.tutorials.tryRender(sb);
        this.description.tryRender(sb);
        this.next.tryRender(sb);
        this.prev.tryRender(sb);
        if (current != null) {
            current.tryRender(sb);
        }
    }

    protected void setPage(EUITutorialPage page) {
        this.current = page;
        this.page = this.tutorials.indexOf(page);
        this.description.setLabel(current != null ? current.description : "");
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        this.tutorials.tryUpdate();
        this.description.updateImpl();
        this.next.tryUpdate();
        this.prev.tryUpdate();
        if (current != null) {
            current.tryUpdate();
        }
    }
}
