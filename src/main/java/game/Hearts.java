package game;

import algorithms.*;
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
    private boolean training;
    private boolean runningAITurn;

    private CFRPlayer cfrPlayer; // Single CFRPlayer instance for all CFR processing

    public Hearts(View v)
    {
        this();
        view = v;
        view.setModel(model);

        training = false;
        cfrPlayer = new CFRPlayer(this);
    }

    public Hearts()
    {
        model = new Model();
    }

    private Hearts(Hearts other, boolean runningAITurn) {
        view = other.view;
        training = other.training;
        this.runningAITurn = other.runningAITurn || runningAITurn;
        model = new Model(other.model);
        model.setRunningAITurn(this.runningAITurn);
        playerTypes = Arrays.copyOf(other.playerTypes, other.playerTypes.length);
    }

    @Override
    public int roundNumber() { return model.getRoundNum(); }

    @Override
    public int[] scores() { return model.getTotalScores(); }

    public void startGame()
    {
        initAIPlayers();
    }

    public void setAllPlayerTypes(PlayerType playerType)
    {
        playerTypes = new PlayerType[NUM_PLAYERS];
        for (int i = 0; i < playerTypes.length; ++i) {
            playerTypes[i] = playerType;
        }
    }

    public void setTraining(boolean b) { training = b; }

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
        } else if (isFirstTrick && tryingToPlay.getSuit() == Card.SPADE_SUIT && tryingToPlay.getValue() == Card.QUEEN_VAL) {
            // Cannot play points card (i.e., Queen of Spades) on first trick
            return;
        }

        playCard(playerID, model.getCard(playerID, index));
        view.update();
    }

    /**
     * Method called for both a human or an AI player playing a card
     *
     * @return true if round over, else false
     */
    private boolean playCard(int playerID, Card toPlay)
    {
        boolean roundOver = false;

        model.setPlayed(playerID, toPlay);

//        if (view != null && !runningAITurn) {
//            view.update();
//        }

        if (!model.isTrickOver()) {
            // Do not move to next player if the trick is over
            advancePlayer();
        }

        // Must only finalize trick when training or running an AI planning
        // This is because the user must finalize a trick by clicking the
        // center pane when they are ready
        if (model.isTrickOver() && (training || runningAITurn)) {
            roundOver = finalizeTrick();
        }

        return roundOver;
    }

    private void advancePlayer()
    {
        final int activePlayer = model.nextPlayer();
        if (isAIPlayer(activePlayer) && !training && !runningAITurn) {
            // Active player is an AI player
            handleAIPlayerTurn();
        }
    }

    private void handleAIPlayerTurn()
    {
        if (runningAITurn) {
            return; // Do not run an AI turn if currently running an AI turn
        }

        final int activePlayer = model.getActivePlayer();
        final List<Card> hand = model.getHand(activePlayer);
        final Card cardLed = model.getLedCard();
        final boolean isFirstTrick = model.isFirstTrick();
        final boolean heartsBroken = model.isHeartsBroken();
        final Card[] cardsPlayed = model.getCardsPlayed();

        Card toPlay = null;

        switch (playerTypes[activePlayer]) {
            case DUMB_AI:
                toPlay = DumbPlayer.chooseCard(hand, cardLed, isFirstTrick, heartsBroken);
                break;
            case CFR_AI:
                toPlay = cfrPlayer.chooseCard(hand, isFirstTrick, heartsBroken, cardsPlayed, activePlayer);
                break;
            case UCT_AI:
                UCTAlgorithm<Card> uctAlgorithm = new UCTAlgorithm<>(this);
                toPlay = uctAlgorithm.uct(getState());
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
    private boolean finalizeTrick()
    {
        boolean roundOver = false;

        if (model.isTrickOver()) {
            roundOver = model.endTrick(); // Ends trick, and sets active player appropriately

            if (roundOver && !training && !runningAITurn && view != null) {
                view.showScoreDialog();
            }

            if (isAIPlayer(model.getActivePlayer()) && !training) {
                handleAIPlayerTurn();
            }

            // Do we still call view.update() immediately if the round is over?
            // TODO If round is over, and a player has a score >= 100, game is over - need to handle this state as well
            if (view != null) {
                view.update();
            }
        }

        return roundOver;
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
    public boolean makeMove(Card move)
    {
        return playCard(model.getActivePlayer(), move);
    }

    @Override
    public List<Double> payout()
    {
        List<Double> payout = new ArrayList<>();
        int[] scores = model.getFinalTotalScores();

        for (int i = 0; i < scores.length; ++i) {
            payout.add((double) scores[i]);
        }

        // TODO Temp
        if (payout.size() < 4) {
            System.err.println("payout is incorrect size");
            System.exit(1);
        }

        return payout;
    }

    @Override
    public GameInterface<Card> deepCopy()
    {
        final boolean runningAITurn = true;
        return new Hearts(this, runningAITurn);
    }

    @Override
    public GameID getID(int playerID)
    {
        return State.fromModel(model, playerID);
    }

    @Override
    public boolean isGameOver()
    {
        return model.isGameOver();
    }

    @Override
    public int numPlayers() { return Hearts.NUM_PLAYERS; }

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

    @Override
    public State getState() {
        return State.fromModel(model, activePlayer());
    }

    @Override
    public List<Integer> winningPlayers() {
        return model.winningPlayers();
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
