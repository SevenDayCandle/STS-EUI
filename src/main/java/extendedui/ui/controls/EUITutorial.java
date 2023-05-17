package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
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
    private static final float ICON_SIZE = 96f * Settings.scale;
    protected EUISearchableDropdown<EUITutorialPage> tutorials;
    protected EUIButton next;
    protected EUIButton prev;
    protected EUILabel description;
    protected EUIImage backgroundImage;
    private int page;
    private EUITutorialPage current;

    public EUITutorial(EUITutorialPage... descriptions) {
        this(Arrays.asList(descriptions));
    }

    public EUITutorial(Collection<EUITutorialPage> descriptions) {
        this(new EUIHitbox(Settings.WIDTH / 2.0F - 675.0F, Settings.OPTION_Y - 360.0F, 1350, 720), EUIRM.images.greySquare.texture(), descriptions);
    }

    public EUITutorial(EUIHitbox hb, Texture backgroundTexture, Collection<EUITutorialPage> tutorials) {
        super(hb);
        this.backgroundImage = new EUIBorderedImage(backgroundTexture, hb);

        this.tutorials = (EUISearchableDropdown<EUITutorialPage>) new EUISearchableDropdown<EUITutorialPage>(new RelativeHitbox(hb, hb.width, scale(53), hb.width * 0.5f, hb.height * 0.77f), p -> p.title)
                .setFontForButton(EUIFontHelper.cardTooltipTitleFontNormal, 1f)
                .setOnOpenOrClose(isOpen -> {
                    CardCrawlGame.isPopupOpen = this.isActive;
                })
                .setCanAutosizeButton(true);
        this.tutorials.setOnChange(selectedSeries -> {
                    this.tutorials.forceClose();
                    if (selectedSeries.size() > 0) {
                        setPage(selectedSeries.get(0));
                    }
                }
        );
        this.description = new EUILabel(EUIFontHelper.cardTooltipFont,
                new RelativeHitbox(hb, hb.width * 0.8f, hb.height, hb.width * 0.1f, hb.height * 0.65f))
                .setAlignment(0.5f, 0.5f, true)
                .setSmartText(true, false);

        this.tutorials.setItems(tutorials);
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

    protected void setPage(EUITutorialPage page) {
        this.current = page;
        this.description.setLabel(current != null ? current.description : "");
    }

    protected void changePage(int newPage) {
        this.page = newPage;
        this.tutorials.setSelectedIndex(newPage);
    }

    public EUITutorial(EUIHitbox hb, Texture backgroundTexture, EUITutorialPage... descriptions) {
        this(hb, backgroundTexture, Arrays.asList(descriptions));
    }

    public void close() {
        this.tutorials.forceClose();
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

    @Override
    public void updateImpl() {
        super.updateImpl();
        this.tutorials.tryUpdate();
        this.description.tryUpdate();
        this.next.tryUpdate();
        this.prev.tryUpdate();
        if (current != null) {
            current.tryUpdate();
        }
    }
}
