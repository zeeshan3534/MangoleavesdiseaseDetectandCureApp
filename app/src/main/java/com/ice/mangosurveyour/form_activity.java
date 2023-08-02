package com.ice.mangosurveyour;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class form_activity extends AppCompatActivity {
    MutableLiveData<String> reportKeyLiveData  = new MutableLiveData<>();
    String getUser;
    String FarmName;
    EditText Reg_user;
    String selectedValue;
    String selectedValue2;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    CheckBox generate_key;
    String randomString;
    EditText create_farm;
    EditText create_loc;
    Spinner mySpinner;
    Spinner mySpinner2;
    Button Submit_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        generate_key = findViewById(R.id.generateKey);
        create_farm = findViewById(R.id.createFarm);
        create_loc = findViewById(R.id.createLoc);
        mySpinner = findViewById(R.id.spinner);
        mySpinner2 = findViewById(R.id.spinner2);
        Reg_user = findViewById(R.id.updateUser);
        Submit_btn = findViewById(R.id.submitBtn);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mySpinner.setPrompt(getResources().getString(R.string.mySpinnerPrompt));
        mySpinner.setAdapter(ArrayAdapter.createFromResource(this, R.array.mySpinnerItems, android.R.layout.simple_spinner_dropdown_item));
        mySpinner.setSelection(0, false);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setError("Please select an option");
                } else {
                    selectedValue = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
                ((TextView) parent.getChildAt(0)).setError("Please select an option");
            }
        });

        mySpinner2.setPrompt(getResources().getString(R.string.mySpinnerPrompt2));
        mySpinner2.setAdapter(ArrayAdapter.createFromResource(this, R.array.typesSpinnerItems, android.R.layout.simple_spinner_dropdown_item));
        mySpinner2.setSelection(0, false);
        mySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setError("Please select an option");
                } else {
                    selectedValue2 = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        Reg_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    randomString = null;
                    create_farm.setText(null);
                    create_loc.setText(null);
                    selectedValue = null;
                    generate_key.setChecked(false);
                    generate_key.setVisibility(GONE);
                    create_farm.setVisibility(GONE); // hide the TextView
                    create_loc.setVisibility(GONE); // hide the TextView
                    mySpinner.setVisibility(GONE); // hide the TextView
                } else {
                    generate_key.setVisibility(View.VISIBLE); // VISIBLE the TextView
                    create_farm.setVisibility(View.VISIBLE); // VISIBLE the TextView
                    create_loc.setVisibility(View.VISIBLE); // VISIBLE the TextView
                    mySpinner.setVisibility(View.VISIBLE); // VISIBLE the TextView
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        generate_key.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Reg_user.setText(null);
                    Reg_user.setVisibility(GONE); // hide the TextView

                    if (randomString == null) {
                        String lettersAndDigits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
                        int length = 20;
                        Random random = new Random();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < length; i++) {
                            int index = random.nextInt(lettersAndDigits.length());
                            sb.append(lettersAndDigits.charAt(index));
                        }
                        randomString = sb.toString();
                    }
                } else {
                    Reg_user.setVisibility(View.VISIBLE); // show the TextView

                }
            }
        });
        Submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Reg_user.getText().toString().isEmpty()){
                    implementFirebaseForAdmin();
                }else{
                    implementFirebaseForWorker();
                }
            }
        });
    }

    private void implementFirebaseForWorker() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference userRef = mDatabase.child("users").child(userId);
        DatabaseReference userRefFarm = mDatabase.child("farms_");

        userRefFarm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Farm_Name_Value = "";
                if (dataSnapshot.child(Reg_user.getText().toString()).child("farm_name").getValue() != null){
                    Farm_Name_Value = dataSnapshot.child(Reg_user.getText().toString()).child("farm_name").getValue().toString();
                }
                reportKeyLiveData.setValue(Farm_Name_Value);
//                Log.d("", Objects.requireNonNull(dataSnapshot.child(String.valueOf(Reg_user.getText())).child("farm_name").getValue()).toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur while reading the data
                Log.e("FirebaseError", "Error reading 'role' value: " + databaseError.getMessage());
            }
        });


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the "role" value exists in the database
                if (dataSnapshot.exists()) {

                    Map<String, Object> updateValuesfarms = new HashMap<>();
                    String make = (String) dataSnapshot.child("farm").getValue();
                    // Get the value of "role" from the dataSnapshot
                    String role = dataSnapshot.child("role").getValue(String.class);
                    // Now, you have the value of "role"
                    // You can use the "role" value here or perform any desired actions
                    if (Objects.equals(role, "admin")){
                        Toast.makeText(getApplicationContext(),"You Are Admin Cannot Be Worker",Toast.LENGTH_LONG).show();
                    }else {
                        if (Objects.equals(role, "worker")){
                            reportKeyLiveData.observe(form_activity.this, new Observer<String>() {
                                @Override
                                public void onChanged(String reportKey) {
                                    // Here you can use the reportKey value from the LiveData
                                    if (!reportKey.equals("")) {

                                        if (make == null) {
                                            userRef.child("farm").setValue(Reg_user.getText().toString() + "_" + reportKey);
//                                            updateValuesfarms.put("farm", );

//                                            userRef.updateChildren(updateValuesfarms);
                                        } else {
                                            userRef.child("farm").setValue( dataSnapshot.child("farm").getValue().toString() + "@" + Reg_user.getText().toString() + "_" + reportKey);
                                        }
                                        //////////////////////////////////////////
                                        Intent intent = new Intent(form_activity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }else{
                                        Toast.makeText(getApplicationContext(),"Farm Id is not correct",Toast.LENGTH_LONG).show();
                                    }
                                    // Do something with the reportKey...
                                }
                            });


                            //
//                            if (make == null) {
//                                updateValuesfarms.put("farm", Reg_user.getText().toString() + "_" + reportKeyLiveData.getValue());
//
//                            }else{
//                                updateValuesfarms.put("farm", dataSnapshot.child("farm").getValue().toString()+"@"+Reg_user.getText().toString() + "_" + reportKeyLiveData.getValue());
//                            }
                        }
                        else if (Objects.equals(role, null)){
                            reportKeyLiveData.observe(form_activity.this, new Observer<String>() {
                                @Override
                                public void onChanged(String reportKey) {
                                    // Here you can use the reportKey value from the LiveData
                                    if (!reportKey.equals("")) {

                                        if (make == null) {
                                            userRef.child("farm").setValue(Reg_user.getText().toString() + "_" + reportKey);
//                                            updateValuesfarms.put("farm", );
                                            userRef.child("role").setValue("worker");
//                                            userRef.updateChildren(updateValuesfarms);
                                        } else {
                                            userRef.child("farm").setValue( dataSnapshot.child("farm").getValue().toString() + "@" + Reg_user.getText().toString() + "_" + reportKey);
                                        }
                                        ///////////////////////////////
                                        Intent intent = new Intent(form_activity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Farm Id is not correct",Toast.LENGTH_LONG).show();
                                    }
                                    // Do something with the reportKey...
                                }
                            });

                        }

                    }
                } else {
                    // Handle the case where the "role" value does not exist in the database
                    Log.d("RoleValue", "Role value does not exist.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur while reading the data
                Log.e("FirebaseError", "Error reading 'role' value: " + databaseError.getMessage());
            }
        });
    }

    private void implementFirebaseForAdmin() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference userRef = mDatabase.child("users").child(userId);
        DatabaseReference userRefFarm = mDatabase.child("farms_");
        if (user.equals(null)) {
            startActivity(new Intent(form_activity.this, MainActivity.class));
        } else {
            if (Reg_user.length() < 1) {
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // This method will be called whenever the data at the "messages" node changes
                        if (dataSnapshot.exists()) {
                            Map<String, Object> updateValuesfarms = new HashMap<>();
                            String make = (String) dataSnapshot.child("farm").getValue();

                            if (make == null) {
                                updateValuesfarms.put("farm", randomString + "_" + create_farm.getText().toString());
                                userRef.child("role").setValue("admin");
                            }else{
                                updateValuesfarms.put("farm", dataSnapshot.child("farm").getValue().toString()+"@"+randomString + "_" + create_farm.getText().toString());
                            }

                            userRef.updateChildren(updateValuesfarms);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("Form Activity", "Failed to read value.", error.toException());
                    }
                });

                userRefFarm.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // This method will be called whenever the data at the "messages" node changes
                        if (dataSnapshot.exists()) {
                            Map<String, Object> updateValues = new HashMap<>();
                            updateValues.put("admin", getUser);
                            updateValues.put("farm_name", create_farm.getText().toString());
                            updateValues.put("location", create_loc.getText().toString());
                            updateValues.put("province", selectedValue);
                            updateValues.put("specie", selectedValue2);
                            updateValues.put("workers", "");
                            // Update the node with the new value(s)
                            userRefFarm.child(randomString).updateChildren(updateValues);
                            Intent intent = new Intent(form_activity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("MainActivity", "Failed to read value.", error.toException());
                    }
                });
            } else {
//                userReffarm.addListenerForSingleValueEvent(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        // This method will be called whenever the data at the "messages" node changes
//                        if (dataSnapshot.exists()){
//                            Map<String, Object> updateValues = new HashMap<>();
//                            updateValues.put("admin", getUser);
//                            updateValues.put("farm_name", create_farm.getText().toString());
//                            updateValues.put("location", create_loc.getText().toString());
//                            updateValues.put("province",selectedValue );
//                            updateValues.put("specie",selectedValue2);
//                            updateValues.put("workers","");
//
//                            // Update the node with the new value(s)
//                            userReffarm.child(randomString).updateChildren(updateValues);
//
//                            Intent intent = new Intent(form_activity.this,MainActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        }
//
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.w("MainActivity", "Failed to read value.", error.toException());
//                    }
//                });
            }
        }


    }
//    private Integer saveKeyValuePair() {
//
//        return 1;
//    }

}