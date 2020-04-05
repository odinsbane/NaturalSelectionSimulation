package naturalselection;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * For storing and aquiring neighbors.
 * 
 * User: mbs207
 * Date: May 21, 2010
 * Time: 1:08:19 PM
 * To change this template use File | Settings | File Templates.
 */

public class BeastPens{

    public CopyOnWriteArrayList<CopyOnWriteArraySet<Beast>> pens;
    CopyOnWriteArrayList<ArrayList<Beast>> working;
    int size;
    int xpts, ypts;

        /**
         *
         * */
        public BeastPens(int width, int height, int size){
            xpts = width/size;
            ypts = height/size;
            this.size = size;

            pens = new CopyOnWriteArrayList<CopyOnWriteArraySet<Beast>>();
            working = new CopyOnWriteArrayList<ArrayList<Beast>>();
            for(int i = 0; i<xpts; i++){
                for(int j = 0; j<ypts; j++){

                    pens.add(new CopyOnWriteArraySet<Beast>());
                    working.add(new ArrayList<Beast>());
                }
            }


        }

        public void setupSpace(List<Beast> bs){
            int i, j;
            for(CopyOnWriteArraySet<Beast> pen: pens)
                pen.clear();

            for(ArrayList<Beast> tran: working)
                tran.clear();

            for(Beast b: bs){
                i = (int)b.loc.getX()/size;
                j = (int)b.loc.getY()/size;
                working.get(j*xpts + i).add(b);
            }

            for(int k = 0; k<working.size();k++){

                pens.get(k).addAll(working.get(k));
            }

        }

        public Set<Beast> getNeighbors(Point2D loc){
            int i = (int)loc.getX()/size;
            int j = (int)loc.getY()/size;
            return pens.get(j*xpts + i);
        }


}


