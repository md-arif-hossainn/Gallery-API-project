package com.arif.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FullImage extends AppCompatActivity {
    private static int REQUEST_CODE=100;
    private ImageView imageView;
    OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        imageView = findViewById(R.id.myZoomageView);
        Button saveBtn = findViewById(R.id.save_btn);
        Button shareBtn = findViewById(R.id.share_btn);

        Glide.with(this)
                .load(getIntent().getStringExtra("image"))
                .into(imageView);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(FullImage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    saveImage();
                }else{
                    askPermission();
                }
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });
    }
    private void saveImage() {
        File dir = new File(Environment.getExternalStorageDirectory(),"SaveImage");
        if (!dir.exists()){
            dir.mkdir();
        }
       BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        File file = new File(dir,System.currentTimeMillis()+".jpg");
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        Toast.makeText(this, "Successfully Saved", Toast.LENGTH_SHORT).show();
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(FullImage.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                saveImage();
            }else {
                Toast.makeText(this, "please provide the required permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void share() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        File file = new File(getExternalCacheDir()+"/"+getResources().getString(R.string.app_name)+".png");
        Intent share_intent;
        try{
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
            outputStream.close();
            share_intent = new Intent(Intent.ACTION_SEND);
            share_intent.setType("image/*");
            share_intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            share_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        startActivity(Intent.createChooser(share_intent,"share image"));

    }
}