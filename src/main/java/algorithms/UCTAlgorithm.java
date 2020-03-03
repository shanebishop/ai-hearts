package algorithms;

public class UCTAlgorithm {

    private static class Node {
        private State state;
        private Node parent;

        public Node(State state, Node parent) {
            this.state = state;
            this.parent = parent;
        }
    }

    public static int uct(Node node, int depth)
    {
        for (timer.timeRemaining()) {
            position = root;
            while (position.isExplored()) {
                int val = Integer.MIN_VALUE;
                for (Type child : position.getChildren()) {
                    // Unexplored node check here
                    val = Math.max(val, uctValue(child));
                    position = val.getNode();
                }
            }

            play random game(s) at child;

            while (position is not root) {
                update win rate for player at node;
                position = position.parent();
            }
        }
    }

    private static int uctValue(child)
    {
        final int winrate = wins / totalPlays;
        // Math.log(d) computes ln(d)
        final int secondTerm = Math.sqrt(Math.log(parent.numVisists()) / visits));
        return winrate + secondTerm;
    }

}
