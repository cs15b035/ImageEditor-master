package com.example.ohmprakashpagolu.test1.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.ohmprakashpagolu.test1.R;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.ohmprakashpagolu.test1.R.id.imgView;

public class MainActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    private static int PIC_CROP = 2;
    public static int REQUEST_TAKE_IMAGE = 3;
    public static Uri selectedImage;
    public static Bitmap cbmap;
    public static Image img;
    public static int ct = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button buttonTakeImage = (Button) findViewById(R.id.buttonTakeImage);
        buttonTakeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_TAKE_IMAGE);

            }
        });


        Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadImage);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        Button buttonSaveImage = (Button) findViewById(R.id.buttonSaveImage);
        buttonSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = img.getBitmap();
                File file = new File(Environment
                        .getExternalStorageDirectory()
                        + File.separator
                        + "/DCIM/Camera/" + "image_new" + Integer.toString(ct++) + ".png");
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    if (fos != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                        fos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "image_new" + ct , "photo");
            }
        });

        Button buttonCropImage = (Button) findViewById(R.id.buttonCropImage);
        buttonCropImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                performCrop(selectedImage);
            }
        });

        Button buttonRotateImage = (Button) findViewById(R.id.buttonRotateImage);
        buttonRotateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ImageView imageView = (ImageView) findViewById(imgView);
                img.rotate();
                imageView.setImageBitmap(img.getBitmap());
            }
        });

        Button buttonEdgeDetect = (Button) findViewById(R.id.buttonEdgeDetect);
        buttonEdgeDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EdgesActivity.class);
                ImageView imageView = (ImageView) findViewById(R.id.imgView);
                img.setBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
                startActivity(i);
            }
        });


        final SeekBar SeekBar_contrast = (SeekBar) findViewById(R.id.SeekBar1);
        SeekBar_contrast.setMax(10);
        SeekBar_contrast.setProgress((5));
        SeekBar_contrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {


            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                ImageView imageView = (ImageView) findViewById(R.id.imgView);
                Bitmap bitmap = img.getBitmap();
                img.changeBitmapContrastBrightness(progress,0);
                imageView.setImageBitmap(img.getBitmap());
            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageView = (ImageView) findViewById(imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            img = new Image(BitmapFactory.decodeFile(picturePath),"image " + Integer.toString(ct++));
        }

        if (requestCode == PIC_CROP && data != null) {
            Bundle extras = data.getExtras();
            Bitmap selectedBitmap = extras.getParcelable("data");
            ImageView imageView = (ImageView) findViewById(imgView);
            imageView.setImageBitmap(selectedBitmap);
            img = new Image(selectedBitmap,"image " + Integer.toString(ct++));
        }
        if (requestCode == REQUEST_TAKE_IMAGE && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(bitmap);
            img = new Image(bitmap,"image " + Integer.toString(ct++));
        }
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", true);
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


}
