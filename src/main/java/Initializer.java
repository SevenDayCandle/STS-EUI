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
import extendedui.*;
import extendedui.patches.EUIKeyword;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.logging.log4j.LogManager;
import extendedui.configuration.EUIConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static extendedui.configuration.EUIConfiguration.BASE_SPRITES_DEFAULT;
import static extendedui.configuration.EUIConfiguration.ShouldReloadEffekseer;

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
        EUIConfiguration.Load();
        BaseMod.subscribe(this);
    }

    @Override
    public void receiveEditCards()
    {
        EUIRM.Initialize();
    }

    @Override
    public void receiveEditKeywords()
    {
        EUI.RegisterBasegameKeywords();
        String language = Settings.language.name().toLowerCase();
        this.loadKeywordStrings("eng");
        this.loadKeywordStrings(language);
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
        EUIConfiguration.PostInitialize();
        EUI.Initialize();
        EUI.RegisterGrammar(loadKeywords("eng", JSON_KEYWORD_EXTENSION));
        EUI.RegisterKeywordIcons();
        EUIRenderHelpers.InitializeBuffers();
        STSEffekseerManager.Initialize();
        LogManager.getLogger(STSEffekseerManager.class.getName()).info("Initialized STSEffekseerManager");
    }

    private Map<String, EUIKeyword> loadKeywords(String language, String path) {
        FileHandle handle = Gdx.files.internal(PATH + language + path);
        if (handle.exists()) {
            return EUIUtils.Deserialize(handle.readString(String.valueOf(StandardCharsets.UTF_8)), new TypeToken<Map<String, EUIKeyword>>(){}.getType());
        }
        return new HashMap<>();
    }

    private void loadKeywordStrings(String language)
    {
        FileHandle handle = Gdx.files.internal(PATH + language + JSON_KEYWORD);
        if (handle.exists()) {
            Map<String, EUIKeyword> keywords = EUIUtils.Deserialize(handle.readString(String.valueOf(StandardCharsets.UTF_8)), new TypeToken<Map<String, EUIKeyword>>(){}.getType());
            // Find standard tooltips. These tooltips only appear in the filters screen
            for (Map.Entry<String, EUIKeyword> pair : keywords.entrySet()) {
                EUIKeyword keyword = pair.getValue();
                EUITooltip tooltip = new EUITooltip(keyword).CanHighlight(false).ShowText(false);
                EUITooltip.RegisterID(pair.getKey(), tooltip);
                for (String name : keyword.NAMES)
                {
                    EUITooltip.RegisterName(name, tooltip);
                }
            }
        }
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
        if (EUIConfiguration.FlushOnGameStart.Get() || ShouldReloadEffekseer) {
            STSEffekseerManager.Reset();
            LogManager.getLogger(STSEffekseerManager.class.getName()).info("Reset STSEffekseerManager. Particles: " + BASE_SPRITES_DEFAULT);
            ShouldReloadEffekseer = false;
        }
        EUITooltip.UpdateTooltipIcons();
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom)
    {
        if (EUIConfiguration.FlushOnRoomStart.Get()) {
            STSEffekseerManager.Reset();
            LogManager.getLogger(STSEffekseerManager.class.getName()).info("Reset STSEffekseerManager");
        }
    }

    @Override
    public void receivePostUpdate()
    {
        EUIInputManager.PostUpdate();
    }
}