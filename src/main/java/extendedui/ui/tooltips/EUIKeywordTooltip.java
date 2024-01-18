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
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.mod.stslib.icons.AbstractCustomIcon;
import com.evacipated.cardcrawl.mod.stslib.icons.CustomIconHelper;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.*;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.patches.EUIKeyword;
import extendedui.text.EUITextHelper;
import extendedui.ui.TextureCache;
import extendedui.utilities.ColoredString;
import extendedui.utilities.EUIFontHelper;

import java.util.*;

public class EUIKeywordTooltip extends EUITooltip {
    private static final HashMap<String, EUIKeywordTooltip> REGISTERED_NAMES = new HashMap<>();
    private static final HashMap<String, EUIKeywordTooltip> REGISTERED_IDS = new HashMap<>();
    private static final HashMap<String, EUIKeywordTooltip> TEMP_IDS = new HashMap<>();
    private static final HashSet<EUIKeywordTooltip> ICON_UPDATING_LIST = new HashSet<>();
    public static final float BASE_ICON_SIZE = 28;
    protected FuncT0<TextureRegion> iconFunc;
    protected FuncT0<EUICardPreview> previewFunc;
    protected Float lastModNameHeight;
    public Color badgeColor;
    public Color backgroundColor = Color.WHITE;
    public Color iconColor = Color.WHITE;
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
    public boolean canAdd = true;

    public EUIKeywordTooltip(String title) {
        super(title);
    }

    public EUIKeywordTooltip(String title, String description) {
        super(title, description);
    }

    public EUIKeywordTooltip(String title, String description, String modID) {
        super(title, description);

        setModID(modID);
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
        this.canAdd = other.canAdd;
        this.canHighlight = other.canHighlight;
    }

    public static void clearHidden() {
        for (EUIKeywordTooltip tooltip : REGISTERED_IDS.values()) {
            tooltip.canAdd = true;
        }
    }

    public static void clearTemp() {
        TEMP_IDS.clear();
    }

    /* Search for an existing ID, ignoring temporary IDs */
    public static EUIKeywordTooltip findByID(String id) {
        return REGISTERED_IDS.get(id);
    }

    /* Search for an existing ID, then fall back on temporary IDs */
    public static EUIKeywordTooltip findByIDTemp(String id) {
        EUIKeywordTooltip tip = REGISTERED_IDS.get(id);
        if (tip != null) {
            return tip;
        }
        return TEMP_IDS.get(id);
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

    public static Collection<EUIKeywordTooltip> getTips() {
        return REGISTERED_IDS.values();
    }

    public static void initializeVisibilityStatus() {
        for (Map.Entry<String, EUIKeywordTooltip> entry : getEntries()) {
            EUIKeywordTooltip tip = entry.getValue();
            tip.canAdd = !EUIConfiguration.getIsTipDescriptionHidden(entry.getKey());
            tip.headerFont = FontHelper.tipHeaderFont;
            tip.descriptionFont = EUIFontHelper.tooltipFont;
        }
    }

    public static void invalidateAllHeights() {
        for (EUIKeywordTooltip tip : REGISTERED_IDS.values()) {
            tip.invalidateHeight();
        }
    }

    public static void postInitialize() {
        initializeVisibilityStatus();
        registerKeywordIcons();
    }

    public static void registerID(String id, EUIKeywordTooltip tooltip) {
        REGISTERED_IDS.put(id, tooltip);
        tooltip.ID = id;
    }

    public static void registerIDTemp(String id, EUIKeywordTooltip tooltip) {
        TEMP_IDS.put(id, tooltip);
        tooltip.ID = id;
    }

    public static void registerKeywordIcons() {
        // Add CommonKeywordIcon and CustomIconHelper pictures to keywords. This REQUIRES stslib to run
        if (EUI.isStsLib()) {
            for (EUIKeywordTooltip tooltip : EUIUtils.map(getEntries(), Map.Entry::getValue)) {
                String title = tooltip.title;
                // CommonKeywordIcon
                if (title.equals(GameDictionary.INNATE.NAMES[0])) {
                    tooltip.setIcon(StSLib.BADGE_INNATE);
                }
                else if (title.equals(GameDictionary.ETHEREAL.NAMES[0])) {
                    tooltip.setIcon(StSLib.BADGE_ETHEREAL);
                }
                else if (title.equals(GameDictionary.RETAIN.NAMES[0])) {
                    tooltip.setIcon(StSLib.BADGE_RETAIN);
                }
                else if (title.equals(GameDictionary.EXHAUST.NAMES[0])) {
                    tooltip.setIcon(StSLib.BADGE_EXHAUST);
                }
                // CustomIconHelper IDs start with "[" and end with "Icon]"
                else {
                    String iconName = '[' + title + AbstractCustomIcon.CODE_ENDING;
                    AbstractCustomIcon icon = CustomIconHelper.getIcon(iconName);
                    if (icon != null) {
                        tooltip.setIcon(icon.region);
                        EUIKeywordTooltip.registerName(iconName, tooltip);
                    }
                }
            }
        }
    }

    public static void registerName(String name, EUIKeywordTooltip tooltip) {
        REGISTERED_NAMES.put(name, tooltip);
    }

    public static void removeTemp(EUIKeywordTooltip tip) {
        TEMP_IDS.remove(tip.ID);
    }

    public static void removeTemp(String id) {
        TEMP_IDS.remove(id);
    }

    public static void setHideTooltip(String id, boolean value) {
        EUIKeywordTooltip tooltip = findByIDTemp(id);
        if (tooltip != null) {
            tooltip.canAdd = !value;
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

    public EUICardPreview createPreview() {
        return this.previewFunc != null ? previewFunc.invoke() : null;
    }

    public EUIKeywordTooltip forceIcon(boolean value) {
        this.forceIcon = value;

        return this;
    }

    @Override
    public String getTitleOrIcon() {
        return forceIcon ? getTitleOrIconForced() : super.getTitleOrIcon();
    }

    @Override
    public float height() {
        if (lastHeight == null) {
            BitmapFont descFont = descriptionFont != null ? descriptionFont : EUIFontHelper.tooltipFont;
            lastTextHeight = EUITextHelper.getSmartHeight(descFont, description, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING);
            lastModNameHeight = (modName != null) ? EUITextHelper.getSmartHeight(descFont, modName.text, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - TIP_DESC_LINE_SPACING : 0;
            lastSubHeaderHeight = (subHeader != null) ? EUITextHelper.getSmartHeight(descFont, subHeader.text, BODY_TEXT_WIDTH, TIP_DESC_LINE_SPACING) - TIP_DESC_LINE_SPACING * 1.5f : 0;
            lastHeight = (-(lastTextHeight + lastModNameHeight + lastSubHeaderHeight) - 7f * Settings.scale);
        }
        return lastHeight;
    }

    @Override
    public void invalidateHeight() {
        lastHeight = null;
        lastTextHeight = null;
        lastModNameHeight = null;
        lastSubHeaderHeight = null;
    }

    @Override
    public boolean isRenderable() {
        return super.isRenderable() && canHighlight && canAdd;
    }

    @Override
    public EUIKeywordTooltip makeCopy() {
        return new EUIKeywordTooltip(this);
    }

    public String parsePlural(int amount) {
        if (plural == null) {
            plural = EUIRM.strings.plural(title);
        }
        return useLogic ? EUITextHelper.parseKeywordLogicWithAmount(plural, amount) : plural;
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

    @Override
    public float render(SpriteBatch sb, float x, float y, int index) {
        verifyFonts();
        final float h = height();
        renderBg(sb, Settings.TOP_PANEL_SHADOW_COLOR, x + SHADOW_DIST_X, y - SHADOW_DIST_Y, h);
        renderBg(sb, backgroundColor, x, y, h);
        renderTitle(sb, x, y);
        renderSubtext(sb, x, y);

        float yOff = y + BODY_OFFSET_Y;
        yOff += renderSubheader(sb, x, yOff);
        renderDescription(sb, x, yOff);

        return h;
    }

    @Override
    public float renderSubheader(SpriteBatch sb, float x, float y) {
        float subHeight = 0;
        if (modName != null) {
            FontHelper.renderFontLeftTopAligned(sb, descriptionFont, modName.text, x + TEXT_OFFSET_X, y, modName.color);
            subHeight += lastModNameHeight;
        }
        subHeight += super.renderSubheader(sb, x, y + subHeight);
        return subHeight;
    }

    public void renderTipEnergy(SpriteBatch sb, TextureRegion region, float x, float y, float width, float height) {
        renderTipEnergy(sb, region, x, y, width, height, Settings.scale, Settings.scale, iconColor);
    }

    public void renderTipEnergy(SpriteBatch sb, TextureRegion region, float x, float y, float width, float height, float scaleX, float scaleY, Color renderColor) {
        if (badgeColor != null) {
            sb.setColor(badgeColor);
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

    @Override
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

    public EUIKeywordTooltip setBackgroundColor(Color color) {
        this.backgroundColor = color;

        return this;
    }

    public EUIKeywordTooltip setBadgeBackground(Color color) {
        this.badgeColor = color;

        return this;
    }

    public EUIKeywordTooltip setCanAdd(boolean value) {
        this.canAdd = value;

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
        if (texture != null) {
            this.icon = new TextureRegion(texture);
        }

        return this;
    }

    public EUIKeywordTooltip setIcon(TextureRegion region) {
        this.icon = region;

        return this;
    }

    public EUIKeywordTooltip setIconColor(Color color) {
        this.iconColor = color;

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

    public EUIKeywordTooltip setModID(String modID) {
        if (WhatMod.enabled && modID != null) {
            ModInfo found = EUIGameUtils.getModInfoFromID(modID);
            if (found != null) {
                modName = new ColoredString(found.Name, Settings.PURPLE_COLOR);
            }
        }

        return this;
    }

    public EUIKeywordTooltip setPreviewFunc(FuncT0<EUICardPreview> previewFunc) {
        this.previewFunc = previewFunc;
        return this;
    }
}
