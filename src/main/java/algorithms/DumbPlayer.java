package algorithms;

import model.Card;

import java.util.List;

public class DumbPlayer {

    // Prevent creating instances of this class
    private DumbPlayer() {}

    public static Card chooseCard(List<Card> hand, Card cardLed, boolean firstTrick, boolean heartsBroken)
    {
        if (hand.size() == 1) {
            return hand.get(0);
        }

        final boolean leading = cardLed == null;

        if (leading) {
            if (heartsBroken) {
                // We can lead anything
                return hand.get(0);
            }

            // Return first non-heart card, if it exists
            for (Card c : hand) {
                if (firstTrick && isQueenOfSpades(c)) {
                    continue;
                }
                if (c.getSuit() != Card.HEART_SUIT) {
                    return c;
                }
            }

            // No non-heart cards
            return hand.get(0);
        }

        // Not leading

        final int suitLed = cardLed.getSuit();

        // Return first card that matches suit led
        for (Card c : hand) {
            if (firstTrick && isQueenOfSpades(c)) {
                continue;
            }
            if (c.getSuit() == suitLed) {
                return c;
            }
        }

        // Could not follow suit
        // Return first non-heart card, if it exists
        for (Card c : hand) {
            if (firstTrick && isQueenOfSpades(c)) {
                continue;
            }
            if (c.getSuit() != Card.HEART_SUIT) {
                return c;
            }
        }

        // No non-heart cards
        return hand.get(0);
    }

    private static boolean isQueenOfSpades(Card c)
    {
        return c.getSuit() == Card.SPADE_SUITE && c.getValue() == Card.QUEEN_VAL;
    }

}
