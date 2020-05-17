package app.mcs.projectoneeight.techhub;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Jayanth on 04-06-2018.
 */

public class JSONParser {
    private static final String url="http://jayanthstark.000webhost.com/announcement/announcement.php?a=3567";
    private static final String TAG="TAG";
    private static Response response;
    public static JSONObject getDataFromWeb(){
        try{
            OkHttpClient client=new OkHttpClient();
            Request request=new Request.Builder().url(url).build();
            response=client.newCall(request).execute();
            return new JSONObject(response.body().string());
        }catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
