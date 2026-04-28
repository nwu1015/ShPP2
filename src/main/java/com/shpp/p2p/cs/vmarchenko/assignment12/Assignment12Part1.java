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

public class Assignment12Part1 {

    private static final int PIXELS_TO_DEFINE_BACKGROUND = 50;
    private static final double CALCULATION_MISTAKE = 10;

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

    private BufferedImage getImageFromArgs(String imageURL) {
        try {
            File file = new File(imageURL);
            BufferedImage image;

            if(file.exists()) {
                image = ImageIO.read(file);
            }else {
                InputStream var = Assignment12Part1.class.getResourceAsStream("/" + imageURL);

                if (var == null) {
                    throw new Exception("File doesn't exists: " + imageURL);
                }

                image = ImageIO.read(var);
            }

            return image;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int findSilhouettes(BufferedImage image, boolean[][] isVisited, boolean[][] isBackground) {
        int silhouetteCounter = 0;
        double minSize = (image.getWidth() * image.getHeight()) * 0.01;

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