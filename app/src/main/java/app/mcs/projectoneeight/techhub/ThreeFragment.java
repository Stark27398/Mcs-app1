package app.mcs.projectoneeight.techhub;

/**
 * Created by Jayanth on 02-06-2018.
 */

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


public class ThreeFragment extends Fragment{

    public ThreeFragment() {
        // Required empty public constructor
    }
    ArrayList<Long> list = new ArrayList<>();
    View v;
    Button zip,o;
    TextView m;
    SharedPreferences s;
    String mal;
    SharedPreferences sp;
    private static final String TAG = MainActivity.class.getSimpleName();
    BroadcastReceiver mRegistrationBroadcastReceiver;
//    private static Response response;
    String z="http://jayanthstark.000webhostapp.com/malloc/";
    ProgressBar pb;
    Dialog dialog;
    int downloadedSize = 0;
    int totalSize = 0;
    TextView cur_val;
    String dwnload_file_path = "http://jayanthstark.000webhostapp.com/malloc/";

    ArrayList<String> userMessage;
    PopupMenu popupMenu=null;
    SwipeRefreshLayout mySwipeRefreshLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_three, container, false);
        zip=(Button)v.findViewById(R.id.m1);
        o=(Button)v.findViewById(R.id.open);
        m=(TextView)v.findViewById(R.id.malloc);
        s= PreferenceManager.getDefaultSharedPreferences(getActivity());
        mal=s.getString("malloc","No questions updated");
        m.setText(mal);
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }
        popupMenu=new PopupMenu(getActivity(),zip);
        popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("files");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectusers((Map<String,Object>) dataSnapshot.getValue());
                        for(int i=0;i<userMessage.size();i++){
                            popupMenu.getMenu().add(userMessage.get(i).toString());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        DatabaseReference r= FirebaseDatabase.getInstance().getReference("malloc");
        //r.child("malloc").setValue(mal);
        r.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot p: dataSnapshot.getChildren()){
                    String t1= p.getValue(String.class);
                    mal=t1;
                    m.setText(mal);
                    SharedPreferences.Editor editor = s.edit();
                    editor.putString("malloc", mal);
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        zip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            dwnload_file_path=z+menuItem.getTitle();
                            if(isConnectingToInternet()){
                                meow2();
                            }else{
                                Toast.makeText(getActivity(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
            }
        });
        o.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFolder();
            }
        });
        return v;
    }

    private void refresh() {

        deletemenu();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("files");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectusers((Map<String,Object>) dataSnapshot.getValue());
                        for(int i=0;i<userMessage.size();i++){
                            popupMenu.getMenu().add(userMessage.get(i).toString());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        DatabaseReference r= FirebaseDatabase.getInstance().getReference("malloc");
        //r.child("malloc").setValue(mal);
        r.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot p: dataSnapshot.getChildren()){
                    String t1= p.getValue(String.class);
                    mal=t1;
                    m.setText(mal);
                    SharedPreferences.Editor editor = s.edit();
                    editor.putString("malloc", mal);
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deletemenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            popupMenu.getMenu().clear();
        }
    }

    private void meow2(){
//        showProgress(dwnload_file_path);

        new Thread(new Runnable() {
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    downloadFile();
                }
            }
        }).start();
    }
    private void openFolder(){
        if(isCardAvailable()){
            File storage=new File(Environment.getExternalStorageDirectory().getPath()+"/MCS/Downloads");
            if(!storage.exists()){
                Toast.makeText(getActivity(),"No downloads found",Toast.LENGTH_SHORT).show();
            }else{
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/MCS/Downloads"),"resource/folder");
                startActivity(Intent.createChooser(intent,"Downloads"));
            }
        }else{
            Toast.makeText(getActivity(),"Storage not found",Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isCardAvailable(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }
    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    void downloadFile(){
        File storage=null;
        File f = null;
        try {
            URL url = new URL(dwnload_file_path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            //connect
            urlConnection.connect();
            int MyVersion = Build.VERSION.SDK_INT;
            if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (!checkIfAlreadyhavePermission()) {
                    requestForSpecificPermission();
                }
            }

            //set the path where we want to save the file
            File SDCardRoot = getActivity().getExternalFilesDir("MCS");
            File sdCard=new File(Environment.getExternalStorageDirectory(),"MCS");
            if(!sdCard.exists()){
                sdCard.mkdir();
            }
            //create a new file, to save the downloaded file
            storage= new File(sdCard.getAbsolutePath(),"Downloads");
            if(!storage.exists()){
                storage.mkdir();
            }
            String fileName= URLUtil.guessFileName(dwnload_file_path,null, MimeTypeMap.getFileExtensionFromUrl(dwnload_file_path));
            File file = new File(storage,fileName);
            int i=1;
            String[] name=fileName.split("\\.(?=[^\\.]+$)");
            while(file.exists()){

                file = new File(storage,name[0]+"("+i+")."+name[1]);
                if(!file.exists()){
                    break;
                }else{
                    i++;
                }
            }
            if(!file.exists()) {
                file.createNewFile();
                f=file;
            }


            if(isDownloadManagerAvailable(getActivity())) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(dwnload_file_path));
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

                    request.setAllowedOverRoaming(false);
                    request.setDescription("Downloading your files.....");
                    request.setTitle("Downloads");

// in order for this if to run, you must use the android 3.2 to compile your app
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    }
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory() + "MCS/Downloads", String.valueOf(file));

// get download service and enqueue file
                    DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    long refid = manager.enqueue(request);
                    list.add(refid);
                }
            }else{
                Toast.makeText(getActivity(),"Download Manager not available",Toast.LENGTH_SHORT).show();
            }



            FileOutputStream fileOutput = new FileOutputStream(file);

            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            //this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();

//            getActivity().runOnUiThread(new Runnable() {
//                public void run() {
//                    pb.setMax(totalSize);
//                }
//            });

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                // update the progressbar //
//                getActivity().runOnUiThread(new Runnable() {
//                    public void run() {
//                        pb.setProgress(downloadedSize);
//                        float per = ((float)downloadedSize/totalSize) * 100;
//                        cur_val.setText("Downloaded " + downloadedSize + "KB / " + totalSize + "KB (" + (int)per + "%)" );
//                    }
//                });
            }
            //close the output stream when complete //
            fileOutput.close();
//            getActivity().runOnUiThread(new Runnable() {
//                public void run() {
//                     dialog.dismiss();
//                }
//            });

        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            showError("File not found, please refresh");
//            dialog.dismiss();
            File file = f;
            boolean deleted = file.delete();
            if(!deleted){
                boolean deleted2 = false;
                try {
                    deleted2 = file.getCanonicalFile().delete();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if(!deleted2){
                    boolean deleted3 = getActivity().deleteFile(file.getName());
                }
            }
            e.printStackTrace();
        }
        catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }
    }

    void showError(final String err){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), err, Toast.LENGTH_LONG).show();
            }
        });
    }

    void showProgress(String file_path){
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.myprogressdialog);
        dialog.setTitle("Download Progress");

        TextView text = (TextView) dialog.findViewById(R.id.tv1);
        text.setText("Downloading file from ... " + file_path);
        cur_val = (TextView) dialog.findViewById(R.id.cur_pg_tv);
        cur_val.setText("Starting download...");
        dialog.show();
        pb = (ProgressBar)dialog.findViewById(R.id.progress_bar);
        pb.setProgress(0);
        pb.setProgressDrawable(getResources().getDrawable(R.drawable.blue_progress));
    }

    private void collectusers(Map<String, Object> users) {
         userMessage = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            userMessage.add((String) singleUser.get("name"));

        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    public static boolean isDownloadManagerAvailable(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return true;
        }
        return false;
    }
    BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {




            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);


            Log.e("IN", "" + referenceId);

            list.remove(referenceId);


            if (list.isEmpty())
            {


                Log.e("INSIDE", "" + referenceId);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getActivity())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("MCS Downloads")
                                .setContentText("All Download completed");


                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(455, mBuilder.build());


            }

        }
    };






    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){


            // permission granted

        }
    }

}
