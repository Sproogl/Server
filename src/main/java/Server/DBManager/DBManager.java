package Server.DBManager;

import Server.Server.Friend;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

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
        String md5Password = hashingMd5(password);
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
                                                        md5Password+"','"+
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

        String md5Password = hashingMd5(password);
        String reqest = "select * from users where login ='"
                                                    +login
                                                    +"' and password = '"
                                                    +md5Password+"';";
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

    public void addFriend(Friend user, Friend friend) throws IOException {

        if(user.friendType != Friend.FRIEND) {
            String reqest = "insert into friends(user_id,friend_id,friend_login,state) values(" +
                    user.id +
                    "," +
                    friend.id + ",'" +
                    friend.login + "',"
                    + user.friendType + ");";
            String requestmirror = "insert into friends(user_id,friend_id,friend_login,state) values(" +
                    friend.id +
                    "," +
                    user.id + ",'" +
                    user.login + "',"
                    + friend.friendType + ");";
            try {
                statement.execute(reqest);
                statement.execute(requestmirror);
            } catch (SQLException e) {
                throw new IOException(e);
            }
        }
        else
        {
            String reqest = "update friends set state = "
                    + user.friendType + " where user_id =" + user.id + " and friend_id = " + friend.id + ";";


            String requestmirror = "update friends set state = "
                    + user.friendType + " where user_id =" + friend.id + " and friend_id = " + user.id + ";";
            try {
                statement.execute(reqest);
                statement.execute(requestmirror);
            } catch (SQLException e) {
                throw new IOException(e);
            }
        }
    }

    public void deleteFriend(Friend user, Friend friend) throws IOException {
        String reqest = "delete from friends where user_id="+
                user.id+
                " and friend_id = "+
                friend.id+";";
        String requestmirror = "delete from friends where user_id="+
                friend.id+
                " and friend_id = "+
                user.id+";";
        try {
            statement.execute(reqest);
            statement.execute(requestmirror);
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public ArrayList<Friend> getFriendList(int user_id) throws  IOException {
        ArrayList<Friend> userlist = null;
        String reqest = "select * from friends where user_id ='"
                +user_id+"';";
        try {
            ResultSet resultSet = statement.executeQuery(reqest);
            userlist = new ArrayList<Friend>();
            while (resultSet.next())
            {
                userlist.add(new Friend(resultSet.getString(3),resultSet.getInt(2),resultSet.getInt(4)));
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }

        return userlist;
    }

    public ArrayList<Friend> SearchFriend(String request) throws IOException {

        request = "select * from users where login RLIKE '^"+request+"' LIMIT 10;";
        ResultSet resultSet;
        ArrayList<Friend> userlist= new ArrayList<Friend>();
        try {

            resultSet = statement.executeQuery(request);
            while (resultSet.next())
            {
                userlist.add(new Friend(resultSet.getString("login"),resultSet.getInt("id"),Friend.UNACCEPTED));
            }
            resultSet.close();

        }catch (SQLException e)
        {
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

    public String hashingMd5(String password) {
        String salt =  "p!th5du#gj2+g6";
        password = salt + password;
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(password.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while( md5Hex.length() < 32 ){
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }
}
