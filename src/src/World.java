import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class World {	
	public static final int WINDOW_WIDTH = 768;
	public static final int PATH_WIDTH = 1;
	public static final char GO_LEFT_c = 'L';
	public static final char GO_UP_c = 'U';
	public static final char GO_RIGHT_c = 'R';
	public static final char GO_DOWN_c = 'D';
	
	private BufferedImage 	original_map, // The image that we will check the obstacles (In case that like someone draw something to image, edited_map is not safe)
							edited_map,	  // The image that was drew on
						    scaled_map;	  // Scaled image according to WINDOW_WIDTH 
	private String mapFile;
	private double scale;
	private JFrame frame;
	private JPanel panel;
	private JLabel label;
	private Point start, stop;
	private boolean isStartSet, isStopSet;
	private Random random;

	/**
	 * Parkur Constructor
	 * @param mapFile	It has to be address of PNG file. 
     * It can be important that pixels of this image are from black(0,0,0) and white(1,1,1).
	 * NOTE: When setStart and setStop were not used, these values (starting points) are generated from near to the middle of track.
	 */
    public World(String mapFile) {
    	this.mapFile = mapFile;
    	edited_map = loadPNG(mapFile);
    	original_map = loadPNG(mapFile);
    	
    	isStartSet = false;
    	isStopSet = false;
    	random = new Random();

    	// Create scaled_map
    	scale = (double)WINDOW_WIDTH / (double)edited_map.getWidth();
    	int w = (int) (edited_map.getWidth() * scale);
    	int h = (int) (edited_map.getHeight()* scale);
    	scaled_map = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

    	showGUI();
    	clearDrawings();
    }
    
    public int getWidth() {
    	return edited_map.getWidth();
    }
    
    public int getHeight() {
    	return edited_map.getHeight();
    }
    
    public Point getStart() {
		return start;
	}

    /**
     * Set Start Position
     * @param x,y	 Mice start point
     */
	public void setStart(int x, int y) {
		this.start = new Point(x,y);
		isStartSet = true;
	}

	public Point getStop() {
		return stop;
	}

	/**
	 * Set Stop Position
	 * @param x,y	Coordinate that Mice has to arrive or stop
	 */
	public void setStop(int x, int y) {
		this.stop = new Point(x,y);
		isStopSet = true;
	}

	/**
	 * Renew the drawings. If you use drawGen() or similar functions, call this function to renew the image.
	 */
    public void updateDrawings() {
    	AffineTransform at = new AffineTransform();
    	at.scale(scale, scale);
    	AffineTransformOp scaleOp = 
    	   new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    	scaled_map = scaleOp.filter(edited_map, scaled_map);
    	frame.repaint();
    }
    
    /**
     * Clean the drawings in track and brings back to its first look.
     */
    public void clearDrawings() {
    	edited_map = loadPNG(mapFile);
    }
    
    // Add path to edited_map. Call the updateDrawings() for updating the screen. 
    /**
     * Draw the path from StartPosition according to DNA. Lenght of path can be limited with _length_
     * @param dna		(R)IGHT, (U)P, (L)EFT, (D)OWN abbreviated with R,U,L,D should be String
     * @param color		Colour of path. It would be nice to be selected any colour except black.
     * @param length	If whole DNA is not wanted to be drawed, an upper limit can be determined.
     */
    public void drawGen(String dna, Color color, int length) {
    	int x = start.x;
    	int y = start.y;   	
    	boolean isOutOfBounds = false;
    	
    	if (!isStartSet) {
    		System.err.println("Caution! You are trying to call drawGen without calling Parkur.setStart() !");
    	}
    	
    	if (!isStopSet) {
    		System.err.println("Caution! You are trying to call drawGen without calling Parkur.setStop() !");
    	}
    	
    	for (int i=0; i<length && i<dna.length(); i++) {
    		switch (dna.charAt(i)) {
    		case GO_LEFT_c:
    			x--;
    			for (int j = y-PATH_WIDTH/2; j<y+PATH_WIDTH/2; j++) {
    				if (isInside(edited_map, x, j))
    					edited_map.setRGB(x, j, color.getRGB() );
    			}
    			if (isInside(edited_map, x, y) == true) 
    				edited_map.setRGB(x, y, color.getRGB() );
    			else 
    				isOutOfBounds = true;
    			break;
    		case GO_UP_c:
    			y--;
    			for (int j = x-PATH_WIDTH/2; j<x+PATH_WIDTH/2; j++) {
    				if (isInside(edited_map, j, y))
    					edited_map.setRGB(j, y, color.getRGB() );
    			}
       			if (isInside(edited_map, x, y) == true) 
    				edited_map.setRGB(x, y, color.getRGB() );
    			else 
    				isOutOfBounds = true;
    			break;
    		case GO_RIGHT_c:
    			x++;
    			for (int j = y-PATH_WIDTH/2; j<y+PATH_WIDTH/2; j++) {
    				if (isInside(edited_map, x, j))
    					edited_map.setRGB(x, j, color.getRGB() );
    			}
       			if (isInside(edited_map, x, y) == true) 
    				edited_map.setRGB(x, y, color.getRGB() );
    			else 
    				isOutOfBounds = true;
    			break;	
    		case GO_DOWN_c:
    			y++;
    			for (int j = x-PATH_WIDTH/2; j<x+PATH_WIDTH/2; j++) {
    				if (isInside(edited_map, j, y))
    					edited_map.setRGB(j, y, color.getRGB() );
    			}
       			if (isInside(edited_map, x, y) == true) 
    				edited_map.setRGB(x, y, color.getRGB() );
    			else 
    				isOutOfBounds = true;
    			break;
    		}
    	}
    	
    	if (isOutOfBounds) {
    		System.err.println("Caution! Gene drawn is out of the edges of track!");
    	}
    	
    	//updateDrawings(); // TODO: Do not keep it here, otherwise the program may work slower BUNU BURADA TUTMA YOKSA PROGRAM KASABILIR
    }
    
    /**
     * Draw the path from StartPosition according to DNA
     * @param dna		(R)IGHT, (U)P, (L)EFT, (D)OWN abbreviated with R,U,L,D have to be String
     * @param color		Colour of path. It would be nice to be selected any colour except black.
     */
    public void drawGen(String dna, Color color) {
    	drawGen(dna, color, 99999999);
    }
    
    /**
     * Check whether there are obstacles on current coordinates
     * @param x,y	World position
     * @return	If there is obstacle True; otherwise False
     */
    public boolean isObstacle(int x, int y) {
    	if ( isInside(original_map, x, y) == false ) {
    		return true;
    	}
    	
    	Color c = new Color( original_map.getRGB(x, y) );
    	if ( c.getBlue() < 127 || c.getGreen() < 127 || c.getRed() < 127) {
    		return true;
    	}
    	return false;
    }
    
    public Color randomColor() {
    	Color c = Color.getHSBColor(random.nextFloat(),//random hue, color
    	                1.0f,//full saturation, 1.0 for 'colorful' colors, 0.0 for grey
    	                1.0f //1.0 for bright, 0.0 for black
    	                );
    	return c;
    }
    
    // ---------------------------------- PRIVATE ------------------------------------
    
    private void showGUI(){
    	frame = new JFrame("Genetic Path Planning");
    	panel = new JPanel(new FlowLayout()); 	
    	label = new JLabel(new ImageIcon(scaled_map));
    	panel.add(label);
    	frame.add(panel);
    	frame.setSize(scaled_map.getWidth()+100, scaled_map.getHeight()+100);	// TODO: +100 +100 :)
    	frame.setVisible(true);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private BufferedImage loadPNG(String filename) {
    	BufferedImage map = null;
    	try {
    		map = ImageIO.read(new File(filename));
    	} catch (IOException e) {
    		System.err.println(filename + " file not found!");
    	}
    	return map;
    }
    
    private boolean isInside(BufferedImage bi, int x, int y) {	
    	int w,h;
    	w = bi.getWidth();
    	h = bi.getHeight();
    	if (0<=x && 0<=y && x<w && y<h) {
    		return true;
    	}
    	return false;
    }
    
    @Deprecated
    private void addBorders(BufferedImage bi) {
    	int w = bi.getWidth();
    	int h = bi.getHeight();

    	for (int i=0; i<w; i++) {
    		for (int j=0; j<PATH_WIDTH*2; j++) {
	    		// UPPER BORDER
    			bi.setRGB(i, j, (Color.BLACK).getRGB() );
	    		
	    		// BOTTOM BORDER
    			bi.setRGB(i, h-1-j, (Color.BLACK).getRGB() );
    		}
    	}
    	
    	for (int i=0; i<h; i++) {
    		for (int j=0; j<PATH_WIDTH*2; j++) {
	    		// LEFT BORDER
	    		edited_map.setRGB(j, i, (Color.BLACK).getRGB() );
	    		
	    		// RIGHT BORDER
	    		edited_map.setRGB(w-1-j, i, (Color.BLACK).getRGB() );
    		}
    	}
    }
    
   
}








