package extendedui.ui.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIRenderHelpers;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.EUIHoverable;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUITextHelper;

public class EUIMainMenuPanelButton extends EUIHoverable {
    public static final float START_Y = -100.0F * Settings.scale;
    protected ActionT0 onClick;
    protected float yMod;
    protected float animTimer = 0.3F;
    protected float animTime = 0.3F;
    protected float uiScale = 1f;
    public Color color;
    public Color pColor;
    public Color dColor;
    public Color gColor;
    public String header;
    public String description;
    public Texture panelImg;
    public Texture portraitImg;

    public EUIMainMenuPanelButton(Texture panelImg, Texture portraitImg, String header, String description, ActionT0 onClick) {
        this(Color.WHITE, Settings.GOLD_COLOR, Settings.CREAM_COLOR, panelImg, portraitImg, header, description, onClick);
    }

    public EUIMainMenuPanelButton(Color pColor, Texture panelImg, Texture portraitImg, String header, String description, ActionT0 onClick) {
        this(pColor, Settings.GOLD_COLOR, Settings.CREAM_COLOR, panelImg, portraitImg, header, description, onClick);
    }

    public EUIMainMenuPanelButton(Color pColor, Color gColor, Color dColor, Texture panelImg, Texture portraitImg, String header, String description, ActionT0 onClick) {
        super(new EUIHitbox(0, Settings.HEIGHT * 0.5f, scale(400), scale(700)));
        this.onClick = onClick;
        this.color = Color.WHITE.cpy();
        this.pColor = pColor.cpy();
        this.dColor = dColor.cpy();
        this.gColor = gColor.cpy();
        this.panelImg = panelImg;
        this.portraitImg = portraitImg;
        this.header = header;
        this.description = description;
    }

    public EUIMainMenuPanelButton animatePanelIn() {
        this.animTimer -= Gdx.graphics.getDeltaTime();
        if (this.animTimer < 0.0F) {
            this.animTimer = 0.0F;
        }

        this.yMod = Interpolation.swingIn.apply(0.0F, START_Y, this.animTimer / this.animTime);
        this.pColor.a = this.dColor.a = this.gColor.a = this.color.a = 1.0F - this.animTimer / this.animTime;
        return this;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        sb.setColor(this.pColor);
        sb.draw(this.panelImg, this.hb.cX - 256.0F, this.hb.cY + this.yMod - 400.0F, 256.0F, 400.0F, 512.0F, 800.0F, this.uiScale * Settings.scale, this.uiScale * Settings.scale, 0.0F, 0, 0, 512, 800, false, false);
        if (this.hb.hovered) {
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, (this.uiScale - 1.0F) * 16.0F));
            sb.setBlendFunction(770, 1);
            sb.draw(ImageMaster.MENU_PANEL_BG_BLUE, this.hb.cX - 256.0F, this.hb.cY + this.yMod - 400.0F, 256.0F, 400.0F, 512.0F, 800.0F, this.uiScale * Settings.scale, this.uiScale * Settings.scale, 0.0F, 0, 0, 512, 800, false, false);
            sb.setBlendFunction(770, 771);
        }

        sb.setColor(this.color);
        sb.draw(this.portraitImg, this.hb.cX - 158.5F, this.hb.cY + this.yMod - 103.0F + 140.0F * Settings.scale, 158.5F, 103.0F, 317.0F, 206.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 317, 206, false, false);

        sb.draw(ImageMaster.MENU_PANEL_FRAME, this.hb.cX - 256.0F, this.hb.cY + this.yMod - 400.0F, 256.0F, 400.0F, 512.0F, 800.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 800, false, false);
        if (FontHelper.getWidth(FontHelper.damageNumberFont, this.header, 0.8F) > 310.0F * Settings.scale) {
            EUITextHelper.renderFontCenteredHeight(sb, FontHelper.damageNumberFont, this.header, this.hb.cX - 138.0F * Settings.scale, this.hb.cY + this.yMod + 294.0F * Settings.scale, 280.0F * Settings.scale, this.gColor, 0.7F);
        }
        else {
            EUITextHelper.renderFontCenteredHeight(sb, FontHelper.damageNumberFont, this.header, this.hb.cX - 153.0F * Settings.scale, this.hb.cY + this.yMod + 294.0F * Settings.scale, 310.0F * Settings.scale, this.gColor, 0.8F);
        }

        EUITextHelper.renderFontCenteredHeight(sb, FontHelper.charDescFont, this.description, this.hb.cX - 153.0F * Settings.scale, this.hb.cY + this.yMod - 130.0F * Settings.scale, 330.0F * Settings.scale, this.dColor);
        this.hb.render(sb);
    }

    public EUIMainMenuPanelButton reposition(float x) {
        this.hb.move(x, Settings.HEIGHT * 0.5f);
        return this;
    }

    public void reset() {
        yMod = 0;
        animTimer = 0.3F;
        animTime = 0.3F;
        uiScale = 1f;
    }

    public void update() {
        this.hb.update();

        if (this.hb.justHovered) {
            CardCrawlGame.sound.playV("UI_HOVER", 0.5F);
        }

        if (this.hb.hovered) {
            this.uiScale = EUIRenderHelpers.lerpSnap(this.uiScale, 1.025F, 12.0F, 0.01F);
            if (InputHelper.justClickedLeft) {
                this.hb.clickStarted = true;
            }
        }
        else {
            this.uiScale = EUIRenderHelpers.lerpSnap(this.uiScale, 1.0F, 7.5F);
        }

        if (this.hb.hovered && CInputActionSet.select.isJustPressed()) {
            this.hb.clicked = true;
        }

        if (this.hb.clicked) {
            this.hb.clicked = false;
            CardCrawlGame.sound.play("DECK_OPEN");
            CardCrawlGame.mainMenuScreen.panelScreen.hide();
            onClick.invoke();
        }

        this.animatePanelIn();
    }
}
