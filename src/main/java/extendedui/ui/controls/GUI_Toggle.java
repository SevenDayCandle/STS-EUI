package extendedui.ui.controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputAction;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import eatyourbeets.interfaces.delegates.ActionT1;
import extendedui.EUI;
import extendedui.EUIRenderHelpers;
import extendedui.ui.GUI_Hoverable;
import extendedui.ui.hitboxes.AdvancedHitbox;

public class GUI_Toggle extends GUI_Hoverable
{
    public String text = "";
    public boolean toggled = false;
    public boolean interactable = true;

    public CInputAction controllerAction = CInputActionSet.select;
    public GUI_Image untickedImage = new GUI_Image(ImageMaster.COLOR_TAB_BOX_UNTICKED);
    public GUI_Image tickedImage = new GUI_Image(ImageMaster.COLOR_TAB_BOX_TICKED);
    public GUI_Image backgroundImage = null;
    public Color defaultColor = Settings.CREAM_COLOR.cpy();
    public Color hoveredColor = Settings.GOLD_COLOR.cpy();
    public BitmapFont font = FontHelper.topPanelInfoFont;
    public float fontSize = 1;
    public float tickSize = 48;
    public ActionT1<Boolean> onToggle = null;

    public GUI_Toggle(AdvancedHitbox hb)
    {
        super(hb);
    }

    public GUI_Toggle SetFontColors(Color defaultColor, Color hoveredColor)
    {
        this.defaultColor = defaultColor.cpy();
        this.hoveredColor = hoveredColor.cpy();

        return this;
    }

    public GUI_Toggle SetControllerAction(CInputAction action)
    {
        this.controllerAction = action;

        return this;
    }

    public GUI_Toggle SetTickImage(GUI_Image unticked, GUI_Image ticked, float size)
    {
        this.untickedImage = unticked;
        this.tickedImage = ticked;
        this.tickSize = size;

        return this;
    }

    public GUI_Toggle SetInteractable(boolean interactable)
    {
        this.interactable = interactable;

        return this;
    }

    public GUI_Toggle SetFontSize(float fontSize)
    {
        this.fontSize = fontSize;

        return this;
    }

    public GUI_Toggle SetFont(BitmapFont font, float fontSize)
    {
        this.font = font;
        this.fontSize = fontSize;

        return this;
    }

    public GUI_Toggle SetText(String text)
    {
        this.text = text;

        return this;
    }

    public GUI_Toggle SetPosition(float cX, float cY)
    {
        this.hb.move(cX, cY);

        return this;
    }

    public GUI_Toggle SetBackground(GUI_Image image)
    {
        this.backgroundImage = image;

        return this;
    }

    public GUI_Toggle SetBackground(Texture texture, Color color)
    {
        this.backgroundImage = EUIRenderHelpers.ForTexture(texture).SetHitbox(hb).SetColor(color);

        return this;
    }

    public GUI_Toggle SetOnToggle(ActionT1<Boolean> onToggle)
    {
        this.onToggle = onToggle;

        return this;
    }

    public GUI_Toggle SetToggle(boolean value)
    {
        this.toggled = value;

        return this;
    }

    public void Toggle()
    {
        Toggle(!toggled);
    }

    public void Toggle(boolean value)
    {
        if (toggled != value)
        {
            toggled = value;

            if (onToggle != null)
            {
                onToggle.Invoke(value);
            }
        }
    }

    public boolean IsToggled()
    {
        return toggled;
    }

    @Override
    public void Update()
    {
        super.Update();

        if (!interactable)
        {
            return;
        }

        if (EUI.TryHover(hb))
        {
            if (hb.justHovered)
            {
                CardCrawlGame.sound.playA("UI_HOVER", -0.3f);
            }

            if (hb.hovered && InputHelper.justClickedLeft)
            {
                hb.clickStarted = true;
            }
        }

        if (hb.clicked)
        {
            hb.clicked = false;
            CardCrawlGame.sound.playA("UI_CLICK_1", -0.2f);

            Toggle();
        }
    }

    @Override
    public void Render(SpriteBatch sb)
    {
        if (backgroundImage != null)
        {
            if (backgroundImage.hb != null)
            {
                backgroundImage.Render(sb);
            }
            else
            {
                backgroundImage.RenderCentered(sb, hb.x + (tickSize / 6f), hb.cY - (tickSize / 2f), tickSize, tickSize);
            }
        }

        Color fontColor;
        if (!interactable)
        {
            fontColor = TEXT_DISABLED_COLOR;
        }
        else if (hb.hovered)
        {
            fontColor = hoveredColor;
        }
        else
        {
            fontColor = defaultColor;
        }

        if (fontSize != 1)
        {
            font.getData().setScale(fontSize);
            FontHelper.renderFontLeft(sb, font, text, hb.x + (tickSize * 1.3f * Settings.scale), hb.cY, fontColor);
            EUIRenderHelpers.ResetFont(font);
        }
        else
        {
            FontHelper.renderFontLeft(sb, font, text, hb.x + (tickSize * 1.3f * Settings.scale), hb.cY, fontColor);
        }

        GUI_Image image = toggled ? tickedImage : untickedImage;
        if (image != null)
        {
            image.RenderCentered(sb, hb.x + (tickSize / 6f), hb.cY - (tickSize / 2f), tickSize, tickSize);

//            sb.setColor(fontColor);
//            sb.draw(image, hb.x + (tickSize / 6f) * Settings.scale, hb.cY - tickSize / 2f, tickSize / 2f, tickSize / 2f, tickSize, tickSize,
//                    Settings.scale, Settings.scale, 0f, 0, 0, 48, 48, false, false);
        }

        hb.render(sb);
    }
}
