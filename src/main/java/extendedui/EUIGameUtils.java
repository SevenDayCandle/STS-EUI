package extendedui;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.RunicDome;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import extendedui.ui.tooltips.EUITooltip;

import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static extendedui.ui.AbstractScreen.EUI_SCREEN;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod and https://github.com/SevenDayCandle/STS-FoolMod

public class EUIGameUtils {
    public static final HashMap<AbstractCard.CardColor, String> CustomColorNames = new HashMap<>();
    private static final HashMap<CodeSource, ModInfo> ModInfoMapping = new HashMap<>();
    private static final HashMap<String, AbstractCard.CardColor> RelicColors = new HashMap<>();

    public static void addRelicColor(AbstractRelic relic, AbstractCard.CardColor color)
    {
        RelicColors.put(relic.relicId, color);
    }

    public static boolean canShowUpgrades(boolean isLibrary)
    {
        return SingleCardViewPopup.isViewingUpgrade && (AbstractDungeon.player == null || isLibrary
                || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD
                || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.CARD_REWARD
                || AbstractDungeon.screen == EUI_SCREEN);
    }

    public static boolean canViewEnemyIntents()
    {
        return AbstractDungeon.player.hasRelic(RunicDome.ID);
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

    public static ArrayList<String> getAllRelicIDs() {
        ArrayList<String> result = new ArrayList<>();
        result.addAll(AbstractDungeon.commonRelicPool);
        result.addAll(AbstractDungeon.uncommonRelicPool);
        result.addAll(AbstractDungeon.rareRelicPool);
        result.addAll(AbstractDungeon.shopRelicPool);
        result.addAll(AbstractDungeon.bossRelicPool);
        return result;
    }

    public static Color getColorColor(AbstractCard.CardColor co){
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
                return CustomColorNames.getOrDefault(co, EUIUtils.capitalize(String.valueOf(co)));
        }
    }

    public static AbstractCard.CardColor getRelicColor(String relicID) {
        return RelicColors.getOrDefault(relicID, AbstractCard.CardColor.COLORLESS);
    }

    public static ArrayList<CardGroup> getSourceCardPools() {
        ArrayList<CardGroup> result = new ArrayList<>();
        result.add(AbstractDungeon.srcColorlessCardPool);
        result.add(AbstractDungeon.srcCommonCardPool);
        result.add(AbstractDungeon.srcUncommonCardPool);
        result.add(AbstractDungeon.srcRareCardPool);
        result.add(AbstractDungeon.srcCurseCardPool);
        return result;
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

    public static ModInfo getModInfo(Object o) {
        return getModInfo(o.getClass());
    }

    public static ModInfo getModInfo(Class<?> objectClass) {
        CodeSource source = objectClass.getProtectionDomain().getCodeSource();
        ModInfo info = ModInfoMapping.get(source);
        if (info != null) {
            return info;
        }

        try {
            URL jarURL = source.getLocation().toURI().toURL();
            for (ModInfo loadedInfo : Loader.MODINFOS) {
                if (jarURL.equals(loadedInfo.jarURL)) {
                    ModInfoMapping.put(source, loadedInfo);
                    return loadedInfo;
                }
            }
        }
        catch (Exception ignored) {
        }

        return null;
    }

    public static boolean isPlayerClass(AbstractPlayer.PlayerClass playerClass) {
        return AbstractDungeon.player != null && AbstractDungeon.player.chosenClass == playerClass;
    }

    public static float scale(float value)
    {
        return Settings.scale * value;
    }

    public static void scanForTips(String rawDesc, ArrayList<EUITooltip> tips) {
        final Scanner desc = new Scanner(rawDesc);
        String s;
        boolean alreadyExists;
        do
        {
            if (!desc.hasNext())
            {
                desc.close();
                return;
            }

            s = desc.next();
            if (s.charAt(0) == '#')
            {
                s = s.substring(2);
            }

            s = s.replace(',', ' ');
            s = s.replace('.', ' ');

            if (s.length() > 4)
            {
                s = s.replace('[', ' ');
                s = s.replace(']', ' ');
            }

            s = s.trim();
            s = s.toLowerCase();

            EUITooltip tip = EUITooltip.findByName(s);
            if (tip != null && !tips.contains(tip))
            {
                tips.add(tip);
            }
        }
        while (true);
    }

    public static float screenH(float value)
    {
        return Settings.HEIGHT * value;
    }

    public static float screenW(float value)
    {
        return Settings.WIDTH * value;
    }

    public static String textForRarity(AbstractCard.CardRarity type) {
        switch (type)
        {
            case BASIC:
                return EUIRM.strings.uiBasic; // STS calls this rarity "Starter" but this keyword is used by Animator/Clown Emporium

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

    public static String textForRelicTier(AbstractRelic.RelicTier type) {
        switch (type)
        {
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
        switch (type)
        {
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
