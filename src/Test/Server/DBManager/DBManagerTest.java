package Server.DBManager;

import org.junit.Test;

import java.io.IOException;

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
            db.addUser("den","12345","den@mail.ru");
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
            db.addFriend(10,15);
            db.close();
        } catch (IOException e) {
            System.err.print(e);
            assert(false);
        }

    }

}