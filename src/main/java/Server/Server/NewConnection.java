package Server.Server;

import Server.DBManager.DBManager;
import Server.Protocol.CPS;

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
                sendMessage(message);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case (107) : {
                message = new CPS(byteMessage);
                addFriend(socket,message,Friend.REQUEST);
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
                addFriend(socket,message,Friend.FRIEND);
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

            Server.userSession.put(message.ID_SRC, user);
            onlineFriendsList(socketUser,messageUser,user.getFriend());


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

    public void sendMessage(CPS messageDest) {

        User userDest = null;
        User userSrc = Server.userSession.get(messageDest.ID_SRC);
        ArrayList<Friend> friends = userSrc.getFriend();
        int size = friends.size();
        while (size!=0)
        {
            if(friends.get(size-1).id==messageDest.ID_DEST && friends.get(size-1).friendType==Friend.FRIEND)
            {
                userDest = Server.userSession.get(messageDest.ID_DEST);

            }
            size--;
        }

        try {
        if(userDest == null)
        {
            return;
        }
        Socket socketDest = userDest.getSocket();
        if(incorrectDisconnected(socketDest,messageDest))
        {


                messageDest.type = 106;
            OutputStream out = userSrc.getSocket().getOutputStream();
            out.write(messageDest.toByte());
            return;
        }
                message.type = 102;
                OutputStream Lout = socketDest.getOutputStream();
                Lout.write(message.toByte());


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
                mirrortype = Friend.FRIEND;
                break;
             }
            case (1):
            {
                mirrortype = Friend.UNACCEPTED;
                break;
            }
        }

        User user1 = Server.userSession.get(messageReqest.ID_SRC);
        User user2 = Server.userSession.get(messageReqest.ID_DEST);



        Friend friend = new Friend(messageReqest.MSG,messageReqest.ID_DEST,type);
        Friend user = new Friend(user1.login,user1.id,mirrortype);

        try {
            CPS srcmessage = null;
            dbManager.addFriend(user, friend);

        }catch (IOException e)
        {

        }

    }

    public void onlineFriendsList(Socket socketUser, CPS messageUser,ArrayList<Friend> friends) {

        try {
            OutputStream out = socketUser.getOutputStream();
            Friend friend;
            friends = dbManager.getFriendList(messageUser.ID_SRC);
            int size = friends.size();

            while(size!=0)
            {
                friend = friends.get(size-1);

                User user = null;
                user =  Server.userSession.get(friend.id);

                switch (friend.friendType)
                {

                    case (0) :{
                        if(user != null
                           && !incorrectDisconnected(user.getSocket(),new CPS(user.id)))
                        {

                            Socket friendSocket = user.getSocket();
                            OutputStream outFriend = friendSocket.getOutputStream();

                            messageUser.type = 105;
                            outFriend.write(messageUser.toByte());

                            messageUser.ID_SRC = friend.id;
                            messageUser.MSG = friend.login;
                            out.write(messageUser.toByte());


                        }else
                        {
                            messageUser.type = 106;
                            messageUser.ID_SRC=friend.id;
                            messageUser.MSG = friend.login;
                            out.write(messageUser.toByte());
                        }

                        break;
                    }

                    case (1):{
                        messageUser.type = 107;
                        messageUser.ID_SRC=friend.id;
                        messageUser.MSG = friend.login;
                        out.write(messageUser.toByte());
                        break;
                    }

                    case  (2):{
                        messageUser.type = 106;
                        messageUser.ID_SRC=friend.id;
                        messageUser.MSG = friend.login;
                        out.write(messageUser.toByte());
                        break;
                    }


                }

                size--;
            }

        }catch (IOException e) {
            System.err.print(e);
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
            ArrayList<Friend> users = dbManager.SearchFriend(message.MSG);

            if(user == null)
            {
                return;
            }

            if(incorrectDisconnected(user.getSocket(),message)) {
                return;
            }

            ArrayList<Friend> friendsSRC = user.getFriend();
            int size = friendsSRC.size();
            int size2 = users.size();
            OutputStream out = user.getSocket().getOutputStream();
            for(int i = 0 ; i<size ;i++ )
            {
                for (int j = 0; j < size2; j++)
                {
                    if(friendsSRC.get(i).id == users.get(j).id || users.get(j).id == user.id)
                    {

                        users.remove(j);
                        size2--;
                        break;
                    }
                }
            }

            size = users.size();
            if(incorrectDisconnected(user.getSocket(),message)) {
                return;
            }

            while (size!=0)
            {
                message.type = 108;
                message.ID_SRC = users.get(size-1).id;
                message.MSG = users.get(size-1).login;
                out.write(message.toByte());
                size--;
            }

        } catch (IOException e) {
            incorrectDisconnected(user.getSocket(),message);
        }

    }

    public void requestonFriend(CPS message) {

        User user1 = Server.userSession.get(message.ID_SRC);
        User user2 = Server.userSession.get(message.ID_DEST);



        try
        {
            OutputStream out = user1.getSocket().getOutputStream();
            int buf;
            buf = message.ID_SRC;
            message.ID_SRC = message.ID_DEST;
            message.ID_DEST = buf;
            message.type = 106;
            out.write(message.toByte());
            buf = message.ID_SRC;
            message.ID_SRC = message.ID_DEST;
            message.ID_DEST = buf;

            OutputStream out1 = user1.getSocket().getOutputStream();
        if(user2 != null)
        {
            if(!incorrectDisconnected(user2.getSocket(),message))
            {
                OutputStream out2 = user2.getSocket().getOutputStream();

                message.type = 107;
                message.MSG = user1.login;
                out2.write(message.toByte());


                }

            }
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
            Friend friend = new Friend(message.MSG,message.ID_DEST,Friend.FRIEND);
            user1.addFriendtoList(friend);
            if(user2 != null)
            {

                if(!incorrectDisconnected(user2.getSocket(),message))
                {
                    friend = new Friend(user1.login,user1.id,Friend.FRIEND);
                    user2.addFriendtoList(friend);
                    OutputStream out2 = user2.getSocket().getOutputStream();

                    message.type = 105;
                    message.MSG = user1.login;
                    message.ID_SRC = user1.id;
                    message.ID_DEST = user2.id;
                    out2.write(message.toByte());

                    OutputStream out = user1.getSocket().getOutputStream();
                    message.type = 105;
                    message.ID_SRC = user2.id;
                    message.ID_DEST = user1.id;
                    message.MSG = user2.login;
                    out.write(message.toByte());

                        return;
                }

            }
            OutputStream out = user1.getSocket().getOutputStream();
            message.type = 106;
            message.ID_SRC = message.ID_DEST;
            out.write(message.toByte());

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void disconnectFriendList(Socket socketUser, CPS messageUser){
        User user = Server.userSession.get(messageUser.ID_SRC);
        messageUser.type = 106;
        ArrayList<Friend> friendList = null;
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
