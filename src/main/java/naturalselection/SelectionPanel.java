package naturalselection;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * ImagePanel for drawing scene.
 * 
 * User: mbs207
 
 */
public class SelectionPanel extends JPanel implements MouseListener, MouseMotionListener{
    BufferedImage img;
    String label;
    Rectangle view;
    NaturalSelection model;
    Point last;
    public SelectionPanel(BufferedImage img, NaturalSelection parent){
        this.img = img;
        view = parent.view;
        model=parent;
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(img,
            0,0,img.getWidth(), img.getHeight(),
            Color.WHITE, this);

        if(label!=null)
            g.drawString(label, 20,img.getHeight()+ 35);

   }

    public void updateLabel(String s){
        label = s;
    }

    public void mouseClicked(MouseEvent e) {
        Point click = e.getPoint();
        model.createPredator(click.x + view.x, click.y+ view.y);
    }

    public void mousePressed(MouseEvent e) {
    }


    public void mouseReleased(MouseEvent e) {
        last = null;
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        Point now = e.getPoint();
        if(last!=null){
            view.x += (last.x-now.x);
            view.y += (last.y-now.y);
        }
        last=now;
    }

    public void mouseMoved(MouseEvent e) {
    }


}
