package com.arashsalar.cameragallery.CameraImagePick;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.arashsalar.cameragallery.BuildConfig;
import com.arashsalar.cameragallery.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCaller;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

public class CameraGalleryImagePick extends AppCompatActivity {

    private final Activity activity;
    private Bitmap selectedImage;
    private ImageView imageView = null;
    public final static int PERMISSION_CAMERA_REQUEST = 52;
    public final static int PERMISSION_STORAGE_REQUEST = 53;
    public static String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public final static String ImagePath = Environment.getExternalStorageDirectory() + File.separator + ".welfareassistantyariresan" + File.separator;
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher;
    File file;
    public CameraGalleryImagePick(Activity activity, int viewId)
    {
        file = new File(ImagePath);
        this.activity = activity;
        if(viewId != 0)
            imageView = activity.findViewById(viewId);
        activityLauncher = BetterActivityResult.registerActivityForResult((ActivityResultCaller) activity);
    }
    public Bitmap openSomeActivityForResult(ImagePickedStatus mode) {
        selectedImage = null;

        Intent intent;
        if (!file.exists()) {
            Log.d("isExist", file.mkdirs()+", " + ImagePath);
        }

        switch (mode)
        {
            case Gallery:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!checkStoragePermission()) {
                        alertDialog("storage", PERMISSION_STORAGE_REQUEST);
                        Log.d("isExist1", file.mkdirs()+", " + ImagePath);
                        return null;
                    }
                }

                intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                break;

            case Camera:
            default:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!checkStoragePermission()) {
                        alertDialog("storage", PERMISSION_STORAGE_REQUEST);
                        return null;
                    }
                    else if (!checkCameraPermission()) {
                        alertDialog("camera", PERMISSION_CAMERA_REQUEST);
                        return null;
                    }
                }

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                break;

        }
        String rowPhoto = ImagePath + "picture.jpg";
        int compressLevel = 40;
        activityLauncher.launch(intent, result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {

                switch (mode) {

                    case Gallery: {
                            final Uri imageUri = intent.getData();
                            final InputStream imageStream1;
                            try {
                                imageStream1 = activity.getContentResolver().openInputStream(imageUri);
                                selectedImage = BitmapFactory.decodeStream(imageStream1);
                                if (selectedImage == null) {
                                    Toast.makeText(activity, "خطا در دریافت عکس!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                long time = System.currentTimeMillis();
                                File f = new File(file, time + ".jpg");
                                FileOutputStream out = new FileOutputStream(f);


                                /* rotating the photo*/
                                Matrix matrix = new Matrix();
                                matrix.postRotate(0);

                                int targetWidth = 1200;
                                int targetHeight = (int) (selectedImage.getHeight() * targetWidth / (double) selectedImage.getWidth());
                                selectedImage = Bitmap.createScaledBitmap(selectedImage, targetWidth, targetHeight, false);

                                /* compress the photo and save it as new*/
                                selectedImage.compress(Bitmap.CompressFormat.JPEG, compressLevel, out);
                                if(imageView != null)
                                    imageView.setImageBitmap(selectedImage);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case Camera: {
                            try {
                                /* get the image*/
                                BitmapFactory.Options options;
                                options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                Bitmap photo = BitmapFactory.decodeFile(rowPhoto, options);

                                if (photo == null) {
                                    Toast.makeText(activity, "خطا در دریافت عکس!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                /* name the photo*/
                                long time = System.currentTimeMillis();
                                File f = new File(file, time + ".jpg");
                                FileOutputStream out = new FileOutputStream(f);

                                /* rotating the photo*/
                                Matrix matrix = new Matrix();
                                matrix.postRotate(90);

                                int targetWidth = 1200;
                                int targetHeight = (int) (photo.getHeight() * targetWidth / (double) photo.getWidth());
                                photo = Bitmap.createScaledBitmap(photo, targetWidth, targetHeight, false);
                                photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
                                photo.compress(Bitmap.CompressFormat.JPEG, compressLevel, out);
                                if(imageView != null)
                                    imageView.setImageBitmap(photo);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        break;
                    }
                }

            }
        });
        return selectedImage;
    }

    public Uri setImageUri() {
        File file = new File(ImagePath + "picture.jpg");
        Uri uri = Uri.fromFile(file);
        Uri imgUri = FileProvider.getUriForFile(activity, BuildConfig.LIBRARY_PACKAGE_NAME + ".provider", file);
        if (Build.VERSION.SDK_INT >= 24)
            return imgUri;
        else
            return uri;
    }

    private boolean checkStoragePermission() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private boolean checkCameraPermission() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void alertDialog(final String flag, final int numberRequestCode) {
        final Dialog dialog = new Dialog(activity, R.style.AnimatedDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_permission);
        //dialog.setCancelable(false);
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.90);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        ////////////////////////////////
        TextView txtTitle = dialog.findViewById(R.id.dialogPermission_txtTitle);
        TextView txtComment = dialog.findViewById(R.id.dialogPermission_txtComment);
        Button btnOk = dialog.findViewById(R.id.dialogPermission_btnok);
        Button btnCancel = dialog.findViewById(R.id.dialogPermission_btnCancel);
        ////////////////////////////////
        txtTitle.setText("پیغام سیستم");

        if (flag.equals("permission"))
            txtComment.setText("برای ادامه برنامه ، نیاز به دسترسی به مکان نما می باشد.");
        else if (flag.equals("gps"))
            txtComment.setText("لطفا مکان نما خود را روشن کنید .");
        else if (flag.equals("storage"))
            txtComment.setText("برای ادامه برنامه نیاز به دسترسی به حافظه داخلی می باشد.");
        else if (flag.equals("camera"))
            txtComment.setText("برای ادامه برنامه نیاز به دسترسی به دوربین می باشد.");

        /* button ok */
        btnOk.setOnClickListener(view -> {
           if (flag.equals("storage")) {
                checkLocationPermission(PERMISSION_STORAGE, numberRequestCode);
                dialog.dismiss();
            } else if (flag.equals("camera")) {
                checkLocationPermission(PERMISSION_CAMERA, numberRequestCode);
                dialog.dismiss();
            }
        });

        /* button cancel */
        btnCancel.setOnClickListener(view -> dialog.dismiss());
        ////////////////////////////////
        dialog.show();
    }

    public void checkLocationPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            else
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

}
