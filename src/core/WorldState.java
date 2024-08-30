package core;

import tileengine.TERenderer;
import tileengine.TETile;
import utils.FileUtils;


public class WorldState implements GameState {
    private World world;
    private TERenderer ter;
    private TETile[][] tiles;
    private StateMachine stateMachine;
    private String userKeystrokes = "";
    private final Integer DEFAULT_WIDTH = 80;
    private final Integer DEFAULT_HEIGHT = 40;

    public WorldState(StateMachine stateMachine) {
        ter = new TERenderer();
        this.stateMachine = stateMachine;
    }

    /**
     * This method enables access to the tiles object outside of this class
     * @return  tiles object containing the world
     */
    public TETile[][] returnTiles() {
        return tiles;
    }

    @Override
    public void update(Character pressed) {
        String input = String.valueOf(pressed);
        //update the recorded keystrokes of the user
        userKeystrokes = userKeystrokes + input;
        //Quit Game logic
        if (userKeystrokes.endsWith(":q") || userKeystrokes.endsWith(":Q")) {
            //If we're in a new file, save the seed. Else, skip saving since a saved seed already exists
            if (stateMachine.getUserSeed() != null) {
                stateMachine.updateRecordedInputs(stateMachine.getRecordedInputs() + userKeystrokes);
                FileUtils.writeFile("SavedSeed.txt", String.valueOf(stateMachine.getUserSeed()));
            }
            //save the net distance travelled in the Y and X directions
            FileUtils.writeFile("SavedAvatarXTraversal.txt", String.valueOf(world.getAvatarXTraversal()));
            FileUtils.writeFile("SavedAvatarYTraversal.txt", String.valueOf(world.getAvatarYTraversal()));
            System.exit(0);
        }
        //Move character
        if (pressed.equals('w') || pressed.equals('W')) {
            world.moveAvatar(0, 1);
        } else if (pressed.equals('a') || pressed.equals('A')) {
            world.moveAvatar(-1, 0);
        } else if (pressed.equals('s') || pressed.equals('S')) {
            world.moveAvatar(0, -1);
        } else if (pressed.equals('d') || pressed.equals('D')) {
            world.moveAvatar(1, 0);
        }

        //toggle light
        if (pressed.equals('t') || pressed.equals('T')) {
            if (world.getLightOff()) {
                tiles = world.getWorldArray();
                world.updateLightOff(false);
            } else {
                tiles = world.getCopyArray();
                world.updateLightOff(true);
            }
        }
    }

    @Override
    public void render() {
        //continuously update the alternate world
        world.lightsOut();
        ter.drawTiles(tiles);
        world.renderHud();
    }

    @Override
    public void enter() {
        world = new World(stateMachine.getUserSeed());
        TERenderer tertwo = new TERenderer();
        tertwo.initialize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        tiles = world.getTiles();
        //if we are loading a saved game, update the character positions
        System.out.println(stateMachine.getAvatarSavedX());
        if (!stateMachine.getAvatarSavedX().equals(0)) {
            System.out.println("updating character position");
            world.moveAvatar(stateMachine.getAvatarSavedX(), stateMachine.getAvatarSavedY());
        }
    }

    @Override
    public void exit() {

    }
}
