package game;

import model.Model;
import ui.View;

public class Hearts {

    private View view;
    private Model model;

    public static int CARDS_PER_PLAYER = 13;
    public static int NUM_PLAYERS = 4;
    public static int QUEEN_OF_SPADES_SCORE = 13;

    public Hearts(View v)
    {
        view = v;
        model = new Model();
        view.setModel(model);
    }

    public void startGame()
    {
        // TODO Implement me
    }

    public void handleCardClicked(int playerID, int index)
    {
        if (playerID != model.getActivePlayer() || !model.cardAtIndex(playerID, index)) {
            return;
        }

        model.setPlayed(playerID, index);

        if (!model.isTrickOver()) {
            // Do not move to next player if the trick is over
            model.nextPlayer();
        }

        view.update();
    }

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

}
