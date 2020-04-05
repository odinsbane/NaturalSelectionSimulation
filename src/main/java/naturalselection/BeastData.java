package naturalselection;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: melkor
 * Date: May 25, 2010
 * Time: 11:06:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class BeastData implements Serializable {
    CopyOnWriteArrayList<Beast> beasts;
    NaturalTerrain terrain;
    //BeastPens bp;
    public BeastData(CopyOnWriteArrayList<Beast> b, NaturalTerrain nt){
        beasts = b;
        terrain = nt;
        //bp = nt.pens;
    }

    public void saveData(){
        try {
                FileOutputStream fo = new FileOutputStream("beast.dat");
                ObjectOutputStream so = new ObjectOutputStream(fo);
                so.writeObject(this);
                so.flush();
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
                System.exit(1);
            }

        
    }

    public static BeastData readData(File infile){
        try {
               FileInputStream fo = new FileInputStream(infile);
               ObjectInputStream is = new ObjectInputStream(fo);
               BeastData b = (BeastData)is.readObject();
               return b;
           } catch (Exception e) {
               System.out.println(e);
               System.exit(1);
           }

        return null;

    }
}
