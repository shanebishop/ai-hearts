package game;

import model.Model;
import ui.View;

public class Hearts {

    private View view;
    private Model model;

    public static int CARDS_PER_PLAYER = 13;
    public static int NUM_PLAYERS = 4;
    public static int QUEEN_OF_SPADES_SCORE = 13;

    private boolean[] aiPlayers; // true if player i is an AI player, else false

    public Hearts(View v)
    {
        view = v;

        aiPlayers = new boolean[NUM_PLAYERS];
        // TODO Set the indices in aiPlayers to true based on what the user chose in setup dialog

        model = new Model();
        view.setModel(model);
    }

    public void startGame()
    {
        // TODO Implement me
    }

    public void handleCardClicked(int playerID, int index)
    {
        final int activePlayer = model.getActivePlayer();

        if (playerID != activePlayer || !model.cardAtIndex(playerID, index) || isAIPlayer(activePlayer)) {
            return;
        }

        model.setPlayed(playerID, index);

        if (!model.isTrickOver()) {
            // Do not move to next player if the trick is over
            model.nextPlayer();
        }

        view.update();
    }

    // If a human clicks in the center during their turn, nothing happens, because trick is not over
    // If a human clicks in the center during an AI player's turn, nothing happens, because trick is not over
    // If a human clicks in the center after all AI players are done, then we are free to finalize trick,
    // because the trick is over
    // Therefore, the only guard we need for execution is model.isTrickOver()
    public void finalizeTrick()
    {
        if (model.isTrickOver()) {
            boolean roundOver = model.endTrick(); // Ends trick, and sets active player appropriately
            // TODO Handle round over being true - display score dialog, etc.
            // Do we still call view.update() immediately if the round is over?
            // TODO If round is over, and a player has a score >= 100, game is over - need to handle this state as well
            view.update();
        }
    }

    private boolean isAIPlayer(int ind) { return aiPlayers[ind]; }

}
