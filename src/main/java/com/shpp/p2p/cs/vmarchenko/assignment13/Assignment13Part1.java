package com.shpp.p2p.cs.vmarchenko.assignment13;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.*;

public class Assignment13Part1 {
    /**
     * To determine the minimum silhouette size.
     * Required to avoid unnecessary waste
     */
    private static final double NOISE_TOLERANCE = 0.01;

    public static void main(String[] args) {
        Assignment13Part1 assignment = new Assignment13Part1();

        String imageName = "test.jpg";
        if(args.length > 0 && args[0] != null) {
            imageName = args[0];
        }

        BufferedImage image = assignment.getImageFromArgs(imageName);

        boolean[][] isBackground = new boolean[image.getHeight()][image.getWidth()];
        boolean[][] isVisited = new boolean[image.getHeight()][image.getWidth()];

        assignment.defineBackground1(image, isBackground);

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

            if(file.exists()) {
                image = ImageIO.read(file);
            }else {
                InputStream name = Assignment13Part1.class.getResourceAsStream("/" + imageName);

                if (name == null) {
                    throw new Exception("File doesn't exists: " + imageName);
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

    private void defineBackground1(BufferedImage image, boolean[][] isBackground){
        int[] histogram = new int[256];

        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {

                int pixel = image.getRGB(x, y);
                Color color = new Color(pixel);
                int brightness = (color.getRed() + color.getBlue() + color.getGreen()) / 3;

                histogram[brightness]++;
            }
        }

        int totalPixels = image.getHeight() * image.getWidth();
        int totalSum = 0;
        for(int i = 0; i < histogram.length; i++) {
            totalSum += i * histogram[i];
        }

        int wB = 0;
        double sumB = 0;

        double maximum = 0;
        int threshold = 0;

        for(int i = 0; i < histogram.length; i++) {
            wB += histogram[i];
            if (wB == 0){
                continue;
            }

            int wF = totalPixels - wB;
            if(wF == 0){
                break;
            }

            sumB += i * histogram[i];
            double sumF = totalSum - sumB;

            double mB = sumB / wB;
            double mF = sumF / wF;

            double varBetween = wB * wF * (mB - mF) * (mB - mF);

            if(varBetween > maximum) {
                maximum = varBetween;
                threshold = i;
            }
        }

        int upperLeftPixel = image.getRGB(0, 0);
        Color color = new Color(upperLeftPixel);
        int brightness = (color.getRed() + color.getGreen() + color.getBlue()) / 3;

        boolean isBackgroundLight = brightness > threshold;

        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {

                int pixel = image.getRGB(x, y);
                color = new Color(pixel);
                double currentBrightness = (color.getRed() + color.getBlue() + color.getGreen()) / 3.0;

                if(isBackgroundLight) {
                    isBackground[y][x] = currentBrightness >= threshold;
                }else{
                    isBackground[y][x] = currentBrightness <= threshold;
                }

            }
        }

    }

    private int runBFSAlgorithm(Node node, boolean[][] isVisited, boolean[][] isBackground) {
        int x = node.x();
        int y = node.y();

        if (y < 0 || y >= isBackground.length || x < 0 || x >= isBackground[0].length) {
            return 0;
        }

        if (isVisited[y][x] || isBackground[y][x]) {
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

            if (addNewPixelToQueue(currentX, currentY+1, isVisited, isBackground)) {
                uncheckedPixels.add(new Node(currentX, currentY+1));
            }

            if (addNewPixelToQueue(currentX, currentY-1, isVisited, isBackground)) {
                uncheckedPixels.add(new Node(currentX, currentY-1));
            }

            if (addNewPixelToQueue(currentX+1, currentY, isVisited, isBackground)) {
                uncheckedPixels.add(new Node(currentX+1, currentY));
            }

            if (addNewPixelToQueue(currentX-1, currentY, isVisited, isBackground)) {
                uncheckedPixels.add(new Node(currentX-1, currentY));
            }

        }
        return size;
    }

    private boolean addNewPixelToQueue(int x, int y, boolean[][] isVisited, boolean[][] isBackground) {
        if(y >= 0 && y < isBackground.length && x >= 0 && x < isBackground[0].length){
            if(!isBackground[y][x] && !isVisited[y][x]){
                isVisited[y][x] = true;
                return true;
            }
        }

        return false;
    }

}

record Node(int x, int y) {
}
