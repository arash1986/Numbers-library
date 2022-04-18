package com.arashsalar.librarytest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.arashsalar.cameragallery.CameraImagePick.CameraGalleryImagePick;
import com.arashsalar.cameragallery.CameraImagePick.ImagePickedStatus;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button gal, cam;
        ImageView imageView;
        imageView = findViewById(R.id.image);
        gal = findViewById(R.id.gal);
        cam = findViewById(R.id.cam);

        CameraGalleryImagePick cameraGalleryImagePick = new CameraGalleryImagePick(this, imageView.getId());

        gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraGalleryImagePick.openSomeActivityForResult(ImagePickedStatus.Gallery);
            }
        });

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraGalleryImagePick.openSomeActivityForResult(ImagePickedStatus.Camera);
            }
        });
    }
}