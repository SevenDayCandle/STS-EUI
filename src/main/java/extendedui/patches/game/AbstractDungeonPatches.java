package extendedui.patches.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.buttons.DynamicBanner;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.STSEffekseerManager;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import static extendedui.ui.AbstractScreen.EUI_SCREEN;

public class AbstractDungeonPatches {
    private static boolean fromEUI;

    @SpirePatch(clz = AbstractDungeon.class, method = "closeCurrentScreen")
    public static class AbstractDungeonPatches_CloseCurrentScreen {
        @SpirePostfixPatch
        public static void postfix() {
            if (AbstractDungeon.screen != EUI_SCREEN) {
                EUI.postDispose();
                // Dungeon map needs to be manually closed after returning to the main screen
                // Also, boss chests might sometimes goof up
                if (fromEUI && EUIGameUtils.inGame()) {
                    if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MAP) {
                        AbstractDungeon.dungeonMapScreen.map.hideInstantly();
                        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.NONE)
                        {
                            AbstractRoom currentRoom = AbstractDungeon.getCurrRoom();
                            if (currentRoom != null && currentRoom.rewardTime) {
                                AbstractDungeon.previousScreen = AbstractDungeon.CurrentScreen.COMBAT_REWARD;
                            }
                        }
                    }
                    Settings.hideTopBar = false;
                    Settings.hideRelics = false;
                    fromEUI = false;
                }
            }
        }

        @SpirePrefixPatch
        public static void prefix() {
            if (AbstractDungeon.screen == EUI_SCREEN) {
                EUI.dispose();
                fromEUI = true;
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "openPreviousScreen")
    public static class AbstractDungeonPatches_OpenPreviousScreen {
        @SpirePrefixPatch
        public static void prefix(AbstractDungeon.CurrentScreen s) {
            if (EUI.currentScreen != null) {
                // closeCurrentScreen will set screen to NONE if the previous screen was null
                if (s == AbstractDungeon.CurrentScreen.NONE) {
                    AbstractDungeon.screen = EUI_SCREEN;
                }
                EUI.currentScreen.reopen();
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "render")
    public static class AbstractDungeon_Render {
        @SpireInsertPatch(locator = Locator.class)
        public static void insert(AbstractDungeon __instance, SpriteBatch sb) {
            EUI.preRender(sb);
        }

        @SpireInsertPatch(locator = Locator2.class)
        public static void insert2(AbstractDungeon __instance, SpriteBatch sb) {
            STSEffekseerManager.update();
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(DynamicBanner.class, "render");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(OverlayMenu.class, "render");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
