package extendedui.ui.tooltips;

import basemod.ReflectionHacks;
import basemod.patches.whatmod.WhatMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
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
import extendedui.interfaces.delegates.FuncT0;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.patches.EUIKeyword;
import extendedui.text.EUISmartText;
import extendedui.utilities.ColoredString;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class EUITooltip
{
    protected static final HashMap<String, EUITooltip> RegisteredIDs = new HashMap<>();
    protected static final HashMap<String, EUITooltip> RegisteredNames = new HashMap<>();
    protected static final HashSet<EUITooltip> IconUpdatingList = new HashSet<>();

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
    private static final String ALT_STRING = Input.Keys.toString(Input.Keys.ALT_LEFT) + "+";
    private static boolean inHand;
    private static TooltipProvider provider;
    private static TooltipProvider lastProvider;
    private static AbstractCreature creature;
    private static AbstractCreature lastHoveredCreature;
    private static final Vector2 genericTipPos = new Vector2(0, 0);

    public ArrayList<String> descriptions = new ArrayList<>();
    public BitmapFont headerFont = EUIFontHelper.cardtooltiptitlefontNormal;
    public BitmapFont descriptionFont = EUIFontHelper.cardTooltipFont;
    public Boolean hideDescription = null;
    public Color backgroundColor;
    public ColoredString modName;
    public ColoredString subHeader;
    public ColoredString subText;
    public EUITooltip child;
    public String ID;
    public String past;
    public String plural;
    public String present;
    public String title;
    public TextureRegion icon;
    public boolean canHighlight = true;
    public boolean canFilter = true;
    public boolean canRender = true;
    public boolean renderBg = true;
    public boolean useLogic = false;
    public float iconmultiH = 1;
    public float iconmultiW = 1;
    protected int currentDesc;
    protected FuncT0<TextureRegion> iconFunc;
    private Float lastModNameHeight;
    private Float lastSubHeaderHeight;
    private Float lastTextHeight;
    private Float lastHeight;

    public EUITooltip(String title, String... descriptions)
    {
        this(title, Arrays.asList(descriptions));
    }

    public EUITooltip(String title, Collection<String> descriptions)
    {
        this.title = title;
        this.descriptions.addAll(descriptions);
    }

    public EUITooltip(String title, AbstractPlayer.PlayerClass playerClass, String... descriptions) {
        this(title, playerClass, Arrays.asList(descriptions));
    }

    public EUITooltip(String title, AbstractPlayer.PlayerClass playerClass, Collection<String> descriptions)
    {
        this.title = title;
        this.descriptions.addAll(descriptions);

        if (WhatMod.enabled && playerClass != null) {
            String foundName = WhatMod.findModName(playerClass.getClass());
            if (foundName != null) {
                modName = new ColoredString(foundName, Settings.PURPLE_COLOR);
            }
        }
    }

    public EUITooltip(Keyword keyword)
    {
        this.title = keyword.PROPER_NAME;
        this.descriptions.add(keyword.DESCRIPTION);
    }

    public EUITooltip(EUIKeyword keyword)
    {
        this.title = keyword.PROPER_NAME;
        this.descriptions.add(keyword.DESCRIPTION);
        this.past = keyword.PAST;
        this.present = keyword.PRESENT;
        this.plural = keyword.PLURAL;
        // If the plural starts with $, use logic mode
        if (keyword.PLURAL != null && !keyword.PLURAL.isEmpty() && keyword.PLURAL.charAt(0) == '$') {
            this.useLogic = true;
        }
    }

    public static void registerID(String id, EUITooltip tooltip)
    {
        RegisteredIDs.put(id, tooltip);
        tooltip.ID = id;
    }

    public static void registerName(String name, EUITooltip tooltip)
    {
        RegisteredNames.put(name, tooltip);
    }

    public static Set<Map.Entry<String, EUITooltip>> getEntries() {
        return RegisteredIDs.entrySet();
    }

    public static EUITooltip findByName(String name)
    {
        return RegisteredNames.get(name);
    }

    public static EUITooltip findByID(String id)
    {
        return RegisteredIDs.get(id);
    }

    public static String findName(EUITooltip tooltip)
    {
        for (String key : RegisteredNames.keySet())
        {
            if (RegisteredNames.get(key) == tooltip)
            {
                return key;
            }
        }

        return null;
    }

    public static boolean canRenderTooltips()
    {
        return !EUIClassUtils.getFieldStatic(TipHelper.class, "renderedTipThisFrame", Boolean.class);
    }

    public static void canRenderTooltips(boolean canRender)
    {
        ReflectionHacks.setPrivateStatic(TipHelper.class, "renderedTipThisFrame", !canRender);

        if (!canRender)
        {
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

    public static EUITooltip fromMonsterIntent(AbstractMonster monster)
    {
        PowerTip tip = ReflectionHacks.getPrivate(monster, AbstractMonster.class, "intentTip");
        return tip != null ? fromPowerTip(tip) : null;
    }

    public static EUITooltip fromPowerTip(PowerTip tip)
    {
        EUITooltip newTip = new EUITooltip(tip.header, tip.body);
        if (tip.imgRegion != null)
        {
            newTip.icon = tip.imgRegion;
        }
        else if (tip.img != null)
        {
            newTip.icon = new TextureRegion(tip.img);
        }
        return newTip;
    }

    public static void updateTooltipIcons() {
        for (EUITooltip tip : IconUpdatingList) {
            tip.icon = tip.iconFunc.invoke();
        }
    }

    private static boolean tryRender()
    {
        final boolean canRender = canRenderTooltips();
        if (canRender)
        {
            canRenderTooltips(false);
        }

        return canRender;
    }

    public static void queueTooltip(EUITooltip tooltip)
    {
        float x = InputHelper.mX;
        float y = InputHelper.mY;
        x += (x < Settings.WIDTH * 0.75f) ? (Settings.scale * 40f) : -(BOX_W + (Settings.scale * 40f));
        y += (y < Settings.HEIGHT * 0.9f) ? (Settings.scale * 40f) : -(Settings.scale * 50f);
        queueTooltip(tooltip, x, y);
    }

    public static void queueTooltip(EUITooltip tooltip, float x, float y)
    {
        if (tryRender())
        {
            EUITooltip cur = tooltip;
            while (cur != null)
            {
                tooltips.add(cur);
                cur = cur.child;
            }
            genericTipPos.x = x;
            genericTipPos.y = y;
            EUI.addPriorityPostRender(EUITooltip::renderGeneric);
        }
    }

    public static void queueTooltips(Collection<EUITooltip> tips)
    {
        float estHeight = EUIUtils.sum(tips, EUITooltip::height);
        float x = InputHelper.mX;
        float y = InputHelper.mY;
        x += (x < Settings.WIDTH * 0.75f) ? (Settings.scale * 40f) : -(BOX_W + (Settings.scale * 40f));
        y += (y < Settings.HEIGHT * 0.9f) ? (Settings.scale * 40f) : -(Settings.scale * 50f);
        if (y - estHeight < 0) {
            y += estHeight;
        }

        queueTooltips(tips, x, y);
    }

    public static void queueTooltips(Collection<EUITooltip> tips, float x, float y)
    {
        if (tryRender())
        {
            tooltips.addAll(tips);
            genericTipPos.x = x;
            genericTipPos.y = y;
            EUI.addPriorityPostRender(EUITooltip::renderGeneric);
        }
    }

    public static void queueTooltips(AbstractCreature source)
    {
        if (tryRender())
        {
            creature = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromCreature);
        }
    }

    public static <T extends AbstractCard & TooltipProvider> void queueTooltips(T source)
    {
        if (tryRender())
        {
            provider = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromCard);
        }
    }

    public static <T extends AbstractPotion & TooltipProvider> void queueTooltips(T source)
    {
        if (tryRender())
        {
            provider = (TooltipProvider) source;
            EUI.addPriorityPostRender(EUITooltip::renderFromPotion);
        }
    }

    public static <T extends AbstractRelic & TooltipProvider> void queueTooltips(T source)
    {
        if (tryRender())
        {
            provider = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromRelic);
        }
    }

    public static <T extends AbstractBlight & TooltipProvider> void queueTooltips(T source)
    {
        if (tryRender())
        {
            provider = source;
            EUI.addPriorityPostRender(EUITooltip::renderFromBlight);
        }
    }

    public static void renderFromCard(SpriteBatch sb)
    {
        AbstractCard card = EUIUtils.safeCast(provider, AbstractCard.class);
        if (card == null)
        {
            return;
        }

        inHand = AbstractDungeon.player != null && AbstractDungeon.player.hand.contains(card);

        if (lastProvider != provider) {
            lastProvider = provider;
            List<EUITooltip> pTips = provider.getTips();
            lastHoveredCreature = null;
            tooltips.clear();
            provider.generateDynamicTooltips(tooltips);
            for (EUITooltip tip : pTips)
            {
                if (tip.canRender && !tooltips.contains(tip))
                {
                    tooltips.add(tip);
                }
            }
        }

        final boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
        for (int i = 0; i < tooltips.size(); i++)
        {
            EUITooltip tip = tooltips.get(i);
            if (StringUtils.isNotEmpty(tip.ID))
            {
                if (tip.hideDescription == null)
                {
                    tip.hideDescription = EUIConfiguration.hideTipDescription(tip.ID);
                }

                if (!inHand && alt && Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i))
                {
                    EUIConfiguration.hideTipDescription(tip.ID, (tip.hideDescription ^= true), true);
                }
            }

            if (tip.hideDescription == null)
            {
                tip.hideDescription = false;
            }
        }

        float x;
        float y;
        if (provider.isPopup())
        {
            x = 0.78f * Settings.WIDTH;
            y = 0.85f * Settings.HEIGHT;
        }
        else
        {
            x = card.current_x;
            if (card.current_x < (float) Settings.WIDTH * 0.7f)
            {
                x += AbstractCard.IMG_WIDTH / 2f + CARD_TIP_PAD;
            }
            else
            {
                x -= AbstractCard.IMG_WIDTH / 2f + CARD_TIP_PAD + BOX_W;
            }

            y = card.current_y - BOX_EDGE_H;
            float size = 0;
            for (EUITooltip tip : tooltips)
            {
                if (tip.hideDescription || StringUtils.isEmpty(tip.description()))
                {
                    if (!inHand)
                    {
                        size += 0.2f;
                    }
                }
                else
                {
                    size += 1f;
                }
            }

            if (size > 3f && card.current_y < Settings.HEIGHT * 0.5f && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.CARD_REWARD)
            {
                float steps = (tooltips.size() - 3) * 0.4f;
                float multi = 1f - (card.current_y / (Settings.HEIGHT * 0.5f));

                y += AbstractCard.IMG_HEIGHT * (0.5f + MathUtils.round(multi * steps));
            }
            else
            {
                y += AbstractCard.IMG_HEIGHT * 0.5f;
            }
        }

        for (int i = 0; i < tooltips.size(); i++)
        {
            EUITooltip tip = tooltips.get(i);
            if (inHand && (tip.hideDescription || StringUtils.isEmpty(tip.description())))
            {
                continue;
            }

            y -= tip.render(sb, x, y, i) + BOX_EDGE_H * 3.15f;
        }

        EUICardPreview preview = provider.getPreview();
        if (preview != null)
        {
            preview.render(sb, card, card.upgraded || EUIGameUtils.canShowUpgrades(false), provider.isPopup());
        }
    }

    public static void renderFromPotion(SpriteBatch sb)
    {
        AbstractPotion potion = EUIUtils.safeCast(provider, AbstractPotion.class);
        if (potion == null)
        {
            return;
        }

        lastProvider = provider;
        List<EUITooltip> pTips = provider.getTips();

        float x;
        float y;
        if ((float) InputHelper.mX >= 1400.0F * Settings.scale)
        {
            x = InputHelper.mX - (350 * Settings.scale);
            y = InputHelper.mY - (50 * Settings.scale);
        }
        else if (CardCrawlGame.mainMenuScreen.screen == MainMenuScreen.CurScreen.POTION_VIEW)
        {
            x = 150 * Settings.scale;
            y = 800.0F * Settings.scale;
        }
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP && potion.tips.size() > 2 && !AbstractDungeon.player.hasPotion(potion.ID))
        {
            x = InputHelper.mX + (60 * Settings.scale);
            y = InputHelper.mY + (180 * Settings.scale);
        }
        else if (AbstractDungeon.player != null && AbstractDungeon.player.hasPotion(potion.ID))
        {
            x = InputHelper.mX + (60 * Settings.scale);
            y = InputHelper.mY - (30 * Settings.scale);
        }
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD)
        {
            x = 360 * Settings.scale;
            y = InputHelper.mY + (50 * Settings.scale);
        }
        else
        {
            x = InputHelper.mX + (50 * Settings.scale);
            y = InputHelper.mY + (50 * Settings.scale);
        }

        renderTipsImpl(sb, pTips, x, y);
    }


    public static void renderFromRelic(SpriteBatch sb)
    {
        AbstractRelic relic = EUIUtils.safeCast(provider, AbstractRelic.class);
        if (relic == null)
        {
            return;
        }

        lastProvider = provider;
        List<EUITooltip> pTips = provider.getTips();

        float x;
        float y;
        if ((float) InputHelper.mX >= 1400.0F * Settings.scale)
        {
            x = InputHelper.mX - (350 * Settings.scale);
            y = InputHelper.mY - (50 * Settings.scale);
        }
        else if (CardCrawlGame.mainMenuScreen.screen == MainMenuScreen.CurScreen.RELIC_VIEW)
        {
            x = 180 * Settings.scale;
            y = 0.7f * Settings.HEIGHT;
        }
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP && pTips.size() > 2 && !AbstractDungeon.player.hasRelic(relic.relicId))
        {
            x = InputHelper.mX + (60 * Settings.scale);
            y = InputHelper.mY + (180 * Settings.scale);
        }
        else if (AbstractDungeon.player != null && AbstractDungeon.player.hasRelic(relic.relicId))
        {
            x = InputHelper.mX + (60 * Settings.scale);
            y = InputHelper.mY - (30 * Settings.scale);
        }
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD)
        {
            x = 360 * Settings.scale;
            y = InputHelper.mY + (50 * Settings.scale);
        }
        else
        {
            x = InputHelper.mX + (50 * Settings.scale);
            y = InputHelper.mY + (50 * Settings.scale);
        }

        renderTipsImpl(sb, pTips, x, y);
    }

    public static void renderFromBlight(SpriteBatch sb)
    {
        AbstractBlight blight = EUIUtils.safeCast(provider, AbstractBlight.class);
        if (blight == null)
        {
            return;
        }

        lastProvider = provider;
        List<EUITooltip> pTips = provider.getTips();

        float x;
        float y;
        if ((float) InputHelper.mX >= 1400.0F * Settings.scale)
        {
            x = InputHelper.mX - (350 * Settings.scale);
            y = InputHelper.mY - (50 * Settings.scale);
        }
        else if (CardCrawlGame.mainMenuScreen.screen == MainMenuScreen.CurScreen.RELIC_VIEW)
        {
            x = 180 * Settings.scale;
            y = 0.7f * Settings.HEIGHT;
        }
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP && pTips.size() > 2 && !AbstractDungeon.player.hasBlight(blight.blightID))
        {
            x = InputHelper.mX + (60 * Settings.scale);
            y = InputHelper.mY + (180 * Settings.scale);
        }
        else if (AbstractDungeon.player != null && AbstractDungeon.player.hasBlight(blight.blightID))
        {
            x = InputHelper.mX + (60 * Settings.scale);
            y = InputHelper.mY - (30 * Settings.scale);
        }
        else if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD)
        {
            x = 360 * Settings.scale;
            y = InputHelper.mY + (50 * Settings.scale);
        }
        else
        {
            x = InputHelper.mX + (50 * Settings.scale);
            y = InputHelper.mY + (50 * Settings.scale);
        }

        renderTipsImpl(sb, pTips, x, y);
    }

    // TODO rework
    public static void renderFromCreature(SpriteBatch sb)
    {
        if (creature == null)
        {
            return;
        }

        float x;
        float y = creature.hb.cY + EUIRenderHelpers.calculateAdditionalOffset(tooltips, creature.hb.cY);
        if ((creature.hb.cX + creature.hb.width * 0.5f) < TIP_X_THRESHOLD)
        {
            x = creature.hb.cX + (creature.hb.width / 2.0F) + TIP_OFFSET_R_X;
        }
        else
        {
            x = creature.hb.cX - (creature.hb.width / 2.0F) + TIP_OFFSET_L_X;
        }

        if (lastHoveredCreature != creature) {
            lastHoveredCreature = creature;

            tooltips.clear();

            if (creature instanceof TooltipProvider)
            {
                lastProvider = provider = (TooltipProvider) creature;
                EUITooltip intentTip = provider.getIntentTip();
                if (intentTip != null)
                {
                    tooltips.add(intentTip);
                }
                EUICardPreview preview = provider.getPreview();
                if (preview != null)
                {
                    float previewOffset = (x < Settings.WIDTH * 0.1f) ? x + BOX_W : x - AbstractCard.IMG_WIDTH;
                    preview.render(sb, previewOffset, y, 0.8f, preview.getCard().upgraded);
                }
            }
            else
            {
                lastProvider = null;
                if (creature instanceof AbstractMonster)
                {
                    if (((AbstractMonster) creature).intent != AbstractMonster.Intent.NONE && EUIGameUtils.canViewEnemyIntents())
                    {
                        tooltips.add(fromMonsterIntent((AbstractMonster) creature));
                    }
                }
            }

            for (AbstractPower p : creature.powers)
            {
                if (p instanceof InvisiblePower)
                {
                    continue;
                }
                else if (p instanceof TooltipProvider) {
                    tooltips.add(((TooltipProvider) p).getTooltip());
                    continue;
                }

                final EUITooltip tip = new EUITooltip(p.name, p.description);
                if (p.region48 != null)
                {
                    tip.icon = p.region48;
                }

                if (tip.icon == null && p.img != null)
                {
                    tip.setIcon(p.img, 6);
                }

                tooltips.add(tip);
            }
        }

        final float original_y = y;
        final float offset_x = (x > TIP_X_THRESHOLD) ? BOX_W : -BOX_W;
        float offset = 0.0F;

        float offsetChange;
        for (int i = 0; i < tooltips.size(); i++)
        {
            EUITooltip tip = tooltips.get(i);
            offsetChange = EUIRenderHelpers.getTooltipHeight(tip) + BOX_EDGE_H * 3.15F;
            if ((offset + offsetChange) >= (Settings.HEIGHT * 0.7F))
            {
                offset = 0.0F;
                y = original_y;
                x += offset_x;
            }

            if (tip.hideDescription == null)
            {
                tip.hideDescription = !StringUtils.isEmpty(tip.ID) && EUIConfiguration.hideTipDescription(tip.ID);
            }

            y -= tip.render(sb, x, y, i) + BOX_EDGE_H * 3.15f;
            offset += offsetChange;
        }
    }

    public static void renderGeneric(SpriteBatch sb)
    {
        renderTipsImpl(sb, tooltips, genericTipPos.x, genericTipPos.y);
    }

    protected static void renderTipsImpl(SpriteBatch sb, List<EUITooltip> tips, float x, float y)
    {
        for (int i = 0; i < tips.size(); i++)
        {
            final EUITooltip tip = tips.get(i);
            if (tip.hideDescription == null)
            {
                tip.hideDescription = !StringUtils.isEmpty(tip.ID) && EUIConfiguration.hideTipDescription(tip.ID);
            }

            if (!tip.hideDescription && tip.canRender)
            {
                y -= tip.render(sb, x, y, i) + BOX_EDGE_H * 3.15f;
            }
        }
    }

    public boolean hideDescription() {
        if (hideDescription == null)
        {
            hideDescription = !StringUtils.isEmpty(ID) && EUIConfiguration.hideTipDescription(ID);
        }
        return hideDescription;
    }

    public boolean is(EUITooltip tooltip)
    {
        return tooltip != null && ID.equals(tooltip.ID);
    }

    public void invalidateHeight()
    {
        lastHeight = null;
        lastTextHeight = null;
        lastModNameHeight = null;
        lastSubHeaderHeight = null;
    }

    public float height() {
        if (lastHeight == null)
        {
            BitmapFont descFont = getDescriptionFont();
            String desc = description();
            lastTextHeight = EUISmartText.getSmartHeight(descFont, desc, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);
            lastModNameHeight = (modName != null) ? EUISmartText.getSmartHeight(descFont, modName.text, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - TIP_DESC_LINE_SPACING : 0;
            lastSubHeaderHeight = (subHeader != null) ? EUISmartText.getSmartHeight(descFont, subHeader.text, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - TIP_DESC_LINE_SPACING * 1.5f : 0;
            lastHeight = (hideDescription() || StringUtils.isEmpty(desc)) ? (-40f * Settings.scale) : (-(lastTextHeight + lastModNameHeight + lastSubHeaderHeight) - 7f * Settings.scale);
        }
        return lastHeight;
    }

    public float render(SpriteBatch sb, float x, float y, int index)
    {
        if (EUIHotkeys.cycle.isJustPressed()) {
            cycleDescription();
        }
        if (descriptions.size() > 1 && (subText == null || subText.text == null || subText.text.isEmpty())) {
            updateCycleText();
        }

        BitmapFont descFont = getDescriptionFont();
        BitmapFont hFont = getHeaderFont();
        String desc = description();

        final float h = height();

        if (renderBg)
        {
            sb.setColor(Settings.TOP_PANEL_SHADOW_COLOR);
            sb.draw(ImageMaster.KEYWORD_TOP, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
            sb.draw(ImageMaster.KEYWORD_BODY, x + SHADOW_DIST_X, y - h - BOX_EDGE_H - SHADOW_DIST_Y, BOX_W, h + BOX_EDGE_H);
            sb.draw(ImageMaster.KEYWORD_BOT, x + SHADOW_DIST_X, y - h - BOX_BODY_H - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
            sb.setColor(Color.WHITE);
            sb.draw(ImageMaster.KEYWORD_TOP, x, y, BOX_W, BOX_EDGE_H);
            sb.draw(ImageMaster.KEYWORD_BODY, x, y - h - BOX_EDGE_H, BOX_W, h + BOX_EDGE_H);
            sb.draw(ImageMaster.KEYWORD_BOT, x, y - h - BOX_BODY_H, BOX_W, BOX_EDGE_H);
        }

        if (icon != null)
        {
            // To render it on the right: x + BOX_W - TEXT_OFFSET_X - 28 * Settings.scale
            renderTipEnergy(sb, icon, x + TEXT_OFFSET_X, y + ORB_OFFSET_Y, 28 * iconmultiW, 28 * iconmultiH);
            FontHelper.renderFontLeftTopAligned(sb, hFont, title, x + TEXT_OFFSET_X * 2.5f, y + HEADER_OFFSET_Y, Settings.GOLD_COLOR);
        }
        else
        {
            FontHelper.renderFontLeftTopAligned(sb, hFont, title, x + TEXT_OFFSET_X, y + HEADER_OFFSET_Y, Settings.GOLD_COLOR);
        }

        if (!StringUtils.isEmpty(desc))
        {
            if (provider != null && StringUtils.isNotEmpty(ID) && !inHand && index >= 0)
            {
                FontHelper.renderFontRightTopAligned(sb, descFont, ALT_STRING + (index + 1), x + BODY_TEXT_WIDTH * 1.07f, y + HEADER_OFFSET_Y * 1.33f, Settings.PURPLE_COLOR);
            }
            else if (subText != null)
            {
                FontHelper.renderFontRightTopAligned(sb, descFont, subText.text, x + BODY_TEXT_WIDTH * 1.07f, y + HEADER_OFFSET_Y * 1.33f, subText.color);
            }

            float yOff = y + BODY_OFFSET_Y;
            if (modName != null) {
                FontHelper.renderFontLeftTopAligned(sb, descFont, modName.text, x + TEXT_OFFSET_X, yOff, modName.color);
                yOff += lastModNameHeight;
            }
            if (subHeader != null) {
                FontHelper.renderFontLeftTopAligned(sb, descFont, subHeader.text, x + TEXT_OFFSET_X, yOff, subHeader.color);
                yOff += lastSubHeaderHeight;
            }

            if (!hideDescription())
            {
                EUISmartText.write(sb, descFont, desc, x + TEXT_OFFSET_X, yOff, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING, BASE_COLOR);
            }
        }

        return h;
    }

    protected BitmapFont getDescriptionFont()
    {
        return descriptionFont != null ? descriptionFont : EUIFontHelper.cardTooltipFont;
    }

    protected BitmapFont getHeaderFont()
    {
        return headerFont != null ? headerFont : EUIFontHelper.cardtooltiptitlefontNormal;
    }

    public EUITooltip setChild(EUITooltip other)
    {
        this.child = other;
        // Remove circular children to avoid infinite loops when queueing tooltips
        if (other.child == this)
        {
            other.child = null;
        }

        return this;
    }

    public EUITooltip setBadgeBackground(Color color)
    {
        this.backgroundColor = color;

        return this;
    }

    public EUITooltip setIconSizeMulti(float w, float h)
    {
        this.iconmultiW = w;
        this.iconmultiH = h;

        return this;
    }

    public EUITooltip setIcon(AbstractRelic relic) {
        return this.setIcon(relic.img, 4);
    }

    public EUITooltip setIcon(TextureRegion region)
    {
        this.icon = region;

        return this;
    }

    public EUITooltip setIcon(TextureRegion region, int div)
    {
        int w = region.getRegionWidth();
        int h = region.getRegionHeight();
        int x = region.getRegionX();
        int y = region.getRegionY();
        int half_div = div / 2;
        this.icon = new TextureRegion(region.getTexture(), x + (w / div), y + (h / div), w - (w / half_div), h - (h / half_div));

        return this;
    }

    public EUITooltip setIcon(Texture texture)
    {
        this.icon = new TextureRegion(texture);

        return this;
    }

    public EUITooltip setIcon(Texture texture, int div)
    {
        this.icon = EUIRenderHelpers.getCroppedRegion(texture, div);

        return this;
    }

    public EUITooltip setIconFunc(FuncT0<TextureRegion> iconFunc) {
        this.iconFunc = iconFunc;
        IconUpdatingList.add(this);

        return this;
    }

    public EUITooltip setText(String title, String... description) {
        return setText(title, Arrays.asList(description));
    }

    public EUITooltip setText(String title, List<String> description)
    {
        if (title != null)
        {
            setTitle(title);
        }
        if (description != null && description.size() > 0)
        {
            setDescriptions(description);
        }

        return this;
    }

    public EUITooltip canHighlight(boolean value)
    {
        this.canHighlight = value;

        return this;
    }

    public EUITooltip canFilter(boolean value)
    {
        this.canFilter = value;

        return this;
    }

    public EUITooltip renderBackground(boolean value)
    {
        this.renderBg = value;

        return this;
    }

    public EUITooltip showText(boolean value)
    {
        this.canRender = value;

        return this;
    }

    public EUITooltip setTitle(String title)
    {
        this.title = title;
        invalidateHeight();

        return this;
    }

    public EUITooltip setSubheader(ColoredString string)
    {
        this.subHeader = string;
        invalidateHeight();

        return this;
    }

    public EUITooltip setDescription(String description) {
        return setDescription(description, 0);
    }

    public EUITooltip setDescription(String description, int index)
    {
        if (this.descriptions.size() <= index) {
            this.descriptions.add(description);
        }
        else {
            this.descriptions.set(index, description);
        }
        updateCycleText();

        return this;
    }

    public EUITooltip setDescriptions(String... descriptions)
    {
        return setDescriptions(Arrays.asList(descriptions));
    }

    public EUITooltip setDescriptions(List<String> descriptions)
    {
        this.descriptions.clear();
        this.descriptions.addAll(descriptions);
        currentDesc = 0;
        updateCycleText();

        return this;
    }

    public EUITooltip setFonts(BitmapFont headerFont, BitmapFont descriptionFont)
    {
        this.headerFont = headerFont;
        this.descriptionFont = descriptionFont;
        return this;
    }

    public EUITooltip setHeaderFont(BitmapFont headerFont)
    {
        this.headerFont = headerFont;
        return this;
    }

    public EUITooltip setDescriptionFont(BitmapFont descriptionFont)
    {
        this.descriptionFont = descriptionFont;
        return this;
    }

    public void renderTipEnergy(SpriteBatch sb, TextureRegion region, float x, float y, float width, float height) {
        renderTipEnergy(sb, region, x, y, width, height, Settings.scale, Settings.scale, Color.WHITE);
    }

    public void renderTipEnergy(SpriteBatch sb, TextureRegion region, float x, float y, float width, float height, float scaleX, float scaleY, Color renderColor)
    {
        if (backgroundColor != null) {
            sb.setColor(backgroundColor);
            sb.draw(EUIRM.images.baseBadge.texture(), x, y, 0f, 0f,
                    width, height, Settings.scale, Settings.scale, 0f,
                    region.getRegionX(), region.getRegionY(), region.getRegionWidth(),
                    region.getRegionHeight(), false, false);
        }
        sb.setColor(renderColor);
        sb.draw(region.getTexture(), x, y, 0f, 0f,
                width, height, Settings.scale, Settings.scale, 0f,
                region.getRegionX(), region.getRegionY(), region.getRegionWidth(),
                region.getRegionHeight(), false, false);
    }

    public void cycleDescription() {
        if (descriptions.size() > 1) {
            setIndex((currentDesc + 1) % descriptions.size());
        }
    }

    public String description()
    {
        return currentDesc < descriptions.size() ? descriptions.get(currentDesc) : "";
    }

    public String getTitleOrIcon()
    {
        return (ID != null) ? "[" + ID + "]" : title;
    }

    public String past() {
        if (past == null) {
            past = EUIRM.strings.past(title);
        }
        return past;
    }

    public String plural() {
        if (plural == null) {
            plural = EUIRM.strings.plural(title);
        }
        return plural;
    }

    public String parsePlural(int amount) {
        if (plural == null) {
            plural = EUIRM.strings.plural(title);
        }
        return useLogic ? EUISmartText.parseLogicString(EUIUtils.format(plural.substring(1), amount)) : plural;
    }

    public String present() {
        if (present == null) {
            present = EUIRM.strings.present(title);
        }
        return present;
    }

    public String setIndex(int index) {
        if (descriptions.size() < 1) {
            return "";
        }
        currentDesc = MathUtils.clamp(index, 0, descriptions.size() - 1);
        updateCycleText();
        return description();
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

    @Override
    public String toString()
    {
        return getTitleOrIcon();
    }
}