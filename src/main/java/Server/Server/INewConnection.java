package Server.Server;

import Server.Protocol.CPS;

import java.net.Socket;

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
     * @param socketDest # this socket of Server.userSession! #
     * @param messageDest
     */
    void sendMessage(Socket socketDest,Socket socketSrc ,CPS messageDest);


    void addFriend(Socket socketUser, CPS messageReqest);

    void onlineFriendsList(Socket socketUser , CPS messageUser);


    /**
     * remove the user from the Server.userSession that has id messageUser.ID_SRC
     * @param socketUser
     * @param messageUser
     */
    void disconnectionUser(Socket socketUser , CPS messageUser);

}
