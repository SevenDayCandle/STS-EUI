import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.Keyword;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import extendedui.*;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.logging.log4j.LogManager;
import extendedui.configuration.EUIConfiguration;
import extendedui.utilities.EUIFontHelper;

import java.lang.reflect.Field;

import static extendedui.configuration.EUIConfiguration.BASE_SPRITES_DEFAULT;
import static extendedui.configuration.EUIConfiguration.RequiresReload;

@SpireInitializer
public class Initializer implements PostInitializeSubscriber, EditStringsSubscriber, EditCardsSubscriber, StartGameSubscriber, OnStartBattleSubscriber
{
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
        EUI.Initialize();
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

    private void loadLangStrings(String language)
    {
        try
        {
            BaseMod.loadCustomStringsFile(UIStrings.class, "localization/extendedui/" + language + "/UIStrings.json");
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
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom)
    {
        if (EUIConfiguration.FlushOnRoomStart.Get()) {
            STSEffekseerManager.Reset();
            LogManager.getLogger(STSEffekseerManager.class.getName()).info("Reset STSEffekseerManager");
        }
    }
}