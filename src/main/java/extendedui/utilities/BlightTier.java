package extendedui.utilities;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import extendedui.EUIRM;

public enum BlightTier {
    BASIC,
    BOSS,
    SPECIAL;

    public static BlightTier getTier(AbstractBlight blight) {
        return getTier(blight.blightID);
    }

    public static BlightTier getTier(String id) {
        if (BlightHelper.chestBlights.contains(id)) {
            return BOSS;
        }
        else if (BlightHelper.blights.contains(id)) {
            return BASIC;
        }
        return SPECIAL;
    }

    public String getName() {
        switch (this) {
            case BASIC:
                return EUIRM.strings.ui_basic;
            case BOSS:
                return SingleRelicViewPopup.TEXT[0];
            case SPECIAL:
                return SingleRelicViewPopup.TEXT[5];
        }
        return SingleRelicViewPopup.TEXT[9];
    }

    public boolean isRarity(AbstractBlight blight) {
        return getTier(blight) == this;
    }

    public boolean isRarity(String id) {
        return getTier(id) == this;
    }
}
