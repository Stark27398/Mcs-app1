package app.mcs.projectoneeight.techhub;

/**
 * Created by Jayanth on 02-06-2018.
 */

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class FourFragment extends Fragment{

    private View v;
    EditText e1;
    ListView l1;
    ArrayAdapter<String> adapter;
    FirebaseListAdapter<Forum> firebaseListAdapter;
    String name;
    Button add;
    FirebaseAuth mAuth;
    ArrayList<String> arrayList;
    DatabaseReference r1;
    DatabaseReference r2;
    ValueEventListener r3;
    SwipeRefreshLayout mySwipeRefreshLayout;
    public FourFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_four, container, false);
        mAuth=FirebaseAuth.getInstance();
       name=mAuth.getCurrentUser().getDisplayName();
        e1 = (EditText)v.findViewById(R.id.topic);
        l1 = (ListView)v.findViewById(R.id.list1);
        add=(Button)v.findViewById(R.id.addTopic);
        arrayList = new ArrayList<>();

        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,arrayList);
        l1.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        r1 = FirebaseDatabase.getInstance().getReference().child("discussion");
//        displayTopics();
        r2=FirebaseDatabase.getInstance().getReference().child("topicCreators");
        r3=FirebaseDatabase.getInstance().getReference().child("topicCreators").orderByChild("updateTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    arrayList.clear();
                    for(DataSnapshot each:dataSnapshot.getChildren()){
                        if(!each.getKey().equals("MCS")){
                            arrayList.add(each.getKey());
                        }
                    }
                    arrayList.add("MCS");
                    Collections.reverse(arrayList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("topicCreators").orderByChild("updateTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        FirebaseListAdapter<String> firebaseListAdapter = new FirebaseListAdapter<String>(
//                this,
//                String.class,
//                android.R.layout.simple_list_item_1,
//                r2.orderByValue()
//        ){
//            @Override
//            protected void populateView(View v, String model, int position) {
//
//                TextView textView = (TextView) v.findViewById(android.R.id.text1);
//                textView.setText(model);
//
//            }
//        };



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert_data(view);
            }
        });
        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getActivity(),Chat.class);
                intent.putExtra("topic-name",((TextView)view).getText().toString());
                startActivity(intent);
            }
        });
        Snackbar.make(v,"No internet connection",200);
        return  v;
    }

    private void displayTopics() {
        ListView listView=(ListView)v.findViewById(R.id.list1);
        Query query=FirebaseDatabase.getInstance().getReference().child("topicCreators");
        final FirebaseListOptions<Forum> options = new FirebaseListOptions.Builder<Forum>()
                .setQuery(query.orderByValue(), Forum.class).setLayout(R.layout.forum)
                .build();
        firebaseListAdapter=new FirebaseListAdapter<Forum>(options){
            @Override
            protected void populateView(View v, Forum model, int position) {
                TextView t=(TextView)v.findViewById(R.id.forumTitle);
                t.setText(model.getTitle());
            }
        };
        firebaseListAdapter.notifyDataSetChanged();
        listView.setAdapter(firebaseListAdapter);
    }

    public void insert_data(View v)
    {

        Map<String,Object> map = new HashMap<>();
        map.put(e1.getText().toString(), "");
        if(isConnectingToInternet() && arrayList.size()!=0) {
            if (e1.getText().toString().trim().length() > 0) {
                int check = 0;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).equals(e1.getText().toString())) {
                        Toast.makeText(getActivity(), "Topic already found", Toast.LENGTH_SHORT).show();
                        check = 1;
                        break;
                    }
                }
                if (check == 0) {
                    r1.updateChildren(map);
                    r2.child(e1.getText().toString()).setValue(new Forum(e1.getText().toString(),FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                    Toast.makeText(getActivity(), "Topic added", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Please enter a topic to add", Toast.LENGTH_SHORT).show();
            }
            e1.setText(null);
        }else{
            Toast.makeText(getActivity(),"Please check your connection",Toast.LENGTH_SHORT).show();
        }

    }
    private void refresh() {
    }

    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    @Override
    public void onStart() {
        super.onStart();
//        firebaseListAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
//        firebaseListAdapter.stopListening();
    }
}
