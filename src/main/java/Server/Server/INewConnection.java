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

    /**
     * add friend in date base
     * @param socketUser
     * @param messageReqest
     * @param type
     */
    void addFriend(Socket socketUser, CPS messageReqest, int type);

    /**
     * It sends a message to a friend that the user went to the network
     * @param socketUser
     * @param messageUser
     * @param friends
     */
    void onlineFriendsList(Socket socketUser , CPS messageUser,ArrayList<Friend> friends);


    /**
     * remove the user from the Server.userSession that has id messageUser.ID_SRC
     * @param socketUser
     * @param messageUser
     */
    void disconnectionUser(Socket socketUser , CPS messageUser);

    /**
     * check the socket operation and remove users if socket doesn't work
     * @param socketUser
     * @param testMessage
     * @return
     */
    boolean incorrectDisconnected(Socket socketUser,CPS testMessage);


    /**
     * search users in data base
     * @param message
     */
    void searchfrend(CPS message);


    /**
     * sends message.ID_DEST a request to add a Friend
     * @param message
     */
    void requestonFriend(CPS message);


    /**
     * processing confirmation message for adding friends and add userSession
     * @param message
     */
    void acceptFriend(CPS message);


}
