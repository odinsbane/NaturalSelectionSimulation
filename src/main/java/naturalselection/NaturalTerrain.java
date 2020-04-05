package naturalselection;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Keeps track of geometric considerations of the bests
 * this is the world.
 * 
 * User: mbs207
 * Date: May 20, 2010
 * Time: 3:01:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class NaturalTerrain  implements Serializable {

    int WIDTH=800;
    int HEIGHT=800;

    int CELL_SIZE = 5;
    int PEN_SIZE = 20;

    /** terrain image */
    transient BufferedImage scene;
    /** the graphics, a constant reference is used */
    transient Graphics graphics;

    double[][] food = new double[WIDTH/CELL_SIZE][HEIGHT/CELL_SIZE];
    TerrainTypes[][] landscape = new TerrainTypes[WIDTH/CELL_SIZE][HEIGHT/CELL_SIZE];

    Rectangle2D bounds;

    /** outlines of boxes a drawn using these colors to show how much food is in each box */
    Color[] colors = new Color[]{Color.GRAY, Color.YELLOW, Color.BLUE, Color.GREEN, Color.WHITE};

    /** for keeping track of neighbors. */
    transient BeastPens pens = new BeastPens(WIDTH,HEIGHT,PEN_SIZE);

    public double production = 0;
    
    NaturalTerrain(){
        loadLandscape();
        


        
        bounds = new Rectangle2D.Double(0,0,WIDTH, HEIGHT);
        scene = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        graphics = scene.createGraphics();

        for(int i = 0; i<food.length; i++){
            for(int j =0; j<food[0].length;j++){
                TerrainTypes t = landscape[i][j];
                food[i][j] = t.FOOD;
                graphics.setColor(t.COLOR);
                graphics.fillRect(i*CELL_SIZE, j*CELL_SIZE, CELL_SIZE, CELL_SIZE);
                production += t.GROW;
            }
        }

    }

    public void refresh(){
        scene = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);        
        pens = new BeastPens(WIDTH,HEIGHT,PEN_SIZE);
        graphics = scene.createGraphics();

        for(int i = 0; i<food.length; i++){
            for(int j =0; j<food[0].length;j++){
                TerrainTypes t = landscape[i][j];
                graphics.setColor(t.COLOR);
                graphics.fillRect(i*CELL_SIZE, j*CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    /** gets the terrain for a specific point
     *
     * @param loc the point of interest
     * @return appropriate terrain.
     */
    public TerrainTypes getType(Point2D loc){
        int i = (int)loc.getX()/CELL_SIZE;
        int j = (int)loc.getY()/CELL_SIZE;
        return landscape[i][j];
    }

    /**
     * Draws the visible portion of the scene onto the display.
     * 
     * @param g graphics for the display image.
     * @param r the 'view' rectangle.
     */
    public void fillBackground(Graphics2D g, Rectangle r ){
        int x1 = r.x;
        int y1 = r.y;
        int x2 = r.x+r.width;
        int y2 = r.y+r.height;
        g.setColor(Color.BLACK);        
        if(x1<0)
            g.fillRect(0,0,-r.x,r.height);
        else if(x2>WIDTH)
            g.fillRect(WIDTH-x1, 0, x2-WIDTH,r.height);

        if(y1<0)
            g.fillRect(0,0,r.width,-r.y);
        else if(y2>HEIGHT)
            g.fillRect(0,HEIGHT-y1, r.width, y2 - HEIGHT);
        g.drawImage(scene,0,0,r.width, r.height, x1,y1,x2,y2,null);


    }

    /**
     * Removes food from a cell.
     * @param loc position that food is removed from.
     * @param amt amount of food to be removed
     * @return actual amount removed.
     */
    public double eat(Point2D loc, double amt ){
        int i = (int)loc.getX()/CELL_SIZE;
        int j = (int)loc.getY()/CELL_SIZE;

        double f = food[i][j];

        double rvalue;
        if(f>amt){
            f -= amt;
            rvalue = amt;
        }else{
            rvalue = f;
            f = 0;
        }

        food[i][j]=f;
        return rvalue;
    }

    /**
     * tells how much food is available.
     * @param loc position of interest.
     * @return amount of food.
     */
    public double getFood(Point2D loc){
        int i = (int)loc.getX()/CELL_SIZE;
        int j = (int)loc.getY()/CELL_SIZE;
        return food[i][j];
    }

    /**
     * For preventing animals from leaving.
     *
     * @param r bounding rectangle of animal.
     *
     * @return if rectangle is contained.
     */
    public boolean checkBounds(Rectangle2D r){

        return bounds.contains(r);
        
    }

    /**
     * Goes through every cell and 'growsFood' if the cell has room
     * to grow.
     *
     */
    public void growFood(){
        for(int i = 0; i<food.length; i++){
            for(int j =0; j<food[0].length;j++){
                double f = food[i][j];
                TerrainTypes t = landscape[i][j];
                    if(f<t.FOOD){
                        int v = (int)(f/t.FOOD*4);
                        f += t.GROW;

                        f = f>t.FOOD?t.FOOD:f;

                        int u = (int)(f/t.FOOD*4);

                        food[i][j]=f;

                        if(u!=v){

                            graphics.setColor(colors[u]);
                            graphics.drawRect(i*CELL_SIZE, j*CELL_SIZE, CELL_SIZE, CELL_SIZE);

                        }
                    }
            }
        }

    }

    /**
     * Sets up the landscape, this needs to be replace by reading an image and using the image
     * as a map.
     * 
     */
    void loadLandscape(){
        double radius, x, y;
        RectangularShape deep_water, shallows, shores, grass;
        radius = 200;
        x = 800;
        y = 0;
        deep_water = new Ellipse2D.Double(x-radius,y-radius,2*radius,2*radius);
        radius +=25;
        shallows = new Ellipse2D.Double(x-radius,y-radius,2*radius,2*radius);
        radius += 20;
        shores = new Ellipse2D.Double(x-radius,y-radius,2*radius,2*radius);
        radius += 220;
        grass = new Ellipse2D.Double(x-radius,y-radius,2*radius,2*radius);
        

        for(int i = 0; i<food.length; i++){
            for(int j =0; j<food[0].length;j++){
                if(deep_water.contains(i*CELL_SIZE, j*CELL_SIZE))
                    landscape[i][j] = TerrainTypes.water;
                else if(shallows.contains(i*CELL_SIZE, j*CELL_SIZE))
                    landscape[i][j] = TerrainTypes.shallow_water;
                else if(shores.contains(i*CELL_SIZE, j*CELL_SIZE))
                    landscape[i][j] = TerrainTypes.shore;
                else if(grass.contains(i*CELL_SIZE, j*CELL_SIZE))
                    landscape[i][j] = TerrainTypes.grass;
                else
                    landscape[i][j] = TerrainTypes.desert;

            }
        }

        radius = 2.5;
        x = 0;
        y = 800;
        deep_water = new Rectangle2D.Double(x,y-100 - radius ,300+radius, 2*radius);

        shallows = new Rectangle2D.Double(x,y-100 - radius ,400+radius, 2*radius);

        radius += 5;
        shores = new Rectangle2D.Double(x,y-100 - radius ,350+radius, 2*radius);

        radius = 400;
        grass = new Ellipse2D.Double(x-radius,y-radius,2*radius,2*radius);


        for(int i = 0; i<food.length; i++){
            for(int j =0; j<food[0].length;j++){
                if(deep_water.contains(i*CELL_SIZE, j*CELL_SIZE))
                    landscape[i][j] = TerrainTypes.water;
                else if(shallows.contains(i*CELL_SIZE, j*CELL_SIZE))
                    landscape[i][j] = TerrainTypes.shallow_water;
                else if(shores.contains(i*CELL_SIZE, j*CELL_SIZE))
                    landscape[i][j] = TerrainTypes.shore;
                else if(grass.contains(i*CELL_SIZE, j*CELL_SIZE))
                    landscape[i][j] = TerrainTypes.grass;

            }
        }

        x=700;
        y=700;
        for(int i = 0; i<3; i++){
            for(int j = 0; j<3; j++){

                landscape[(int)x/CELL_SIZE + i][(int)y/CELL_SIZE + j] = TerrainTypes.shallow_water;
            }
        }

        x=300;
        y=300;
        for(int i = 0; i<3; i++){
            for(int j = 0; j<3; j++){

                landscape[(int)x/CELL_SIZE + i][(int)y/CELL_SIZE + j] = TerrainTypes.shallow_water;
            }
        }

        x=0;
        y=0;
        for(int i = 0; i<10; i++){
            for(int j = 0; j<10; j++){

                landscape[(int)x/CELL_SIZE + i][(int)y/CELL_SIZE + j] = TerrainTypes.grass;
            }
        }
    }

    /**
     * puts all the beasts into the appropriate pens.
     *
     * @param b requires a complete list of beasts
     */
    public void updatePens(List<Beast> b){
        pens.setupSpace(b);

    }
    /** get all beasts in the current cell
     *
     * @param loc location of the cell
     * @return a set containing all of the beasts in the cell.
     */
    public Set<Beast> getNeighbors(Point2D loc){
        return pens.getNeighbors(loc);
    }

}

/**
 * These are the different terrains.
 */
enum TerrainTypes implements Serializable{

    grass(500,Color.GREEN, 20),
    water(1500,new Color(0,0,255), 45),
    shallow_water(200,new Color(100,100,255), 70),
    shore(150,new Color(50,150,25),30),
    desert(25,Color.YELLOW, 1);

    /**Initialize with properties
     *
     * @param f the total food this can contain.
     * @param c what color it is painted
     * @param g how much grows per update
     */
    private TerrainTypes(double f, Color c, double g){
        FOOD=f;
        COLOR = c;
        GROW = g;
    }

    /** total food */
    public double FOOD;
    /** painted color */
    public Color COLOR;
    /** amt recovered */
    public double GROW;
}
