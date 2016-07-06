package Server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by Denis on 11.06.2016.
 */
public class ServerListeningThread extends Thread implements IServerListeningThread {

    private ServerSocket serverSocket;
    private int port;
    private static Logger log = Logger.getLogger(ServerListeningThread.class.getName());
    private ThreadPoolExecutor executor;
    public ServerListeningThread(int port) {

        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.port = port;
    }

   @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(port);
        }catch (IOException e) {
            executor.shutdownNow();
            return;
        }



        while(!Thread.interrupted()){

            try {
                NewConnection task = new NewConnection(serverSocket.accept());

                executor.submit(task);

            }catch (IOException e)
            {
                System.err.print(e);
                close();
            }
        }
       close();


   }

    private void close(){
        try {
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            boolean statusTask =  executor.awaitTermination(5, TimeUnit.SECONDS);
            if(!statusTask)
            {
                executor.shutdownNow();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    }

