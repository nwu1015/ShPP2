package com.shpp.p2p.cs.vmarchenko.assignment13;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Assignment 13: Silhouettes
 * Takes the file name as arguments and
 * determines how many separate silhouettes are in the image
 */
public class Assignment13Part1 {
    /**
     * To determine the minimum silhouette size.
     * Required to avoid unnecessary waste
     */
    private static final double NOISE_TOLERANCE = 0.005;

    private static final String DEFAULT_FILENAME = "test.jpg";

    private static final int HISTOGRAM_ARRAY_LENGTH = 256;

    public static void main(String[] args) {
        Assignment13Part1 assignment = new Assignment13Part1();

        String imageName = DEFAULT_FILENAME;
        if (args.length > 0 && args[0] != null) {
            imageName = args[0];
        }

        BufferedImage image = assignment.getImageFromArgs(imageName);

        boolean[][] isBackground = new boolean[image.getHeight()][image.getWidth()];
        boolean[][] isVisited = new boolean[image.getHeight()][image.getWidth()];

        assignment.defineBackground(image, isBackground);

        System.out.println(assignment.findSilhouettes(image, isVisited, isBackground));
    }

    /**
     * It takes the file name and searches for that file in the resources' folder.
     *
     * @param imageName file name
     * @return a file with the appropriate name
     */
    private BufferedImage getImageFromArgs(String imageName) {
        try {
            File file = new File(imageName);
            BufferedImage image;

            if (file.exists()) {
                image = ImageIO.read(file);
            } else {
                try(InputStream name = Assignment13Part1.class.getResourceAsStream("/" + imageName)){
                    if (name == null) {
                        throw new Exception("File doesn't exists: " + imageName);
                    }

                    image = ImageIO.read(name);
                }
            }
            return image;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
    private int findSilhouettes(BufferedImage image, boolean[][] isVisited, boolean[][] isBackground) {
        int silhouetteCounter = 0;
        double minSize = (image.getWidth() * image.getHeight()) * NOISE_TOLERANCE;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                if (!isBackground[y][x] && !isVisited[y][x]) {
                    Node node = new Node(x, y);
                    int size = runBFSAlgorithm(node, isVisited, isBackground);

                    if (size > minSize) {
                        silhouetteCounter++;
                    }
                }
            }
        }

        return silhouetteCounter;
    }

    /**
     * Method for determining the background by Otsu method
     *
     * @param image        given picture
     * @param isBackground an array that collects information pixel
     *                     by pixel whether this pixel belongs to the background
     */
    private void defineBackground(BufferedImage image, boolean[][] isBackground) {
        int totalPixels = image.getHeight() * image.getWidth();

        int[] histogram = calculateHistogram(image);
        int threshold = findThreshold(histogram, totalPixels);
        boolean isLight = isBackgroundLight(image, threshold);
        applyThresholdMask(image, isBackground, threshold, isLight);
    }

    /**
     * Calculates the grayscale brightness histogram of the entire image.
     * Groups pixels into 256 bins based on their average RGB intensity.
     *
     * @param image given picture
     * @return an array of size 256, where each index contains the count of pixels with that brightness
     */
    private int[] calculateHistogram(BufferedImage image) {
        int[] histogram = new int[HISTOGRAM_ARRAY_LENGTH];
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int brightness = (int) calculatePixelBrightness(image.getRGB(x, y));
                histogram[brightness]++;
            }
        }
        return histogram;
    }

    /**
     * Finds the optimal threshold value using Otsu's algorithm.
     * It iterates through all possible thresholds,
     * effectively separating the background from foreground silhouettes.
     * <p>
     * At the previous level, background detection was implemented by taking random pixels.
     * However, this implementation was unpredictable. So, looking at other options, I found Otsu's method.
     * Source: https://uk.wikipedia.org/wiki/%D0%9C%D0%B5%D1%82%D0%BE%D0%B4_%D0%9E%D1%86%D1%83
     *
     * @param histogram   the 256-bin brightness histogram of the image
     * @param totalPixels the total number of pixels in the image.
     * @return the optimal threshold brightness value (0-255).
     */
    private int findThreshold(int[] histogram, int totalPixels) {
        int totalSum = 0;
        for (int i = 0; i < histogram.length; i++) {
            totalSum += i * histogram[i];
        }

        int weightBackground = 0;
        double sumBackground = 0;
        double maximum = 0;
        int threshold = 0;

        for (int i = 0; i < histogram.length; i++) {
            weightBackground += histogram[i];
            if (weightBackground == 0) continue;

            int weightForeground = totalPixels - weightBackground;
            if (weightForeground == 0) break;

            sumBackground += i * histogram[i];
            double sumForeground = totalSum - sumBackground;

            double averageBrightnessBackground = sumBackground / weightBackground;
            double averageBrightnessForeground = sumForeground / weightForeground;

            double varBetween = (double) weightBackground * weightForeground *
                    (averageBrightnessBackground - averageBrightnessForeground) *
                    (averageBrightnessBackground - averageBrightnessForeground);

            if (varBetween > maximum) {
                maximum = varBetween;
                threshold = i;
            }
        }
        return threshold;
    }

    /**
     * Determines whether the image background is light or dark.
     * It checks the top-left corner pixel as a benchmark against the computed threshold.
     *
     * @param image     the given picture
     * @param threshold the calculated optimal Otsu threshold.
     * @return true if the background is lighter than the threshold, in other cases - false.
     */
    private boolean isBackgroundLight(BufferedImage image, int threshold) {
        int upperLeftPixel = image.getRGB(0, 0);
        int brightness = (int) calculatePixelBrightness(upperLeftPixel);
        return brightness > threshold;
    }

    /**
     * Applies the final mask to the isBackground array based on the threshold.
     * Compares each pixel's brightness to the threshold and marks it as background or silhouette.
     *
     * @param image             given picture.
     * @param isBackground      the 2D boolean mask array to populate.
     * @param threshold         the optimal brightness threshold to split pixels.
     * @param isBackgroundLight a flag indicating whether background pixels are bright or dark.
     */
    private void applyThresholdMask(BufferedImage image, boolean[][] isBackground, int threshold, boolean isBackgroundLight) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                double currentBrightness = calculatePixelBrightness(image.getRGB(x, y));

                if (isBackgroundLight) {
                    isBackground[y][x] = currentBrightness >= threshold;
                } else {
                    isBackground[y][x] = currentBrightness <= threshold;
                }
            }
        }
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

        if (isInvalidStartPoint(x, y, isVisited, isBackground)) {
            return 0;
        }

        int size = 0;
        Queue<Node> uncheckedPixels = new ArrayDeque<>();
        uncheckedPixels.add(node);
        isVisited[y][x] = true;

        while (!uncheckedPixels.isEmpty()) {
            Node current = uncheckedPixels.remove();
            int currentX = current.x();
            int currentY = current.y();

            size++;

            if (addNewPixelToQueue(currentX, currentY + 1, isVisited, isBackground)) {
                uncheckedPixels.add(new Node(currentX, currentY + 1));
            }

            if (addNewPixelToQueue(currentX, currentY - 1, isVisited, isBackground)) {
                uncheckedPixels.add(new Node(currentX, currentY - 1));
            }

            if (addNewPixelToQueue(currentX + 1, currentY, isVisited, isBackground)) {
                uncheckedPixels.add(new Node(currentX + 1, currentY));
            }

            if (addNewPixelToQueue(currentX - 1, currentY, isVisited, isBackground)) {
                uncheckedPixels.add(new Node(currentX - 1, currentY));
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
        if (y >= 0 && y < isBackground.length && x >= 0 && x < isBackground[0].length) {
            if (!isBackground[y][x] && !isVisited[y][x]) {
                isVisited[y][x] = true;
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether the given coordinates are valid as a starting point for BFS.
     * A starting point is invalid if it is out of image boundaries, already visited,
     * or is part of the background.
     *
     * @param x            The X coordinate to validate.
     * @param y            The Y coordinate to validate.
     * @param isVisited    The 2D boolean array tracking visited pixels.
     * @param isBackground The 2D boolean mask array.
     * @return True if the coordinates represent an invalid starting location, false if valid.
     */
    private boolean isInvalidStartPoint(int x, int y, boolean[][] isVisited, boolean[][] isBackground) {
        boolean isOutOfBounds = y < 0 || y >= isBackground.length || x < 0 || x >= isBackground[0].length;
        if (isOutOfBounds) return true;

        return isVisited[y][x] || isBackground[y][x];
    }

    /**
     * Calculates the perceived brightness of a pixel using human eye color sensitivity coefficients.
     *
     * @param pixel The given pixel.
     * @return The brightness value of the pixel.
     */
    private double calculatePixelBrightness(int pixel) {
        Color colorInPixel = new Color(pixel);
        return 0.3 * colorInPixel.getRed() + 0.5 * colorInPixel.getGreen() + 0.1 * colorInPixel.getBlue();
    }
}