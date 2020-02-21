package game;

import algorithms.DumbPlayer;
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

    private PlayerType[] playerTypes;

    public Hearts(View v)
    {
        view = v;
        model = new Model();
        view.setModel(model);
    }

    public void startGame()
    {
        initAIPlayers();
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
                canPlayHeart = heartsBroken || playerHasOnlyHearts(activePlayer);
            } else {
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

        playCard(playerID, model.getCard(playerID, index));
        view.update();
    }

    // Method called for both a human or an AI player playing a card
    private void playCard(int playerID, Card toPlay)
    {
        model.setPlayed(playerID, toPlay);

        if (!model.isTrickOver()) {
            // Do not move to next player if the trick is over
            advancePlayer();
        }
    }

    private void advancePlayer()
    {
        final int activePlayer = model.nextPlayer();
        if (isAIPlayer(activePlayer)) {
            // Active player is an AI player
            handleAIPlayerTurn();
        }
    }

    private void handleAIPlayerTurn()
    {
        final int activePlayer = model.getActivePlayer();
        final List<Card> hand = model.getHand(activePlayer);
        final Card cardLed = model.getLedCard();
        final boolean isFirstTrick = model.isFirstTrick();
        final boolean heartsBroken = model.isHeartsBroken();

        Card toPlay = null;

        switch (playerTypes[activePlayer]) {
            case DUMB_AI:
                toPlay = DumbPlayer.chooseCard(hand, cardLed, isFirstTrick, heartsBroken);
                break;
            case CFR_AI:
                System.err.println("CFR AI not supported yet");
                System.exit(1);
                break;
            case UCT_AI:
                System.err.println("UCT AI not supported yet");
                System.exit(1);
                break;
            default:
                System.err.printf("Invalid PlayerType: %s\n", playerTypes[activePlayer]);
                System.exit(1);
                break;
        }

        playCard(activePlayer, toPlay);
        view.update();
    }

    public void handleCenterClicked()
    {
        if (model.isTrickOver()) {
            finalizeTrick();
        }
        if (isAIPlayer(model.getActivePlayer())) {
            handleAIPlayerTurn();
        }
    }

    // If a human clicks in the center during their turn, nothing happens, because trick is not over
    // If a human clicks in the center during an AI player's turn, nothing happens, because trick is not over
    // If a human clicks in the center after all AI players are done, then we are free to finalize trick,
    // because the trick is over
    // Therefore, the only guard we need for execution is model.isTrickOver()
    private void finalizeTrick()
    {
        if (model.isTrickOver()) {
            boolean roundOver = model.endTrick(); // Ends trick, and sets active player appropriately
            if (roundOver) {
                view.showScoreDialog();
            }

            if (isAIPlayer(model.getActivePlayer())) {
                // TODO Handle AI Player turn
            }

            // Do we still call view.update() immediately if the round is over?
            // TODO If round is over, and a player has a score >= 100, game is over - need to handle this state as well
            view.update();
        }
    }

    private boolean isAIPlayer(int playerID) { return playerTypes[playerID] != PlayerType.HUMAN; }

    private void initAIPlayers()
    {
        playerTypes = view.getPlayerTypes();
    }

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
