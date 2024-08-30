package core;

public interface GameState {
    void update(Character pressed);
    void render();
    /**
     * Enter and Exit methods that updates specific items needed for this state
     */
    void enter();
    void exit();
}
