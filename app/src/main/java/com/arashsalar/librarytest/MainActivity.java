package com.arashsalar.librarytest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.arashsalar.cameragallery.CameraImagePick.CameraGalleryImagePick;
import com.arashsalar.cameragallery.CameraImagePick.ImagePickedStatus;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CameraGalleryImagePick.GetList, CameraGalleryImagePick.GetClick {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button gal, cam;
        ImageView imageView;
        RecyclerView recyclerView;
        imageView = findViewById(R.id.image);
        recyclerView = findViewById(R.id.recycler);
        gal = findViewById(R.id.gal);
        cam = findViewById(R.id.cam);


        CameraGalleryImagePick cameraGalleryImagePick = new CameraGalleryImagePick(this, imageView.getId(), recyclerView.getId());
        cameraGalleryImagePick.ClickListener(this);
        cameraGalleryImagePick.ListListener(this);
        gal.setOnClickListener(v -> cameraGalleryImagePick.openSomeActivityForResult(ImagePickedStatus.Gallery));

        cam.setOnClickListener(v -> cameraGalleryImagePick.openSomeActivityForResult(ImagePickedStatus.Camera));
    }

    @Override
    public void getList(ArrayList<Bitmap> list) {
        Log.d("getlist", list.size()+"");
    }

    @Override
    public void getClick(View view, int position) {
        Log.d("getClick", position+"");
    }
}