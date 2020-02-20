package ui;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import model.Card;

public class CardLabel extends Label {

    public static final int WIDTH = 72, HEIGHT = 96;

    private int suit, value; // Cached suit and value to avoid update if not necessary

    private int playerID;

    // Index into this like images[suit][value]
    // Initial nulls is because lowest value is 2 (skip 0 and 1)
    private static final String[][] images = {
            {null, null, "2C.png", "3C.png", "4C.png", "5C.png", "6C.png", "7C.png", "8C.png", "9C.png", "10C.png", "JC.png", "QC.png", "KC.png", "AC.png"},
            {null, null, "2D.png", "3D.png", "4D.png", "5D.png", "6D.png", "7D.png", "8D.png", "9D.png", "10D.png", "JD.png", "QD.png", "KD.png", "AD.png"},
            {null, null, "2H.png", "3H.png", "4H.png", "5H.png", "6H.png", "7H.png", "8H.png", "9H.png", "10H.png", "JH.png", "QH.png", "KH.png", "AH.png"},
            {null, null, "2S.png", "3S.png", "4S.png", "5S.png", "6S.png", "7S.png", "8S.png", "9S.png", "10S.png", "JS.png", "QS.png", "KS.png", "AS.png"},
    };

    private int index; // 0-based index of card in player's hand

    public CardLabel(int pID, int ind)
    {
        playerID = pID;
        index = ind;

        setMinSize(WIDTH, HEIGHT);
        setMaxSize(WIDTH, HEIGHT);
        setPrefSize(WIDTH, HEIGHT);
    }

    public int getIndex() { return index; }

    public void setImage(int s, int v)
    {
        if (s == suit && v == value) {
            return; // Avoid changing anything
        }

        suit = s;
        value = v;

        if (suit == -1) {
            setGraphic(null); // Remove image
            return;
        }

        setGraphic(new ImageView(Images.readImage(images[suit][value])));
    }

    public static void setImage(Label lbl, Card card)
    {
        if (card == null) {
            lbl.setGraphic(null);
            return;
        }
        lbl.setGraphic(new ImageView(Images.readImage(images[card.getSuit()][card.getValue()])));
    }

    private void setBorderColor(Color c)
    {
        setBorder(new Border(new BorderStroke(c, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
    }

}
