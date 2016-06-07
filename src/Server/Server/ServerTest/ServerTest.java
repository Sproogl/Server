package Server.Server.ServerTest;

import Server.Server.Server;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Denis on 07.06.2016.
 */
public class ServerTest extends Assert {


    @Test
    public void testBaseServer() throws IOException, ClassNotFoundException {

        int actual = 1;
        int expected;
        Server server = new Server(1332);
        expected = server.start();
        assertEquals(expected, actual);
        expected = server.stop();
        assertEquals(expected, actual);
        }
    }

