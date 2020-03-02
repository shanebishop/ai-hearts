package game;

import algorithms.GameID;
import model.Card;
import model.Model;

import java.util.*;
import java.util.stream.Collectors;

public class State extends GameID {

//    private int[] playerScores;
    private List<Card> hand;
    private Card[] played; // Cards played this trick
    private boolean isFirstTrick;

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

        return isFirstTrick == other.isFirstTrick &&
                played.length == other.played.length &&
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
        result = 31 * result + (isFirstTrick ? 1 : 0);
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
        return String.format("%s_%s_%s", toStringNoSpaces(hand), toStringNoSpaces(played), Boolean.toString(isFirstTrick));
    }

    private static <T> String toStringNoSpaces(Collection<T> c)
    {
        if (c.size() <= 1) {
            return c.toString();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        int i = 0;
        T last = null;
        for (T e : c) {
            if (i == c.size()-1) {
                last = e;
                break;
            }
            sb.append(e == null ? "null" : e.toString()).append(",");
            ++i;
        }

        sb.append(last == null ? "null" : last.toString()).append("]");
        return sb.toString();
    }

    private static <T> String toStringNoSpaces(T[] a) { return toStringNoSpaces(Arrays.asList(a)); }

    public static GameID fromString(final String str)
    {
        State state = new State();

        final String delim = "_";
        final String[] tokens = str.split(delim);

        state.hand = parseCardCollection(tokens[0]);
        state.played = parseCardArray(tokens[1]);
        state.isFirstTrick = Boolean.parseBoolean(tokens[2]);

        return state;
    }

    private static List<Card> parseCardCollection(final String str)
    {
        final String toParse = str.substring(1, str.length()-1); // Remove [ and ] characters from beginning and end
        final String[] tokens = toParse.split(",");
        return Arrays.stream(tokens).map(Card::fromString).collect(Collectors.toList());
    }

    private static Card[] parseCardArray(final String str)
    {
        List<Card> listForm = parseCardCollection(str);
        Card[] array = new Card[listForm.size()];
        for (int i = 0; i < listForm.size(); ++i) {
            array[i] = listForm.get(i);
        }
        return array;
    }

    public static GameID fromModel(Model m, int playerID)
    {
        State state = new State();

        //state.playerScores = m.getCurrentScoresForThisRound().clone();

        for (int i = 0; i < state.played.length; ++i) {
            state.played[i] = m.getPlayedCard(i);
        }

        state.hand = new ArrayList<>(m.getHand(playerID));
        state.isFirstTrick = m.isFirstTrick();

        return state;
    }

}
