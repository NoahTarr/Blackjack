package tarr.noah.Final.blackjack.deck;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by noaht on 4/29/2017.
 */
public class Deck
{
    private int thisCard;
    private IntegerProperty remainingCards = new SimpleIntegerProperty();
    private ArrayList<Card> deck;
    private BooleanProperty isEmpty = new SimpleBooleanProperty(this, "isEmpty", false);
    private PropertyChangeSupport remainingCardsListener = new PropertyChangeSupport(this);
    

    public Deck()
    {
        resetDeck();
    }

    public Card deal()
    {
        Card toBeDealt = deck.get(thisCard);
        if (thisCard == deck.size() - 1)
        {
            isEmpty.set(true);
            return null;
        }
        else
        {
            thisCard += 1;
            remainingCards.setValue(remainingCards.getValue() - 1);
//            this.remainingCardsListener.firePropertyChange("remainingCards", remainingCards - 1, remainingCards);
        }
        return  toBeDealt;
    }

    public void resetDeck()
    {
        deck = new ArrayList<>();

        for (Card.Suits s : Card.Suits.values())
        {
            for (Card.Ranks r : Card.Ranks.values())
            {
                deck.add(new Card(r, s));
            }
        }
        thisCard = 0;
        remainingCards.setValue(52);
    }

    public IntegerProperty remainingCardsProperty()
    {
        return remainingCards;
    }

    public void shuffleDeck()
    {
        Collections.shuffle(deck);
    }

    public int getRemainingCards()
    {
        return remainingCards.getValue();
    }

    public void addPropertyChangeListener (PropertyChangeListener remainingCards)
    {
        remainingCardsListener.addPropertyChangeListener(remainingCards);
    }

    public void removePropertyChangeListener (PropertyChangeListener remainingCards)
    {
        remainingCardsListener.removePropertyChangeListener(remainingCards);
    }
}
