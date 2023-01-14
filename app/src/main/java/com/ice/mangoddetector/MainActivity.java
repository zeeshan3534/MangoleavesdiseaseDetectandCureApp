package com.ice.mangoddetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button button,button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)  findViewById(R.id.signup);
        button1 = (Button)  findViewById(R.id.signin);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Signin();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Signuppage();
            }
        });

    }
    private  void Signin(){
        Intent intent = new Intent(this,Signin.class);
        startActivity(intent);
    }

    private void Signuppage(){
        Intent intent = new Intent(this,Signup.class);
        startActivity(intent);
    }
}