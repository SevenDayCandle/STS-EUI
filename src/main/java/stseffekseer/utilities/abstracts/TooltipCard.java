package stseffekseer.utilities.abstracts;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import stseffekseer.EUI;
import stseffekseer.EUIGameUtils;
import stseffekseer.interfaces.markers.TooltipProvider;
import stseffekseer.ui.tooltips.EUITooltip;

import java.util.ArrayList;
import java.util.List;

public abstract class TooltipCard extends AbstractCard implements TooltipProvider {

    public float hoverDuration;
    public boolean hovered;
    public boolean isPopup = false;
    public boolean isPreview = false;
    public boolean renderTip;
    public final ArrayList<EUITooltip> tooltips = new ArrayList<>();


    public TooltipCard(String id, String name, String deprecatedJokeUrl, String imgUrl, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target) {
        super(id, name, imgUrl, cost, rawDescription, type, color, rarity, target);
    }

    public TooltipCard(String id, String name, String imgUrl, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target, DamageInfo.DamageType dType) {
        super(id, name, imgUrl, cost, rawDescription, type, color, rarity, target, dType);
    }

    @Override
    public void renderCardTip(SpriteBatch sb)
    {
        if (!Settings.hideCards && !isFlipped && !isLocked && isSeen && (isPopup || renderTip) && EUITooltip.CanRenderTooltips() && (AbstractDungeon.player == null || !AbstractDungeon.player.isDraggingCard || Settings.isTouchScreen))
        {
            EUITooltip.QueueTooltips(this);
        }
    }

    @Override
    public List<EUITooltip> GetTips() {
        return tooltips;
    }

    @Override
    public void update()
    {
        super.update();

        if (EUIGameUtils.InGame() && AbstractDungeon.player != null && AbstractDungeon.player.hoveredCard != this && !AbstractDungeon.isScreenUp)
        {
            this.hovered = false;
            this.renderTip = false;
        }
    }

    @Override
    public void updateHoverLogic() {
        this.hb.update();

        if (this.hb.hovered) {
            this.hover();
            this.hoverDuration += EUI.Delta();
            this.renderTip = this.hoverDuration > 0.2F && !Settings.hideCards;
        } else {
            this.unhover();
        }
    }

    @Override
    public void unhover() {
        if (this.hovered) {
            this.hoverDuration = 0.0F;
            this.targetDrawScale = 0.75F;
        }

        this.hovered = false;
        this.renderTip = false;
    }

    @Override
    public void hover() {
        if (!this.hovered) {
            this.drawScale = 1.0F;
            this.targetDrawScale = 1.0F;
        }

        this.hovered = true;
    }

    @Override
    public void untip()
    {
        this.hoverDuration = 0f;
        this.renderTip = false;
    }
}
