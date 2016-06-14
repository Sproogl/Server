package Server.DBManager;

import Server.Server.User;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Denis on 09.06.2016.
 */
public class DBManagerTest {

    @Test
    public void baseTest(){

        try {
            DBManager db = new DBManager();
            db.close();
        } catch (IOException e) {
            System.err.print(e);
            assert(false);
        }


    }

    @Test
    public  void AdduserTest(){
        try {
            DBManager db = new DBManager();
           db.addUser("denis3222","12345","den@mail.ru");
            db.close();
        } catch (IOException e) {
            System.err.print(e);
            assert(false);
        }

    }

    @Test
    public  void AddfriendTest(){

        try {
            DBManager db = new DBManager();
            db.addFriend(new User("admin",1,null,null),new User("den",2,null,null));
            //db.deleteFriend(new User("den",1,null,null),new User("tom",2,null,null));
            db.close();
        } catch (IOException e) {
            System.err.print(e);
            assert(false);
        }


    }


    @Test
    public  void searchFriendTest(){
        int id=0;
        try {
            DBManager db = new DBManager();
            id = db.searchUser("denis123","12345");
            db.close();
        } catch (IOException e) {
            System.err.print(e);
            assert(false);
        }
        if(id==0)
            assert(false);
    }

    @Test
    public  void getFrienListdTest(){
        ArrayList<User> id=null;
        try {
            DBManager db = new DBManager();
            //id = db.getFriendList(1);
            db.close();
        } catch (IOException e) {
            System.err.print(e);
            assert(false);
        }
        if(id.isEmpty())
            assert(false);
    }

}