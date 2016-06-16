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
    private ArrayList<Friends> friendsArray;
    public  User(String Login, Integer Id, Socket socket, ArrayList<Friends> friendsArray){
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

    public ArrayList<Friends> getFriend(){
        return friendsArray;
    }

    public void addFriendtoList(Friends friend){
        friendsArray.add(friend);
    }
}
