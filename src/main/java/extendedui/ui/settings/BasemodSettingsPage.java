package extendedui.ui.settings;

import basemod.IUIElement;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIGameUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.hitboxes.EUIHitbox;

import java.util.ArrayList;

public class BasemodSettingsPage implements IUIElement {
    public static final float ICON_SIZE = EUIGameUtils.scale(40);
    public ArrayList<ArrayList<IUIElement>> pages = new ArrayList<>();
    public int current = 0;
    protected ArrayList<IUIElement> currentItems;
    protected EUIButton leftButton;
    protected EUIButton rightButton;

    public BasemodSettingsPage() {
        pages.add(new ArrayList<>());
        leftButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new EUIHitbox(EUIConfiguration.BASE_OPTION_OFFSET_X * Settings.scale, 800 * Settings.scale, ICON_SIZE, ICON_SIZE))
                .setOnClick(__ -> setPage(current - 1));
        rightButton = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new EUIHitbox(leftButton.hb.cX + ICON_SIZE, leftButton.getY(), ICON_SIZE, ICON_SIZE))
                .setOnClick(__ -> setPage(current + 1));
        setPage(current);
    }

    public void setPage(int page) {
        current = MathUtils.clamp(page, 0, pages.size() - 1);
        ArrayList<IUIElement> selected = current < pages.size() ? pages.get(current) : null;
        if (selected != null) {
            currentItems = selected;
        }
        leftButton.setInteractable(current > 0);
        rightButton.setInteractable(current < pages.size());
    }

    public void addUIElement(int page, IUIElement element) {
        while (pages.size() <= page) {
            pages.add(new ArrayList<>());
        }
        pages.get(page).add(element);
    }

    @Override
    public void render(SpriteBatch sb) {
        leftButton.render(sb);
        rightButton.render(sb);
        for (IUIElement element : currentItems) {
            element.render(sb);
        }
    }

    @Override
    public void update() {
        leftButton.update();
        rightButton.update();
        for (IUIElement element : currentItems) {
            element.update();
        }
    }

    @Override
    public int renderLayer() {
        return 0;
    }

    @Override
    public int updateOrder() {
        return 0;
    }
}
