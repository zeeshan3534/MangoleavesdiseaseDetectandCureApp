package com.ice.mangosurveyour;



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
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.ice.mangosurveyour.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
//import org.tensorflow.lite.support.model.Model;

import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class UploadImage extends AppCompatActivity implements RecyclerAdapter.CountOfImagesWhenRemoved,RecyclerAdapter.itemClickListner{
    RecyclerView recyclerView;
    TextView textView,text2,usernametext;
    Button pick,predict,ask;
    ImageView userimage;
    private AvLoadingDialog avLoadingDialog;



    List<String> DiseaseArray = Arrays.asList("Anthracnose","Die Black","Gall Midge","Healthy","Powdery Mildew","Sooty Mould");
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

        setContentView(R.layout.activity_upload_image);textView = findViewById((R.id.totalPhotos));
        recyclerView = findViewById(R.id.recyclerVeiw_Gallery_Images);
        pick = findViewById(R.id.pick);
        predict = findViewById(R.id.button3);
        avLoadingDialog = new AvLoadingDialog(this);
//        ask = findViewById(R.id.button6);
//        text2  = findViewById(R.id.totalPhotos1);
        usernametext = findViewById(R.id.username1);
        userimage = findViewById(R.id.internet);

        Intent intent = getIntent();



//        Camera Work
        String type = (String) intent.getExtras().getString("type");
        ArrayList<Uri> uriList = intent.getParcelableArrayListExtra("uriimage");
        String text = getIntent().getStringExtra("specie");
        if(type.equals("camera")){

            uri = uriList;
            pick.setVisibility(View.INVISIBLE);
            predict.setVisibility(View.VISIBLE);

        }
        else{


        }

        adapter = new RecyclerAdapter(uri,this,this);
        recyclerView.setLayoutManager(new GridLayoutManager(UploadImage.this,4));
        recyclerView.setAdapter(adapter);

//        String username = (String) intent.getExtras().getString("username");
//        if (username!=null){
//            usernametext.setText(username);
//        }else{
//            usernametext.setText("Offline Mode");
//            userimage.setVisibility(View.INVISIBLE);
//
//        }





        if(ContextCompat.checkSelfPermission(UploadImage.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(UploadImage.this,
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
                avLoadingDialog.show();

                for(int k = 0; k<uri.size();k++){

                    ConvBitmap(uri.get(k));
                }

                for(int i=0;i<bitmapArray2.size();i++){
                    Bitmap bitmap =bitmapArray2.get(i);
                    bitmap = Bitmap.createScaledBitmap(bitmap,254,254,true);
                    try {
//                        Model model;
//                        model = Model.newInstance(getApplicationContext());
//
//                        // Creates inputs for reference.
//                        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
//
//                        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
//                        tensorImage.load(bitmap);
//                        ByteBuffer byteBuffer = tensorImage.getBuffer();
//
//                        inputFeature0.loadBuffer(byteBuffer);
//
//                        // Runs model inference and gets result.
//                        Model.Outputs outputs = model.process(inputFeature0);
//
//                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//                        ArrayList<Float> imgList = new ArrayList();
//                        for (int j=0 ; j < outputFeature0.getFloatArray().length; j++ ){
//                            imgList.add(outputFeature0.getFloatArray()[j]);
//                        }
//                        floatarray.add(imgList);
//                        System.out.println(imgList);
//
////                            System.out.println("added");
//                        // Releases model resources if no longer used.
//                        model.close();
                        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 1 * 224 * 224 * 3); // Assuming FLOAT32 data type and image size 224x224
                        byteBuffer.order(ByteOrder.nativeOrder());
                        byteBuffer.rewind();
                        for (int y = 0; y < 224; y++) {
                            for (int x = 0; x < 224; x++) {
                                int pixelValue = bitmap.getPixel(x, y);

                                // Assuming your model expects RGB input with values in the range [0, 1]
                                float r = Color.red(pixelValue) / 255.0f;
                                float g = Color.green(pixelValue) / 255.0f;
                                float b = Color.blue(pixelValue) / 255.0f;

                                byteBuffer.putFloat(r);
                                byteBuffer.putFloat(g);
                                byteBuffer.putFloat(b);
                            }
                        }

                        // Set the input tensor to the interpreter
                        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
                        inputFeature0.loadBuffer(byteBuffer);

                        // Runs model inference and gets result.
                        Model model = Model.newInstance(getApplicationContext());
                        Model.Outputs outputs = model.process(inputFeature0);

                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                        ArrayList<Float> imgList = new ArrayList<>();
                        for (int j = 0; j < outputFeature0.getFloatArray().length; j++) {
                            imgList.add(outputFeature0.getFloatArray()[j]);
                        }
                        floatarray.add(imgList);
                        System.out.println(imgList);

                        // Releases model resources if no longer used.
                        model.close();
                    } catch (IOException e) {

                    }

                }

                for(int float_len=0;float_len<floatarray.size();float_len++){
//                    icon = BitmapFactory.de(bitmapArray2.get(float_len));
                    Float_Report_list.put(uri.get(float_len),floatarray.get(float_len));
                }




//                System.out.println(Float_Report_list);
                Intent intentreport = new Intent(UploadImage.this,Report.class);
                intentreport.putExtra("direct","yes");
                intentreport.putExtra("hashMap",Float_Report_list);
                startActivity(intentreport);

                bitmapArray2.clear();
                floatarray.clear();
                Report_final.clear();
                Float_Report_list.clear();

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
                            Toast.makeText(UploadImage.this,"You Are Not Allowed To uplaod same image",Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(UploadImage.this,"You are not allowed to upload more than 10",Toast.LENGTH_SHORT);
                    }


                }
                adapter.notifyDataSetChanged();
                textView.setText("Photos("+uri.size()+")");

                // For Single Image
            }else {
                if (uri.size() <= 10) {
                    Uri value;
                    if (uri.contains(data.getData())){
                        Toast.makeText(UploadImage.this,"You Are Not Allowed To uplaod same image",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(UploadImage.this,"You are not allowed to upload more than 10",Toast.LENGTH_SHORT);
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



