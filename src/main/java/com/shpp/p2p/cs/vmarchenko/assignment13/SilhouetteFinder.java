package com.shpp.p2p.cs.vmarchenko.assignment13;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Based on background information,
 * it counts the number of silhouettes using a depth-first search algorithm.
 */
public class SilhouetteFinder {
    /**
     * A method for finding silhouettes.
     * It goes through all pixels and
     * if it is not the background and the pixel has not been visited -
     * the recursive depth-first search algorithm is started
     *
     * @param image        a file with the appropriate name
     * @param isVisited    an array that shows whether the pixel has already been visited
     * @param isBackground an array indicating whether a pixel belongs to the background
     * @return number of silhouettes found
     */
    public int findSilhouettes(BufferedImage image, boolean[][] isVisited, boolean[][] isBackground) {
        List<Integer> silhouetteSizes = new ArrayList<>();
        boolean[][] background = isBackground;

        for (int i = 0; i < Constants.NUMBER_OF_REDUCTION; i++) {
            background = applyReduction(background);
        }

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (!background[y][x] && !isVisited[y][x]) {
                    int size = runBFSAlgorithm(new Node(x, y), isVisited, background);
                    silhouetteSizes.add(size);
                }
            }
        }

        return countSignificantSilhouettes(silhouetteSizes);
    }

    /**
     * Applies a taper to the object mask.
     * Each silhouette pixel becomes the background
     * if there is at least one background pixel among its direct neighbors.
     *
     * @param isBackground array of information about where the background is and where the silhouette is
     * @return new array after silhouette reduction
     */
    private boolean[][] applyReduction(boolean[][] isBackground) {
        int height = isBackground.length;
        int width = isBackground[0].length;
        boolean[][] reducedGrid = new boolean[height][width];

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isBackground[y][x]) {
                    reducedGrid[y][x] = true;
                    continue;
                }

                boolean touchesBackground = false;
                for (int[] dir : directions) {
                    int neighborX = x + dir[0];
                    int neighborY = y + dir[1];

                    if (neighborY < 0 || neighborY >= height || neighborX < 0 || neighborX >= width
                            || isBackground[neighborY][neighborX]) {
                        touchesBackground = true;
                        break;
                    }
                }

                reducedGrid[y][x] = touchesBackground;
            }
        }

        return reducedGrid;
    }

    /**
     * Traverses a connected silhouette component using the Breadth-First Search (BFS) algorithm.
     * It counts the total area of the silhouette in pixels to determine its size.
     *
     * @param node         the starting pixel of the silhouette.
     * @param isVisited    boolean array keeping track of already processed pixels.
     * @param isBackground boolean mask indicating which pixels belong to the background.
     * @return total number of connected pixels belonging to this single silhouette.
     */
    private int runBFSAlgorithm(Node node, boolean[][] isVisited, boolean[][] isBackground) {
        int x = node.x();
        int y = node.y();

        int size = 0;
        Queue<Node> uncheckedPixels = new ArrayDeque<>();
        uncheckedPixels.add(node);

        while (!uncheckedPixels.isEmpty()) {
            Node current = uncheckedPixels.remove();
            int currentX = current.x();
            int currentY = current.y();

            size++;
            isVisited[y][x] = true;

            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

            for (int[] dir : directions) {
                int nextX = currentX + dir[0];
                int nextY = currentY + dir[1];

                if (addNewPixelToQueue(nextX, nextY, isVisited, isBackground)) {
                    uncheckedPixels.add(new Node(nextX, nextY));
                }
            }
        }
        return size;
    }

    /**
     * Validates a neighbor pixel and adds it to the exploration queue if eligible.
     *
     * @param x            The X coordinate of the neighbor pixel.
     * @param y            The Y coordinate of the neighbor pixel.
     * @param isVisited    The boolean array tracking visited pixels.
     * @param isBackground The boolean mask array.
     * @return True if the pixel is part of the silhouette and was successfully queued, false otherwise.
     */
    private boolean addNewPixelToQueue(int x, int y, boolean[][] isVisited, boolean[][] isBackground) {
        if (!isValidCoordinate(x, y, isBackground)) {
            return false;
        }

        if (isBackground[y][x] || isVisited[y][x]) {
            return false;
        }

        isVisited[y][x] = true;
        return true;
    }

    /**
     * Checks if the given coordinates are within the boundaries of the image grid.
     *
     * @param x    The X coordinate to validate.
     * @param y    The Y coordinate to validate.
     * @param grid The 2D boolean grid representing the image template.
     * @return True if coordinates are valid and inside boundaries, false otherwise.
     */
    private boolean isValidCoordinate(int x, int y, boolean[][] grid) {
        return y >= 0 && y < grid.length && x >= 0 && x < grid[0].length;
    }

    /**
     * Filters out small background noise and counts only significant silhouettes.
     * The threshold is determined dynamically based on the maximum found silhouette size.
     *
     * @param sizes A list containing sizes of all detected pixel components.
     * @return The number of silhouettes that exceed the allowed noise tolerance threshold.
     */
    private int countSignificantSilhouettes(List<Integer> sizes) {
        if (sizes.isEmpty()) {
            return 0;
        }

        int maxSize = Collections.max(sizes);
        double threshold = maxSize * Constants.NOISE_TOLERANCE;

        int count = 0;
        for (int size : sizes) {
            if (size >= threshold) {
                count++;
            }
        }
        return count;
    }
}