package model;

public class Card {

    public static final int CLUB_SUIT = 0, DIAMOND_SUIT = 1, HEART_SUIT = 2, SPADE_SUITE = 3;
    public static final int JACK_VAL = 11, QUEEN_VAL = 12, KING_VAL = 13, ACE_VAL = 14;

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

    @Override
    public String toString()
    {
        String suitStr;
        switch (suit) {
            case CLUB_SUIT: suitStr = "C"; break;
            case DIAMOND_SUIT: suitStr = "D"; break;
            case HEART_SUIT: suitStr = "H"; break;
            case SPADE_SUITE: suitStr = "S"; break;
            default: suitStr = null; break;
        }

        String valStr;
        if (value < JACK_VAL) {
            valStr = Integer.toString(value);
        } else {
            switch (value) {
                case JACK_VAL: valStr = "J"; break;
                case QUEEN_VAL: valStr = "Q"; break;
                case KING_VAL: valStr = "K"; break;
                case ACE_VAL: valStr = "A"; break;
                default: valStr = null; break;
            }
        }

        return String.format("%s%s", valStr, suitStr);
    }

}
