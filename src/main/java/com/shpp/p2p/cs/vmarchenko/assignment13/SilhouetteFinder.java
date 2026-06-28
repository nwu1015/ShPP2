package com.shpp.p2p.cs.vmarchenko.assignment13;

import java.awt.image.BufferedImage;
import java.util.*;

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

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (!isBackground[y][x] && !isVisited[y][x]) {
                    int size = runBFSAlgorithm(new Node(x, y), isVisited, isBackground);
                    silhouetteSizes.add(size);
                }
            }
        }

        return countSignificantSilhouettes(silhouetteSizes);
    }

    private int countSignificantSilhouettes(List<Integer> sizes) {
        if (sizes.isEmpty()) {
            return 0;
        }

        Collections.sort(sizes);
        double median = sizes.get(sizes.size() / 2);

        double threshold = median * 0.1;

        int count = 0;
        for (int size : sizes) {
            if (size >= threshold) {
                count++;
            }
        }
        return count;
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

    private boolean isValidCoordinate(int x, int y, boolean[][] grid) {
        return y >= 0 && y < grid.length && x >= 0 && x < grid[0].length;
    }
}
