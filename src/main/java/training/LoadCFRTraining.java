package training;

import algorithms.CounterfactualRegretMinimizer;
import game.Hearts;
import game.PlayerType;
import model.Card;

public class LoadCFRTraining {

    public static void main(String[] args)
    {
        Hearts game = new Hearts();
        game.setAllPlayerTypes(PlayerType.CFR_AI);
        game.setTraining(true);

        final int maxTrainDepth = 100;

        CounterfactualRegretMinimizer<Card> minimizer = new CounterfactualRegretMinimizer<>(game, maxTrainDepth);

        boolean loaded = minimizer.load("cfrtraining.txt");
        if (loaded) {
            System.out.println("Loaded succesfully");
        } else {
            System.err.println("Failed to load");
            System.exit(1);
        }
    }

}
