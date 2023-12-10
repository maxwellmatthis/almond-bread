package almond;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class Main {
    // Settings
    // public final static double CENTER_X = -1;
    // public final static double CENTER_Y = -0.3;
    // public final static double RADIUS = 0.01;
    public final static double CENTER_X = 0;
    public final static double CENTER_Y = 0;
    public final static double RADIUS = 2;
    public final static int SIZE = 1500;
    public final static int MAX_ITERATIONS = 1000;

    // Computed
    public final static double DELTA = (2 * RADIUS) / (double) SIZE;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try {
            BufferedImage plot = Main.plot();
            Main.save(plot);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("Time: " + (System.currentTimeMillis() - start) + "ms");
    }

    private static BufferedImage plot() {
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < SIZE; y += 1) {
            for (int x = 0; x < SIZE; x += 1) {
                int iterations = Main.calcAlmondBreadValueAt(
                        new Complex(new Coordinate(x * DELTA + CENTER_X - RADIUS, y * DELTA + CENTER_Y - RADIUS)));
                int brightness = iterations < MAX_ITERATIONS ? (int) (255 * ((double) iterations / MAX_ITERATIONS)) : 0;
                Color color = new Color(brightness, (brightness * brightness) % 255,
                        (brightness * brightness * brightness) % 255);
                image.setRGB(x, y, color.getRGB());
            }
        }
        return image;
    }

    private static void save(BufferedImage image) throws IOException {
        File outputfile = new File("almond-bread.png");
        ImageIO.write(image, "png", outputfile);
    }

    private static int calcAlmondBreadValueAt(Complex c) {
        Complex z = new Complex(0);
        int i;
        for (i = 0; z.sqAbs() <= (2 * 2) && i < MAX_ITERATIONS; i++) {
            // z = z^2 + c
            z = z.squared().add(c);
        }
        return i;
    }
}
