package com.example.newcamera;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.FOREGROUND_SERVICE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.AlphabeticIndex;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
public class MainActivity extends AppCompatActivity {
    String mFilePath;
    private static int RESULT_LOAD_IMG = 1;
    String imgpath,storedpath;
    SharedPreferences sp;
    ImageView myImage;
    Bitmap stored = null;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,FOREGROUND_SERVICE,SYSTEM_ALERT_WINDOW,RECORD_AUDIO, CAMERA}, 1);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        myImage = (ImageView) findViewById(R.id.imgView);
        sp=getSharedPreferences("setback", MODE_PRIVATE);
        if(sp.contains("imagepath")) {
            storedpath=sp.getString("imagepath", "");
            myImage.setImageBitmap(BitmapFactory.decodeFile(storedpath));
            stored = BitmapFactory.decodeFile(storedpath);
        }
        /*
        findViewById(R.id.record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,value);
                startActivityForResult(intent,1);
            }
        })
        */
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendMessage(View view) {
        // Do something in response to button
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stored.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Intent in1 = new Intent(this, CameraActivity.class);
        in1.putExtra("image",byteArray);
        Intent intent = new Intent(this, CameraActivity.class);
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,FOREGROUND_SERVICE,SYSTEM_ALERT_WINDOW,RECORD_AUDIO, CAMERA}, 1);
        startActivity(intent);
    }
    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }
    public ImageView returnImage(){
        return myImage;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.MediaColumns.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgpath = cursor.getString(columnIndex);
                Log.d("path", imgpath);
                cursor.close();

                SharedPreferences.Editor edit=sp.edit();
                edit.putString("imagepath",imgpath);
                edit.commit();


                Bitmap myBitmap = BitmapFactory.decodeFile(imgpath);
                stored = myBitmap;
                myImage.setImageBitmap(myBitmap);
            }
            else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            Uri chosenImageUri = data.getData();

            Bitmap mBitmap = null;
            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageUri);
        }
    }
    */
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(resultCode == RESULT_OK && requestCode == 1){

        }
    }
    */

}