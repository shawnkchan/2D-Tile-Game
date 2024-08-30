package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;

public class SeedState implements GameState {
    private TETile[][] screen;
    private final int SCREEN_HEIGHT = 40;
    private final int SCREEN_WIDTH = 80;
    private final double TITLE_X = (double) SCREEN_WIDTH / 2;
    private final double TITLE_Y = (double) SCREEN_HEIGHT / 2;
    private String userInput = "";
    private TERenderer ter;
    private StateMachine stateMachine;
    private boolean error;

    public SeedState(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
        screen = new TETile[SCREEN_WIDTH][SCREEN_HEIGHT];
        ter = new TERenderer();
        error = false;
    }

    /**
     * This method updates the user input string and displays it on the screen.
     * @param pressed   Character that user enters
     */
    @Override
    public void update(Character pressed) {
        String input = String.valueOf(pressed);
        userInput = userInput + input;
        if (pressed.equals('s') || pressed.equals('S')) {
            String userInputDigits = userInput.replaceAll("[^\\d]", "");
            if (userInputDigits.equals("")) {
                error = true;
                userInput = "";
            } else {
                stateMachine.updateUserSeed(Long.valueOf(userInputDigits));
                stateMachine.changeState("world");
            }
        }
    }

    private void renderError() {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(TITLE_X, TITLE_Y - 4, "Seed is invalid. Please enter a number");
    }
    /**
     * This method renders the screen object and displays the prompt for a seed
     */
    @Override
    public void render() {
        ter.drawTiles(screen);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(TITLE_X, TITLE_Y, "Enter Seed and press 's' when finished: ");
        StdDraw.text(TITLE_X, TITLE_Y - 2, userInput);
        if (error) {
            renderError();
        }
        StdDraw.show();
    }

    /**
     * This method fills the screen object with blank tiles for the renderer
     */
    private void initializeScreen() {
        for (int x = 0; x < SCREEN_WIDTH; x++) {
            for (int y = 0; y < SCREEN_HEIGHT; y++) {
                screen[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * This method initializes the ter renderer object
     */
    @Override
    public void enter() {
        ter.initialize(SCREEN_WIDTH, SCREEN_HEIGHT);
        initializeScreen();
    }

    @Override
    public void exit() {
        return;
    }
}
