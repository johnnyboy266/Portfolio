package assignment;
import java.awt.image.*;
import java.util.ArrayList;

/**
 * Abstract superclass for classes implementing single-source/single-destination
 * transformations of 24-bit RGB images.
 * <p>
 * This class is primarily concerned with manipulating images stored as
 * two-dimensional arrays of pixel information, i.e.
 * <code>int[][] pixels</code>.  Such arrays conform to the following
 * specifications:
 * <ul>
 * <li> Pixel information is stored in row-column order, thus the first array
 * index specifies a row in an image and the second array index specifies a
 * column in an image.  The upper-left pixel of an image is row 0, column 0.
 * </li>
 * <li> The width of an image (number of columns) is the length of the second
 * array dimension, e.g. <code>pixels[0].length</code>, and the height of an
 * image (number of rows) is the length of the first array dimension, e.g.
 * <code>pixels.length</code>.</li>
 * <li> Each pixel is a 24-bit RGB value packed in a 32-bit <code>int</code>.
 * The red, blue, and green components of a pixel may be retrieved via the
 * convenience methods {@link #getRed getRed}, {@link #getGreen getGreen}, and
 * {@link #getBlue getBlue}.  The packed <code>int</code> representation for a
 * pixel with specific red, blue, and green components may be constructed via
 * the convenience method {@link #makePixel makePixel}.</li>
 * </ul>
 */
public abstract class ImageEffect {

    // List of parameters required to apply the effect on the image.
    // This list should be initialized in the constructor of the class
    // which implements this abstract class.
    protected ArrayList<ImageEffectParam> params = null;

    /**
     * Applys the effect to image data.
     *
     * @param pixels the source two-dimensional array of image data.
     *
     * @param params parameters required to apply the effect.
     *
     * @return    a two-dimensional array of image data that is the
     *            result of applying the effect to the source image.
     */
    public abstract int[][] apply(int[][] pixels,
                                  ArrayList<ImageEffectParam> params);

    /**
     * Returns a String qualitatively describing the effect.
     *
     * @return    a String describing the effect.
     */
    public abstract String getDescription();

    /**
     * Returns the parameters needed to apply the effect.
     *
     * @return a list of parameters required to apply this effect.
     */
    public ArrayList<ImageEffectParam> getParameters() {
        return params;
    }

    /**
     * Extracts the red component of a pixel.
     *
     * @param pixel    an integer pixel
     *
     * @return    the red component [0-255] of the pixel.
     */
    public static int getRed(int pixel) {
        return pixel >> 16 & 0xff;
    }

    /**
     * Extracts the green component of a pixel.
     *
     * @param pixel    an integer pixel
     *
     * @return    the green component [0-255] of the pixel.
     */
    public static int getGreen(int pixel) {
        return pixel >> 8 & 0xff;
    }

    /**
     * Extracts the blue component of a pixel.
     *
     * @param pixel    an integer pixel
     *
     * @return    the blue component [0-255] of the pixel.
     */
    public static int getBlue(int pixel) {
        return pixel & 0xff;
    }

    /**
     * Constructs a pixel from RGB components.
     *
     * @param red    the red component [0-255]
     * @param green    the green component [0-255]
     * @param blue    the blue component [0-255]
     *
     * @return    the packed integer pixel.
     */
    public static int makePixel(int red, int green, int blue) {
        return (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
    }

    /**
     * Applies the effect to an image.
     *
     * @param image    the source BufferedImage
     *
     * @param params parameters required to apply the effect.
     *
     * @return    a BufferedImage that is the result of applying the
     *            effect to the source image.
     */
    public BufferedImage apply(BufferedImage image,
                               ArrayList<ImageEffectParam> params) {
        return pixelsToImage(apply(imageToPixels(image), params));
    }

    /**
     * Converts a two-dimensional array of image data to a BufferedImage.
     *
     * @param pixels    the source two-dimensional array of image data
     *
     * @return    a BufferedImage representing the source image.
     *
     * @throws    IllegalArgumentException if the source image is
     *           ill-defined.
     */
    public static BufferedImage pixelsToImage(int[][] pixels) throws IllegalArgumentException {
        if (pixels == null) {
            throw new IllegalArgumentException();
        }

        int width = pixels[0].length;
        int height = pixels.length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < height; row++) {
            image.setRGB(0, row, width, 1, pixels[row], 0, width);
        }
        return image;
    }

    /**
     * Converts a BufferedImage to a two-dimensional array of image data.
     *
     * @param image    the source BufferedImage
     *
     * @return    a two-dimensional array of image data representing the
     *            source image
     *
     * @throws    IllegalArgumentException if the source image is
     *           ill-defined.
     */
    public static int[][] imageToPixels(BufferedImage image) throws IllegalArgumentException {
        if (image == null) {
            throw new IllegalArgumentException();
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int[][] pixels = new int[height][width];
        for (int row = 0; row < height; row++) {
            image.getRGB(0, row, width, 1, pixels[row], 0, width);
        }
        return pixels;
    }
}
