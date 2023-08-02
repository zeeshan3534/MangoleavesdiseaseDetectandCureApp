package com.ice.mangosurveyour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    EditText login_input;
    EditText pass_input;
    Button btnLogin;
    FirebaseAuth mAuth;
    TextView Sign_up_btn;
    private AvLoadingDialog avLoadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        avLoadingDialog = new AvLoadingDialog(this);
        login_input = findViewById(R.id.eLoginMail);
        pass_input = findViewById(R.id.pass_word);
        btnLogin = findViewById(R.id.login_btn);
        Sign_up_btn = findViewById(R.id.signUpBtn);
        mAuth = FirebaseAuth.getInstance();


        Sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, Register.class);
                startActivity(intent);

            }
        });
        btnLogin.setOnClickListener(view ->{
            avLoadingDialog.show();
            loginUser();
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    avLoadingDialog.cancel();
                }
            };
            handler.postDelayed(runnable,5000);
        });

//        Login video

        VideoView videoView = findViewById(R.id.password);

        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.normal));
        videoView.start();

        videoView.setMediaController(null);
        login_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    videoView.stopPlayback();
                    videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.username));
                    videoView.start();
                } else {
                    videoView.stopPlayback();
                    videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.normal));
                    videoView.start();
                }
            }
        });
        pass_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    videoView.stopPlayback();
                    videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.password));
                    videoView.start();
                } else {
                    videoView.stopPlayback();
                    videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.normal));
                    videoView.start();
                }
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        videoView.start();










//        End




    }

    private void loginUser(){
        String email = login_input.getText().toString();
        String password = pass_input.getText().toString();

        if (TextUtils.isEmpty(email)){
            login_input.setError("Email can not be Empty");
            login_input.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            pass_input.setError("Password can not be Empty");
            pass_input.requestFocus();
        }else {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(login.this,"User Login Successfully",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(login.this,MainActivity.class));
                    }else{
                        Toast.makeText(login.this,"User Login Error"+task.getException().getMessage(),Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}