package Server.Protocol;

import org.junit.Before;
import org.junit.Test;
import Server.Protocol.CPS;
import static org.junit.Assert.*;

/**
 * Created by Denis on 09.06.2016.
 */
public class CPStestTest {
    public static int actual;
    public static byte[] array;
    public static CPS cps;
    public  static  String []actualMSG;


    @Before
    public void init(){

        actual = 15;
        array = null;
        cps = new CPS((byte)0);
        actualMSG = new String[10];

        actualMSG[0] = "none";
        actualMSG[1] = "login/password\\";
        actualMSG[2] = "papa jons";
        actualMSG[3] = "vsia popol";
        actualMSG[4] = "fdfdfdgd;lfgdfgdfgdfg gfghfghfghfghfg";
        actualMSG[5] = "";
        actualMSG[6] = "rer";
        actualMSG[7] = "pfgpgpfgppgfhfgphpfg";
        actualMSG[8] = "]fg]fg[h[fgh]f";
        actualMSG[9] = "1213/2.312/3/23";

    }


    @Test
    public void testToByte() throws Exception {

    }

    @Test
    public void testToCPS() throws Exception {

        for(int i=0;i<10;i++) {
            cps.type = (byte) 0;
            cps.ID_SRC = i*14;
            cps.ID_DEST = i*15;
            cps.MSG_LEN = actualMSG[i].length();
            cps.MSG = actualMSG[i];
            byte[] array = cps.toByte();

            CPS expected = new CPS((byte) 0);
            expected.toCPS(array);

            assertEquals(expected.ID_SRC, cps.ID_SRC);
            assertEquals(expected.ID_DEST, cps.ID_DEST);
            assertEquals(expected.MSG_LEN, cps.MSG_LEN);
            assertEquals(expected.type, cps.type);
            assertEquals(expected.MSG, cps.MSG);
        }

    }

    @Test
    public void testBytetoIntReverse() throws Exception {

        int expected;
        for(int i= 0; i<10000;i++) {
            actual = i*10+13 + i*13;
            array = cps.intToByte(actual);
            expected = cps.BytetoInt(array,0);
            assertEquals(expected, actual);
        }

    }


    @Test
    public void testGetLoginPassword() throws Exception {

        String actualLogin = "login";
        String actualPassword = "password";
        cps.MSG = actualMSG[1];

        String expectedlogin = cps.getLogin();
        String expectedPassword = cps.getPassword();

        assertEquals(expectedlogin, actualLogin);
        assertEquals(expectedPassword, actualPassword);

    }

}