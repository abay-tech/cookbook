package com.example.firebasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;


public class splash extends AppCompatActivity {
    @Override

    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        //Toast.makeText(splash.this, "WELCOME", Toast.LENGTH_SHORT).show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(splash.this,StartActivity.class));

                finish();
            }
        },3000);

        ImageView image;
        image=findViewById(R.id.splashGif);
        //Glide.with(this).load(R.drawable.bowl).into(image);
        Glide.with(this).load(R.drawable.sandwitch).into(image);


        //for setting action bar(the thing that shows the battery percentage)
        Window window = splash.this.getWindow();
            //for color
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(splash.this, R.color.splashColor));
            //for text color
        getWindow().getDecorView().setSystemUiVisibility( WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS );
        // window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);




    }
}
