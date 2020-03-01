package game;

import algorithms.DumbPlayer;
import algorithms.GameID;
import algorithms.GameInterface;
import model.Card;
import model.Model;
import ui.View;

import java.util.*;

public class Hearts implements GameInterface<Card> {

    private View view;
    private Model model;

    public static int CARDS_PER_PLAYER = 13;
    public static int NUM_PLAYERS = 4;
    public static int QUEEN_OF_SPADES_SCORE = 13;
    public static int END_SCORE = 100;

    private PlayerType[] playerTypes;

    public Hearts(View v)
    {
        this();
        view = v;
        view.setModel(model);
    }

    public Hearts()
    {
        model = new Model();
    }

    private Hearts(Hearts other) {
        model = new Model(other.model);
        playerTypes = Arrays.copyOf(other.playerTypes, other.playerTypes.length);
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

    private boolean playerHasTwelveOrMoreHearts(int playerID)
    {
        int numHearts = 0;
        List<Card> hand = model.getHand(playerID);
        for (Card c : hand) {
            if (c.getSuit() != Card.HEART_SUIT) {
                ++numHearts;
            }
        }
        return numHearts >= 12;
    }

    @Override
    public void beginGame()
    {
        model = new Model();
    }

    @Override
    public void makeMove(Card move)
    {
        playCard(model.getActivePlayer(), move);
    }

    @Override
    public List<Double> payout()
    {
        List<Double> payout = new ArrayList<>();
        Map<Integer, int[]> scores = model.getRoundScores();
        double score;

        for (int playerID : scores.keySet()) {
            score = Arrays.stream(scores.get(playerID)).sum();
            payout.add(score);
        }

        return payout;
    }

    @Override
    public GameInterface<Card> deepCopy()
    {
        return new Hearts(this);
    }

    @Override
    public GameID getID()
    {
        return State.fromModel(model);
    }

    @Override
    public boolean isGameOver()
    {
        return model.isGameOver();
    }

    @Override
    public int numPlayers()
    {
        return playerTypes.length;
    }

    @Override
    public int activePlayer()
    {
        return model.getActivePlayer();
    }

    @Override
    public List<Card> moves()
    {
        List<Card> cards = model.getHand(activePlayer());

        if (cards.size() < 2) {
            // If have 0 or 1 cards, all cards in hand are playable
            return cards;
        }

        // Sort cards to ensure deterministic order
        Collections.sort(cards);

        List<Card> playable = new ArrayList<>();

        for (Card c : cards) {
            if (canPlay(c)) {
                playable.add(c);
            }
        }

        return playable;
    }

    private boolean canPlay(Card c) {
        final Card cardLed = model.getLedCard();
        final int tryingToPlaySuit = c.getSuit();

        final boolean heartsBroken = model.isHeartsBroken();
        final boolean tryingToPlayHeart = tryingToPlaySuit == Card.HEART_SUIT;
        final boolean isFirstTrick = model.isFirstTrick();

        if (isFirstTrick && cardLed != null) {
            if (c.getSuit() == cardLed.getSuit()) {
                return true;
            }
            if (playerHasCardsInSuit(activePlayer(), cardLed.getSuit())) {
                return false;
            }
            if (!c.isPointsCard()) {
                return true;
            }
            if (playerHasOnlyHearts(activePlayer())) {
                return true;
            }

            // c is a points card, so if player has 12 or more hearts
            // (either 13 hearts or 12 hearts + queen of spades),
            // they can technically play
            return playerHasTwelveOrMoreHearts(activePlayer());
        }

        if (cardLed == null) {
            if (isFirstTrick && !c.isPointsCard()) {
                // Technically, this isn't quite correct - it might be the
                // that the player only has points cards in their hand
                return true;
            }
            if (!tryingToPlayHeart) {
                return true;
            }
            return heartsBroken || playerHasOnlyHearts(activePlayer());
        }

        // Card has been led

        if (tryingToPlaySuit == cardLed.getSuit()) {
            // Following suit of card led
            return true;
        }

        return !playerHasCardsInSuit(activePlayer(), cardLed.getSuit());
    }

}
