package com.ice.mangoddetector;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;

public class CustomGalleryActivity extends AppCompatActivity {

    ArrayList<String> f = new ArrayList<>();
    File[] listFile;
    private String folderName = "MyPhotoDir";

    ViewPager mViewPager;

    ViewPagerAdapter mViewPagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);
        getFromSdcard();
        mViewPager = findViewById(R.id.viewPagerMain);
        mViewPagerAdapter = new ViewPagerAdapter(this, f);
        mViewPager.setAdapter(mViewPagerAdapter);



    }

    public void getFromSdcard(){
        File file = new File(getExternalFilesDir(folderName),"/");
        if(file.isDirectory()){
            listFile = file.listFiles();
            for(int i=0 ; i<listFile.length; i++){
                f.add(listFile[i].getAbsolutePath());
            }
        }
    }
}
