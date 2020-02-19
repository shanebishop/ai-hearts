package ui;

import game.Hearts;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Model;

public class View extends Application {

    private Hearts game;
    private Model model;

    private BorderPane rootPane;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        game = new Hearts(this);

        rootPane = new BorderPane();
        initRootPane();

        stage.setTitle("Hearts");
        stage.setScene(new Scene(rootPane));
        stage.setResizable(false);
        stage.show();

        game.startGame();

        update();
    }

    public void update()
    {
        // TODO Implement me
    }

    private void initRootPane()
    {
        CardLabel[] p1Cards = new CardLabel[Hearts.CARDS_PER_PLAYER];
        CardLabel[] p2Cards = new CardLabel[Hearts.CARDS_PER_PLAYER];
        CardLabel[] p3Cards = new CardLabel[Hearts.CARDS_PER_PLAYER];
        CardLabel[] p4Cards = new CardLabel[Hearts.CARDS_PER_PLAYER];

        initHorizontalHand(p1Cards, Pos.BOTTOM_CENTER);
        initHorizontalHand(p3Cards, Pos.TOP_CENTER);
        initVerticalHand(p2Cards, Pos.CENTER_LEFT);
        initVerticalHand(p4Cards, Pos.CENTER_RIGHT);
    }

    private void initHorizontalHand(CardLabel[] hand, Pos pos)
    {
        HBox pane = new HBox();
        pane.setPadding(new Insets(10));
        pane.setSpacing(4);

        for (int i = 0; i < hand.length; ++i) {
            hand[i] = new CardLabel();
            hand[i].setGraphic(new ImageView(Images.BACK));
            hand[i].setOnMouseClicked(e -> {
                CardLabel card = (CardLabel) e.getSource();
                System.out.println("Clicked on card " + card);
            });
        }

        pane.getChildren().addAll(hand);

        if (pos == Pos.TOP_CENTER) {
            rootPane.setTop(pane);
        } else {
            rootPane.setBottom(pane);
        }
    }

    private void initVerticalHand(CardLabel[] hand, Pos pos)
    {
        VBox leftPane = new VBox();
        VBox middlePane = new VBox();
        VBox rightPane = new VBox();

        for (int i = 0; i < hand.length; ++i) {
            hand[i] = new CardLabel();
            hand[i].setGraphic(new ImageView(Images.BACK));
            hand[i].setOnMouseClicked(e -> {
                CardLabel card = (CardLabel) e.getSource();
                System.out.println("Clicked on card " + card);
            });
        }

        leftPane.getChildren().addAll(hand[0], hand[1], hand[2], hand[3]);
        middlePane.getChildren().addAll(hand[4], hand[5], hand[6], hand[7], hand[8]);
        rightPane.getChildren().addAll(hand[9], hand[10], hand[11], hand[12]);

        HBox handPane = new HBox();
        handPane.getChildren().addAll(leftPane, middlePane, rightPane);

        if (pos == Pos.CENTER_LEFT) {
            rootPane.setLeft(handPane);
        } else {
            rootPane.setRight(handPane);
        }
    }

}
