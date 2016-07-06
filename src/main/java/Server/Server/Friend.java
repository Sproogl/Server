package Server.Server;

/**
 * Created by Denis on 13.06.2016.
 */



public class Friend {

    public static final int REQUEST =1;
    public static final int UNACCEPTED =2;
    public static final int FRIEND = 0;

   public int friendType;
   public Integer id;
    public String login;
   public Friend (String Login, Integer Id, int friendType){
        login = Login;
        id = Id;
       this.friendType = friendType;

    }
}
