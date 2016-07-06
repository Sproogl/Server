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
    Socket socket;
    DBManager dbManager;
    private Logger log = Logger.getLogger(NewConnection.class.getName());


   public NewConnection(Socket socket) throws IOException {


       this.socket = socket;



   }

    public void run() {

        CPS message = null;
        InputStream in = null;
        OutputStream out = null;
        long startTime = System.currentTimeMillis();
        byte [] byteMessage = new byte[520];

        try {
            in = socket.getInputStream();
            in.read(byteMessage);
            Server.epoch = System.currentTimeMillis();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        switch (byteMessage[0])
        {

            case (TypeMessage.REGISTRATION): {
                try {
                    dbManager = new DBManager();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                message = new CPS(byteMessage);
                registrationUser(socket,message );
                    break;
            }

            case (TypeMessage.CONNECT): {
                try {
                    dbManager = new DBManager();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                message = new CPS(byteMessage);
                connectionUser(socket,message );

                break;
            }

            case (TypeMessage.MESSAGE) : {
                message = new CPS(byteMessage);
                sendMessage(message);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

            case (TypeMessage.DISCONNECT) : {
                message = new CPS(byteMessage);
                disconnectionUser(socket , message);
                break;
            }

            case (TypeMessage.ERROR) : {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case (TypeMessage.REQUESTONFRIEND) : {
                try {
                    dbManager = new DBManager();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                message = new CPS(byteMessage);
                addFriend(socket,message,Friend.REQUEST);
                requestonFriend(message);

                break;
            }
            case (TypeMessage.SEARCHUSER):{
                try {
                    dbManager = new DBManager();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                message = new CPS(byteMessage);
                searchfrend(message);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case (TypeMessage.ACCEPTONFRIEND):{
                try {
                    dbManager = new DBManager();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            default: {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        try {
            dbManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long timeSpent = System.currentTimeMillis() - startTime;
       // log.fine("TYPE : "+ byteMessage[0] + " : " + timeSpent + " millisecond");
        return;

    }

    public void registrationUser(Socket socketUser, CPS messageUser) {

        Integer id=0;
        try {

                OutputStream out = socketUser.getOutputStream();
                id = dbManager.searchUser(messageUser.getLogin(), messageUser.getPassword());
                User user = Server.userSession.get(id);
            if (user == null && id!=0 && messageUser.getLogin() != null) {
                user = new User(messageUser.getLogin(),id,
                        null,
                        dbManager.getFriendList(id),true);

                Server.userSession.put(id, user);

                messageUser.ID_SRC = id;
                out = socket.getOutputStream();
                out.write(messageUser.toByte());
                out.close();
                socketUser.close();
            }
            else
            {
                if(user !=null) {
                    if (incorrectDisconnected(user.getSocket(), new CPS(id))) {
                        messageUser.ID_SRC = id;
                        out = socket.getOutputStream();
                        out.write(messageUser.toByte());
                    }
                    out.close();
                    socketUser.close();
                }
            }
            }catch(IOException e){
                Server.userSession.remove(id);
                e.printStackTrace();
            }

    }

    public void connectionUser(Socket socketUser, CPS messageUser) {

        try {
            User user = Server.userSession.get(messageUser.ID_SRC);
        if(user != null) {
            if(user.statusConnections == true) {
                user = new User(messageUser.MSG, messageUser.ID_SRC,
                        socketUser,
                        dbManager.getFriendList(messageUser.ID_SRC),false);

                Server.userSession.put(messageUser.ID_SRC, user);
                onlineFriendsList(socketUser, messageUser, user.getFriend());
            }

        }
        else{
            messageUser.type = TypeMessage.ERROR;

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
        if(!isConnected(userSrc))
        {
            return;
        }
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

                messageDest.type = TypeMessage.USEROFFLINE;
            OutputStream out = userSrc.getSocket().getOutputStream();
            out.write(messageDest.toByte());
            return;
        }
            messageDest.type = TypeMessage.MESSAGE;
                OutputStream Lout = socketDest.getOutputStream();
                Lout.write(messageDest.toByte());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFriend(Socket socketUser, CPS messageReqest,int type) {
        int mirrortype =0 ;

        switch (type)
        {
            case (Friend.FRIEND):
            {
                mirrortype = Friend.FRIEND;
                break;
             }
            case (Friend.REQUEST):
            {
                mirrortype = Friend.UNACCEPTED;
                break;
            }
        }

        User user1 = Server.userSession.get(messageReqest.ID_SRC);


        Friend friend = new Friend(messageReqest.MSG,messageReqest.ID_DEST,type);
        Friend user = new Friend(user1.login,user1.id,mirrortype);

        try {
            dbManager.addFriend(user, friend);

        }catch (IOException e)
        {

        }

    }

    public void onlineFriendsList(Socket socketUser, CPS messageUser,ArrayList<Friend> friends) {

        try {
            OutputStream out = socketUser.getOutputStream();
            Friend friend;
            int IDuser=0;
            IDuser = messageUser.ID_SRC;
            String loginUser = null;
            loginUser = messageUser.MSG;
            friends = dbManager.getFriendList(messageUser.ID_SRC);
            int size = friends.size();

            while(size!=0)
            {
                friend = friends.get(size-1);

                User user = null;
                user =  Server.userSession.get(friend.id);

                switch (friend.friendType)
                {

                    case (Friend.FRIEND) :{
                        if(user != null
                           && !incorrectDisconnected(user.getSocket(),new CPS(user.id)))
                        {

                            Socket friendSocket = user.getSocket();
                            OutputStream outFriend = friendSocket.getOutputStream();

                            messageUser.type = TypeMessage.USERONLINE;
                            messageUser.ID_SRC = IDuser;
                            messageUser.MSG = loginUser;
                            outFriend.write(messageUser.toByte());

                            messageUser.ID_SRC = friend.id;
                            messageUser.MSG = friend.login;
                            out.write(messageUser.toByte());


                        }else
                        {
                            messageUser.type = TypeMessage.USEROFFLINE;
                            messageUser.ID_SRC=friend.id;
                            messageUser.MSG = friend.login;
                            out.write(messageUser.toByte());
                        }

                        break;
                    }

                    case (Friend.REQUEST):{
                        messageUser.type = TypeMessage.REQUESTONFRIEND;
                        messageUser.ID_SRC=friend.id;
                        messageUser.MSG = friend.login;
                        out.write(messageUser.toByte());
                        break;
                    }

                    case  (Friend.UNACCEPTED):{
                        messageUser.type = TypeMessage.USEROFFLINE;
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
            testMessage.type=TypeMessage.DISCONNECT;
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
                message.type = TypeMessage.SEARCHUSER;
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
            message.type = TypeMessage.USEROFFLINE;
            out.write(message.toByte());
            buf = message.ID_SRC;
            message.ID_SRC = message.ID_DEST;
            message.ID_DEST = buf;

            OutputStream out1 = user1.getSocket().getOutputStream();
        if(user2 != null) {
            if (user2.statusConnections != true) {
                if (!incorrectDisconnected(user2.getSocket(), message)) {
                    OutputStream out2 = user2.getSocket().getOutputStream();

                    message.type = TypeMessage.REQUESTONFRIEND;
                    message.MSG = user1.login;
                    out2.write(message.toByte());


                }

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
            if(isConnected(user2))
            {
                    friend = new Friend(user1.login,user1.id,Friend.FRIEND);
                    user2.addFriendtoList(friend);
                    OutputStream out2 = user2.getSocket().getOutputStream();

                    message.type = TypeMessage.USERONLINE;
                    message.MSG = user1.login;
                    message.ID_SRC = user1.id;
                    message.ID_DEST = user2.id;
                    out2.write(message.toByte());

                    OutputStream out = user1.getSocket().getOutputStream();
                    message.type = TypeMessage.USERONLINE;
                    message.ID_SRC = user2.id;
                    message.ID_DEST = user1.id;
                    message.MSG = user2.login;
                    out.write(message.toByte());

                        return;
            }


            OutputStream out = user1.getSocket().getOutputStream();
            message.type = TypeMessage.USEROFFLINE;
            message.ID_SRC = message.ID_DEST;
            out.write(message.toByte());

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void disconnectFriendList(Socket socketUser, CPS messageUser){
        User user = Server.userSession.get(messageUser.ID_SRC);
        messageUser.type = TypeMessage.USEROFFLINE;
        ArrayList<Friend> friendList = null;
        OutputStream outL = null;
        if(user!=null)
        {
            friendList = user.getFriend();
            for(Friend usr : friendList){
                try {
                    User userF = Server.userSession.get(usr.id);
                    if(isConnected(userF)) {
                        outL = userF.getSocket().getOutputStream();
                        outL.write(messageUser.toByte());
                    }
                }catch (IOException e)
                {

                }
            }

        }


    }


    boolean isConnected(User user)
    {

        if(user == null)
        {
            return false;
        }
        if(user.statusConnections)
        {
            return false;
        }
        if (incorrectDisconnected(user.getSocket(),new CPS(3)))
        {
            return false;
        }

        return true;
    }
}
