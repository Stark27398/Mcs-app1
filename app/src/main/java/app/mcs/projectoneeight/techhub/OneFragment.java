package app.mcs.projectoneeight.techhub;

/**
 * Created by Jayanth on 02-06-2018.
 */


        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.BroadcastReceiver;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.support.annotation.NonNull;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentTransaction;
        import android.support.v4.widget.SwipeRefreshLayout;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;


        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.squareup.okhttp.Response;


public class OneFragment extends Fragment {

    private StringBuilder file;

    public OneFragment() {
        // Required empty public constructor
//        announcement();
    }

    TextView a;
    View v;
    Activity context;
    ProgressDialog pd;
    String str = "No New Announcement";
    String message;
    Button edit,editOk;
    EditText editText;
    SharedPreferences sp;
    DatabaseReference dr;
    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static Response response;
    String url = "http://jayanthstark.000webhostapp.com/announcement/announcement.php?a=3567";
    SwipeRefreshLayout mySwipeRefreshLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_one, container, false);
        a =(TextView) v.findViewById(R.id.announce);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        message = sp.getString("announcement", "No new announcement !");
        a.setText(message);
        edit=(Button)v.findViewById(R.id.edit);
        editText=(EditText)v.findViewById(R.id.announceEdit);
        editOk=(Button)v.findViewById(R.id.editOk);
        dr=FirebaseDatabase.getInstance().getReference().child("announcement").child("message");
        if(Config.isAdmin()){
            edit.setVisibility(View.VISIBLE);
        }
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    edit.setVisibility(View.INVISIBLE);
                    a.setVisibility(View.INVISIBLE);
                    editText.setVisibility(View.VISIBLE);
                    editOk.setVisibility(View.VISIBLE);
            }
        });
        editOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().trim().length()>0){
                    a.setVisibility(View.VISIBLE);
                    a.setText(editText.getText().toString());
                    editText.setText(null);
                    editText.setVisibility(View.INVISIBLE);
                    editOk.setVisibility(View.INVISIBLE);
                    edit.setVisibility(View.VISIBLE);
                    dr.setValue(a.getText().toString());
                }else{
                    a.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.INVISIBLE);
                    editOk.setVisibility(View.INVISIBLE);
                    edit.setVisibility(View.VISIBLE);
                }
            }
        });

        DatabaseReference r= FirebaseDatabase.getInstance().getReference("announcement");
        //r.child("announcement").setValue(message);
        r.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String t1= dataSnapshot.child("message").getValue(String.class);
                        message=t1;
                        a.setText(message);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("announcement", message);
                        editor.commit();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;
    }

    private void refresh() {
        DatabaseReference r= FirebaseDatabase.getInstance().getReference("announcement");
        //r.child("announcement").setValue(message);
        r.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot p: dataSnapshot.getChildren()){
                    String t1= p.getValue(String.class);
                    message=t1;
                    a.setText(message);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("announcement", message);
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void ref(){
//                FragmentTransaction ft=getFragmentManager().beginTransaction();
//                ft.detach(OneFragment.this).attach(OneFragment.this).commit();
                Toast.makeText(getActivity(),"Refreshed",Toast.LENGTH_SHORT).show();
    }

}




