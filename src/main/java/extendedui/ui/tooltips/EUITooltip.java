package extendedui.ui.tooltips;

import basemod.ReflectionHacks;
import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import extendedui.*;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.EUIHotkeys;
import extendedui.interfaces.markers.IntentProvider;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.text.EUITextHelper;
import extendedui.utilities.ColoredString;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.RotatingList;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class EUITooltip {
    private static final ArrayList<String> EMPTY_LIST = new ArrayList<>();
    private static final ArrayList<EUITooltip> TOOLTIPS = new ArrayList<>();
    private static final RotatingList<EUIPreview> PREVIEWS = new RotatingList<>();
    private static final Vector2 genericTipPos = new Vector2(0, 0);
    protected static final float BORDER_SIZE = Settings.scale * 32.0F;
    protected static final float TIP_OFFSET_L_X = -380.0F * Settings.scale;
    protected static final float TIP_OFFSET_R_X = 20.0F * Settings.scale;
    protected static final float TIP_X_THRESHOLD = (Settings.WIDTH * 0.5f); // 1544.0F * Settings.scale;
    protected static final Color BASE_COLOR = new Color(1f, 0.9725f, 0.8745f, 1f);
    protected static final float BODY_OFFSET_Y = -20f * Settings.scale;
    protected static final float BODY_TEXT_WIDTH = 320f * Settings.scale;
    protected static final float BOX_BODY_H = 64f * Settings.scale;
    protected static final float BOX_EDGE_H = 32f * Settings.scale;
    protected static final float BOX_W = 360f * Settings.scale;
    protected static final float CARD_TIP_PAD = 12f * Settings.scale;
    protected static final float HEADER_OFFSET_Y = 12f * Settings.scale;
    protected static final float ORB_OFFSET_Y = -8f * Settings.scale;
    protected static final float SHADOW_DIST_X = 9f * Settings.scale;
    protected static final float SHADOW_DIST_Y = 14f * Settings.scale;
    protected static final float TEXT_OFFSET_X = 22f * Settings.scale;
    protected static final float TIP_DESC_LINE_SPACING = 26f * Settings.scale;
    protected static final float TIP_Y_LIMIT = Settings.HEIGHT * 0.97f;
    public static final float BOX_RENDER_OFFSET = BOX_EDGE_H * 3.15f;
    private static Object provider;
    private static Object lastProvider;
    public static Color TIP_BUFF = Color.WHITE;
    public static Color TIP_DEBUFF = Color.WHITE;
    protected int currentDesc;
    protected Float lastSubHeaderHeight;
    protected Float lastTextHeight;
    protected Float lastHeight;
    public BitmapFont headerFont = FontHelper.tipHeaderFont;
    public BitmapFont descriptionFont = EUIFontHelper.tooltipFont;
    public ColoredString subHeader;
    public ColoredString subText;
    public List<EUITooltip> children;
    public String ID;
    public String title;
    public String description;
    public float width = BOX_W;

    public EUITooltip(String title) {
        this(title, EUIUtils.EMPTY_STRING);
    }

    public EUITooltip(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public EUITooltip(EUITooltip other) {
        this.title = other.title;
        this.description = other.description;
        this.subHeader = other.subHeader;
    }

    protected static void addGenericTips(Iterable<PowerTip> vanillaTips) {
        if (provider instanceof TooltipProvider) {
            for (EUITooltip tip : ((TooltipProvider) provider).getTipsForRender()) {
                if (tip.isRenderable()) {
                    TOOLTIPS.add(tip);
                }
            }
        }
        else {
            for (PowerTip sk : vanillaTips) {
                EUIKeywordTooltip tip = EUIKeywordTooltip.findByName(StringUtils.lowerCase(sk.header));
                if (tip != null) {
                    if (tip.isRenderable()) {
                        TOOLTIPS.add(tip);
                    }
                }
                else {
                    TOOLTIPS.add(new EUITooltip(sk.header, sk.body));
                }
            }
        }
    }

    public static void blockTooltips() {
        clearVanillaTips();
        TOOLTIPS.clear();
        PREVIEWS.clear();
        provider = null;
        lastProvider = null;
    }

    public static float calculateAdditionalOffset(ArrayList<EUITooltip> tips, float hb_cY) {
        return tips.isEmpty() ? 0f : (1f - hb_cY / (float) Settings.HEIGHT) * getTallestOffset(tips) - (tips.get(0).getTotalHeight()) * 0.5f;
    }

    // Do not render powers with no name or invisible powers (stslib feature)
    public static boolean canRenderPower(AbstractPower po) {
        return po.name != null && (!EUI.isStsLib() || !(po instanceof InvisiblePower));
    }

    public static boolean canRenderTooltips() {
        return !EUIClassUtils.getFieldStatic(TipHelper.class, "renderedTipThisFrame", Boolean.class);
    }

    private static void clearVanillaTips() {
        ReflectionHacks.setPrivateStatic(TipHelper.class, "renderedTipThisFrame", true);
        ReflectionHacks.setPrivateStatic(TipHelper.class, "BODY", null);
        ReflectionHacks.setPrivateStatic(TipHelper.class, "HEADER", null);
        ReflectionHacks.setPrivateStatic(TipHelper.class, "card", null);
        ReflectionHacks.setPrivateStatic(TipHelper.class, "KEYWORDS", EMPTY_LIST);
        ReflectionHacks.setPrivateStatic(TipHelper.class, "POWER_TIPS", EMPTY_LIST);
    }

    public static void fillProviderPreview() {
        fillProviderPreview(provider);
    }

    public static void fillProviderPreview(Object provider) {
        PREVIEWS.clear();
        if (provider instanceof TooltipProvider) {
            ((TooltipProvider) provider).fillPreviews(PREVIEWS);
        }
    }

    public static EUIKeywordTooltip fromMonsterIntent(AbstractMonster monster) {
        PowerTip tip = ReflectionHacks.getPrivate(monster, AbstractMonster.class, "intentTip");
        return tip != null ? fromPowerTip(tip) : null;
    }

    public static EUIKeywordTooltip fromPowerTip(PowerTip tip) {
        if (StringUtils.isEmpty(tip.header)) {
            return null;
        }
        EUIKeywordTooltip newTip = new EUIKeywordTooltip(tip.header, tip.body);
        if (tip.imgRegion != null) {
            newTip.icon = tip.imgRegion;
        }
        else if (tip.img != null) {
            newTip.icon = new TextureRegion(tip.img);
        }
        return newTip;
    }

    public static float getTallestOffset(ArrayList<EUITooltip> tips) {
        float currentOffset = 0f;
        float maxOffset = 0f;

        for (EUITooltip p : tips) {
            float offsetChange = p.getTotalHeight();
            if ((currentOffset + offsetChange) >= (float) Settings.HEIGHT * 0.7F) {
                currentOffset = 0f;
            }

            currentOffset += offsetChange;
            if (currentOffset > maxOffset) {
                maxOffset = currentOffset;
            }
        }

        return maxOffset;
    }

    public static void queueTooltip(EUITooltip tooltip) {
        float x = InputHelper.mX;
        float y = InputHelper.mY;
        x += (x < Settings.WIDTH * 0.75f) ? (Settings.scale * 40f) : -(tooltip.width + (Settings.scale * 40f));
        y += (y < Settings.HEIGHT * 0.9f) ? (Settings.scale * 40f) : -(Settings.scale * 50f);
        queueTooltip(tooltip, x, y);
    }

    public static void queueTooltip(EUITooltip tooltip, float x, float y) {
        if (tryQueue()) {
            if (lastProvider != tooltip) {
                lastProvider = tooltip;
                provider = null;
                TOOLTIPS.clear();
                PREVIEWS.clear();
                TOOLTIPS.add(tooltip);
                if (tooltip.children != null) {
                    TOOLTIPS.addAll(tooltip.children);
                }
            }

            genericTipPos.x = x;
            genericTipPos.y = y;
            EUI.addPriorityPostRender(EUITooltip::renderGeneric);
        }
    }

    public static void queueTooltips(EUITooltip... tips) {
        queueTooltips(Arrays.asList(tips));
    }

    public static void queueTooltips(Collection<? extends EUITooltip> tips) {
        float maxWidth = !tips.isEmpty() ? EUIUtils.max(tips, tip -> tip.width) : BOX_W;
        float estHeight = EUIUtils.sum(tips, EUITooltip::height);
        float x = InputHelper.mX;
        float y = InputHelper.mY;
        x += (x < Settings.WIDTH * 0.75f) ? (Settings.scale * 40f) : -(maxWidth + (Settings.scale * 40f));
        y += (y < Settings.HEIGHT * 0.9f) ? (Settings.scale * 40f) : -(Settings.scale * 50f);
        if (y - estHeight < 0) {
            y += estHeight;
        }

        queueTooltips(tips, x, y);
    }

    public static void queueTooltips(Collection<? extends EUITooltip> tips, float x, float y) {
        if (tryQueue()) {
            if (lastProvider != tips) {
                lastProvider = tips;
                provider = null;
                TOOLTIPS.clear();
                PREVIEWS.clear();
                TOOLTIPS.addAll(tips);
            }
            genericTipPos.x = x;
            genericTipPos.y = y;
            EUI.addPriorityPostRender(EUITooltip::renderGeneric);
        }
    }

    public static void queueTooltips(AbstractCreature source) {
        if (tryQueue()) {
            provider = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromCreature);
        }
    }

    public static void queueTooltips(AbstractCard source) {
        if (tryQueue()) {
            provider = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromCard);
        }
    }

    public static void queueTooltips(AbstractPotion source) {
        if (tryQueue()) {
            provider = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromPotion);
        }
    }

    public static void queueTooltips(AbstractRelic source) {
        if (tryQueue()) {
            provider = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromRelic);
        }
    }

    public static void queueTooltips(AbstractBlight source) {
        if (tryQueue()) {
            provider = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromBlight);
        }
    }

    private static void renderFromBlight(SpriteBatch sb) {
        AbstractBlight blight = EUIUtils.safeCast(provider, AbstractBlight.class);
        if (blight == null) {
            return;
        }

        if (lastProvider != provider) {
            lastProvider = provider;
            TOOLTIPS.clear();
            PREVIEWS.clear();
            addGenericTips(blight.tips);
            scanListForAdditionalTips(TOOLTIPS);
        }

        float x;
        float y;
        if ((float) InputHelper.mX >= 1400.0F * Settings.scale) {
            x = InputHelper.mX - (350 * Settings.scale);
            y = InputHelper.mY - (50 * Settings.scale);
        }
        else if (CardCrawlGame.mainMenuScreen.screen == MainMenuScreen.CurScreen.RELIC_VIEW) {
            x = 180 * Settings.scale;
            y = 0.7f * Settings.HEIGHT;
        }
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP && TOOLTIPS.size() > 2 && !AbstractDungeon.player.hasBlight(blight.blightID)) {
            x = InputHelper.mX + (60 * Settings.scale);
            y = InputHelper.mY + (180 * Settings.scale);
        }
        else if (AbstractDungeon.player != null && AbstractDungeon.player.hasBlight(blight.blightID)) {
            x = InputHelper.mX + (60 * Settings.scale);
            y = InputHelper.mY - (30 * Settings.scale);
        }
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD) {
            x = 360 * Settings.scale;
            y = InputHelper.mY + (50 * Settings.scale);
        }
        else {
            x = InputHelper.mX + (50 * Settings.scale);
            y = InputHelper.mY + (50 * Settings.scale);
        }

        renderTipsImpl(sb, x, y);
    }

    private static void renderFromCard(SpriteBatch sb) {
        AbstractCard card = EUIUtils.safeCast(provider, AbstractCard.class);
        if (card == null) {
            return;
        }

        if (lastProvider != provider) {
            lastProvider = provider;
            TOOLTIPS.clear();
            if (provider instanceof TooltipProvider) {
                for (EUITooltip tip : ((TooltipProvider) provider).getTipsForRender()) {
                    if (tip.isRenderable()) {
                        TOOLTIPS.add(tip);
                    }
                }
            }
            else {
                for (String k : card.keywords) {
                    EUIKeywordTooltip tip = EUIKeywordTooltip.findByName(k);
                    if (tip != null && tip.isRenderable()) {
                        TOOLTIPS.add(tip);
                    }
                }
            }
            scanListForAdditionalTips(TOOLTIPS);
            fillProviderPreview();
        }

        float x = card.current_x;
        float y = card.current_y - BOX_EDGE_H + AbstractCard.IMG_HEIGHT * 0.5f;
        if (card.current_x < (float) Settings.WIDTH * 0.7f) {
            x += AbstractCard.IMG_WIDTH / 2f + CARD_TIP_PAD;
        }
        else {
            x -= AbstractCard.IMG_WIDTH / 2f + CARD_TIP_PAD + BOX_W;
        }

        boolean popUp = provider instanceof TooltipProvider && ((TooltipProvider) provider).isPopup();
        for (EUIPreview preview : PREVIEWS) {
            preview.render(sb, card.current_x, card.current_y, 0.83f, card.upgraded || EUIGameUtils.canShowUpgrades(false), popUp);
        }
        renderTipsImpl(sb, x, y);
    }

    private static void renderFromCreature(SpriteBatch sb) {
        AbstractCreature creature = EUIUtils.safeCast(provider, AbstractCreature.class);
        if (creature == null) {
            return;
        }
        ArrayList<PowerTip> ogTips = ReflectionHacks.getPrivate(creature, AbstractCreature.class, "tips");

        float x;
        if ((creature.hb.cX + creature.hb.width * 0.5f) < TIP_X_THRESHOLD) {
            x = creature.hb.cX + (creature.hb.width / 2.0F) + TIP_OFFSET_R_X;
        }
        else {
            x = creature.hb.cX - (creature.hb.width / 2.0F) + TIP_OFFSET_L_X;
        }

        if (lastProvider != creature) {
            lastProvider = creature;

            TOOLTIPS.clear();

            if (creature instanceof IntentProvider) {
                EUITooltip intentTip = ((IntentProvider) creature).getIntentTip();
                if (intentTip != null) {
                    TOOLTIPS.add(intentTip);
                }
            }
            else {
                if (creature instanceof AbstractMonster) {
                    AbstractMonster monster = (AbstractMonster) creature;
                    if (EUIGameUtils.canViewEnemyIntents(monster)) {
                        EUITooltip tip = fromMonsterIntent(monster);
                        if (tip != null) {
                            TOOLTIPS.add(tip);
                        }
                    }
                }
            }

            for (AbstractPower p : creature.powers) {
                if (canRenderPower(p)) {
                    // Background colors should be handled by the provider
                    if (p instanceof TooltipProvider) {
                        TOOLTIPS.add(((TooltipProvider) p).getTooltip());
                        continue;
                    }

                    final EUIKeywordTooltip tip = new EUIKeywordTooltip(p.name, p.description);
                    if (p.region128 != null) {
                        tip.icon = p.region128;
                    }
                    else if (p.region48 != null) {
                        tip.icon = p.region48;
                    }

                    if (tip.icon == null && p.img != null) {
                        tip.setIcon(p.img, 6);
                    }

                    switch (p.type) {
                        case BUFF:
                            tip.setBackgroundColor(TIP_BUFF);
                            break;
                        case DEBUFF:
                            tip.setBackgroundColor(TIP_DEBUFF);
                    }

                    TOOLTIPS.add(tip);
                }
            }

            scanListForAdditionalTips(TOOLTIPS);
            fillProviderPreview();

            // If renderFromCreature is called instead of renderTip, creature tips will not be filled with power tips but will still have additional tips added by modders
            if (creature instanceof AbstractMonster) {
                for (PowerTip tip : ogTips) {
                    if (tip instanceof CardPowerTip) {
                        PREVIEWS.add(new EUICardPreview(((CardPowerTip) tip).card));
                    }
                    else {
                        final EUIKeywordTooltip t = new EUIKeywordTooltip(tip.header, tip.body);
                        if (tip.img != null) {
                            t.setIcon(tip.img);
                        }
                        TOOLTIPS.add(t);
                    }
                }
            }
        }

        // Need to clear out these tips because apparently anything that adds to them will add to them unto infinity
        ogTips.clear();

        float y = creature.hb.cY + calculateAdditionalOffset(TOOLTIPS, creature.hb.cY);
        renderPreviews(sb, x, y);
        renderTipsImpl(sb, x, y);
    }

    private static void renderFromPotion(SpriteBatch sb) {
        AbstractPotion potion = EUIUtils.safeCast(provider, AbstractPotion.class);
        if (potion == null) {
            return;
        }

        if (lastProvider != provider) {
            lastProvider = provider;
            TOOLTIPS.clear();
            addGenericTips(potion.tips);
            scanListForAdditionalTips(TOOLTIPS);
            fillProviderPreview();
        }

        boolean hasPotion = AbstractDungeon.player != null && AbstractDungeon.player.hasPotion(potion.ID);
        float x;
        float y;
        if (CardCrawlGame.mainMenuScreen.screen == MainMenuScreen.CurScreen.POTION_VIEW) {
            x = 150 * Settings.scale;
            y = 800.0F * Settings.scale;
            renderTipsImpl(sb, x, y);
            renderPreviews(sb, 360 * Settings.scale, 0.25f * Settings.HEIGHT);
        }
        else {
            if ((float) InputHelper.mX >= 1400.0F * Settings.scale) {
                x = InputHelper.mX - (350 * Settings.scale);
                y = InputHelper.mY - (50 * Settings.scale);
            }
            else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP && potion.tips.size() > 2 && !hasPotion) {
                x = InputHelper.mX + (60 * Settings.scale);
                y = InputHelper.mY + (180 * Settings.scale);
            }
            else if (hasPotion) {
                x = InputHelper.mX + (60 * Settings.scale);
                y = InputHelper.mY - (30 * Settings.scale);
            }
            else {
                x = InputHelper.mX + (50 * Settings.scale);
                y = InputHelper.mY + (50 * Settings.scale);
            }

            renderPreviews(sb, potion.posX + potion.hb.width, potion.posY);
            renderTipsImpl(sb, x, y);
        }
    }

    private static void renderFromRelic(SpriteBatch sb) {
        AbstractRelic relic = EUIUtils.safeCast(provider, AbstractRelic.class);
        if (relic == null) {
            return;
        }

        if (lastProvider != provider) {
            lastProvider = provider;
            TOOLTIPS.clear();
            addGenericTips(relic.tips);
            scanListForAdditionalTips(TOOLTIPS);
            fillProviderPreview();
        }

        boolean hasRelic = AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(relic.relicId);
        float x;
        float y;
        if (CardCrawlGame.mainMenuScreen.screen == MainMenuScreen.CurScreen.RELIC_VIEW) {
            x = 180 * Settings.scale;
            y = 0.7f * Settings.HEIGHT;
            renderPreviews(sb, 655 * Settings.scale, 0.1f * Settings.HEIGHT);
            renderTipsImpl(sb, x, y);
        }
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD && !hasRelic) {
            x = 350 * Settings.scale;
            y = InputHelper.mY + (50 * Settings.scale);
            renderPreviews(sb, 410 * Settings.scale, y);
            renderTipsImpl(sb, x, y);
        }
        else {
            if ((float) InputHelper.mX >= 1400.0F * Settings.scale) {
                x = InputHelper.mX - (350 * Settings.scale);
                y = InputHelper.mY - (50 * Settings.scale);
            }
            else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP && TOOLTIPS.size() > 2 && !hasRelic) {
                x = InputHelper.mX + (60 * Settings.scale);
                y = InputHelper.mY + (180 * Settings.scale);
            }
            else if (hasRelic) {
                x = InputHelper.mX + (60 * Settings.scale);
                y = InputHelper.mY - (30 * Settings.scale);
            }
            else {
                x = InputHelper.mX + (50 * Settings.scale);
                y = InputHelper.mY + (50 * Settings.scale);
            }

            renderPreviews(sb, x, y);
            renderTipsImpl(sb, x, y);
        }
    }

    private static void renderGeneric(SpriteBatch sb) {
        renderTipsImpl(sb, genericTipPos.x, genericTipPos.y);
    }

    public static void renderPreviews(SpriteBatch sb, float x, float y) {
        renderPreviews(sb, x, y, false, false);
    }

    public static void renderPreviews(SpriteBatch sb, float x, float y, boolean upgraded, boolean popup) {
        if (!PREVIEWS.isEmpty()) {
            float previewOffset = (x < Settings.WIDTH * 0.1f) ? x + BOX_W : x - AbstractCard.IMG_WIDTH;
            EUIPreview preview;
            if (EUIHotkeys.cycle.isJustPressed()) {
                preview = PREVIEWS.next(true);
            }
            else {
                preview = PREVIEWS.current();
            }
            preview.render(sb, previewOffset, y, 0.8f, upgraded, popup);
            if (PREVIEWS.size() > 1) {
                String cyclePreviewText = EUIRM.strings.keyToCycle(EUIHotkeys.cycle.getKeyString());
                BitmapFont font = FontHelper.cardDescFont_N;
                final float dY = y - AbstractCard.RAW_H * 0.55f;
                EUIRenderHelpers.draw(sb, EUIRM.images.panelRoundedHalfH.texture(), Color.DARK_GRAY, x, dY, AbstractCard.IMG_WIDTH * 0.6f, font.getLineHeight() * 1.8f);
                FontHelper.renderFont(sb, font, cyclePreviewText, x, dY, Settings.PURPLE_COLOR);
                EUIRenderHelpers.resetFont(font);
            }
        }
    }

    private static void renderTipsImpl(SpriteBatch sb, float x, float y) {
        // Attempt to push up the tooltip rendering start as far up as possible if the tooltips would go off the screen
        float projected_end = y + SHADOW_DIST_Y - 1;
        for (EUITooltip tip : TOOLTIPS) {
            projected_end -= tip.getTotalHeight();
        }
        if (projected_end < 0) {
            y = y - projected_end;
        }

        if (y > TIP_Y_LIMIT) {
            y = TIP_Y_LIMIT;
        }

        float original_y = y;
        final float offset_x = (x > TIP_X_THRESHOLD) ? BOX_W : -BOX_W;
        for (int i = 0; i < TOOLTIPS.size(); i++) {
            EUITooltip tip = TOOLTIPS.get(i);
            float projected = y - tip.getTotalHeight();
            if (projected < -SHADOW_DIST_Y) {
                y = original_y;
                x += offset_x;
            }

            y -= tip.render(sb, x, y, i) + BOX_RENDER_OFFSET;
        }
    }

    public static ArrayList<EUITooltip> scanForTips(String rawDesc) {
        return scanForTips(rawDesc, true);
    }

    public static ArrayList<EUITooltip> scanForTips(String rawDesc, boolean allowNonRenderable) {
        ArrayList<EUITooltip> tips = new ArrayList<>();
        scanForTips(rawDesc, tips, tips, allowNonRenderable);
        return tips;
    }

    public static void scanForTips(String rawDesc, Collection<? super EUIKeywordTooltip> tips) {
        scanForTips(rawDesc, tips, tips, true);
    }

    public static void scanForTips(String rawDesc, Collection<? super EUIKeywordTooltip> tips, Collection<? super EUIKeywordTooltip> dest, boolean allowNonRenderable) {
        StringBuilder sb = new StringBuilder();
        String s;
        EUIKeywordTooltip tip;
        for (int i = 0; i < rawDesc.length(); i++) {
            char c = rawDesc.charAt(i);
            switch (c) {
                case '[':
                case '†':
                    while (i + 1 < rawDesc.length()) {
                        i += 1;
                        c = rawDesc.charAt(i);
                        if (c == ']') {
                            i += 1; // Skip this character
                            break;
                        }
                        else {
                            sb.append(c);
                        }
                    }
                    tip = EUIKeywordTooltip.findByIDTemp(sb.toString());
                    if (tip != null && (allowNonRenderable || tip.isRenderable()) && !tips.contains(tip)) {
                        dest.add(tip);
                    }
                    sb.setLength(0);
                    break;
                case '{':
                    while (i + 1 < rawDesc.length()) {
                        i += 1;
                        c = rawDesc.charAt(i);
                        if (c == ':') {
                            sb.setLength(0);
                        }
                        else if (c == '}') {
                            i += 1;  // Skip this character
                            break;
                        }
                        else {
                            sb.append(c);
                        }
                    }
                    s = StringUtils.lowerCase(sb.toString());
                    tip = EUIKeywordTooltip.findByName(s);
                    if (tip != null && (allowNonRenderable || tip.isRenderable()) && !tips.contains(tip)) {
                        dest.add(tip);
                    }
                    sb.setLength(0);
                    break;
                case '#':
                    i += 1;  // Denotes a color
                    break;
                default:
                    if (!Character.isWhitespace(c)) {
                        sb.append(c);
                        break;
                    }
                case '?':
                case '!':
                case '。':
                case '.':
                case ',':
                    s = StringUtils.lowerCase(sb.toString());
                    tip = EUIKeywordTooltip.findByName(s);
                    if (tip != null && (allowNonRenderable || tip.isRenderable()) && !tips.contains(tip)) {
                        dest.add(tip);
                    }
                    sb.setLength(0);
            }
        }
    }

    public static void scanListForAdditionalTips(Collection<EUITooltip> receiver) {
        scanListForAdditionalTips(new ArrayList<>(receiver), receiver);
    }

    public static void scanListForAdditionalTips(Collection<EUITooltip> source, Collection<EUITooltip> receiver) {
        if (EUIConfiguration.enableExpandTooltips.get()) {
            for (EUITooltip tip : source) {
                if (tip.description != null && tip.isRenderable()) {
                    scanForTips(tip.description, receiver, receiver, false);
                }
            }
        }
    }

    private static boolean tryQueue() {
        final boolean canRender = canRenderTooltips();
        if (canRender) {
            clearVanillaTips();
        }

        return canRender;
    }

    public EUITooltip formatDescription(Object... items) {
        return setDescription(EUIUtils.format(description, items));
    }

    public String getTitleHighlighted() {
        return '{' + title + '}';
    }

    public String getTitleOrIcon() {
        return (ID != null) ? '[' + ID + ']' : title;
    }

    public String getTitleOrIconForced() {
        return this.ID != null ? '†' + this.ID + ']' : this.title;
    }

    public float getTotalHeight() {
        return height() + BOX_RENDER_OFFSET;
    }

    public float height() {
        if (lastHeight == null) {
            BitmapFont descFont = descriptionFont != null ? descriptionFont : EUIFontHelper.tooltipFont;
            lastTextHeight = EUITextHelper.getSmartHeight(descFont, description, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);
            lastSubHeaderHeight = (subHeader != null) ? EUITextHelper.getSmartHeight(descFont, subHeader.text, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - TIP_DESC_LINE_SPACING * 1.5f : 0;
            lastHeight = (StringUtils.isEmpty(description)) ? (-40f * Settings.scale) : (-(lastTextHeight + lastSubHeaderHeight) - 7f * Settings.scale);
        }
        return lastHeight;
    }

    public boolean idEquals(EUITooltip tooltip) {
        return tooltip != null && ID != null && ID.equals(tooltip.ID);
    }

    public void invalidateHeight() {
        lastHeight = null;
        lastTextHeight = null;
        lastSubHeaderHeight = null;
    }

    public boolean isRenderable() {
        return true;
    }

    public EUITooltip makeCopy() {
        return new EUITooltip(this);
    }

    public float render(SpriteBatch sb, float x, float y, int index) {
        verifyFonts();
        final float h = height();
        renderBg(sb, Settings.TOP_PANEL_SHADOW_COLOR, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, h);
        renderBg(sb, Color.WHITE, x, y, h);
        renderTitle(sb, x, y);
        renderSubtext(sb, x, y);

        float yOff = y + BODY_OFFSET_Y;
        yOff += renderSubheader(sb, x, yOff);
        renderDescription(sb, x, yOff);

        return h;
    }

    public void renderBg(SpriteBatch sb, Color color, float x, float y, float h) {
        float totalHeight = h + BORDER_SIZE;
        float boxW = width - BORDER_SIZE * 2;
        float middleY = y - totalHeight;
        float bottomY = middleY - BORDER_SIZE;
        float topY = middleY + totalHeight;
        sb.setColor(color);
        sb.draw(EUIRM.images.vanillaTipCornerBL.texture(), x, bottomY, BORDER_SIZE, BORDER_SIZE);
        sb.draw(EUIRM.images.vanillaTipBorderB.texture(), x + BORDER_SIZE, bottomY, boxW, BORDER_SIZE);
        sb.draw(EUIRM.images.vanillaTipCornerBR.texture(), x + BORDER_SIZE + boxW, bottomY, BORDER_SIZE, BORDER_SIZE);
        sb.draw(EUIRM.images.vanillaTipBorderL.texture(), x, middleY, BORDER_SIZE, totalHeight);
        sb.draw(EUIRM.images.vanillaTip.texture(), x + BORDER_SIZE, middleY, boxW, totalHeight);
        sb.draw(EUIRM.images.vanillaTipBorderR.texture(), x + BORDER_SIZE + boxW, middleY, BORDER_SIZE, totalHeight);
        sb.draw(EUIRM.images.vanillaTipCornerTL.texture(), x, topY, BORDER_SIZE, BORDER_SIZE);
        sb.draw(EUIRM.images.vanillaTipBorderT.texture(), x + BORDER_SIZE, topY, boxW, BORDER_SIZE);
        sb.draw(EUIRM.images.vanillaTipCornerTR.texture(), x + BORDER_SIZE + boxW, topY, BORDER_SIZE, BORDER_SIZE);
    }

    public void renderDescription(SpriteBatch sb, float x, float y) {
        if (!StringUtils.isEmpty(description)) {
            EUITextHelper.renderSmart(sb, descriptionFont, description, x + TEXT_OFFSET_X, y, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING, BASE_COLOR);
        }
    }

    public float renderSubheader(SpriteBatch sb, float x, float y) {
        if (subHeader != null) {
            FontHelper.renderFontLeftTopAligned(sb, descriptionFont, subHeader.text, x + TEXT_OFFSET_X, y, subHeader.color);
            return lastSubHeaderHeight;
        }
        return 0;
    }

    public void renderSubtext(SpriteBatch sb, float x, float y) {
        if (subText != null) {
            FontHelper.renderFontRightTopAligned(sb, descriptionFont, subText.text, x + BODY_TEXT_WIDTH * 1.07f, y + HEADER_OFFSET_Y * 1.33f, subText.color);
        }
    }

    public void renderTitle(SpriteBatch sb, float x, float y) {
        FontHelper.renderFontLeftTopAligned(sb, headerFont, title, x + TEXT_OFFSET_X, y + HEADER_OFFSET_Y, Settings.GOLD_COLOR);
    }

    public EUITooltip setAutoWidth() {
        this.width = Math.max(BOX_W, EUITextHelper.getSmartWidth(headerFont, title) + BOX_EDGE_H);
        invalidateHeight();

        return this;
    }

    public EUITooltip setChildren(List<EUITooltip> other) {
        this.children = other;

        return this;
    }

    public EUITooltip setChildren(EUITooltip... other) {
        this.children = Arrays.asList(other);

        return this;
    }

    public EUITooltip setChildrenFromDescription() {
        this.children = scanForTips(this.description);
        return this;
    }

    public EUITooltip setChildrenFromDescription(boolean allowUnrenderable) {
        this.children = scanForTips(this.description, allowUnrenderable);
        return this;
    }

    public EUITooltip setDescription(String description) {
        this.description = description;
        invalidateHeight();
        return this;
    }

    public EUITooltip setDescriptionFont(BitmapFont descriptionFont) {
        this.descriptionFont = descriptionFont;
        return this;
    }

    public EUITooltip setFonts(BitmapFont headerFont, BitmapFont descriptionFont) {
        this.headerFont = headerFont;
        this.descriptionFont = descriptionFont;
        invalidateHeight();
        return this;
    }

    public EUITooltip setHeaderFont(BitmapFont headerFont) {
        this.headerFont = headerFont;
        return this;
    }

    public EUITooltip setSubheader(ColoredString string) {
        this.subHeader = string;
        invalidateHeight();

        return this;
    }

    public EUITooltip setText(String title, String description) {
        if (title != null) {
            setTitle(title);
        }
        if (description != null) {
            setDescription(description);
        }
        return this;
    }

    public EUITooltip setTitle(String title) {
        this.title = title;
        invalidateHeight();

        return this;
    }

    public EUITooltip setWidth(float width) {
        this.width = width;
        invalidateHeight();

        return this;
    }

    @Override
    public String toString() {
        return getTitleOrIcon();
    }

    // Because keyword tooltips can be instantiated before EUIFontHelper fonts are set up, we need to verify keyword tooltips when they render
    public void verifyFonts() {
        if (headerFont == null) {
            headerFont = FontHelper.tipHeaderFont;
        }
        if (descriptionFont == null) {
            descriptionFont = EUIFontHelper.tooltipFont;
        }
    }
}