package naturalselection;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Random;

/**
 * Lives, divides to reproduce.
 *
 * User: mbs207
 */
public class Amoeboid extends Beast implements Runnable,Serializable {
    final Ellipse2D shape;

    transient NaturalSelection model;
    transient NaturalTerrain nt;

    //traits
    Point2D good_terrain;
    TerrainTypes affinity_terrain;

    boolean moving = true;

    Random brain = new Random();
    int age;

    int move_count=0;
    double MOVE_PROB = 0.1;
    int trait_count;

    boolean grazing;


    double vx, vy;
    double radius;

    boolean stops_to_eat, redirect, grazes, land_affinity, predator, conservative,swims, social, fast;
    /**
     * For creating from scratch, does not have any traits.
     *
     * @param x starting x position
     * @param y starting y position
     * @param parent the NaturalSelection program that this is running.
     */
    public Amoeboid(double x, double y, NaturalSelection parent){

        age = (int)(100*brain.nextDouble());

        size = 625;
        radius = Math.sqrt(size);

        MAX_VELOCITY = 0.5*brain.nextDouble();

        reproductive_age = 105 - 5*brain.nextDouble() + 1e6;

        life = size*10;
        MAX_LIFE=size*20;

        loc = new Point2D.Double(x,y);
        shape = new Ellipse2D.Double(loc.getX() - 0.5*radius,loc.getY() - 0.5*radius, radius, radius);

        double theta = 2*Math.PI*brain.nextDouble();
        vx = MAX_VELOCITY *Math.sin(theta);
        vy = MAX_VELOCITY *Math.cos(theta);

        
        dead = false;

        traits = new HashSet<BeastTraits>();
        initializeTraits();
        
        consume = 1 + size  + MAX_VELOCITY;

        trait_count = traits.size();

        setParent(parent);
    }

    /**
     * Creates a descendant of the ancestor.  This will allow
     * more variation in mutiations and such, and the addition
     * of traits.
     * 
     * @param ancestor this amoeboid that 'gave birth' to this one.
     */
    public Amoeboid(Amoeboid ancestor){
        age = 0;

        reproductive_age = ancestor.reproductive_age  + 5 - 10*brain.nextDouble();
        size = ancestor.size + 2 - 4*brain.nextDouble();
        size = size<=1?1:size;

        radius = Math.sqrt(size);
        
        reproductive_age = reproductive_age<10 + size*5?10 + 5*size:reproductive_age;
        
        MAX_VELOCITY = ancestor.MAX_VELOCITY + 0.05 - 0.1*brain.nextDouble();
        MAX_VELOCITY = MAX_VELOCITY>0?MAX_VELOCITY:0;

        loc = new Point2D.Double(ancestor.loc.getX(), ancestor.loc.getY());

        MAX_LIFE=size*10;
        life = ancestor.life;

        double radius = Math.sqrt(size);
        shape = new Ellipse2D.Double(loc.getX() - 0.5*radius,loc.getY() - 0.5*radius, radius, radius);


        double theta = 2*Math.PI*brain.nextDouble();
        vx = MAX_VELOCITY *Math.sin(theta);
        vy = MAX_VELOCITY *Math.cos(theta);

        inheritTraits(ancestor);
        initializeTraits();

        consume = 1 + size  + MAX_VELOCITY + traits.size();

        if(trait_count>ancestor.trait_count)
            c = new Color((int)((1<<24)*(brain.nextDouble())));
        else
            c = ancestor.c;

        dead = false;
        setParent(ancestor.model);

    }

    /**
     * Goes through the ancestors traits and other traits to decide
     * if the trait will exists.  Then initializes associated variables.
     * @param ancestor
     */
    private void inheritTraits(Amoeboid ancestor){
        traits = new HashSet<BeastTraits>();
        //traits.addAll(ancestor.traits);
        for(BeastTraits t: BeastTraits.values()){
            if(ancestor.traits.contains(t)){
                if(brain.nextDouble()<0.99)
                    traits.add(t);
            } else if(brain.nextDouble()<0.001){
                traits.add(t);
            }
        }
        if(traits.contains(BeastTraits.land_affinity)){
            if(ancestor.traits.contains(BeastTraits.land_affinity)){
                affinity_terrain = ancestor.affinity_terrain;
                good_terrain = ancestor.good_terrain;
            }else
                chooseLand();
        }
    }

    public void initializeTraits(){

        predator = traits.contains(BeastTraits.predator);
        stops_to_eat = !predator||traits.contains(BeastTraits.omnivore);
        redirect = traits.contains(BeastTraits.redirect);
        grazes = traits.contains(BeastTraits.graze);
        land_affinity = traits.contains(BeastTraits.land_affinity);
        conservative = traits.contains(BeastTraits.conservation);
        swims = affinity_terrain==TerrainTypes.water;
        social = traits.contains(BeastTraits.social);
        fast = traits.contains(BeastTraits.fast);

        trait_count = traits.size();
        grazing = false;


    }

    /**
     * for land affinity, selects with land to be attracted too.
     */
    private void chooseLand(){
        TerrainTypes[] tt = TerrainTypes.values();
        affinity_terrain=tt[(int)(brain.nextDouble()*tt.length)];
        good_terrain = loc;
    }

    public void paint(Graphics2D g){
        g.setColor(c);

        shape.setFrame(loc.getX() - 0.5*radius,loc.getY() - 0.5*radius, radius, radius);

        g.draw(shape);
        
    }

    /**
     * Follows logic depending on traits.  If it is moving
     * it continues to move, if it is not moving it eats.
     * If it is a predator it kills things.  If it follows or
     * redirects it does so.
     *
     */
    public void interact(){
        age++;
        double f = nt.getFood(loc);
        if(moving){
            life-=0.2*consume;
            move_count++;

            if(stops_to_eat&&(f>2*consume||life<2)){
                moving=false;
            }  else if(redirect){
                    redirect();
            }
            if(grazes){
                grazing=life<MAX_LIFE/2&&life>2;
                if(grazing)
                    eatFood();
            }
        } else{
            life-=0.1*consume;
            if(f>0){
                    eatFood();
            } else{
                life-=0.1*consume; 
                move_count = 0;
                moving=true;
            }

        }
        if(life<=0)
            die();
        if(age>reproductive_age)
            birth();
        if(land_affinity){
            if(affinity_terrain != null && affinity_terrain.compareTo(nt.getType(loc))==0){
                good_terrain=new Point2D.Double(loc.getX(), loc.getY());

            }
        }
        if(predator){
            for(Beast b: nt.getNeighbors(loc)){

                kill(b);
                if(life==MAX_LIFE&&conservative)
                    break;
            }
        }
    }

    /**
     * Tries to eat its weight in food.
     *
     */
    void eatFood(){
        if(stops_to_eat){
            double v = consume;
            if(conservative)
                v = consume>MAX_LIFE-life?MAX_LIFE-life:consume;
            double y = nt.eat(loc,v);
            y = y<size?y:size;
            life+=y;
            life = life>MAX_LIFE?MAX_LIFE:life;
        }
    }

    /**
     * tries to kill the other beast.
     *
     * @param b the beast to be killed.
     */
    void kill(Beast b){
        if(b.c==c)
            return;
        if(!b.dead&&b.size<size&&shape.contains(b.loc)){
            if(b.traits.contains(BeastTraits.evasive)&&brain.nextDouble()<0.25)
                return;


            model.killBeast(b);
            b.dead=true;

            life += b.life + b.size;
            life = life>MAX_LIFE?MAX_LIFE:life;
        }
    }

    /**
     * moves towards another beast.  Intended to mimic
     * heard-like behavior.
     *
     * @param b followed
     */
    void follow(Beast b){
        moveTowards(b.loc);
    }

    /**
     * Goes towards a specific point, used by two different traits.
     *
     * @param target
     */
    void moveTowards(Point2D target){

        double dx =  target.getX() - loc.getX();
        double dy =  target.getY() - loc.getY();
        double mag = Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
        if(mag>MAX_VELOCITY){
            vx = MAX_VELOCITY*dx/mag;
            vy = MAX_VELOCITY*dy/mag;

        } else if(mag>0){
            vx = dx;
            vy = dy;

        }

    }


    /**
     * Moves in a random direction.
     * 
     */
    public void changeDirections(){
        double theta = 2*Math.PI*brain.nextDouble();
        vx = MAX_VELOCITY *Math.sin(theta);
        vy = MAX_VELOCITY *Math.cos(theta);

    }

    /**
     * Creates a new Amoeboid
     */
    void birth(){

        life = life/2;
        Amoeboid kin = new Amoeboid(this);
        model.addBeast(kin);
        age = 0;

    }

    /**
     * dies and informs the model of death.
     */
    void die(){
        model.killBeast(this);
        dead = true;
    }

    /**For checking bounds w/out access to shape.*/
    public Rectangle2D getBounds(){
        return shape.getBounds();
    }

    /** moves */
    public void run(){
        if(moving){
            move();
        } 
    }
    public void setPosition(double x, double y){
        Rectangle2D rect = new Rectangle2D.Double();
        loc.setLocation(x, y);
        shape.setFrame(x - radius*0.5, y - radius*0.5, radius, radius);
    }

    /** moves */
    public void move(){
        double tmod = 1;
        switch(nt.getType(loc)){
            case water:
                if(!swims)
                    tmod = 0.5;
                break;
        }
        if(fast)
            tmod+=2;
        if(grazing)
            tmod*=0.5;

        double x = loc.getX() + tmod*vx;
        double y = loc.getY() + tmod*vy;
        Rectangle2D rect = new Rectangle2D.Double(x - radius*0.5, y - radius*0.5, radius, radius);
        if(model.checkBounds(rect)){
            setPosition(x, y);
        } else{
            changeDirections();
        }
    }

    /** changes direction */
    public void redirect(){
        double test = brain.nextDouble();
        double check=0;
        if(land_affinity&&affinity_terrain.compareTo(nt.getType(loc))!=0){
            check += 0.2*move_count;
            if(test<check){
                moveTowards(good_terrain);
                move_count=0;
                return;
            }
            

        }else if(social){
            check += 0.2*move_count;
            if(test < check){
                for(Beast b: nt.getNeighbors(loc)){
                    if(b!=this&&b.c==c){
                        follow(b);
                        move_count=0;
                        return;
                    }
                }
            }




        }else if(brain.nextDouble()<MOVE_PROB*move_count + check){
            changeDirections();
            move_count = 0;
        }
    }
    public void setParent(NaturalSelection ns){
        model = ns;
        nt = ns.terrain;
        if(nt.checkBounds(shape.getBounds()))
            model.scheduleBeastAction(this,50l);
        else
            die();
    }

    
}

