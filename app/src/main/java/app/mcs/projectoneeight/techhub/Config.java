package app.mcs.projectoneeight.techhub;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Jayanth on 07-06-2018.
 */

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";
    public static final String adminUser="mcstechhelpline@gmail.com";
    public static int admin=0;
    public static boolean isAdmin(){
        if(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(adminUser)){
            return true;
        }
        return false;
    }
    public static String topic;
    public static String oldTopic;

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the pikachu in the pikachu tray
    public static int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    public static String pos="1";
    public static String mes="New message";
}
