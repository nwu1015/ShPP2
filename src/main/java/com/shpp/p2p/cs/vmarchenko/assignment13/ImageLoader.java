package com.shpp.p2p.cs.vmarchenko.assignment13;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {
    /**
     * It takes the file name and searches for that file in the resources' folder.
     *
     * @param imageName file name
     * @return a file with the appropriate name
     */
    public BufferedImage getImageFromArgs(String imageName, Class<?> resourceClass) throws IOException {
        try {
            File file = new File(imageName);

            if (file.exists()) {
                return ImageIO.read(file);
            }

            try (InputStream name = resourceClass.getResourceAsStream("/" + imageName)) {
                if (name == null) {
                    throw new FileNotFoundException("File doesn't exists: " + imageName);
                }
                return ImageIO.read(name);
            }

        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
