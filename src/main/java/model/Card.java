package model;

public class Card implements Comparable<Card> {

    public static final int CLUB_SUIT = 0, DIAMOND_SUIT = 1, HEART_SUIT = 2, SPADE_SUIT = 3;
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

    public boolean isPointsCard()
    {
        return suit == HEART_SUIT || (value == QUEEN_VAL && suit == SPADE_SUIT);
    }

    @Override
    public int hashCode()
    {
        return suit * ACE_VAL + value; // Ace is largest value
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Card)) {
            return false;
        }

        final Card other = (Card) o;
        return suit == other.suit && value == other.value;
    }

    @Override
    public String toString()
    {
        String suitStr;
        switch (suit) {
            case CLUB_SUIT: suitStr = "C"; break;
            case DIAMOND_SUIT: suitStr = "D"; break;
            case HEART_SUIT: suitStr = "H"; break;
            case SPADE_SUIT: suitStr = "S"; break;
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

    public static Card fromString(String str)
    {
        final char valChar = str.charAt(0);
        final char suitChar = str.charAt(str.length()-1);

        int suit;
        switch (suitChar) {
            case 'C': suit = CLUB_SUIT; break;
            case 'D': suit = DIAMOND_SUIT; break;
            case 'H': suit = HEART_SUIT; break;
            case 'S': suit = SPADE_SUIT; break;
            default: return null;
        }

        if (valChar == '1') {
            // This is a ten
            return new Card(suit, 10);
        }

        switch (valChar) {
            case '2': return new Card(suit, 2);
            case '3': return new Card(suit, 3);
            case '4': return new Card(suit, 4);
            case '5': return new Card(suit, 5);
            case '6': return new Card(suit, 6);
            case '7': return new Card(suit, 7);
            case '8': return new Card(suit, 8);
            case '9': return new Card(suit, 9);
            case 'J': return new Card(suit, JACK_VAL);
            case 'Q': return new Card(suit, QUEEN_VAL);
            case 'K': return new Card(suit, KING_VAL);
            case 'A': return new Card(suit, ACE_VAL);
            default: return null;
        }
    }

    @Override
    public int compareTo(Card o) {
        final int ourSum = value + suit;
        final int theirSum = o.value + o.suit;
        return ourSum - theirSum;
    }

}
