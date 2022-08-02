import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
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
import extendedui.utilities.EUIFontHelper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static extendedui.configuration.EUIConfiguration.BASE_SPRITES_DEFAULT;
import static extendedui.configuration.EUIConfiguration.RequiresReload;

@SpireInitializer
public class Initializer implements PostInitializeSubscriber, EditStringsSubscriber, EditCardsSubscriber, StartGameSubscriber, OnStartBattleSubscriber, PostUpdateSubscriber
{
    public static final String PATH = "localization/extendedui/";
    public static final String JSON_KEYWORDS = "/Keywords.json";

    //Used by @SpireInitializer
    public static void initialize()
    {
        Initializer initializer = new Initializer();
    }

    public Initializer()
    {
        BaseMod.subscribe(this);
    }

    @Override
    public void receiveEditCards()
    {
        EUIRM.Initialize();
    }

    @Override
    public void receivePostInitialize()
    {
        EUIConfiguration.Load();
        EUI.Initialize(loadCustomKeywords("eng"));
        EUIRenderHelpers.InitializeBuffers();
        STSEffekseerManager.Initialize();
        LogManager.getLogger(STSEffekseerManager.class.getName()).info("Initialized STSEffekseerManager");
    }

    @Override
    public void receiveEditStrings()
    {
        String language = Settings.language.name().toLowerCase();
        this.loadLangStrings("eng");
        this.loadLangStrings(language);
        EUIFontHelper.Initialize();
    }

    private Map<String, EUIKeyword> loadCustomKeywords(String language) {
        FileHandle handle = Gdx.files.internal(PATH + language + JSON_KEYWORDS);
        if (handle.exists()) {
            return JavaUtils.Deserialize(handle.readString(String.valueOf(StandardCharsets.UTF_8)), new TypeToken<Map<String, EUIKeyword>>(){}.getType());
        }
        return new HashMap<>();
    }

    private void loadLangStrings(String language)
    {
        try
        {
            BaseMod.loadCustomStringsFile(UIStrings.class, PATH + language + "/UIStrings.json");
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
        if (EUIConfiguration.FlushOnGameStart.Get() || RequiresReload) {
            STSEffekseerManager.Reset();
            LogManager.getLogger(STSEffekseerManager.class.getName()).info("Reset STSEffekseerManager. Particles: " + BASE_SPRITES_DEFAULT);
            RequiresReload = false;
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