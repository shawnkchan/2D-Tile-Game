package core;

import java.util.HashMap;
import java.util.Map;

public class Rectangle {
    private Map<String, Integer> origin;
    private int height;
    private int width;

    public Rectangle(int x, int y, int width, int height) {
        this.height = height;
        this.width = width;
        this.origin = new HashMap<>();
        this.origin.put("x", x);
        this.origin.put("y", y);
    }

    /**
     * Returns the integer value of the origin's location along the specified axis.
     *
     * @param axis  a string of the desired axis (x or y) of the origin coordinate
     * @return   the value of the coordinate
    */
    public int origin(String axis) {
        return this.origin.get(axis);
    }

    /**
     * Returns the integer value of the rectangle's height.
     *
     * @return  the height of the rectangle object
    */
    public int height() {
        return this.height;
    }

    /**
     * Returns the integer value of the rectangle's width.
     *
     * @return  the height of the rectangle object
     */
    public int width() {
        return this.width;
    }

}
