package core;
import java.awt.*;
import java.util.HashMap;

/**
 * Class that manages the different states of the game
 */
public class StateMachine {
    private HashMap<String, GameState> gameStates;
    private String current = "menu";
    /**
     * userSeed stores the user's input seed
     */
    private Long userSeed;
    private String recordedInputs;
    private Integer avatarSavedX;
    private Integer avatarSavedY;

    /**
     * All possible states are initialized in the stateMachine in the constructor
     */
    public StateMachine() {
        MenuState mainMenu = new MenuState(this);
        WorldState world = new WorldState(this);
        SeedState seed = new SeedState(this);
        this.avatarSavedX = 0;
        this.avatarSavedY = 0;
        gameStates = new HashMap<>();
        gameStates.put("menu", mainMenu);
        gameStates.put("world", world);
        gameStates.put("seed", seed);
        //open the menu on initial startup
        gameStates.get(current).enter();
    }
    public Integer getAvatarSavedY() {
        return this.avatarSavedY;
    }
    public void updateAvatarSavedY(Integer value) {
        this.avatarSavedY = value;
    }
    public Integer getAvatarSavedX() {
        return this.avatarSavedX;
    }

    public void updateAvatarSavedX(Integer value) {
        this.avatarSavedX = value;
    }

    public String getRecordedInputs() {
        return this.recordedInputs;
    }

    public void updateRecordedInputs(String input) {
        this.recordedInputs = input;
    }

    public Long getUserSeed() {
        return this.userSeed;
    }

    public void updateUserSeed(Long userInput) {
        this.userSeed = userInput;
    }

    public HashMap<String, GameState> getGameStates() {
        return this.gameStates;
    }

    /**
     * This method updates the current gameState
     * Pressed characters are parsed to the active gameState
     * Calls current gameState's version of .update()
     * @param pressed   Characters that the user types in
     */
    public void update(Character pressed) {
        gameStates.get(current).update(pressed);
    }

    /**
     * This method renders the entire world
     */
    public void render() {
        gameStates.get(current).render();
    }

    /**
     * This method changes the current game state by reassigning the 'current' variable
     * Exits and enters the appropriate state.
     * @param to    The state to be changed to
     */
    public void changeState(String to) {
        if (!gameStates.containsKey(to)) {
            throw new IllegalArgumentException("Requested world result does not exist");
        } else {
            gameStates.get(current).exit();
            gameStates.get(to).enter();
            current = to;
            System.out.println("Changed game state to " + to);
        }
    }
}
