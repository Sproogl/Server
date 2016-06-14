package Server.DBManager;
import Server.Server.Friends;
import Server.Server.Server;
import Server.Server.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by Denis on 09.06.2016.
 */
public class DBManager implements IDBManager {

    Connection connection;
    Statement statement;

    public DBManager() throws IOException {

        try {
            connection = DriverManager.getConnection(URL,LOGIN,PASSWORD);
            statement = connection.createStatement();
            if(connection.isClosed())
            {
                throw new IOException("error connection to data base");
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }

    }


    public int addUser(String login, String password, String email) throws IOException {
        int id =0;
        String reqest = "select * from users where login ='"
                +login+"';";
        try {
            ResultSet resultSet = statement.executeQuery(reqest);
            if (resultSet.next()){
               throw new IOException("login busy");
            }
        } catch (SQLException e) {
           throw new IOException(e);
        }


        reqest = "insert into users(login,password,mail) values('"+
                                                                login+
                                                                  "','"+
                                                         password+"','"+
                                                                email+"');";
        try {
            statement.execute(reqest);
            id = searchUser(login, password);
        } catch (SQLException e) {
            throw new IOException(e);
        }
        return id;
    }

    public int searchUser(String login, String password) throws IOException {
        int id=0;
        String reqest = "select * from users where login ='"
                                                    +login
                                                    +"' and password = '"
                                                    +password+"';";
        try {
            ResultSet resultSet = statement.executeQuery(reqest);
            if(resultSet.next()) {
                id = resultSet.getInt("id");
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
            return id;
    }

    public void addFriend(User user, User friend) throws IOException {

        String reqest = "insert into friends(user_id,friend_id,friend_login) values("+
                user.id+
                ","+
                friend.id+",'"+
                friend.login+"');";
        String reqestmirror = "insert into friends(user_id,friend_id,friend_login) values("+
                friend.id+
                ","+
                user.id+",'"+
                user.login+"');";
        try {
            statement.execute(reqest);
            statement.execute(reqestmirror);
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public void deleteFriend(User user, User friend) throws IOException {
        String reqest = "delete from friends where user_id="+
                user.id+
                " and friend_id = "+
                friend.id+";";
        String reqestmirror = "delete from friends where user_id="+
                friend.id+
                " and friend_id = "+
                user.id+";";
        try {
            statement.execute(reqest);
            statement.execute(reqestmirror);
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public ArrayList<Friends> getFriendList(int user_id) throws  IOException {
        ArrayList<Friends> userlist = null;
        String reqest = "select * from friends where user_id ='"
                +user_id+"';";
        try {
            ResultSet resultSet = statement.executeQuery(reqest);
            userlist = new ArrayList<Friends>();
            while (resultSet.next())
            {
                userlist.add(new Friends(resultSet.getString(3),resultSet.getInt(2)));
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }

        return userlist;
    }

    public void close() throws IOException {
        try {
            connection.close();
            statement.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
