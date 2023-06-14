package extendedui.ui.tooltips;

import basemod.patches.whatmod.WhatMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.patches.EUIKeyword;
import extendedui.text.EUISmartText;
import extendedui.ui.TextureCache;
import extendedui.utilities.ColoredString;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EUIKeywordTooltip extends EUITooltip {
    private static final HashMap<String, EUIKeywordTooltip> REGISTERED_NAMES = new HashMap<>();
    private static final HashMap<String, EUIKeywordTooltip> REGISTERED_IDS = new HashMap<>();
    private static final HashSet<EUIKeywordTooltip> ICON_UPDATING_LIST = new HashSet<>();
    public static final float BASE_ICON_SIZE = 28;
    protected FuncT0<TextureRegion> iconFunc;
    protected Float lastModNameHeight;
    public Color backgroundColor;
    public ColoredString modName;
    public String past;
    public String plural;
    public String present;
    public String progressive;
    public TextureRegion icon;
    public boolean canHighlight = true;
    public boolean canFilter = true;
    public boolean forceIcon;
    public float iconmultiH = 1;
    public float iconmultiW = 1;
    public boolean useLogic = false;

    public EUIKeywordTooltip(String title) {
        super(title);
    }

    public EUIKeywordTooltip(String title, String description) {
        super(title, description);
    }

    public EUIKeywordTooltip(String title, String description, String modID) {
        super(title, description);

        if (WhatMod.enabled && modID != null) {
            ModInfo found = EUIGameUtils.getModInfoFromID(modID);
            if (found != null) {
                modName = new ColoredString(found.Name, Settings.PURPLE_COLOR);
            }
        }
    }

    public EUIKeywordTooltip(Keyword keyword) {
        super(keyword.PROPER_NAME, keyword.DESCRIPTION);
    }

    public EUIKeywordTooltip(EUIKeyword keyword) {
        super(keyword.NAME, keyword.DESCRIPTION);
        this.past = keyword.PAST;
        this.present = keyword.PRESENT;
        this.progressive = keyword.PROGRESSIVE;
        this.plural = keyword.PLURAL;
        // If the plural starts with $, use logic mode
        if (keyword.PLURAL != null && !keyword.PLURAL.isEmpty() && keyword.PLURAL.charAt(0) == '$') {
            this.useLogic = true;
        }
    }

    public EUIKeywordTooltip(EUITooltip other) {
        super(other);
    }

    public EUIKeywordTooltip(EUIKeywordTooltip other) {
        super(other);
        this.past = other.past;
        this.present = other.present;
        this.progressive = other.progressive;
        this.plural = other.plural;
        this.useLogic = other.useLogic;
        this.modName = other.modName;
    }

    public static EUIKeywordTooltip findByID(String id) {
        return REGISTERED_IDS.get(id);
    }

    public static EUIKeywordTooltip findByName(String name) {
        return REGISTERED_NAMES.get(name);
    }

    public static String findName(EUITooltip tooltip) {
        for (String key : REGISTERED_NAMES.keySet()) {
            if (REGISTERED_NAMES.get(key) == tooltip) {
                return key;
            }
        }

        return null;
    }

    public static Set<Map.Entry<String, EUIKeywordTooltip>> getEntries() {
        return REGISTERED_IDS.entrySet();
    }

    public static void postInitialize() {
        for (Map.Entry<String, EUIKeywordTooltip> entry : getEntries()) {
            EUIKeywordTooltip tip = entry.getValue();
            tip.canRender = !EUIConfiguration.getIsTipDescriptionHidden(entry.getKey());
            tip.headerFont = EUIFontHelper.cardTooltipTitleFontNormal;
            tip.descriptionFont = EUIFontHelper.cardTooltipFont;
        }
    }

    public static void registerID(String id, EUIKeywordTooltip tooltip) {
        REGISTERED_IDS.put(id, tooltip);
        tooltip.ID = id;
    }

    public static void registerName(String name, EUIKeywordTooltip tooltip) {
        REGISTERED_NAMES.put(name, tooltip);
    }

    public static void setHideTooltip(String id, boolean value) {
        EUIKeywordTooltip tooltip = findByID(id);
        if (tooltip != null) {
            tooltip.canRender = !value;
        }
    }

    public static void updateTooltipIcons() {
        for (EUIKeywordTooltip tip : ICON_UPDATING_LIST) {
            tip.icon = tip.iconFunc.invoke();
        }
    }

    public EUIKeywordTooltip canFilter(boolean value) {
        this.canFilter = value;

        return this;
    }

    public EUIKeywordTooltip canHighlight(boolean value) {
        this.canHighlight = value;

        return this;
    }

    public EUIKeywordTooltip forceIcon(boolean value) {
        this.forceIcon = value;

        return this;
    }

    public float height() {
        if (lastHeight == null) {
            BitmapFont descFont = descriptionFont != null ? descriptionFont : EUIFontHelper.cardTooltipFont;
            lastTextHeight = EUISmartText.getSmartHeight(descFont, description, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);
            lastModNameHeight = (modName != null) ? EUISmartText.getSmartHeight(descFont, modName.text, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - TIP_DESC_LINE_SPACING : 0;
            lastSubHeaderHeight = (subHeader != null) ? EUISmartText.getSmartHeight(descFont, subHeader.text, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - TIP_DESC_LINE_SPACING * 1.5f : 0;
            lastHeight = (!canRender || StringUtils.isEmpty(description)) ? (-40f * Settings.scale) : (-(lastTextHeight + lastModNameHeight + lastSubHeaderHeight) - 7f * Settings.scale);
        }
        return lastHeight;
    }

    public void invalidateHeight() {
        lastHeight = null;
        lastTextHeight = null;
        lastModNameHeight = null;
        lastSubHeaderHeight = null;
    }

    public boolean isRenderable() {
        return super.isRenderable() && canHighlight;
    }

    public EUIKeywordTooltip makeCopy() {
        return new EUIKeywordTooltip(this);
    }

    public void renderTitle(SpriteBatch sb, float x, float y) {
        if (icon != null) {
            // To render it on the right: x + BOX_W - TEXT_OFFSET_X - 28 * Settings.scale
            renderTipEnergy(sb, icon, x + TEXT_OFFSET_X, y + ORB_OFFSET_Y, BASE_ICON_SIZE * iconmultiW, BASE_ICON_SIZE * iconmultiH);
            FontHelper.renderFontLeftTopAligned(sb, headerFont, title, x + TEXT_OFFSET_X * 2.5f, y + HEADER_OFFSET_Y, Settings.GOLD_COLOR);
        }
        else {
            FontHelper.renderFontLeftTopAligned(sb, headerFont, title, x + TEXT_OFFSET_X, y + HEADER_OFFSET_Y, Settings.GOLD_COLOR);
        }
    }

    public EUIKeywordTooltip showText(boolean value) {
        super.showText(value);

        return this;
    }

    public String parsePlural(int amount) {
        if (plural == null) {
            plural = EUIRM.strings.plural(title);
        }
        return useLogic ? EUISmartText.parseKeywordLogicWithAmount(plural, amount) : plural;
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

    public String present() {
        if (present == null) {
            present = EUIRM.strings.present(title);
        }
        return present;
    }

    // If progressive is not present, assume present tense works for it as well
    public String progressive() {
        if (progressive == null) {
            return present();
        }
        return progressive;
    }

    public void renderTipEnergy(SpriteBatch sb, TextureRegion region, float x, float y, float width, float height) {
        renderTipEnergy(sb, region, x, y, width, height, Settings.scale, Settings.scale, Color.WHITE);
    }

    public void renderTipEnergy(SpriteBatch sb, TextureRegion region, float x, float y, float width, float height, float scaleX, float scaleY, Color renderColor) {
        if (backgroundColor != null) {
            sb.setColor(backgroundColor);
            sb.draw(EUIRM.images.baseBadge.texture(), x, y, 0f, 0f,
                    width, height, scaleX, scaleY, 0f,
                    region.getRegionX(), region.getRegionY(), region.getRegionWidth(),
                    region.getRegionHeight(), false, false);
        }
        sb.setColor(renderColor);
        sb.draw(region.getTexture(), x, y, 0f, 0f,
                width, height, scaleX, scaleY, 0f,
                region.getRegionX(), region.getRegionY(), region.getRegionWidth(),
                region.getRegionHeight(), false, false);
    }

    public EUIKeywordTooltip setBadgeBackground(Color color) {
        this.backgroundColor = color;

        return this;
    }

    public EUIKeywordTooltip setIcon(AbstractRelic relic) {
        return this.setIcon(relic.img, 4);
    }

    public EUIKeywordTooltip setIcon(Texture texture, int div) {
        this.icon = EUIRenderHelpers.getCroppedRegion(texture, div);

        return this;
    }

    public EUIKeywordTooltip setIcon(TextureRegion region, int div) {
        int w = region.getRegionWidth();
        int h = region.getRegionHeight();
        int x = region.getRegionX();
        int y = region.getRegionY();
        int half_div = div / 2;
        this.icon = new TextureRegion(region.getTexture(), x + (w / div), y + (h / div), w - (w / half_div), h - (h / half_div));

        return this;
    }

    public EUIKeywordTooltip setIcon(TextureCache texture) {
        return setIcon(texture.texture());
    }

    public EUIKeywordTooltip setIcon(Texture texture) {
        this.icon = new TextureRegion(texture);

        return this;
    }

    public EUIKeywordTooltip setIcon(TextureRegion region) {
        this.icon = region;

        return this;
    }

    public EUIKeywordTooltip setIconFromPath(String imagePath) {
        if (Gdx.files.internal(imagePath).exists()) {
            setIcon(EUIRM.getTexture(imagePath));
        }
        else {
            EUIUtils.logWarning(this, "Could not load icon at " + imagePath);
        }
        return this;
    }

    public EUIKeywordTooltip setIconFromPowerRegion(String imagePath) {
        TextureAtlas.AtlasRegion region = AbstractPower.atlas.findRegion("48/" + imagePath);
        if (region != null) {
            setIcon(region);
        }
        else {
            EUIUtils.logWarning(this, "Could not load region at " + imagePath);
        }
        return this;
    }

    public EUIKeywordTooltip setIconFunc(FuncT0<TextureRegion> iconFunc) {
        this.iconFunc = iconFunc;
        ICON_UPDATING_LIST.add(this);

        return this;
    }

    public EUIKeywordTooltip setIconSizeMulti(float w, float h) {
        this.iconmultiW = w;
        this.iconmultiH = h;

        return this;
    }

    @Override
    public String getTitleOrIcon() {
        return forceIcon ? getTitleOrIconForced() : super.getTitleOrIcon();
    }
}
