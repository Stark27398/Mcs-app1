package app.mcs.projectoneeight.techhub;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    ViewPagerAdapter adapter;
    public ViewPager viewPager;
    Button refresh;
    int currentPosition;
    FirebaseAuth mAuth;
    private int[] icons={R.drawable.announcement,R.drawable.chat};
    public static String user=null;
    TextView a;
    String str="Meow";
    String fileurl="http://jayanthstark.000webhostapp.com/announcement/announcement.php";
    String notificationMessage="No new announcement";
    int position=1;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager,position);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setIcons();
        mAuth=FirebaseAuth.getInstance();
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        databaseReference=FirebaseDatabase.getInstance().getReference().child("topic");
        FirebaseMessaging.getInstance().subscribeToTopic("allUsers");
        refresh=(Button)findViewById(R.id.refreshMain);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RotateAnimation rotateAnimation=new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                rotateAnimation.setDuration(800);
                rotateAnimation.setRepeatCount(0);
                refresh.startAnimation(rotateAnimation);
                if(isConnectingToInternet()){
                    setupViewPager(viewPager,currentPosition);
                    setIcons();
                }else{
                    Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Config.topic=dataSnapshot.getValue(String.class);
                Config.oldTopic=sharedPreferences.getString("topic","mcs");
                if(!Config.oldTopic.equals(Config.topic)){
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.topic);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Config.oldTopic);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("topic",Config.topic);
                    editor.commit();
                }
//                Toast.makeText(getApplicationContext(),"new - "+Config.topic+", old - "+Config.oldTopic,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference d1= FirebaseDatabase.getInstance().getReference().child("users");
        d1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String roll=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("roll").getValue(String.class);
                UserDetails.rollno=roll.trim();
                UserDetails.batch=UserDetails.rollno.substring(0,Math.min(UserDetails.rollno.length(),4));
                //Toast.makeText(getApplicationContext(),UserDetails.batch,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                    currentPosition=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setIcons() {
        tabLayout.getTabAt(0).setIcon(icons[0]);
        tabLayout.getTabAt(3).setIcon(icons[1]);
    }

    private void setupViewPager(ViewPager viewPager,int pos) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "Announcement");
        adapter.addFragment(new TwoFragment(), "Home");
        adapter.addFragment(new ThreeFragment(), "Malloc");
        adapter.addFragment(new FourFragment(), "Discussion");
        viewPager.setAdapter(adapter);

            viewPager.setCurrentItem(pos);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0 || position==3){
                return null;
            }
            return mFragmentTitleList.get(position);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.signout){
            if(isConnectingToInternet()){
            mAuth.signOut();
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Config.topic);
            }
            Intent in=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(in);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Toast.makeText(getApplicationContext(),"Refreshing....",Toast.LENGTH_SHORT).show();
            viewPager.setCurrentItem(data.getIntExtra("CHANGED_POSITION",1));
        }
    }


}