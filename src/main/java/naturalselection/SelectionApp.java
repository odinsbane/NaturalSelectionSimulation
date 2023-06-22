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
    public SelectionApp(boolean visuals){
        model = new NaturalSelection(null, visuals);
        model.setTerrain(new NaturalTerrain());
        model.popluteBeasts();
    }
    public SelectionApp(BeastData bd, boolean visuals){

        model = new NaturalSelection(bd, visuals);
    }
    public static void main(String[] args){
        SelectionApp app;
        if(args.length>0){
            File infile = new File(args[0]);
            BeastData bd = BeastData.readData(infile);
            app = new SelectionApp(bd, true);

            SwingUtilities.invokeLater(app);

        } else{
            app = new SelectionApp(true);
            SwingUtilities.invokeLater(app);
        }
        app.model.start();
    }

    public void run(){
        if(model.VISUALS){
            JFrame y = new JFrame("My Frame");
            y.setSize(600,600);
            y.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            y.setContentPane(model.getPanel());
            y.setVisible(true);
            model.graph.show();
        }
    }
}
