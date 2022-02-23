import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import org.apache.logging.log4j.LogManager;
import stseffekseer.STSEffekseerManager;

import java.util.ArrayList;

@SpireInitializer
public class Initializer implements PostInitializeSubscriber
{
    //Used by @SpireInitializer
    public static void initialize(){
        Initializer initializer = new Initializer();
    }

    public Initializer() {
        BaseMod.subscribe(this);
    }

     @Override
     public void receivePostInitialize()
     {
         STSEffekseerManager.Initialize();
         LogManager.getLogger(STSEffekseerManager.class.getName()).info("Initialized STSEffekseerManager");
     }
}