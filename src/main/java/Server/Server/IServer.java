package Server.Server;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Denis on 07.06.2016.
 */
public interface IServer {


    /**
     * Started ServerListeningThread in new thread
     * then wait input from console
     * @throws IOException
     */
    void start() throws IOException;


    /**
     * stopped ServerListeningServer.
     * @throws IOException
     */
    void stop() throws  IOException;


}
