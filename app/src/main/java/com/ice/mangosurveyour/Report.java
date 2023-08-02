package com.ice.mangosurveyour;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Report extends AppCompatActivity {
    private boolean isDialogOpened = false;
    private static final String KEY_DIALOG_STATE = "dialog_state";
    MutableLiveData<HashMap<String, String>> mutableLiveData = new MutableLiveData<>();
    RecyclerView reported;

    HashMap<String, String> DataMutableMap;
    RecyclerView.Adapter<ReportGeneratorAdapter.Reporterviewholder> adapter;
    DatabaseReference userRef;
    DatabaseReference userfarm_report;
    Button btn1;
    FirebaseAuth mAuth;

    String result;

    HashMap<String,String> resultSelectFarm;
    HashMap<String,byte[]> bitmapArrayListHashMap;
    ArrayList<ArrayList<String >> arraySendToDialog;
    DatabaseReference mDatabase;

    ArrayList<String> newArr= new ArrayList<String>();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);


        HashMap<Bitmap,HashMap<String,Float>> newhash = new HashMap<>();
        DataMutableMap = new HashMap<>();
        arraySendToDialog = new ArrayList<>();
        resultSelectFarm = new HashMap<>();
        bitmapArrayListHashMap = new HashMap<>();
        List<String> DiseaseArray = Arrays.asList("Anthracnose","Die Black","Gall Midge","Healthy","Powdery Mildew","Sooty Mould");
        btn1= findViewById(R.id.report);
        reported = findViewById(R.id.recylerview);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        userRef = mDatabase.child("users").child(userId);
        userfarm_report = mDatabase.child("farms_");

        Intent intent = getIntent();
        HashMap<Uri, ArrayList<Float>> Report_array = (HashMap<Uri, ArrayList<Float>>) intent.getSerializableExtra("hashMap");

//        Report retrieveing bitmap
        ArrayList<String> imageTitle= new ArrayList<>();
        HashMap<String,Bitmap> report_data = new HashMap<>();


//        HashMap<String, String> map = new HashMap<>();
//        map.put("Anthracnose","Regular spraying of trees from flowering time onwards with mancozeb (at recommended label rates every 14 days) is useful to reduce the level of infection in the developing fruit. Do not use mancozeb within 14 days of harvest. If anthracnose becomes serious in green immature fruit it would be useful to give a couple of judicious sprays of prochloraz");
//        map.put("Apoderus Javanicus","Pesticides: Pesticides can be used to control the beetles on the tree. Sanitation: Proper sanitation of the orchard can help to reduce the population of the beetles. This includes removing and destroying any fallen fruit and debris from the tree, which can serve as a breeding ground for the beetles");
//        map.put("Bacterial Canker","Regular inspection of orchards, sanitation, and seedling certification are recommended as preventive measures against the disease. Spray of copper-based fungicides has been found effective in controlling bacterial canker.");
//        map.put("Dappula Tertia","Pesticides: Insecticides can be applied to the trunk and branches of the tree to kill larvae and adult beetles. Cultural control: Keeping trees healthy by providing adequate water, fertilizer, and pruning can help to reduce the chances of infestation.");
//        map.put("Dialeuropora Decempuncta","Control of Dialeurodes infestation can be done through the use of pesticides and other control measures like using natural enemies of whitefly such as lady beetles, lacewings, and parasitic wasps.");
//        map.put("Gall Midge"," Pesticides: Spraying of 0.05% fenitrothion, 0.045% dimethoate at bud burst stage of the inflorescence can be effective in controlling the pest. Foliar application of bifenthrin (70ml/100lit) mixed with water has also given satisfactory results. Sanitation: removing and destroying any heavily infested shoots, leaves, and fruits from the tree.");
//        map.put("Black Soothy Mold","The best way to control sooty mold fungi is using preventive method by eliminating their sugary food supply. Controlling sap-feeding insects on the foliage as well as ants that tend and protect them. General-purpose fungicide may be effective on killing fungi but not removing black color.");
//        map.put("Icerya Seychellarum","Insecticides such as horticultural oil, and insecticidal soap can be used for prevention. It was found that paraffin oil at 1.25% was the most effective insecticide.");
//        map.put("Mictis Longicornis","Control measures include the use of pesticides, pruning and removing infested branches.");
//        map.put("Neomelicharia Sparsa","The disease can be controlled by removing infected plant parts, and by applying fungicides. Cultural practices such as proper pruning and irrigation can also help prevent the disease from spreading.");



        ////////////////////////////////////////Condition for data coming from  /////////////////////////////////////

        String condition = (String) intent.getSerializableExtra("direct");
        System.out.println(condition);
///////////////////////////////////// ///////////////////////////////////// /////////////////////////////////////
        if (condition.equals("yes")){

            for (HashMap.Entry<Uri, ArrayList<Float>> set :Report_array.entrySet()){
                HashMap<String,Float> Report_list = new HashMap<>();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        set.getValue().stream().sorted((v1, v2) -> v2.compareTo(v1)).limit(3).map(v -> set.getValue().indexOf(v)).forEach(i -> Report_list.put(DiseaseArray.get(i), set.getValue().get(i)));
                    }
                    List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String, Float>>(Report_list.entrySet());
                    Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });
                    Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
                    for (Map.Entry<String, Float> entry : list) {
                        sortedMap.put(entry.getKey(), entry.getValue());
                    }
                    newhash.put(Convbitmap(set.getKey()), (HashMap<String, Float>) sortedMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            arraySendToDialog = generateReportListValues(newhash,report_data,imageTitle);
            result = arraySendToDialog.toString();




            reportgenerator(newhash);}
        else if(condition.equals("no")){
            reported.setHasFixedSize(true);
            reported.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            ArrayList<ReportGeneratorHelper> reportGeneratorHelpers = new ArrayList<>();
            List<List<String>> diseases = (List<List<String>>) intent.getSerializableExtra("Prediction");
//            ArrayList<byte[]> Imagearray = (ArrayList<byte[]>) intent.getSerializableExtra("images");



            // Retrieve the image file paths from the intent
            ArrayList<String> imageFilePaths = getIntent().getStringArrayListExtra("images");
            ArrayList<byte[]> Imagearray = new ArrayList<>();

            // Loop through each image file path and read the bytes from the file
            for (String filePath : imageFilePaths) {
                try {
                    // Read the bytes from the file
                    FileInputStream inputStream = new FileInputStream(filePath);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[10024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                    inputStream.close();
                    outputStream.close();

                    // Get the byte array from the output stream and add it to the list
                    byte[] byteArray = outputStream.toByteArray();
                    Imagearray.add(byteArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

















            System.out.println("Actual image agyi"+Imagearray);
            System.out.println("Actual Data agyi"+diseases);
//            String data = diseases.get(0).get(6);
//            System.out.println("data"+data);
            ///////////////////////////////////////////////
            int newHeightInPixels = 2000;

// Get the current layout parameters of the RecyclerView
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) reported.getLayoutParams();

// Update the height in the layout parameters
            layoutParams.height = newHeightInPixels;

// Set the updated layout parameters back to the RecyclerView
            reported.setLayoutParams(layoutParams);

            //////////////////////////////////////////
            btn1.setVisibility(View.INVISIBLE);
            for (int i = 0; i < Imagearray.size(); i++){
                String data = diseases.get(i).get(2);
                System.out.println("data"+data);
                Bitmap receivedBitmap = BitmapFactory.decodeByteArray(Imagearray.get(i), 0, Imagearray.get(i).length);
                reportGeneratorHelpers.add(new ReportGeneratorHelper(diseases.get(i).get(5)+"="+diseases.get(i).get(6).substring(0,diseases.get(i).get(6).length()-1),diseases.get(i).get(3)+"="+diseases.get(i).get(4).substring(0,diseases.get(i).get(6).length()-1),diseases.get(i).get(1)+"="+diseases.get(i).get(2).substring(0,diseases.get(i).get(6).length()-1),receivedBitmap));

            }
            adapter = new ReportGeneratorAdapter(reportGeneratorHelpers);
            reported.setAdapter(adapter);
        }


//        if(currentUser!= null){
//            btn1.setVisibility(View.GONE);
//        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userRef.child("farm").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String farms = (String) dataSnapshot.getValue();
                        String[] strArray = farms.split("@");

                        for (String item : strArray) {
                            String[] parts = item.split("_");
                            resultSelectFarm.put(parts[0],parts[1]);
                        }
                        mutableLiveData.setValue(resultSelectFarm);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error
                    }
                });

                if (!isDialogOpened) {
                    // Set up the observer to open the dialog when data changes
                    mutableLiveData.observe(Report.this, new Observer<HashMap<String, String>>() {
                        @Override
                        public void onChanged(HashMap<String, String> dataMap) {
                            // Create and show the dialog only if it's not already open
                            if (!isDialogOpened) {
                                // Now you can access the value of dataMap and pass it to your fragment
                                selectFarm selectFarmFragment = new selectFarm();
                                selectFarmFragment.dataMap = dataMap;
                                selectFarmFragment.userReportValue = result;
                                selectFarmFragment.bitmapArrayListHashMap = bitmapArrayListHashMap;
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                selectFarmFragment.show(fragmentManager, "selectFarmDialog");

                                isDialogOpened = true;
                            }
                        }
                    });
                    Toast.makeText(getBaseContext(),"Successfully added ",Toast.LENGTH_LONG).show();
                }



            }
        });

    }

    private ArrayList<ArrayList<String>> generateReportListValues(HashMap<Bitmap, HashMap<String, Float>> modelPredict,HashMap<String,Bitmap> report_data,ArrayList<String> imageTitle){
        ArrayList<ArrayList<String>> MainReportValuesList = new ArrayList<>();

        int counter = 1;
        for (Map.Entry<Bitmap, HashMap<String,Float>> entry : modelPredict.entrySet()) {
            Bitmap image = entry.getKey();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageData = baos.toByteArray();

            report_data.put("image"+counter+".PNG",image);
            imageTitle.add("image"+counter+".PNG");
            bitmapArrayListHashMap.put("image"+counter+".PNG",imageData);
            counter++;
        }
        for (int k = 0; k < imageTitle.size(); k++){
            ArrayList<String> eachReportValuesList = new ArrayList<>();
            Bitmap finding = report_data.get(imageTitle.get(k));
            HashMap<String,Float> hashMap = modelPredict.get(finding);
            eachReportValuesList.add(imageTitle.get(k));
            for (Map.Entry<String,Float> entryGet : hashMap.entrySet()){
                eachReportValuesList.add(entryGet.getKey());
                eachReportValuesList.add(entryGet.getValue().toString());
            }
            MainReportValuesList.add(eachReportValuesList);
            eachReportValuesList = new ArrayList<>();
        }

        return MainReportValuesList;

    }


    private Bitmap Convbitmap(Uri key) throws IOException {
        Bitmap result = MediaStore.Images.Media.getBitmap(this.getContentResolver(), key);
        return result;
    }
    private void reportgenerator(HashMap<Bitmap, HashMap<String, Float>> model_pridict) {
        int lengthList = 0;
        ArrayList<Integer> intArr = new ArrayList<Integer>();
        reported.setHasFixedSize(true);
        reported.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ArrayList<ReportGeneratorHelper> reportGeneratorHelpers = new ArrayList<>();
        for (Map.Entry<Bitmap, HashMap<String, Float>> set1 :model_pridict.entrySet()){
            for (Map.Entry<String, Float> set2 :set1.getValue().entrySet()){
                newArr.add(String.valueOf(set2));
            }
            for (int this_k = 1; this_k<=newArr.size();this_k++){
                if (this_k % 3 == 0){
                    if (!intArr.contains(this_k)){
                        intArr.add(this_k);
                    }
                }
            }
            reportGeneratorHelpers.add(new ReportGeneratorHelper(newArr.get(intArr.get(lengthList)-1),newArr.get(intArr.get(lengthList)-2),newArr.get(intArr.get(lengthList)-3),set1.getKey()));
            lengthList++;
        }

        adapter = new ReportGeneratorAdapter(reportGeneratorHelpers);
        reported.setAdapter(adapter);
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of isDialogOpened
        outState.putBoolean(KEY_DIALOG_STATE, isDialogOpened);
    }
}
