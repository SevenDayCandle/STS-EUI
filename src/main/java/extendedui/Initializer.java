package extendedui;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import extendedui.configuration.EUIConfiguration;
import extendedui.patches.EUIKeyword;
import extendedui.text.EUISmartText;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.logging.log4j.LogManager;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static extendedui.configuration.EUIConfiguration.BASE_SPRITES_DEFAULT;
import static extendedui.configuration.EUIConfiguration.shouldReloadEffekseer;

@SpireInitializer
public class Initializer implements PostInitializeSubscriber, EditStringsSubscriber, EditKeywordsSubscriber, EditCardsSubscriber, StartGameSubscriber, OnStartBattleSubscriber, PostUpdateSubscriber
{
    public static final String PATH = "localization/extendedui/";
    public static final String JSON_KEYWORD_EXTENSION = "/KeywordExtensions.json";
    public static final String JSON_KEYWORD = "/KeywordStrings.json";
    public static final String JSON_UI = "/UIStrings.json";

    //Used by @SpireInitializer
    public static void initialize()
    {
        Initializer initializer = new Initializer();
    }

    public Initializer()
    {
        EUIConfiguration.load();
        BaseMod.subscribe(this);
    }

    @Override
    public void receiveEditCards()
    {
        EUIRM.initialize();
    }

    @Override
    public void receiveEditKeywords()
    {
        EUI.registerBasegameKeywords();
        String language = Settings.language.name().toLowerCase();
        this.registerKeywords("eng");
        this.registerKeywords(language);
    }

    @Override
    public void receiveEditStrings()
    {
        String language = Settings.language.name().toLowerCase();
        this.loadUIStrings("eng");
        this.loadUIStrings(language);
    }

    @Override
    public void receivePostInitialize()
    {
        EUIConfiguration.postInitialize();
        EUI.initialize();
        EUI.registerGrammar(loadKeywords("eng", JSON_KEYWORD_EXTENSION));
        EUI.registerKeywordIcons();
        EUIRenderHelpers.initializeBuffers();
        STSEffekseerManager.initialize();
        ShaderDebugger.initialize();
        HitboxDebugger.initialize();
        LogManager.getLogger(STSEffekseerManager.class.getName()).info("Initialized STSEffekseerManager");
    }

    private Map<String, EUIKeyword> loadKeywords(String language, String path) {
        return EUI.loadKeywords(Gdx.files.internal(PATH + language + path));
    }

    private void registerKeywords(String language)
    {
        EUI.registerKeywords(Gdx.files.internal(PATH + language + JSON_KEYWORD));
    }

    private void loadUIStrings(String language)
    {
        try
        {
            BaseMod.loadCustomStringsFile(UIStrings.class, PATH + language + JSON_UI);
        }
        catch (GdxRuntimeException var4)
        {
            LogManager.getLogger(STSEffekseerManager.class.getName()).error(var4.getMessage());
            if (!var4.getMessage().startsWith("File not found:"))
            {
                throw var4;
            }
        }
    }

    @Override
    public void receiveStartGame()
    {
        if (EUIConfiguration.flushOnGameStart.get() || shouldReloadEffekseer) {
            STSEffekseerManager.reset();
            LogManager.getLogger(STSEffekseerManager.class.getName()).info("Reset STSEffekseerManager. Particles: " + BASE_SPRITES_DEFAULT);
            shouldReloadEffekseer = false;
        }
        EUITooltip.updateTooltipIcons();
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom)
    {
        if (EUIConfiguration.flushOnRoomStart.get()) {
            STSEffekseerManager.reset();
            LogManager.getLogger(STSEffekseerManager.class.getName()).info("Reset STSEffekseerManager");
        }
    }

    @Override
    public void receivePostUpdate()
    {
        EUIInputManager.postUpdate();
    }
}