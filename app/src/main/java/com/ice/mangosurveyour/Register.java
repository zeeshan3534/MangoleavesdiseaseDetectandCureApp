package com.ice.mangosurveyour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

public class Register extends AppCompatActivity {

    EditText Reg_User;
    EditText Reg_Email;
    EditText Reg_Pass;
    EditText Reg_Pass_Cnfrm;
    Button regbtn;
    ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9+_.-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth mAuth;
    FirebaseUser mUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        Reg_User = findViewById(R.id.RegUser);
        Reg_Email = findViewById(R.id.RegEmail);
        Reg_Pass = findViewById(R.id.RegPass);
        Reg_Pass_Cnfrm = findViewById(R.id.RegPassCnfrm);
        regbtn = findViewById(R.id.reg_btn);
        progressDialog = new ProgressDialog(this);
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformAuth();

            }
        });
    }

    private void PerformAuth() {
        String user_name = Reg_User.getText().toString();
        String user_email = Reg_Email.getText().toString();
        String pass = Reg_Pass.getText().toString();
        String pass_conf = Reg_Pass_Cnfrm.getText().toString();

        if (!user_email.matches(emailPattern)){
            Reg_Email.setError("Email context not correct");
        }else if(pass.isEmpty() || pass.length()<6){
            Reg_Pass.setError("Password length should be more then 5");
        }else if(!pass.equals(pass_conf)){
            Reg_Pass_Cnfrm.setError("Password not match");
        }else{
            progressDialog.setMessage("Please wait ");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(user_email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        progressDialog.dismiss();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference usersRef = database.getReference("users");
                        String userId = mAuth.getUid();
                        DatabaseReference userRef = usersRef.child(userId);
                        userRef.child("username").setValue(user_name);
                        sendUserToNextActivity();
                        Toast.makeText(Register.this,"Registration Successful",Toast.LENGTH_LONG).show();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(Register.this,""+task.getException(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }
    private void sendUserToNextActivity(){
        Intent intent = new Intent(Register.this,login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}