package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class World {
    private Random randomNum;
    private BinarySpacePartitioning tree;
    private Integer WORLD_X = 0;
    private Integer WORLD_Y = 0;
    private Integer width;
    private Integer height;
    private TETile[][] world;
    private TETile[][] copy;
    private final Integer DEFAULT_WIDTH = 80;
    private final Integer DEFAULT_HEIGHT = 40;
    private final Integer MINIMUM_ROOM_SIZE = 5;
    private final Double TIME_LOCATION = 15.0;
    private Point avatarLocation;
    //count the net distance in the x and y direction
    private Integer avatarXTraversal = 0;
    private Integer avatarYTraversal = 0;
    private Integer LINE_OF_SIGHT_RADIUS = 5;

    private boolean lightOff;

    public void updateLightOff(boolean status) {
        this.lightOff = status;
    }

    public boolean getLightOff() {
        return this.lightOff;
    }

    public Integer getAvatarYTraversal() {
        return this.avatarYTraversal;
    }

    public Integer getAvatarXTraversal() {
        return this.avatarXTraversal;
    }

    public TETile[][] getCopyArray() {
        return this.copy;
    }

    public TETile[][] getWorldArray() {
        return this.world;
    }

    public World(long seed) {
        this.randomNum = new Random(seed);
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        lightOff = false;
        world = new TETile[this.width][this.height];
        copy = new TETile[this.width][this.height];
        tree = new BinarySpacePartitioning(WORLD_X, WORLD_Y, this.width, this.height, this.randomNum);
        tree.addSpaces();
    }

    public TETile[][] getTiles() {
        getSpaces();
        getRooms();
        getHallways();
        spawnAvatar();
        return this.world;
    }

    /**
     * Method to populate 2D world array with spaces
     * For testing purposes
     */
    public void getSpaces() {
        List<Space> leafSpaces = this.tree.leafSpaces;
        for (Space space : leafSpaces) {
            for (int x = space.origin("x"); x < space.origin("x") + space.width(); x++) {
                for (int y = space.origin("y"); y < space.origin("y") + space.height(); y++) {
                    this.world[x][y] = Tileset.NOTHING;
                }
            }
            // fill columns with walls
            for (int yCol = space.origin("y"); yCol < space.height() + space.origin("y"); yCol++) {
                world[space.origin("x")][yCol] = Tileset.NOTHING;
                world[space.origin("x") + space.width() - 1][yCol] = Tileset.NOTHING;
            }
            // fill rows with walls
            for (int xRow = space.origin("x") + 1; xRow < space.width() + space.origin("x") - 1; xRow++) {
                world[xRow][space.origin("y") + space.height() - 1] = Tileset.NOTHING;
                int tTopRow = space.origin("y") + space.height() - 1;
                world[xRow][space.origin("y")] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Method to populate the 2D world array
     */
    public void getRooms() {
        List<Space> leafSpaces = this.tree.leafSpaces;
        for (Space space : leafSpaces) {
            if (space.width() >= MINIMUM_ROOM_SIZE && space.height() >= MINIMUM_ROOM_SIZE) {
                space.createRoom();
                Rectangle room = space.getRoomContained();
                //fills room with floor tiles
                for (int x = room.origin("x"); x < room.origin("x") + room.width(); x++) {
                    for (int y = room.origin("y"); y < room.origin("y") + room.height(); y++) {
                        this.world[x][y] = Tileset.FLOOR;
                    }
                }

                // fill columns with walls
                for (int yCol = room.origin("y"); yCol < room.height() + room.origin("y"); yCol++) {
                    world[room.origin("x")][yCol] = Tileset.WALL;
                    int testX = room.origin("x") + room.width() - 1;
                    world[room.origin("x") + room.width() - 1][yCol] = Tileset.WALL;
                }
                // fill rows with walls
                for (int xRow = room.origin("x") + 1; xRow < room.width() + room.origin("x") - 1; xRow++) {
                    world[xRow][room.origin("y") + room.height() - 1] = Tileset.WALL;
                    int tTopRow = room.origin("y") + room.height() - 1;
                    world[xRow][room.origin("y")] = Tileset.WALL;
                }
            }

        }
    }


    public void getHallways() {
        tree.root.createCorridor();
        for (Space space : tree.nodes) {
            if (space.getCorridorContained().isEmpty()) {
                continue;
            }
            for (Rectangle corridor : space.getCorridorContained()) {
                for (int x = corridor.origin("x"); x < corridor.origin("x") + corridor.width(); x++) {
                    for (int y = corridor.origin("y"); y < corridor.origin("y") + corridor.height(); y++) {
                        if (world[x][y] != Tileset.FLOOR) {
                            world[x][y] = Tileset.WALL;
                            if (corridor.width() > corridor.height()) {
                                world[x][y + 1] = Tileset.WALL;
                                world[x][y - 1] = Tileset.WALL;
                            } else {
                                world[x + 1][y] = Tileset.WALL;
                                world[x - 1][y] = Tileset.WALL;
                            }
                        }
                    }
                }
            }
            for (Rectangle corridor : space.getCorridorContained()) {
                for (int x = corridor.origin("x"); x < corridor.origin("x") + corridor.width(); x++) {
                    for (int y = corridor.origin("y"); y < corridor.origin("y") + corridor.height(); y++) {
                        if (world[x][y] != Tileset.FLOOR) {
                            world[x][y] = Tileset.FLOOR;
                        }
                    }
                }
            }
        }
    }

    public void renderHud() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        double x = 2.0;
        double y = DEFAULT_HEIGHT - 1;
        String mouseTile = mouseTile();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(x, y, mouseTile);
        StdDraw.text(TIME_LOCATION, DEFAULT_HEIGHT - 1, formattedDateTime);
        StdDraw.show();
    }

    /**
     * @return  the current tile that the mouse is hovering over
     */
    private String mouseTile() {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        //lousy fix
        if (mouseY == DEFAULT_HEIGHT) {
            mouseY = 39;
        }
        TETile currentTile = world[mouseX][mouseY];
        return currentTile.description();
    }

    /**
     * This method returns a random point within the room.
     * A point is defined as a set of two integers, (X, Y)
     */
    private Point randomRoomPoint(Room room) {
        int randomX = RandomUtils.uniform(randomNum, room.origin("x") + 1, room.origin("x") + room.width() - 1);
        int randomY = RandomUtils.uniform(randomNum, room.origin("y") + 1, room.origin("y") + room.height() - 1);
        return new Point(randomX, randomY);
    }

    /**
     * Method that checks if the avatar can move in specified direction
     * @return boolean depending on outcome
     */
    private boolean canMove(int x, int y) {
        int futureX = avatarLocation.x + x;
        int futureY = avatarLocation.y + y;
        //out of bounds check
        if (futureX > DEFAULT_WIDTH || futureX < 0 || futureY > DEFAULT_HEIGHT || futureY < 0) {
            return false;
        }
        //world check
        if (world[futureX][futureY].equals(Tileset.WALL)) {
            return false;
        }
        return true;
    }

    public void moveAvatar(int x, int y) {
        if (canMove(x, y)) {
            world[avatarLocation.x][avatarLocation.y] = Tileset.FLOOR;
            avatarXTraversal += x;
            avatarLocation.x += x;
            avatarLocation.y += y;
            avatarYTraversal += y;
            world[avatarLocation.x][avatarLocation.y] = Tileset.AVATAR;
        }
    }

    public void spawnAvatar() {
        TETile avatar = Tileset.AVATAR;
        Integer randomIndex = RandomUtils.uniform(randomNum, 0, tree.leafSpaces.size());
        Space randomSpace = tree.leafSpaces.get(randomIndex);
        Room randomRoom = randomSpace.getRoomContained();
        avatarLocation = randomRoomPoint(randomRoom);
        int x = avatarLocation.x;
        int y = avatarLocation.y;
        world[x][y] = avatar;
    }

    private void copyWorld(TETile[][] original, TETile[][] copyTo) {
        for (int x = 0; x < DEFAULT_WIDTH; x++) {
            for (int y = 0; y < DEFAULT_HEIGHT; y++) {
                copyTo[x][y] = original[x][y];
            }
        }
    }

    private List<Point> spotlightTiles(Point avatar, Integer radius) {
        List<Point> points = new ArrayList<>();
        int x = avatar.x;
        int y = avatar.y;

        for (int i = (x - radius); i <= (x + radius); i++) {
            for (int j = (y - radius); j <= (y + radius); j++) {
                points.add(new Point(i, j));
            }
        }
        return points;
    }

    /**
     * This method blacks out the map except for a spotlight around the avatar
     * Edits the world 2D array
     */
    public void lightsOut() {
        List<Point> points = spotlightTiles(avatarLocation, LINE_OF_SIGHT_RADIUS);
        copyWorld(world, copy);
        for (int x = 0; x < DEFAULT_WIDTH; x++) {
            for (int y = 0; y < DEFAULT_HEIGHT; y++) {
                Point currPoint = new Point(x, y);
                if (!points.contains(currPoint)) {
                    copy[x][y] = Tileset.NOTHING;
                }
            }
        }
    }

    public List<Integer> getDimensions() {
        List<Integer> dimensions = new ArrayList<>();
        dimensions.add(this.width);
        dimensions.add(this.height);
        return dimensions;
    }
}
