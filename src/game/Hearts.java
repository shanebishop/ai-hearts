package game;

import model.Model;
import ui.View;

public class Hearts {

    private View view;
    private Model model;

    public static int CARDS_PER_PLAYER = 13;

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

}
