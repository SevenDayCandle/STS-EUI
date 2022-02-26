import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import stseffekseer.STSEffekseerManager;
import stseffekseer.configuration.STSConfiguration;
import stseffekseer.configuration.STSConfigurationOption;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static stseffekseer.configuration.STSConfiguration.BASE_SPRITES_DEFAULT;
import static stseffekseer.configuration.STSConfiguration.RequiresReload;

@SpireInitializer
public class Initializer implements PostInitializeSubscriber, EditStringsSubscriber, StartGameSubscriber, OnStartBattleSubscriber
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
    public void receivePostInitialize()
    {
        STSConfiguration.Load();
        STSEffekseerManager.Initialize();
        LogManager.getLogger(STSEffekseerManager.class.getName()).info("Initialized STSEffekseerManager");
    }

    @Override
    public void receiveEditStrings()
    {
        String language = Settings.language.name().toLowerCase();
        this.loadLangStrings("eng");
        this.loadLangStrings(language);
    }

    private void loadLangStrings(String language)
    {
        try
        {
            BaseMod.loadCustomStringsFile(UIStrings.class, "localization/stseffekseer/" + language + "/ui.json");
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
        if (STSConfiguration.FlushOnGameStart.Get() || RequiresReload) {
            STSEffekseerManager.Reset();
            LogManager.getLogger(STSEffekseerManager.class.getName()).info("Reset STSEffekseerManager. Particles: " + BASE_SPRITES_DEFAULT);
            RequiresReload = false;
        }
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom)
    {
        if (STSConfiguration.FlushOnRoomStart.Get()) {
            STSEffekseerManager.Reset();
            LogManager.getLogger(STSEffekseerManager.class.getName()).info("Reset STSEffekseerManager");
        }
    }
}