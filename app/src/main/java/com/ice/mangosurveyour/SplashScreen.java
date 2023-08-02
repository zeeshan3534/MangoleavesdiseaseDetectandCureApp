package com.ice.mangosurveyour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {
    private static int  a7 = 2500;
    TextView imageView;
    LottieAnimationView lottie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imageView = findViewById(R.id.appname);
        lottie = findViewById(R.id.lottie);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i =new Intent(getApplicationContext(), startingscreen.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        },a7);
    }
}