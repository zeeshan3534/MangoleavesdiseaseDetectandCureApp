package com.ice.mangoddetector;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;


public class Upload extends AppCompatActivity implements RecyclerAdapter.CountOfImagesWhenRemoved,RecyclerAdapter.itemClickListner{
    RecyclerView recyclerView;
    TextView textView;
    Button pick,predict;


    ArrayList<Uri> uri = new ArrayList<>();
    RecyclerAdapter adapter;

    private static final int Read_Permission = 101;
    private static final int PickImage = 1;

    ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);textView = findViewById((R.id.totalPhotos));
        recyclerView = findViewById(R.id.recyclerVeiw_Gallery_Images);
        pick = findViewById(R.id.pick);

        adapter = new RecyclerAdapter(uri,this,this);
        recyclerView.setLayoutManager(new GridLayoutManager(Upload.this,4));
        recyclerView.setAdapter(adapter);

        if(ContextCompat.checkSelfPermission(Upload.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(Upload.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},Read_Permission);

        }

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                predict = findViewById(R.id.button3);

                if(result.getResultCode() == Activity.RESULT_OK && null != result.getData()){
                    if(result.getData().getClipData() != null){
                        int x = result.getData().getClipData().getItemCount();
                        // For Multiple Images
                        for(int i=0;i<x;i++){
                            if (uri.size() <= 10) {

                                uri.add(result.getData().getClipData().getItemAt(i).getUri());
                            }else{
                                Toast.makeText(Upload.this,"You are not allowed to upload more than 10",Toast.LENGTH_SHORT);
                            }


                        }
                        adapter.notifyDataSetChanged();
                        textView.setText("Photos("+uri.size()+")");
                        // For Single Image
                    }else {
                        if (uri.size() <= 10) {
                            uri.add(result.getData().getData());
                        }else{
                            Toast.makeText(Upload.this,"You are not allowed to upload more than 10",Toast.LENGTH_SHORT);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    textView.setText("Photos("+uri.size()+")");
                } else{
                    Toast.makeText(Upload.this,"You haven't pick any Image",Toast.LENGTH_LONG).show();
                }
            }
        });

        ////////////////////

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }

                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PickImage);
                activityResultLauncher.launch(intent);
//                predict.setVisibility(View.VISIBLE);
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == PickImage && resultCode == Activity.RESULT_OK && null != data){
//            if(data.getClipData() != null){
//                int x = data.getClipData().getItemCount();
//                // For Multiple Images
//                for(int i=0;i<x;i++){
//                    if (uri.size() <= 10) {
//
//                            uri.add(data.getClipData().getItemAt(i).getUri());
//                    }else{
//                        Toast.makeText(MainActivity.this,"You are not allowed to upload more than 10",Toast.LENGTH_SHORT);
//                    }
//
//
//                }
//                adapter.notifyDataSetChanged();
//                textView.setText("Photos("+uri.size()+")");
//                // For Single Image
//            }else {
//                if (uri.size() <= 10) {
//                    uri.add(data.getData());
//                }else{
//                    Toast.makeText(MainActivity.this,"You are not allowed to upload more than 10",Toast.LENGTH_SHORT);
//                }
//            }
//            adapter.notifyDataSetChanged();
//            textView.setText("Photos("+uri.size()+")");
//        } else{
//            Toast.makeText(this,"You haven't pick any Image",Toast.LENGTH_LONG).show();
//        }
//
//    }

    @Override
    public void clicked(int getsize) {
        textView.setText("Photos("+uri.size()+")");
    }

    @Override
    public void itemClick(int position) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.customlayoutzoom);

        TextView textView = dialog.findViewById(R.id.text_dialog);
        ImageView imageView  = dialog.findViewById(R.id.image_view_dialog);
        Button buttonClose = dialog.findViewById(R.id.btl_close_dialog);

        textView.setText("Image "+position );
        imageView.setImageURI(uri.get(position));
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}

