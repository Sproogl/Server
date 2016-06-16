package Server.Server;

import Server.Protocol.CPS;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Denis on 12.06.2016.
 */
public interface INewConnection {

    /**
     * Registration user in network.
     * The server receives a message from a client,
     * and if the username and password are valid on the same socket sent its id.
     * @param socketUser socket
     * @param messageUser
     */
    void registrationUser(Socket socketUser,CPS messageUser);


    /**
     * Added SocketUser and messageUser.ID_SRC in Server.userSession
     * @param socketUser
     * @param messageUser
     */
    void connectionUser(Socket socketUser, CPS messageUser);


    /**
     * Send messageDest to socketDest
     * @param messageDest
     */
    void sendMessage(CPS messageDest);


    void addFriend(Socket socketUser, CPS messageReqest, int type);

    void onlineFriendsList(Socket socketUser , CPS messageUser,ArrayList<Friends> friends);


    /**
     * remove the user from the Server.userSession that has id messageUser.ID_SRC
     * @param socketUser
     * @param messageUser
     */
    void disconnectionUser(Socket socketUser , CPS messageUser);

    boolean incorrectDisconnected(Socket socketUser,CPS testMessage);

    void searchfrend(CPS message);

    void requestonFriend(CPS message);

    void acceptFriend(CPS message);


}
