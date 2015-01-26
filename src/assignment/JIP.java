package assignment;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;


/**
  * This class provides a GUI interface and handles the layer mechanism.
  */
public class JIP extends JFrame implements ActionListener {
    int[][] display = null; // matrix of pixels that are currently displayed
    ArrayList<int[][]> pixels = new ArrayList<int[][]>(); // list of the layers' pixels
    ArrayList<Point> layerCorners = new ArrayList<Point>(); // list of points for location of layers' top left corner
    int currLayer = -1; // the current layer selected; index of pixels and layerCorners
    int maxHeight = 0; // the height of the tallest layer
    int maxWidth = 0; // the width of the widest layer
    
    public static void main(String[] args) {
        try {
            // Set cross-platform Java L&F 
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
    } 
    catch (UnsupportedLookAndFeelException e) {
       // handle exception
    }
    catch (ClassNotFoundException e) {
       // handle exception
    }
    catch (InstantiationException e) {
       // handle exception
    }
    catch (IllegalAccessException e) {
       // handle exception
    }
        (new JIP()).setVisible(true);        
    }

    public JIP() {
        effectMap = new HashMap<>();
        setupGUI();
    }

    protected void setupGUI() {
        // setup Frame elements
        setTitle("Java Image Processor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        // file menu for opening a new image, saving it, and exiting
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);
        menuItemOpenImage = new JMenuItem("Open image...");
        menuItemOpenImage.setMnemonic(KeyEvent.VK_O);
        menuItemOpenImage.addActionListener(this);
        menu.add(menuItemOpenImage);
        menuItemSaveImage = new JMenuItem("Save image...");
        menuItemSaveImage.setMnemonic(KeyEvent.VK_S);
        menuItemSaveImage.addActionListener(this);
        menu.add(menuItemSaveImage);
        menuItemExit = new JMenuItem("Exit");
        menuItemExit.setMnemonic(KeyEvent.VK_X);
        menuItemExit.addActionListener(this);
        menu.add(menuItemExit);
        // edit menu for performing actions, currently only moving a layer
        editMenu = new JMenu("Edit");
        menuBar.add(editMenu);
        menuItemMove = new JMenuItem("Move a layer");
        menuItemMove.addActionListener(this);
        editMenu.add(menuItemMove);
        menuItemRotate = new JMenuItem("Rotate a layer");
        menuItemRotate.addActionListener(this);
        editMenu.add(menuItemRotate);
        // effects menu for applying any special effect or filter
        effectsMenu = new JMenu("Effects");
        effectsMenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(effectsMenu);
        // layers menu for opening, closing, and selecting a layer
        JMenu layersMenu = new JMenu("Layers");
        menuItemOpenLayer = new JMenuItem("Open Layer...");
        menuItemOpenLayer.addActionListener(this);
        layersMenu.add(menuItemOpenLayer);
        menuItemCloseLayer = new JMenuItem("Close Selected Layer");
        menuItemCloseLayer.addActionListener(this);
        layersMenu.add(menuItemCloseLayer);
        selectLayerMenu = new JMenu("Select Layer...");
        layersMenu.add(selectLayerMenu);
        menuBar.add(layersMenu);

        // add known ImageEffects to effects menu
        for(ImageEffect ie : getFilters().values()) {
            addEffect(ie);
        }

        // add component to display image
        imagePanel = new JImagePanel();
        getContentPane().add(imagePanel);
        pack();

        // setup open file dialog
        String[] formats = ImageIO.getReaderFormatNames();
        int unique = 0, remaining = formats.length;
openOuter:
        for (; unique < remaining; unique++) {
            formats[unique] = formats[unique].toLowerCase();
            for (int j = 0; j < unique; j++) {
                if (formats[j].equals(formats[unique])) {
                    formats[unique--] = formats[--remaining];
                    continue openOuter;
                }
            }
        }
        Arrays.sort(formats, 0, unique);
        final String[] fReadableFormats = formats;
        final int fRUnique = unique;

        StringBuffer desc = new StringBuffer("Image files  [");
        if (fRUnique == 0) {
            desc.append("N/A");
        } else {
            desc.append(fReadableFormats[0].toUpperCase());
            for (int i = 1; i < fRUnique; i++) {
                desc.append(", " + fReadableFormats[i].toUpperCase());
            }
        }
        desc.append(']');
        final String fDesc = desc.toString();

        openFileChooser = new JFileChooser(".");
        openFileChooser.setDialogTitle("Open Image");
        openFileChooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory()) {
                     return true;
                }

                String filenameExtension = getExtension(file).toLowerCase();
                for (int i = 0; i < fRUnique; i++) {
                    if (filenameExtension.equals(fReadableFormats[i])) {
                        return true;
                    }
                }
                return false;
            }
            public String getDescription() {
                return fDesc;
            }
        });

        // setup save file dialog
        formats = ImageIO.getWriterFormatNames();
        unique = 0;
        remaining = formats.length;
saveOuter:
        for (; unique < remaining; unique++) {
            formats[unique] = formats[unique].toLowerCase();
            for (int j = 0; j < unique; j++) {
                if (formats[j].equals(formats[unique])) {
                    formats[unique--] = formats[--remaining];
                    continue saveOuter;
                }
            }
        }
        Arrays.sort(formats, 0, unique);
        writableFormats = new String[unique];
        for (int i = 0; i < unique; i++) {
            writableFormats[i] = formats[i];
        }
        final String[] fWritableFormats = writableFormats;
        final int fWUnique = unique;

        saveFileChooser = new JFileChooser(".");
        saveFileChooser.setDialogTitle("Save Image");
        saveFileChooser.setAcceptAllFileFilterUsed(false);
        for (int i = 0; i < fWUnique; i++) {
            final int j = i;
            saveFileChooser.addChoosableFileFilter(new FileFilter() {
                public boolean accept(File file) {
                    if (file.isDirectory()) {
                        return true;
                    }

                    String filenameExtension = getExtension(file).toLowerCase();
                    if (filenameExtension.equals(fWritableFormats[j])) {
                        return true;
                    }
                    return false;
                }
                public String getDescription() {
                    return fWritableFormats[j].toUpperCase();
                }
            });
        }
        
    }

    // adds all the image effects to the effects menu
    public void addEffect(ImageEffect ie) {
        JMenuItem menuItem = new JMenuItem(ie.getDescription());
        menuItem.addActionListener(this);

        int pos = 0;
        try {
            while (pos < effectsMenu.getMenuComponentCount() && ie.getDescription().compareTo(((JMenuItem)effectsMenu.getMenuComponent(pos)).getText()) >= 0) {
                    pos++;
            }
        } catch (ClassCastException e) {
        }

        effectsMenu.add(menuItem, pos);
        effectMap.put(menuItem, ie);
    }

    // calls the appropriate prompt based on the action
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuItemOpenImage) {
            promptOpenImage();
            repaint();
        } else if (e.getSource() == menuItemSaveImage) {
            promptSaveImage();
        } else if (e.getSource() == menuItemExit) {
            System.exit(0);
        }else if(e.getSource() == menuItemOpenLayer){
            promptOpenLayer();
            repaint();
        }else if(e.getSource() == menuItemCloseLayer){
            promptCloseLayer();
            repaint();
        }else if(e.getSource() == menuItemMove){
            // takes in input of how far to move horizontally and vertically
            Object inputX = JOptionPane.showInputDialog(null,
                    "Horizontal: ", "Number of pixels to move from top left corner of image",
                    JOptionPane.INFORMATION_MESSAGE,
                    null, null, 0);
            Object inputY = JOptionPane.showInputDialog(null,
                    "Vertical: ", "Number of pixels to move from top left corner of image",
                    JOptionPane.INFORMATION_MESSAGE,
                    null, null, 0);
            int x, y;
            try {
                x = Integer.parseInt(inputX.toString());
            } catch (NumberFormatException ex) {
                showErrorMessage("Invalid integer format.");
                return;
            }
            try {
                y = Integer.parseInt(inputY.toString());
            } catch (NumberFormatException ex) {
                showErrorMessage("Invalid integer format.");
                return;
            }
            promptTranslateLayer(x, y);
            repaint();
        }else if(e.getSource() == menuItemRotate){
            Object input = JOptionPane.showInputDialog(null,
                    "Degrees (0 - 360): ", "Rotate",
                    JOptionPane.INFORMATION_MESSAGE,
                    null, null, 0);
            int deg;
            try{
                deg = Integer.parseInt(input.toString());
            }catch(NumberFormatException ex){
                showErrorMessage("Invalid integer format.");
                return;
            }
            if(deg < 0 || deg > 360){
                showErrorMessage("Invalid degree value.");
                return;
            }else if(deg == 0 || deg == 360){
                return;
            }
            while(deg > 90){
                promptRotateLayer(90);
                deg -= 90;
            }
            promptRotateLayer(deg);
            repaint();
        }else{
            for(int i = 0; i < selectLayerMenu.getItemCount(); i++){
                if(e.getSource() == selectLayerMenu.getItem(i)){
                    promptChangeLayer(i);
                    return;
                }
            }
            ImageEffect effect = (ImageEffect) effectMap.get(e.getSource());
            ArrayList<ImageEffectParam> params = effect.getParameters();
            if (params != null) {
                for (ImageEffectParam param : params) {
                    Object input = null;
                    do {
                        input = JOptionPane.showInputDialog(null,
                            param.getDescription(), param.getName(),
                            JOptionPane.INFORMATION_MESSAGE,
                            null, null, param.getDefaultValue());
                    } while(!parseAndVerifyInput(input.toString(),
                                                  param));
                }
            }
            BufferedImage img = ImageEffect.pixelsToImage(pixels.get(currLayer));
            removeLayer(ImageEffect.imageToPixels(img), layerCorners.get(currLayer));
            if (effect != null && img  != null) {
                BufferedImage newImg = effect.apply(img, params);
                int[][] layer = ImageEffect.imageToPixels(newImg);
                pixels.set(currLayer, layer);                
                insertLayer(layer, layerCorners.get(currLayer));
                imagePanel.setBackgroundImage(ImageEffect.pixelsToImage(display));
                if (newImg != null && (newImg.getWidth() != img.getWidth() || newImg.getHeight() != img.getHeight())) 
                    pack();
                repaint();
                
            }
        }
    }

    // remove layer from list of layers and from display
    private void promptCloseLayer() {        
        int[][] layer = pixels.get(currLayer);
        Point location = layerCorners.get(currLayer);
        if(!pixels.isEmpty()){
            removeLayer(layer, location);
            currLayer--;        
            selectLayerMenu.remove(currLayer);
        }else{
            display = pixels.get(0);
            currLayer = -1;
            selectLayerMenu.remove(0);
        }
        pixels.remove(currLayer);
        layerCorners.remove(currLayer);
        imagePanel.setBackgroundImage(ImageEffect.pixelsToImage(display));
    }

    // removes the given layer from the display
    private void removeLayer(int[][] layer, Point location){
        for(int i = location.y; i < layer.length + location.y; i++){
            for(int j = location.x; j < layer[0].length + location.x; j++){
                        for(int x = pixels.size() - 1; x > -1; x--){
                            int[][] underLayer = pixels.get(x);
                            Point underLoc = layerCorners.get(x);
                            if(x != currLayer && i >= underLoc.y && j >= underLoc.x && i < underLayer.length + underLoc.y && j < underLayer[0].length + underLoc.x){
                                int pix = underLayer[i - underLoc.y][j - underLoc.x];
                                if(pix != 0){
                                    display[i][j] = pix;
                                    break;
                                }
                            }
                }
            }
        }            
    }
    
    //inserts the given layer into the display; assumes it is the currLayer
    private void insertLayer(int[][] layer, Point location){
        if(location.x + layer[0].length > maxWidth || location.y + layer.length > maxHeight){
            maxWidth = Math.max(maxWidth, location.x + layer[0].length);
            maxHeight = Math.max(maxHeight, location.y + layer.length);
            int[][] newDisplay = new int[maxHeight][maxWidth];
            int[][] background = background(maxHeight, maxWidth);
            pixels.set(0, background);
            for(int i = 0; i < newDisplay.length; i++){
                for(int j = 0; j < newDisplay[i].length; j++){
                    if(i < display.length && j < display[0].length){
                        newDisplay[i][j] = display[i][j];
                    }else{
                        newDisplay[i][j] = background[i][j];
                    }
                }
            }
            display = newDisplay;
        }
        for(int i = location.y; i < location.y + layer.length; i++){
            for(int j = location.x; j < location.x + layer[0].length; j++){
                if(pixels.size() - 1 == currLayer){
                    int pix = layer[i - location.y][j - location.x];
                    if(pix != 0){
                        display[i][j] = pix;
                    }else{
                        for(int k = pixels.size() - 2; k >= 0; k--){
                            int[][] lay = pixels.get(k);
                            Point pt = layerCorners.get(k);
                            if(i > pt.y && j > pt.x && i < lay.length && j < lay[0].length){
                                display[i][j] = lay[i - pt.y][j - pt.x];
                                break;
                            }
                        }
                    }
                }else{
                    boolean change = true;
                    for(int k = pixels.size() - 1; k > currLayer; k--){
                        int[][] lay = pixels.get(k);
                        Point pt = layerCorners.get(k);                        
                        if(i >= pt.y && j >= pt.x && i < lay.length + pt.y && j < lay[0].length + pt.x){
                            int pix = lay[i - pt.y][j - pt.x];
                            if(pix != 0){
                                change = false;
                            }
                        }
                    }
                    if(change){
                        int pix = layer[i - location.y][j - location.x];
                        if(pix != 0){
                            display[i][j] = pix;
                        }
                    }
                }
            }
        }
    }
    
    private int[][] background(int h, int w){
        int[][] back = new int[h][w];
        for(int i = 0; i < h; i++){
            for(int j = 0; j < w; j++){
                    back[i][j] = ImageEffect.makePixel(127, 127, 127);
                 
            }
        }
        return back;
    }
    
    public void promptOpenImage() {
        int action = openFileChooser.showOpenDialog(this);
        if (action != JFileChooser.APPROVE_OPTION) {
            return;
        }
        BufferedImage img = createBufferedImage(openFileChooser.getSelectedFile());
        if (img == null) {
            JOptionPane.showMessageDialog(this, "Error opening " + openFileChooser.getSelectedFile(), "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            imagePanel.setBackgroundImage(img);
            JMenuItem layer1 = new JMenuItem("Layer 1");
            layer1.addActionListener(this);
            selectLayerMenu.removeAll();
            selectLayerMenu.add(layer1);
            pack();
            display = ImageEffect.imageToPixels(img);
            pixels.clear();
            maxHeight = display.length;
            maxWidth = display[0].length;
            pixels.add(background(maxHeight, maxWidth));
            int[][] clone = new int[maxHeight][maxWidth];
            for(int i = 0; i < display.length; i++){
                clone[i] = display[i].clone();
            }
            display[0][0] = 0;
            pixels.add(clone);
            currLayer = 1;
            layerCorners.clear();
            //for both background and new image
            layerCorners.add(new Point(0, 0));
            layerCorners.add(new Point(0, 0));
        }
        }
        
        public void promptOpenLayer() {
            int action = openFileChooser.showOpenDialog(this);
            if (action != JFileChooser.APPROVE_OPTION) {
                return;
            }
            BufferedImage img = createBufferedImage(openFileChooser.getSelectedFile());
            if (img == null) {
                JOptionPane.showMessageDialog(this, "Error opening " + openFileChooser.getSelectedFile(), "Error", JOptionPane.ERROR_MESSAGE);
            } else {               
                if(display == null){
                    imagePanel.setBackgroundImage(img);
                    JMenuItem layer1 = new JMenuItem("Layer 1");
                    layer1.addActionListener(this);
                    selectLayerMenu.add(layer1);
                    pack();
                    display = ImageEffect.imageToPixels(img);
                    maxHeight = display.length;
                    maxWidth = display[0].length;
                    pixels.add(background(maxHeight, maxWidth));
                    pixels.add(display.clone());
                    currLayer = 1;
                    //for both background and new image
                    layerCorners.add(new Point(0, 0));
                    layerCorners.add(new Point(0, 0));
                }else{
                    currLayer++;
                    int[][] layer = ImageEffect.imageToPixels(img);
                    if(layer.length > display.length || layer[0].length > display[0].length){
                        maxHeight = Math.max(maxHeight, layer.length);
                        maxWidth = Math.max(maxWidth, layer[0].length);
                        pixels.remove(0);
                        pixels.add(0, background(maxHeight, maxWidth));
                        int[][] newDisplay = new int[maxHeight][maxWidth];
                        for(int i = 0; i < newDisplay.length; i++){
                            for(int j = 0; j < newDisplay[0].length; j++){
                                if(i >= display.length || j >= display[0].length){
                                    newDisplay[i][j] = pixels.get(0)[i][j];
                                }else{
                                    newDisplay[i][j] = display[i][j];
                                }
                            }
                        }
                        display = newDisplay;
                    }
                    for(int i = 0; i < layer.length; i++){
                        for(int j = 0; j < layer[0].length; j++){
                            display[i][j] = layer[i][j];
                        }
                    }
                    imagePanel.setBackgroundImage(ImageEffect.pixelsToImage(display));
                    JMenuItem nextLayer = new JMenuItem("Layer " + currLayer);
                    nextLayer.addActionListener(this);
                    selectLayerMenu.add(nextLayer);
                    pack();                   
                    pixels.add(layer);
                    layerCorners.add(new Point(0, 0));
                }
            }
    }
        
    public void promptChangeLayer(int index){
        currLayer = index + 1;
    }

    public void promptSaveImage() {
        int action = saveFileChooser.showSaveDialog(this);
        if (action != JFileChooser.APPROVE_OPTION || imagePanel.getBackgroundImage() == null) {
            return;
        }
        currLayer = 1;
        pixels.clear();
        pixels.add(background(maxHeight, maxWidth));
        pixels.add(display.clone());
        JMenuItem layer1 = new JMenuItem("Layer 1");
        layer1.addActionListener(this);
        selectLayerMenu.removeAll();
        selectLayerMenu.add(layer1);
        layerCorners.clear();
        layerCorners.add(new Point(0, 0));
        File file = saveFileChooser.getSelectedFile();
        String format = getExtension(file).toLowerCase();
        int i = 0;
        for (; i < writableFormats.length; i++) {
            if (format.equals(writableFormats[i])) {
                break;
            }
        }
        if (i == writableFormats.length) {
            format = saveFileChooser.getFileFilter().getDescription().toLowerCase();
            file = new File(saveFileChooser.getCurrentDirectory(), file.getName() + "." + format);
        }

        try {
            if (!ImageIO.write((BufferedImage)imagePanel.getBackgroundImage(), format, file)) {
                JOptionPane.showMessageDialog(this, "Error saving " + file, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving " + file, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void promptTranslateLayer(int x, int y){
        int[][] layer = pixels.get(currLayer);
        double[][] transform = { {1, 0, x}, {0, 1, y}, {0, 0, 1} };
        Point location = layerCorners.get(currLayer);
        if(location.x + x < 0 || location.y + y < 0){
            throw new IllegalArgumentException("You cannot move a layer past (0, 0)");
        }
        double[] points = {location.x, location.y, 1};
        double[] newPoints = matrixMulti(transform, points);
        removeLayer(layer, location);
        location = new Point((int)newPoints[0], (int)newPoints[1]);
        layerCorners.set(currLayer, location);        
        insertLayer(layer, location);
        imagePanel.setBackgroundImage(ImageEffect.pixelsToImage(display));
    }
    
    public void promptRotateLayer(int degrees){
        int[][] layer = pixels.get(currLayer);
        double tan2 = Math.tan(degrees / 2.0 * Math.PI / 180.0);
        double sin = Math.sin(degrees * Math.PI / 180.0);
        double cos = Math.cos(degrees * Math.PI / 180.0);
        //rotation matrix
        double[][] transform1 = { {1, -tan2}, {0 , 1} };
        double[][] transform2 = { {1, 0}, {sin, 1} };
        Point location = layerCorners.get(currLayer);
        double[] points = {location.x, location.y};
        //remove current layer from display
        removeLayer(layer, location);
        //get new rotated corner point
        double[] newPoints = matrixMulti(transform1, points);
        newPoints = matrixMulti(transform2, points);
        newPoints = matrixMulti(transform1, newPoints);
        location = new Point((int)Math.round(newPoints[0]), (int)Math.round(newPoints[1]));
        //if one of points is negative, have to shift over display and corners by negative value
        int xShift = (int) points[0] + location.x;
        int yShift = (int) points[1] + location.y;
        if(xShift < 0 || yShift < 0){            
            if(xShift > 0){
                xShift = 0;
            }else{
                xShift = -xShift;
            }
            if(yShift > 0){
                yShift = 0;
            }else{
                yShift = -yShift;
            }
            for(int i = 0; i < layerCorners.size(); i++){
                if(i != currLayer){
                    Point corner = layerCorners.get(i);
                    corner.x = corner.x + xShift;
                    corner.y = corner.y + yShift;
                    layerCorners.set(i, corner);
                }
                
            }
            maxWidth += xShift;
            maxHeight += yShift;
            int[][] background = background(maxHeight, maxWidth);
            pixels.set(0, background);
            //create new display that is shifted
            int[][] newDisplay = new int[maxHeight][maxWidth];   
            for(int i = 0; i < newDisplay.length; i++){
                for(int j = 0; j < newDisplay[0].length; j++){
                    if(i < yShift || j < xShift){
                        newDisplay[i][j] = background[i][j];
                    }else{
                        newDisplay[i][j] = display[i - yShift][j - xShift];
                    }
                }
            }
            display = newDisplay;
        }
        //check if the other 3 vertices are negative
        //pointShift is how far negative a point may be
        int[] pointShift1 = new int[2];
        int[] pointShift2 = new int[2];
        int[] pointShift3 = new int[2];
        int[] points1 = new int[2];
        int[] points2 = new int[2];
        int[] points3 = new int[2];
        
        //vertex 1
        newPoints = matrixMulti(transform1, points);
        newPoints = matrixMulti(transform2, newPoints);
        if((int)newPoints[0] < 0){
            pointShift2[0] = -(int)(newPoints[0]);
        }
        //start at vertex 2
        double[] location2 = {0, layer[0].length};
        double[] npoints2 = matrixMulti(transform1, location2);
        
        if((int)npoints2[0] < 0){
            pointShift1[0] = -(int)npoints2[0];
        }
        if(pointShift1[0] < 0){
            pointShift1[0] = 0;
        }
        npoints2 = matrixMulti(transform2, npoints2);
        npoints2 = matrixMulti(transform1, npoints2);
        if((int)npoints2[0] < 0){
            pointShift3[0] = -(int)npoints2[0];
        } 
        if(pointShift3[0] < 0){
            pointShift3[0] = 0;
        }
        double[] location3 = {layer.length, 0};
        double[] npoints3 = matrixMulti(transform1, location3);        
        points1[0] = (int) npoints3[0];
        npoints3 = matrixMulti(transform2, npoints3);
        points2[0] = (int) npoints3[0];
        npoints3 = matrixMulti(transform1, npoints3);
        points3[0] = (int) npoints3[0];
        double[] location4 = {layer.length, layer[0].length};
        double[] npoints4 = matrixMulti(transform1, location4);
        points1[1] = layer[0].length;
        npoints4 = matrixMulti(transform2, npoints4);
        points2[1] = (int) npoints4[1];
        npoints4 = matrixMulti(transform1, npoints4);
        points3[1] = (int) npoints4[1];       
        
        double[] pointShift = matrixMulti(transform2, new double[] {(double) -pointShift1[0], (double) -pointShift1[1]});
        
        int[][] newLayer = new int[points1[0] + pointShift1[0]][points1[1] + pointShift1[1]];
        int[][] newLayer2 = new int[points2[0] + pointShift1[0] + pointShift2[0]][points2[1] + pointShift1[1] + pointShift2[1]];
        int[][] newLayer3 = new int[points3[0] + pointShift1[0] + pointShift2[0] + pointShift3[0]][points3[1] + pointShift1[1] + pointShift2[1] + pointShift3[1]];
        pointShift2[1] += pointShift[1];
        if(pointShift2[1] < 0){
            pointShift1[1] = 0;
        }
        pointShift = matrixMulti(transform1, new double[] {(double) -pointShift1[0], (double) -pointShift1[1]});
        pointShift3[0] += pointShift[0];
        if(pointShift3[0] < 0){
            pointShift3[0] = 0;
        }
        for(int i = 0; i < layer.length; i++){
            for(int j = 0; j < layer[0].length; j++){
                newPoints = matrixMulti(transform1, new double[]{i, j});
                int col = (int)Math.round(newPoints[1] + pointShift1[1]);
                int row = (int)Math.round(newPoints[0] + pointShift1[0]);
                if(!(row >= newLayer.length || col >= newLayer[0].length)){
                    newLayer[row][col] = layer[i][j];
                }
            }
        }
        for(int i = 0; i < newLayer.length; i++){
            for(int j = 0; j < newLayer[0].length; j++){
                newPoints = matrixMulti(transform2, new double[]{i, j});
                int col = (int)Math.round(newPoints[1] + pointShift2[1]);
                int row = (int)Math.round(newPoints[0] + pointShift2[0]);
                if(!(row >= newLayer2.length || col >= newLayer2[0].length|| row < 0 || col < 0)){
                    newLayer2[row][col] = newLayer[i][j];
                }
            }
        }
        for(int i = 0; i < newLayer2.length; i++){
            for(int j = 0; j < newLayer2[0].length; j++){
                newPoints = matrixMulti(transform1, new double[]{i, j});
                int col = (int)Math.round(newPoints[1] + pointShift3[1]);
                int row = (int)Math.round(newPoints[0] + pointShift3[0]);
                if(!(row >= newLayer3.length || col >= newLayer3[0].length|| row < 0 || col < 0)){
                    newLayer3[row][col] = newLayer2[i][j];
                }
            }
        }
        pixels.set(currLayer, newLayer3);
        layerCorners.set(currLayer, location);
        insertLayer(newLayer3, location);
        imagePanel.setBackgroundImage(ImageEffect.pixelsToImage(display));
    }
    
    private double[] matrixMulti(double[][] transform, double[] newPoints1){
        double[] mat = new double[transform.length];
        for(int i = 0; i < transform.length; i++){
            for(int j = 0; j < newPoints1.length; j++){
                double point = newPoints1[j] * transform[i][j];
                mat[i] += point;
            }
        }
        return mat;
    }
    

    public BufferedImage createBufferedImage(File file) {
        BufferedImage img;
        try {
            img = ImageIO.read(file);
        } catch (Exception e) {
            return null;
        }
        if (img.getType() == BufferedImage.TYPE_INT_RGB) {
            return img;
        }
        BufferedImage nImg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        nImg.createGraphics().drawImage(img, 0, 0, null);
        return nImg;
    }

    public static boolean parseAndVerifyInput(String input,
                                        ImageEffectParam param) {
        if (param instanceof ImageEffectIntParam) {
            ImageEffectIntParam intParam = (ImageEffectIntParam) param;
            int value = -1;
            try {
                value = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                showErrorMessage("Invalid integer format.");
                return false;
            }

            if (value > intParam.getMaxValue() ||
                value < intParam.getMinValue()) {
                showErrorMessage("Integer value out of allowed range." +
                    " Min: " + intParam.getMinValue() +
                    " Max: " + intParam.getMaxValue());
                return false;
            }

            intParam.setValue(value);
            return true;
        } else {
            // Execution should ideally never reach here.
            showErrorMessage("Unknown type of input parameter.");
        }
        return false;
    }

    private static void showErrorMessage(String description) {
        JOptionPane.showMessageDialog(null, description, "ERROR",
            JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Kind of messy method, but it populates the HashMap effects with all the ImageEffects
     * it can find in the class paths given.
     */
    public static HashMap<String, ImageEffect> getFilters() {
        String s = ImageEffect.class.getResource(ImageEffect.class.getSimpleName() + ".class").getFile();
        s = s.substring(0, s.lastIndexOf(ImageEffect.class.getSimpleName()) - 1);
        String[] searchPaths = s.split(File.pathSeparator);
        HashMap<String, ImageEffect> effects = new HashMap<>();

        for(int i = 0; i < searchPaths.length; i++) {
            File path = new File(searchPaths[i]);

            if (!path.isDirectory()) {
                continue;
            }

            File[] classFiles = path.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if(name.endsWith(".class")) {
                        return true;
                    }
                    return false;
                }
            });
            for(int j = 0; j < classFiles.length; j++) {
                String className = getBase(classFiles[j]);
                try {
                    Class c = Class.forName(ImageEffect.class.getPackage().getName() + "." + className);
                    if(ImageEffect.class.isAssignableFrom(c)) {
                        Constructor construct = c.getDeclaredConstructor();
                        construct.setAccessible(true);
                        effects.put(className, (ImageEffect) construct.newInstance());
                    }
                } catch(Exception e) {
                }
            }
        }
        return effects;
    }

    private static String getExtension(File file) {
        if (file == null || file.isDirectory()) {
            return "";
        }

        int index = file.getName().lastIndexOf('.');
        if (index == -1) {
            return "";
        }
        return file.getName().substring(index + 1);
    }

    private static String getBase(File file) {
        if (file == null || file.isDirectory()) {
            return "";
        }

        int index = file.getName().lastIndexOf('.');
        if (index == -1) {
            return file.getName();
        }
        return file.getName().substring(0, index);
    }

    private JMenu effectsMenu;
    private JMenuItem menuItemOpenImage;
    private JMenuItem menuItemSaveImage;
    private JMenuItem menuItemExit;
    private JMenuItem menuItemOpenLayer;
    private JMenuItem menuItemCloseLayer;
    private JMenu selectLayerMenu;
    private JMenu editMenu;
    private JMenuItem menuItemMove;
    private JMenuItem menuItemRotate;
    private JImagePanel imagePanel;
    private JFileChooser openFileChooser;
    private String[] writableFormats;
    private JFileChooser saveFileChooser;
    private Map<JMenuItem, ImageEffect> effectMap;
}

/**
 * A JPanel with an image for a background.
 */
class JImagePanel extends JPanel {
    public JImagePanel() {
        super();
    }

    public JImagePanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public JImagePanel(LayoutManager layout) {
        super(layout);
    }

    public JImagePanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Image image) {
        backgroundImage = image;
        invalidate();
    }

    public void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            super.paintComponent(g);
        }
    }

    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        if (backgroundImage != null) {
            dim.setSize(Math.max(dim.getWidth(), backgroundImage.getWidth(this)), Math.max(dim.getHeight(), backgroundImage.getHeight(this)));
        }
        return dim;
    }

    private Image backgroundImage;
}
