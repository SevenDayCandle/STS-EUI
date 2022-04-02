package stseffekseer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import stseffekseer.ui.TextureCache;

import java.util.HashMap;

public class EUIRM
{
    public static final String ID = "stseffekseer";
    public static Images Images = new Images();
    public static Strings Strings;
    protected static final HashMap<String, Texture> internalTextures = new HashMap<>();
    private static final HashMap<String, Texture> localTextures = new HashMap<>();

    public static void Initialize() {
        Strings = new Strings();
    }

    public static Texture GetTexture(String path) {
        return GetTexture(path, false);
    }

    public static Texture GetTexture(String path, boolean useMipMap) {
        return GetTexture(path, true, false);
    }

    public static Texture GetTexture(String path, boolean useMipMap, boolean refresh) {
        Texture texture = (Texture)internalTextures.get(path);
        if (texture == null || refresh) {
            texture = LoadTextureImpl(Gdx.files.internal(path), useMipMap);
            internalTextures.put(path, texture);
        }

        return texture;
    }

    public static Texture GetLocalTexture(String path) {
        return GetLocalTexture(path, false);
    }

    public static Texture GetLocalTexture(String path, boolean useMipMap) {
        return GetLocalTexture(path, true, false);
    }

    public static Texture GetLocalTexture(String path, boolean useMipMap, boolean refresh) {
        Texture texture = (Texture)localTextures.get(path);
        if (texture == null || refresh) {
            texture = LoadTextureImpl(Gdx.files.local(path), useMipMap);
            localTextures.put(path, texture);
        }

        return texture;
    }

    private static Texture LoadTextureImpl(FileHandle file, boolean useMipMap) {
        if (file.exists()) {
            Texture texture = new Texture(file, useMipMap);
            if (useMipMap) {
                texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
            } else {
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            }
            return texture;
        } else {
            JavaUtils.GetLogger(EUIRM.class).error("Texture does not exist: " + file.path());
        }
        return null;
    }

    public static String GetID(String suffix)
    {
        return ID + ":" + suffix;
    }

    private static UIStrings GetUIStrings(String suffix)
    {
        return CardCrawlGame.languagePack.getUIString(GetID(suffix));
    }

    public static class Images {
        public final TextureCache Base_Badge                  = new TextureCache("images/ui/Base_Badge.png");
        public final TextureCache Base_Border                 = new TextureCache("images/ui/Base_Border.png");
        public final TextureCache Border                      = new TextureCache("images/ui/Border.png");
        public final TextureCache CardPool                    = new TextureCache("images/ui/CardPool.png");
        public final TextureCache Divider                     = new TextureCache("images/ui/Divider.png");
        public final TextureCache Draggable                   = new TextureCache("images/ui/Draggable.png");
        public final TextureCache FullSquare                  = new TextureCache("images/ui/FullSquare.png");
        public final TextureCache HexagonalButton             = new TextureCache("images/ui/HexagonalButton.png");
        public final TextureCache HexagonalButtonBorder       = new TextureCache("images/ui/HexagonalButtonBorder.png");
        public final TextureCache HexagonalButtonHover        = new TextureCache("images/ui/HexagonalButtonHover.png");
        public final TextureCache LongButton                  = new TextureCache("images/ui/LongButton.png");
        public final TextureCache LongButtonBorder            = new TextureCache("images/ui/LongButtonBorder.png");
        public final TextureCache Minus                       = new TextureCache("images/ui/Minus.png");
        public final TextureCache Panel                       = new TextureCache("images/ui/Panel.png");
        public final TextureCache Panel_Elliptical            = new TextureCache("images/ui/Panel_Elliptical.png");
        public final TextureCache Panel_Elliptical_Half_H     = new TextureCache("images/ui/Panel_Elliptical_Half_H.png");
        public final TextureCache Panel_Rounded               = new TextureCache("images/ui/Panel_Rounded.png");
        public final TextureCache Panel_Rounded_Half_H        = new TextureCache("images/ui/Panel_Rounded_Half_H.png");
        public final TextureCache Plus                        = new TextureCache("images/ui/Plus.png");
        public final TextureCache RectangularButton           = new TextureCache("images/ui/RectangularButton.png");
        public final TextureCache SquaredButton               = new TextureCache("images/ui/SquaredButton.png");
        public final TextureCache Tag                         = new TextureCache("images/ui/Tag.png");
        public final TextureCache X                           = new TextureCache("images/ui/X.png");
    }

    public static class Strings {
        private final UIStrings StringsConfig = GetUIStrings("config");
        private final UIStrings StringsHotkeys = GetUIStrings("hotkeys");
        private final UIStrings StringsUI = GetUIStrings("ui");

        public final String Config_FlushOnGameStart = StringsConfig.TEXT[0];
        public final String Config_FlushOnRoomStart = StringsConfig.TEXT[1];

        public final String Hotkey_Cycle = StringsHotkeys.TEXT[0];
        public final String Hotkey_Toggle = StringsHotkeys.TEXT[1];

        public final String UI_ItemsSelected = StringsUI.TEXT[0];
        public final String UI_Keywords = StringsUI.TEXT[1];
        public final String UI_Amount = StringsUI.TEXT[2];
        public final String UI_Origins = StringsUI.TEXT[3];
        public final String UI_Colors = StringsUI.TEXT[4];
        public final String UI_Total = StringsUI.TEXT[5];
        public final String UI_ShowColorless = StringsUI.TEXT[6];
        public final String UI_TypeToSearch = StringsUI.TEXT[7];
        public final String UI_ViewCardPool = StringsUI.TEXT[8];
        public final String UI_ViewCardPoolDescription = StringsUI.TEXT[9];
        public final String UI_Filters = StringsUI.TEXT[10];
        public final String UI_Any = StringsUI.TEXT[11];
        public final String UI_NoMatch = StringsUI.TEXT[12];
        public final String UI_KeyToCycle = StringsUI.TEXT[13];

        public final String KeyToCycle(String keyName) {
            return JavaUtils.Format(UI_KeyToCycle, keyName);
        }
    }
}
