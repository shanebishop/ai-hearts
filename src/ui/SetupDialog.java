package ui;

import game.Hearts;
import game.PlayerType;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class SetupDialog extends Dialog<ButtonType> {

    private static final String humanPlayerStr = "Human player";
    private static final String aiDumbPlayerStr = "AI dumb player";
    private static final String aiCFRPlayerStr = "AI CFR player";
    private static final String aiUCTPlayerStr = "AI UCT player";

    private ChoiceBox<String> p2ChoiceBox, p3ChoiceBox, p4ChoiceBox;

    public SetupDialog()
    {
        setTitle("Hearts Game Setup");

        final String[] choices = {humanPlayerStr, aiDumbPlayerStr, aiCFRPlayerStr, aiUCTPlayerStr};
        p2ChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(choices));
        p3ChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(choices));
        p4ChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(choices));

        // Make each choice box have a selection
        p2ChoiceBox.getSelectionModel().selectFirst();
        p3ChoiceBox.getSelectionModel().selectFirst();
        p4ChoiceBox.getSelectionModel().selectFirst();

        Label p2Label = new Label("Player 2 type");
        Label p3Label = new Label("Player 3 type");
        Label p4Label = new Label("Player 4 type");

        GridPane pane = new GridPane();
        pane.setVgap(5);
        pane.setHgap(5);

        //add(child, col, row)
        pane.add(p2Label, 0, 0);
        pane.add(p2ChoiceBox, 1, 0);
        pane.add(p3Label, 0, 1);
        pane.add(p3ChoiceBox, 1, 1);
        pane.add(p4Label, 0, 2);
        pane.add(p4ChoiceBox, 1, 2);

        getDialogPane().setContent(pane);

        ButtonType okButtonType = ButtonType.OK;
        getDialogPane().getButtonTypes().add(okButtonType);
    }

    public PlayerType[] getPlayerTypes()
    {
        PlayerType[] playerTypes = new PlayerType[Hearts.NUM_PLAYERS];

        playerTypes[0] = PlayerType.HUMAN; // Player 1 is always human
        playerTypes[1] = getChoice(p2ChoiceBox);
        playerTypes[2] = getChoice(p3ChoiceBox);
        playerTypes[3] = getChoice(p4ChoiceBox);

        return playerTypes;
    }

    private PlayerType getChoice(ChoiceBox<String> choiceBox)
    {
        final String choice = choiceBox.getValue();

        switch (choice) {
            case humanPlayerStr:
                return PlayerType.HUMAN;
            case aiDumbPlayerStr:
                return PlayerType.DUMB_AI;
            case aiCFRPlayerStr:
                return PlayerType.CFR_AI;
            case aiUCTPlayerStr:
                return PlayerType.UCT_AI;
            default:
                System.err.printf("Invalid choice %s.\n", choice);
                System.exit(1);
                return null;
        }
    }

}
