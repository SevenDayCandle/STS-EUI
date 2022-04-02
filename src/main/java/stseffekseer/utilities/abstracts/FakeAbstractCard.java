package stseffekseer.utilities.abstracts;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import stseffekseer.EUIGameUtils;

public class FakeAbstractCard extends TooltipCard
{
    private final AbstractCard source;

    @Override
    public void upgrade()
    {

    }

    public FakeAbstractCard(AbstractCard card)
    {
        super(card.cardID, card.name, "status/beta", "status/beta", card.cost, card.rawDescription, card.type, card.color, card.rarity, card.target);
        this.source = card;
    }

    public FakeAbstractCard SetID(String cardID)
    {
        this.cardID = cardID;

        return this;
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
    public AbstractCard makeCopy()
    {
        return new FakeAbstractCard(source).SetID(cardID);
    }


    @Override
    public void render(SpriteBatch sb, boolean selected)
    {
        EUIGameUtils.CopyVisualProperties(source, this);
        source.render(sb, selected);
    }
}
