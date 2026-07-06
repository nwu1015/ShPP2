package com.shpp.p2p.cs.vmarchenko.assignment12;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Assignment 13: Silhouettes
 * Takes the file name as arguments and
 * determines how many separate silhouettes are in the image
 */
public class Assignment12Part1 {
    public static void main(String[] args) throws IOException {
        String imageName = Constants.DEFAULT_FILENAME;
        if (args.length > 0 && args[0] != null && !args[0].isBlank()) {
            imageName = args[0];
        }

        ImageLoader imageLoader = new ImageLoader();
        BufferedImage image = imageLoader.getImageFromArgs(imageName, Assignment12Part1.class);

        boolean[][] isBackground = new boolean[image.getHeight()][image.getWidth()];
        boolean[][] isVisited = new boolean[image.getHeight()][image.getWidth()];

        BackgroundDefiner backgroundDefiner = new BackgroundDefiner();
        backgroundDefiner.defineBackground(image, isBackground);

        SilhouetteFinder silhouetteFinder = new SilhouetteFinder();
        System.out.println(silhouetteFinder.findSilhouettes(image, isVisited, isBackground));
    }
}