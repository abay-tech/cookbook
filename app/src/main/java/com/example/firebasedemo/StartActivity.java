package com.example.firebasedemo;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class StartActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN=9001;
    private long pressedTime;

    private EditText email;
    private EditText password;
    private FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_2);
        auth=FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());



        findViewById(R.id.registernew).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(StartActivity.this,RegisterActivity.class));
                finish();
            }
        });
        findViewById(R.id.helpbutton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this,RegisterActivity.class));
                finish();
            }
        });
        findViewById(R.id.googlebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        email=findViewById(R.id.loginEmail);
        password=findViewById(R.id.loginPassword);
        findViewById(R.id.signIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_Txt=email.getText().toString();
                String password_Txt=password.getText().toString();

                if(email_Txt.isEmpty()||password_Txt.isEmpty()) {
                    Toast.makeText(StartActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    logUser(email_Txt,password_Txt);
                }
            }
        });

    }

    private void logUser(String email, String password) {
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Toast.makeText(StartActivity.this, "LOGGED IN!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StartActivity.this, MainRecycler.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });
    }
    public void onBackPressed(){

        if(pressedTime+2000>System.currentTimeMillis())   {
            super.onBackPressed();
            finish();
        }
        else   {
            Toast.makeText(StartActivity.this, "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime=System.currentTimeMillis();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            startActivity(new Intent(StartActivity.this, MainRecycler.class));
            finish();

        }
    }

//for rotating doughnut loading ,make new startactivity class and use this functions
    public void rotaterStart(ImageView img,long time){

        RotateAnimation rotate=new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,.5f,Animation.RELATIVE_TO_SELF,.5f);
        rotate.setDuration(time);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        img.startAnimation(rotate);
    }
    public void rotaterStop(ImageView img){
        img.clearAnimation();
    }


    void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {


            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(StartActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(StartActivity.this, MainRecycler.class));
                            finish();



                        } else {
                            Toast.makeText(StartActivity.this, "ERRRRRR", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}