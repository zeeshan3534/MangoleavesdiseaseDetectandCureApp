package com.ice.mangoddetector;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import androidx.fragment.app.Fragment;

public class SignupFragment extends Fragment {
    EditText username;
    EditText email;
    EditText repass;
    EditText password;
    Button btn;
    DatabaseReference Userdata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        try {
            ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_tab_fragment,container,false);


            username = (EditText) root.findViewById(R.id.username);
            email = (EditText) root.findViewById(R.id.email);
            password = (EditText) root.findViewById(R.id.password);
            repass = (EditText) root.findViewById(R.id.repass);
            btn =  root.findViewById(R.id.btn);

            Userdata = FirebaseDatabase.getInstance().getReference().child("users");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View view) {
                    String var1 = email.getText().toString();

                    List<String> newemail = Arrays.asList(var1.split("@")) ;

                    if (password.getText().toString().equals(repass.getText().toString())){
                        username.setText("");
                        email.setText("");
                        password.setText("");
                        repass.setText("");
                        InsertData(newemail.get(0));
                        Login();

                    }else{
                        Toast.makeText(getContext(),"Your Password is not correct",Toast.LENGTH_LONG).show();
                    }
                }
            });



            return root;
        } catch (Exception e) {
            Log.e(TAG, "onCreateView", e);
            throw e;
        }
    }
    private void Login(){
        Toast.makeText(getContext(),"Account Created Kindly Login",Toast.LENGTH_SHORT).show();

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
        Toast.makeText(getContext(),"Data Inserted",Toast.LENGTH_SHORT).show();
    }


}
