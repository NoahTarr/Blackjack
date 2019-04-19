package tarr.noah.Final.blackjack;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import tarr.noah.Final.blackjack.deck.Card;

import java.util.ArrayList;

/**
 * Created by noaht on 4/29/2017.
 */
public class Person
{
    private IntegerProperty score = new SimpleIntegerProperty("this", "score", 0);
    private IntegerProperty numCards = new SimpleIntegerProperty("this", "numCards", 0);
    private IntegerProperty chipBalance = new SimpleIntegerProperty("this", "chips", 0);
    private Label LBLMainCard;
    private Label LBLLastDealtCard;
    private ArrayList<Card> hand = new ArrayList<>();

    public Person (String name)
    {
        numCards.addListener((observable, oldValue, newValue) ->
        {
            System.out.println(name);
            System.out.println("Old Score: " + score.getValue() + "\nOld Chip Value: " + chipBalance.getValue());

            score.setValue(0);
            for (Card aCard : hand)
            {
                score.setValue(score.getValue() + aCard.getValue());
            }

            for (Card c : hand)
            {
                if (c.getValue() == 11)
                {
                    if (score.getValue() > 21)
                    {
                        score.setValue(score.getValue() - 10);
                        c.setValue(1);
                    }
                }
            }
            System.out.println("New Score: " + score.getValue() + "\nNew Chip Value: " + chipBalance.getValue());
        });
    }

    public ArrayList<Card> getHand()
    {
        return hand;
    }

    public int getScore()
    {
        return score.get();
    }


    public Label getLBLMainCard()
    {
        return LBLMainCard;
    }

    public void setLBLMainCard(Label LBLMainCard)
    {
        this.LBLMainCard = LBLMainCard;
    }


    public Label getLBLLastDealtCard()
    {
        return LBLLastDealtCard;
    }

    public void setLBLLastDealtCard(Label LBLLastDealtCard)
    {
        this.LBLLastDealtCard = LBLLastDealtCard;
    }


    public IntegerProperty numCardsProperty()
    {
        return numCards;
    }


    public int getChipBalance()
    {
        return chipBalance.get();
    }

    public void setChipBalance(int chipBalance)
    {
        this.chipBalance.set(chipBalance);
    }

    public IntegerProperty chipBalanceProperty()
    {
        return chipBalance;
    }


    public void addCard(Card newCard)
    {
        hand.add(newCard);
        numCards.setValue(numCards.getValue() + 1);
    }


    public void clearHand()
    {
        hand.clear();
        score.setValue(0);
    }


    public void removeOneCard(int index)
    {
        hand.remove(index);
    }
}
