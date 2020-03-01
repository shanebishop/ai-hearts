package game;

import algorithms.GameID;
import model.Card;
import model.Model;

import java.util.*;
import java.util.stream.Collectors;

public class State extends GameID {

    private int[] playerScores;
    private List<Card> hand;
    private Card[] played; // Cards played this trick
    //private int trickNumber;

    // Instances of this class can only be created through public static methods
    private State()
    {
        played = new Card[Hearts.NUM_PLAYERS];
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof State)) {
            return false;
        }

        final State other = (State)o;

        // Trick number is not relevant to equality
        return played.length == other.played.length &&
                hand.size() == other.hand.size() &&
                playedEqual(played, other.played) && handsEqual(hand, other.hand);
    }

    @Override
    public int hashCode()
    {
        final Set<Card> playedMinusNulls = Arrays.stream(played)
                .filter(Objects::isNull).collect(Collectors.toSet());

        int result = new HashSet<>(hand).hashCode();
        result = 31 * result + playedMinusNulls.hashCode();
        return result;
    }

    private boolean playedEqual(Card[] played1, Card[] played2)
    {
        Set<Card> played1Set = Arrays.stream(played1)
                .filter(Objects::isNull).collect(Collectors.toSet());
        Set<Card> played2Set = Arrays.stream(played2)
                .filter(Objects::isNull).collect(Collectors.toSet());
        return played1Set.equals(played2Set);
    }

    private boolean handsEqual(final List<Card> hand1, final List<Card> hand2)
    {
        Set<Card> hand1Set = new HashSet<>(hand1);
        Set<Card> hand2Set = new HashSet<>(hand2);
        return hand1Set.equals(hand2Set);
    }

    @Override
    public String toString()
    {
        //TODO
    }

    public static GameID fromString()
    {
        //TODO
    }

    public static GameID fromModel(Model m, int playerID)
    {
        State state = new State();

        state.playerScores = m.getCurrentScoresForThisRound().clone();

        for (int i = 0; i < state.played.length; ++i) {
            state.played[i] = m.getPlayedCard(i);
        }

        state.hand = new ArrayList<>(m.getHand(playerID));
        //state.trickNumber = m.getTrickNumber();

        return state;
    }

}
