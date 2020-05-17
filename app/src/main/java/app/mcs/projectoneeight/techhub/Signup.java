package app.mcs.projectoneeight.techhub;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static app.mcs.projectoneeight.techhub.MainActivity.user;

public class Signup extends AppCompatActivity {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    private Toolbar toolbar;
    EditText name,roll,phone,mail,pass,confirm;
    Button signup;
    private DatabaseReference d;
    boolean uniq=false;
    FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        name=(EditText)findViewById(R.id.name);
        roll=(EditText)findViewById(R.id.roll);
        phone=(EditText)findViewById(R.id.phone1);
        mail=(EditText)findViewById(R.id.mail);
        pass=(EditText)findViewById(R.id.password);
        confirm=(EditText)findViewById(R.id.confirm_pass);
        signup=(Button)findViewById(R.id.signup_btn);
        final String[] hash = {null};
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String n,r,ph,m,p,c,rem;
                n=name.getText().toString();
                r=roll.getText().toString();
                ph=phone.getText().toString();
                m=mail.getText().toString();
                p=pass.getText().toString();
                c=confirm.getText().toString();
                String[] s=m.split("@");
                final String user=s[0];
                if(!n.equalsIgnoreCase("") || !r.equalsIgnoreCase("") || !ph.equalsIgnoreCase("") || !m.equalsIgnoreCase("") || !p.equalsIgnoreCase("")){
                    if(p.equalsIgnoreCase(c)){
                        try {
                            // Create MessageDigest instance for MD5
                            MessageDigest md = MessageDigest.getInstance("MD5");
                            //Add password bytes to digest
                            md.update(p.getBytes());
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
                        if(isConnectingToInternet()){
                                Toast.makeText(getApplicationContext(),"Please wait while signing up...",Toast.LENGTH_SHORT).show();
                                createAccount(m,p);
                        }else{
                            Toast.makeText(getApplicationContext(),"Please check the internet connection",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(getApplicationContext(),"Passwords didn't match",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please check the details again, fields can't be empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createAccount(String m, String p) {
        Log.d(TAG, "signIn:" + m);
        if(!validateForm()){
            return;
        }
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(m,p).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "signInWithEmail:success");
                    Toast.makeText(getApplicationContext(),"Sign up successful",Toast.LENGTH_SHORT).show();
                    UserProfileChangeRequest profile=new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).build();
                    mAuth.getCurrentUser().updateProfile(profile);
                    d=FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
                    d.child("name").setValue(name.getText().toString());
                    d.child("roll").setValue(roll.getText().toString());
                    d.child("phone").setValue(phone.getText().toString());
                    d.child("mail").setValue(mail.getText().toString());
                    sendVerification();
                }else{
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(getApplicationContext(),"Sign up failed,please try again : Password must be atleast has 6 characters",Toast.LENGTH_SHORT).show();
                }
            }
        });
        hideProgressDialog();
    }

    private void sendVerification() {
        final FirebaseUser firebaseUser=mAuth.getCurrentUser();
        firebaseUser.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Verification mail sent to : "+firebaseUser.getEmail(),Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(Signup.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Verification failed : "+task.getException(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mail.setError("Required.");
            valid = false;
        } else {
            mail.setError(null);
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
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    public boolean onOptionsItemSelected(MenuItem menu){
        mAuth.signOut();
        onBackPressed();
        return true;
    }


    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

}


