package core;

import java.util.*;

import utils.RandomUtils;


public class Space extends Rectangle {
    private Space leftSpace;
    private Space rightSpace;
    private Room roomContained;
    private List<Rectangle> corridorContained;
    private Random randomNum;
    //this must be at <= MINIMUM_SPACE_SIZE - MINIMUM_ROOM_SIZE_LOWER
    private final Integer MAX_ROOM_ORIGIN_DEVIATION = 7;
    private final Integer MIN_ROOM_ORIGIN_DEVIATION = 2;
    private final Integer MINIMUM_ROOM_SIZE_UPPER = 7;
    private final Integer MINIMUM_ROOM_SIZE_LOWER = 5;
    private final Integer MINIMUM_SPACE_SIZE = 12;
    private final Integer MINIMUM_SPLITTABLE_LENGTH = 26;

    public List<Rectangle> getCorridorContained() {
        return this.corridorContained;
    }

    public Room getRoomContained() {
        return this.roomContained;
    }

    public Space getRightSpace() {
        return this.rightSpace;
    }

    public Space getLeftSpace() {
        return this.leftSpace;
    }

    public Space(int x, int y, int width, int height, Random randomNum) {
        super(x, y, width, height);
        this.roomContained = null;
        this.corridorContained = null;
        this.leftSpace = null;
        this.rightSpace = null;
        this.randomNum = randomNum;
        this.corridorContained = new ArrayList<>();
    }

    /**
     * Method that returns whether a space can be split in a specified direction.
     * @param direction of split, a string of "v" or "h"
     * @return  boolean
     */
    public boolean canSplit(String direction) {
        if (direction.equals("v")) {
            int splitBy = MINIMUM_SPLITTABLE_LENGTH;
            return this.width() >= splitBy;
        } else if (direction.equals("h")) {
            int splitBy = MINIMUM_SPLITTABLE_LENGTH;
            return this.height() >= splitBy;
        } else {
            throw new IllegalArgumentException("argument should be either a string of 'v' or 'h'.");
        }
    }

    /**
     * Generates random coordinates for rooms
     */
    private HashMap<String, Integer> coordinates() {
        //room origin should always be >= 1 unit away from border
        int maxRadius = RandomUtils.uniform(randomNum, MIN_ROOM_ORIGIN_DEVIATION, MAX_ROOM_ORIGIN_DEVIATION);
        int roomX = RandomUtils.uniform(randomNum, this.origin("x") + 1, this.origin("x") + maxRadius);
        int roomY = RandomUtils.uniform(randomNum, this.origin("y") + 1, this.origin("y") + maxRadius);

        HashMap<String, Integer> coordinates = new HashMap<>();
        coordinates.put("x", roomX);
        coordinates.put("y", roomY);

        return coordinates;
    }

    /**
     * Generates width and height of a room based on its coordinates and containing space
     * @return
     */
    private HashMap<String, Integer> lengths(int roomX, int roomY) {
        int randomMin = RandomUtils.uniform(randomNum, MINIMUM_ROOM_SIZE_LOWER, MINIMUM_ROOM_SIZE_UPPER);
        int width = RandomUtils.uniform(randomNum, randomMin, this.width() - (roomX - this.origin("x")));
        int height = RandomUtils.uniform(randomNum, randomMin, this.height() - (roomY - this.origin("y")));

        HashMap<String, Integer> lengths = new HashMap<>();
        lengths.put("w", width);
        lengths.put("h", height);

        return lengths;
    }


    public void createRoom() {
        int roomX = coordinates().get("x");
        int roomY = coordinates().get("y");
        int roomWidth = lengths(roomX, roomY).get("w");
        int roomHeight = lengths(roomX, roomY).get("h");
        roomContained = new Room(roomX, roomY, roomWidth, roomHeight);
    }

    public void splitHorizontal() {
        int upperBound = this.height() - MINIMUM_SPACE_SIZE;
        int horizontalCutY = RandomUtils.uniform(randomNum, MINIMUM_SPACE_SIZE, upperBound) + this.origin("y");
        int bottomHeight = horizontalCutY - this.origin("y");
        int topHeight = this.height() - bottomHeight;

        leftSpace = new Space(this.origin("x"), horizontalCutY, this.width(), topHeight, randomNum);
        rightSpace = new Space(this.origin("x"), this.origin("y"), this.width(), bottomHeight, randomNum);
    }

    /**
    * Method that randomly determines a vertical cut and creates children spaces.
    * Assigns children spaces to Space invoking this method.
    */
    public void splitVertical() {
        int upperBound = this.width() - MINIMUM_SPACE_SIZE;
        int verticalCutX = RandomUtils.uniform(randomNum, MINIMUM_SPACE_SIZE, upperBound) + this.origin("x");
        int leftWidth = verticalCutX - this.origin("x");
        int rightWidth = this.width() - leftWidth;

        this.leftSpace = new Space(this.origin("x"), this.origin("y"), leftWidth, this.height(), randomNum);
        this.rightSpace = new Space(verticalCutX, this.origin("y"), rightWidth, this.height(), randomNum);
    }


    private Room recurse() {
        //base case: we're in a leaf space
        if (this.roomContained != null) {
            return this.roomContained;
        } else {
            //recurse on the left and right children
            Room childRoom1 = this.leftSpace.recurse();
            Room childRoom2 = this.rightSpace.recurse();

            createHallway(childRoom1, childRoom2);
            //return one of the random rooms
            return childRoom2;
        }
    }

    /**
     * Method that creates a corridor between a Space's two children rooms by setting corridorContained to some value
     * If the children do not contain rooms, recurse and go down to the lowest layer
     */
    public void createCorridor() {
        recurse();
    }

    private List<List<Integer>> getBorder(Rectangle room) {
        List<List<Integer>> borderCoordinates = new ArrayList<>();
        for (int x = room.origin("x") + 2; x < room.origin("x") + room.width() - 2; x++) {
            List<Integer> coordinate = new ArrayList<>();
            coordinate.add(x);
            coordinate.add(room.origin("y"));
            borderCoordinates.add(coordinate);
        }
        for (int y = room.origin("y") + 2; y < room.origin("y") + room.height() - 2; y++) {
            List<Integer> coordinate = new ArrayList<>();
            coordinate.add(room.origin("x"));
            coordinate.add(y);
            borderCoordinates.add(coordinate);
        }
        return borderCoordinates;
    }

    /**
     * This method returns a random value along a specified axis within a room.
     * Ensures that the selected random point is not within a 1 unit of a previously selected point
     * @param room  the room where the point is being created
     * @param direction the specified axis in which the return value lies on
     * @return  returns the random value
     */
    private Integer returnPoint(Room room, String direction) {
        int result;
        if (direction.equals("x")) {
            result = RandomUtils.uniform(randomNum, room.origin("x") + 2, room.origin("x") + room.width() - 2);
            while (room.getxPoints().contains(result + 1) || room.getxPoints().contains(result - 1)) {
                result = RandomUtils.uniform(randomNum, room.origin("x") + 2, room.origin("x") + room.width() - 1);
            }
            room.getxPoints().add(result);
            return result;
        } else {
            result = RandomUtils.uniform(randomNum, room.origin("y") + 2, room.origin("y") + room.height() - 1);
            while (room.getyPoints().contains(result + 1) || room.getyPoints().contains(result - 1)) {
                result = RandomUtils.uniform(randomNum, room.origin("y") + 1, room.origin("y") + room.height() - 2);
            }
            room.getyPoints().add(result);
            return result;
        }
    }

    /**
     * This method generates the coordinates of a hallway between two given rooms.
     * The start and end points of the hallway are randomly chosen within the given rooms.
     * The hallway created is stored as a variable within any Space object.
     * @param room1 the first room where one of the points is kept
     * @param room2 the second room where the other point is kept
     */
    public void createHallway(Room room1, Room room2) {
        Rectangle verticalCorridor;
        Rectangle horizontalCorridor;
        int x1 = returnPoint(room1, "x");
        int y1 = returnPoint(room1, "y");
        int x2 = returnPoint(room2, "x");
        int y2 = returnPoint(room2, "y");
        int w = x2 - x1;
        int h = y2 - y1;
        int width = Math.abs(w);
        int height = Math.abs(h) + 1;

        //always build up or left to right
        if (w < 0) {
            if (h < 0) {
                horizontalCorridor = new Rectangle(x2, y1, width, 1);
                verticalCorridor = new Rectangle(x2, y2, 1, height);
                corridorContained.add(verticalCorridor);
            } else if (h > 0) {
                horizontalCorridor = new Rectangle(x2, y1, width, 1);
                verticalCorridor = new Rectangle(x2, y1, 1, height);
                corridorContained.add(verticalCorridor);
            } else {
                horizontalCorridor = new Rectangle(x2, y2, width, 1);
            }
            corridorContained.add(horizontalCorridor);
        } else if (w > 0) {
            if (h < 0) {
                horizontalCorridor = new Rectangle(x1, y2, width, 1);
                verticalCorridor = new Rectangle(x1, y2, 1, height);
                corridorContained.add(verticalCorridor);
            } else if (h > 0) {
                horizontalCorridor = new Rectangle(x1, y2, width, 1);
                verticalCorridor = new Rectangle(x1, y1, 1, height);
                corridorContained.add(verticalCorridor);
            } else {
                horizontalCorridor = new Rectangle(x1, y1, width, 1);
            }
            corridorContained.add(horizontalCorridor);
        } else {
            if (h < 0) {
                verticalCorridor = new Rectangle(x2, y2, 1, height);
            } else {
                verticalCorridor = new Rectangle(x2, y1, 1, height);
            }
            corridorContained.add(verticalCorridor);
        }
    }

    /**
     * Method that returns the width to height ratio of the space
     * @return  Double width to height ratio
     */
    public Double widthToHeight() {
        return (Double) (double) (this.width() / this.height());
    }

    /**
     * Method that returns the height to width ratio of the space
     * @return  Double height to width ratio
     */
    public Double heightToWidth() {
        return (Double) (double) (this.height() / this.width());
    }
}
