package extendedui;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.Diverse;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.potions.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import com.megacrit.cardcrawl.relics.RunicDome;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.TextureCache;
import extendedui.ui.screens.CardPoolScreen;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIClassUtils;
import javassist.ClassPool;
import javassist.CtClass;

import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.*;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod and https://github.com/SevenDayCandle/STS-FoolMod

public class EUIGameUtils {
    private static final HashMap<AbstractCard.CardColor, String> CUSTOM_COLOR_NAMES = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, AbstractPlayer.PlayerClass> COLOR_TO_CLASS = new HashMap<>();
    private static final HashMap<AbstractPlayer.PlayerClass, AbstractCard.CardColor> CLASS_TO_COLOR = new HashMap<>();
    private static final HashMap<Class, ModInfo> MOD_INFO_MAPPING = new HashMap<>();
    private static final HashMap<String, AbstractCard.CardColor> RELIC_COLORS = new HashMap<>();
    public static final Color COLOR_BASIC = new Color(0.52f, 0.52f, 0.52f, 1f);
    public static final Color COLOR_COMMON = new Color(0.7f, 0.7f, 0.7f, 1f);
    public static final Color COLOR_CURSE = new Color(0.44f, 0.30f, 0.30f, 1);
    public static final Color COLOR_RARE = new Color(1f, 0.8f, 0.35f, 1f);
    public static final Color COLOR_SPECIAL = new Color(0.7f, 1f, 0.5f, 1f);
    public static final Color COLOR_UNCOMMON = new Color(0.45f, 0.82f, 1f, 1f);

    public static void addRelicColor(String relicID, AbstractCard.CardColor color) {
        RELIC_COLORS.put(relicID, color);
    }

    public static boolean canReceiveAnyColorCard() {
        return (player != null && player.hasRelic(PrismaticShard.ID)) || ModHelper.isModEnabled(Diverse.ID);
    }

    // Imitate getAnyColorCard logic
    public static boolean canSeeAnyColorCardFromPool(AbstractCard c) {
        return canSeeCard(c) && (c.rarity == AbstractCard.CardRarity.COMMON || c.rarity == AbstractCard.CardRarity.UNCOMMON || c.rarity == AbstractCard.CardRarity.RARE || c.rarity == AbstractCard.CardRarity.CURSE) && c.type != AbstractCard.CardType.STATUS;
    }

    public static boolean canSeeCard(AbstractCard c) {
        return (Settings.treatEverythingAsUnlocked() || !UnlockTracker.isCardLocked(c.cardID));
    }

    public static boolean canShowUpgrades(boolean isLibrary) {
        return SingleCardViewPopup.isViewingUpgrade && (AbstractDungeon.player == null || isLibrary
                || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD
                || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.CARD_REWARD
                || AbstractDungeon.screen == CardPoolScreen.CARD_POOL_SCREEN);
    }

    public static boolean canViewAnyEnemyIntent() {
        return !AbstractDungeon.player.hasRelic(RunicDome.ID);
    }

    public static boolean canViewEnemyIntents(AbstractMonster mo) {
        return canViewEnemyIntents(mo.intent);
    }

    public static boolean canViewEnemyIntents(AbstractMonster.Intent intent) {
        return intent != AbstractMonster.Intent.NONE && intent != AbstractMonster.Intent.DEBUG && canViewAnyEnemyIntent();
    }

    public static Color colorForPotionRarity(AbstractPotion.PotionRarity rarity) {
        switch (rarity) {
            case UNCOMMON:
                return COLOR_UNCOMMON;
            case RARE:
                return COLOR_RARE;
            case COMMON:
                return COLOR_COMMON;
            default:
                return COLOR_SPECIAL;
        }
    }

    public static Color colorForRarity(AbstractCard.CardRarity rarity) {
        switch (rarity) {
            case SPECIAL:
                return COLOR_SPECIAL;
            case UNCOMMON:
                return COLOR_UNCOMMON;
            case RARE:
                return COLOR_RARE;
            case CURSE:
                return COLOR_CURSE;
            case BASIC:
                return COLOR_BASIC;
            case COMMON:
            default:
                return COLOR_COMMON;
        }
    }

    public static Color colorForRelicTier(AbstractRelic.RelicTier rarity) {
        switch (rarity) {
            case UNCOMMON:
                return COLOR_UNCOMMON;
            case RARE:
                return COLOR_RARE;
            case SHOP:
                return Settings.BLUE_TEXT_COLOR;
            case BOSS:
                return Settings.RED_TEXT_COLOR;
            case STARTER:
                return COLOR_BASIC;
            case COMMON:
                return COLOR_COMMON;
            case SPECIAL:
            default:
                return COLOR_SPECIAL;
        }
    }

    public static void copyVisualProperties(AbstractCard copy, AbstractCard original) {
        copy.current_y = original.current_y;
        copy.current_x = original.current_x;
        copy.target_x = original.target_x;
        copy.target_y = original.target_y;
        copy.targetDrawScale = original.targetDrawScale;
        copy.drawScale = original.drawScale;
        copy.transparency = original.transparency;
        copy.targetTransparency = original.targetTransparency;
        copy.angle = original.angle;
        copy.targetAngle = original.targetAngle;
    }

    public static ArrayList<AbstractBlight> getAllBlights() {
        if (BlightHelper.blights.isEmpty()) {
            BlightHelper.initialize();
        }
        return EUIUtils.map(BlightHelper.blights, EUIGameUtils::getSeenBlight);
    }

    public static ArrayList<AbstractRelic> getAllRelics() {
        return EUIUtils.mapAsNonnull(RELIC_COLORS.keySet(), RelicLibrary::getRelic);
    }

    public static OrthographicCamera getCamera() {
        return ReflectionHacks.getPrivate(Gdx.app.getApplicationListener(), CardCrawlGame.class, "camera");
    }

    public static AbstractCard.CardColor getCardColorForPlayer(AbstractPlayer.PlayerClass pc) {
        return CLASS_TO_COLOR.getOrDefault(pc, AbstractCard.CardColor.COLORLESS);
    }

    public static Color getColorColor(AbstractCard.CardColor co) {
        switch (co) {
            case RED:
                return new Color(0.5F, 0.1F, 0.1F, 1.0F);
            case GREEN:
                return new Color(0.25F, 0.55F, 0.0F, 1.0F);
            case BLUE:
                return new Color(0.01F, 0.34F, 0.52F, 1.0F);
            case PURPLE:
                return new Color(0.37F, 0.22F, 0.49F, 1.0F);
            case COLORLESS:
                return new Color(0.4F, 0.4F, 0.4F, 1.0F);
            case CURSE:
                return new Color(0.18F, 0.18F, 0.16F, 1.0F);
            default:
                return BaseMod.getTrailVfxColor(co);
        }
    }

    public static String getColorName(AbstractCard.CardColor co) {
        switch (co) {
            case RED:
                return CardLibraryScreen.TEXT[1];
            case GREEN:
                return CardLibraryScreen.TEXT[2];
            case BLUE:
                return CardLibraryScreen.TEXT[3];
            case PURPLE:
                return CardLibraryScreen.TEXT[8];
            case CURSE:
                return CardLibraryScreen.TEXT[5];
            case COLORLESS:
                return CardLibraryScreen.TEXT[4];
            default:
                return CUSTOM_COLOR_NAMES.getOrDefault(co, EUIUtils.capitalize(String.valueOf(co)));
        }
    }

    public static ArrayList<AbstractCard> getEveryColorCardForPoolDisplay() {
        return EUIUtils.filter(CardLibrary.cards.values(), EUIGameUtils::canSeeAnyColorCardFromPool);
    }

    public static ArrayList<CardGroup> getGameCardPools() {
        return EUIUtils.arrayList(colorlessCardPool, commonCardPool, uncommonCardPool, rareCardPool, curseCardPool);
    }

    public static ArrayList<ArrayList<String>> getGameRelicPools() {
        return EUIUtils.arrayList(commonRelicPool, uncommonRelicPool, rareRelicPool, bossRelicPool, shopRelicPool);
    }

    public static ArrayList<String> getInGameRelicIDs() {
        ArrayList<String> result = new ArrayList<>();
        result.addAll(AbstractDungeon.commonRelicPool);
        result.addAll(AbstractDungeon.uncommonRelicPool);
        result.addAll(AbstractDungeon.rareRelicPool);
        result.addAll(AbstractDungeon.shopRelicPool);
        result.addAll(AbstractDungeon.bossRelicPool);
        result.removeIf(UnlockTracker::isRelicLocked);
        return result;
    }

    public static AbstractRelic.LandingSound getLandingSound(AbstractRelic relic) {
        return ReflectionHacks.getPrivate(relic, AbstractRelic.class, "landingSFX");
    }

    public static String getModID(Object o) {
        return getModID(o.getClass());
    }

    public static String getModID(Class<?> objectClass) {
        ModInfo info = getModInfo(objectClass);
        return info != null ? info.ID : EUIUtils.EMPTY_STRING;
    }

    public static ModInfo getModInfo(Object o) {
        return getModInfo(o.getClass());
    }

    public static ModInfo getModInfo(Class<?> objectClass) {
        if (MOD_INFO_MAPPING.containsKey(objectClass)) {
            return MOD_INFO_MAPPING.get(objectClass);
        }

        CodeSource source = objectClass.getProtectionDomain().getCodeSource();
        try {
            URL jarURL = source.getLocation();
            // If the class was patched by ModTheSpire, getLocation will be null or may be EUI
            if (jarURL == null) {
                ClassPool pool = Loader.getClassPool();
                pool.childFirstLookup = true;
                CtClass ctCls = pool.get(objectClass.getName());
                String url = ctCls.getURL().getFile();
                int i = url.lastIndexOf('!');
                url = url.substring(0, i);
                jarURL = new URL(url);
            }

            for (ModInfo loadedInfo : Loader.MODINFOS) {
                if (jarURL.equals(loadedInfo.jarURL)) {
                    MOD_INFO_MAPPING.put(objectClass, loadedInfo);
                    return loadedInfo;
                }
            }
            MOD_INFO_MAPPING.put(objectClass, null);
        }
        catch (Exception e) {
            EUIUtils.logError(objectClass, "Failed to find source for codesource " + source);
            e.printStackTrace();
            MOD_INFO_MAPPING.put(objectClass, null);
        }

        return null;
    }

    public static ModInfo getModInfoFromID(String id) {
        for (ModInfo loadedInfo : Loader.MODINFOS) {
            if (loadedInfo.ID.equals(id)) {
                return loadedInfo;
            }
        }
        return null;
    }

    public static AbstractPlayer.PlayerClass getPlayerClassForCardColor(AbstractCard.CardColor pc) {
        return COLOR_TO_CLASS.get(pc);
    }

    public static AbstractCard.CardColor getPotionColor(String potionID) {
        // These are hardcoded in PotionHelper -_-
        switch (potionID) {
            case BloodPotion.POTION_ID:
            case Elixir.POTION_ID:
            case HeartOfIron.POTION_ID:
                return AbstractCard.CardColor.RED;
            case PoisonPotion.POTION_ID:
            case CunningPotion.POTION_ID:
            case GhostInAJar.POTION_ID:
                return AbstractCard.CardColor.GREEN;
            case FocusPotion.POTION_ID:
            case PotionOfCapacity.POTION_ID:
            case EssenceOfDarkness.POTION_ID:
                return AbstractCard.CardColor.BLUE;
            case BottledMiracle.POTION_ID:
            case StancePotion.POTION_ID:
            case Ambrosia.POTION_ID:
                return AbstractCard.CardColor.PURPLE;
        }
        AbstractPlayer.PlayerClass pc = BaseMod.getPotionPlayerClass(potionID);
        return CLASS_TO_COLOR.getOrDefault(pc, AbstractCard.CardColor.COLORLESS);
    }

    public static AbstractCard.CardColor getRelicColor(String relicID) {
        return RELIC_COLORS.getOrDefault(relicID, AbstractCard.CardColor.COLORLESS);
    }

    public static AbstractBlight getSeenBlight(String id) {
        AbstractBlight blight = BlightHelper.getBlight(id);
        blight.isSeen = true;
        EUIClassUtils.invoke(blight, AbstractBlight.class, "initializeTips");
        return blight;
    }

    public static ArrayList<CardGroup> getSourceCardPools() {
        return EUIUtils.arrayList(AbstractDungeon.srcColorlessCardPool, AbstractDungeon.srcCommonCardPool, AbstractDungeon.srcUncommonCardPool, AbstractDungeon.srcRareCardPool, AbstractDungeon.srcCurseCardPool);
    }

    public static SpriteBatch getSpriteBatch() {
        return ReflectionHacks.getPrivate(Gdx.app.getApplicationListener(), CardCrawlGame.class, "sb");
    }

    public static TextureCache iconForType(AbstractCard.CardType type) {
        switch (type) {
            case ATTACK:
                return EUIRM.images.typeAttack;
            case CURSE:
                return EUIRM.images.typeCurse;
            case POWER:
                return EUIRM.images.typePower;
            case SKILL:
                return EUIRM.images.typeSkill;
            default:
                return EUIRM.images.typeStatus;
        }
    }

    public static boolean inBattle() {
        AbstractRoom room = AbstractDungeon.currMapNode == null ? null : AbstractDungeon.currMapNode.getRoom();
        return room != null && AbstractDungeon.player != null && !room.isBattleOver && !AbstractDungeon.player.isDead && room.phase == AbstractRoom.RoomPhase.COMBAT;
    }

    public static boolean inGame() {
        return CardCrawlGame.GameMode.GAMEPLAY.equals(CardCrawlGame.mode);
    }

    public static boolean isObjectFromMod(Object o, ModInfo mod) {
        return getModInfo(o) == mod;
    }

    public static boolean isPlayerClass(AbstractPlayer.PlayerClass playerClass) {
        return AbstractDungeon.player != null && AbstractDungeon.player.chosenClass == playerClass;
    }

    public static CardGroup makeCardGroup(Collection<AbstractCard> cards) {
        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        group.group.addAll(cards);
        return group;
    }

    public static void registerColorPlayer(AbstractCard.CardColor co, AbstractPlayer.PlayerClass pc) {
        EUIGameUtils.COLOR_TO_CLASS.put(co, pc);
        EUIGameUtils.CLASS_TO_COLOR.put(pc, co);
    }

    public static void registerCustomColorName(AbstractCard.CardColor co, String name) {
        EUIGameUtils.CUSTOM_COLOR_NAMES.put(co, name);
    }

    public static void renderBlightTip(AbstractBlight po) {
        if (po instanceof TooltipProvider) {
            EUITooltip.queueTooltips(po);
        }
        else {
            TipHelper.queuePowerTips(InputHelper.mX + 50.0F * Settings.scale, InputHelper.mY + 50.0F * Settings.scale, po.tips);
        }
    }

    public static void renderPotionTip(AbstractPotion po) {
        if (po instanceof TooltipProvider) {
            EUITooltip.queueTooltips(po);
        }
        else {
            TipHelper.queuePowerTips(InputHelper.mX + 50.0F * Settings.scale, InputHelper.mY + 50.0F * Settings.scale, po.tips);
        }
    }

    public static float scale(float value) {
        return Settings.scale * value;
    }

    public static float screenH(float value) {
        return Settings.HEIGHT * value;
    }

    public static float screenW(float value) {
        return Settings.WIDTH * value;
    }

    public static String textForPotionEffect(AbstractPotion.PotionEffect size) {
        switch (size) {
            case RAINBOW:
                return EUIRM.strings.potion_rainbow;
            case OSCILLATE:
                return EUIRM.strings.potion_oscillate;
        }
        return EUIRM.strings.ui_na;
    }

    // Potion rarities use the same names as card rarities
    public static String textForPotionRarity(AbstractPotion.PotionRarity type) {
        switch (type) {
            case COMMON:
                return RunHistoryScreen.TEXT[12];

            case UNCOMMON:
                return RunHistoryScreen.TEXT[13];

            case RARE:
                return RunHistoryScreen.TEXT[14];

            default:
                return AbstractCard.TEXT[5];
        }
    }

    public static String textForPotionSize(AbstractPotion.PotionSize size) {
        switch (size) {
            case ANVIL:
                return EUIRM.strings.potion_anvil;
            case BOLT:
                return EUIRM.strings.potion_bolt;
            case BOTTLE:
                return EUIRM.strings.potion_bottle;
            case CARD:
                return EUIRM.strings.potion_card;
            case EYE:
                return EUIRM.strings.potion_eye;
            case FAIRY:
                return EUIRM.strings.potion_fairy;
            case GHOST:
                return EUIRM.strings.potion_ghost;
            case H:
                return EUIRM.strings.potion_h;
            case HEART:
                return EUIRM.strings.potion_heart;
            case JAR:
                return EUIRM.strings.potion_jar;
            case M:
                return EUIRM.strings.potion_m;
            case MOON:
                return EUIRM.strings.potion_moon;
            case S:
                return EUIRM.strings.potion_s;
            case SNECKO:
                return EUIRM.strings.potion_snecko;
            case SPHERE:
                return EUIRM.strings.potion_sphere;
            case SPIKY:
                return EUIRM.strings.potion_spiky;
            case T:
                return EUIRM.strings.potion_t;
        }
        return EUIUtils.EMPTY_STRING;
    }

    public static String textForRarity(AbstractCard.CardRarity type) {
        switch (type) {
            case BASIC:
                return EUIRM.strings.ui_basic; // STS calls this rarity "Starter" but this keyword is used by Animator/Clown Emporium to denote something else
            case COMMON:
                return RunHistoryScreen.TEXT[12];
            case UNCOMMON:
                return RunHistoryScreen.TEXT[13];
            case RARE:
                return RunHistoryScreen.TEXT[14];
            case SPECIAL:
                return RunHistoryScreen.TEXT[15];
            case CURSE:
                return RunHistoryScreen.TEXT[16];
            default:
                return AbstractCard.TEXT[5];
        }
    }

    public static String textForRelicLandingSound(AbstractRelic.LandingSound sfx) {
        switch (sfx) {
            case CLINK:
                return EUIRM.strings.relic_clink;
            case FLAT:
                return EUIRM.strings.relic_flat;
            case HEAVY:
                return EUIRM.strings.relic_heavy;
            case MAGICAL:
                return EUIRM.strings.relic_magical;
            case SOLID:
                return EUIRM.strings.relic_solid;
        }
        return EUIUtils.EMPTY_STRING;
    }

    public static String textForRelicTier(AbstractRelic.RelicTier type) {
        switch (type) {
            case STARTER:
                return SingleRelicViewPopup.TEXT[6];

            case COMMON:
                return SingleRelicViewPopup.TEXT[1];

            case UNCOMMON:
                return SingleRelicViewPopup.TEXT[7];

            case RARE:
                return SingleRelicViewPopup.TEXT[3];

            case SPECIAL:
                return SingleRelicViewPopup.TEXT[5];

            case BOSS:
                return SingleRelicViewPopup.TEXT[0];

            case SHOP:
                return SingleRelicViewPopup.TEXT[4];

            case DEPRECATED:
                return SingleRelicViewPopup.TEXT[2];

            default:
                return SingleRelicViewPopup.TEXT[9];
        }
    }

    public static String textForType(AbstractCard.CardType type) {
        switch (type) {
            case ATTACK:
                return AbstractCard.TEXT[0];

            case CURSE:
                return AbstractCard.TEXT[3];

            case STATUS:
                return AbstractCard.TEXT[7];

            case SKILL:
                return AbstractCard.TEXT[1];

            case POWER:
                return AbstractCard.TEXT[2];

            default:
                return AbstractCard.TEXT[5];
        }
    }
}
