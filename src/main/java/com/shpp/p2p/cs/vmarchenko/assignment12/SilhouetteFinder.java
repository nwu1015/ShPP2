package com.shpp.p2p.cs.vmarchenko.assignment12;

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

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (!background[y][x] && !isVisited[y][x]) {
                    int size = runDFSAlgorithm(x, y, isVisited, background);
                    silhouetteSizes.add(size);
                }
            }
        }

        return countSignificantSilhouettes(silhouetteSizes);
    }

    /**
     * Recursively traverses all connected pixels of the object (silhouette).
     * It uses the DFS algorithm to calculate the total area of the object in pixels.
     *
     * @param x The X coordinate of the current pixel.
     * @param y The Y coordinate of the current pixel.
     * @param isVisited An array to keep track of pixels that have already been processed.
     * @param isBackground An array mask, where true means the background and false means part of the silhouette.
     * @return The number of pixels found that belong to a single object.
     */
    private int runDFSAlgorithm(int x, int y, boolean[][] isVisited, boolean[][] isBackground) {
        if (y < 0 || y >= isBackground.length || x < 0 || x >= isBackground[0].length) {
            return 0;
        }

        if (isVisited[y][x] || isBackground[y][x]) {
            return 0;
        }

        isVisited[y][x] = true;

        return 1 + runDFSAlgorithm(x + 1, y, isVisited, isBackground)
                + runDFSAlgorithm(x - 1, y, isVisited, isBackground)
                + runDFSAlgorithm(x, y + 1, isVisited, isBackground)
                + runDFSAlgorithm(x, y - 1, isVisited, isBackground);
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