package com.ice.mangosurveyour;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.content.Context;
import android.hardware.camera2.CameraManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class Camera_MainActivity extends Fragment {


    private static final String TAG = "AndroidCameraApi";
    private Button btnTake;
    private Button btnGallery;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private Activity mActivity;
    static {
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);

    }
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private File folder;
    private String folderName = "MyPhotoDir";
    private static final int REQUEST_CAMERA_PERMISSION =200;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

//    Gallery Work

    public ArrayList<Bitmap> bitmapList = new ArrayList<>();
    public ArrayList<Uri> uriList = new ArrayList<>();
    File[] listFile;

//


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_camera_main, container, false);
        mActivity = getActivity();
        textureView = root.findViewById(R.id.texture);
        if(textureView != null){
            textureView.setSurfaceTextureListener(textureListener);
            btnTake = root.findViewById(R.id.btnTake);
            btnGallery = root.findViewById(R.id.btnGallary);
            if(btnTake != null){
                btnTake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePicture();

                    }
                });

            }
            if(btnGallery != null){
                btnGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity,CustomGalleryActivity.class);
                        startActivity(intent);

                    }
                });
            }
        }


        return root;
    }



    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.e(TAG,"onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }




        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };


    protected void startBackgroundThread(){
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread(){
        mBackgroundThread.quitSafely();
        try{
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    protected void takePicture(){
        if (cameraDevice == null){
            Log.e(TAG,"camera device is null");
            return;
        }
        if(!isExternalStorageAvailableForRW() || isExternalStorageReadOnly()){
            btnTake.setEnabled(false);
        }
        if(isStoragePermissionGranted()){
            Context context = getContext();
            if (context != null) {
                CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

                try{
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());

                    Size[] jpegSizes = null;
                    if (characteristics != null) {
                        jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                    }
                    int width = 640;
                    int height = 480;
                    if (jpegSizes != null && jpegSizes.length>0){
                        width = jpegSizes[0].getWidth();
                        height= jpegSizes[0].getHeight();

                    }
                    ImageReader reader = ImageReader.newInstance(width,height,ImageFormat.JPEG,1);
                    List<Surface> outputSurface = new ArrayList<>(2);//-----------------------------------------------------
                    outputSurface.add(reader.getSurface());
                    outputSurface.add(new Surface(textureView.getSurfaceTexture(

                    )));
                    final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                    captureBuilder.addTarget(reader.getSurface());
                    captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                    Activity activity = mActivity;
                    if (activity != null) {
                        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
                    }
                    else {
                        System.out.println("activity is null");
                    }
                    file = null;
                    folder = new File(folderName);
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "IMG" + timestamp + ".jpg";
                    file = new File(getContext().getExternalFilesDir(folderName),"/"+imageFileName);
                    if (!folder.exists()){
                        folder.mkdirs();
                    }
                    ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                        @Override
                        public void onImageAvailable(ImageReader reader) {
                            Image image = null;
                            try {
                                image = reader.acquireLatestImage();
                                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                                byte[] bytes = new byte[buffer.capacity()];
                                buffer.get(bytes);
                                save(bytes);
                            }catch (FileNotFoundException e){
                                e.printStackTrace();
                            }catch (IOException e){
                                e.printStackTrace();
                            }finally {
                                if (image != null){
                                    image.close();
                                }
                            }
                        }
                        private void save(byte[] bytes) throws IOException{
                            OutputStream output = null;
                            try {
                                output = new FileOutputStream(file);
                                output.write(bytes);
                            }finally {
                                if (null != output){
                                    output.close();
                                }
                            }
                        }
                    };
                    reader.setOnImageAvailableListener(readerListener,mBackgroundHandler);
                    final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                            super.onCaptureCompleted(session, request, result);
                            Toast.makeText(mActivity, "Saved:" + file, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "" + file);
                            createCameraPreview();
                        }
                    };
                    cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            try{
                                session.capture(captureBuilder.build(),captureListener,mBackgroundHandler);
                            }catch (CameraAccessException e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                        }
                    },mBackgroundHandler);
                }catch (CameraAccessException e){
                    e.printStackTrace();
                }
            }}
    }
    private static boolean isExternalStorageReadOnly(){
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)){
            return true;
        }
        return false;
    }
    private static boolean isExternalStorageAvailableForRW(){
        String extStorageState = Environment.getExternalStorageState();
        if(extStorageState.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }
    private boolean isStoragePermissionGranted(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int permissionCheck = getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                return true;
            }else{

                if (mActivity != null) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return false;
                }
            }
        }else {
            return false;
        }

        return false;
    }
    protected void createCameraPreview(){
        try{
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if(null == cameraDevice){
                        return;
                    }
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(mActivity,"Configuration Change",Toast.LENGTH_SHORT).show();
                }
            },null);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    private void openCamera(){
        Context context = getContext();
        if (context != null) {
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
//
//            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

            Log.e(TAG,"is Camera open");
            try {
                cameraId = manager.getCameraIdList()[0];
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                assert map != null;
                imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

                if (ActivityCompat.checkSelfPermission(mActivity,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA_PERMISSION);
                    return;
                }
                manager.openCamera(cameraId,stateCallback,null);
            }catch (CameraAccessException e){
                e.printStackTrace();
            }
            Log.e(TAG,"openCamera X");
        }
    }
    protected void updatePreview(){
        if (null == cameraDevice){
            Log.e(TAG,"update preview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CameraMetadata.CONTROL_MODE_AUTO);
        try{
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION){
            if(grantResults[0]==PackageManager.PERMISSION_DENIED){
                Toast.makeText(mActivity,"you cant use this app closing",Toast.LENGTH_LONG).show();
                mActivity.finish();
            }
        }


    }

    @Override
    public void onResume(){
        super.onResume();
        Log.e(TAG,"onResume");
        startBackgroundThread();
        if (textureView != null && textureView.isAvailable()){
            openCamera();
        }else if (textureView != null) {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    public void onPause() {

        Log.e(TAG, "onPause");
        stopBackgroundThread();
        super.onPause();

    }
}