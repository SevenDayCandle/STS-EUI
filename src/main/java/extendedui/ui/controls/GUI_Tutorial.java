package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.ui.FtueTip;
import com.megacrit.cardcrawl.ui.MultiPageFtue;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUIRM;
import extendedui.ui.GUI_Hoverable;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static com.megacrit.cardcrawl.ui.MultiPageFtue.LABEL;
import static extendedui.EUIRenderHelpers.DARKENED_SCREEN;

public class GUI_Tutorial extends GUI_Hoverable
{
    private static final float ICON_SIZE = 96f * Settings.scale;
    protected ArrayList<String> descriptions = new ArrayList<>();
    protected ArrayList<ActionT1<SpriteBatch>> postRenders = new ArrayList<>();
    protected GUI_Button next;
    protected GUI_Button prev;
    protected GUI_Label header;
    protected GUI_Label sublabel;
    protected GUI_Label description;
    protected GUI_Image backgroundImage;
    private int page;
    private ActionT1<SpriteBatch> renderFunction;

    public GUI_Tutorial(String headerText, String... descriptions) {
        this(headerText, Arrays.asList(descriptions));
    }

    public GUI_Tutorial(String headerText, Collection<String> descriptions)
    {
        this(new AdvancedHitbox(Settings.WIDTH / 2.0F - 675.0F, Settings.OPTION_Y - 360.0F, 1350F, 720F), EUIRM.Images.Panel_Large.Texture(), headerText, descriptions);
    }

    public GUI_Tutorial(AdvancedHitbox hb, Texture backgroundTexture, String headerText, Collection<String> descriptions)
    {
        super(hb);
        this.backgroundImage = new GUI_Image(backgroundTexture, hb);

        this.header = new GUI_Label(FontHelper.buttonLabelFont,
                new RelativeHitbox(hb, hb.width, hb.height, hb.width * 0.5f, hb.height * 0.85f, false))
                .SetAlignment(0.5f,0.5f,false)
                .SetText(headerText);
        this.sublabel = new GUI_Label(EUIFontHelper.CardTitleFont_Small,
                new RelativeHitbox(hb, hb.width, hb.height, hb.width * 0.5f, hb.height * 0.75f, false))
                .SetFontScale(0.85f)
                .SetAlignment(0.5f,0.5f,false);
        this.description = new GUI_Label(FontHelper.tipBodyFont,
                new RelativeHitbox(hb, hb.width * 0.8f, hb.height, hb.width * 0.1f, hb.height * 0.65f, false))
                .SetAlignment(0.5f,0.5f,true)
                .SetSmartText(true, false);

        this.descriptions.addAll(descriptions);
        ChangePage(0);

        this.next = new GUI_Button(ImageMaster.POPUP_ARROW,
                new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, hb.width * 1.1f, hb.height * 0.5f, false))
                .SetOnClick(() -> {
                    ChangePage(page >= descriptions.size() - 1 ? 0 : page + 1);
                });
        this.next.background.SetFlipping(true, false);

        this.prev = new GUI_Button(ImageMaster.POPUP_ARROW,
                new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, hb.width * -0.1f, hb.height * 0.5f, false))
                .SetOnClick(() -> {
                    ChangePage(page <= 0 ? descriptions.size() - 1 : page - 1);
                });

        this.next.SetActive(this.descriptions.size() > 1);
        this.prev.SetActive(this.descriptions.size() > 1);
    }

    @SafeVarargs
    public final GUI_Tutorial SetPostRenders(ActionT1<SpriteBatch>... postRenders) {
        return SetPostRenders(Arrays.asList(postRenders));
    }

    public GUI_Tutorial SetPostRenders(Collection<ActionT1<SpriteBatch>> postRenders) {
        this.postRenders.addAll(postRenders);
        renderFunction = page < postRenders.size() ? this.postRenders.get(page) : null;
        return this;
    }

    protected void ChangePage(int newPage) {
        this.page = newPage;
        this.description.SetText(page < descriptions.size() ? descriptions.get(page) : "");
        this.sublabel.SetText(LABEL[3] + (page + 1) + "/" + descriptions.size() + ")");
        renderFunction = page < postRenders.size() ? postRenders.get(page) : null;

    }

    @Override
    public void Update()
    {
        super.Update();
        this.header.TryUpdate();
        this.sublabel.TryUpdate();
        this.description.TryUpdate();
        this.next.TryUpdate();
        this.prev.TryUpdate();
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        sb.setColor(DARKENED_SCREEN);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, 0.0F, (float) Settings.WIDTH, (float)Settings.HEIGHT);
        sb.setColor(Color.WHITE);
        this.backgroundImage.TryRender(sb);

        this.header.TryRender(sb);
        this.sublabel.TryRender(sb);
        this.description.TryRender(sb);
        this.next.TryRender(sb);
        this.prev.TryRender(sb);
        if (renderFunction != null) {
            renderFunction.Invoke(sb);
        }
    }
}
