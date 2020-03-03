package algorithms;

import game.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class UCTAlgorithm<S> {

    private GameInterface<S> m_game;
    private Random rng;
    private int m_playerID;

    public UCTAlgorithm(GameInterface<S> game) {
        m_game = game;
        m_playerID = game.activePlayer();
        rng = new Random();
    }

    private class Node {
        private State state;
        private Node parent;
        private int visits;
        private double reward;
        private List<Node> children;
        private GameInterface<S> n_game;
        private S moveMade;

        public Node(State state, GameInterface<S> g, Node parent, S move) {
            this.state = state;
            this.moveMade = move;
            this.parent = parent;
            this.visits = 0;
            this.n_game = g;
            this.reward = 0; // TODO Should this be a list of doubles, all init'd to 0, instead?
            this.children = new ArrayList<>();
        }

        public int numVisits() { return visits; }
        public double getReward() { return reward; }
        public Node parent() { return parent; }
        public List<Node> getChildren() { return children; }
        public Node getChildAtIndex(int i) { return children.get(i); }
        public GameInterface<S> game() { return n_game; }
        public boolean isGameOver() { return n_game.isGameOver(); }
        public void addChild(Node child) { children.add(child); }
        public S move() { return moveMade; }
        public void incVisits() { ++visits; }
        public void incReward(double inc) { reward += inc; }
        public State state() { return state; }
    }

    // Runs UCT algorithm, and returns move
    public S uct(State state)
    {
        // Create root node
        Node root = new Node(state, m_game.deepCopy(), null, null);

        while (timer.timeRemaining()) {
            // Select a node to expand
            Node selectedNode = treePolicy(root);

            // Determine the reward for this node
            double reward = rollout(selectedNode);

            // Back propagate the reward
            backPropagate(selectedNode, reward);
        }

        // Return best known action from the root
        return bestAction(root).move();
    }

    // Defines the tree policy for MCTS
    // Tree policies select a node to expand
    private Node treePolicy(Node node)
    {
        GameInterface<S> gameCopy = node.game().deepCopy();

        while (!node.isGameOver()) {
            // Find all available moves
            final List<S> availableMoves = gameCopy.moves();

            if (!availableMoves.isEmpty()) {

                // Remove a random item from the set of available moves
                S move = removeRandomItem(availableMoves);

                // Make the move
                gameCopy = node.game().deepCopy();
                gameCopy.makeMove(move);

                // Generate a child node, and add to node's children
                Node child = new Node(gameCopy.getState(), gameCopy, node, move);
                node.addChild(child);

                // Return child
                return child;

            } else {
                node = bestAction(node);
            }
        }

        return node;
    }

    // Remove and return a random item from a list
    private <T> T removeRandomItem(List<T> list)
    {
        return list.remove(rng.nextInt(list.size()));
    }

    // Play game from given node, making random moves, and return
    // reward
    private int rollout(Node node)
    {
        GameInterface<S> gameCopy = node.game();

        while (!gameCopy.isGameOver()) {
            // Find all available moves
            List<S> moves = gameCopy.moves();

            // Choose a random move to make
            S move = removeRandomItem(moves);

            // Make the move
            gameCopy = gameCopy.deepCopy();
            gameCopy.makeMove(move);
        }

        // Determine reward
        if (gameCopy.onlyWinner(m_playerID)) {
            return 1;
        } else if (gameCopy.lost(m_playerID)) {
            return 0;
        }
        return -1;
    }

    // Back propagate rewards back up the tree
    private void backPropagate(Node node, double reward)
    {
        for (; node != null; node = node.parent()) {
            node.incVisits();
            node.incReward(reward);
        }
    }

    // Find the best known action in the tree
    private Node bestAction(Node node)
    {
        List<Node> children = node.getChildren();

//        List<Integer> visits = new ArrayList<>();
//        List<List<Double>> rewards = new ArrayList<>();
//        for (Node child : children) {
//            visits.add(child.numVisits());
//            rewards.add(child.getRewards());
//        }

//        int totalRollouts = node.numVisits();

        // TODO Should this maybe be node.state().playerID() instead?
        int playerID = m_game.activePlayer();

        // Calculate UCB value for all child nodes
        List<Double> ucbValues = new ArrayList<>();
        for (Node child : children) {
            ucbValues.add(ucbValue(child, m_playerID));
        }

        // Return child of node with highest UCB value
        final double max = Collections.max(ucbValues);
        final int indexOfMax = ucbValues.indexOf(max);
        return node.getChildAtIndex(indexOfMax);
    }

    // Computes the UCB value of the given node
    private double ucbValue(Node node, int playerID)
    {
        // Compute winrate
        final double visits = node.numVisits();
        final double winrate = node.getReward() / visits;

        // Compute the second term in the UCB formula
        // Math.log(d) computes ln(d)
        final double secondTerm = Math.sqrt(Math.log(node.parent().numVisits()) / visits);

        // Compute and return UCB value
        return winrate + secondTerm;
    }

}