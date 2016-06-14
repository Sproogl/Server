package Server.Server;
import Server.Protocol.CPS;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.Logger;


/**
 * Created by Denis on 06.06.2016.
 */
public class Server {

    private int port;
    public static ConcurrentHashMap<Integer,User> userSession;
    private static Logger log = Logger.getLogger(Server.class.getName());
    ServerListeningThread serverThread;
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
        int s = 1;
        System.out.print("Enter 0 for stopped server : ");
        while(s!=0) {
            s = scan.nextInt();
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
