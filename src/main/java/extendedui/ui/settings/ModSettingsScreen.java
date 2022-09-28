package extendedui.ui.settings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.EUIGameUtils;
import extendedui.configuration.STSConfigItem;
import extendedui.interfaces.markers.ModSettingsProvider;
import extendedui.ui.AbstractScreen;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.GUI_Button;
import extendedui.ui.controls.GUI_ButtonList;
import extendedui.ui.controls.GUI_Image;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

import static extendedui.ui.controls.GUI_ButtonList.BUTTON_H;

public class ModSettingsScreen extends AbstractScreen
{
    protected static final float OPTION_SIZE = Scale(40);
    protected static final float OFFSET_SIZE = Scale(420);
    protected static final float COLOR_BUTTON_SIZE = Scale(51);
    protected static final TextureCache modPanel = new TextureCache("img/ModPanelBg.png");
    protected static final HashMap<ModInfo, ArrayList<ModSettingsProvider<?>>> configSubscribers = new HashMap<>();
    protected static final HashMap<ModInfo, Float> offsets = new HashMap<>();
    protected static final AdvancedHitbox hb = new AdvancedHitbox(478.0F * Settings.scale, Settings.OPTION_Y - 376.0F, 1364.0F, 752.0F);
    protected final GUI_ButtonList modButtons = new GUI_ButtonList(7, ScreenW(0.14f), ScreenH(0.62f), Scale(240), BUTTON_H);
    public final MenuCancelButton button;
    private final GUI_Image background;
    private ModInfo activeMod;

    public static void AddSubscriber(ModInfo mod)
    {
        configSubscribers.putIfAbsent(mod, new ArrayList<>());
        offsets.putIfAbsent(mod, OFFSET_SIZE);
    }

    public static void SubscribeBoolean(ModInfo mod, STSConfigItem<Boolean> option, String label)
    {
        ArrayList<ModSettingsProvider<?>> list = configSubscribers.get(mod);
        float offY = offsets.getOrDefault(mod, OFFSET_SIZE);
        if (list != null)
        {
            ModSettingsToggle toggle = new ModSettingsToggle(new RelativeHitbox(hb, OPTION_SIZE * 2, OPTION_SIZE, OPTION_SIZE * 3.3f, offY, false), option, label);
            list.add(toggle);
            offsets.put(mod, offY -= toggle.hb.height * 1.1f);
        }
    }

    public static void SubscribeOption(ModInfo mod, ModSettingsProvider<?> option, String label)
    {
        ArrayList<ModSettingsProvider<?>> list = configSubscribers.get(mod);
        if (list != null)
        {
            list.add(option);
        }
    }

    public ModSettingsScreen()
    {
        super();
        background = new GUI_Image(modPanel.Texture(), hb);
        button = new MenuCancelButton();
    }

    public void Open() {
        super.Open(false, false);
        SingleCardViewPopup.isViewingUpgrade = false;
        this.button.show(MasterDeckViewScreen.TEXT[1]);

        modButtons.Clear();
        ArrayList<ModInfo> infos = new ArrayList<>(configSubscribers.keySet());
        infos.sort((a, b) -> StringUtils.compare(a.Name, b.Name));
        for (ModInfo info : infos)
        {
            MakeModButton(info);
        }

        if (infos.size() > 0)
        {
            SetActiveMod(infos.get(0));
        }
    }

    @Override
    protected void UpdateDungeonScreen()
    {
        // Allow settings screens to be previous
        if (AbstractDungeon.screen != EUI_SCREEN) {

            if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MAP
                    && AbstractDungeon.screen != AbstractDungeon.CurrentScreen.MASTER_DECK_VIEW) {
                AbstractDungeon.previousScreen = AbstractDungeon.screen;
            }

            AbstractDungeon.screen = EUI_SCREEN;
        }
    }

    @Override
    public void Reopen()
    {
        this.button.show(MasterDeckViewScreen.TEXT[1]);
    }

    public void SetActiveMod(ModInfo info)
    {
        if (configSubscribers.containsKey(info))
        {
            activeMod = info;
        }
    }

    @Override
    public void Update()
    {
        background.TryUpdate();
        modButtons.Update();

        ArrayList<ModSettingsProvider<?>> list = configSubscribers.get(activeMod);
        if (list != null)
        {
            for (ModSettingsProvider<?> option : list)
            {
                option.UpdateProvider();
            }
        }

        button.update();
        if (this.button.hb.clicked || InputHelper.pressedEscape) {
            InputHelper.pressedEscape = false;
            this.button.hb.clicked = false;
            this.button.hide();
            AbstractDungeon.CurrentScreen prevScreen = AbstractDungeon.previousScreen;
            AbstractDungeon.closeCurrentScreen();
            if (prevScreen == AbstractDungeon.CurrentScreen.SETTINGS)
            {
                AbstractDungeon.settingsScreen.open();
            }
        }
    }

    public void Render(SpriteBatch sb)
    {
        super.Render(sb);

        background.TryRender(sb);
        modButtons.Render(sb);

        ArrayList<ModSettingsProvider<?>> list = configSubscribers.get(activeMod);
        if (list != null)
        {
            for (ModSettingsProvider<?> option : list)
            {
                option.RenderProvider(sb);
            }
        }

        button.render(sb);
    }

    protected void MakeModButton(ModInfo info) {
        modButtons.AddButton(button -> SetActiveMod(info), info.Name)
                .SetColor(Color.GRAY);
    }
}
