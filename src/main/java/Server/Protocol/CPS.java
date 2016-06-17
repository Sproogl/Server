package Server.Protocol;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;


/**
 * Protocol sproogl
 */
public class CPS implements ICPS {
    public byte type;
    public int ID_SRC;
    public int ID_DEST;
    public int MSG_LEN;
    public String MSG;

    public CPS(byte type){

        this.type = type;
        ID_DEST = 0;
        ID_SRC=0;
        MSG_LEN = 10;
        MSG = "disconnect";

    }

    public  CPS(int id){
        type = 104;
        ID_DEST = id;
        ID_SRC = id;
        MSG_LEN = 5;
        MSG = "test\0";


    }
    public CPS(byte []array){
         this.toCPS(array);
    }

    public byte[] toByte(){
        byte [] arraytype = new byte[1];
        arraytype[0]=type;

        byte []arrMsg = contact(arraytype,intToByte(ID_SRC));
        arrMsg= contact(arrMsg,intToByte(ID_DEST));
        arrMsg = contact(arrMsg,intToByte(MSG_LEN));
        arrMsg = contact(arrMsg,new byte[3]);


        try {
            arrMsg = contact(arrMsg,MSG.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        int size = arrMsg.length;
        if(arrMsg[size-1]!= 0 )
        {
            if(arrMsg[size-1]==10)
            {
                arrMsg[size-1]=0;
            }
            else
            {
                arrMsg = contact(arrMsg,new byte[1]);
            }

        }

        return arrMsg;
    }

    public void toCPS(byte[] array)
    {
        type = array[0];
        ID_SRC = BytetoInt(array,1);
        ID_DEST = BytetoInt(array,5);
        MSG_LEN = BytetoInt(array,9);

        if(MSG_LEN>200)
            MSG_LEN=200;

        byte []msg = new byte[MSG_LEN];
        System.arraycopy(array,16,msg,0,MSG_LEN);
        MSG = new String(msg, StandardCharsets.UTF_8);
    }


    public int BytetoInt(byte[] array, int offset){

        int i = ((array[offset] & 0xFF) << 24)
                + ((array[offset+1] & 0xFF) << 16)
                + ((array[offset+2] & 0xFF) << 8)
                + (array[offset+3] & 0xFF);

        return  i;
    }

    public byte[] intToByte(int val){
        byte [] buf = new byte[4];
        buf[0] = (byte) (val >>> 24);
        buf[1] = (byte) (val >>> 16);
        buf[2] = (byte) (val >>> 8);
        buf[3] = (byte) val;

        return buf;
    }

    public String getLogin() {
        int index = MSG.indexOf("/");
        String login = MSG.substring(0,index);
        return login;
    }

    public String getPassword() {
        int indexstart = MSG.indexOf("/");
        int indexend = MSG.indexOf("\\");
        String password = MSG.substring(indexstart+1,indexend);
        return password;
    }

    private byte[] contact(byte[] A, byte[] B) {
        int aLen = A.length;
        int bLen = B.length;
        byte[] C= new byte[aLen+bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }


}
