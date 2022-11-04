package extendedui.ui.settings;

import basemod.IUIElement;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.STSConfigItem;
import extendedui.ui.AbstractScreen;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIButtonList;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.AdvancedHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ModSettingsScreen extends AbstractScreen
{
    protected static final float OPTION_SIZE = Scale(40);
    protected static final float OFFSET_SIZE = Scale(640);
    protected static final float COLOR_BUTTON_SIZE = Scale(51);
    protected static final TextureCache modPanel = new TextureCache("img/ModPanelBg.png");
    protected static final HashMap<Category, ArrayList<IUIElement>> modListCategories = new HashMap<>();
    protected static final HashMap<Category, ArrayList<IUIElement>> configCategories = new HashMap<>();
    protected static final HashMap<Category, Float> offsets = new HashMap<>();
    protected static final AdvancedHitbox hb = new AdvancedHitbox(ScreenW(0.5f) - Scale(700), Settings.OPTION_Y - Scale(400), Scale(1400), Scale(800));
    protected final EUIButtonList buttons = new EUIButtonList(7, ScreenW(0.077f), hb.y + Scale(800), Scale(205), Scale(42)).SetFontScale(0.6f);
    public final MenuCancelButton button;
    private final EUIImage background;
    private Category activeMod;

    public static void AddModList(Category cat, ModPanel panel)
    {
        modListCategories.putIfAbsent(cat, panel.getUIElements());
    }

    public static void AddCategory(Category cat)
    {
        configCategories.putIfAbsent(cat, new ArrayList<>());
        offsets.putIfAbsent(cat, OFFSET_SIZE);
    }

    public static ModSettingsToggle AddBoolean(Category cat, STSConfigItem<Boolean> option, String label)
    {
        ArrayList<IUIElement> list = configCategories.get(cat);
        float offY = offsets.getOrDefault(cat, OFFSET_SIZE);
        if (list != null)
        {
            ModSettingsToggle toggle = new ModSettingsToggle(new RelativeHitbox(hb, OPTION_SIZE * 2, OPTION_SIZE, OPTION_SIZE * 3.3f, offY, false), option, label);
            list.add(toggle);
            offsets.put(cat, offY -= toggle.hb.height * 1.1f);
            return toggle;
        }
        return null;
    }

    public static ModSettingsPathSelector AddPathSelection(Category cat, STSConfigItem<String> option, String label, String... extensions)
    {
        ArrayList<IUIElement> list = configCategories.get(cat);
        float offY = offsets.getOrDefault(cat, OFFSET_SIZE);
        if (list != null)
        {
            ModSettingsPathSelector selector = new ModSettingsPathSelector(new RelativeHitbox(hb, OPTION_SIZE * 8, OPTION_SIZE, OPTION_SIZE * 7f, offY, false), option, label);
            if (extensions.length > 0)
            {
                selector.SetFileFilters(extensions);
            }
            list.add(selector);
            offsets.put(cat, offY -= selector.hb.height * 1.2f);
            return selector;
        }
        return null;
    }

    public static EUILabel AddLabel(Category cat, String text, BitmapFont font)
    {
        ArrayList<IUIElement> list = configCategories.get(cat);
        float offY = offsets.getOrDefault(cat, OFFSET_SIZE);
        if (list != null)
        {
            EUILabel label = new EUILabel(font, new RelativeHitbox(hb, OPTION_SIZE * 16, OPTION_SIZE, OPTION_SIZE * 6f, offY, false)).SetText(text);
            list.add(label);
            offsets.put(cat, offY -= label.hb.height * 1.2f);
            return label;
        }
        return null;
    }


    public static void AddIUI(Category cat, IUIElement option, String label)
    {
        ArrayList<IUIElement> list = configCategories.get(cat);
        if (list != null)
        {
            list.add(option);
        }
    }

    public ModSettingsScreen()
    {
        super();
        background = new EUIImage(modPanel.Texture(), hb);
        button = new MenuCancelButton();
    }

    public void Open() {
        super.Open(false, false);
        SingleCardViewPopup.isViewingUpgrade = false;
        this.button.show(MasterDeckViewScreen.TEXT[1]);

        buttons.Clear();
        ArrayList<Category> infos = new ArrayList<>(GetCategories().keySet());
        infos.sort((a, b) -> StringUtils.compare(a.name, b.name));
        for (Category info : infos)
        {
            MakeButton(info);
        }

        if (infos.size() > 0)
        {
            SetActiveItem(infos.get(0));
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

    public void SetActiveItem(Category info)
    {
        if (GetCategories().containsKey(info))
        {
            activeMod = info;
        }
    }

    @Override
    public void Update()
    {
        background.TryUpdate();
        buttons.Update();

        ArrayList<IUIElement> list = GetCategories().get(activeMod);
        if (list != null)
        {
            for (IUIElement option : list)
            {
                option.update();
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
        buttons.Render(sb);

        ArrayList<IUIElement> list = GetCategories().get(activeMod);
        if (list != null)
        {
            for (IUIElement option : list)
            {
                option.render(sb);
            }
        }

        button.render(sb);
    }

    protected void MakeButton(Category info) {
        buttons.AddButton(button -> SetActiveItem(info), info.name)
                .SetColor(Color.GRAY);
    }

    protected HashMap<Category, ArrayList<IUIElement>> GetCategories()
    {
        return EUIConfiguration.ShowModSettings.Get() ? modListCategories : configCategories;
    }

    public static class Category
    {
        public String name;

        public Category(String name)
        {
            this.name = name;
        }
    }
}
