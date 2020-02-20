package model;

public class Card {

    public static int CLUB_SUIT = 0, DIAMOND_SUIT = 1, HEART_SUIT = 2, SPADE_SUITE = 3;
    public static int JACK_VAL = 11, QUEEN_VAL = 12, KING_VAL = 13, ACE_VAL = 14;

    private int suit, value;

    public Card(int s, int v)
    {
        suit = s;
        value = v;
    }

    public Card(Card other)
    {
        suit = other.suit;
        value = other.value;
    }

    public int getSuit() { return suit; }
    public int getValue() { return value; }

}
