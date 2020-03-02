package training;

import algorithms.CounterfactualRegretMinimizer;
import game.Hearts;
import game.PlayerType;
import model.Card;

public class TrainCFR {

    public static void main(String[] args)
    {
        Hearts game = new Hearts();
        game.setAllPlayerTypes(PlayerType.CFR_AI);
        game.setTraining(true);

        final int maxTrainDepth = 100;

        //CounterfactualRegretMinimizer<HeartsMove> trainer = new CounterfactualRegretMinimizer<>(game);
        CounterfactualRegretMinimizer<Card> trainer = new CounterfactualRegretMinimizer<>(game, maxTrainDepth);
        //trainer.solve("cfrtraining.txt", 1000, 1000, 10000);
        trainer.solve("cfrtraining.txt", 1, 1, 1);
    }

}
