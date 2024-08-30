package core;

import edu.princeton.cs.algs4.StdDraw;

public class Main {
    /**
     * This method acts as the main loop to run the game
     */
    public static void gameLoop() {
        StateMachine stateMachine = new StateMachine();
        while (true) {
            //read input if it exists, update the current state
            if (StdDraw.hasNextKeyTyped()) {
                Character pressed = StdDraw.nextKeyTyped();
                stateMachine.update(pressed);
            }
            //if no input, render the game anyways
            stateMachine.render();
        }
    }
    public static void main(String[] args) {
        gameLoop();
    }
}
