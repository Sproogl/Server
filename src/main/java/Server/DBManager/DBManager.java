package Server.DBManager;
import java.io.IOException;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Denis on 09.06.2016.
 */
public class DBManager implements IDBManager {

    Connection connection;
    Statement statement;

    DBManager() throws IOException {

        try {

            connection = DriverManager.getConnection(URL,LOGIN,PASSWORD);
            statement = connection.createStatement();
            if(connection.isClosed())
            {
                throw new IOException("error connection");
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }

    }


    public int addUser(String login, String password, String email) throws IOException {

        String reqest = "insert into users(login,password,mail) values('"+
                                                                login+
                                                                  "','"+
                                                         password+"','"+
                                                                email+"');";
        try {
            statement.execute(reqest);
        } catch (SQLException e) {
            throw new IOException(e);
        }
        return 0;
    }

    public int SearchUser(String login, String password) {

        return 0;
    }


    public void addFriend(int id_user, int id_newFriend) throws IOException {

        String reqest = "insert into friends(user_id,friend_id) values("+
                id_user+
                ","+
                id_newFriend+");";
        try {
            statement.execute(reqest);
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public ArrayList<Integer> getFriendList(int user_id) {

        return null;
    }

    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
