package algorithms;

import game.State;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;

public class CounterfactualRegretMinimizer<S> {

    private GameInterface<S> m_game;
    private int numPlayers;
    private List<Map<GameID, List<Double>>> aggregateRegrets;
    private List<Map<GameID, List<Double>>> aggregateStrategies;

    private int trainDepth, maxTrainDepth;

    public CounterfactualRegretMinimizer(GameInterface<S> g, int maxTrainDepth)
    {
        m_game = g;
        this.maxTrainDepth = maxTrainDepth;
        trainDepth = 0;
        numPlayers = m_game.numPlayers();

        aggregateRegrets = new ArrayList<>();
        aggregateStrategies = new ArrayList<>();
        initAggregateList(aggregateRegrets);
        initAggregateList(aggregateStrategies);
    }

    private void initAggregateList(List<Map<GameID, List<Double>>> list)
    {
        for (int i = 0; i < numPlayers; ++i) {
            list.add(new HashMap<>());
        }
    }

    public void solve(String outputFilename, int itersPerSave, int itersPerUpdate, int maxIterations)
    {
        // Initialize training counters
        int saveCounter = 0, totalIterations = 0;
        System.out.println("Beginning training.");

        while (totalIterations < maxIterations) {
            // Train the model
            train(itersPerUpdate);

            saveCounter += itersPerUpdate;
            totalIterations += itersPerUpdate;
            System.out.printf("Completed iteration %d.\n", totalIterations);

            // Save a checkpoint
            if (saveCounter >= itersPerSave) {
                saveCounter = saveCounter % itersPerSave;
                System.out.println("Saving...");
                save(outputFilename);
            }
        }

        System.out.println("Reached mex iterations. Will now stop training.");
    }

    private void train(int numIterations)
    {
        // TODO Uncomment
        //for (int i = 0; i < numIterations; ++i) {
            GameInterface<S> copy = m_game.deepCopy();
            copy.beginGame();

            List<Double> probabilities = newList(numPlayers, 1.0);

            train(copy, probabilities);
        //}
    }

    private List<Double> train(final GameInterface<S> game, final List<Double> probabilities)
    {
        // Check if game has ended or rounds has exceeded 14
        // TODO Maybe I shouldn't be forcing ending at 14
        if (game.isGameOver() || game.roundNumber() > 2) {
            //System.out.printf("Scores: %s\n", Arrays.toString(game.scores()));
            //System.out.println("A game ended");
            assert(game.payout().size() >= 4);
            //System.out.println("payout: "+game.payout());
            return game.payout();
        }

        if (trainDepth++ >= maxTrainDepth) {
            return game.payout();
        }
        System.out.println(trainDepth);

        // TODO Temp
        //System.out.printf("\nRound %d.\n", game.roundNumber());
        if (game.roundNumber() > 14) {
            System.err.println("Round number exceeded 13");
            System.err.printf("Scores: %s\n", Arrays.toString(game.scores()));
            System.exit(1);
        }

        final int activePlayer = game.activePlayer();
        final GameID id = game.getID(activePlayer);

        if (!aggregateStrategies.get(activePlayer).containsKey(id)) {
            aggregateStrategies.get(activePlayer).put(id, new ArrayList<>());
        }

        final List<S> moves = game.moves();
        //System.out.println(moves.size());

        // Determine player's strategy
        final List<Double> strategy = getStrategy(activePlayer, id, moves);
        List<List<Double>> actionUtilities = new ArrayList<>();
        List<Double> nodeUtilies = newList(numPlayers, 0.0);
        assert(nodeUtilies.size() >= 4);

        // Recursively train on each action
        for (int move = 0; move < moves.size(); ++move) {
            // Branch on player's action
            GameInterface<S> gameCopy = game.deepCopy();
            gameCopy.makeMove(moves.get(move));
            List<Double> probabilitiesCopy = new ArrayList<>(probabilities);
            multInPlace(probabilitiesCopy, activePlayer, strategy.get(move));

            // Update utilities
            List<Double> trainResults = train(gameCopy, probabilitiesCopy);
            //System.out.println("trainResults: "+trainResults);
            assert(trainResults.size() >= 4);
            //actionUtilities.add(train(gameCopy, probabilitiesCopy));
            actionUtilities.add(trainResults);
            for (int agent = 0; agent < numPlayers; ++agent) {
                //System.out.println("actionUtilities.get(move): "+actionUtilities.get(move));
                final double toAdd = strategy.get(move) * actionUtilities.get(move).get(agent);
                addInPlace(nodeUtilies, agent, toAdd);
            }
        }

        // Make sure we have all zeroes in aggregateRegrets and aggregateStrategies
        setToZeroes(aggregateRegrets.get(activePlayer).get(id), moves.size());
        setToZeroes(aggregateStrategies.get(activePlayer).get(id), moves.size());

        // Accumulate counterfactual regret
        for (int move = 0; move < moves.size(); ++move) {
            double counterfactual = 1.0;

            // Calculate counterfactual probability
            for (int agent = 0; agent < numPlayers; ++agent) {
                if (agent != activePlayer) {
                    counterfactual *= probabilities.get(agent);
                }
            }

            // Update regrets
            final double regret = actionUtilities.get(move).get(activePlayer) -
                    nodeUtilies.get(activePlayer);
            addInPlace(aggregateRegrets.get(activePlayer).get(id), move,
                    counterfactual * regret);
            addInPlace(aggregateStrategies.get(activePlayer).get(id), move,
                    counterfactual * strategy.get(move));
        }

        //System.out.println("nodeUtilities: "+nodeUtilies);
        //System.out.flush();
        return nodeUtilies;
    }

    private void setToZeroes(List<Double> list, int n) {
        if (list.isEmpty()) {
            list.addAll(Collections.nCopies(n, 0.0));
        }
    }

    private void save(String filename)
    {
        try {
            FileWriter writer = new FileWriter(filename, true); // true means append to file
            PrintWriter out = new PrintWriter(new BufferedWriter(writer));
            save(out);
        } catch (IOException e) {
            System.err.printf("Failed to save to file '%s': %s.\n", filename, e.getMessage());
        }
    }

    private void save(PrintWriter out)
    {
        // Write probabilities to file
        out.println("Probabilities");
//        for (int player = 0; player < numPlayers; ++player) {
//            Map<String, List<Double>> map = aggregateStrategies.get(player);
//            out.printf("Player: %d\n", player);
//
//            for (String id : map.keySet()) {
//                final List<Double> strat = map.get(id);
//                final double total = sum(strat);
//
//                out.printf("%s\t", id);
//                for (double value : strat) {
//                    out.printf(" %f", value / total);
//                }
//                out.println();
//            }
//            out.println("END");
//        }
        saveData(out, aggregateStrategies, true);

        // Write raw data for aggregate strategies to file
        out.println("Strategies");
        saveData(out, aggregateStrategies, false);

        out.println("Regrets");
        saveData(out, aggregateRegrets, false);

        out.close();
    }

    private void saveData(PrintWriter out, List<Map<GameID, List<Double>>> aggregate, boolean divByTotal)
    {
        for (int player = 0; player < numPlayers; ++player) {
            Map<GameID, List<Double>> map = aggregate.get(player);
            out.printf("Player: %d\n", player);

            for (GameID id : map.keySet()) {
                final List<Double> strat = map.get(id);
                final double total = sum(strat);

                out.printf("%s\t", id);
                for (double value : strat) {
                    if (divByTotal) {
                        out.printf(" %f", value / total);
                    } else {
                        out.printf(" %f", value);
                    }
                }
                out.println();
            }
            out.println("END");
        }
    }

//    private double lambda(PrintWriter out, List<Double> list, String id, boolean divByTotal) {
//        final double total = sum(list);
//
//        out.printf("%s\t", id);
//        for (double value : list) {
//            if (divByTotal) {
//                out.printf(" %f", value / total);
//            } else {
//                out.printf(" %f", value);
//            }
//        }
//        out.println();
//    }

    public boolean load(String filename)
    {
        boolean succeeded = false;

        try {
            List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
            succeeded = load(lines);
            if (!succeeded) {
                System.err.printf("Could not parse file '%s'.\n", filename);
            }
        } catch (NoSuchFileException e) {
            System.err.printf("Failed to read from file '%s' because it does not exist.\n", filename);
        } catch (IOException e) {
            System.err.printf("Failed to read from file '%s': %s.\n", filename, e.getMessage());
        }

        return succeeded;
    }

    private boolean load(List<String> lines)
    {
        String buffer = lines.remove(0);
        if (!buffer.equalsIgnoreCase("probabilities")) {
            return false;
        }

        // Read probabilities
        for (int player = 0; player < numPlayers; ++player) {
            lines.remove(0); // Discard line
            buffer = lines.remove(0);
            while (!buffer.equalsIgnoreCase("end")) {
                buffer = lines.remove(0);
            }
        }

        // Read raw data for aggregate strategies
        buffer = lines.remove(0);
        if (!buffer.equalsIgnoreCase("strategies")) {
            return false;
        }

        aggregateStrategies = new ArrayList<>();
        readData(lines, aggregateStrategies);

        buffer = lines.remove(0);
        if (!buffer.equalsIgnoreCase("regrets")) {
            return false;
        }

        aggregateRegrets = new ArrayList<>();
        readData(lines, aggregateRegrets);

        return true;
    }

    private void readData(List<String> lines, List<Map<GameID, List<Double>>> aggregates)
    {
        String buffer;

        for (int player = 0; player < numPlayers; ++player) {
            aggregates.add(new HashMap<>());
            lines.remove(0); // Discard line
            buffer = lines.remove(0);

            while (!buffer.equalsIgnoreCase("end")) {
                final int delim_index = buffer.indexOf('\t');
                final String key = buffer.substring(0, delim_index);
                final String[] tokens = key.split("\t");
                List<Double> value = new ArrayList<>();

                for (String token : tokens) {
                    value.add(Double.parseDouble(token));
                }

                aggregates.get(player).put(State.fromString(key), value);
                buffer = lines.remove(0);
            }
        }
    }

    private List<Double> getStrategy(int playerID, GameID infoSetID, List<S> moves)
    {
        if (!aggregateRegrets.get(playerID).containsKey(infoSetID)) {
            // If no historical data for this information set, make a new entry for this information set
            aggregateRegrets.get(playerID).put(infoSetID, new ArrayList<>());
        }

        // Retrieve historical data for this information set
        final List<Double> cumulativeRegrets = aggregateRegrets.get(playerID).get(infoSetID);

        List<Double> strategy = new ArrayList<>();
        double normalizingSum = 0;

        // Choose actions with probability in proportion to their regret
        for (int move = 0; move < moves.size(); ++move) {
            final double regret = Math.max(getOrZero(cumulativeRegrets, move), 0);
            strategy.add(regret);
            normalizingSum += regret;
        }

        // Normalize the strategy into a probability distribution
        for (int move = 0; move < moves.size(); ++move) {
            if (normalizingSum > 0) {
                strategy.set(move, strategy.get(move) / normalizingSum);
            } else {
                strategy.set(move, 1.0 / moves.size());
            }
        }

        return strategy;
    }

    private double getOrZero(List<Double> list, int index)
    {
        return index < list.size() ? list.get(index) : 0;
    }

    private static double sum(List<Double> toSum)
    {
        return toSum.stream().reduce(0.0, Double::sum);
    }

    /**
     * Generates a new list of size numVals where all values are the given value
     *
     * @param val The value for all elements to be
     * @param numVals The number of values
     * @return The new list
     */
    private static <T> List<T> newList(int numVals, T val)
    {
        List<T> ret = new ArrayList<>();
        for (int i = 0; i < numVals; ++i) {
            ret.add(val);
        }
        return ret;
    }

    private static void multInPlace(List<Double> list, int ind, double multiplicand)
    {
        final double val = list.get(ind) * multiplicand;
        list.set(ind, val);
    }

    private static void addInPlace(List<Double> list, int ind, double addend)
    {
        // Make sure there are zeroes in whatever might be missing from this list's size to index into it
        // TODO Refactor so that we only add once
        while (ind >= list.size()) {
            list.add(0.0);
        }

        final double val = list.get(ind) + addend;
        list.set(ind, val);
    }

}
