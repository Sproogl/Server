package Server.DBManager;

import Server.Server.Friend;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Denis on 09.06.2016.
 */
public interface IDBManager {

    public static final String URL        =  "jdbc:mysql://127.0.0.1:3306/userssproogl";
    public static  final String LOGIN     =  "root";
    public  static  final String PASSWORD =  "*****";



    /**
     * Added new user to DB and returned his id ##method for testing##
     * @param login
     * @param password
     * @param email
     * @return id
     */
    int addUser(String login,String password,String email) throws IOException;


    /**
     * Searched user by login and password and if user found returned his id
     * else returned 0
     * @param login
     * @param password
     * @return id
     */
    int searchUser(String login,String password) throws IOException;

    /**
     *  added new friend to DB
     * @param user
     * @param friend
     */
    void addFriend(Friend user, Friend friend) throws IOException;



    /**
     * deleted user from table "friends"
     * @param user
     * @param friend
     */
    void deleteFriend(Friend user, Friend friend) throws IOException;


    /**
     *  returned arraylist friends this user
     * @param user_id
     * @return arraylist friend
     */
    ArrayList<Friend> getFriendList(int user_id) throws IOException;


    /**
     * Searching users by login, in userssproogl
     * @param request
     * @return
     */
    ArrayList<Friend> SearchFriend(String request) throws IOException;


    /**
     * closed connection
     */
    void close() throws IOException;


    /**
     * hashing password
     * @param password
     * @return
     */
     String hashingMd5(String password);


}
