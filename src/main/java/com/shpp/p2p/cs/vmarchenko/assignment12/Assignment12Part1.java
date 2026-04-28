package com.shpp.p2p.cs.vmarchenko.assignment12;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

public class Assignment12Part1 {
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


        int silhouetteCounter = 0;
        double minSize = (image.getWidth() * image.getHeight()) * 0.01;

        for(int y = 0; y < image.getHeight(); ++y) {
            for(int x = 0; x < image.getWidth(); ++x) {

                if(!isBackground[y][x] && !isVisited[y][x]) {
                    int size = assignment.runDFSAlgorithm(x, y, isVisited, isBackground);

                    if (size > minSize) {
                        silhouetteCounter++;
                    }
                }


            }
        }

        System.out.println(silhouetteCounter);


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

    private int findSilhouettes(BufferedImage image) {

        return 0;
    }

    private void defineBackground(BufferedImage image, boolean[][] isBackground) {
        for(int y = 0; y < image.getHeight(); ++y) {
            for(int x = 0; x < image.getWidth(); ++x) {

                int pixel = image.getRGB(x, y);
                Color color = new Color(pixel);
                double averageNumber = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
                if(averageNumber > 222.0) {
                    isBackground[y][x] = true;
                }

            }
        }
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
