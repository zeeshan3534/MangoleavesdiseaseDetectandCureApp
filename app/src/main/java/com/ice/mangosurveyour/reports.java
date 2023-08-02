package com.ice.mangosurveyour;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ice.mangosurveyour.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class reports extends Fragment {

    DatabaseReference mDatabase, userRef, userfarm_report, nDatabase;
    MutableLiveData<ArrayList<byte[]>> reportKeyLiveData  = new MutableLiveData<>();
    private ListView reportKeysListView;
    private ArrayAdapter<String> reportKeysAdapter;
    private List<List<String>> ActualData;
    FirebaseAuth mAuth;
    ViewGroup root;
    String FarmSpinnerValue, SpeciesValue;
    String date;
    ArrayList< byte[]> imagedaata;
    ArrayList<String> farms,imageUrls;
    String Farmid,datetime;
    Integer flag;
    ArrayList<Bitmap> imageArray;
    String[] splittedBybracket;
    Spinner spinner;
    private AvLoadingDialog avLoadingDialog;
    HashMap<String, String> ActualValues, allReportKeys;

    // Add a variable to hold the third spinner
    Spinner thirdSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        avLoadingDialog = new AvLoadingDialog(getContext());

        root = (ViewGroup) inflater.inflate(R.layout.fragment_reports, container, false);
        // Initialize Firebase Auth

        mAuth = FirebaseAuth.getInstance();

        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is logged in, get the user ID
            String userId = currentUser.getUid();

            //Retriving User Farms information

            // Initialize Firebase Database
            mDatabase = FirebaseDatabase.getInstance().getReference();
            ActualValues = new HashMap<>();
            // Specify the path to the "users" node and the specific user ID "daninal"
            DatabaseReference userRef = mDatabase.child("users").child(userId);

            // Attach a listener to retrieve data from the "farm" key
            userRef.child("farm").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    farms = new ArrayList<String>();
                    if (dataSnapshot.exists()) {
                        // Data exists, retrieve the value associated with the "farm" key
                        String farmValue = dataSnapshot.getValue(String.class);
                        if (farmValue != null) {

                            String[] firstSplit = farmValue.split("@");

                            // Step 2: Loop through the first split array
                            for (String firstSplitString : firstSplit) {
                                // Step 3: Split each element using "_"

                                String[] secondSplit = firstSplitString.split("_");

                                // Check if there is an element after "_"
                                if (secondSplit.length > 1) {
                                    // Get the value after "_"
                                    String valueAfterUnderscore = secondSplit[1];

                                    ActualValues.put(valueAfterUnderscore, (String) firstSplitString);
                                    farms.add(valueAfterUnderscore);
                                }
                            }


                            // splitValuesList
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_spinner_item, farms);

                            // Set the dropdown layout style
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // Find the Spinner in your layout
                            spinner = root.findViewById(R.id.farmSelection);

                            // Set the ArrayAdapter as the data source for the Spinner
                            spinner.setAdapter(adapter);
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    // Retrieve the selected value
                                    FarmSpinnerValue = parent.getItemAtPosition(position).toString();

                                    UpdateSeconSpinner(ActualValues.get(FarmSpinnerValue));
//
//
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    // Handle the case when nothing is selected (optional)
                                    ((TextView) parent.getChildAt(0)).setError("Please select an option");
                                }
                            });



                        }
                    } else {
                        // Handle the case when "farm" key does not exist or has no value
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error if data retrieval is canceled
                }
            });

            DatabaseReference ReportsFarm = mDatabase.child("farms_").child(userId);


        } else {
            System.out.println("nothing");
        }



        AppCompatButton generate = root.findViewById(R.id.generateReport);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avLoadingDialog.show();
                if(Farmid!=null || datetime !=null && flag!=1){

                    userfarm_report = mDatabase.child("farms_").child(Farmid).child("reports").child(datetime);
                    userfarm_report.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                // Data exists, retrieve the String value
                                String reportData = dataSnapshot.getValue(String.class);


                                // Step 2: Define the type of the target data using TypeToken


                                ActualData=ConvertingStringToArray(reportData);


                            } else {
                                // Data does not exist at the specified location
                                // Handle the case when the data is not available
                                Toast.makeText(getContext(),"Please Select All Value",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle the error if data retrieval is canceled
                            Toast.makeText(getContext(),"Please Select All Value",Toast.LENGTH_LONG).show();
                        }
                    });

                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    // Create a storage reference from the image path
                    StorageReference imageRef = storage.getReference().child("farms/"+Farmid+"/"+datetime);
                    imagedaata = new ArrayList<>();

                    // List all the items (images) under the "datetime" location
                    imageRef.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ListResult> task) {
                            if (task.isSuccessful()) {
                                ArrayList<String> imageFilePaths = new ArrayList<>();

                                for (StorageReference item : task.getResult().getItems()) {
                                    // Get each image reference and download the bitmap
                                    item.getBytes(4024 * 4024) // Max download size (adjust as needed)
                                            .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                                                @Override
                                                public void onComplete(@NonNull Task<byte[]> task) {
                                                    if (task.isSuccessful() && task.getResult() != null) {
                                                        // Convert the downloaded bytes to a bitmap
                                                        Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);

                                                        // Save the bitmap to local storage and get the file path
                                                        String imagePath = saveBitmapToLocal(bitmap);

                                                        if (imagePath != null) {
                                                            imageFilePaths.add(imagePath);
                                                            // Check if all images are downloaded
//                                                            if (imageFilePaths.size() == task.getResult().length) {
                                                            // All images are downloaded and their file paths are saved
                                                            // You can use the "imageFilePaths" array here
                                                            // For example, pass it through the intent or use it as needed

                                                            Intent ReportGeneration = new Intent(getContext(), Report.class);
                                                            ReportGeneration.putExtra("Prediction", (Serializable) ActualData);
                                                            ReportGeneration.putStringArrayListExtra("images", imageFilePaths);
                                                            ReportGeneration.putExtra("direct", "no");
                                                            startActivity(ReportGeneration);

                                                        } else {
                                                            Log.e("TAG", "Error saving image to local storage");
                                                        }
                                                    } else {
                                                        Log.e("TAG", "Error downloading image: " + task.getException());
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Log.e("TAG", "Error listing images: " + task.getException());
                            }
                        }

                        private String saveBitmapToLocal(Bitmap bitmap) {
                            try {
                                // Save the bitmap to a file in the app's internal storage
                                Context context = getContext();
                                String filename = "image_" + System.currentTimeMillis() + ".png";
                                FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                outputStream.close();

                                // Get the file path of the saved image
                                return context.getFilesDir().getAbsolutePath() + "/" + filename;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                    });




                }else{

                    Toast.makeText(getContext(),"Please Select All Value",Toast.LENGTH_LONG).show();

                }

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        avLoadingDialog.cancel();
                    }
                };
                handler.postDelayed(runnable,7000);

            }
        });
        // Find the third Spinner in your layout
        saveKeyValuePair();
        thirdSpinner = root.findViewById(R.id.ThridSpinner);
        return root;
    }

    private List<List<String>> ConvertingStringToArray(String data) {


        List<List<String>> result = new ArrayList<>();

        // Remove square brackets at the beginning and end
        data = data.substring(1, data.length() - 1);

        // Split the string by '], [' to get individual arrays
        String[] arrayStrings = data.split("\\], \\[");

        // Iterate through each array string and split values by comma
        for (String arrayString : arrayStrings) {
            String[] values = arrayString.split(", ");
            result.add(Arrays.asList(values));
        }
        return result;
    }

    private void UpdateSeconSpinner(String value) {

        String[] Splitting = value.split("_");

        Farmid = Splitting[0];
        userfarm_report = mDatabase.child("farms_").child(Splitting[0]).child("reports/");
        userfarm_report.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allReportKeys = new HashMap<>();
                ArrayList<String> actualValue = new ArrayList<>();

                for (DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                    String key = reportSnapshot.getKey();

                    String[] SplitterReport = key.split("@");
                    allReportKeys.put(SplitterReport[0], key);
                    actualValue.add(key);
                }


                // Here, you have the list of all keys from the "reports" node
                // Call the function that depends on the allReportKeys here
                // For example, you can update the second spinner here
                if (allReportKeys == null) {
                    updateSecondSpinnerWithData(null, null);
                } else {
                    updateSecondSpinnerWithData(allReportKeys, actualValue);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("cancelled");
            }
        });
    }

    private void updateSecondSpinnerWithData(HashMap<String, String> reportDic, ArrayList<String> actualValue) {
        System.out.println("esi ki thesi");
        ArrayList<String> reportKeys = new ArrayList<>();
        System.out.println("reportDic: " + reportDic);
        if(reportDic == null){
            flag = 1;
        }
        flag=0;
        /////////////
        reportKeys.addAll(reportDic.keySet());

        // Create an ArrayAdapter for the second Spinner using reportKeys data
        ArrayAdapter<String> secondSpinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, reportKeys);
        secondSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Find the second Spinner in your layout
        Spinner secondSpinner = root.findViewById(R.id.reportSelection);

        // Set the ArrayAdapter as the data source for the second Spinner
        secondSpinner.setAdapter(secondSpinnerAdapter);

        // Remove the old OnItemSelectedListener before setting the new one
        secondSpinner.setOnItemSelectedListener(null);

        if (actualValue == null) {
            System.out.println("mather1");
            UpdateThirdSpinner(null, null);
        } else {
            secondSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Retrieve the selected value
                    System.out.println("yahan tu ara");
                    SpeciesValue = parent.getItemAtPosition(position).toString();

                    UpdateThirdSpinner(SpeciesValue, actualValue);
//                                    System.out.println("farms spinner" + FarmSpinnerValue);
//                                    selectedfarm(FarmSpinnerValue);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Handle the case when nothing is selected (optional)
                    ((TextView) parent.getChildAt(0)).setError("Please select an option");
                }
            });
        }
    }

    private void UpdateThirdSpinner(String speciesValue, ArrayList<String> actualValue) {
        System.out.println("speciesValue: " + speciesValue);
        ArrayList<String> datevaluelist = new ArrayList<>();
        HashMap<String, String> valuematching = new HashMap<>();
        for (int counter = 0; counter < actualValue.size(); counter++) {
            String SplitterValue = actualValue.get(counter);
            String[] Splitted = SplitterValue.split("@");
            if (Splitted[0].equals(speciesValue)) {
                datevaluelist.add(Splitted[2]);
                valuematching.put(Splitted[2], SplitterValue);
            }
        }
        ArrayAdapter<String> ThirdSpinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, datevaluelist);
        ThirdSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Find the third Spinner in your layout
        // Use the correct ID of the third Spinner in your layout
        Spinner thirdSpinner = root.findViewById(R.id.ThridSpinner);

        // Set the ArrayAdapter as the data source for the third Spinner
        thirdSpinner.setAdapter(ThirdSpinnerAdapter);


        thirdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected value
                String thirdValue = parent.getItemAtPosition(position).toString();

                datetime = valuematching.get(thirdValue);

//                                    System.out.println("farms spinner" + FarmSpinnerValue);
//                                    selectedfarm(FarmSpinnerValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected (optional)
                ((TextView) parent.getChildAt(0)).setError("Please select an option");
            }
        });
    }
    private void saveKeyValuePair() {
        reportKeyLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<byte[]>>() {
            @Override
            public void onChanged(ArrayList<byte[]>  BitmapImage) {
                // Here you can use the reportKey value from the LiveData

                // Do something with the reportKey...
            }
        });
    }
    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
