package com.ice.mangoddetector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    RecyclerView reported;
    RecyclerView.Adapter adapter;
    ArrayList<String> newArr= new ArrayList<String>();
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        HashMap<Bitmap,HashMap<String,Float>> newhash = new HashMap<>();

        List<String> DiseaseArray = Arrays.asList("Anthracnose","Apoderus Javanicus","Bacterial Canker","Dappula Tertia","Dialeuropora Decempuncta","Gall Midge","Black Soothy Mold","Icerya Seychellarum","Mictis Longicornis","Neomelicharia Sparsa");

//        TextView random = findViewById(R.id.textView2);
        reported = findViewById(R.id.recylerview);
        Intent intent = getIntent();

        HashMap<Uri, ArrayList<Float>> Report_array = (HashMap<Uri, ArrayList<Float>>) intent.getSerializableExtra("hashMap");
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

            System.out.println(set.getValue()+"set.getValue()");
            System.out.println(Report_list+"Report List");


        }
        System.out.println(newhash);
        reportgenerator(newhash);



    }

    private Bitmap Convbitmap(Uri key) throws IOException {
        Bitmap result = MediaStore.Images.Media.getBitmap(this.getContentResolver(), key);
        return result;
    };
    private void reportgenerator(HashMap<Bitmap, HashMap<String, Float>> model_pridict) {
        int lengthList = 0;
        ArrayList<Integer> intArr = new ArrayList<Integer>();
        reported.setHasFixedSize(true);
        reported.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ArrayList<ReportGeneratorHelper> reportGeneratorHelpers = new ArrayList<>();

        System.out.println();
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

}
