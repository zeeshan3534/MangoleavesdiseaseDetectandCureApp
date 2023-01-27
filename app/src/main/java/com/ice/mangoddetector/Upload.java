package com.ice.mangoddetector;


import androidx.activity.result.ActivityResultLauncher;
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
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ice.mangoddetector.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Upload extends AppCompatActivity implements RecyclerAdapter.CountOfImagesWhenRemoved,RecyclerAdapter.itemClickListner{
    RecyclerView recyclerView;
    TextView textView,text2;
    Button pick,predict,ask;



    List<String> DiseaseArray = Arrays.asList("Anthracnose","Apoderus Javanicus","Bacterial Canker","Dappula Tertia","Dialeuropora Decempuncta","Gall Midge","Black Soothy Mold","Icerya Seychellarum","Mictis Longicornis","Neomelicharia Sparsa");
    ArrayList<Float> Final = new ArrayList<>();
    ArrayList<Bitmap> bitmapArray2 =  new ArrayList<>();
    HashMap<String, Float> Report_list = new HashMap<>();
    HashMap<Bitmap, HashMap<String, Float>> Report_final = new HashMap<>();
    ArrayList<Uri> uri = new ArrayList<>();
    ArrayList<Bitmap> bitmapArray = new ArrayList<>();
    ArrayList<ArrayList<Float>> floatarray = new ArrayList<ArrayList<Float>>();
    RecyclerAdapter adapter;
    HashMap<Uri, ArrayList<Float>> Float_Report_list = new HashMap<Uri, ArrayList<Float>>();


    private static final int Read_Permission = 101;
    private static final int PickImage = 1;

    ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload);textView = findViewById((R.id.totalPhotos));
        recyclerView = findViewById(R.id.recyclerVeiw_Gallery_Images);
        pick = findViewById(R.id.pick);
        predict = findViewById(R.id.button3);
        ask = findViewById(R.id.button6);
        text2  = findViewById(R.id.totalPhotos1);

        String text = getIntent().getStringExtra("specie");
        if(text!=null){
            pick.setVisibility(View.VISIBLE);
            ask.setVisibility(View.INVISIBLE);
            text2.setText(text);
        }
        else{
            pick.setVisibility(View.INVISIBLE);
            ask.setVisibility(View.VISIBLE);
        }
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Upload.this,DialogeBox.class);
                startActivity(i);
            }
        });
        adapter = new RecyclerAdapter(uri,this,this);
        recyclerView.setLayoutManager(new GridLayoutManager(Upload.this,4));
        recyclerView.setAdapter(adapter);

        if(ContextCompat.checkSelfPermission(Upload.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(Upload.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},Read_Permission);

        }



        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }

                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PickImage);
//                activityResultLauncher.launch(intent);
                predict.setVisibility(View.VISIBLE);
            }
        });


        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(int k = 0; k<uri.size();k++){

                    ConvBitmap(uri.get(k));
                }

                for(int i=0;i<bitmapArray2.size();i++){
                        Bitmap bitmap =bitmapArray2.get(i);
                        bitmap = Bitmap.createScaledBitmap(bitmap,256,256,true);
                        try {
                            Model model = Model.newInstance(getApplicationContext());

                            // Creates inputs for reference.
                            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);

                            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                            tensorImage.load(bitmap);
                            ByteBuffer byteBuffer = tensorImage.getBuffer();

                            inputFeature0.loadBuffer(byteBuffer);

                            // Runs model inference and gets result.
                            Model.Outputs outputs = model.process(inputFeature0);
                            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                            ArrayList<Float> imgList = new ArrayList();
                            for (int j=0 ; j < outputFeature0.getFloatArray().length; j++ ){
                                imgList.add(outputFeature0.getFloatArray()[j]);
                            }
                            floatarray.add(imgList);

//                            System.out.println("added");
                            // Releases model resources if no longer used.
                            model.close();
                        } catch (IOException e) {
                            // TODO Handle the exception
                        }

                    }

                for(int float_len=0;float_len<floatarray.size();float_len++){
//                    icon = BitmapFactory.de(bitmapArray2.get(float_len));
                    Float_Report_list.put(uri.get(float_len),floatarray.get(float_len));
                }




//                System.out.println(Float_Report_list);
                Intent intentreport = new Intent(Upload.this,Report.class);
                intentreport.putExtra("hashMap",Float_Report_list);
                startActivity(intentreport);

                bitmapArray2.clear();
                floatarray.clear();
                Report_final.clear();
                Float_Report_list.clear();


            }


        });
    }





        private ArrayList<Bitmap> ConvBitmap(Uri uri) {



        try {
            Bitmap result = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            bitmapArray2.add(result);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return bitmapArray2;
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PickImage && resultCode == Activity.RESULT_OK && null != data){
            if(data.getClipData() != null){
                int x = data.getClipData().getItemCount();
                // For Multiple Images
                for(int i=0;i<x;i++){
                    if (uri.size() <= 10) {
                        if (uri.contains(data.getData())){
                            Toast.makeText(Upload.this,"You Are Not Allowed To uplaod same image",Toast.LENGTH_SHORT).show();
                        }else {
                            uri.add(data.getClipData().getItemAt(i).getUri());
                            Uri value = data.getClipData().getItemAt(i).getUri();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), value);
                                bitmapArray.add(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }else{

                        Toast.makeText(Upload.this,"You are not allowed to upload more than 10",Toast.LENGTH_SHORT);
                    }


                }
                adapter.notifyDataSetChanged();
                textView.setText("Photos("+uri.size()+")");

                // For Single Image
            }else {
                if (uri.size() <= 10) {
                    Uri value;
                    if (uri.contains(data.getData())){
                        Toast.makeText(Upload.this,"You Are Not Allowed To uplaod same image",Toast.LENGTH_SHORT).show();
                    }else{
                        uri.add(data.getData());
                        value = data.getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),value);
                            bitmapArray.add(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }



                }else{
                    Toast.makeText(Upload.this,"You are not allowed to upload more than 10",Toast.LENGTH_SHORT);
                }
            }
            adapter.notifyDataSetChanged();
            textView.setText("Photos("+uri.size()+")");
        } else{
            Toast.makeText(this,"You haven't pick any Image",Toast.LENGTH_LONG).show();
        }

    }

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



