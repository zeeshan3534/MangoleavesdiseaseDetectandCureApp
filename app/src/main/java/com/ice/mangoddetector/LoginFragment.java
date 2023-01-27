package com.ice.mangoddetector;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LoginFragment extends Fragment {
    EditText email;
    EditText password;
    Button btn,btn1;
    DatabaseReference Userdata;
    HashMap<String,String> newhash;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        try {
            ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment,container,false);
            email = root.findViewById(R.id.mail);
            password = root.findViewById(R.id.pass);
            btn = root.findViewById(R.id.btn);
            btn1 = root.findViewById(R.id.button2);

            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   Offline();

                }
            });
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Checkin();

                }
            });


            return root;
        } catch (Exception e) {
            Log.e(TAG, "onCreateView", e);
            throw e;
        }

    }

    private void Offline() {
        Intent intent = new Intent(getContext(),mainpage.class);
        intent.putExtra("check","offline");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

                    if (String.valueOf(newhash.get("Password")).equals(password.getText().toString())){
                        newpage(true);
                    }else{
//                        Toast.makeText(this,"Your password is incorrect",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getContext(),"Password is Wrong", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getContext(),"Password or email is Wrong", Toast.LENGTH_SHORT).show();
                };

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
        Intent intent = new Intent(getContext(), mainpage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        ((Activity) getActivity()).overridePendingTransition(0, 0);
    }

}
