package assignment;
/*

CS314H Programming Assignment 1 - Java image processing

Included is the Invert effect from the assignment.  Use this as an
example when writing the rest of your transformations.  For
convenience, you should place all of your transformations in this file.

You can compile everything that is needed with
javac -d bin src/assignment/*.java

You can run the program with
java -cp bin assignment.JIP

Please note that the above commands assume that you are in the prog1
directory.

*/

import java.util.ArrayList;

class InvertEffect extends ImageEffect {
    public int[][] apply(int[][] pixels,
                         ArrayList<ImageEffectParam> params) {
        int width = pixels[0].length;
        int height = pixels.length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[y][x] = ~pixels[y][x];
            }
        }
        return pixels;
    }

    public String getDescription() {
        return "Invert";
    }
}


class NoRed extends ImageEffect {
    /**
     * Removes the color red from the image.
     *@param pixels 2D array of the inputed image
     *@return pixels 2D array of inputed image without red
     */
    public int[][] apply(int[][] pixels, ArrayList<ImageEffectParam> params) {
        int width = pixels[0].length;
        int height = pixels.length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[y][x] = makePixel(0, getGreen(pixels[y][x]), getBlue(pixels[y][x]));
            }
        }				
        return pixels;
    }

    public String getDescription() {
        return "Removes red";
    }	
	
}

class NoGreen extends ImageEffect {
    /**
     * Removes the color green from the image.
     *@param pixels 2D array of the inputed image
     *@return pixels 2D array of inputed image without green
     */
    public int[][] apply(int[][] pixels, ArrayList<ImageEffectParam> params) {
        int width = pixels[0].length;
        int height = pixels.length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[y][x] = makePixel(getRed(pixels[y][x]), 0, getBlue(pixels[y][x]));
            }
        }				
        return pixels;
    }

    public String getDescription() {
        return "Removes green";
    }
	
}

class NoBlue extends ImageEffect {
    /**
     * Removes the color blue from the image.
     *@param pixels 2D array of the inputed image
     *@return pixels 2D array of inputed image without blue
     */
    public int[][] apply(int[][] pixels, ArrayList<ImageEffectParam> params) {
		int width = pixels[0].length;
        int height = pixels.length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
            	pixels[y][x] = makePixel(getRed(pixels[y][x]), getGreen(pixels[y][x]), 0);
            }
        }				
        return pixels;
    }

    public String getDescription() {
        return "Removes blue";
    }
	
}

class RedOnly extends ImageEffect {
    /**
     * Removes all the colors but red from the image.
     *@param pixels 2D array of the inputed image
     *@return pixels 2D array of inputed image with only red
     */
	public int[][] apply(int[][] pixels, ArrayList<ImageEffectParam> params) {
		int width = pixels[0].length;
        int height = pixels.length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
            	pixels[y][x] = makePixel(getRed(pixels[y][x]), 0, 0);
            }
        }				
		return pixels;
	}

	public String getDescription() {
		return "Red Only";
	}
	
}

class GreenOnly extends ImageEffect {
    /**
     * Removes all the colors but green from the image.
     *@param pixels 2D array of the inputed image
     *@return pixels 2D array of inputed image with only green
     */
	public int[][] apply(int[][] pixels, ArrayList<ImageEffectParam> params) {
		int width = pixels[0].length;
        int height = pixels.length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
            	pixels[y][x] = makePixel(0, getGreen(pixels[y][x]), 0);
            }
        }				
		return pixels;
	}

	public String getDescription() {
		return "Green Only";
	}
	
}

class BlueOnly extends ImageEffect {
    /**
     * Removes all the colors but blue from the image.
     *@param pixels 2D array of the inputed image
     *@return pixels 2D array of inputed image with only blue
     */
	public int[][] apply(int[][] pixels, ArrayList<ImageEffectParam> params) {
		int width = pixels[0].length;
        int height = pixels.length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
            	pixels[y][x] = makePixel(0, 0, getBlue(pixels[y][x]));
            }
        }				
		return pixels;
	}

	public String getDescription() {
		return "Blue Only";
	}
	
	
}

class BlackAndWhite extends ImageEffect {
    /**
     * Turns the image into black and white.
     *@param pixels 2D array of the inputed image
     *@return pixels 2D array of inputed image in grayscale
     */
	public int[][] apply(int[][] pixels, ArrayList<ImageEffectParam> params) {
		int width = pixels[0].length;
        int height = pixels.length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
            	int r = getRed(pixels[y][x]);
            	int g = getGreen(pixels[y][x]);
            	int b = getBlue(pixels[y][x]);
            	int avg = (r + g + b) / 3;
            	
            	pixels[y][x] = makePixel(avg, avg, avg);
            }
        }				
		return pixels;
	}
	
	public String getDescription() {
		return "Black and white";
	}
	
}

class HorizontalReflect extends ImageEffect {
    /**
     * Reflects the image over the horizontal axis.
     *@param pixels 2D array of the inputed image
     *@return pixels 2D array of inputed image reflected horizontally
     */
		public int[][] apply(int[][] pixels, ArrayList<ImageEffectParam> params) {
			int width = pixels[0].length;
	        int height = pixels.length;

	        for (int x = 0; x < width; x++) {
	            for (int y = 0; y < height / 2; y++) {
	            	int temp = pixels[height-y-1][x];	            	
	            	pixels[height-y-1][x] = pixels[y][x];
	            	pixels[y][x] = temp;
	            }
	        }				
			return pixels;
		}	
	
	public String getDescription() {
		return "Horizontal reflect";
	}	
	
}

class VerticalReflect extends ImageEffect {
    /**
     * Reflects the image over the vertical axis.
     *@param pixels 2D array of the inputed image
     *@return pixels 2D array of inputed image reflected vertically
     */
	public int[][] apply(int[][] pixels, ArrayList<ImageEffectParam> params) {
		int width = pixels[0].length;
        int height = pixels.length;

        for (int x = 0; x < width / 2; x++) {
            for (int y = 0; y < height; y++) {
            	int temp = pixels[y][width-x-1];	            	
            	pixels[y][width-x-1] = pixels[y][x];
            	pixels[y][x] = temp;
            }
        }				
		return pixels;
	}	

	public String getDescription() {
		return "Vertical reflect";
	}	

}

class Grow extends ImageEffect {
    /**
     * Doubles the size of the image
     *@param pixels 2D array of the inputed image
     *@return pixels 2D array of inputed image doubled in size
     */
	public int[][] apply(int[][] pixels, ArrayList<ImageEffectParam> params) {
		int width = pixels[0].length;
        int height = pixels.length;
        int[][] npixs = new int[height*2][width*2];
        int nx = 0;
        int ny = 0;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
            	npixs[ny][nx] = pixels[y][x];
            	npixs[ny][nx+1] = pixels[y][x];
            	npixs[ny+1][nx] = pixels[y][x];
            	npixs[ny+1][nx+1] = pixels[y][x];
            	ny += 2;
            }
            ny = 0;
            nx += 2;
        }				
		return npixs;
	}
	
	public String getDescription() {
		return "Grow";
	}	
}
class Shrink extends ImageEffect {
    /**
     * Halves the size of the image
     *@param pixels 2D array of the inputed image
     *@return pixels 2D array of inputed image halved in size
     */
	public int[][] apply(int[][] pixels, ArrayList<ImageEffectParam> params) {
		int width = pixels[0].length;
        int height = pixels.length;
        int[][] npixs = new int[height / 2][width / 2];
        int x = 0;
        int y = 0;
        
        for (int nx = 0; nx < width / 2; nx++) {
            for (int ny = 0; ny < height / 2; ny++) {
                int r = (getRed(pixels[y][x]) + getRed(pixels[y+1][x]) + 
                        getRed(pixels[y][x+1]) + getRed(pixels[y+1][x+1])) / 4;
                int g = (getGreen(pixels[y][x]) + getGreen(pixels[y+1][x]) + 
                        getGreen(pixels[y][x+1]) + getGreen(pixels[y+1][x+1])) / 4;
                int b = (getBlue(pixels[y][x]) + getBlue(pixels[y+1][x]) + 
                        getBlue(pixels[y][x+1]) + getBlue(pixels[y+1][x+1])) / 4;
            	npixs[ny][nx] = makePixel(r, g , b);
            	y += 2;
            }
            y = 0;
            x += 2;
        }				
		return npixs;
	}
	
	public String getDescription() {
		return "Shrink";
	}	

}
	
	class Threshold extends ImageEffect {
	    /**
	     * Reduces the image to each color being either fully on or off,
	     * which is split by the given threshold.
	     *@param pixels 2D array of the inputed image
	     *@param params ArrayList of the inputed parameter, containing the threshold value
	     *@return pixels 2D array of inputed image in extreme colors
	     */
	    public Threshold() {
	        super();
	        params = new ArrayList<ImageEffectParam>();
	        params.add(new ImageEffectIntParam("Threshold",
	                                           "Threshold of splitting colors",
	                                           127, 0, 255));
	    }

	    public int[][] apply(int[][] pixels,
	                         ArrayList<ImageEffectParam> params) {
	        ImageEffectIntParam threshold = (ImageEffectIntParam)params.get(0);
	        int thres = threshold.getValue();
	        int width = pixels[0].length;
	        int height = pixels.length;

	        for (int x = 0; x < width; x++) {
	            for (int y = 0; y < height; y++) {
	            	int r = 0;
	            	if(getRed(pixels[y][x]) > thres)
	            		r = 255;
	            	int g = 0;
	            	if(getGreen(pixels[y][x]) > thres)
	            		g = 255;
	            	int b = 0;
	            	if(getBlue(pixels[y][x]) > thres)
	            		b = 255;
	            	pixels[y][x] = makePixel(r, g, b);
	            }
	        }	
	        return pixels;
	    }

	    public String getDescription() {
	        return "Threshold";
	    }
	    
	}

class Smooth extends ImageEffect {
    /**
     * Smoothes out the image by setting a 4x4 grid of pixels to their average color.
     *@param pixels 2D array of the inputed image
     *@return pixels 2D array of inputed image smoothed
     */
    public int[][] apply(int[][] pixels, ArrayList<ImageEffectParam> params) {
        int width = pixels[0].length;
        int height = pixels.length;
        int[][] npixs = new int[height][width];
        
        for (int x = 0; x < width; x+=4) {
            for (int y = 0; y < height; y+=4) {
                ArrayList<Integer> rgb = getRGB(pixels, x, y);   
                for (int nx = x; nx < x+4; nx++) {
                    for (int ny = y; ny < y+4; ny++) {
                npixs[ny][nx] = makePixel(rgb.get(0), rgb.get(1) , rgb.get(2));
                    }
                }
            }
        }               
        return npixs;
    }
    /**
     * Returns the sum of the RGB values in a given image in a 4x4 grid starting at x,y.
     *@param pixels 2D array of the inputed image
     *@param x starting index for 4x4 grid in horizontal direction
     *@param y starting index for 4x4 grid in vertical direction
     *@return RGB ArrayList of the 3 sums for Red, Green, and Blue
     */
    public ArrayList<Integer> getRGB(int[][] pixels, int x, int y){
        ArrayList<Integer> RGB=new ArrayList<Integer>();
        ArrayList<Integer> grid=new ArrayList<Integer>();
        for (int nx = x; nx < x+4; nx++) {
            for (int ny = y; ny < y+4; ny++) {
                grid.add(pixels[ny][nx]);
            }        
        }
        int r = 0;
        int g = 0;
        int b = 0;
        for(int i = 0; i < grid.size(); i++){
            r+=getRed(grid.get(i));
            g+=getGreen(grid.get(i));
            b+=getBlue(grid.get(i));
        }
        RGB.add(r/grid.size());
        RGB.add(g/grid.size());
        RGB.add(b/grid.size());
        return RGB;
    }
    
    public String getDescription() {
        return "Smooth";
    }   

}	



