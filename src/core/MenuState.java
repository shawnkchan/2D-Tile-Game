package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.TERenderer;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.*;

public class MenuState implements GameState {
    private TETile[][] screen;
    private final int SCREEN_HEIGHT = 40;
    private final int SCREEN_WIDTH = 80;
    private final double TITLE_X = (double) SCREEN_WIDTH / 2;
    private final double TITLE_Y = (double) SCREEN_HEIGHT / 2;
    private final int LARGE_FONT_SIZE = 32;
    private final int SMALL_FONT_SIZE = 16;

    private TERenderer ter;
    private StateMachine stateMachine;


    public MenuState(StateMachine stateMachine) {
        screen = new TETile[SCREEN_WIDTH][SCREEN_HEIGHT];
        ter = new TERenderer();
        this.stateMachine = stateMachine;
    }


    @Override
    public void update(Character pressed) {
        if (pressed.equals('n') || pressed.equals('N')) {
            stateMachine.changeState("seed");
        } else if (pressed.equals('l') || pressed.equals('L')) {
            String seed = FileUtils.readFile("SavedSeed.txt");
            stateMachine.updateAvatarSavedX(Integer.valueOf(FileUtils.readFile("SavedAvatarXTraversal.txt")));
            stateMachine.updateAvatarSavedY(Integer.valueOf(FileUtils.readFile("SavedAvatarYTraversal.txt")));
            stateMachine.updateUserSeed(Long.valueOf(seed));
            stateMachine.changeState("world");
        } else if (pressed.equals('q') || pressed.equals('Q')) {
            System.exit(0);
        }
    }

    @Override
    public void render() {
        ter.drawTiles(screen);
        StdDraw.setPenColor(Color.WHITE);
        Font title = new Font("SansSerif", Font.PLAIN, LARGE_FONT_SIZE);
        Font body = new Font("SansSerif", Font.PLAIN, SMALL_FONT_SIZE);
        StdDraw.setFont(title);
        StdDraw.text(TITLE_X, TITLE_Y, "61B GAME (BETA)");
        StdDraw.setFont(body);
        StdDraw.text(TITLE_X, TITLE_Y - 2, "New Game (N)");
        StdDraw.text(TITLE_X, TITLE_Y - 4, "Load Game (L)");
        StdDraw.text(TITLE_X, TITLE_Y - 6, "Quit (Q)");
        StdDraw.show();
    }

    private void initializeScreen() {
        for (int x = 0; x < SCREEN_WIDTH; x++) {
            for (int y = 0; y < SCREEN_HEIGHT; y++) {
                screen[x][y] = Tileset.NOTHING;
            }
        }
    }

    @Override
    public void enter() {
        ter.initialize(SCREEN_WIDTH, SCREEN_HEIGHT);
        initializeScreen();
    }

    @Override
    public void exit() {

    }
}
