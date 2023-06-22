package naturalselection;

import javax.swing.*;

public class TerrainTests {

    public static void main(String[] args){
        int width = 512;
        int height = 512;

        NaturalTerrain terrain = new NaturalTerrain(width, height);
        TerrainTypes[][] types = new TerrainTypes[width/terrain.CELL_SIZE][height/ terrain.CELL_SIZE];
        for(TerrainTypes[] row: types){
            for(int i = 0; i<row.length; i++){
                row[i] = TerrainTypes.water;
            }
        }
        terrain.setLandscape(types);
        NaturalSelection model = new NaturalSelection(null, true);
        model.setTerrain(terrain);
        model.popluteBeasts();
        model.graph.show();

        JFrame y = new JFrame("My Frame");
        y.setSize(600,600);
        y.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        y.setContentPane(model.getPanel());
        y.setVisible(true);
        model.start();

    }
}
