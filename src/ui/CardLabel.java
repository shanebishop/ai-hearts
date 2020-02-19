package ui;

import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;

public class CardLabel extends Label {

    public static final int WIDTH = 72;
    public static final int HEIGHT = 96;

    // Index into this like images[suite][value]
    // Initial nulls is because lowest value is 2 (skip 0 and 1)
    private static final String[][] images = {
            {null, null, "2C.png", "3C.png", "4C.png", "5C.png", "6C.png", "7C.png", "8C.png", "9C.png", "10C.png", "JC.png", "QC.png", "KC.png", "AC.png"},
            {null, "AD.png", "2D.png", "3D.png", "4D.png", "5D.png", "6D.png", "7D.png", "8D.png", "9D.png"},
            {null, "AH.png", "2H.png", "3H.png", "4H.png", "5H.png", "6H.png", "7H.png", "8H.png", "9H.png"},
            {null, "AS.png", "2S.png", "3S.png", "4S.png", "5S.png", "6S.png", "7S.png", "8S.png", "9S.png"},
    };

    private int index; // 0-based index of card in player's hand

    public CardLabel()
    {
        setMinSize(WIDTH, HEIGHT);
        setMaxSize(WIDTH, HEIGHT);
        setPrefSize(WIDTH, HEIGHT);
    }

    public void setIndex(int i) { index = i; }
    public int getIndex() { return index; }

    private void setBorderColor(Color c)
    {
        setBorder(new Border(new BorderStroke(c, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
    }

}
