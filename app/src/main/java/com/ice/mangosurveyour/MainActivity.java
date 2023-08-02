package com.ice.mangosurveyour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.database.DatabaseError;
import com.ice.mangosurveyour.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public ArrayList<Uri> uriList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new home());
        binding.bottomNavigationView.setBackground(null);
        FloatingActionButton fabNext = findViewById(R.id.upload);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to navigate to the next activity
                Intent intent = new Intent(MainActivity.this, UploadImage.class);
                intent.putExtra("type", "notcamera");
                intent.putExtra("uriimage", uriList);

                startActivity(intent);

            }
        });


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.cure:
                    replaceFragment(new cure());
                    break;
                case R.id.home:
                    replaceFragment(new home());
                    break;
                case R.id.camera:
                    replaceFragment(new Camera_MainActivity());
                    break;
                case R.id.report:
                    replaceFragment(new reports());
                    break;
//                case R.id.home:
//                    replaceFragment(new MainActivity());MainActivity

            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }




//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser user = mAuth.getCurrentUser();
//        String userId = user.getUid();
//        DatabaseReference userRef = mDatabase.child("users").child(userId);
//        if (user == null) {
//            startActivity(new Intent(MainActivity.this, login.class));
//        }
//
//        if (user != null) {
//
//
//            userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // This method will be called whenever the data at the "messages" node changes
//                if (dataSnapshot.exists()){
//                    String role = dataSnapshot.child("role").getValue().toString();
//                    System.out.println(role);
//                }
//
//
//
//            }
//
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w("MainActivity", "Failed to read value.", error.toException());
//            }
//        });
//        }
//
//
//    }


}