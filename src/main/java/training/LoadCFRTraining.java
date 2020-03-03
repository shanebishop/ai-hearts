package training;

import algorithms.CounterfactualRegretMinimizer;
import game.Hearts;
import model.Card;

public class LoadCFRTraining {

    public static void main(String[] args)
    {
        CounterfactualRegretMinimizer<Card> minimizer = new CounterfactualRegretMinimizer<>(Hearts.NUM_PLAYERS);

        boolean loaded = minimizer.load("cfrtraining.txt");
        if (loaded) {
            System.out.println("Loaded succesfully");
        } else {
            System.err.println("Failed to load");
            System.exit(1);
        }
    }

}
