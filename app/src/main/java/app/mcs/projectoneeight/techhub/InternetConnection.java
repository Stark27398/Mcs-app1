package app.mcs.projectoneeight.techhub;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Jayanth on 04-06-2018.
 */

public class InternetConnection {

    /** CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT */
    public static boolean checkConnection(Context context) {
        return  ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
