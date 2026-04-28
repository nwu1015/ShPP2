package com.shpp.p2p.cs.vmarchenko.assignment12;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Assignment 12: Silhouettes
 * Takes the file name as arguments and
 * determines how many separate silhouettes are in the image
 *
 */

public class Assignment12Part1 {

    /**
     * How many points will be taken to determine the background
     */
    private static final int PIXELS_TO_DEFINE_BACKGROUND = 15;

    /**
     * How much can the average RGB value deviate
     */
    private static final double CALCULATION_MISTAKE = 10;

    /**
     * To determine the minimum silhouette size.
     * Required to avoid unnecessary waste
     */
    private static final double NOISE_TOLERANCE = 0.0005;

    public static void main(String[] args) {
        Assignment12Part1 assignment = new Assignment12Part1();

        String imageURL = "test.jpg";
        if(args.length > 0 && args[0] != null) {
            imageURL = args[0];
        }

        BufferedImage image = assignment.getImageFromArgs(imageURL);

        boolean[][] isBackground = new boolean[image.getHeight()][image.getWidth()];
        boolean[][] isVisited = new boolean[image.getHeight()][image.getWidth()];

        assignment.defineBackground(image, isBackground);

        System.out.println(assignment.findSilhouettes(image, isVisited, isBackground));
    }

    /**
     * It takes the file name and searches for that file in the resources' folder.
     *
     * @param imageURL file name
     * @return a file with the appropriate name
     */
    private BufferedImage getImageFromArgs(String imageURL) {
        try {
            File file = new File(imageURL);
            BufferedImage image;

            if(file.exists()) {
                image = ImageIO.read(file);
            }else {
                InputStream name = Assignment12Part1.class.getResourceAsStream("/" + imageURL);

                if (name == null) {
                    throw new Exception("File doesn't exists: " + imageURL);
                }

                image = ImageIO.read(name);
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
     * @param image a file with the appropriate name
     * @param isVisited an array that shows whether the pixel has already been visited
     * @param isBackground an array indicating whether a pixel belongs to the background
     * @return number of silhouettes found
     */
    private int findSilhouettes(BufferedImage image, boolean[][] isVisited, boolean[][] isBackground) {
        int silhouetteCounter = 0;
        double minSize = (image.getWidth() * image.getHeight()) * NOISE_TOLERANCE;

        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {

                if(!isBackground[y][x] && !isVisited[y][x]) {
                    int size = runDFSAlgorithm(x, y, isVisited, isBackground);

                    if (size > minSize) {
                        silhouetteCounter++;
                    }
                }
            }
        }

        return silhouetteCounter;
    }

    /**
     * A method for determining whether a pixel belongs to the background.
     * Random points are taken, and the most are compared.
     * Which color is the most - this is the background.
     *
     * @param image a file with the appropriate name
     * @param isBackground an array indicating whether a pixel belongs to the background
     */
    private void defineBackground(BufferedImage image, boolean[][] isBackground) {
        int number;
        int[] randomWidth = new int[PIXELS_TO_DEFINE_BACKGROUND];
        number = image.getWidth();
        fillTheArrayWithRandomNumbers(randomWidth, number);

        int[] randomHeight = new int[PIXELS_TO_DEFINE_BACKGROUND];
        number = image.getHeight();
        fillTheArrayWithRandomNumbers(randomHeight, number);

        double[] randomPixels = new double[PIXELS_TO_DEFINE_BACKGROUND];
        getRandomPixels(image, randomWidth, randomHeight, randomPixels);

        double backgroundIntensity = calculateIntensity(randomPixels);

        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {

                int pixel = image.getRGB(x, y);
                Color color = new Color(pixel);
                double currentPixelIntensity = (color.getRed() + color.getBlue() + color.getGreen()) / 3.0;

                isBackground[y][x] = Math.abs(currentPixelIntensity - backgroundIntensity) < CALCULATION_MISTAKE;

            }
        }
    }

    /**
     * Fills an array with random coordinates within a given range.
     * To increase the accuracy of background detection,
     * every fifth element of the array is set as a threshold value (0 or extreme point).
     *
     * @param array Array to fill with coordinates
     * @param number Upper limit of the range (width or height of the image), exclusively
     */
    private void fillTheArrayWithRandomNumbers(int[] array, int number){
        Random rand = new Random();
        for(int x = 0; x < array.length; x++){
            if(x % 5 == 0){
                if(x % 2 == 0){
                    array[x] = 0;
                }else {
                    array[x] = number - 1;
                }
            }else {
                array[x] = rand.nextInt(0, number);
            }
        }
    }

    /**
     * A method for sampling random pixels in an image.
     * The average RGB value is taken and written to the result.
     *
     * @param image a file with the appropriate name
     * @param arrayWidth An array of numbers that stores the width (like the x-coordinate)
     * @param arrayHeight An array of numbers that stores the height (like the y-coordinate)
     * @param result an array of numbers that stores the average RGB values
     */
    private void getRandomPixels(BufferedImage image, int[] arrayWidth, int[] arrayHeight, double[] result){
        if(arrayHeight.length != arrayWidth.length || arrayHeight.length != result.length){
            throw new IllegalArgumentException("Arrays must have the same length");
        }

        for(int y = 0; y < result.length; y++){
            int pixel = image.getRGB(arrayWidth[y], arrayHeight[y]);
            Color color = new Color(pixel);
            result[y] = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
        }
    }

    /**
     * Based on an array containing average RGB values,
     * it is determined which value occurs most frequently.
     * I took this approach, where you need to determine the largest value,
     * and with an error, as a ready-made solution.
     *
     * @param randomPixels an array of numbers that stores the average RGB values
     * @return the number that occurs most often (which will be the background color)
     */
    private double calculateIntensity(double[] randomPixels){
        return Arrays.stream(randomPixels)
                .boxed()
                .collect(Collectors.groupingBy(
                        val -> Math.round(val / CALCULATION_MISTAKE),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey() * CALCULATION_MISTAKE)
                .orElse(255.0);
    }

    /**
     * Recursively traverses all connected pixels of the object (silhouette).
     * It uses the DFS algorithm to calculate the total area of ​​the object in pixels.
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
}