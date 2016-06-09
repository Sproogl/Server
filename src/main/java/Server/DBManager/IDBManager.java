package Server.DBManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Denis on 09.06.2016.
 */
public interface IDBManager {

    public static final String URL        =  "jdbc:mysql://127.0.0.1:3306/userssproogl";
    public static  final String LOGIN     =  "root";
    public  static  final String PASSWORD =  "******";


    /**
     * Added new user to DB and returned his id
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
    int SearchUser(String login,String password);

    /**
     *  added new friend to DB
     * @param id_user
     * @param id_newFriend
     */
    void addFriend(int id_user , int id_newFriend) throws IOException;

    /**
     *  returned arraylist friends this user
     * @param user_id
     * @return arraylist friend
     */
    ArrayList<Integer> getFriendList(int user_id);

    /**
     * closed connection
     */
    void close() throws IOException;



}
