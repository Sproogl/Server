package Server.Server;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Denis on 12.06.2016.
 */
public class User {
   public String login;
    public Integer id;
    private Socket socket;
    private ArrayList<Friend> friendsArray;
    public  User(String Login, Integer Id, Socket socket, ArrayList<Friend> friendsArray){
        this.socket = socket;
        login = Login;
        id = Id;
        this.friendsArray = friendsArray;
    }
    public Socket getSocket(){
        if(socket.isClosed())
        {
            return null;
        }
        return socket;
    }

    public ArrayList<Friend> getFriend(){
        return friendsArray;
    }

    public void addFriendtoList(Friend friend){
        friendsArray.add(friend);
    }
}
