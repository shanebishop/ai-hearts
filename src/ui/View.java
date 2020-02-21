package ui;

import game.Hearts;
import game.PlayerType;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Card;
import model.Model;

import java.util.Optional;

public class View extends Application {

    private Hearts game;
    private Model model;

    private BorderPane rootPane;

    private CardLabel[] p1Cards, p2Cards, p3Cards, p4Cards;
    private Label[] centerCards;
    private Label turnLabel;

    private PlayerType[] playerTypes;

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

        showSetupDialog();

        stage.setTitle("Hearts");
        stage.setScene(new Scene(rootPane));
        stage.setResizable(false);
        stage.show();

        game.startGame();

        update();
    }

    public PlayerType[] getPlayerTypes() { return playerTypes; }

    public void setModel(Model m) { model = m; }

    public void update()
    {
        turnLabel.setText(String.format("Player %d's turn", model.getActivePlayer()+1));

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

    public void showScoreDialog()
    {
        Dialog dialog = new ScoreDialog(model);
        dialog.showAndWait();
    }

    private void showSetupDialog()
    {
        SetupDialog setupDialog = new SetupDialog();
        Optional<ButtonType> result = setupDialog.showAndWait();

        if (!result.isPresent()) {
            // User closed dialog without pressing OK
            System.out.println("Terminating.");
            System.exit(0);
        }

        playerTypes = setupDialog.getPlayerTypes();

        System.out.printf("Player types: %s, %s, %s, %s\n", playerTypes[0], playerTypes[1], playerTypes[2], playerTypes[3]);
    }

    private void initRootPane()
    {
        p1Cards = new CardLabel[Hearts.CARDS_PER_PLAYER];
        p2Cards = new CardLabel[Hearts.CARDS_PER_PLAYER];
        p3Cards = new CardLabel[Hearts.CARDS_PER_PLAYER];
        p4Cards = new CardLabel[Hearts.CARDS_PER_PLAYER];

        Pane p1Pane = initHorizontalHand(p1Cards, 1);
        Pane p3Pane = initHorizontalHand(p3Cards, 3);
        Pane p2Pane = initVerticalHand(p2Cards, 2);
        Pane p4Pane = initVerticalHand(p4Cards, 4);

        VBox topPane = new VBox();
        MenuBar menuBar = genMenu();
        topPane.getChildren().addAll(menuBar, p3Pane);

        rootPane.setBottom(p1Pane);
        rootPane.setLeft(p2Pane);
        rootPane.setTop(topPane);
        rootPane.setRight(p4Pane);

        centerCards = new Label[4];
        initCenterPane();
    }

    private MenuBar genMenu()
    {
        MenuItem item = new MenuItem("Show scores for previous rounds");
        item.setOnAction(e -> showScoreDialog());

        final Menu menu = new Menu("Show Scores");
        menu.getItems().add(item);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);

        return menuBar;
    }

    private void initCenterPane()
    {
        for (int i = 0; i < centerCards.length; ++i) {
            centerCards[i] = new Label();
            centerCards[i].setMinSize(CardLabel.WIDTH, CardLabel.HEIGHT);
            centerCards[i].setMaxSize(CardLabel.WIDTH, CardLabel.HEIGHT);
            centerCards[i].setPrefSize(CardLabel.WIDTH, CardLabel.HEIGHT);
        }

        HBox playedCardsPane = new HBox();
        playedCardsPane.getChildren().addAll(centerCards);

        turnLabel = new Label();

        StackPane centerPane = new StackPane();
        centerPane.setOnMouseClicked(e -> game.finalizeTrick());

        final int width = 550, height = 460;
        centerPane.setMinSize(width, height);
        centerPane.setMaxSize(width, height);
        centerPane.setPrefSize(width, height);

        centerPane.getChildren().addAll(playedCardsPane, turnLabel);
        playedCardsPane.setAlignment(Pos.CENTER); // StackPane.setAlignment() didn't work, but this does
        StackPane.setAlignment(turnLabel, Pos.BOTTOM_CENTER);

        rootPane.setCenter(centerPane);
    }

    private Pane initHorizontalHand(CardLabel[] hand, int playerID)
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
        return pane;
    }

    private Pane initVerticalHand(CardLabel[] hand, int playerID)
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
        return handPane;
    }

}
