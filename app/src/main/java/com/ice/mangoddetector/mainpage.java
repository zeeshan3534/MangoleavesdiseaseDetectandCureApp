package com.ice.mangoddetector;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class mainpage extends AppCompatActivity {


    private final int Camera_code = 101;
    RecyclerView featuredRecycler;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        String check = (String) intent.getExtras().getString("check");
        System.out.println(check+"dfdf");

        if (Objects.equals(check, "offline")){
            System.out.println(check+"fhdf");
            Toast.makeText(this,"In this mode You cannot STORE your report",Toast.LENGTH_LONG).show();
        }


        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        setContentView(R.layout.activity_mainpage);
        TextView internet = findViewById(R.id.internet);

        if (connected){
            internet.setText("Internet Connect");
        }
        else{
            internet.setText("Internet not Connect ");
        }

        //hook
        featuredRecycler = findViewById(R.id.features);
        featuredRecycler();

        LinearLayout btn = findViewById(R.id.materialButton);
        LinearLayout btn1 = findViewById(R.id.materialButton1);

        LinearLayout btn3  = findViewById(R.id.button);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallary = new Intent(mainpage.this,Upload.class);
                startActivity(gallary);

            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera_intent = new Intent(mainpage.this,Camera_MainActivity.class);
                startActivity(camera_intent);

            }
        }
        );
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diagnosespage();
            }
        });
    }

    private void featuredRecycler() {
        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));


        ArrayList<featureHelper> featureHelpers = new ArrayList<>();

        featureHelpers.add(new featureHelper(R.drawable.anthracnose,"Anthracnose","Mango Anthracnose is a fungal infection caused by the fungus Colletotrichum gloeosporioides and is presently recognized as the most destructive field and post-harvest disease of mango worldwide."));
        featureHelpers.add(new featureHelper(R.drawable.apoderus_javanicus,"Apoderus Javanicus","The Apoderus Javanicus, also known as the mango seed beetle, can cause damage to mango trees by feeding on the fruit and seeds of the tree, which can lead to reduced yield and fruit quality."));
        featureHelpers.add(new featureHelper(R.drawable.bacterial_canker,"Bacterial Canker","Bacterial canker in mango occurs due to Bacterium which causes Angular, water-soaked spots on leaves which coalesce and turn black"));
        featureHelpers.add(new featureHelper(R.drawable.dappula_tertia,"Dappula Tertia","Dappula is a genus of beetles. If a mango tree is infested by Dappula Tertia, the larvae of the beetles will bore into the wood of the tree, causing damage to the tree's stem and branches."));
        featureHelpers.add(new featureHelper(R.drawable.dialeuropora_decempuncta,"Dialeuropora Decempuncta","Dialeurodes is a genus of whitefly. They feed on the sap of plants and can cause yellowing and wilting of the leaves, as well as the production of honeydew which can lead to the growth of sooty mold."));
        featureHelpers.add(new featureHelper(R.drawable.gall_midge,"Gall Midge","The larvae of the gall midge feed on the shoots and fruit of the tree, causing the formation of galls or swellings on the tree's branches and fruit. This can lead to reduced yield and fruit quality, and in severe cases.    "));
        featureHelpers.add(new featureHelper(R.drawable.black_soothy_mold,"Black Soothy Mold","Mango sooty mold (Meliola mangiferae) is one of the species of fungi that grow on honeydew results from interactions among sap-feeding insects such as soft scale (wax, green and cottony cushion scales), mealybugs, aphids,"));
        featureHelpers.add(new featureHelper(R.drawable.icerya_seychellarum,"Icerya Seychellarum","Icerya Seychellarum is a polyphagous phloem-feeding coccid. This insect feeds on the underside of leaves."));
        featureHelpers.add(new featureHelper(R.drawable.mictis_longicornis,"Mictis Longicornis","Mictis longicornis, also known as the mango stem borer, is a type of insect that infests mango trees and can cause significant damage to the tree's stem and branches. "));
        featureHelpers.add(new featureHelper(R.drawable.neomelicharia_sparsa,"Neomelicharia Sparsa","Neomelicharia sparsa is a fungal disease that affects mango trees. The symptoms include small, dark spots on the leaves and fruit, as well as premature defoliation."));

        adapter = new FeatureAdapter(featureHelpers);
        featuredRecycler.setAdapter(adapter);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){

            if (requestCode==Camera_code){
                //yahan camera view
            }
        }
    }


    private void newpage(){
        Intent intent = new Intent(this,AboutDiseases.class);
        startActivity(intent);
    }
    private void diagnosespage(){
        Intent intent = new Intent(this,Diagnose.class);
        startActivity(intent);
    }

}