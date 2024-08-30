# Build Your Own World Design Document

This is a copy of the code used in one of my class' assignments

## Classes and Data Structures

### Binary Space Partitioning Class

**Description**

This class adds spaces to the world grid.

Uses a binary tree to represent the spaces and rooms

Each subspace is child of parent space

The constructor immediately creates root node, which represents the world

Takes in the world grid and number of rooms to generate

**Instance Variables**

1. int spaceCount: total number of Spaces in the BSP
2. List<Space> leafSpaces: list tracking the spaces which are leaf nodes of the tree

### Room

**Description**

A class that handles the creation of rooms. Inherits from the Rectangle class.

Takes in:

1. boolean isHallway; is the Room supposed to be a hallway? 2. if so, restrict the width to 1.
2. int width
3. int height
4. int x org; the x coordinate of it's bottom left corner
5. int y org; the y coordinate of it's bottom left corner

**Instance Variables**

1. int width
2. int height
3. int x_org
4. int y_org
5. bool isHallway

### Rectangle

**Description**

An interface representing any rectangular object in the world.

Any Rectangle is defined by its height, width, and origin coordinates.

The origin of any rectangle is a point on its bottom left corner.

**Instance Variables**

1. int width: rectangle's horizontal size
2. int height: rectangle's vertical size
3. int x: x coordinates of the rectangle's bottom left corner
4. int y: y coordinates of the rectangle's bottom left corner

### Space

**Description**

A class representing a partitioned area of the world. Each space behaves as a node within the Binary Space Partition class

Each Space implements the Rectangle class.

A Space is defined by its height, width, origin coordinates, and can have up to two children Spaces at a time.

The purpose of a Space is to define the area in which a room can be positioned.

**Instance Variables**

1. int width: rectangle's horizontal size
2. int height: rectangle's vertical size
3. int x: x coordinates of the rectangle's bottom left corner
4. int y: y coordinates of the rectangle's bottom left corner
5. Space leftChild: current Space's left child. Null if there is no child
6. Space rightChild: current Space's right child. Null if there is no child
7. Room containedRoom: the Room class contained within the Space. This Room will have its height and width limited by that of the Space's

### StateMachine

A StateMachine class is used to keep track of the current state the game is in.
Handles the changing of states
Handles updating of the current state
Renders the current state of the game

### GameState

An interface that acts as a template for different states of the game
Should be able to update the board depending on characters pressed
Render method
Exit and Enter methods

## Algorithms

### Space methods

**createRoom()**

- Instantiates a Room object and assigns it to Space.containedRoom variable
- Instantiated room object is such that 0 ≤ Room.width ≤ Space.width and 0 ≤ Room.height ≤ Space.height
- A room within the space must take up at least 60% of the space's area
  -> Creating a Room object should update the 2D array's tiles
  **createHallway()**

- Creates hallways between a room and a hallway, two rooms, or two hallways
- Looks at space, identifies where a room/hallway is, generates new hallway between the Space and its sibling space
- @params Room room1: first room that hallway connects
- @params Room room2: second room that hallway connects
