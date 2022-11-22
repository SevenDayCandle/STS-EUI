package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUIRM;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static com.megacrit.cardcrawl.ui.MultiPageFtue.LABEL;
import static extendedui.EUIRenderHelpers.DARKENED_SCREEN;

public class EUITutorial extends EUIHoverable
{
    private static final float ICON_SIZE = 96f * Settings.scale;
    protected ArrayList<String> descriptions = new ArrayList<>();
    protected ArrayList<ActionT1<SpriteBatch>> postRenders = new ArrayList<>();
    protected EUIButton next;
    protected EUIButton prev;
    protected EUILabel header;
    protected EUILabel sublabel;
    protected EUILabel description;
    protected EUIImage backgroundImage;
    private int page;
    private ActionT1<SpriteBatch> renderFunction;

    public EUITutorial(String headerText, String... descriptions) {
        this(headerText, Arrays.asList(descriptions));
    }

    public EUITutorial(String headerText, Collection<String> descriptions)
    {
        this(new AdvancedHitbox(Settings.WIDTH / 2.0F - 675.0F, Settings.OPTION_Y - 360.0F, 1350F, 720F), EUIRM.Images.panelLarge.texture(), headerText, descriptions);
    }

    public EUITutorial(AdvancedHitbox hb, Texture backgroundTexture, String headerText, Collection<String> descriptions)
    {
        super(hb);
        this.backgroundImage = new EUIImage(backgroundTexture, hb);

        this.header = new EUILabel(FontHelper.buttonLabelFont,
                new RelativeHitbox(hb, hb.width, hb.height, hb.width * 0.5f, hb.height * 0.85f, false))
                .setAlignment(0.5f,0.5f,false)
                .setLabel(headerText);
        this.sublabel = new EUILabel(EUIFontHelper.CardTitleFont_Small,
                new RelativeHitbox(hb, hb.width, hb.height, hb.width * 0.5f, hb.height * 0.75f, false))
                .setFontScale(0.85f)
                .setAlignment(0.5f,0.5f,false);
        this.description = new EUILabel(EUIFontHelper.CardTooltipFont,
                new RelativeHitbox(hb, hb.width * 0.8f, hb.height, hb.width * 0.1f, hb.height * 0.65f, false))
                .setAlignment(0.5f,0.5f,true)
                .setSmartText(true, false);

        this.descriptions.addAll(descriptions);
        changePage(0);

        this.next = new EUIButton(ImageMaster.POPUP_ARROW,
                new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, hb.width * 1.1f, hb.height * 0.5f, false))
                .setOnClick(() -> {
                    changePage(page >= descriptions.size() - 1 ? 0 : page + 1);
                });
        this.next.background.setFlipping(true, false);

        this.prev = new EUIButton(ImageMaster.POPUP_ARROW,
                new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, hb.width * -0.1f, hb.height * 0.5f, false))
                .setOnClick(() -> {
                    changePage(page <= 0 ? descriptions.size() - 1 : page - 1);
                });

        this.next.setActive(this.descriptions.size() > 1);
        this.prev.setActive(this.descriptions.size() > 1);
    }

    @SafeVarargs
    public final EUITutorial setPostRenders(ActionT1<SpriteBatch>... postRenders) {
        return setPostRenders(Arrays.asList(postRenders));
    }

    public EUITutorial setPostRenders(Collection<ActionT1<SpriteBatch>> postRenders) {
        this.postRenders.addAll(postRenders);
        renderFunction = page < postRenders.size() ? this.postRenders.get(page) : null;
        return this;
    }

    protected void changePage(int newPage) {
        this.page = newPage;
        this.description.setLabel(page < descriptions.size() ? descriptions.get(page) : "");
        this.sublabel.setLabel(LABEL[3] + (page + 1) + "/" + descriptions.size() + ")");
        renderFunction = page < postRenders.size() ? postRenders.get(page) : null;

    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        this.header.tryUpdate();
        this.sublabel.tryUpdate();
        this.description.tryUpdate();
        this.next.tryUpdate();
        this.prev.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        sb.setColor(DARKENED_SCREEN);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float)Settings.HEIGHT);
        sb.setColor(Color.WHITE);
        this.backgroundImage.tryRender(sb);

        this.header.tryRender(sb);
        this.sublabel.tryRender(sb);
        this.description.tryRender(sb);
        this.next.tryRender(sb);
        this.prev.tryRender(sb);
        if (renderFunction != null) {
            renderFunction.invoke(sb);
        }
    }
}
