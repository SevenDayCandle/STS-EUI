package extendedui.utilities.abstracts;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import extendedui.EUIGameUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public abstract class TooltipBlight extends AbstractBlight implements TooltipProvider {
    public ArrayList<EUITooltip> tips;
    public EUITooltip mainTooltip;

    public TooltipBlight(String setId, String name, String description, String imgName, boolean unique) {
        super(setId, name, description, imgName, unique);
        if (tips == null) {
            initializeTips();
        }
    }

    @Override
    protected void initializeTips()
    {
        if (tips == null)
        {
            tips = new ArrayList<>();
        }
        else
        {
            tips.clear();
        }

        mainTooltip = new EUITooltip(name, description);
        tips.add(mainTooltip);
        EUIGameUtils.ScanForTips(description, tips);
    }

    @Override
    public void renderTip(SpriteBatch sb)
    {
        EUITooltip.QueueTooltips(this);
    }

    @Override
    public void updateDescription() {
        super.updateDescription();
        if (tips == null) {
            initializeTips();
        }
        if (tips.size() > 0) {
            tips.get(0).description = description;
        }
    }


    @Override
    public List<EUITooltip> GetTips() {
        return tips;
    }
}
