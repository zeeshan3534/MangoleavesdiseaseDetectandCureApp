package com.ice.mangoddetector;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Diagnose extends AppCompatActivity {
    Button search;
    TextView text,text1,text2,text3;
    EditText editText;
    ImageView img;
    DatabaseReference Userdata,Userdata2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnose);
        search = findViewById(R.id.button4);
        text = findViewById(R.id.textView3);
        text1 = findViewById(R.id.textView);
        editText = findViewById(R.id.editTextTextPersonName);
        img = findViewById(R.id.imageView3);
        text2 = findViewById(R.id.textView13);
        text3 = findViewById(R.id.textView14);
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        Intent intent = getIntent();

        String username = (String) intent.getExtras().getString("username");
        if (connected){
            text2.setText(username);
        }else{
            text2.setText("Internet not Connect ");
        };
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text.setVisibility(View.VISIBLE);
                Search();
            }
        });

    }
    private void Search(){
        Userdata = FirebaseDatabase.getInstance().getReference().child("cure");
        Userdata2 = FirebaseDatabase.getInstance().getReference().child("DiseasesImage");

        Userdata.addValueEventListener(new ValueEventListener() {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            boolean connected1 = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                boolean isKeyPresent = map.containsKey(editText.getText().toString().toLowerCase());

                if (isKeyPresent){
                    text1.setText(editText.getText().toString().toUpperCase());
                    String value1 = editText.getText().toString().toLowerCase();
                    String value = (String) map.get(value1);


                    text.setText(value);
//                    System.out.println(map+"khfhle");
                    Userdata2.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String,Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                            String link = (String) map.get(editText.getText().toString().toLowerCase()) ;
                            Picasso.get().load(link).into(img);


//                    System.out.println(map+"khfhle");


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });


                }else if(!connected1){
                    text.setVisibility(View.INVISIBLE);
                    text3.setText("This Portion Required Internet");
                }

                else{

                    text.setText("The Diseases your trying to find is not available yet...");
//                    Toast.makeText(Diagnose.this,"Diseases Not found",Toast.LENGTH_LONG);
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    };
}