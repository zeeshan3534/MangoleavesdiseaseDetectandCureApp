package com.ice.mangoddetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Signin extends AppCompatActivity {
    TextView txt;
    EditText email;
    EditText password;
    Button btn;
    DatabaseReference Userdata;
    HashMap<String,String> newhash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        txt = findViewById(R.id.textView);
        email = findViewById(R.id.mail);
        password = findViewById(R.id.pass);
        btn = findViewById(R.id.btn);


//        rootDatabase = FirebaseDatabase.getInstance().getReference().child("User");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Checkin();

            }
        });

    }
    private void Checkin(){
        boolean flag = false;
        String var1 = email.getText().toString();

        List<String> newemail = Arrays.asList(var1.split("@")) ;
        Userdata = FirebaseDatabase.getInstance().getReference().child("users");


        Userdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                boolean isKeyPresent = map.containsKey(newemail.get(0));
                if (isKeyPresent){
                    newhash = (HashMap<String,String>)map.get(newemail.get(0)) ;
//                    System.out.println(String.valueOf(newhash.get("Password")));
//                    System.out.println(password.getText().toString());
                    if (String.valueOf(newhash.get("Password")).equals(password.getText().toString())){
                        newpage(true);
                    }else{
                        Toast.makeText(Signin.this,"Your password is incoorect",Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
//        System.out.println(newhash);


//
        }

    private void newpage(boolean b) {
        Intent intent = new Intent(this, mainpage.class);
        startActivity(intent);
    }

}
