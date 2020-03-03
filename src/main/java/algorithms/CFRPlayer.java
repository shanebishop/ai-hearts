package algorithms;

import game.Hearts;
import game.State;
import model.Card;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class CFRPlayer {

    private List<Map<GameID, List<Double>>> strategies;
    private Hearts game;
    private Random rng;

    public CFRPlayer(Hearts game)
    {
        this.game = game;
        this.rng = new Random();

        CounterfactualRegretMinimizer<Card> minimizer = new CounterfactualRegretMinimizer<>(Hearts.NUM_PLAYERS);

        boolean loaded = minimizer.load("cfrtraining.txt");
        if (!loaded) {
            System.err.println("Failed to load CFR training data.");
            System.exit(1);
        }

        strategies = minimizer.getStrategies();
    }

    // TODO CFR agent should likely take into account whether hearts has been broken or not
    // Should probably only do this if I am able to do enough training
    public Card chooseCard(List<Card> hand, boolean isFirstTrick,
                                  boolean heartsBroken, Card[] cardsPlayed, int activePlayer)
    {
        State state = new State(hand, cardsPlayed, isFirstTrick);
        final List<Double> relevantStrategies = strategies.get(activePlayer).get(state);

        // Get all valid moves for this player
        List<Card> validMoves = game.moves();

        if (relevantStrategies == null) {
            // Return random card in validMoves (use uniform random distribution)
            return validMoves.get(rng.nextInt(validMoves.size()));
        }

        // Generate random double
        final double r = rng.nextDouble();

        // Move up until we found the bucket for the probability
        double sum = 0.0;
        int index = 0;
        while (sum < r) {
            sum += relevantStrategies.get(index);
            ++index;
        }

        // Return card at this index
        return validMoves.get(index);
    }

}
