package model;

public class Card {

    private int suit, value;

    public Card(int s, int v)
    {
        suit = s;
        value = v;
    }

    public int getSuit() { return suit; }
    public int getValue() { return value; }

}
