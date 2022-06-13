package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password,nickName;
    private Button register;

    private FirebaseAuth auth;

    private ImageView doughnut3;
    private StartActivity start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email=findViewById(R.id.Email);
        password=findViewById(R.id.Password);
        register=findViewById(R.id.RegisterButton);
        nickName=findViewById(R.id.nickname);

        auth=FirebaseAuth.getInstance();
        start=new StartActivity();

        doughnut3=findViewById(R.id.doughnutregister);
        doughnut3.setVisibility(View.INVISIBLE);

        ImageView image;
        image=findViewById(R.id.registerimage);
        Glide.with(this).load(R.drawable.raccoon).into(image);



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                doughnut3.setVisibility(View.VISIBLE);
                start.rotaterStart(doughnut3,500);

                String email_Txt=email.getText().toString();
                String password_Txt=password.getText().toString();





                if(TextUtils.isEmpty(email_Txt)||TextUtils.isEmpty(password_Txt))
                {
                    Toast.makeText(RegisterActivity.this, "Empty credentials", Toast.LENGTH_SHORT).show();
                    start.rotaterStop(doughnut3);
                    doughnut3.setVisibility(View.INVISIBLE);
                }
                else if(password_Txt.length()<6)
                {
                    Toast.makeText(RegisterActivity.this, "Password length too short", Toast.LENGTH_SHORT).show();
                    start.rotaterStop(doughnut3);
                    doughnut3.setVisibility(View.INVISIBLE);
                }
                else
                    registerUser(email_Txt,password_Txt);
            }
        });





    }

    private void registerUser(String email, String password) {

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User Successfully Registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainRecycler.class));


                    FirebaseFirestore db=FirebaseFirestore.getInstance();
                    Map<String,Object> users=new HashMap<>();
                    users.put("Name",nickName.getText().toString());

                    ArrayList<String> s=new ArrayList<String>();

                    users.put("favorites", Arrays.asList(""));
                    db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(users);

                    finish();
                }
                else
                {Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    start.rotaterStop(doughnut3);
                    doughnut3.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    public void onBackPressed() {

        startActivity(new Intent(RegisterActivity.this,StartActivity.class));
        finish();
    }

}