package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity_dead extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;

    private FirebaseAuth auth;


    private ImageView doughnut2;
    private StartActivity start;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //email=findViewById(R.id.loginEmail);
        //password=findViewById(R.id.loginPassword);
        //login=findViewById(R.id.signinbutton);

        doughnut2=findViewById(R.id.doughnutlogin);
        doughnut2.setVisibility(View.INVISIBLE);

        start=new StartActivity();

        auth=FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doughnut2.setVisibility(View.VISIBLE);
                start.rotaterStart(doughnut2,500);

                String email_Txt=email.getText().toString();
                String password_Txt=password.getText().toString();

                if(email_Txt.isEmpty()||password_Txt.isEmpty()) {
                    Toast.makeText(LoginActivity_dead.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                    start.rotaterStop(doughnut2);
                    doughnut2.setVisibility(View.INVISIBLE);
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

                Toast.makeText(LoginActivity_dead.this, "LOGGED IN!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity_dead.this, MainRecycler.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity_dead.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                start.rotaterStop(doughnut2);
                doughnut2.setVisibility(View.INVISIBLE);
            }
        });
    }




    public void onBackPressed() {

        startActivity(new Intent(LoginActivity_dead.this,StartActivity.class));
        finish();
    }

}