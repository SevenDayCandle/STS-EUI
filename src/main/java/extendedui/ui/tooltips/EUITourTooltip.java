package extendedui.ui.tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import extendedui.*;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.STSConfigItem;
import extendedui.interfaces.markers.TourProvider;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;

import java.util.ArrayList;

public class EUITourTooltip extends EUITooltip {
    private static final ArrayList<EUITourTooltip> tutorialQueue = new ArrayList<>();
    private static final Color TOOLTIP_COLOR = new Color(0.2f, 0.3f, 0.4f, 1f);
    private static EUIImage prevImage;
    private static EUIImage xImage;
    private static int queuePos;

    public final Type type;
    private float linkedProgress;
    protected Class<? extends AbstractGameAction> waitOnAction;
    protected Hitbox waitOnHitbox;
    protected TourProvider waitOnProvider;
    protected EUIImage linkedImage; // flashes when this tip is up
    protected AbstractDungeon.CurrentScreen dungeonScreen;
    protected MainMenuScreen.CurScreen mainMenuScreen;
    public boolean canDismiss = true;
    public boolean canTerminate = true;
    public boolean canUpdatePos;
    public boolean disableInteract;
    public float x;
    public float y;

    public EUITourTooltip(Class<? extends AbstractGameAction> waitOnAction, String title, String description) {
        this(Type.Action, title, description);
        this.waitOnAction = waitOnAction;
        this.dungeonScreen = AbstractDungeon.CurrentScreen.NONE;
        this.canDismiss = false;
        this.canTerminate = false;
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

    public static void clearTutorialQueue() {
        tutorialQueue.clear();
        queuePos = 0;
    }

    public static EUITourTooltip getCur() {
        return queuePos < tutorialQueue.size() ? tutorialQueue.get(queuePos) : null;
    }

    protected static EUIImage getPrevImage() {
        if (prevImage == null) {
            Texture tex = EUIRM.images.previous.texture();
            prevImage = new EUIImage(tex, new EUIHitbox(0, 0, tex.getWidth(), tex.getHeight()));
        }
        return prevImage;
    }

    protected static EUIImage getXImage() {
        if (xImage == null) {
            xImage = new EUIImage(EUIRM.images.xButton.texture(), new EUIHitbox(0, 0, EUIBase.scale(32), EUIBase.scale(32)));
        }
        return xImage;
    }

    public static boolean hasTutorial(EUITourTooltip tip) {
        return tutorialQueue.contains(tip);
    }

    public static boolean isQueueEmpty() {
        return tutorialQueue.isEmpty();
    }

    public static void moveQueueBackward() {
        if (queuePos > 0) {
            queuePos--;
        }
    }

    public static void moveQueueForward(EUITourTooltip tip) {
        queuePos++;
        tip.onComplete();
        if (getCur() == null) {
            clearTutorialQueue();
        }
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

    public static void queueTutorial(AbstractDungeon.CurrentScreen screen, EUITourTooltip tip) {
        if (tip != null) {
            tutorialQueue.add(tip.setRenderScreen(screen));
        }
    }

    public static void queueTutorial(AbstractDungeon.CurrentScreen screen, EUITourTooltip... tips) {
        for (EUITourTooltip tip : tips) {
            if (tip != null) {
                tutorialQueue.add(tip.setRenderScreen(screen));
            }
        }
    }

    public static void queueTutorial(AbstractDungeon.CurrentScreen screen, Iterable<? extends EUITourTooltip> tips) {
        for (EUITourTooltip tip : tips) {
            if (tip != null) {
                tutorialQueue.add(tip.setRenderScreen(screen));
            }
        }
    }

    public static void queueTutorial(MainMenuScreen.CurScreen screen, EUITourTooltip tip) {
        if (tip != null) {
            tutorialQueue.add(tip.setRenderScreen(screen));
        }
    }

    public static void queueTutorial(MainMenuScreen.CurScreen screen, EUITourTooltip... tips) {
        for (EUITourTooltip tip : tips) {
            if (tip != null) {
                tutorialQueue.add(tip.setRenderScreen(screen));
            }
        }
    }

    public static void queueTutorial(MainMenuScreen.CurScreen screen, Iterable<? extends EUITourTooltip> tips) {
        for (EUITourTooltip tip : tips) {
            if (tip != null) {
                tutorialQueue.add(tip.setRenderScreen(screen));
            }
        }
    }

    public static boolean shouldBlockInteract(Hitbox hb) {
        EUITourTooltip tip = getCur();
        return tip != null && tip.disableInteract && tip.waitOnHitbox != hb;
    }

    public static void updateAndRender(SpriteBatch sb) {
        EUITourTooltip tip = getCur();
        if (tip != null && tip.canShowForScreen()) {
            tip.render(sb, tip.x, tip.y, 0);
            if (EUIInputManager.leftClick.isJustPressed() && tip.canDismiss) {
                if (getPrevImage().hb.hovered) {
                    prevImage.hb.unhover();
                    moveQueueBackward();
                }
                else if (getXImage().hb.hovered) {
                    xImage.hb.unhover();
                    clearTutorialQueue();
                }
                else {
                    moveQueueForward(tip);
                }
            }
            else {
                switch (tip.type) {
                    case Action:
                        if (!EUIGameUtils.inBattle() || tip.waitOnAction == null || tip.waitOnAction.isInstance(AbstractDungeon.actionManager.currentAction)) {
                            moveQueueForward(tip);
                        }
                        break;
                    case Hitbox:
                        if (tip.waitOnHitbox == null || (tip.waitOnHitbox.hovered && EUIInputManager.leftClick.isJustPressed())) {
                            moveQueueForward(tip);
                        }
                        break;
                    case Provider:
                        if (tip.waitOnProvider == null || tip.waitOnProvider.isComplete()) {
                            moveQueueForward(tip);
                        }
                        break;
                }
            }
        }
    }

    public boolean canShowForScreen() {
        return (dungeonScreen == null || AbstractDungeon.screen == dungeonScreen)
                && (mainMenuScreen == null || (CardCrawlGame.mainMenuScreen != null && CardCrawlGame.mainMenuScreen.screen == mainMenuScreen));
    }

    protected void onComplete() {
        if (waitOnProvider != null) {
            waitOnProvider.onComplete();
        }
    }

    // Also handles updating
    @Override
    public float render(SpriteBatch sb, float x, float y, int index) {
        verifyFonts();
        final float h = height();

        if (waitOnHitbox != null && canUpdatePos) {
            setPosition(waitOnHitbox.x, waitOnHitbox.y);
        }

        if (linkedImage != null) {
            if (linkedProgress > 1) {
                linkedImage = null;
            }
            else {
                float maxLen = Math.max(linkedImage.texture.getWidth(), linkedImage.texture.getHeight());
                float lerp = Interpolation.exp5Out.apply(0f, 0.9f + 190f / maxLen, linkedProgress);
                linkedImage.setScale(lerp, lerp);
                linkedImage.renderCentered(sb, EUIRenderHelpers.ShaderMode.Colorize, EUIRenderHelpers.BlendingMode.Glowing, linkedImage.hb, new Color(0.5f, 0.8f, 1f, 1 - linkedProgress));
                linkedProgress += EUI.delta();
            }
        }

        renderBg(sb, Settings.TOP_PANEL_SHADOW_COLOR, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, h);
        EUIRenderHelpers.ShaderMode.Colorize.draw(sb, s -> renderBg(s, TOOLTIP_COLOR, x, y, h));
        renderTitle(sb, x, y);
        renderSubtext(sb, x, y);
        if (canDismiss) {
            Texture t = EUIRM.images.proceed.texture();
            sb.setColor(Color.WHITE);
            float yLoc = y - h - BOX_BODY_H - t.getHeight();
            sb.draw(t, x + width - t.getWidth(), yLoc, t.getWidth(), t.getHeight());
            if (queuePos > 0) {
                EUITourTooltip prev = tutorialQueue.get(queuePos - 1);
                // Do not allow going back to an undismissable tutorial
                if (prev != null && prev.canDismiss) {
                    EUIImage prevButton = getPrevImage();
                    prevButton.hb.translate(x, yLoc);
                    prevButton.updateImpl();
                    prevButton.render(sb);
                }
            }
        }
        if (canTerminate) {
            EUIImage xButton = getXImage();
            xButton.hb.translate(x + width, y);
            xButton.updateImpl();
            xButton.render(sb);
        }

        float yOff = y + BODY_OFFSET_Y;
        yOff += renderSubheader(sb, x, yOff);
        renderDescription(sb, x, yOff);

        return h;
    }

    @Override
    public void renderTitle(SpriteBatch sb, float x, float y) {
        FontHelper.renderFontLeftTopAligned(sb, headerFont, title, x + TEXT_OFFSET_X, y + HEADER_OFFSET_Y, Settings.GREEN_TEXT_COLOR);
    }

    public EUITourTooltip setCanDismiss(boolean canDismiss) {
        this.canDismiss = canDismiss;
        return this;
    }

    public EUITourTooltip setCanTerminate(boolean canDismiss) {
        this.canTerminate = canDismiss;
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

    public EUITourTooltip setRenderScreen(AbstractDungeon.CurrentScreen screen) {
        dungeonScreen = screen;
        mainMenuScreen = null;
        return this;
    }

    public EUITourTooltip setRenderScreen(MainMenuScreen.CurScreen screen) {
        mainMenuScreen = screen;
        dungeonScreen = null;
        return this;
    }

    public EUITourTooltip setRenderScreenToCurrent() {
        if (CardCrawlGame.mainMenuScreen != null) {
            mainMenuScreen = CardCrawlGame.mainMenuScreen.screen;
            dungeonScreen = null;
        }
        else {
            dungeonScreen = AbstractDungeon.screen;
            mainMenuScreen = null;
        }
        return this;
    }

    public EUITourTooltip setWaitOnProvider(TourProvider provider) {
        this.waitOnProvider = provider;
        return this;
    }

    public enum Type {
        Action,
        Hitbox,
        Provider
    }
}
