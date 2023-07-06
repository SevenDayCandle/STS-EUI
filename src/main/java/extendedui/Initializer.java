package extendedui;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import extendedui.configuration.EUIConfiguration;
import extendedui.patches.EUIKeyword;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Map;

import static extendedui.configuration.EUIConfiguration.BASE_SPRITES_DEFAULT;
import static extendedui.configuration.EUIConfiguration.shouldReloadEffekseer;

@SpireInitializer
public class Initializer
        implements PostInitializeSubscriber, EditStringsSubscriber, EditKeywordsSubscriber, EditCardsSubscriber, StartGameSubscriber, OnStartBattleSubscriber, PostUpdateSubscriber, PostDeathSubscriber {
    public static final String PATH = "localization/extendedui/";
    public static final String JSON_KEYWORD_EXTENSION = "/KeywordExtensions.json";
    public static final String JSON_KEYWORD = "/KeywordStrings.json";
    public static final String JSON_UI = "/UIStrings.json";
    public static final String ENG_LOWER = "eng";

    public Initializer() {
        EUIConfiguration.load();
        BaseMod.subscribe(this);
    }

    //Used by @SpireInitializer
    public static void initialize() {
        Initializer initializer = new Initializer();
    }

    private Map<String, EUIKeyword> loadKeywords(String language, String path) {
        return EUI.loadKeywords(Gdx.files.internal(PATH + language + path));
    }

    private void loadUIStrings(String language) {
        try {
            BaseMod.loadCustomStringsFile(UIStrings.class, PATH + language + JSON_UI);
        }
        catch (GdxRuntimeException var4) {
            LogManager.getLogger(STSEffekseerManager.class.getName()).error(var4.getMessage());
            if (!var4.getMessage().startsWith("File not found:")) {
                throw var4;
            }
        }
    }

    @Override
    public void receiveEditCards() {
        EUIRM.initialize();
    }

    @Override
    public void receiveEditKeywords() {
        EUI.registerBasegameKeywords();
        String language = Settings.language.name().toLowerCase();
        this.registerKeywords(ENG_LOWER);
        EUI.registerGrammar(loadKeywords(ENG_LOWER, JSON_KEYWORD_EXTENSION));
        if (!ENG_LOWER.equals(language)) {
            this.registerKeywords(language);
            EUI.registerGrammar(loadKeywords(language, JSON_KEYWORD_EXTENSION));
        }
    }

    @Override
    public void receiveEditStrings() {
        String language = Settings.language.name().toLowerCase();
        this.loadUIStrings(ENG_LOWER);
        if (!ENG_LOWER.equals(language)) {
            this.loadUIStrings(language);
        }
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        if (EUIConfiguration.flushOnRoomStart.get()) {
            STSEffekseerManager.reset();
            LogManager.getLogger(STSEffekseerManager.class.getName()).info("Reset STSEffekseerManager");
        }
    }

    @Override
    public void receivePostDeath() {
        EUITourTooltip.clearTutorialQueue();
    }

    @Override
    public void receivePostInitialize() {
        EUIConfiguration.postInitialize();
        EUI.initialize();
        EUI.registerKeywordIcons();
        EUIRenderHelpers.initializeBuffers();
        STSEffekseerManager.initialize();
        ShaderDebugger.initialize();
        HitboxDebugger.initialize();
        EUITooltip.postInitialize();
    }

    @Override
    public void receivePostUpdate() {
        EUIInputManager.postUpdate();
    }

    @Override
    public void receiveStartGame() {
        if (EUIConfiguration.flushOnGameStart.get() || shouldReloadEffekseer) {
            STSEffekseerManager.reset();
            LogManager.getLogger(STSEffekseerManager.class.getName()).info("Reset STSEffekseerManager. Particles: " + BASE_SPRITES_DEFAULT);
            shouldReloadEffekseer = false;
        }
        EUIKeywordTooltip.updateTooltipIcons();
    }

    // EUI's own tooltips should not be highlighted
    private void registerKeywords(String language) {
        ArrayList<EUIKeywordTooltip> tips = EUI.registerKeywords(Gdx.files.internal(PATH + language + JSON_KEYWORD));
        for (EUIKeywordTooltip tip : tips) {
            tip.canHighlight(false).showText(false);
        }
    }
}