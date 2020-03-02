package ui;

import game.Hearts;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import model.Model;

import java.util.HashMap;
import java.util.Map;

public class ScoreDialog extends Dialog<ButtonType> {

    public ScoreDialog(Model model)
    {
        setTitle("Scores");

        if (model.getRoundNum() < 2) {
            // No rounds have finished yet
            initEmptyDialog();
        } else {
            initRegularDialog(model.getRoundScores());
        }
        //showTestScoresDialog(); // Uncomment to test scores dialog

        getDialogPane().getButtonTypes().add(ButtonType.OK);
    }

    private void initEmptyDialog()
    {
        getDialogPane().setContentText("No rounds have been completed yet.");
    }

    private void initRegularDialog(Map<Integer, int[]> scores)
    {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);

        // Add column headers
        for (int i = 1; i <= Hearts.NUM_PLAYERS; ++i) {
            //add(child, col, row)
            pane.add(new Label("Player"+i), i, 0);
        }

        int[] totals = new int[Hearts.NUM_PLAYERS]; // Array entries default to 0 in Java

        // Display scores by round
        for (int roundNum : scores.keySet()) {
            final int[] roundScores = scores.get(roundNum);
            pane.add(new Label("Round "+roundNum), 0, roundNum);

            for (int i = 0; i < roundScores.length; ++i) {
                final int score = roundScores[i];
                totals[i] += score;

                pane.add(new Label(Integer.toString(score)), i+1, roundNum);
            }
        }

        final int totalRow = scores.size()+1;
        pane.add(new Label("Total"), 0, totalRow);

        // Display score totals
        for (int i = 0; i < totals.length; ++i) {
            pane.add(new Label(Integer.toString(totals[i])), i+1, totalRow);
        }

        getDialogPane().setContent(pane);
    }

    // This is just to easily test the scores dialog without playing an entire round
    private void showTestScoresDialog()
    {
        // Init a test scores thing
        Map<Integer, int[]> exampleScores = new HashMap<>();
        final int[] round1 = {3, 4, 5, 6};
        exampleScores.put(1, round1);
        final int[] round2 = {26, 0, 0, 0};
        exampleScores.put(2, round2);

        // Init the dialog with these scores
        initRegularDialog(exampleScores);
    }

}
