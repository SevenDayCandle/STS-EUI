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
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import extendedui.*;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.EUIHotkeys;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.utilities.ClassUtils;
import extendedui.utilities.ColoredString;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.Mathf;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class EUITooltip
{
    protected static final HashMap<String, EUITooltip> RegisteredIDs = new HashMap<>();
    protected static final HashMap<String, EUITooltip> RegisteredNames = new HashMap<>();

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
    public static final float POWER_ICON_OFFSET_X = 40f * Settings.scale;

    private final static ArrayList<String> EMPTY_LIST = new ArrayList<>();
    private static final ArrayList<EUITooltip> tooltips = new ArrayList<>();
    private static boolean inHand;
    private static TooltipProvider provider;
    private static AbstractCreature creature;
    private static Vector2 genericTipPos = new Vector2(0, 0);

    public ArrayList<String> descriptions = new ArrayList<>();
    public Boolean hideDescription = null;
    public Color backgroundColor;
    public ColoredString modName;
    public ColoredString subHeader;
    public ColoredString subText;
    public String id;
    public String title;
    public TextureRegion icon;
    public boolean canHighlight = true;
    public boolean canFilter = true;
    public boolean canRender = true;
    public float iconMulti_H = 1;
    public float iconMulti_W = 1;
    protected int currentDesc;

    public EUITooltip(String title, String... descriptions)
    {
        this(title, Arrays.asList(descriptions));
    }

    public EUITooltip(String title, List<String> descriptions)
    {
        this.title = title;
        this.descriptions.addAll(descriptions);
    }

    public EUITooltip(String title, AbstractPlayer.PlayerClass playerClass, String... descriptions) {
        this(title, playerClass, Arrays.asList(descriptions));
    }

    public EUITooltip(String title, AbstractPlayer.PlayerClass playerClass, List<String> descriptions)
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

    public static void RegisterID(String id, EUITooltip tooltip)
    {
        RegisteredIDs.put(id, tooltip);
        tooltip.id = id;
    }

    public static void RegisterName(String name, EUITooltip tooltip)
    {
        RegisteredNames.put(name, tooltip);
    }

    public static Set<Map.Entry<String, EUITooltip>> GetEntries() {
        return RegisteredIDs.entrySet();
    }

    public static EUITooltip FindByName(String name)
    {
        return RegisteredNames.get(name);
    }

    public static EUITooltip FindByID(String id)
    {
        return RegisteredIDs.get(id);
    }

    public static String FindName(EUITooltip tooltip)
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

    public static boolean CanRenderTooltips()
    {
        return !ClassUtils.GetFieldStatic(TipHelper.class, "renderedTipThisFrame", Boolean.class);
    }

    public static void CanRenderTooltips(boolean canRender)
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
            provider = null;
        }
    }

    private static boolean TryRender()
    {
        final boolean canRender = CanRenderTooltips();
        if (canRender)
        {
            CanRenderTooltips(false);
        }

        return canRender;
    }

    public static void QueueTooltip(EUITooltip tooltip)
    {
        float x = InputHelper.mX;
        float y = InputHelper.mY;
        x += (x < Settings.WIDTH * 0.75f) ? (Settings.scale * 40f) : -(BOX_W + (Settings.scale * 40f));
        y += (y < Settings.HEIGHT * 0.9f) ? (Settings.scale * 40f) : -(Settings.scale * 50f);
        QueueTooltip(tooltip, x, y);
    }

    public static void QueueTooltip(EUITooltip tooltip, float x, float y)
    {
        if (TryRender())
        {
            tooltips.add(tooltip);
            genericTipPos.x = x;
            genericTipPos.y = y;
            EUI.AddPriorityPostRender(EUITooltip::RenderGeneric);
        }
    }

    public static void QueueTooltips(Collection<EUITooltip> tips)
    {
        float estHeight = JavaUtils.Sum(tips, EUITooltip::Height);
        float x = InputHelper.mX;
        float y = InputHelper.mY;
        x += (x < Settings.WIDTH * 0.75f) ? (Settings.scale * 40f) : -(BOX_W + (Settings.scale * 40f));
        y += (y < Settings.HEIGHT * 0.9f) ? (Settings.scale * 40f) : -(Settings.scale * 50f);
        if (y - estHeight < 0) {
            y += estHeight;
        }

        QueueTooltips(tips, x, y);
    }

    public static void QueueTooltips(Collection<EUITooltip> tips, float x, float y)
    {
        if (TryRender())
        {
            tooltips.addAll(tips);
            genericTipPos.x = x;
            genericTipPos.y = y;
            EUI.AddPriorityPostRender(EUITooltip::RenderGeneric);
        }
    }

    public static void QueueTooltips(AbstractCreature source)
    {
        if (TryRender())
        {
            creature = source;
            EUI.AddPriorityPostRender(EUITooltip::RenderFromCreature);
        }
    }

    public static <T extends AbstractCard & TooltipProvider> void QueueTooltips(T source)
    {
        if (TryRender())
        {
            provider = source;
            EUI.AddPriorityPostRender(EUITooltip::RenderFromCard);
        }
    }

    public static <T extends AbstractPotion & TooltipProvider> void QueueTooltips(T source)
    {
        if (TryRender())
        {
            provider = (TooltipProvider) source;
            EUI.AddPriorityPostRender(EUITooltip::RenderFromPotion);
        }
    }

    public static <T extends AbstractRelic & TooltipProvider> void QueueTooltips(T source)
    {
        if (TryRender())
        {
            provider = source;
            EUI.AddPriorityPostRender(EUITooltip::RenderFromRelic);
        }
    }

    public static <T extends AbstractBlight & TooltipProvider> void QueueTooltips(T source)
    {
        if (TryRender())
        {
            provider = source;
            EUI.AddPriorityPostRender(EUITooltip::RenderFromBlight);
        }
    }

    public static void RenderFromCard(SpriteBatch sb)
    {
        AbstractCard card = JavaUtils.SafeCast(provider, AbstractCard.class);
        if (card == null)
        {
            return;
        }

        List<EUITooltip> pTips = provider.GetTips();

        int totalHidden = 0;
        inHand = AbstractDungeon.player != null && AbstractDungeon.player.hand.contains(card);
        tooltips.clear();
        provider.GenerateDynamicTooltips(tooltips);

        for (EUITooltip tip : pTips)
        {
            if (tip.canRender && !tooltips.contains(tip))
            {
                tooltips.add(tip);
            }
        }

        final boolean alt = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
        for (int i = 0; i < tooltips.size(); i++)
        {
            EUITooltip tip = tooltips.get(i);
            if (StringUtils.isNotEmpty(tip.id))
            {
                if (tip.hideDescription == null)
                {
                    tip.hideDescription = EUIConfiguration.HideTipDescription(tip.id);
                }

                if (!inHand && alt && Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i))
                {
                    EUIConfiguration.HideTipDescription(tip.id, (tip.hideDescription ^= true), true);
                }
            }

            if (tip.hideDescription == null)
            {
                tip.hideDescription = false;
            }
        }

        float x;
        float y;
        if (provider.IsPopup())
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
                if (tip.hideDescription || StringUtils.isEmpty(tip.Description()))
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

                y += AbstractCard.IMG_HEIGHT * (0.5f + Mathf.Round(multi * steps));
            }
            else
            {
                y += AbstractCard.IMG_HEIGHT * 0.5f;
            }
        }

        for (int i = 0; i < tooltips.size(); i++)
        {
            EUITooltip tip = tooltips.get(i);
            if (inHand && (tip.hideDescription || StringUtils.isEmpty(tip.Description())))
            {
                continue;
            }

            y -= tip.Render(sb, x, y, i) + BOX_EDGE_H * 3.15f;
        }

        EUICardPreview preview = provider.GetPreview();
        if (preview != null)
        {
            preview.Render(sb, card, card.upgraded || EUIGameUtils.CanShowUpgrades(false), provider.IsPopup());
        }
    }

    public static void RenderFromPotion(SpriteBatch sb)
    {
        AbstractPotion potion = JavaUtils.SafeCast(provider, AbstractPotion.class);
        if (potion == null)
        {
            return;
        }
        List<EUITooltip> pTips = provider.GetTips();

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

        for (int i = 0; i < pTips.size(); i++)
        {
            EUITooltip tip = pTips.get(i);
            if (tip.hideDescription == null)
            {
                tip.hideDescription = !StringUtils.isEmpty(tip.id) && EUIConfiguration.HideTipDescription(tip.id);
            }

            if (!tip.hideDescription)
            {
                y -= tip.Render(sb, x, y, i) + BOX_EDGE_H * 3.15f;
            }
        }
    }


    public static void RenderFromRelic(SpriteBatch sb)
    {
        AbstractRelic relic = JavaUtils.SafeCast(provider, AbstractRelic.class);
        if (relic == null)
        {
            return;
        }

        List<EUITooltip> pTips = provider.GetTips();

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

        for (int i = 0; i < pTips.size(); i++)
        {
            EUITooltip tip = pTips.get(i);
            if (tip.hideDescription == null)
            {
                tip.hideDescription = !StringUtils.isEmpty(tip.id) && EUIConfiguration.HideTipDescription(tip.id);
            }

            if (!tip.hideDescription)
            {
                y -= tip.Render(sb, x, y, i) + BOX_EDGE_H * 3.15f;
            }
        }
    }

    public static void RenderFromBlight(SpriteBatch sb)
    {
        AbstractBlight blight = JavaUtils.SafeCast(provider, AbstractBlight.class);
        if (blight == null)
        {
            return;
        }

        List<EUITooltip> pTips = provider.GetTips();

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

        for (int i = 0; i < pTips.size(); i++)
        {
            EUITooltip tip = pTips.get(i);
            if (tip.hideDescription == null)
            {
                tip.hideDescription = !StringUtils.isEmpty(tip.id) && EUIConfiguration.HideTipDescription(tip.id);
            }

            if (!tip.hideDescription)
            {
                y -= tip.Render(sb, x, y, i) + BOX_EDGE_H * 3.15f;
            }
        }
    }

    // TODO rework
    public static void RenderFromCreature(SpriteBatch sb)
    {
        if (creature == null)
        {
            return;
        }

        final float TIP_X_THRESHOLD = (Settings.WIDTH * 0.5f); // 1544.0F * Settings.scale;
        final float TIP_OFFSET_R_X = 20.0F * Settings.scale;
        final float TIP_OFFSET_L_X = -380.0F * Settings.scale;

        tooltips.clear();
        for (AbstractPower p : creature.powers)
        {
            if (p instanceof InvisiblePower)
            {
                continue;
            }
            else if (p instanceof TooltipProvider) {
                tooltips.add(((TooltipProvider) p).GetTooltip());
                continue;
            }

            final EUITooltip tip = new EUITooltip(p.name, p.description);
            if (p.region48 != null)
            {
                tip.icon = p.region48;
            }

            if (tip.icon == null && p.img != null)
            {
                tip.SetIcon(p.img, 6);
            }

            tooltips.add(tip);
        }

        float x;
        float y = creature.hb.cY + EUIRenderHelpers.CalculateAdditionalOffset(tooltips, creature.hb.cY);
        if ((creature.hb.cX + creature.hb.width * 0.5f) < TIP_X_THRESHOLD)
        {
            x = creature.hb.cX + (creature.hb.width / 2.0F) + TIP_OFFSET_R_X;
        }
        else
        {
            x = creature.hb.cX - (creature.hb.width / 2.0F) + TIP_OFFSET_L_X;
        }

        final float original_y = y;
        final float offset_x = (x > TIP_X_THRESHOLD) ? BOX_W : -BOX_W;
        float offset = 0.0F;

        float offsetChange;
        for (int i = 0; i < tooltips.size(); i++)
        {
            EUITooltip tip = tooltips.get(i);
            offsetChange = EUIRenderHelpers.GetTooltipHeight(tip) + BOX_EDGE_H * 3.15F;
            if ((offset + offsetChange) >= (Settings.HEIGHT * 0.7F))
            {
                offset = 0.0F;
                y = original_y;
                x += offset_x;
            }

            if (tip.hideDescription == null)
            {
                tip.hideDescription = !StringUtils.isEmpty(tip.id) && EUIConfiguration.HideTipDescription(tip.id);
            }

            y -= tip.Render(sb, x, y, i) + BOX_EDGE_H * 3.15f;
            offset += offsetChange;
        }
    }

    public static void RenderGeneric(SpriteBatch sb)
    {
        float x = genericTipPos.x;
        float y = genericTipPos.y;
        for (int i = 0; i < tooltips.size(); i++)
        {
            final EUITooltip tip = tooltips.get(i);
            if (tip.hideDescription == null)
            {
                tip.hideDescription = !StringUtils.isEmpty(tip.id) && EUIConfiguration.HideTipDescription(tip.id);
            }

            if (!tip.hideDescription)
            {
                y -= tip.Render(sb, x, y, i) + BOX_EDGE_H * 3.15f;
            }
        }
    }

    public boolean HideDescription() {
        if (hideDescription == null)
        {
            hideDescription = !StringUtils.isEmpty(id) && EUIConfiguration.HideTipDescription(id);
        }
        return hideDescription;
    }

    public boolean Is(EUITooltip tooltip)
    {
        return tooltip != null && id.equals(tooltip.id);
    }

    public float Height() {
        BitmapFont descriptionFont = provider == null ? FontHelper.tipBodyFont : EUIFontHelper.CardTooltipFont;
        String desc = Description();
        final float textHeight = EUIRenderHelpers.GetSmartHeight(descriptionFont, desc, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);
        final float modTextHeight = (modName != null) ? EUIRenderHelpers.GetSmartHeight(descriptionFont, modName.text, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - TIP_DESC_LINE_SPACING : 0;
        final float subHeaderTextHeight = (subHeader != null) ? EUIRenderHelpers.GetSmartHeight(descriptionFont, subHeader.text, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - TIP_DESC_LINE_SPACING * 1.5f : 0;
        return (HideDescription() || StringUtils.isEmpty(desc)) ? (-40f * Settings.scale) : (-(textHeight + modTextHeight + subHeaderTextHeight) - 7f * Settings.scale);
    }

    public float Render(SpriteBatch sb, float x, float y, int index)
    {
        if (EUIHotkeys.cycle.isJustPressed()) {
            CycleDescription();
        }
        if (descriptions.size() > 1 && (subText == null || subText.text.isEmpty())) {
            UpdateCycleText();
        }

        BitmapFont descriptionFont = provider == null ? FontHelper.tipBodyFont : EUIFontHelper.CardTooltipFont;
        String desc = Description();

        final float textHeight = EUIRenderHelpers.GetSmartHeight(descriptionFont, desc, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);
        final float modTextHeight = (modName != null) ? EUIRenderHelpers.GetSmartHeight(descriptionFont, modName.text, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - TIP_DESC_LINE_SPACING : 0;
        final float subHeaderTextHeight = (subHeader != null) ? EUIRenderHelpers.GetSmartHeight(descriptionFont, subHeader.text, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - TIP_DESC_LINE_SPACING * 1.5f : 0;
        final float h = (HideDescription() || StringUtils.isEmpty(desc)) ? (-40f * Settings.scale) : (-(textHeight + modTextHeight + subHeaderTextHeight) - 7f * Settings.scale);

        sb.setColor(Settings.TOP_PANEL_SHADOW_COLOR);
        sb.draw(ImageMaster.KEYWORD_TOP, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x + SHADOW_DIST_X, y - h - BOX_EDGE_H - SHADOW_DIST_Y, BOX_W, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x + SHADOW_DIST_X, y - h - BOX_BODY_H - SHADOW_DIST_Y, BOX_W, BOX_EDGE_H);
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.KEYWORD_TOP, x, y, BOX_W, BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BODY, x, y - h - BOX_EDGE_H, BOX_W, h + BOX_EDGE_H);
        sb.draw(ImageMaster.KEYWORD_BOT, x, y - h - BOX_BODY_H, BOX_W, BOX_EDGE_H);

        if (icon != null)
        {
            // To render it on the right: x + BOX_W - TEXT_OFFSET_X - 28 * Settings.scale
            renderTipEnergy(sb, icon, x + TEXT_OFFSET_X, y + ORB_OFFSET_Y, 28 * iconMulti_W, 28 * iconMulti_H);
            FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, title, x + TEXT_OFFSET_X * 2.5f, y + HEADER_OFFSET_Y, Settings.GOLD_COLOR);
        }
        else
        {
            FontHelper.renderFontLeftTopAligned(sb, FontHelper.tipHeaderFont, title, x + TEXT_OFFSET_X, y + HEADER_OFFSET_Y, Settings.GOLD_COLOR);
        }

        if (!StringUtils.isEmpty(desc))
        {
            if (provider != null && StringUtils.isNotEmpty(id) && !inHand && index >= 0)
            {
                FontHelper.renderFontRightTopAligned(sb, descriptionFont, "Alt+" + (index + 1), x + BODY_TEXT_WIDTH * 1.07f, y + HEADER_OFFSET_Y * 1.33f, Settings.PURPLE_COLOR);
            }
            else if (subText != null)
            {
                FontHelper.renderFontRightTopAligned(sb, descriptionFont, subText.text, x + BODY_TEXT_WIDTH * 1.07f, y + HEADER_OFFSET_Y * 1.33f, subText.color);
            }

            float yOff = y + BODY_OFFSET_Y;
            if (modName != null) {
                FontHelper.renderFontLeftTopAligned(sb, descriptionFont, modName.text, x + TEXT_OFFSET_X, yOff, modName.color);
                yOff += modTextHeight;
            }
            if (subHeader != null) {
                FontHelper.renderFontLeftTopAligned(sb, descriptionFont, subHeader.text, x + TEXT_OFFSET_X, yOff, subHeader.color);
                yOff += subHeaderTextHeight;
            }

            if (!HideDescription())
            {
                EUIRenderHelpers.WriteSmartText(sb, descriptionFont, desc, x + TEXT_OFFSET_X, yOff, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING, BASE_COLOR);
            }
        }

        return h;
    }

    public EUITooltip SetBadgeBackground(Color color)
    {
        this.backgroundColor = color;

        return this;
    }

    public EUITooltip SetIconSizeMulti(float w, float h)
    {
        this.iconMulti_W = w;
        this.iconMulti_H = h;

        return this;
    }

    public EUITooltip SetIcon(AbstractRelic relic) {
        return this.SetIcon(relic.img, 4);
    }

    public EUITooltip SetIcon(TextureRegion region)
    {
        this.icon = region;

        return this;
    }

    public EUITooltip SetIcon(TextureRegion region, int div)
    {
        int w = region.getRegionWidth();
        int h = region.getRegionHeight();
        int x = region.getRegionX();
        int y = region.getRegionY();
        int half_div = div / 2;
        this.icon = new TextureRegion(region.getTexture(), x + (w / div), y + (h / div), w - (w / half_div), h - (h / half_div));

        return this;
    }

    public EUITooltip SetIcon(Texture texture)
    {
        this.icon = new TextureRegion(texture);

        return this;
    }

    public EUITooltip SetIcon(Texture texture, int div)
    {
        this.icon = EUIRenderHelpers.GetCroppedRegion(texture, div);

        return this;
    }

    public EUITooltip SetText(String title, String... description) {
        return SetText(title, Arrays.asList(description));
    }

    public EUITooltip SetText(String title, List<String> description)
    {
        if (title != null)
        {
            SetTitle(title);
        }
        if (description != null && description.size() > 0)
        {
            SetDescriptions(description);
        }

        return this;
    }

    public EUITooltip CanHighlight(boolean value)
    {
        this.canHighlight = value;

        return this;
    }

    public EUITooltip ShowText(boolean value)
    {
        this.canRender = value;

        return this;
    }

    public EUITooltip SetTitle(String title)
    {
        this.title = title;

        return this;
    }

    public EUITooltip SetDescription(String description) {
        return SetDescription(description, 0);
    }

    public EUITooltip SetDescription(String description, int index)
    {
        if (this.descriptions.size() <= index) {
            this.descriptions.add(description);
        }
        else {
            this.descriptions.set(index, description);
        }

        return this;
    }

    public EUITooltip SetDescriptions(String... descriptions)
    {
        return SetDescriptions(Arrays.asList(descriptions));
    }

    public EUITooltip SetDescriptions(List<String> descriptions)
    {
        this.descriptions.clear();
        this.descriptions.addAll(descriptions);
        currentDesc = 0;
        UpdateCycleText();

        return this;
    }

    public void renderTipEnergy(SpriteBatch sb, TextureRegion region, float x, float y, float width, float height) {
        renderTipEnergy(sb, region, x, y, width, height, Settings.scale, Settings.scale, Color.WHITE);
    }

    public void renderTipEnergy(SpriteBatch sb, TextureRegion region, float x, float y, float width, float height, float scaleX, float scaleY, Color renderColor)
    {
        if (backgroundColor != null) {
            sb.setColor(backgroundColor);
            sb.draw(EUIRM.Images.Base_Badge.Texture(), x, y, 0f, 0f,
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

    public void CycleDescription() {
        if (descriptions.size() > 1) {
            SetIndex((currentDesc + 1) % descriptions.size());
        }
    }

    public String Description()
    {
        return currentDesc < descriptions.size() ? descriptions.get(currentDesc) : "";
    }

    public String GetTitleOrIcon()
    {
        return (id != null) ? "[" + id + "]" : title;
    }

    public String SetIndex(int index) {
        if (descriptions.size() < 1) {
            return "";
        }
        currentDesc = Mathf.Clamp(index, 0, descriptions.size() - 1);
        UpdateCycleText();
        return Description();
    }

    protected void UpdateCycleText() {
        if (descriptions.size() > 1) {
            if (subText == null) {
                subText = new ColoredString("", Settings.PURPLE_COLOR);
            }
            subText.SetText(EUIRM.Strings.KeyToCycle(EUIHotkeys.cycle.getKeyString()) + " (" + (currentDesc + 1) + "/" + descriptions.size() + ")");
        }
    }

    @Override
    public String toString()
    {
        return GetTitleOrIcon();
    }
}