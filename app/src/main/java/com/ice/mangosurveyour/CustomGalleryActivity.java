package com.ice.mangosurveyour;



import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CustomGalleryActivity extends AppCompatActivity {

    ArrayList<String> f = new ArrayList<>();
    public ArrayList<Bitmap> bitmapList = new ArrayList<>();
    public ArrayList<Uri> uriList = new ArrayList<>();
    File[] listFile;
    private String folderName = "MyPhotoDir";

    ViewPager mViewPager;
    ArrayList<Bitmap> imagebitmap;
    ViewPagerAdapter mViewPagerAdapter;
//    RecyclerAdapter adapter;adapter
    AppCompatButton uploadtonext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);
        getFromSdcard();

//        Loading page

        VideoView videoView = findViewById(R.id.videoload);
        String path = "android.resource://"+ getPackageName()+"/"+R.raw.loading;
        Uri uri =Uri.parse(path);
        videoView.setVideoURI(uri);
        videoView.setMediaController(null);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // autoplay the video when it's ready
                videoView.start();
            }
        });

        // end


//        uploadtonext = findViewById(R.id.uploadtonext);
//        uploadtonext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(CustomGalleryActivity.this,DialogeBox.class);
//                intent.putExtra("camera","camera");
//                intent.putExtra("uriimage",uriList);
//                startActivity(intent);
//            }
//        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(CustomGalleryActivity.this, UploadImage.class);
                intent.putExtra("type","camera");
                intent.putExtra("uriimage",uriList);
                startActivity(intent);
            }
        }, 6000);
    }




    public void imageBitmap(int length){

        for(int i=0 ; i<length; i++){
            f.add(listFile[i].getAbsolutePath());



            // First decode the image dimensions to determine the size to which it should be scaled
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(listFile[i].getAbsolutePath(), options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;

            // Define maximum image width and height constants
            final int MAX_IMAGE_WIDTH = 800;
            final int MAX_IMAGE_HEIGHT = 800;

            // Calculate the scale factor based on the maximum image size
            int scale = Math.min(imageWidth/MAX_IMAGE_WIDTH, imageHeight/MAX_IMAGE_HEIGHT);

            // Set the options to decode the image with the desired scale
            options = new BitmapFactory.Options();
            options.inSampleSize = scale;
            Bitmap myBitmap = BitmapFactory.decodeFile(listFile[i].getAbsolutePath(), options);

            System.out.println(myBitmap);
            bitmapList.add(myBitmap);

        }

        convertBitmapsToUris(bitmapList);
    }


    public void convertBitmapsToUris(ArrayList<Bitmap> bitmapList) {

        for (int i = 0; i < bitmapList.size(); i++) {
            Bitmap bitmap = bitmapList.get(i);

            // Create a file in the cache directory with a unique name based on the current time
            File cacheDir = getCacheDir();
            File file = new File(cacheDir, "img_" + System.currentTimeMillis() + ".jpg");

            try {
                // Write the bitmap to the file
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();

                // Convert the file to a Uri
                Uri uri = Uri.fromFile(file);
                uriList.add(uri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Now you have all Uri objects in the uriList.
    }






    public void getFromSdcard(){
        File file = new File(getExternalFilesDir(folderName),"/");
        if(file.isDirectory()){
            listFile = file.listFiles();
            System.out.println(listFile.length);
            if (listFile.length > 10){
                imageBitmap(10);
            }else{
                imageBitmap(listFile.length);
            }


        }
    }
}
