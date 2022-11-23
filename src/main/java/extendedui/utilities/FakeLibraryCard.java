package extendedui.utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import extendedui.ui.hitboxes.FakeHitbox;

public class FakeLibraryCard extends AbstractCard
{
    @Override
    public void upgrade()
    {

    }

    public FakeLibraryCard()
    {
        super("","", "", "", 0, EUIRM.strings.uiNomatch, CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
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
    public void renderCardPreview(SpriteBatch sb) {
    }

    @Override
    public void renderCardPreviewInSingleView(SpriteBatch sb) {
    }

    @Override
    public void renderWithSelections(SpriteBatch sb) {
        renderImpl(sb);
    }

    @Override
    public void renderInLibrary(SpriteBatch sb) {
        renderImpl(sb);
    }

    @Override
    public void render(SpriteBatch sb, boolean selected)
    {
        renderImpl(sb);
    }

    private void renderImpl(SpriteBatch sb) {
        FontHelper.renderFontLeft(sb, EUIFontHelper.cardtitlefontNormal, EUIRM.strings.uiNomatch, hb.cX, hb.cY + hb.height / 4, Color.WHITE);
    }
}
