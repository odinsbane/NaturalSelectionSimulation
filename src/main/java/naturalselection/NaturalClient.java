package naturalselection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This will interface a server to recieve information about position and such.
 * 
 * User: melkor
 * Date: Jun 15, 2010
 * Time: 10:43:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class NaturalClient implements Runnable{
    JButton butt;
    JTextField field;
    JLabel label;
    JPanel display;
    BlockingQueue<Runnable> messages = new LinkedBlockingDeque<>(10);
    DataOutputStream out;
    DataInputStream in;
    BufferedImage buffer;
    NaturalClient(){
        butt = new JButton("send");
        butt.addActionListener((evt)->{
            messages.add(()-> {
                try {
                    requestSnapShot();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            });
        display = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                if(buffer!=null){
                    g.drawImage(buffer, 0, 0, this);
                }
            }

            @Override
            public Dimension getPreferredSize(){
                if(buffer!=null){
                    return new Dimension(buffer.getWidth(), buffer.getHeight());
                }
                else return new Dimension(200, 200);
            }
            @Override
            public Dimension getMinimumSize(){
                if(buffer!=null){
                    return new Dimension(buffer.getWidth(), buffer.getHeight());
                }
                else return new Dimension(200, 200);
            }@Override
            public Dimension getMaximumSize(){
                if(buffer!=null){
                    return new Dimension(buffer.getWidth(), buffer.getHeight());
                }
                else return new Dimension(200, 200);
            }
        };
        label = new JLabel("...");

        label.setPreferredSize(new Dimension(400,50));
        field = new JTextField();

    }

    public void requestSnapShot() throws IOException {
        out.write(NetCommands.SNAPSHOT);
        int resp = in.read();
        if(resp!=NetCommands.BYTE_MESSAGE){
            System.out.println("error");
            return;
        }
        int size = in.readInt();
        byte[] img = new byte[size];
        in.read(img);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(img));
        buffer = image;
        display.repaint();
    }

    public void run() {
        JFrame f = new JFrame("client");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = f.getContentPane();
        c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));
        c.add(butt);
        c.add(label);
        c.add(field);
        c.add(display);
        f.pack();
        f.setVisible(true);
        startClientThread();
    }

    private void startClientThread(){
        new Thread(()->{
            while(true){
                int attempts = 0;
                try(Socket socket = new Socket("localhost", 8080)){
                    attempts = 0;
                    System.out.println("connected");
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                    while(true){
                        System.out.println("reading from server.");
                        int status = in.read();
                        System.out.println("read: " + status);
                        switch(status){
                            case NetCommands.STRING_MESSAGE:
                                System.out.println("reading string");
                                String s = in.readUTF();
                                System.out.println(s);
                                break;
                            default:
                                System.out.println("unknown");
                            case NetCommands.READY:
                                Runnable run = messages.take();
                                run.run();
                        }
                    }
                } catch (IOException e) {
                    try{
                        if(attempts>10){
                            System.out.println("max attempts exceeded. ");
                            return;
                        }
                            System.out.println("awaiting reconnect.");
                        Thread.sleep(500);
                        attempts++;
                    }catch (InterruptedException e2){
                        return;
                    }
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }).start();
    }


    public static void main(String[] args) {
        NaturalClient x = new NaturalClient();
        SwingUtilities.invokeLater(x);


    }

}
