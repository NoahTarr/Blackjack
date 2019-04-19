package tarr.noah.Final.blackjack.deck;

import javafx.scene.image.Image;

/**
 * Created by noaht on 4/29/2017.
 */
public class Card
{
    public enum Ranks
    {
        Ace,
        Two,
        Three,
        Four,
        Five,
        Six,
        Seven,
        Eight,
        Nine,
        Ten,
        Jack,
        Queen,
        King
    }

    public enum Suits
    {
        Diamonds,
        Clubs,
        Hearts,
        Spades
    }

    private Ranks rank;
    private Suits suit;
    private int value;
    private Image image;

    public Card(Ranks rank, Suits suit)
    {
        this.rank = rank;
        this.suit = suit;
        calculateValue();
        image = new Image(getClass().getResourceAsStream("../../../../../Assets/cards/Classic/" + suit.toString() + '-' + rank.toString() + ".png"));
    }

    private void calculateValue()
    {
        this.value = this.rank.ordinal() + 1;

        if (this.rank == Ranks.Jack ||
                this.rank == Ranks.Queen ||
                this.rank == Ranks.King)
        {
            this.value = 10;
        }
        else if (this.rank == Ranks.Ace)
        {
            this.value = 11;
        }
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public Ranks getRank()
    {
        return rank;
    }

    public Image getImage()
    {
        return image;
    }

    @Override
    public String toString()
    {
        return rank + " of " + suit;
    }

}