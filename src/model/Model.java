package model;

import game.Hearts;

import java.util.*;

public class Model {

    private List<List<Card>> hands;
    private List<Card> p1Hand, p2Hand, p3Hand, p4Hand;
    private int[] playerScores;

    private Map<Integer, int[]> oldPlayerScores; // Map of round number to list of player scores

    private int activePlayer, trickNumber, roundNum;
    private boolean heartsBroken;

    private Card[] played; // Cards played this trick
    private Card led; // The card that was led to start the trick

    private boolean gameOver;

    public Model()
    {
        trickNumber = 1;
        roundNum = 1;
        led = null;
        played = new Card[Hearts.NUM_PLAYERS];
        playerScores = new int[Hearts.NUM_PLAYERS]; // In Java, values default to 0
        oldPlayerScores = new HashMap<>();
        heartsBroken = false;
        gameOver = false;

        dealCards();
    }

    public Model(Model other)
    {
        // Deep copy hands list
        hands = new ArrayList<>();
        for (int i = 0; i < other.hands.size(); ++i) {
            List<Card> hand = other.hands.get(i);
            hands.add(new ArrayList<>());

            for (Card card : hand) {
                hands.get(i).add(new Card(card));
            }
        }

        p1Hand = hands.get(0);
        p2Hand = hands.get(1);
        p3Hand = hands.get(2);
        p4Hand = hands.get(3);

        playerScores = Arrays.copyOf(other.playerScores, other.playerScores.length);

        // Deep copy oldPlayerScores map
        oldPlayerScores = new HashMap<>();
        for (int key : other.oldPlayerScores.keySet()) {
            int[] scores = other.oldPlayerScores.get(key);
            oldPlayerScores.put(key, Arrays.copyOf(scores, scores.length));
        }

        activePlayer = other.activePlayer;
        trickNumber = other.trickNumber;
        roundNum = other.roundNum;
        heartsBroken = other.heartsBroken;

        // Deep copy played array
        played = new Card[other.played.length];
        for (int i = 0; i < played.length; ++i) {
            played[i] = new Card(other.played[i]);
        }

        led = new Card(other.led);
        gameOver = other.gameOver;
    }

    public int getActivePlayer() { return activePlayer; }
    public int getRoundNum() { return roundNum; }
    public Map<Integer, int[]> getRoundScores() { return oldPlayerScores; }
    public Card getLedCard() { return led; }
    public List<Card> getHand(int handNum) { return hands.get(handNum); }
    public boolean isHeartsBroken() { return heartsBroken; }
    public boolean isFirstTrick() { return trickNumber == 1; }
    public boolean isGameOver() { return gameOver; }
    public int[] getCurrentScoresForThisRound() { return playerScores; }
    public int getTrickNumber() { return trickNumber; }

    public List<Card> getP1Hand() { return p1Hand; }
    public List<Card> getP2Hand() { return p2Hand; }
    public List<Card> getP3Hand() { return p3Hand; }
    public List<Card> getP4Hand() { return p4Hand; }

    public int getP1HandSize() { return p1Hand.size(); }
    public int getP2HandSize() { return p2Hand.size(); }
    public int getP3HandSize() { return p3Hand.size(); }
    public int getP4HandSize() { return p4Hand.size(); }

    public Card getCard(int handNum, int index)
    {
        List<Card> hand = hands.get(handNum);
        return index < hand.size() ? hand.get(index) : null;
    }

    public int getSuit(int handNum, int index)
    {
        List<Card> hand = hands.get(handNum);
        return index < hand.size() ? hand.get(index).getSuit() : -1;
    }

    public int getValue(int handNum, int index)
    {
        List<Card> hand = hands.get(handNum);
        return index < hand.size() ? hand.get(index).getValue() : -1;
    }

    public boolean cardAtIndex(int playerID, int index)
    {
        return index < hands.get(playerID).size();
    }

    public void setHeartsBroken()
    {
        if (!heartsBroken) {
            heartsBroken = true;
            System.out.println("Hearts broken!");
        }
    }

    public void setPlayed(int playerID, Card c)
    {
        setPlayed(playerID, hands.get(playerID).indexOf(c));
    }

    public void setPlayed(int playerID, int index)
    {
        played[playerID] = hands.get(playerID).remove(index);
        if (led == null) {
            led = played[playerID];
        }
        if (played[playerID].getSuit() == Card.HEART_SUIT && !heartsBroken) {
            setHeartsBroken();
        }
    }

    public Card getPlayedCard(int index) { return played[index]; }

    public boolean isTrickOver()
    {
        for (Card card : played) {
            if (card == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Ends the trick, and sets active player appropriately. Returns true if
     * round is over.
     *
     * If round is not over, sets active player to player who won the trick.
     * If round is over, deals cards, and sets active player to player 1, so
     * card trading can commence.
     *
     * @return true if round is over
     */
    public boolean endTrick()
    {
        final boolean roundOver = trickNumber == Hearts.CARDS_PER_PLAYER;
        final int oldTrickNum = trickNumber;
        trickNumber = roundOver ? 1 : trickNumber + 1;

        // Determine winner
        activePlayer = determineTrickWinner();

        // Add to the points to the player that won this trick
        scoreTrick(activePlayer, oldTrickNum);

        // Reset cards
        for (int i = 0; i < played.length; ++i) {
            played[i] = null;
        }
        led = null;

        if (roundOver) {
            handleRoundOver();
        }

        return roundOver;
    }

    private void handleRoundOver()
    {
        // Update old scores
        // If a score reaches min for ending game, this.gameOver is set to true
        // TODO Implement shooting the moon
        updateOldScores(roundNum);

        if (gameOver) {
            return; // Don't want to do any resetting, because game is over
        }

        // Increment round number
        ++roundNum;

        // Reset scores, heartsBroken, and trick number
        for (int i = 0; i < playerScores.length; ++i) {
            playerScores[i] = 0;
        }
        heartsBroken = false;
        trickNumber = 1;

        // Deal cards
        dealCards();

        // Choose arbitrary player to be first player for card trading
        activePlayer = 0;
    }

    private void scoreTrick(int playerID, int trickNumber)
    {
        int score = 0;
        for (Card c : played) {
            if (c.getSuit() == Card.HEART_SUIT) {
                ++score;
            } else if (c.getValue() == Card.QUEEN_VAL && c.getSuit() == Card.SPADE_SUITE) {
                score += Hearts.QUEEN_OF_SPADES_SCORE;
            }
        }

        playerScores[playerID] += score;

        System.out.printf("Player %d won trick %d, earning %d point%s. ",
                playerID+1, trickNumber, score, score == 1 ? "" : "s");
        System.out.printf("Player %d's score is now %d.\n", playerID+1, playerScores[playerID]);
    }

    private int determineTrickWinner()
    {
        final int ledSuite = led.getSuit();
        int maxVal = 0, trickWinner = 0;

        for (int i = 0; i < played.length; ++i) {
            final Card c = played[i];
            final int val = c.getSuit() == ledSuite ? c.getValue() : 0;

            if (val > maxVal) {
                maxVal = val;
                trickWinner = i;
            }
        }

        return trickWinner;
    }

    public int nextPlayer()
    {
        ++activePlayer;
        if (activePlayer == Hearts.NUM_PLAYERS) {
            activePlayer = 0;
        }
        return activePlayer;
    }

    // TODO Implement shooting the moon
    private boolean updateOldScores(int roundNumber)
    {
        // Clone the array
        int[] scoresClone = new int[playerScores.length];
        for (int i = 0; i < playerScores.length; ++i) {
            scoresClone[i] = playerScores[i];
            if (scoresClone[i] >= Hearts.END_SCORE) {
                gameOver = true;
            }
        }

        oldPlayerScores.put(roundNumber, scoresClone);
        return gameOver;
    }

    private void dealCards()
    {
        // Reset hands
        p1Hand = new ArrayList<>(Hearts.CARDS_PER_PLAYER);
        p2Hand = new ArrayList<>(Hearts.CARDS_PER_PLAYER);
        p3Hand = new ArrayList<>(Hearts.CARDS_PER_PLAYER);
        p4Hand = new ArrayList<>(Hearts.CARDS_PER_PLAYER);
        hands = Arrays.asList(p1Hand, p2Hand, p3Hand, p4Hand);

        final int numPlayers = Hearts.NUM_PLAYERS;
        final int numCards = numPlayers * Hearts.CARDS_PER_PLAYER;

        List<Card> allCards = new ArrayList<>(numCards);
        for (int val = 2; val <= Hearts.CARDS_PER_PLAYER+1; ++val) {  // Values start at 2
            for (int s = 0; s < numPlayers; ++s) {                    // Suites start at 0
                allCards.add(new Card(s, val));
            }
        }
        Collections.shuffle(allCards);

        for (int i = 0; i < Hearts.CARDS_PER_PLAYER; ++i) {
            for (int j = 0; j < numPlayers; ++j) {
                hands.get(j).add(allCards.get(i*numPlayers+j));
            }
        }
    }

}
