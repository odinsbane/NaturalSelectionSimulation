package naturalselection;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lightgraph.DataSet;
import lightgraph.Graph;
import lightgraph.GraphPoints;

/**
 * Keeps track of a large array of beasts, each time a beast reproduces
 * (divides at this point)  Aspects of the beast will chage.  The
 * beasts are represented by a circle.
 * 
 * User: mbs207
 * Date: May 20, 2010
 * Time: 11:39:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class NaturalSelection{

    /** One image for drawing on each update, display actually draw on the panel */
    BufferedImage base, display;

    /** this contains all of the beasts */
    CopyOnWriteArrayList<Beast> beasties;

    SelectionPanel panel;

    NaturalTerrain terrain;

    Rectangle view = new Rectangle(200,200,900,900);

    Graph graph;
    double[] sizes;
    double[] traits;
    double[] size_ct;
    double[] trait_ct;

    boolean VISUALS;
    boolean running = true;
    int SUB_STEPS = 10;
    ScheduledExecutorService eventLoop = Executors.newSingleThreadScheduledExecutor();
    public NaturalSelection(BeastData bd, boolean visuals){
        if(visuals){
            this.base = new BufferedImage(view.width, view.height, BufferedImage.TYPE_INT_RGB);
            this.display = new BufferedImage(view.width, view.height, BufferedImage.TYPE_INT_RGB);
            panel = new SelectionPanel(display, this);
        }

        if(bd==null){
            //wait for it.
        }else{
            beasties = bd.beasts;

            terrain = bd.terrain;

            terrain.updatePens(beasties);

            for(Beast b: beasties){
                b.setParent(this);

            }
        }

        //set up graph data space
        sizes = new double[20];
        size_ct = new double[20];
        BeastTraits[] bt = BeastTraits.values();
        trait_ct = new double[bt.length];
        traits = new double[bt.length];
        for(int i = 0; i<traits.length; i++){

            traits[i] = bt[i].ordinal() + 1;

        }
        for(int i = 0;i<20;i++){
            sizes[i] = i + 1;
        }

        if(visuals){
            graph = new Graph();
            graph.setXRange(0,21);
            graph.setYRange(0,100);
            graph.addData(sizes,size_ct);
            graph.show(false);
        }
        VISUALS = visuals;
    }

    public void popluteBeasts(){
        //terrain = new NaturalTerrain();
        beasties = new CopyOnWriteArrayList<Beast>();
        double consume = 0;
        while(consume*SUB_STEPS < terrain.production){

            Amoeboid n = new Amoeboid(terrain.WIDTH/2, terrain.WIDTH/2, this);
            consume += n.consume;
            double x = n.radius + (terrain.WIDTH - 2*n.radius)*Math.random();
            double y = n.radius + (terrain.HEIGHT - 2*n.radius)*Math.random();
            n.setPosition(x, y);
            beasties.add(n);
        }
        System.out.println(beasties.size() + " beasts added");
    }

    /** starts time tasks for updating image, moving the beasts, growing terrain and updating display*/
    public void start() {
        if(VISUALS){
            startPaintLoop();
        }
        long count = 0;
        while(running){
            for(int i = 0; i<SUB_STEPS; i++) {
                for(Beast b: beasties){
                    b.move();
                }
                terrain.updatePens(beasties);
                interactionStep();
            }

            terrainGrowth();
            updateDisplay();
            count++;
            if(count%100 == 1){
                saveStep();
            }

        }

    }

    public void updateImage(){
        Graphics2D g = base.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, (int)view.getWidth(), (int)view.getHeight());
        terrain.fillBackground(g,view);

        if(view.x==0&&view.y==0)
            paintBeasts(g);
        else
            paintBeastsTransformed(g);

        g.dispose();
        g = display.createGraphics();
        g.drawImage(base,
                0,0,
                Color.WHITE, panel);
        g.dispose();
        panel.repaint();

    }
    public void paintBeasts(Graphics2D g){
        for(Beast b: beasties){
            if(view.contains(b.loc))
                b.paint(g);

        }
    }

    public void paintBeastsTransformed(Graphics2D g){

        g.translate(-view.x, -view.y);
        for(Beast b: beasties){
            if(view.contains(b.loc)){
                b.paint(g);
            }
        }
        g.setTransform(new AffineTransform(1,0,0,1,0,0));
    }
    public Container getPanel() {
        return panel;
    }

    public boolean checkBounds(Rectangle2D r){
        return terrain.checkBounds(r);
    }

    /**
     * Updates the graph with trait information.
     * 
     */
    public void showNumber(){
        double size = 0;
        double velocity = 0;
        double count = 0;

        for(int i = 0; i<size_ct.length; i++)
            size_ct[i] = 0;
        for(int i = 0; i<trait_ct.length; i++)
            trait_ct[i]=0;

        int dex;
        double consumption = 0;
        for(Beast b: beasties){
            size += b.size;
            velocity += b.MAX_VELOCITY;
            count++;

            dex = (int)b.size -1;
            dex = dex>19?19:dex;
            dex = dex<0?0:dex;
            size_ct[dex]++;

            consumption += b.consume;
            for(BeastTraits bt: b.traits)
                trait_ct[bt.ordinal()]++;
            if(b.dead)
                killBeast(b);
        }

        if(count>0)
            graph.setYRange(0,count);
        graph.clearData();
        DataSet sizeSet = graph.addData(sizes, size_ct);
        sizeSet.setLabel("sizes");
        sizeSet.setPoints(GraphPoints.filledTriangles());
        DataSet ds= graph.addData(traits, trait_ct);
        ds.setLabel("traits");
        graph.resetGraph();
        graph.repaint();

        String line = "total: " + beasties.size() + " size: "
                + (size/count) + " velocity: " + (velocity/count
                + "cosumption: " + consumption + " production: " + terrain.production);
        panel.updateLabel(line);
        //System.out.println(count + " " + size/count + " " + velocity/count);

    }

    public void killBeast(Beast b){
       beasties.remove(b);
    }

    public void addBeast(Beast b){
        beasties.add(b);
    }

    public void createPredator(int x, int y){
        Amoeboid p = new Amoeboid(x,y, this);
        if(!p.dead){
            p.traits.add(BeastTraits.predator);
            p.traits.add(BeastTraits.redirect);
            p.traits.add(BeastTraits.land_affinity);
            p.traits.add(BeastTraits.social);
            p.affinity_terrain = TerrainTypes.water;
            p.good_terrain = p.loc;
            p.size = 30;
            p.c = Color.YELLOW;
            p.reproductive_age = 1e6;
            beasties.add(p);
        }
    }

    public BufferedImage createSnapshot(){
        BufferedImage image = new BufferedImage(view.width, view.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        terrain.fillBackground(g,view);
        paintBeasts(g);
        g.dispose();
        return image;
    }


    public void setTerrain(NaturalTerrain terrain) {
        this.terrain = terrain;
        if(beasties != null)
            terrain.updatePens(beasties);
    }

    public void terrainGrowth(){
        terrain.growFood();
    }
    public void paintStep(){
        updateImage();
    }

    public void updateDisplay(){
        showNumber();
    }
    public void startPaintLoop(){
        eventLoop.scheduleAtFixedRate(this::paintStep, 100L, 33L, TimeUnit.MILLISECONDS);
    }
    public void interactionStep(){
        for(Beast b: beasties) {
            if (!b.dead){
                b.interact();
            }
        }
    }

    public void saveStep(){
        BeastData bd = new BeastData(beasties, terrain);
        bd.saveData();
    }
}