package ui;

import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;

public class CardLabel extends Label {

    private static final int WIDTH = 72;
    private static final int HEIGHT = 96;

    private int index; // 0-based index of card in player's hand

    public CardLabel()
    {
        setMinSize(WIDTH, HEIGHT);
        setMaxSize(WIDTH, HEIGHT);
        setPrefSize(WIDTH, HEIGHT);

        setBorderColor(Color.BLACK);
    }

    public void setIndex(int i) { index = i; }
    public int getIndex() { return index; }

    private void setBorderColor(Color c)
    {
        setBorder(new Border(new BorderStroke(c, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
    }

}
