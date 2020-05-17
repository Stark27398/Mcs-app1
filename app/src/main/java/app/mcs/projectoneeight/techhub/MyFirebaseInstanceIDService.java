package app.mcs.projectoneeight.techhub;

/**
 * Created by Jayanth on 05-06-2018.
 */

import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
//
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        Toast.makeText(getApplicationContext(),"Token",Toast.LENGTH_SHORT).show();
      //  registerToken(token);
    }

//    private void registerToken(String token) {
//
//        OkHttpClient client = new OkHttpClient();
//        String u="http://jayanthstark.000webhostapp.com/mcs1/register.php?Token="+ token;
//        Request request = new Request.Builder()
//                .url(u)
//                .build();
//
//        try {
//            client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
