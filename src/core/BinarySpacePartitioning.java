package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BinarySpacePartitioning {
    int size = 0;
    List<Space> leafSpaces;
    List<Space> canSplitSpaces;
    List<Space> nodes;
    Space root;
    Random randomNum;
    boolean wasCutVertically = false;
    final Double MAX_DIMENSIONS_RATIO = 1.25;

    public BinarySpacePartitioning(int worldX, int worldY, int worldWidth, int worldHeight, Random randomNum) {
        this.randomNum = randomNum;
        root = new Space(worldX, worldY, worldWidth, worldHeight, randomNum);
        leafSpaces = new ArrayList<>();
        canSplitSpaces = new ArrayList<>();
        canSplitSpaces.add(root);
        nodes = new ArrayList<>();
        nodes.add(root);
    }

    /**
     * This method splits a given space horizontally or vertically.
     * A split only occurs if the room is large enough to be split.
     * The split direction is dependent on the ratio of a Space's height and width
     * @param currentSpace
     */
    private void split(Space currentSpace) {
        if (currentSpace.canSplit("v") && currentSpace.canSplit("h")) {
            if (currentSpace.widthToHeight() >= MAX_DIMENSIONS_RATIO) {
                currentSpace.splitVertical();
            } else if (currentSpace.heightToWidth() >= MAX_DIMENSIONS_RATIO) {
                currentSpace.splitHorizontal();
            } else {
                if (randomNum.nextInt() % 2 == 0) {
                    currentSpace.splitHorizontal();
                } else {
                    currentSpace.splitVertical();
                }
            }
        } else if (currentSpace.canSplit("v")) {
            currentSpace.splitVertical();
            wasCutVertically = true;
        } else if (currentSpace.canSplit("h")) {
            currentSpace.splitHorizontal();
            wasCutVertically = false;
        }
    }

    /**
     * Adds spaces to the BSP by splitting lead nodes until they cannot be split anymore.
     */
    public void addSpaces() {
        while (!canSplitSpaces.isEmpty()) {
            Space currentSpace = canSplitSpaces.get(0);
            split(currentSpace);
            canSplitSpaces.remove(0);
            if (currentSpace.getLeftSpace().canSplit("v") || currentSpace.getLeftSpace().canSplit("h")) {
                canSplitSpaces.add(currentSpace.getLeftSpace());
            } else {
                leafSpaces.add(currentSpace.getLeftSpace());
            }
            if (currentSpace.getRightSpace().canSplit("v") || currentSpace.getRightSpace().canSplit("h")) {
                canSplitSpaces.add(currentSpace.getRightSpace());
            } else {
                leafSpaces.add(currentSpace.getRightSpace());
            }
            nodes.add(currentSpace.getLeftSpace());
            nodes.add(currentSpace.getRightSpace());
        }
    }
}
