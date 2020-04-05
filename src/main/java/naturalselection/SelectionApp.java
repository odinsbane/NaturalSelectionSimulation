package naturalselection;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Starts the NaturalSelection program
 * 
 * User: mbs207
 
 */
public class SelectionApp implements Runnable{
    NaturalSelection model;
    boolean VISUALS;
    public SelectionApp(){
        VISUALS=false;
        model = new NaturalSelection(null, false);
        model.start();
    }
    public SelectionApp(BeastData bd){
        VISUALS=true;
        model = new NaturalSelection(bd, true);
        model.start();
    }
    public static void main(String[] args){

        if(args.length>0){
            File infile = new File(args[0]);
            BeastData bd = BeastData.readData(infile);
            SelectionApp app = new SelectionApp(bd);

            SwingUtilities.invokeLater(app);
        } else{
            SelectionApp app = new SelectionApp();
            SwingUtilities.invokeLater(app);
        }
    }

    public void run(){
        if(VISUALS){
            JFrame y = new JFrame("My Frame");
            y.setSize(600,600);
            y.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            y.setContentPane(model.getPanel());
            y.setVisible(true);
            model.graph.show();
        }
    }
}
