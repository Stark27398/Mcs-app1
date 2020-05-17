package app.mcs.projectoneeight.techhub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    FirebaseAuth mAuth;
    EditText user, pass1,forgot_mail;
    TextInputEditText pass;
    LinearLayout passLayout;
    TextInputLayout mailLayout,mailLayout2;
    CheckBox remember;
    Button login,signup,forgot_pass,send_mail,go_back;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    DatabaseReference d1;
    String u, p;
    int r;
    String user_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        user = (EditText) findViewById(R.id.username);
        pass = (TextInputEditText) findViewById(R.id.pass);
        login = (Button) findViewById(R.id.login_btn);
        signup=(Button)findViewById(R.id.sign_up_page);
        forgot_mail=(EditText)findViewById(R.id.forgot_mail);
        forgot_pass=(Button)findViewById(R.id.forgot_pass);
        send_mail=(Button)findViewById(R.id.send_forgot_mail);
        go_back=(Button)findViewById(R.id.go_back);
        passLayout=(LinearLayout)findViewById(R.id.pass1);
        mailLayout=(TextInputLayout)findViewById(R.id.email_layer);
        mailLayout2=(TextInputLayout)findViewById(R.id.email_layer1);
        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setVisibility(View.INVISIBLE);
                pass.setVisibility(View.INVISIBLE);
                login.setVisibility(View.INVISIBLE);
                signup.setVisibility(View.INVISIBLE);
                forgot_pass.setVisibility(View.INVISIBLE);
                go_back.setVisibility(View.VISIBLE);
                forgot_mail.setVisibility(View.VISIBLE);
                send_mail.setVisibility(View.VISIBLE);
                mailLayout2.setVisibility(View.VISIBLE);
                mailLayout.setVisibility(View.INVISIBLE);
                passLayout.setVisibility(View.INVISIBLE);
            }
        });
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setVisibility(View.VISIBLE);
                pass.setVisibility(View.VISIBLE);
                login.setVisibility(View.VISIBLE);
                signup.setVisibility(View.VISIBLE);
                forgot_pass.setVisibility(View.VISIBLE);
                go_back.setVisibility(View.INVISIBLE);
                forgot_mail.setVisibility(View.INVISIBLE);
                send_mail.setVisibility(View.INVISIBLE);
                mailLayout.setVisibility(View.VISIBLE);
                passLayout.setVisibility(View.VISIBLE);
                mailLayout2.setVisibility(View.INVISIBLE);
            }
        });
        send_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(forgot_mail.getText().toString().trim().length()>0){
                mAuth.sendPasswordResetEmail(forgot_mail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Password reset mail sent to : "+forgot_mail.getText().toString(),Toast.LENGTH_SHORT).show();
                            user.setVisibility(View.VISIBLE);
                            pass.setVisibility(View.VISIBLE);
                            login.setVisibility(View.VISIBLE);
                            signup.setVisibility(View.VISIBLE);
                            forgot_pass.setVisibility(View.VISIBLE);
                            go_back.setVisibility(View.INVISIBLE);
                            forgot_mail.setVisibility(View.INVISIBLE);
                            send_mail.setVisibility(View.INVISIBLE);
                            mailLayout.setVisibility(View.VISIBLE);
                            passLayout.setVisibility(View.VISIBLE);
                            mailLayout2.setVisibility(View.INVISIBLE);
                        }else{
                            Toast.makeText(getApplicationContext(),"Please check your mail id",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                    Toast.makeText(getApplicationContext(),"Please enter your mail id",Toast.LENGTH_SHORT).show();
                }
            }
        });
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        u = sp.getString("username", null);
        p = sp.getString("pass", null);
        r = sp.getInt("remember", 0);
        d1=FirebaseDatabase.getInstance().getReference().child("users");
        if(r==1 && !u.equals(null) && !p.equals(null)){
            //goToMain(u,p,r);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int r1=0;
                if (user.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Username is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Password is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!user.getText().toString().equalsIgnoreCase("")&&!pass.getText().toString().equalsIgnoreCase("")) {
                    String u1=user.getText().toString();
                    String p1=pass.getText().toString();
                    //goToMain2(u1,p1,r1);
//                    Intent i=new Intent(LoginActivity.this,MainActivity.class);
//                    i.putExtra("email",u1);
//                    startActivity(i);
                    String[] s=u1.split("@");
                    final String user1=s[0];
                    final String[] hash = {null};
                    try {
                        // Create MessageDigest instance for MD5
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        //Add password bytes to digest
                        md.update(pass.getText().toString().getBytes());
                        //Get the hash's bytes
                        byte[] bytes = md.digest();
                        //This bytes[] has bytes in decimal format;
                        //Convert it to hexadecimal format
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i< bytes.length ;i++)
                        {
                            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                        }
                        //Get complete hashed password in hex format
                        hash[0] = sb.toString();
                    }
                    catch (NoSuchAlgorithmException e)
                    {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(),"Please wait while logging in...",Toast.LENGTH_SHORT).show();
                    signIn(u1,p1);
                    mAuth.signOut();
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent si=new Intent(LoginActivity.this,Signup.class);
                startActivity(si);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            if(currentUser.isEmailVerified()){
                Intent i=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }else{
                mAuth.signOut();
            }
        }
    }
    private void signIn(String u1, String hash1) {
        if(!validateForm()){
            return;
        }
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(u1,hash1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(mAuth.getCurrentUser().isEmailVerified()){
                        Intent i=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();
                        Toast.makeText(getApplicationContext(),"Logged in as : "+mAuth.getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Please verify your email address",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Login failed"+task.getException(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        hideProgressDialog();
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = user.getText().toString();
        if (TextUtils.isEmpty(email)) {
            user.setError("Required.");
            valid = false;
        } else {
            user.setError(null);
        }

        String password = pass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            pass.setError("Required.");
            valid = false;
        } else {
            pass.setError(null);
        }

        return valid;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading ....");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    private void goToMain2(String u1, String p1, int r1) {
        user_mail=u1;
        String php="http://jayanthstark.ihostfull.com/auth/auth.php?user="+u1+"&pass="+p1+"&rem="+r1;
        getJSON(php);
    }

    private void goToMain(String u1,String p1,int r1) {
        user_mail=u1;
        String php="http://jayanthstark.ihostfull.com/auth/open.php?user="+u1+"&pass="+p1+"&rem="+r1;
        getJSON(php);
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                String signout = data.getStringExtra("signout");
//                if (signout.equalsIgnoreCase("SignOut")) {
//                 editor = sp.edit();
//                 editor.putString("username", null);
//                    editor.putString("password", null);
//                    editor.putInt("remember", 0);
//                    editor.commit();
//                 r=0;
//                }
//            }
//        }
//    }



    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                    loadMain(s);

            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                }catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
    private void loadMain(String yes){
        if(yes.equals("1")){
            Intent i=new Intent(LoginActivity.this,MainActivity.class);
            i.putExtra("email",user_mail);
            startActivity(i);
            finish();
        }
    }
//    d1.addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//            int count=0;
//            for(DataSnapshot p:dataSnapshot.getChildren()){
//                String mailId = p.child("mail").getValue(String.class);
//                String verify = p.child("pass").getValue(String.class);
//                if (mailId.equalsIgnoreCase(user.getText().toString()))
//                {
//                    count=1;
//                    try {
//                        // Create MessageDigest instance for MD5
//                        MessageDigest md = MessageDigest.getInstance("MD5");
//                        //Add password bytes to digest
//                        md.update(pass.getText().toString().getBytes());
//                        //Get the hash's bytes
//                        byte[] bytes = md.digest();
//                        //This bytes[] has bytes in decimal format;
//                        //Convert it to hexadecimal format
//                        StringBuilder sb = new StringBuilder();
//                        for(int i=0; i< bytes.length ;i++)
//                        {
//                            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
//                        }
//                        //Get complete hashed password in hex format
//                        hash[0] = sb.toString();
//                    }
//                    catch (NoSuchAlgorithmException e)
//                    {
//                        e.printStackTrace();
//                    }
//                    if(verify.equalsIgnoreCase(hash[0])){
//                        Intent i=new Intent(LoginActivity.this,MainActivity.class);
//                        i.putExtra("email",user.getText().toString());
//                        startActivity(i);
//                        finish();
//                        Toast.makeText(getApplicationContext(),"Logged in successfully", Toast.LENGTH_SHORT).show();
//                    }else{
//                        Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//                }}
//            if(count!=1) {
//                Toast.makeText(getApplicationContext(), "Username not found", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//    });
}
