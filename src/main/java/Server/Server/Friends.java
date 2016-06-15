package Server.Server;

/**
 * Created by Denis on 13.06.2016.
 */



public class Friends {

    public static int REQUEST =1;
    public static int UNACCEPTED =2;
    public static int FRIEND = 0;

   public int friendType;
   public Integer id;
    public String login;
   public Friends (String Login, Integer Id, int friendType){
        login = Login;
        id = Id;
       this.friendType = friendType;

    }
}
