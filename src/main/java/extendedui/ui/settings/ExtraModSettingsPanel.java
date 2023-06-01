package extendedui.ui.settings;

import basemod.IUIElement;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import extendedui.EUIGameUtils;
import extendedui.EUIInputManager;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.STSConfigItem;
import extendedui.text.EUISmartText;
import extendedui.ui.EUIBase;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIButtonList;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ExtraModSettingsPanel extends EUIBase {
    protected static final float OPTION_SIZE = scale(40);
    protected static final float OFFSET_SIZE = scale(640);
    protected static final float COLOR_BUTTON_SIZE = scale(51);
    protected static final TextureCache modPanel = new TextureCache("img/ModPanelBg.png");
    protected static final HashMap<Category, ArrayList<IUIElement>> modListCategories = new HashMap<>();
    protected static final HashMap<Category, ArrayList<IUIElement>> configCategories = new HashMap<>();
    protected static final HashMap<Category, Float> offsets = new HashMap<>();
    protected static final EUIHitbox hb = new EUIHitbox(screenW(0.5f) - scale(700), Settings.OPTION_Y - scale(400), scale(1400), scale(800));
    protected final MenuCancelButton button;
    protected final EUIButtonList buttons = new EUIButtonList(7, screenW(0.077f), hb.y + scale(800), scale(205), scale(42)).setFontScale(0.6f);
    protected final EUIImage background;
    private Category activeMod;
    private String prevOpenString;

    public ExtraModSettingsPanel() {
        super();
        background = new EUIImage(modPanel.texture(), hb);
        button = new MenuCancelButton();
    }

    public static ModSettingsToggle addBoolean(Category cat, STSConfigItem<Boolean> option, String label) {
        ArrayList<IUIElement> list = configCategories.get(cat);
        float offY = offsets.getOrDefault(cat, OFFSET_SIZE);
        if (list != null) {
            float baseWidth = EUISmartText.getSmartWidth(EUIFontHelper.cardDescriptionFontNormal, label);
            ModSettingsToggle toggle = new ModSettingsToggle(new RelativeHitbox(hb, OPTION_SIZE * 2 + baseWidth, OPTION_SIZE, OPTION_SIZE * 3.3f + baseWidth / 2f, offY), option, label);
            list.add(toggle);
            offsets.put(cat, offY -= toggle.hb.height * 1.1f);
            return toggle;
        }
        return null;
    }

    public static void addIUI(Category cat, IUIElement option, String label) {
        ArrayList<IUIElement> list = configCategories.get(cat);
        if (list != null) {
            list.add(option);
        }
    }

    public static EUILabel addLabel(Category cat, String text, BitmapFont font) {
        ArrayList<IUIElement> list = configCategories.get(cat);
        float offY = offsets.getOrDefault(cat, OFFSET_SIZE);
        if (list != null) {
            EUILabel label = new EUILabel(font, new RelativeHitbox(hb, OPTION_SIZE * 16, OPTION_SIZE, OPTION_SIZE * 6f, offY)).setLabel(text);
            list.add(label);
            offsets.put(cat, offY -= label.hb.height * 1.2f);
            return label;
        }
        return null;
    }

    public static void addModList(Category cat, ModPanel panel) {
        if (panel != null) {
            modListCategories.putIfAbsent(cat, panel.getUIElements());
        }
    }

    public static ModSettingsPathSelector addPathSelection(Category cat, STSConfigItem<String> option, String label, String... extensions) {
        ArrayList<IUIElement> list = configCategories.get(cat);
        float offY = offsets.getOrDefault(cat, OFFSET_SIZE);
        if (list != null) {
            ModSettingsPathSelector selector = new ModSettingsPathSelector(new RelativeHitbox(hb, OPTION_SIZE * 8, OPTION_SIZE, OPTION_SIZE * 7f, offY), option, label);
            if (extensions.length > 0) {
                selector.setFileFilters(extensions);
            }
            list.add(selector);
            offsets.put(cat, offY -= selector.hb.height * 1.2f);
            return selector;
        }
        return null;
    }

    public static Category registerByClass(Class<?> classType) {
        return registerByInfo(Objects.requireNonNull(EUIGameUtils.getModInfo(classType)));
    }

    public static Category registerByInfo(ModInfo info) {
        Category c = new Category(info.Name);
        addCategory(c);
        return c;
    }

    public static void addCategory(Category cat) {
        configCategories.putIfAbsent(cat, new ArrayList<>());
        offsets.putIfAbsent(cat, OFFSET_SIZE);
    }

    protected void makeButton(Category info) {
        buttons.addButton(button -> setActiveItem(info), info.name)
                .setColor(Color.GRAY);
    }

    public void open() {
        isActive = true;
        if (EUIGameUtils.inGame() && !AbstractDungeon.overlayMenu.cancelButton.isHidden) {
            AbstractDungeon.overlayMenu.cancelButton.hide();
            prevOpenString = AbstractDungeon.overlayMenu.cancelButton.buttonText;
        }
        else if (!EUIGameUtils.inGame() && !CardCrawlGame.cancelButton.isHidden) {
            CardCrawlGame.cancelButton.hide();
            prevOpenString = CardCrawlGame.cancelButton.buttonText;
        }
        else {
            prevOpenString = null;
        }

        this.button.show(MasterDeckViewScreen.TEXT[1]);

        buttons.clear();
        ArrayList<Category> infos = new ArrayList<>(getCategories().keySet());
        infos.sort((a, b) -> StringUtils.compare(a.name, b.name));
        for (Category info : infos) {
            makeButton(info);
        }

        if (infos.size() > 0) {
            setActiveItem(infos.get(0));
        }
    }

    public void close() {
        if (prevOpenString != null) {
            if (EUIGameUtils.inGame()) {
                AbstractDungeon.overlayMenu.cancelButton.show(prevOpenString);
            }
            else {
                CardCrawlGame.cancelButton.show(prevOpenString);
            }
        }
        isActive = false;
        prevOpenString = null;
    }

    @Override
    public void updateImpl() {
        background.tryUpdate();
        buttons.updateImpl();

        ArrayList<IUIElement> list = getCategories().get(activeMod);
        if (list != null) {
            for (IUIElement option : list) {
                option.update();
            }
        }

        button.update();
        if (this.button.hb.clicked || (EUIInputManager.tryEscape())) {
            this.button.hb.clicked = false;
            this.button.hide();
            close();
        }
    }

    public void renderImpl(SpriteBatch sb) {
        background.tryRender(sb);
        buttons.renderImpl(sb);

        ArrayList<IUIElement> list = getCategories().get(activeMod);
        if (list != null) {
            for (IUIElement option : list) {
                option.render(sb);
            }
        }

        button.render(sb);
    }

    protected HashMap<Category, ArrayList<IUIElement>> getCategories() {
        return EUIConfiguration.showModSettings.get() ? modListCategories : configCategories;
    }

    public void setActiveItem(Category info) {
        if (getCategories().containsKey(info)) {
            activeMod = info;
        }
    }

    public static class Category {
        public String name;

        public Category(String name) {
            this.name = name;
        }
    }
}
