package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import extendedui.*;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.STSConfigItem;
import extendedui.interfaces.markers.TourProvider;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

public class EUITourTooltip extends EUITooltip {
    protected static final LinkedList<EUITourTooltip> tutorialQueue = new LinkedList<>();
    protected static final Color TOOLTIP_COLOR = new Color(0.2f, 0.3f, 0.4f, 1f);

    public final Type type;
    public boolean canDismiss = true;
    public boolean canUpdatePos;
    public boolean disableInteract;
    public float x;
    public float y;
    protected Class<? extends AbstractGameAction> waitOnAction;
    protected Hitbox waitOnHitbox;
    protected TourProvider waitOnProvider;
    protected EUIImage linkedImage; // flashes when this tip is up
    private float linkedProgress;

    public static void clearTutorialQueue() {
        tutorialQueue.clear();
    }

    // Disallow nulls because this will lock up the queue
    public static void queueTutorial(EUITourTooltip tip) {
        if (tip != null) {
            tutorialQueue.add(tip);
        }
    }

    public static void queueTutorial(EUITourTooltip... tips) {
        for (EUITourTooltip tip : tips) {
            if (tip != null) {
                tutorialQueue.add(tip);
            }
        }
    }

    public static void queueTutorial(Iterable<? extends EUITourTooltip> tips) {
        for (EUITourTooltip tip : tips) {
            if (tip != null) {
                tutorialQueue.add(tip);
            }
        }
    }

    public static EUITourTooltip getNext() {
        return tutorialQueue.peek();
    }

    public static boolean hasTutorial(EUITourTooltip tip) {
        return tutorialQueue.contains(tip);
    }

    public static boolean isQueueEmpty() {
        return tutorialQueue.isEmpty();
    }

    public static void queueFirstView(STSConfigItem<Boolean> config, Iterable<? extends EUITourTooltip> tips) {
        if (EUIConfiguration.triggerOnFirstView(config)) {
            queueTutorial(tips);
        }
    }

    public static void queueFirstView(STSConfigItem<Boolean> config, EUITourTooltip... tips) {
        if (EUIConfiguration.triggerOnFirstView(config)) {
            queueTutorial(tips);
        }
    }

    public static void render(SpriteBatch sb) {
        EUITourTooltip tip = getNext();
        if (tip != null) {
            tip.update();
            tip.render(sb, tip.x, tip.y, 0);
            if (EUIInputManager.leftClick.isJustPressed() && tip.canDismiss) {
                tutorialQueue.pop();
                tip.onComplete();
            }
            else {
                switch (tip.type) {
                    case Action:
                        if (!EUIGameUtils.inBattle() || tip.waitOnAction == null || tip.waitOnAction.isInstance(AbstractDungeon.actionManager.currentAction)) {
                            tutorialQueue.pop();
                            tip.onComplete();
                        }
                        break;
                    case Hitbox:
                        if (tip.waitOnHitbox == null || (tip.waitOnHitbox.hovered && EUIInputManager.leftClick.isJustPressed())) {
                            tutorialQueue.pop();
                            tip.onComplete();
                        }
                        break;
                    case Provider:
                        if (tip.waitOnProvider == null || tip.waitOnProvider.isComplete()) {
                            tutorialQueue.pop();
                            tip.onComplete();
                        }
                        break;
                }
            }
        }
    }

    public static boolean shouldBlockInteract(Hitbox hb) {
        EUITourTooltip tip = getNext();
        return tip != null && tip.disableInteract && tip.waitOnHitbox != hb;
    }

    public EUITourTooltip(Class<? extends AbstractGameAction> waitOnAction, String title, String description) {
        this(Type.Action, title, description);
        this.waitOnAction = waitOnAction;
        this.canDismiss = false;
    }

    public EUITourTooltip(EUIButton image, String title, String description) {
        this(image.hb, title, description);
        setFlash(image.background);
    }

    public EUITourTooltip(EUIToggle image, String title, String description) {
        this(image.hb, title, description);
        setFlash(image.backgroundImage);
    }

    public EUITourTooltip(EUIImage image, String title, String description) {
        this(image.hb, title, description);
        setFlash(image);
    }

    public EUITourTooltip(Hitbox waitOnHitbox, String title, String description) {
        this(Type.Hitbox, title, description);
        this.waitOnHitbox = waitOnHitbox;
        setPosition(waitOnHitbox.x, waitOnHitbox.y);
    }

    public EUITourTooltip(TourProvider provider, String title, String description) {
        this(Type.Provider, title, description);
        this.waitOnProvider = provider;
    }

    // Should only be called by the other constructors
    protected EUITourTooltip(Type type, String title, String description) {
        super(title, description);
        this.type = type;
    }

    public EUITourTooltip(EUITourTooltip other) {
        super(other);
        this.type = other.type;
        this.waitOnAction = other.waitOnAction;
        this.waitOnProvider = other.waitOnProvider;
        this.waitOnHitbox = other.waitOnHitbox;
    }

    protected void onComplete() {
        if (waitOnProvider != null) {
            waitOnProvider.onComplete();
        }
    }

    public float render(SpriteBatch sb, float x, float y, int index) {
        if (linkedImage != null) {
            if (linkedProgress > 1) {
                linkedImage = null;
            }
            else {
                float maxLen = Math.max(linkedImage.texture.getWidth(), linkedImage.texture.getHeight());
                float lerp = Interpolation.exp5Out.apply(0f, 0.9f + 170f / maxLen, linkedProgress);
                linkedImage.setScale(lerp, lerp);
                linkedImage.renderCentered(sb, EUIRenderHelpers.ShaderMode.Colorize, EUIRenderHelpers.BlendingMode.Glowing, linkedImage.hb, new Color(0.5f, 0.8f, 1f, 1 - linkedProgress));
                linkedProgress += EUI.delta();
            }
        }
        return super.render(sb, x, y, index);
    }

    public void renderTitle(SpriteBatch sb, float x, float y) {
        FontHelper.renderFontLeftTopAligned(sb, headerFont, title, x + TEXT_OFFSET_X, y + HEADER_OFFSET_Y, Settings.GREEN_TEXT_COLOR);
    }

    public void renderBg(SpriteBatch spriteBatch, float x, float y, float h) {
        EUIRenderHelpers.drawColorized(spriteBatch, TOOLTIP_COLOR, sb ->  {
            sb.setColor(Settings.TOP_PANEL_SHADOW_COLOR);
            sb.draw(ImageMaster.KEYWORD_TOP, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, width, BOX_EDGE_H);
            sb.draw(ImageMaster.KEYWORD_BODY, x + SHADOW_DIST_X, y - h - BOX_EDGE_H - SHADOW_DIST_Y, width, h + BOX_EDGE_H);
            sb.draw(ImageMaster.KEYWORD_BOT, x + SHADOW_DIST_X, y - h - BOX_BODY_H - SHADOW_DIST_Y, width, BOX_EDGE_H);
            sb.setColor(TOOLTIP_COLOR);
            sb.draw(ImageMaster.KEYWORD_TOP, x, y, width, BOX_EDGE_H);
            sb.draw(ImageMaster.KEYWORD_BODY, x, y - h - BOX_EDGE_H, width, h + BOX_EDGE_H);
            sb.draw(ImageMaster.KEYWORD_BOT, x, y - h - BOX_BODY_H, width, BOX_EDGE_H);
        });
        if (canDismiss) {
            Texture t = EUIRM.images.proceed.texture();
            spriteBatch.draw(t, x + width - t.getWidth(), y - h - BOX_BODY_H - t.getHeight(), t.getWidth(), t.getHeight());
        }
    }

    public EUITourTooltip setCanDismiss(boolean canDismiss) {
        this.canDismiss = canDismiss;
        return this;
    }

    public EUITourTooltip setCanUpdatePos(boolean canUpdatePos) {
        this.canUpdatePos = canUpdatePos;
        return this;
    }

    public EUITourTooltip setFlash(EUIImage flash) {
        this.linkedImage = new EUIImage(flash);
        return this;
    }

    public EUITourTooltip setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.x += (this.x < Settings.WIDTH * 0.75f - width)
                ? waitOnHitbox != null ? waitOnHitbox.width + Settings.scale * 40f : Settings.scale * 40f
                : -(width);
        y += (y < Settings.HEIGHT * 0.9f) ? (Settings.scale * 40f) : -(Settings.scale * 50f);
        return this;
    }

    public EUITourTooltip setWaitOnProvider(TourProvider provider) {
        this.waitOnProvider = provider;
        return this;
    }

    public void update() {
        if (waitOnHitbox != null && canUpdatePos) {
            setPosition(waitOnHitbox.x, waitOnHitbox.y);
        }
    }

    public enum Type {
        Action,
        Hitbox,
        Provider
    }
}
