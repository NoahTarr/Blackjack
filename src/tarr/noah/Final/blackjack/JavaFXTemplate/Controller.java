/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tarr.noah.Final.blackjack.JavaFXTemplate;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import tarr.noah.Final.blackjack.Person;
import tarr.noah.Final.blackjack.deck.Card;
import tarr.noah.Final.blackjack.deck.Deck;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;


/**
 * Created by noaht on 4/29/2017.
 * noahtarr1@gmail.com
 */

public class Controller implements Initializable
{
    @FXML
    private AnchorPane ANCRParent;
    @FXML
    private Button BTNReset;
    @FXML
    private Label LBLPlayerInfo;
    @FXML
    private Label LBLDealerInfo;
    @FXML
    private Label LBLFinalStanding;
    @FXML
    private TextArea TXTARules;
    @FXML
    private ToggleButton BTNToggleRules;

    @FXML
    private ImageView IMGDeck3;
    @FXML
    private ImageView IMGDeck2;
    @FXML
    private ImageView IMGDeck1;

    @FXML
    private Label LBLDlrMainCard;
    @FXML
    private Label LBLPlrMainCard;
    @FXML
    private Label LBLPlr2ndHandInfo;
    @FXML
    private Label LBLDeck;
    @FXML
    private Label LBLPlayerChipCount;

    @FXML
    private Button BTNHit;
    @FXML
    private Button BTNStand;
    @FXML
    private Button BTNSplit;
    @FXML
    private Button BTNDouble;

    private Deck mainDeck = new Deck();
    private Person player = new Person("player");
    private Person player2ndHand = new Person("Player 2nd Hand");
    private Person dealer = new Person("dealer");

    private Alert endCurrentRoundDialog = new Alert(Alert.AlertType.CONFIRMATION, "");
    private TextInputDialog buyInDialog = new TextInputDialog("0");
    private boolean roundInProgress = false;
    private boolean onSecondHand = false;
    private boolean wasSplit = false;
    private boolean doubling = false;

    private ArrayList<Label> newCardLBLs = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        BTNToggleRules.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue)
            {
                TXTARules.toFront();
                TXTARules.setVisible(true);
            }
            else
            {
                TXTARules.setVisible(false);
            }
        });

        mainDeck.remainingCardsProperty().addListener((observable, oldValue, newValue) ->
        {
            if (mainDeck.remainingCardsProperty().getValue() < 19)
            {
                IMGDeck1.setVisible(false);
            }
            else if (mainDeck.remainingCardsProperty().getValue() < 32)
            {
                IMGDeck2.setVisible(false);
            }
            else if (mainDeck.remainingCardsProperty().getValue() < 45)
            {
                IMGDeck3.setVisible(false);
            }
        });

        player.numCardsProperty().addListener((observable, oldValue, newValue) ->
        {
            if (player.getScore() > 21 && !doubling)
            {
                LBLPlayerInfo.setText("BUST");
                disableButtons(true, true, true, false, true);
                roundInProgress = false;
                if (wasSplit)
                {
                    disableButtons(true, false, true, false, true);
                    roundInProgress = true;
                }
                else
                {
                    LBLFinalStanding.setText("BUST");
                }
            }
        });

        player2ndHand.numCardsProperty().addListener((observable, oldValue, newValue) ->
        {
            if (player2ndHand.getScore() > 21)
            {
                LBLPlr2ndHandInfo.setText("BUST");
                LBLFinalStanding.setText("Moves for\nTop Hand");
                onSecondHand = false;
            }
        });

        endCurrentRoundDialog.setHeaderText(null);

        /***
         * Initialize Chips
         */
//        initializeChips();

        //DEBUG: Set the following to true for debugging. Makes point values for dealer and player visible
        boolean visible = false;

        LBLPlayerInfo.setVisible(visible);
        LBLDealerInfo.setVisible(visible);
        LBLPlr2ndHandInfo.setVisible(visible);

        MediaPlayer cardShuffleSound = new MediaPlayer(new Media(new File(
                System.getProperty("user.dir") + "/src/Assets//sounds/cardShuffle.wav").toURI().toString()));
        cardShuffleSound.play();

        mainDeck.shuffleDeck();
        LBLDeck.setText(String.valueOf(mainDeck.getRemainingCards()));
        player.setLBLMainCard(LBLPlrMainCard);
        dealer.setLBLMainCard(LBLDlrMainCard);
    }

//    private void initializeChips()
//    {
//        buyInDialog.setTitle("Starting Chip Amount");
//        buyInDialog.setHeaderText(null);
//        buyInDialog.setContentText("Enter the amount of chips you want to start with:");
//        Optional<String> result = buyInDialog.showAndWait();
//
//        if (!result.isPresent())
//        {
//            System.exit(0);
//        }
//        else
//        {
//            LBLPlayerChipCount.setText(String.valueOf(player.getChipBalance()));
//        }
//    }


    public void startRoundAction()
    {
        if (roundInProgress)
        {
            endCurrentRoundDialog.setContentText("Are you sure you want to end the current round progress?");
            endCurrentRoundDialog.showAndWait();
            if(endCurrentRoundDialog.getResult() == ButtonType.CANCEL)
            {
                return;
            }
        }
        resetBoard();

        roundInProgress = true;

        startRoundDeal();

//        DEBUG: Set the to false for debugging split button
        BTNSplit.setDisable(true);
    }

    private void startRoundDeal()
    {
        final int[] counter = {0};
        Timeline timelineStartRoundAnimation = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(400), event -> {
            ImageView image;
            switch (counter[0])
            {
                case 0:
                    player.addCard(mainDeck.deal());
                    placeCardSound();
                    image = new ImageView(player.getHand().get(0).getImage());
                    image.setPreserveRatio(true);
                    image.setFitHeight(120);
                    LBLPlrMainCard.setGraphic(image);
                    LBLDeck.setText(String.valueOf(mainDeck.getRemainingCards()));
                    counter[0]++;
                    break;
                case 1:
                    dealer.addCard(mainDeck.deal());
                    placeCardSound();
                    image = new ImageView(new Image(getClass().getResourceAsStream("../../../../../Assets/Card-Back-Classic.png")));
                    image.setPreserveRatio(true);
                    image.setFitHeight(120);
                    LBLDlrMainCard.setId("hidden");
                    LBLDlrMainCard.setUserData(dealer.getHand().get(0).getImage());
                    LBLDlrMainCard.setGraphic(image);
                    LBLDeck.setText(String.valueOf(mainDeck.getRemainingCards()));
                    counter[0]++;
                    break;
                case 2:
                    newCard(player, mainDeck.deal(), false);
                    counter[0]++;
                    break;
                case 3:
                    newCard(dealer, mainDeck.deal(), false);
                    counter[0]++;
                    break;
                case 4:
                    disableButtons(false, false, false, false, true);
                    LBLDeck.setText(String.valueOf(mainDeck.getRemainingCards()));

                    LBLPlayerInfo.setText(String.valueOf(player.getScore()));
                    LBLDealerInfo.setText(String.valueOf(dealer.getScore()));

                    if(player.getHand().get(0).getRank().equals(player.getHand().get(1).getRank()))
                    {
                        BTNSplit.setDisable(false);
                    }
                    else if(player.getScore() == 21)
                    {
                        playerBlackjack();
                    }

                    timelineStartRoundAnimation.stop();
            }
        });
        timelineStartRoundAnimation.getKeyFrames().add(keyFrame);
        timelineStartRoundAnimation.setCycleCount(Animation.INDEFINITE);
        timelineStartRoundAnimation.play();
    }

    public void resetAction()
    {
        endCurrentRoundDialog.setContentText("Are you sure you want to reset the game?\n" +
                "This will reset the board and the deck!");
        endCurrentRoundDialog.showAndWait();
        if(endCurrentRoundDialog.getResult() == ButtonType.CANCEL)
        {
            return;
        }
        resetDeck();
        resetBoard();
    }

    public void hitAction()
    {
        disableButtons(false, false, false, false, true);
        if (onSecondHand)
        {
            newCard(player2ndHand, mainDeck.deal(), false);

            if (player2ndHand.getScore() <= 21)
            {
                LBLPlr2ndHandInfo.setText(String.valueOf(player2ndHand.getScore()));
            }
        }
        else
        {
            newCard(player, mainDeck.deal(), false);

            if (player.getScore() <= 21)
            {
                LBLPlayerInfo.setText(String.valueOf(player.getScore()));
            }
        }
        LBLDeck.setText(String.valueOf(mainDeck.getRemainingCards()));
    }

    public void standAction()
    {
        if (onSecondHand)
        {
            onSecondHand = false;
            LBLFinalStanding.setText("Moves for\nTop Hand");
            return;
        }
        disableButtons(true, true, true, false, true);
        dealerTurn();
        roundInProgress = false;
    }

    public void doubleAction()
    {
        doubling = true;
        if (onSecondHand)
        {
            newCard(player2ndHand, mainDeck.deal(), true);
            LBLPlr2ndHandInfo.setText(String.valueOf(player2ndHand.getScore()));
            onSecondHand = false;
        }
        else
        {
            newCard(player, mainDeck.deal(), true);
            LBLPlayerInfo.setText(String.valueOf(player.getScore()));
            disableButtons(true, true, true, true, true);
            dealerTurn();
        }
    }

    public void splitAction()
    {
        disableButtons(false, false , false, false, true);
        onSecondHand = true;
        wasSplit = true;

        LBLFinalStanding.setText("Moves for\nBottom Hand");

        Label LBLPlr2ndMainCard = newCardLBLs.get(newCardLBLs.size() - 2);
        newCardLBLs.add(LBLPlr2ndHandInfo);

        LBLPlr2ndMainCard.setLayoutX(LBLPlrMainCard.getLayoutX());
        LBLPlr2ndMainCard.setLayoutY(LBLPlrMainCard.getLayoutY() + 30);
        LBLPlrMainCard.setLayoutY(LBLPlrMainCard.getLayoutY() - 100);
        player2ndHand.setLBLMainCard(LBLPlr2ndMainCard);
        player2ndHand.setLBLLastDealtCard(LBLPlr2ndMainCard);

        player2ndHand.addCard(player.getHand().get(1));
        player.removeOneCard(1);

        LBLPlayerInfo.setText(String.valueOf(player.getScore()));
        LBLPlr2ndHandInfo.setText(String.valueOf(player2ndHand.getScore()));
    }


    /******************
     * Dealer & End Round Methods
     *****************/

    private void dealerTurn()
    {
        ImageView image = new ImageView(dealer.getHand().get(0).getImage());
        image.setPreserveRatio(true);
        image.setFitHeight(120);
        LBLDlrMainCard.setGraphic(image);

        Timeline timelineDealerTurnAnimation = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.millis(500), event -> {
            if(dealer.getScore() >= 17) {
                timelineDealerTurnAnimation.stop();
                determineResults();
            }
            else
            {
                newCard(dealer, mainDeck.deal(), false);
                LBLDealerInfo.setText(String.valueOf(dealer.getScore()));
                LBLDeck.setText(String.valueOf(mainDeck.getRemainingCards()));
            }
        });
        timelineDealerTurnAnimation.getKeyFrames().add(keyFrame);
        timelineDealerTurnAnimation.setCycleCount(Animation.INDEFINITE);
        timelineDealerTurnAnimation.play();


//DEBUG: The following is skips the animation for debugging
//        while (dealer.getScore() < 17)
//        {
//            newCard(dealer, mainDeck.deal(), false);
//            LBLDeck.setText(String.valueOf(mainDeck.getRemainingCards()));
//            LBLDealerInfo.setText(String.valueOf(dealer.getScore()));
//        }
    }

    private void determineResults()
    {
        for (Label l : newCardLBLs)
        {
            if (l.getId().equals("hidden"))
            {
                ImageView image = new ImageView((Image)l.getUserData());
                image.setPreserveRatio(true);
                image.setFitHeight(120);
                l.setGraphic(image);
            }
        }

        String contentText1stHand = "";
        String contentText2ndHand = "";

        if (dealer.getScore() == 21 && dealer.getHand().size() == 2)
        {
            LBLFinalStanding.setText("LOSE");
            return;
        }
        else if (player.getScore() > 21)
        {
            contentText1stHand = "BUST";
        }
        else if (player.getScore() == dealer.getScore())
        {
            contentText1stHand = "PUSH";
        }
        else if ((player.getScore() > dealer.getScore() && player.getScore() <= 21) ||
                (dealer.getScore() > 21 && player.getScore() <= 21))
        {
            contentText1stHand = "WIN!";
        }
        else
        {
            contentText1stHand = "LOSE";
        }

        if (wasSplit && !contentText1stHand.equals("BLACKJACK"))
        {
            contentText2ndHand = determine2ndHandResults();
        }

        if (wasSplit)
        {
            LBLFinalStanding.setText("The top hand: " + contentText1stHand +
                            '\n' + "The bottom hand: " + contentText2ndHand);
        }
        else
        {
            LBLFinalStanding.setText(contentText1stHand);
        }
        roundInProgress = false;
    }

    private String determine2ndHandResults()
    {
        if (player2ndHand.getScore() > 21)
        {
            return "BUST";
        }
        else if (player2ndHand.getScore() == dealer.getScore())
        {
            return "PUSH";
        }
        else if (player2ndHand.getScore() > dealer.getScore() ||
                dealer.getScore() > 21)
        {
            return "WIN!";
        }
        else
        {
            return "LOSE";
        }
    }

    private void playerBlackjack()
    {
        disableButtons(true, true, true, false, true);
        roundInProgress = false;
        ImageView image = new ImageView(dealer.getHand().get(0).getImage());
        image.setPreserveRatio(true);
        image.setFitHeight(120);
        LBLDlrMainCard.setGraphic(image);

        if (player.getScore() == dealer.getScore())
        {
            LBLFinalStanding.setText("PUSH");
        }
        else
        {
            LBLFinalStanding.setText("BLACKJACK!!!");
        }
    }

    /******************
     *  Helper Methods
     *****************/

    private void newCard(Person personToCheck, Card newCard, boolean hidden)
    {
        placeCardSound();

        ImageView image = new ImageView(newCard.getImage());

        if (hidden)
        {
            image = new ImageView(new Image(getClass().getResourceAsStream("../../../../../Assets/Card-Back-Classic.png")));
        }

        image.setPreserveRatio(true);
        image.setFitHeight(120);
        Label newCardLBL = new Label("", image);
        newCardLBL.setUserData(newCard.toString());
        newCardLBL.setId(newCard.toString());

        if (hidden)
        {
            newCardLBL.setId("hidden");
            newCardLBL.setUserData(newCard.getImage());
        }

        if (personToCheck.getHand().size() == 1)
        {
            newCardLBL.setLayoutX(personToCheck.getLBLMainCard().getLayoutX() + 90);
            newCardLBL.setLayoutY(personToCheck.getLBLMainCard().getLayoutY());
        }
        else
        {
            newCardLBL.setLayoutX(personToCheck.getLBLLastDealtCard().getLayoutX() + 90);
            newCardLBL.setLayoutY(personToCheck.getLBLLastDealtCard().getLayoutY());
        }
        personToCheck.addCard(newCard);
        personToCheck.setLBLLastDealtCard(newCardLBL);


        LBLDeck.setText(String.valueOf(mainDeck.getRemainingCards()));
        newCardLBLs.add(newCardLBL);
        ANCRParent.getChildren().add(newCardLBL);
    }


    private void resetBoard()
    {
        Random r = new Random();
        int randNum = r.nextInt(3) + 1;

        MediaPlayer cardShoveSound = new MediaPlayer(new Media(new File(
                System.getProperty("user.dir") + "/src/Assets//sounds/cardShove" + randNum + ".wav").toURI().toString()));
        cardShoveSound.play();

        if (mainDeck.getRemainingCards() < 15)
        {
            resetDeck();
        }

        disableButtons(true, true, true, true, true);
        LBLDeck.setText(String.valueOf(mainDeck.getRemainingCards()));

        for (Label lbl: newCardLBLs)
        {
            lbl.setText("");
            lbl.setGraphic(null);
        }
        newCardLBLs.clear();

        player.clearHand();
        player2ndHand.clearHand();
        dealer.clearHand();

        LBLPlrMainCard.setText("");
        LBLPlrMainCard.setGraphic(null);
        LBLPlrMainCard.setLayoutX(210);
        LBLPlrMainCard.setLayoutY(550);
        LBLDlrMainCard.setText("");
        LBLDlrMainCard.setGraphic(null);

        LBLPlayerInfo.setText("");
        LBLPlr2ndHandInfo.setText("");
        LBLDealerInfo.setText("");
        LBLFinalStanding.setText("");

        roundInProgress = false;
        onSecondHand = false;
        wasSplit = false;
        doubling = false;
    }

    private void resetDeck()
    {
        IMGDeck3.setVisible(true);
        IMGDeck2.setVisible(true);
        IMGDeck1.setVisible(true);
        mainDeck.resetDeck();
        mainDeck.shuffleDeck();
    }

    private void disableButtons(boolean hit, boolean stand, boolean Double, boolean reset, boolean split)
    {
        BTNHit.setDisable(hit);
        BTNStand.setDisable(stand);
        BTNDouble.setDisable(Double);
        BTNReset.setDisable(reset);
        BTNSplit.setDisable(split);
    }

    private void placeCardSound()
    {
        Random r = new Random();
        int randNum = r.nextInt(7) + 1;

        MediaPlayer cardSlideSound = new MediaPlayer(new Media(new File(
                System.getProperty("user.dir") + "/src/Assets//sounds/cardSlide" + randNum + ".wav").toURI().toString()));
        cardSlideSound.play();
    }
}
