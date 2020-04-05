package naturalselection;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.HashSet;

/**
 * This is the base 'beast' that will be moved about, and pegged on the timeline.
 * fields are mainly items that might be interesting for plotting.
 * 
 * User: mbs207
 
 */

public abstract class Beast implements Serializable {
    /** position */
    Point2D loc;
    /** maximum velocity */
    double MAX_VELOCITY;
    /** size/area of the beast */
    double size;

    /**max life value */
    double MAX_LIFE;

    /** value that changes with consumption */
    double life;

    /**how much the beast consumes*/
    double consume;

    /** when this reproduces */
    double reproductive_age;

    /** nuff said*/
    boolean dead;

    /** default color changes with addition of a trait */
    Color c = Color.RED;
    
    /** the current traits this has */
    public HashSet<BeastTraits> traits;

    abstract public void setParent(NaturalSelection ns);
    /**
     * Each beast must paint itself.
     *
     * @param g the tool to do it.
     */
    abstract public void paint(Graphics2D g);
    /** checks the terrain and looks for neighbors */
    abstract public void interact();
    /** moves the beast if it is moving.*/
    abstract public void run();

    /**
      * for checking if it is still contained
      *
      * @return the bounds containing this shape.
     */

    abstract public Rectangle2D getBounds();
}

/**
 * The different traits are realized by the beast that hs them. for the amoeboid:
 * land_affinity - checks if it is on the good terrain, and if not, goes to the
 *   last time it was.
 * predator - kills other beasts that are smaller.
 * redirect - changes directions.
 * social - follows other beasts that nearby and larger.
 * conservation - doesn't eat if it is full.
 * graze - eats while moving.
 */
enum BeastTraits{
    land_affinity,
    predator,
    redirect,
    social,
    conservation,
    graze,
    evasive,
    omnivore,
    fast

    
}
