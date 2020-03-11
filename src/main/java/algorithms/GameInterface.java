package algorithms;

import game.State;

import java.util.List;

/**
 * Interface for abstract multiplayer games
 *
 * @param <S> The type of actions
 */
// TODO Would it be better for the Model or the Game to inherit from this class?
public interface GameInterface<S> {

    /**
     * Starts a new game
     */
    void beginGame();

    /**
     * Make the active player make a move
     *
     * @param move The move
     * @return true if round is over, else false
     */
    boolean makeMove(S move);

    /**
     * Computes payout of a completed game
     *
     * @return List where i-th value is payout for i-th player
     */
    List<Double> payout();

    /**
     * Returns a deep copy of the game
     *
     * @return A deep copy
     */
    GameInterface<S> deepCopy();

    /**
     * Returns the game's unique ID
     *
     * @param playerID ID of player
     * @return Game's ID
     */
    GameID getID(int playerID);

    /**
     * Checks if game is over
     *
     * @return true if game is over, else false
     */
    boolean isGameOver();

    /**
     * Returns number of players
     *
     * @return Number of players
     */
    int numPlayers();

    /**
     * Return 0-based index indicating who's turn it is
     *
     * @return Index of active player
     */
    int activePlayer();

    /**
     * Returns a list of all possible moves the active
     * player can make. The order of moves returned must be
     * deterministic.
     *
     * @return List of possible moves for active player
     */
    List<S> moves();

    /**
     * Returns a state representing the current game state
     * from the perspective of the current player
     *
     * @return Current game state
     */
    State getState();

    /**
     * @return List of indices of winning players
     */
    List<Integer> winningPlayers();

    /**
     * Move game to next round
     */
    //void nextRound();

    // ------------------------------------------------
    // All methods below are for debugging only

    int roundNumber();

    int[] scores();

}
