package Server.Server;

import Server.Protocol.CPS;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


/**
 * Created by Denis on 06.06.2016.
 */
public class Server {

    private int port;
    public static ConcurrentHashMap<Integer,User> userSession;
    private static Logger log = Logger.getLogger(Server.class.getName());
    ServerListeningThread serverThread;
    public static long epoch;
    public Server(int port) {

        this.port = port;
        userSession = new ConcurrentHashMap<Integer,User>();
        serverThread = new ServerListeningThread(port);
    }

    public void start() throws IOException, ClassNotFoundException, NullPointerException {


        serverThread.start();

        if(!serverThread.isAlive()){
            throw  new IOException("error server");
        }

        System.out.println("Server started \n");

        Scanner scan = new Scanner(System.in);
        String s = "1";
        while(!s.equals(new String("0"))) {

            System.out.print("Enter 0 for stopped server : ");
            s = scan.nextLine();

        }
        stop();
        System.out.println("Server stopped: ");

    }





    public void stop() {
        serverThread.interrupt();
        System.out.println("wait... ");
        try {
            Socket closeSocket = new Socket(InetAddress.getByName("127.0.0.1"),1332);
            OutputStream out = closeSocket.getOutputStream();
            out.write(new CPS((byte)104).toByte());
            out.close();
            closeSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(serverThread.isAlive())
        {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
