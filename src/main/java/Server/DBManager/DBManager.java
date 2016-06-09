package Server.DBManager;

import java.util.ArrayList;

/**
 * Created by Denis on 09.06.2016.
 */
public class DBManager implements IDBManager {


    DBManager(){


    }


    public int addUser(String login, String password, String email) {
        return 0;
    }

    public int SearchUser(String login, String password) {
        return 0;
    }

    public boolean addFriend(int id_user, int id_newFriend) {
        return false;
    }

    public ArrayList<Integer> getFriendList(int user_id) {
        return null;
    }
}
