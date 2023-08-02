package com.ice.mangosurveyour;

import static android.content.Intent.getIntent;
//import static androidx.core.app.AppOpsManagerCompat.Api23Impl.getSystemService;


//import static androidx.core.content.ContextCompat.Api23Impl.getSystemService;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class cure extends Fragment {
    Button search;
    TextView text,text1,text2,text3;
    EditText editText;
//    ImageView img;
    DatabaseReference Userdata,Userdata2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_cure,container,false);


        search = root.findViewById(R.id.button4);
        text = root.findViewById(R.id.textView3);
        text1 = root.findViewById(R.id.textView);
        editText = root.findViewById(R.id.editTextTextPersonName);
//        img = root.findViewById(R.id.imageView3);
        text2 = root.findViewById(R.id.textView13);
        text3 = root.findViewById(R.id.textView14);
        ConnectivityManager connectivityManager = null;

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                text.setVisibility(View.VISIBLE);
                Search();

            }
        });

//        return ;
        return root;
    }
    private void Search(){
        Userdata = FirebaseDatabase.getInstance().getReference().child("cure");
        Userdata2 = FirebaseDatabase.getInstance().getReference().child("DiseasesImage");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Userdata.addValueEventListener(new ValueEventListener() {
//                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//
//                private Object getSystemService(String connectivityService) {
//                };

//                boolean connected1 = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

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
//                        Userdata2.addValueEventListener(new ValueEventListener() {
//
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                HashMap<String,Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
//                                String link = (String) map.get(editText.getText().toString().toLowerCase()) ;
//                                Picasso.get().load(link).into(img);
//
//
//    //                    System.out.println(map+"khfhle");
//
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                                System.out.println("The read failed: " + databaseError.getCode());
//                            }
//                        });


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
        }
    }
}