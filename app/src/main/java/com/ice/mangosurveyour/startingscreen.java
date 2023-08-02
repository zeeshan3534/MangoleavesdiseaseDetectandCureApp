
package com.ice.mangosurveyour;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.IOException;

public class startingscreen extends AppCompatActivity {
    AppCompatButton loginbtn;
    private MediaPlayer mediaPlayer;
    private TextureView textureView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startingscreen);

        textureView = findViewById(R.id.textureView);
        loginbtn = findViewById(R.id.go_to_login);
        // Set up the TextureView and start the video playback
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                Surface surface = new Surface(surfaceTexture);
                try {
                    // Set up the MediaPlayer with the video from raw resource
                    mediaPlayer = new MediaPlayer();
                    AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.a1);
                    if (afd != null) {
                        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        afd.close();
                        mediaPlayer.setSurface(surface);
                        mediaPlayer.prepare();
                        mediaPlayer.setLooping(true);
                        mediaPlayer.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
                // No action needed
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                // No action needed
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(startingscreen.this,login.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
