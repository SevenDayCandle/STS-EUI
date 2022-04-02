package extendedui.utilities.abstracts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import extendedui.ui.hitboxes.FakeHitbox;
import extendedui.utilities.EUIFontHelper;

public class FakeLibraryCard extends TooltipCard
{
    @Override
    public void upgrade()
    {

    }

    public FakeLibraryCard()
    {
        super("","", "", "", 0, EUIRM.Strings.UI_NoMatch, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
        this.hb = new FakeHitbox(hb);
    }

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster)
    {

    }

    @Override
    public void renderUpgradePreview(SpriteBatch sb)
    {

    }

    @Override
    public void initializeDescription()
    {

    }

    @Override
    public void calculateCardDamage(AbstractMonster mo)
    {

    }

    @Override
    public AbstractCard makeCopy() {
        return new FakeLibraryCard();
    }

    @Override
    public void update()
    {
        super.update();
        this.hovered = false;
        this.renderTip = false;
    }

    @Override
    public void render(SpriteBatch sb, boolean library)
    {
        FontHelper.renderFontLeft(sb, EUIFontHelper.CardTitleFont_Normal, EUIRM.Strings.UI_NoMatch, hb.cX, hb.cY + hb.height / 4, Color.WHITE);
    }
}
