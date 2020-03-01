package training;

import algorithms.CounterfactualRegretMinimizer;
import game.Hearts;
import model.Card;

public class TrainCFR {

    public static void main(String[] args)
    {
        Hearts game = new Hearts();
        //CounterfactualRegretMinimizer<HeartsMove> trainer = new CounterfactualRegretMinimizer<>(game);
        CounterfactualRegretMinimizer<Card> trainer = new CounterfactualRegretMinimizer<Card>(game);
        trainer.solve("cfrtraining.txt", 1000, 1000, 10000);
    }

}
