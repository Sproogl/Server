package Server.Protocol;

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

    }

    public byte[] toByte(){

        byte []arrayID_SRC = intToByte(ID_SRC);
        byte []arrayID_DEST = intToByte(ID_DEST);
        byte []arrayMSG_LEN = intToByte(MSG_LEN);
        byte [] arrayID = contact(arrayID_SRC,arrayID_DEST);
        byte [] arrayIDandMSG_LEN = contact(arrayID,arrayMSG_LEN);
        byte [] arrayTYPE = new byte[1];
        byte []arrayMSG = MSG.getBytes();
        arrayMSG = contact(new byte[3],arrayMSG);
        arrayTYPE[0]=type;
        byte []arrayINFO = contact(arrayTYPE,arrayIDandMSG_LEN);

        return contact(arrayINFO,arrayMSG);
    }

    public void toCPS(byte[] array)
    {
        type = array[0];
        ID_SRC = BytetoInt(array,1);
        ID_DEST = BytetoInt(array,5);
        MSG_LEN = BytetoInt(array,9);
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

    private byte[] contact(byte[] A, byte[] B) {
        int aLen = A.length;
        int bLen = B.length;
        byte[] C= new byte[aLen+bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }
}
