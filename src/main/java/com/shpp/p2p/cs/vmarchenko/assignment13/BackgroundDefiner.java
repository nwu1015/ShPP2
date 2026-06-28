package com.shpp.p2p.cs.vmarchenko.assignment13;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BackgroundDefiner {
    /**
     * Method for determining the background by Otsu method
     *
     * @param image        given picture
     * @param isBackground an array that collects information pixel
     *                     by pixel whether this pixel belongs to the background
     */
    public void defineBackground(BufferedImage image, boolean[][] isBackground) {
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
        int[] histogram = new int[Constants.HISTOGRAM_ARRAY_LENGTH];
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
     * Source: https://cutt.ly/Wt5eT1Ne
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
        int w = image.getWidth();
        int h = image.getHeight();
        double sum = 0;
        int count = 0;

        int[][] points = {
                {0, 0}, {w / 2, 0}, {w - 1, 0},
                {0, h / 2}, {w / 2, h / 2}, {w - 1, h / 2},
                {0, h - 1}, {w / 2, h - 1}, {w - 1, h - 1}
        };

        for (int[] p : points) {
            sum += calculatePixelBrightness(image.getRGB(p[0], p[1]));
            count++;
        }

        return (sum / count) > threshold;
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

                isBackground[y][x] = isBackgroundLight ?
                        (currentBrightness >= threshold) :
                        (currentBrightness <= threshold);
            }
        }
    }

    private double calculatePixelBrightness(int pixel) {
        Color colorInPixel = new Color(pixel);
        return Constants.RED_COMPONENT_LUMINANCE * colorInPixel.getRed() +
                Constants.GREEN_COMPONENT_LUMINANCE * colorInPixel.getGreen() +
                Constants.BLUE_COMPONENT_LUMINANCE * colorInPixel.getBlue();
    }
}
