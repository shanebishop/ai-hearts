package model;

import game.Hearts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Model {

    private List<List<Card>> hands;
    private List<Card> p1Hand, p2Hand, p3Hand, p4Hand;

    public Model()
    {
        dealCards();
    }

    public List<Card> getP1Hand() { return p1Hand; }
    public List<Card> getP2Hand() { return p2Hand; }
    public List<Card> getP3Hand() { return p3Hand; }
    public List<Card> getP4Hand() { return p4Hand; }

    public int getP1HandSize() { return p1Hand.size(); }
    public int getP2HandSize() { return p2Hand.size(); }
    public int getP3HandSize() { return p3Hand.size(); }
    public int getP4HandSize() { return p4Hand.size(); }

    private void dealCards()
    {
        // Reset hands
        p1Hand = new ArrayList<>(Hearts.CARDS_PER_PLAYER);
        p2Hand = new ArrayList<>(Hearts.CARDS_PER_PLAYER);
        p3Hand = new ArrayList<>(Hearts.CARDS_PER_PLAYER);
        p4Hand = new ArrayList<>(Hearts.CARDS_PER_PLAYER);
        hands = Arrays.asList(p1Hand, p2Hand, p3Hand, p4Hand);

        final int numCards = 4*Hearts.CARDS_PER_PLAYER;
        final int numPlayers = 4;

        List<Card> allCards = new ArrayList<>(numCards);
        for (int val = 1; val <= Hearts.CARDS_PER_PLAYER; ++val) {
            for (int s = 1; s <= numPlayers; ++s) {
                allCards.add(new Card(s, val));
            }
        }
        Collections.shuffle(allCards);

        for (int i = 0; i < Hearts.CARDS_PER_PLAYER; ++i) {
            for (int j = 0; j < numPlayers; ++j) {
                hands.get(j).add(allCards.get(i));
            }
        }
    }

}
