package Server.Server;
import Server.Protocol.CPS;

import java.net.*;
import java.io.*;


/**
 * Created by Denis on 06.06.2016.
 */
public class Server {

    private CPS CPSmessage;
    private ServerSocket socket;
    private int port;

    public Server(int port) {

        this.port = port;
        CPSmessage = new CPS((byte)0);
    }

    public int start() throws IOException, ClassNotFoundException, NullPointerException {
        InputStream in = null;
        OutputStream out = null;

        socket = new ServerSocket(port);

        Socket newsock = socket.accept();

        in = newsock.getInputStream();

        byte[] mesg = new byte[520];
        in.read(mesg);

        CPSmessage.toCPS(mesg);

        CPSmessage.ID_SRC = 100;
        out = newsock.getOutputStream();
        out.write(CPSmessage.toByte());
        in.close();
        out.close();
        newsock.close();
        return 1;
    }

    public int stop() {
        return 1;
    }



}
