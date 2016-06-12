package Server.Server;

import Server.DBManager.DBManager;
import Server.Protocol.CPS;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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

        try{
            OutputStream out = socketUser.getOutputStream();
            Integer id = dbManager.searchUser(message.getLogin(),message.getPassword());

            message.ID_SRC = id;

            out = socket.getOutputStream();
            out.write(message.toByte());
            out.close();
            socketUser.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectionUser(Socket socketUser, CPS messageUser) {
        if(Server.userSession.get(message.ID_SRC)==null)
            Server.userSession.put(message.ID_SRC,socketUser);
        else{
            messageUser.type = (byte)104;
            try {
                OutputStream out = socketUser.getOutputStream();
                out.write(messageUser.toByte());
                out.close();
                socketUser.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(Socket socketDest,Socket socketSrc ,CPS messageDest) {
        try {
            if(socketDest != null)
            {
                OutputStream Lout = socketDest.getOutputStream();
                Lout.write(message.toByte());
                socketSrc.close();
            }
            else
            {
                OutputStream out = socketSrc.getOutputStream();
                message.type= (byte)104;
                out.write(message.toByte());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addFriend(Socket socketUser, CPS messageReqest) {

    }

    public void onlineFriendsList(Socket socketUser, CPS messageUser) {

    }

    public void disconnectionUser(Socket socketUser, CPS messageUser)  {

        Socket delsocket = Server.userSession.get(messageUser.ID_SRC);
        if(delsocket != null)

        {
            try {
                delsocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.userSession.remove(messageUser.ID_SRC);
        }
        try {
            socketUser.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
