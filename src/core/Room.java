package core;

import java.util.ArrayList;
import java.util.List;

public class Room extends Rectangle {
    private int MINIMUM_LENGTH = 5;
    private List<Integer> xPoints;
    private List<Integer> yPoints;
    public Room(int x, int y, int width, int height) {
        super(x, y, width, height);
        xPoints = new ArrayList<>();
        yPoints = new ArrayList<>();
    }

    /**
     * Method that retrieves the x coordinates of all hallway points within the room.
     * @return  the x coordinates of the point
     */
    public List<Integer> getxPoints() {
        return xPoints;
    }

    /**
     * Method that retrieves the y coordinates of all hallway points within the room.
     * @return  the x coordinates of the point
     */
    public List<Integer> getyPoints() {
        return yPoints;
    }
}
