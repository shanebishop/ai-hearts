package ui;

import game.Hearts;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Card;
import model.Model;

public class View extends Application {

    private Hearts game;
    private Model model;

    private BorderPane rootPane;

    private CardLabel[] p1Cards, p2Cards, p3Cards, p4Cards;
    private Label[] centerCards;

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

    public void setModel(Model m) { model = m; }

    public void update()
    {
        for (int i = 0; i < centerCards.length; ++i) {
            Card c = model.getPlayedCard(i);
            CardLabel.setImage(centerCards[i], c);
        }

        for (int i = 0; i < Hearts.CARDS_PER_PLAYER; ++i) {
            p1Cards[i].setImage(model.getSuit(0, i), model.getValue(0, i));
        }
        for (int i = 0; i < Hearts.CARDS_PER_PLAYER; ++i) {
            p2Cards[i].setImage(model.getSuit(1, i), model.getValue(1, i));
        }
        for (int i = 0; i < Hearts.CARDS_PER_PLAYER; ++i) {
            p3Cards[i].setImage(model.getSuit(2, i), model.getValue(2, i));
        }
        for (int i = 0; i < Hearts.CARDS_PER_PLAYER; ++i) {
            p4Cards[i].setImage(model.getSuit(3, i), model.getValue(3, i));
        }
    }

    private void initRootPane()
    {
        p1Cards = new CardLabel[Hearts.CARDS_PER_PLAYER];
        p2Cards = new CardLabel[Hearts.CARDS_PER_PLAYER];
        p3Cards = new CardLabel[Hearts.CARDS_PER_PLAYER];
        p4Cards = new CardLabel[Hearts.CARDS_PER_PLAYER];

        initHorizontalHand(p1Cards, 1);
        initHorizontalHand(p3Cards, 3);
        initVerticalHand(p2Cards, 2);
        initVerticalHand(p4Cards, 4);

        centerCards = new Label[4];
        initCenterPane();
    }

    private void initCenterPane()
    {
        for (int i = 0; i < centerCards.length; ++i) {
            centerCards[i] = new Label();
            centerCards[i].setMinSize(CardLabel.WIDTH, CardLabel.HEIGHT);
            centerCards[i].setMaxSize(CardLabel.WIDTH, CardLabel.HEIGHT);
            centerCards[i].setPrefSize(CardLabel.WIDTH, CardLabel.HEIGHT);
        }

        HBox centerPane = new HBox();
        centerPane.getChildren().addAll(centerCards);
        rootPane.setCenter(centerPane);
    }

    private void initHorizontalHand(CardLabel[] hand, int playerID)
    {
        HBox pane = new HBox();
        pane.setPadding(new Insets(10));
        pane.setSpacing(4);

        for (int i = 0; i < hand.length; ++i) {
            hand[i] = new CardLabel(playerID, i);
            hand[i].setGraphic(new ImageView(Images.BACK));
            hand[i].setOnMouseClicked(e -> {
                CardLabel card = (CardLabel) e.getSource();
                game.handleCardClicked(playerID-1, card.getIndex());
            });
        }

        pane.getChildren().addAll(hand);

        if (playerID == 3) {
            rootPane.setTop(pane);
        } else {
            rootPane.setBottom(pane);
        }
    }

    private void initVerticalHand(CardLabel[] hand, int playerID)
    {
        VBox leftPane = new VBox();
        VBox middlePane = new VBox();
        VBox rightPane = new VBox();

        for (int i = 0; i < hand.length; ++i) {
            hand[i] = new CardLabel(playerID, i);
            hand[i].setGraphic(new ImageView(Images.BACK));
            hand[i].setOnMouseClicked(e -> {
                CardLabel card = (CardLabel) e.getSource();
                game.handleCardClicked(playerID-1, card.getIndex());
            });
        }

        leftPane.getChildren().addAll(hand[0], hand[1], hand[2], hand[3]);
        middlePane.getChildren().addAll(hand[4], hand[5], hand[6], hand[7], hand[8]);
        rightPane.getChildren().addAll(hand[9], hand[10], hand[11], hand[12]);

        HBox handPane = new HBox();
        handPane.getChildren().addAll(leftPane, middlePane, rightPane);

        if (playerID == 2) {
            rootPane.setLeft(handPane);
        } else {
            rootPane.setRight(handPane);
        }
    }

}
