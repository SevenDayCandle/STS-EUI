package extendedui.ui.tooltips;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
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
import extendedui.text.EUISmartText;
import extendedui.utilities.ColoredString;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class EUITooltip {
    public static final Color BASE_COLOR = new Color(1f, 0.9725f, 0.8745f, 1f);
    public static final float CARD_TIP_PAD = 12f * Settings.scale;
    public static final float BOX_EDGE_H = 32f * Settings.scale;
    public static final float SHADOW_DIST_Y = 14f * Settings.scale;
    public static final float SHADOW_DIST_X = 9f * Settings.scale;
    public static final float BOX_BODY_H = 64f * Settings.scale;
    public static final float TEXT_OFFSET_X = 22f * Settings.scale;
    public static final float HEADER_OFFSET_Y = 12f * Settings.scale;
    public static final float ORB_OFFSET_Y = -8f * Settings.scale;
    public static final float BODY_OFFSET_Y = -20f * Settings.scale;
    public static final float BOX_W = 360f * Settings.scale;
    public static final float BODY_TEXT_WIDTH = 320f * Settings.scale;
    public static final float TIP_DESC_LINE_SPACING = 26f * Settings.scale;
    public static final float TIP_X_THRESHOLD = (Settings.WIDTH * 0.5f); // 1544.0F * Settings.scale;
    public static final float TIP_OFFSET_R_X = 20.0F * Settings.scale;
    public static final float TIP_OFFSET_L_X = -380.0F * Settings.scale;
    private final static ArrayList<String> EMPTY_LIST = new ArrayList<>();
    private static final ArrayList<EUITooltip> tooltips = new ArrayList<>();
    private static final Vector2 genericTipPos = new Vector2(0, 0);
    private static TooltipProvider provider;
    private static TooltipProvider lastProvider;
    private static AbstractCreature creature;
    private static AbstractCreature lastHoveredCreature;
    public ArrayList<String> descriptions = new ArrayList<>();
    public BitmapFont headerFont = EUIFontHelper.cardTooltipTitleFontNormal;
    public BitmapFont descriptionFont = EUIFontHelper.cardTooltipFont;
    public ColoredString subHeader;
    public ColoredString subText;
    public EUITooltip child;
    public String ID;
    public String title;
    public boolean renderBg = true;
    public float width = BOX_W;
    protected int currentDesc;
    protected Float lastSubHeaderHeight;
    protected Float lastTextHeight;
    protected Float lastHeight;
    public boolean canRender = true;

    public EUITooltip(String title, String... descriptions) {
        this(title, Arrays.asList(descriptions));
    }

    public EUITooltip(String title, Collection<String> descriptions) {
        this.title = title;
        this.descriptions.addAll(descriptions);
    }

    public EUITooltip(EUITooltip other) {
        this.title = other.title;
        this.descriptions.addAll(other.descriptions);
        this.subHeader = other.subHeader;
    }

    public static boolean canRenderPower(AbstractPower po) {
        return po.name != null;
    }

    public static boolean canRenderTooltips() {
        return !EUIClassUtils.getFieldStatic(TipHelper.class, "renderedTipThisFrame", Boolean.class);
    }

    public static void canRenderTooltips(boolean canRender) {
        ReflectionHacks.setPrivateStatic(TipHelper.class, "renderedTipThisFrame", !canRender);

        if (!canRender) {
            tooltips.clear();
            ReflectionHacks.setPrivateStatic(TipHelper.class, "BODY", null);
            ReflectionHacks.setPrivateStatic(TipHelper.class, "HEADER", null);
            ReflectionHacks.setPrivateStatic(TipHelper.class, "card", null);
            ReflectionHacks.setPrivateStatic(TipHelper.class, "KEYWORDS", EMPTY_LIST);
            ReflectionHacks.setPrivateStatic(TipHelper.class, "POWER_TIPS", EMPTY_LIST);
            lastHoveredCreature = null;
            provider = null;
            lastProvider = null;
        }
    }

    public static EUIKeywordTooltip fromMonsterIntent(AbstractMonster monster) {
        PowerTip tip = ReflectionHacks.getPrivate(monster, AbstractMonster.class, "intentTip");
        return tip != null ? fromPowerTip(tip) : null;
    }

    public static EUIKeywordTooltip fromPowerTip(PowerTip tip) {
        EUIKeywordTooltip newTip = new EUIKeywordTooltip(tip.header, tip.body);
        if (newTip.title == null) {
            newTip.title = "";
        }
        if (tip.imgRegion != null) {
            newTip.icon = tip.imgRegion;
        }
        else if (tip.img != null) {
            newTip.icon = new TextureRegion(tip.img);
        }
        return newTip;
    }

    public static void queueTooltip(EUITooltip tooltip) {
        float x = InputHelper.mX;
        float y = InputHelper.mY;
        x += (x < Settings.WIDTH * 0.75f) ? (Settings.scale * 40f) : -(tooltip.width + (Settings.scale * 40f));
        y += (y < Settings.HEIGHT * 0.9f) ? (Settings.scale * 40f) : -(Settings.scale * 50f);
        queueTooltip(tooltip, x, y);
    }

    public static void queueTooltip(EUITooltip tooltip, float x, float y) {
        if (tryRender()) {
            EUITooltip cur = tooltip;
            while (cur != null) {
                tooltips.add(cur);
                cur = cur.child;
            }
            genericTipPos.x = x;
            genericTipPos.y = y;
            EUI.addPriorityPostRender(EUITooltip::renderGeneric);
        }
    }

    public static void queueTooltips(Collection<? extends EUITooltip> tips) {
        float maxWidth = tips.size() > 0 ? EUIUtils.max(tips, tip -> tip.width) : BOX_W;
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
        if (tryRender()) {
            tooltips.addAll(tips);
            genericTipPos.x = x;
            genericTipPos.y = y;
            EUI.addPriorityPostRender(EUITooltip::renderGeneric);
        }
    }

    public static void queueTooltips(AbstractCreature source) {
        if (tryRender()) {
            creature = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromCreature);
        }
    }

    public static <T extends AbstractCard & TooltipProvider> void queueTooltips(T source) {
        if (tryRender()) {
            provider = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromCard);
        }
    }

    public static <T extends AbstractPotion & TooltipProvider> void queueTooltips(T source) {
        if (tryRender()) {
            provider = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromPotion);
        }
    }

    public static <T extends AbstractRelic & TooltipProvider> void queueTooltips(T source) {
        if (tryRender()) {
            provider = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromRelic);
        }
    }

    public static <T extends AbstractBlight & TooltipProvider> void queueTooltips(T source) {
        if (tryRender()) {
            provider = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromBlight);
        }
    }

    public static void renderFromBlight(SpriteBatch sb) {
        AbstractBlight blight = EUIUtils.safeCast(provider, AbstractBlight.class);
        if (blight == null) {
            return;
        }

        lastProvider = provider;
        List<? extends EUITooltip> pTips = provider.getTips();

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
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP && pTips.size() > 2 && !AbstractDungeon.player.hasBlight(blight.blightID)) {
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

        renderTipsImpl(sb, pTips, x, y);
    }

    public static void renderFromCard(SpriteBatch sb) {
        AbstractCard card = EUIUtils.safeCast(provider, AbstractCard.class);
        if (card == null) {
            return;
        }

        if (lastProvider != provider) {
            lastProvider = provider;
            lastHoveredCreature = null;
            tooltips.clear();
            for (EUITooltip tip : provider.getTipsForRender()) {
                if (tip.canRender) {
                    tooltips.add(tip);
                }
            }
        }

        float x;
        float y;
        if (provider.isPopup()) {
            x = 0.78f * Settings.WIDTH;
            y = 0.85f * Settings.HEIGHT;
        }
        else {
            x = card.current_x;
            if (card.current_x < (float) Settings.WIDTH * 0.7f) {
                x += AbstractCard.IMG_WIDTH / 2f + CARD_TIP_PAD;
            }
            else {
                x -= AbstractCard.IMG_WIDTH / 2f + CARD_TIP_PAD + BOX_W;
            }

            y = card.current_y - BOX_EDGE_H;
            float size = 0;
            for (EUITooltip tip : tooltips) {
                if (!tip.canRender && !StringUtils.isEmpty(tip.description())) {
                    size += 1f;
                }
            }

            if (size > 3f && card.current_y < Settings.HEIGHT * 0.5f && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.CARD_REWARD) {
                float steps = (tooltips.size() - 3) * 0.4f;
                float multi = 1f - (card.current_y / (Settings.HEIGHT * 0.5f));

                y += AbstractCard.IMG_HEIGHT * (0.5f + MathUtils.round(multi * steps));
            }
            else {
                y += AbstractCard.IMG_HEIGHT * 0.5f;
            }
        }

        for (int i = 0; i < tooltips.size(); i++) {
            EUITooltip tip = tooltips.get(i);
            if ((!tip.canRender || StringUtils.isEmpty(tip.description()))) {
                continue;
            }

            y -= tip.render(sb, x, y, i) + BOX_EDGE_H * 3.15f;
        }

        EUICardPreview preview = provider.getPreview();
        if (preview != null) {
            preview.render(sb, card, card.upgraded || EUIGameUtils.canShowUpgrades(false), provider.isPopup());
        }
    }

    public static void renderFromCreature(SpriteBatch sb) {
        if (creature == null) {
            return;
        }

        float x;
        float y = creature.hb.cY + EUIRenderHelpers.calculateAdditionalOffset(tooltips, creature.hb.cY);
        if ((creature.hb.cX + creature.hb.width * 0.5f) < TIP_X_THRESHOLD) {
            x = creature.hb.cX + (creature.hb.width / 2.0F) + TIP_OFFSET_R_X;
        }
        else {
            x = creature.hb.cX - (creature.hb.width / 2.0F) + TIP_OFFSET_L_X;
        }

        if (lastHoveredCreature != creature) {
            lastHoveredCreature = creature;

            tooltips.clear();

            if (creature instanceof IntentProvider) {
                lastProvider = provider = (TooltipProvider) creature;
                EUITooltip intentTip = ((IntentProvider) creature).getIntentTip();
                if (intentTip != null) {
                    tooltips.add(intentTip);
                }
                EUICardPreview preview = provider.getPreview();
                if (preview != null) {
                    float previewOffset = (x < Settings.WIDTH * 0.1f) ? x + BOX_W : x - AbstractCard.IMG_WIDTH;
                    preview.render(sb, previewOffset, y, 0.8f, preview.getCard().upgraded);
                }
            }
            else {
                lastProvider = null;
                if (creature instanceof AbstractMonster) {
                    if (EUIGameUtils.canViewEnemyIntents((AbstractMonster) creature)) {
                        EUITooltip tip = fromMonsterIntent((AbstractMonster) creature);
                        if (tip != null) {
                            tooltips.add(tip);
                        }
                    }
                }
            }

            for (AbstractPower p : creature.powers) {
                if (canRenderPower(p)) {
                    if (p instanceof TooltipProvider) {
                        tooltips.add(((TooltipProvider) p).getTooltip());
                        continue;
                    }

                    final EUIKeywordTooltip tip = new EUIKeywordTooltip(p.name, p.description);
                    if (p.region48 != null) {
                        tip.icon = p.region48;
                    }

                    if (tip.icon == null && p.img != null) {
                        tip.setIcon(p.img, 6);
                    }

                    tooltips.add(tip);
                }
            }
        }

        final float original_y = y;
        final float offset_x = (x > TIP_X_THRESHOLD) ? BOX_W : -BOX_W;
        float offset = 0.0F;

        float offsetChange;
        for (int i = 0; i < tooltips.size(); i++) {
            EUITooltip tip = tooltips.get(i);
            offsetChange = EUIRenderHelpers.getTooltipHeight(tip) + BOX_EDGE_H * 3.15F;
            if ((offset + offsetChange) >= (Settings.HEIGHT * 0.7F)) {
                offset = 0.0F;
                y = original_y;
                x += offset_x;
            }

            y -= tip.render(sb, x, y, i) + BOX_EDGE_H * 3.15f;
            offset += offsetChange;
        }
    }

    public static void renderFromPotion(SpriteBatch sb) {
        AbstractPotion potion = EUIUtils.safeCast(provider, AbstractPotion.class);
        if (potion == null) {
            return;
        }

        lastProvider = provider;
        List<? extends EUITooltip> pTips = provider.getTips();

        float x;
        float y;
        if ((float) InputHelper.mX >= 1400.0F * Settings.scale) {
            x = InputHelper.mX - (350 * Settings.scale);
            y = InputHelper.mY - (50 * Settings.scale);
        }
        else if (CardCrawlGame.mainMenuScreen.screen == MainMenuScreen.CurScreen.POTION_VIEW) {
            x = 150 * Settings.scale;
            y = 800.0F * Settings.scale;
        }
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP && potion.tips.size() > 2 && !AbstractDungeon.player.hasPotion(potion.ID)) {
            x = InputHelper.mX + (60 * Settings.scale);
            y = InputHelper.mY + (180 * Settings.scale);
        }
        else if (AbstractDungeon.player != null && AbstractDungeon.player.hasPotion(potion.ID)) {
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

        renderTipsImpl(sb, pTips, x, y);
    }


    public static void renderFromRelic(SpriteBatch sb) {
        AbstractRelic relic = EUIUtils.safeCast(provider, AbstractRelic.class);
        if (relic == null) {
            return;
        }

        lastProvider = provider;
        List<? extends EUITooltip> pTips = provider.getTips();

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
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP && pTips.size() > 2 && !AbstractDungeon.player.hasRelic(relic.relicId)) {
            x = InputHelper.mX + (60 * Settings.scale);
            y = InputHelper.mY + (180 * Settings.scale);
        }
        else if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(relic.relicId)) {
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

        renderTipsImpl(sb, pTips, x, y);
    }

    public static void renderGeneric(SpriteBatch sb) {
        renderTipsImpl(sb, tooltips, genericTipPos.x, genericTipPos.y);
    }

    protected static void renderTipsImpl(SpriteBatch sb, List<? extends EUITooltip> tips, float x, float y) {
        for (int i = 0; i < tips.size(); i++) {
            final EUITooltip tip = tips.get(i);

            if (tip.canRender) {
                y -= tip.render(sb, x, y, i) + BOX_EDGE_H * 3.15f;
            }
        }
    }

    private static boolean tryRender() {
        final boolean canRender = canRenderTooltips();
        if (canRender) {
            canRenderTooltips(false);
        }

        return canRender;
    }

    public void cycleDescription() {
        if (descriptions.size() > 1) {
            setIndex((currentDesc + 1) % descriptions.size());
        }
    }

    public String description() {
        return currentDesc < descriptions.size() ? descriptions.get(currentDesc) : "";
    }

    public String formatDescription(Object... items) {
        if (currentDesc < descriptions.size()) {
            String newDesc = EUIUtils.format(descriptions.get(currentDesc), items);
            descriptions.set(currentDesc, newDesc);
            return newDesc;
        }
        return "";
    }

    public EUITooltip setDescriptionFont(BitmapFont descriptionFont) {
        this.descriptionFont = descriptionFont;
        return this;
    }

    public EUITooltip setHeaderFont(BitmapFont headerFont) {
        this.headerFont = headerFont;
        return this;
    }

    public String getTitleOrIconForced() {
        return this.ID != null ? "†" + this.ID + "]" : this.title;
    }

    public float height() {
        if (lastHeight == null) {
            BitmapFont descFont = descriptionFont != null ? descriptionFont : EUIFontHelper.cardTooltipFont;
            String desc = description();
            lastTextHeight = EUISmartText.getSmartHeight(descFont, desc, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);
            lastSubHeaderHeight = (subHeader != null) ? EUISmartText.getSmartHeight(descFont, subHeader.text, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - TIP_DESC_LINE_SPACING * 1.5f : 0;
            lastHeight = (!canRender || StringUtils.isEmpty(desc)) ? (-40f * Settings.scale) : (-(lastTextHeight + lastSubHeaderHeight) - 7f * Settings.scale);
        }
        return lastHeight;
    }

    public boolean is(EUITooltip tooltip) {
        return tooltip != null && ID.equals(tooltip.ID);
    }

    public EUITooltip makeCopy() {
        return new EUITooltip(this);
    }

    public float render(SpriteBatch sb, float x, float y, int index) {
        if (EUIHotkeys.cycle.isJustPressed()) {
            cycleDescription();
        }
        if (descriptions.size() > 1 && (subText == null || subText.text == null || subText.text.isEmpty())) {
            updateCycleText();
        }

        verifyFonts();
        final float h = height();

        renderBg(sb, x, y, h);
        renderTitle(sb, x, y);
        renderSubtext(sb, x, y);

        float yOff = y + BODY_OFFSET_Y;
        yOff += renderSubheader(sb, x, yOff);
        renderDescription(sb, x, yOff);

        return h;
    }

    public void renderBg(SpriteBatch sb, float x, float y, float h) {
        sb.setColor(Settings.TOP_PANEL_SHADOW_COLOR);
        sb.draw(ImageMaster.KEYWORD_TOP, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, width, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x + SHADOW_DIST_X, y - h - BOX_EDGE_H - SHADOW_DIST_Y, width, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x + SHADOW_DIST_X, y - h - BOX_BODY_H - SHADOW_DIST_Y, width, BOX_EDGE_H);
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.KEYWORD_TOP, x, y, width, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x, y - h - BOX_EDGE_H, width, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x, y - h - BOX_BODY_H, width, BOX_EDGE_H);
    }

    public void renderTitle(SpriteBatch sb, float x, float y) {
        FontHelper.renderFontLeftTopAligned(sb, headerFont, title, x + TEXT_OFFSET_X, y + HEADER_OFFSET_Y, Settings.GOLD_COLOR);
    }

    public void renderSubtext(SpriteBatch sb, float x, float y) {
        if (subText != null) {
            FontHelper.renderFontRightTopAligned(sb, descriptionFont, subText.text, x + BODY_TEXT_WIDTH * 1.07f, y + HEADER_OFFSET_Y * 1.33f, subText.color);
        }
    }

    public float renderSubheader(SpriteBatch sb, float x, float y) {
        if (subHeader != null) {
            FontHelper.renderFontLeftTopAligned(sb, descriptionFont, subHeader.text, x + TEXT_OFFSET_X, y, subHeader.color);
            return lastSubHeaderHeight;
        }
        return 0;
    }

    public void renderDescription(SpriteBatch sb, float x, float y) {
        EUISmartText.write(sb, descriptionFont, description(), x + TEXT_OFFSET_X, y, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING, BASE_COLOR);
    }

    public EUITooltip setAutoWidth() {
        this.width = Math.max(BOX_W, EUISmartText.getSmartWidth(headerFont, title) + BOX_EDGE_H);
        invalidateHeight();

        return this;
    }

    public void invalidateHeight() {
        lastHeight = null;
        lastTextHeight = null;
        lastSubHeaderHeight = null;
    }

    public EUITooltip setChild(EUITooltip other) {
        this.child = other;
        // Remove circular children to avoid infinite loops when queueing tooltips
        if (other.child == this) {
            other.child = null;
        }

        return this;
    }

    public EUITooltip setDescription(String description) {
        return setDescription(description, 0);
    }

    public EUITooltip setDescription(String description, int index) {
        if (this.descriptions.size() <= index) {
            this.descriptions.add(description);
        }
        else {
            this.descriptions.set(index, description);
        }
        updateCycleText();

        return this;
    }

    protected void updateCycleText() {
        if (descriptions.size() > 1) {
            if (subText == null) {
                subText = new ColoredString("", Settings.PURPLE_COLOR);
            }
            subText.setText(EUIRM.strings.keyToCycle(EUIHotkeys.cycle.getKeyString()) + " (" + (currentDesc + 1) + "/" + descriptions.size() + ")");
            invalidateHeight();
        }
    }

    public EUITooltip setDescriptions(String... descriptions) {
        return setDescriptions(Arrays.asList(descriptions));
    }

    public EUITooltip setDescriptions(List<String> descriptions) {
        this.descriptions.clear();
        this.descriptions.addAll(descriptions);
        currentDesc = 0;
        updateCycleText();

        return this;
    }

    public EUITooltip setFonts(BitmapFont headerFont, BitmapFont descriptionFont) {
        this.headerFont = headerFont;
        this.descriptionFont = descriptionFont;
        return this;
    }

    public String setIndex(int index) {
        if (descriptions.size() < 1) {
            return "";
        }
        currentDesc = MathUtils.clamp(index, 0, descriptions.size() - 1);
        updateCycleText();
        return description();
    }

    public EUITooltip setSubheader(ColoredString string) {
        this.subHeader = string;
        invalidateHeight();

        return this;
    }

    public EUITooltip setText(String title, String... description) {
        return setText(title, Arrays.asList(description));
    }

    public EUITooltip setText(String title, List<String> description) {
        if (title != null) {
            setTitle(title);
        }
        if (description != null && description.size() > 0) {
            setDescriptions(description);
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

    public EUITooltip showText(boolean value) {
        this.canRender = value;

        return this;
    }

    @Override
    public String toString() {
        return getTitleOrIcon();
    }

    // Because keyword tooltips can be instantiated before EUIFontHelper fonts are set up, we need to verify keyword tooltips when they render
    public void verifyFonts() {
        if (headerFont == null) {
            headerFont = EUIFontHelper.cardTooltipTitleFontNormal;
        }
        if (descriptionFont == null) {
            descriptionFont = EUIFontHelper.cardTooltipFont;
        }
    }

    public String getTitleOrIcon() {
        return (ID != null) ? "[" + ID + "]" : title;
    }
}