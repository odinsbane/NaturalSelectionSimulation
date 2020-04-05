package naturalselection;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;

/**
 * Will run a server to mitigate communication between the stand alone simulation and a client/interface
 *
 * 
 * User: melkor
 * Date: Jun 15, 2010
 * Time: 9:48:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class NaturalServer {
    Charset charset;
    CharsetEncoder encoder;
    CharsetDecoder decoder;
    ByteBuffer buffer;
    ServerSocket server;

    NaturalSelection selection;

    public boolean INITIALIZED;
    public NaturalServer(){
        charset = Charset.forName("UTF-8");
        encoder = charset.newEncoder();
        decoder = charset.newDecoder();

        buffer = ByteBuffer.allocate(512);
        try{
            server = new ServerSocket(8080);
            INITIALIZED =true;
        }catch(Exception e){
            INITIALIZED =false;
        }

    }
    public void start() {
        if(!INITIALIZED) throw new RuntimeException("could not start server.");
            for (;;) {
                System.out.println("waiting");
                try(Socket client = server.accept()){
                    DataInputStream stream = new DataInputStream(client.getInputStream());
                    DataOutputStream out = new DataOutputStream(client.getOutputStream());
                    while(true){
                        System.out.println("writing");
                        out.write(NetCommands.READY);
                        System.out.println("written");

                        int cmd = stream.read();
                        System.out.println("read: " + cmd);
                        switch(cmd){
                            case NetCommands.SNAPSHOT:
                                setSnapShot(out);
                                break;
                            case NetCommands.STATUS:
                                out.write(NetCommands.STRING_MESSAGE);
                                out.writeUTF("running");
                                break;
                            default:
                                out.write(NetCommands.STRING_MESSAGE);
                                out.writeUTF("unknown command");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }



    public static void main(String[] args){
        NaturalServer nas = new NaturalServer();
        NaturalSelection selection = new NaturalSelection(null, false);
        nas.startSelection(selection);
        try{
            nas.start();
        } catch(Exception e){
            System.exit(1);
        }
        System.exit(0);
    }

    public void setSnapShot(DataOutputStream out){
        BufferedImage img = selection.createSnapshot();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "PNG", bos);
            int count = bos.size();
            out.write(NetCommands.BYTE_MESSAGE);
            out.writeInt(count);
            out.write(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startSelection(NaturalSelection selection) {
        this.selection = selection;
        selection.start();
    }

}
