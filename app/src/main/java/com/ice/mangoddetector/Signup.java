package com.ice.mangoddetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Signup extends AppCompatActivity {

    EditText username;
    EditText email;
    EditText repass;
    EditText password;
    DatabaseReference Userdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        repass = (EditText) findViewById(R.id.repass);
        MaterialButton btn = (MaterialButton) findViewById(R.id.btn);


        Userdata = FirebaseDatabase.getInstance().getReference().child("users");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                String var1 = email.getText().toString();

                List<String> newemail = Arrays.asList(var1.split("@")) ;

                if (password.getText().toString().equals(repass.getText().toString())){
                    System.out.println("anything");
                    InsertData(newemail.get(0));
                    Login();

                }else{
                    Toast.makeText(Signup.this,"Your Passwords are not same",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void Login(){
        Intent intent = new Intent(this,Signin.class);
        startActivity(intent);
    }
    private void InsertData(String var){

        String user_name = username.getText().toString();
        String Email = email.getText().toString();
        String Password = password.getText().toString();
        HashMap<String,Object> dict = new HashMap<>();
        dict.put("user_name",user_name);
        dict.put("Password",Password);
        dict.put("Email",Email);

//        userdata users = new userdata(user_name,Email,Password);
        Userdata.child(var).setValue(dict);
        Toast.makeText(Signup.this,"Data Inserted",Toast.LENGTH_SHORT).show();
    }


}