package Server;

import Server.Server.Server;

import java.io.IOException;

/**
 * Created by Denis on 06.06.2016.
 */
public class Main  {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NullPointerException {

    Server server = new Server(1332);
        try {
            server.start();
        }catch (IOException e)
        {
            System.err.print(e);
        }
    return;

}

}
