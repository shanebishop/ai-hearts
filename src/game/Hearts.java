package game;

import model.Card;
import model.Model;
import ui.View;

import java.util.List;

public class Hearts {

    private View view;
    private Model model;

    public static int CARDS_PER_PLAYER = 13;
    public static int NUM_PLAYERS = 4;
    public static int QUEEN_OF_SPADES_SCORE = 13;

    private boolean[] aiPlayers; // true if player i is an AI player, else false

    public Hearts(View v)
    {
        view = v;

        aiPlayers = new boolean[NUM_PLAYERS];
        // TODO Set the indices in aiPlayers to true based on what the user chose in setup dialog

        model = new Model();
        view.setModel(model);
    }

    public void startGame()
    {
        // TODO Implement me - this will be where the control will query the view for setup details, and initialize aiPlayers array
    }

    public void handleCardClicked(int playerID, int index)
    {
        final int activePlayer = model.getActivePlayer();

        if (playerID != activePlayer || !model.cardAtIndex(playerID, index) || isAIPlayer(activePlayer)) {
            return;
        }

        final Card cardLed = model.getLedCard();
        final Card tryingToPlay = model.getCard(playerID, index);
        final int tryingToPlaySuit = tryingToPlay.getSuit();

        if (cardLed != null && tryingToPlay.getSuit() != cardLed.getSuit() && playerHasCardsInSuit(activePlayer, cardLed.getSuit())) {
            // Player is not properly following suit
            return;
        }

        final boolean heartsBroken = model.isHeartsBroken();
        final boolean tryingToPlayHeart = tryingToPlaySuit == Card.HEART_SUIT;
        final boolean isFirstTrick = model.isFirstTrick();

        if (tryingToPlayHeart) {
            final boolean heartsLed = cardLed != null && cardLed.getSuit() == Card.HEART_SUIT;
            final boolean leading = cardLed == null;
            boolean canPlayHeart;

            if (isFirstTrick) {
                canPlayHeart = false; // Cannot play a points card on first trick
            } else if (leading) {
                System.out.printf("Trying to lead %s\n", tryingToPlay);
                System.out.printf("Hearts broken: %s\n", heartsBroken);
                System.out.printf("Player has only hearts: %s\n", playerHasOnlyHearts(activePlayer));
                canPlayHeart = heartsBroken || playerHasOnlyHearts(activePlayer);
            } else {
                System.out.println("Trying to play a heart when not leading");
                canPlayHeart = heartsLed || !playerHasCardsInSuit(activePlayer, cardLed.getSuit());
            }

            if (canPlayHeart) {
                model.setHeartsBroken();
            } else {
                // Trying to play a Heart, but cannot play a Heart
                return;
            }
        } else if (isFirstTrick && tryingToPlay.getSuit() == Card.SPADE_SUITE && tryingToPlay.getValue() == Card.QUEEN_VAL) {
            // Cannot play points card (i.e., Queen of Spades) on first trick
            return;
        }

        model.setPlayed(playerID, index);

        if (!model.isTrickOver()) {
            // Do not move to next player if the trick is over
            model.nextPlayer();
        }

        view.update();
    }

    // If a human clicks in the center during their turn, nothing happens, because trick is not over
    // If a human clicks in the center during an AI player's turn, nothing happens, because trick is not over
    // If a human clicks in the center after all AI players are done, then we are free to finalize trick,
    // because the trick is over
    // Therefore, the only guard we need for execution is model.isTrickOver()
    public void finalizeTrick()
    {
        if (model.isTrickOver()) {
            boolean roundOver = model.endTrick(); // Ends trick, and sets active player appropriately
            view.showScoreDialog();
            // Do we still call view.update() immediately if the round is over?
            // TODO If round is over, and a player has a score >= 100, game is over - need to handle this state as well
            view.update();
        }
    }

    private boolean isAIPlayer(int ind) { return aiPlayers[ind]; }

    private boolean playerHasCardsInSuit(int playerID, int suit)
    {
        List<Card> hand = model.getHand(playerID);
        for (Card c : hand) {
            if (c.getSuit() == suit) {
                return true;
            }
        }
        return false;
    }

    private boolean playerHasOnlyHearts(int playerID)
    {
        List<Card> hand = model.getHand(playerID);
        for (Card c : hand) {
            if (c.getSuit() != Card.HEART_SUIT) {
                return false;
            }
        }
        return true;
    }

}
