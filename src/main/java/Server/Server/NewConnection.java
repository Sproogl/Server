package Server.Server;

import Server.DBManager.DBManager;
import Server.Protocol.CPS;
import com.sun.org.apache.bcel.internal.generic.SWAP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by Denis on 11.06.2016.
 */
public class NewConnection implements Runnable , INewConnection {

    CPS message;
    Socket socket;
    DBManager dbManager;
    private static Logger log = Logger.getLogger(NewConnection.class.getName());


   public NewConnection(Socket socket) throws IOException {

       dbManager = new DBManager();
       this.socket = socket;



   }

    public void run() {
        InputStream in = null;
        OutputStream out = null;
        byte [] byteMessage = new byte[520];

        try {
            in = socket.getInputStream();
            in.read(byteMessage);


        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        switch (byteMessage[0])
        {

            case (100): {
                message = new CPS(byteMessage);
                registrationUser(socket,message );
                    break;
            }

            case (101): {

                message = new CPS(byteMessage);
                connectionUser(socket,message );

                break;
            }

            case (102) : {
                message = new CPS(byteMessage);
                sendMessage(Server.userSession.get(message.ID_DEST),socket,message);
                break;
            }

            case (103) : {
                message = new CPS(byteMessage);
                disconnectionUser(socket , message);
                break;
            }

            case (104) : {
                message = new CPS(byteMessage);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            case (105) : {
                message = new CPS(byteMessage);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case (107) : {
                message = new CPS(byteMessage);
                addFriend(socket,message,Friends.REQUEST);
                requestonFriend(message);

                break;
            }
            case (108):{
                message = new CPS(byteMessage);
                searchfrend(message);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case (110):{
                message = new CPS(byteMessage);
                addFriend(socket,message,Friends.FRIEND);
                acceptFriend(message);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            default: break;
        }

        try {
            dbManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;

    }

    public void registrationUser(Socket socketUser, CPS messageUser) {

        Integer id=0;
        try {

                OutputStream out = socketUser.getOutputStream();
                id = dbManager.searchUser(messageUser.getLogin(), messageUser.getPassword());
                User user = Server.userSession.get(id);
            if (user == null) {
                messageUser.ID_SRC = id;
                out = socket.getOutputStream();
                out.write(messageUser.toByte());
                out.close();
                socketUser.close();
            }
            else
            {
                if(incorrectDisconnected(user.getSocket(),new CPS(id))){
                    messageUser.ID_SRC = id;
                    out = socket.getOutputStream();
                    out.write(messageUser.toByte());
                }
                out.close();
                socketUser.close();
            }
            }catch(IOException e){
                Server.userSession.remove(id);
                e.printStackTrace();
            }

    }

    public void connectionUser(Socket socketUser, CPS messageUser) {

        try {
        if(Server.userSession.get(message.ID_SRC)==null) {
           User user = new User(message.MSG,message.ID_SRC,
                                socketUser,
                                dbManager.getFriendList(messageUser.ID_SRC));

            onlineFriendsList(socketUser,messageUser,user.getFriend());
            Server.userSession.put(message.ID_SRC, user);

        }
        else{
            messageUser.type = (byte)104;

                OutputStream out = socketUser.getOutputStream();
                out.write(messageUser.toByte());
                out.close();
                socketUser.close();
            }
        } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public void sendMessage(User userDest,Socket socketSrc ,CPS messageDest) {
        try {
        if(userDest == null)
        {

                messageDest.type = 106;
                OutputStream out = Server.userSession.get(messageDest.ID_SRC).getSocket().getOutputStream();
            out.write(messageDest.toByte());

            socketSrc.close();



            return;
        }
        Socket socketDest = userDest.getSocket();
        if(incorrectDisconnected(socketDest,messageDest))
        {


                messageDest.type = 106;
            OutputStream out = Server.userSession.get(messageDest.ID_SRC).getSocket().getOutputStream();
            out.write(messageDest.toByte());
                out.write(messageDest.toByte());
            socketSrc.close();


            return;
        }
                message.type = 102;
                OutputStream Lout = socketDest.getOutputStream();
                Lout.write(message.toByte());
                socketSrc.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFriend(Socket socketUser, CPS messageReqest,int type) {
        int mirrortype =0 ;

        switch (type)
        {
            case (0):
            {
                mirrortype = Friends.FRIEND;
                break;
             }
            case (1):
            {
                mirrortype = Friends.REQUEST;
                break;
            }
        }

        User user1 = Server.userSession.get(messageReqest.ID_SRC);
        User user2 = Server.userSession.get(messageReqest.ID_DEST);



        Friends friend = new Friends(messageReqest.MSG,messageReqest.ID_DEST,type);
        Friends user = new Friends(user1.login,user1.id,mirrortype);

        try {
            CPS srcmessage = null;
            dbManager.addFriend(user, friend);

        }catch (IOException e)
        {

        }

    }

    public void onlineFriendsList(Socket socketUser, CPS messageUser,ArrayList<Friends> friends) {

        try {
            OutputStream out = socketUser.getOutputStream();
            Friends friend;
            friends = dbManager.getFriendList(messageUser.ID_SRC);
            int size = friends.size();

            while(size!=0)
            {
                friend = friends.get(size-1);

                User user = null;
                user =  Server.userSession.get(friend.id);

                if(user != null)
                {
                    if(!incorrectDisconnected(user.getSocket(),new CPS(user.id))&& friend.friendType == Friends.FRIEND ) {
                        Socket friendSocket = user.getSocket();

                        messageUser.type = 105;

                        OutputStream outFriend = friendSocket.getOutputStream();
                        messageUser.MSG += "\0";
                        messageUser.ID_DEST = messageUser.ID_SRC;
                        outFriend.write(messageUser.toByte());

                        messageUser.ID_DEST = friend.id;
                        messageUser.MSG = friend.login + "\0";
                        out.write(messageUser.toByte());
                    }
                    else {
                        messageUser.type = 106;
                        messageUser.ID_DEST=friend.id;
                        messageUser.MSG = friend.login+"\0";
                        out.write(messageUser.toByte());
                    }

                }else
                {
                    messageUser.type = 106;
                    messageUser.ID_DEST=friend.id;
                    messageUser.MSG = friend.login+"\0";
                    out.write(messageUser.toByte());
                }

                size--;
                Thread.sleep(50);
            }

        }catch (IOException e)
        {
            System.err.print(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void disconnectionUser(Socket socketUser, CPS messageUser)  {

            disconnectFriendList(socketUser,messageUser);
            Server.userSession.remove(messageUser.ID_SRC);

        try {
            socketUser.close();
        } catch (IOException e) {
            System.err.print(e);
        }

    }

    public boolean incorrectDisconnected(Socket socketUser, CPS testMessage) {
        int id = testMessage.ID_DEST;
        try{
        if(socketUser==null)
        {
            Server.userSession.remove(id);
            socketUser.close();
            return true;
        }
        else
        {
            message.type=103;
            OutputStream out = socketUser.getOutputStream();
            out.write(testMessage.toByte());
            out.write(testMessage.toByte());
            out.write(testMessage.toByte());
            return false;
        }
        }catch(IOException e){
            Server.userSession.remove(id);
            return true;
        }

    }

    public void searchfrend(CPS message) {

        User user = Server.userSession.get(message.ID_SRC);

        try {
            ArrayList<Friends> users = dbManager.SearchFriend(message.MSG);

            if(user == null)
            {
                return;
            }

            if(incorrectDisconnected(user.getSocket(),message)) {
                return;
            }

            OutputStream out = user.getSocket().getOutputStream();
            int size = users.size();

            while (size!=0)
            {
                if(incorrectDisconnected(user.getSocket(),message)) {
                    return;
                }
                Friends friend = users.get(size-1);
                message.type = 108;
                message.ID_SRC = friend.id;
                message.MSG = friend.login+"\0";
                out.write(message.toByte());
                size--;
            }




        } catch (IOException e) {
            e.printStackTrace();
            incorrectDisconnected(user.getSocket(),message);
        }

    }

    public void requestonFriend(CPS message) {

        User user1 = Server.userSession.get(message.ID_SRC);
        User user2 = Server.userSession.get(message.ID_DEST);

        try
        {
            OutputStream out1 = user1.getSocket().getOutputStream();
        if(user2 != null)
        {
            if(!incorrectDisconnected(user2.getSocket(),message))
            {
                OutputStream out2 = user2.getSocket().getOutputStream();

                message.type = 107;
                message.MSG = user1.login+"\0";
                out2.write(message.toByte());


                }

            }
            OutputStream out = user1.getSocket().getOutputStream();
            message.type = 106;
            message.ID_SRC = message.ID_DEST;
            message.MSG = user2.login+"\0";
            out.write(message.toByte());

        }
     catch (IOException e) {
        e.printStackTrace();
    }

    }

    public void acceptFriend(CPS message) {
        User user1 = Server.userSession.get(message.ID_SRC);
        User user2 = Server.userSession.get(message.ID_DEST);

        try
        {
            OutputStream out1 = user1.getSocket().getOutputStream();
            if(user2 != null)
            {
                if(!incorrectDisconnected(user2.getSocket(),message))
                {
                    OutputStream out2 = user2.getSocket().getOutputStream();

                    message.type = 105;
                    message.MSG = user1.login+"\0";
                    message.ID_DEST = user1.id;
                    message.ID_SRC = user2.id;
                    out2.write(message.toByte());

                    OutputStream out = user1.getSocket().getOutputStream();
                    message.type = 105;
                    message.ID_DEST = user2.id;
                    message.ID_SRC = user1.id;
                    message.MSG = user2.login+"\0";
                    out.write(message.toByte());

                        return;
                }

            }
            OutputStream out = user1.getSocket().getOutputStream();
            message.type = 106;
            message.MSG = user2.login+"\0";
            out.write(message.toByte());

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void disconnectFriendList(Socket socketUser, CPS messageUser){
        User user = Server.userSession.get(messageUser.ID_SRC);
        messageUser.ID_DEST = messageUser.ID_SRC;
        messageUser.type = 106;
        ArrayList<Friends> friendList = null;
        OutputStream outL = null;
        if(user!=null)
        {
            friendList = user.getFriend();
            int i = friendList.size();
            while(i!=0){
                try {
                    User userF = Server.userSession.get(friendList.get(i-1).id);
                    if(userF !=null) {
                        outL = userF.getSocket().getOutputStream();
                        outL.write(messageUser.toByte());
                    }
                }catch (IOException e)
                {

                }
                i--;
            }
        }


    }
}
