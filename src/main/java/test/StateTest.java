package test;

import game.State;
import model.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StateTest {

    @Test
    public void testToAndFromString() {
        final int playerID = 0;
        Model model = new Model();
        State initialState = State.fromModel(model, playerID);

        final String result = initialState.toString();
        State newState = State.fromString(result);

        assertEquals(newState, initialState);
    }

}
