package app.mcs.projectoneeight.techhub;

import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.google.firebase.database.FirebaseDatabase.*;

public class Chat extends AppCompatActivity {

    private List<ApplicationInfo> mAppList;
    private AppAdapter mAdapter;

    private Toolbar toolbar;
    String title;
    int senderPos=4;
    FirebaseListAdapter<ChatMessage> adapter;
    FirebaseAuth mAuth;
    DatabaseReference ref, r2, senderRef;
    SwipeMenuListView listOfMessages;
    String creator;
    Button d;
    boolean myChat = false;
    String s="Admin";
    PopupMenu popupMenu;
    String isMyChat;
    boolean isClicked=false;
    private SwipeMenuListView.OnSwipeListener swipeListener;
    SwipeMenuListView listOfMessages1;
    SwipeMenuCreator swipeMenuCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        d = (Button) findViewById(R.id.deleteTopic);
        mAdapter=new AppAdapter();
        swipeListener = new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {

            }

            @Override
            public void onSwipeEnd(int position) {

            }
        };
        listOfMessages = (SwipeMenuListView) findViewById(R.id.list_of_messages);

        mAuth = FirebaseAuth.getInstance();
        Bundle b = getIntent().getExtras();
        title = b.getString("topic-name");
        setTitle("Topic - " + title);
        if(title.equals("MCS")){
            d.setVisibility(View.INVISIBLE);
        }else{
            d.setVisibility(View.VISIBLE);
        }
        listOfMessages1 = (SwipeMenuListView) findViewById(R.id.list_of_messages);


        r2 = FirebaseDatabase.getInstance().getReference().child("topicCreators").child(title);
        r2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                creator = dataSnapshot.child("creator").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Config.isAdmin() || isCreator()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Chat.this);
                    alertDialog.setTitle("Delete this topic");
                    alertDialog.setMessage("Are you sure ?");
                    alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ref = FirebaseDatabase.getInstance().getReference().child("discussion").child(title);
                            ref.removeValue();
                            r2.removeValue();
                            onBackPressed();
                            Toast.makeText(getApplicationContext(), "Topic deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry the topic is not created by you", Toast.LENGTH_SHORT).show();
                }
            }
        });
        FloatingActionButton fab =
                (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                if (input.getText().toString().trim().length() > 0) {
                    if (Config.isAdmin()) {
                        getInstance()
                                .getReference().child("discussion").child(title)
                                .push()
                                .setValue(new ChatMessage(input.getText().toString(),
                                        "Admin", "Admin")
                                );
                    } else {
                        getInstance()
                                .getReference().child("discussion").child(title)
                                .push()
                                .setValue(new ChatMessage(input.getText().toString(),
                                        FirebaseAuth.getInstance()
                                                .getCurrentUser()
                                                .getDisplayName() + " (" + UserDetails.batch + " Batch)", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                );
                    }
                    getInstance().getReference().child("topicCreators").child(title).setValue(new Forum(title,creator));
                }

                // Clear the input
                input.setText("");
            }
        });
        displayChatMessages();
        if (Config.isAdmin()) {
            SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {
                    SwipeMenuItem deleteItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                            0x3F, 0x25)));
                    // set item width
                    deleteItem.setWidth(300);
                    // set a icon
                    deleteItem.setIcon(R.drawable.ic_delete_sweep_black_70dp);
                    // add to menu
                    menu.addMenuItem(deleteItem);
                    SwipeMenuItem editItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    editItem.setBackground(R.color.purple);
                    // set item width
                    editItem.setWidth(300);
                    // set a icon
                    editItem.setIcon(R.drawable.ic_create_black_24dp);
                    // add to menu
                    menu.addMenuItem(editItem);
                }
            };
            listOfMessages.setMenuCreator(swipeMenuCreator);
            listOfMessages.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
            listOfMessages.setCloseInterpolator(new BounceInterpolator());
            listOfMessages.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                    if (index == 0) {
                        DatabaseReference item = adapter.getRef(position);
                        item.removeValue();
                        Toast.makeText(getApplicationContext(), "Message deleted", Toast.LENGTH_SHORT).show();
                    }else if(index==1){
                        isClicked=true;
                        final DatabaseReference item1 = adapter.getRef(position);
                        item1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String text=dataSnapshot.child("messageText").getValue(String.class);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    if(isClicked==true){
                                        final AlertDialog.Builder alert=new AlertDialog.Builder(Chat.this);
                                        alert.setTitle("Edit your message");
                                        final EditText edit=new EditText(Chat.this);
                                        edit.setWidth(250);
                                        alert.setView(edit);
                                        edit.setText(text);
                                        edit.setSelection(edit.getText().length());
                                        alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if(edit.getText().toString().trim().length()>0){
                                                    item1.child("messageText").setValue(edit.getText().toString());
                                                    Toast.makeText(getApplicationContext(),"Update successful",Toast.LENGTH_LONG).show();
                                                }
                                                dialogInterface.cancel();
                                                isClicked=false;
                                            }
                                        });
                                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                                isClicked=false;
                                            }
                                        });
                                        AlertDialog dialog1=alert.create();
                                        dialog1.show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    return false;
                }
            });
        } else {
            swipeMenuCreator = new SwipeMenuCreator() {
                @Override
                public void create(SwipeMenu menu) {
//                    switch (menu.getViewType()){
//                        case 0:
//                            break;
//                        case 1:
//                            create0(menu);
//                            break;
//                        default:
//                            break;
//                    }
                    create0(menu);
                }
            };
                listOfMessages1.setMenuCreator(swipeMenuCreator);
                listOfMessages1.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
                listOfMessages1.setCloseInterpolator(new BounceInterpolator());
                listOfMessages1.smoothCloseMenu();
                listOfMessages1.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                        if (index == 0) {
                            final DatabaseReference item = adapter.getRef(position);
                            item.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String me = dataSnapshot.child("sender").getValue(String.class);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        if (Objects.equals(me, FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                            item.removeValue();
                                            Toast.makeText(getApplicationContext(), "Message deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else if (index == 1) {
                            isClicked = true;
                            final DatabaseReference item1 = adapter.getRef(position);
                            item1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String me = dataSnapshot.child("sender").getValue(String.class);
                                    final String text = dataSnapshot.child("messageText").getValue(String.class);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        if (Objects.equals(me, FirebaseAuth.getInstance().getCurrentUser().getEmail()) && isClicked == true) {
                                            final AlertDialog.Builder alert = new AlertDialog.Builder(Chat.this);
                                            alert.setTitle("Edit your message");
                                            final EditText edit = new EditText(Chat.this);
                                            edit.setWidth(250);
                                            alert.setView(edit);
                                            edit.setText(text);
                                            edit.setSelection(edit.getText().length());
                                            alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    if (edit.getText().toString().trim().length() > 0) {
                                                        item1.child("messageText").setValue(edit.getText().toString());
                                                        Toast.makeText(getApplicationContext(), "Update successful", Toast.LENGTH_LONG).show();
                                                    }
                                                    dialogInterface.cancel();
                                                    isClicked = false;
                                                }
                                            });
                                            alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                    isClicked = false;
                                                }
                                            });
                                            AlertDialog dialog1 = alert.create();
                                            dialog1.show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        return false;
                    }
                });

        }



    }

            private void create0(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(300);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_sweep_black_70dp);
                // add to menu
                menu.addMenuItem(deleteItem);
                SwipeMenuItem editItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                editItem.setBackground(R.color.purple);
                // set item width
                editItem.setWidth(300);
                // set a icon
                editItem.setIcon(R.drawable.ic_create_black_24dp);
                // add to menu
                menu.addMenuItem(editItem);
    }

    private void create1() {
        swipeMenuCreator=null;
    }

    private void displayChatMessages() {
        SwipeMenuListView listOfMessages = (SwipeMenuListView) findViewById(R.id.list_of_messages);
        Query query = FirebaseDatabase.getInstance().getReference().child("discussion").child(title);
        final FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class).setLayout(R.layout.message)
                .build();
        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.mtext);
                TextView messageUser = (TextView) v.findViewById(R.id.muser);
                TextView messageTime = (TextView) v.findViewById(R.id.mtime);
                TextView messageText1 = (TextView) v.findViewById(R.id.mtext2);
                TextView messageTime1 = (TextView) v.findViewById(R.id.mtime2);
                SwipeMenuListView listOfMessages1 = (SwipeMenuListView) findViewById(R.id.list_of_messages);

                if (model.getMessageUser().equalsIgnoreCase(mAuth.getCurrentUser().getDisplayName() + " (" + UserDetails.batch + " Batch)") &&
                        model.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

                    // Set their text
                    messageText.setVisibility(View.INVISIBLE);
                    messageUser.setVisibility(View.INVISIBLE);
                    messageTime.setVisibility(View.INVISIBLE);
                    messageText.setText(null);
                    messageUser.setText(null);
                    messageTime.setText(null);
                    messageText1.setVisibility(View.VISIBLE);
                    messageTime1.setVisibility(View.VISIBLE);
                    messageText1.setText(model.getMessageText());
                    // Format the date before showing it
                    messageTime1.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getMessageTime()));

                } else {
                    messageText.setVisibility(View.VISIBLE);
                    messageUser.setVisibility(View.VISIBLE);
                    messageTime.setVisibility(View.VISIBLE);
                    messageText1.setVisibility(View.INVISIBLE);
                    messageTime1.setVisibility(View.INVISIBLE);
                    messageText1.setText(null);
                    messageTime1.setText(null);
                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());
                    if (model.getMessageUser().equals("Admin")) {
                        messageText.setBackgroundResource(R.drawable.admintext);
                    } else {
                        messageText.setBackgroundResource(R.drawable.others_text);
                    }
                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getMessageTime()));
                }

            }
        };

        listOfMessages.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public boolean onOptionsItemSelected(MenuItem menu) {
        onBackPressed();
        return true;
    }

    public boolean isCreator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail(), creator)) {
                return true;
            }
        }
        return false;
    }

    class AppAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return adapter.getCount();
        }

        @Override
        public Object getItem(int i) {
            return adapter.getItem(i);
        }
        @Override
        public int getViewTypeCount() {
            // menu type count
            return 2;
        }
        @Override
        public int getItemViewType(int position) {
            // current menu type
            if(adapter.getItem(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                return 0;
            }
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return adapter.getItemId(i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }
}
