package com.ice.mangosurveyour;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class selectFarm extends DialogFragment  {
    MutableLiveData<String> reportKeyLiveData  = new MutableLiveData<>();
    HashMap<String,String> dataMap;
    HashMap<String,byte[]> bitmapArrayListHashMap;
    String userReportKey;
    String userReportValue;
    String reportKey;
    Spinner farmSpinner;
    Spinner specieSpinner;
    ArrayList<String> farmNameList;
    Button submitReportBtn;
    String selectedSpinnerFarm;
    String selectedSpinnerSpecie;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    DatabaseReference userRef;
    DatabaseReference userRefFarm;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_farm, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        userRef = mDatabase.child("users").child(userId);
        userRefFarm = mDatabase.child("farms_");
        farmNameList = new ArrayList<>();
        farmSpinner = view.findViewById(R.id.farmSelection);
        specieSpinner = view.findViewById(R.id.specieSelection);
        submitReportBtn = view.findViewById(R.id.submitReport);
        getFarmName();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, farmNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        farmSpinner.setAdapter(adapter);
        specieSpinner.setAdapter(ArrayAdapter.createFromResource(requireContext(), R.array.specieSpinnerItems, android.R.layout.simple_spinner_dropdown_item));
        setSpinnerValue();
        setOnClickListener();
        return view;
    }

    private void getFarmName() {
        for (Map.Entry<String, String> farmPair : dataMap.entrySet()) {
            farmNameList.add(farmPair.getValue());
        }
    }

    private void setSpinnerValue(){
        farmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the Spinner and store it in the selectedValue variable
                selectedSpinnerFarm = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // This method is called when nothing is selected.
                ((TextView) parent.getChildAt(0)).setError("Please select an option");
            }
        });
        specieSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the Spinner and store it in the selectedValue variable
                selectedSpinnerSpecie = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // This method is called when nothing is selected.
                ((TextView) parent.getChildAt(0)).setError("Please select an option");
            }
        });
    }

    private void implementFirebase(){
        userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = (String) dataSnapshot.getValue();
                reportKey =  getReportKey(userName);
                reportKeyLiveData.setValue(reportKey);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    private void saveReportImageIntoFirebaseStorage(String farmKey,String reportKey){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("farms/"+reportKey+"/"+farmKey);
            for (Map.Entry<String, byte[]> entry : bitmapArrayListHashMap.entrySet()) {
            String imageName = entry.getKey();
            byte[] imageValue = entry.getValue();
            StorageReference storeImageLastReference = storageRef.child(imageName);
            storeImageLastReference.putBytes(imageValue);
        }



    }

    private void saveReportDataToFirebase(String reportKey){
        String farmKey = getKeyByValue(dataMap, selectedSpinnerFarm);
        Query query = userRefFarm.orderByKey().equalTo(farmKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference farmRef = userRefFarm.child(farmKey);
                HashMap<String, Object> updateKeyValuesFarms = new HashMap<>();
                HashMap<String, Object> updateReportData = new HashMap<>();
                String checkReport = (String) dataSnapshot.child("reports").getValue();
                updateReportData.put(reportKey, userReportValue);
                if (checkReport == null) {
                    // The report does not exist in the database, so add it as a new entry
                    updateKeyValuesFarms.put("reports/" + reportKey, userReportValue);
                    farmRef.updateChildren(updateKeyValuesFarms)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Report added successfully
                                    saveReportImageIntoFirebaseStorage(reportKey,farmKey);


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to add the report
                                    Log.e("Firebase", "Failed to add the report", e);
                                }
                            });
                } else {
                    // The report already exists in the database, so update its value
                    updateKeyValuesFarms.put("reports/" + reportKeyLiveData.getValue(), userReportValue);
                    farmRef.updateChildren(updateKeyValuesFarms)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Report updated successfully

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to update the report
                                    Log.e("Firebase", "Failed to update the report", e);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });



    }
    public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null; // Value not found in the map
    }
    private Integer saveKeyValuePair() {
        reportKeyLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String reportKey) {
                // Here you can use the reportKey value from the LiveData
                saveReportDataToFirebase(reportKey);
                // Do something with the reportKey...
            }
        });
        return 1;
    }
    private String getReportKey(String userName){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss-a", Locale.getDefault());
        String formattedDateTime = sdf.format(calendar.getTime());
        userReportKey = selectedSpinnerSpecie + "@" + userName + "@" + formattedDateTime ;
        return userReportKey;
    }

    private void setOnClickListener(){
        submitReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                implementFirebase();
                saveKeyValuePair();
                Toast.makeText(requireContext(), "Report Submit Successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


}