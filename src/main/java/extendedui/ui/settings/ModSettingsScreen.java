package extendedui.ui.settings;

import basemod.IUIElement;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.EUIGameUtils;
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
import java.util.Objects;

public class ModSettingsScreen extends AbstractScreen
{
    protected static final float OPTION_SIZE = scale(40);
    protected static final float OFFSET_SIZE = scale(640);
    protected static final float COLOR_BUTTON_SIZE = scale(51);
    protected static final TextureCache modPanel = new TextureCache("img/ModPanelBg.png");
    protected static final HashMap<Category, ArrayList<IUIElement>> modListCategories = new HashMap<>();
    protected static final HashMap<Category, ArrayList<IUIElement>> configCategories = new HashMap<>();
    protected static final HashMap<Category, Float> offsets = new HashMap<>();
    protected static final AdvancedHitbox hb = new AdvancedHitbox(screenW(0.5f) - scale(700), Settings.OPTION_Y - scale(400), scale(1400), scale(800));
    protected final EUIButtonList buttons = new EUIButtonList(7, screenW(0.077f), hb.y + scale(800), scale(205), scale(42)).setFontScale(0.6f);
    public final MenuCancelButton button;
    private final EUIImage background;
    private Category activeMod;

    public static void addModList(Category cat, ModPanel panel)
    {
        if (panel != null)
        {
            modListCategories.putIfAbsent(cat, panel.getUIElements());
        }
    }

    public static void addCategory(Category cat)
    {
        configCategories.putIfAbsent(cat, new ArrayList<>());
        offsets.putIfAbsent(cat, OFFSET_SIZE);
    }

    public static Category registerByInfo(ModInfo info)
    {
        Category c = new Category(info.Name);
        addCategory(c);
        return c;
    }

    public static Category registerByClass(Class<?> classType)
    {
        return registerByInfo(Objects.requireNonNull(EUIGameUtils.getModInfo(classType)));
    }

    public static ModSettingsToggle addBoolean(Category cat, STSConfigItem<Boolean> option, String label)
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

    public static ModSettingsPathSelector addPathSelection(Category cat, STSConfigItem<String> option, String label, String... extensions)
    {
        ArrayList<IUIElement> list = configCategories.get(cat);
        float offY = offsets.getOrDefault(cat, OFFSET_SIZE);
        if (list != null)
        {
            ModSettingsPathSelector selector = new ModSettingsPathSelector(new RelativeHitbox(hb, OPTION_SIZE * 8, OPTION_SIZE, OPTION_SIZE * 7f, offY, false), option, label);
            if (extensions.length > 0)
            {
                selector.setFileFilters(extensions);
            }
            list.add(selector);
            offsets.put(cat, offY -= selector.hb.height * 1.2f);
            return selector;
        }
        return null;
    }

    public static EUILabel addLabel(Category cat, String text, BitmapFont font)
    {
        ArrayList<IUIElement> list = configCategories.get(cat);
        float offY = offsets.getOrDefault(cat, OFFSET_SIZE);
        if (list != null)
        {
            EUILabel label = new EUILabel(font, new RelativeHitbox(hb, OPTION_SIZE * 16, OPTION_SIZE, OPTION_SIZE * 6f, offY, false)).setLabel(text);
            list.add(label);
            offsets.put(cat, offY -= label.hb.height * 1.2f);
            return label;
        }
        return null;
    }


    public static void addIUI(Category cat, IUIElement option, String label)
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
        background = new EUIImage(modPanel.texture(), hb);
        button = new MenuCancelButton();
    }

    public void open() {
        super.open(false, false);
        SingleCardViewPopup.isViewingUpgrade = false;
        this.button.show(MasterDeckViewScreen.TEXT[1]);

        buttons.clear();
        ArrayList<Category> infos = new ArrayList<>(getCategories().keySet());
        infos.sort((a, b) -> StringUtils.compare(a.name, b.name));
        for (Category info : infos)
        {
            makeButton(info);
        }

        if (infos.size() > 0)
        {
            setActiveItem(infos.get(0));
        }
    }

    @Override
    protected void updateDungeonScreen()
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
    public void reopen()
    {
        this.button.show(MasterDeckViewScreen.TEXT[1]);
    }

    public void setActiveItem(Category info)
    {
        if (getCategories().containsKey(info))
        {
            activeMod = info;
        }
    }

    @Override
    public void updateImpl()
    {
        background.tryUpdate();
        buttons.updateImpl();

        ArrayList<IUIElement> list = getCategories().get(activeMod);
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

    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);

        background.tryRender(sb);
        buttons.renderImpl(sb);

        ArrayList<IUIElement> list = getCategories().get(activeMod);
        if (list != null)
        {
            for (IUIElement option : list)
            {
                option.render(sb);
            }
        }

        button.render(sb);
    }

    protected void makeButton(Category info) {
        buttons.addButton(button -> setActiveItem(info), info.name)
                .setColor(Color.GRAY);
    }

    protected HashMap<Category, ArrayList<IUIElement>> getCategories()
    {
        return EUIConfiguration.showModSettings.get() ? modListCategories : configCategories;
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
