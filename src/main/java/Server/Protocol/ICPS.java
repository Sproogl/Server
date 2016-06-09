package Server.Protocol;

/**
 * Created by Denis on 07.06.2016.
 */
public interface ICPS {

    /**
     * Method converts this object to array bytes
     * @return byte[]
     */
    byte[] toByte();

    /**
     * Method converts arrays bytes to CPS
     * @return CPS
     */
    void toCPS(byte[] array);

    /**
     *
     * @param array
     * @param offset
     * @return
     */
    int BytetoInt(byte[] array,int offset);


    /**
     *
     * @param val
     * @return
     */
    byte[] intToByte(int val);

}
