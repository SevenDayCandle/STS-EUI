package stseffekseer;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import stseffekseer.ui.tooltips.EUITooltip;

import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static stseffekseer.ui.AbstractScreen.EUI_SCREEN;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod and https://github.com/SevenDayCandle/STS-FoolMod

public class EUIGameUtils {
    private static final HashMap<CodeSource, ModInfo> ModInfoMapping = new HashMap<>();

    public static boolean CanShowUpgrades(boolean isLibrary)
    {
        return SingleCardViewPopup.isViewingUpgrade && (AbstractDungeon.player == null || isLibrary
                || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD
                || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.CARD_REWARD
                || AbstractDungeon.screen == EUI_SCREEN);
    }

    public static void CopyVisualProperties(AbstractCard copy, AbstractCard original) {
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

    public static boolean IsObjectFromMod(Object o, ModInfo mod) {
        return GetModInfo(o) == mod;
    }

    public static ArrayList<CardGroup> GetCardPools() {
        ArrayList<CardGroup> result = new ArrayList<>();
        result.add(AbstractDungeon.colorlessCardPool);
        result.add(AbstractDungeon.commonCardPool);
        result.add(AbstractDungeon.uncommonCardPool);
        result.add(AbstractDungeon.rareCardPool);
        result.add(AbstractDungeon.curseCardPool);
        return result;
    }

    public static ArrayList<CardGroup> GetSourceCardPools() {
        ArrayList<CardGroup> result = new ArrayList<>();
        result.add(AbstractDungeon.srcColorlessCardPool);
        result.add(AbstractDungeon.srcCommonCardPool);
        result.add(AbstractDungeon.srcUncommonCardPool);
        result.add(AbstractDungeon.srcRareCardPool);
        result.add(AbstractDungeon.srcCurseCardPool);
        return result;
    }
    public static int GetTotalCardsInPlay()
    {
        return AbstractDungeon.colorlessCardPool.size()
                + AbstractDungeon.commonCardPool.size()
                + AbstractDungeon.uncommonCardPool.size()
                + AbstractDungeon.rareCardPool.size()
                + AbstractDungeon.curseCardPool.size();
    }

    public static ModInfo GetModInfo(Object o) {
        return GetModInfo(o.getClass());
    }

    public static ModInfo GetModInfo(Class<?> objectClass) {
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
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean InBattle() {
        AbstractRoom room = AbstractDungeon.currMapNode == null ? null : AbstractDungeon.currMapNode.getRoom();
        return room != null && AbstractDungeon.player != null && !room.isBattleOver && !AbstractDungeon.player.isDead && room.phase == AbstractRoom.RoomPhase.COMBAT;
    }

    public static boolean InGame() {
        return CardCrawlGame.GameMode.GAMEPLAY.equals(CardCrawlGame.mode);
    }

    public static boolean IsPlayerClass(AbstractPlayer.PlayerClass playerClass) {
        return AbstractDungeon.player != null && AbstractDungeon.player.chosenClass == playerClass;
    }

    public static void ScanForTips(String rawDesc, ArrayList<EUITooltip> tips) {
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

            EUITooltip tip = EUITooltip.FindByName(s);
            if (tip != null && !tips.contains(tip))
            {
                tips.add(tip);
            }
        }
        while (true);
    }

    public static float Scale(float value)
    {
        return Settings.scale * value;
    }

    public static float ScreenW(float value)
    {
        return Settings.WIDTH * value;
    }

    public static float ScreenH(float value)
    {
        return Settings.HEIGHT * value;
    }

    public static String TextForType(AbstractCard.CardType type) {
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
