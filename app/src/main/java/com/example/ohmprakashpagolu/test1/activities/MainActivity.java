package com.example.ohmprakashpagolu.test1.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.ohmprakashpagolu.test1.R;

import static com.example.ohmprakashpagolu.test1.R.id.imgView;

public class MainActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    private static int PIC_CROP = 2;
    private static int REQUEST_IMAGE_CAPTURE = 3;
    public static Uri selectedImage;
    public static int ct = 0;
    public static float rotation = 0;
    protected String[] requestedPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button buttonTakeImage = (Button) findViewById(R.id.buttonTakeImage);
        buttonTakeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotation = 0;

            }
        });


        Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadImage);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                rotation = 0;
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        Button buttonSaveImage = (Button) findViewById(R.id.buttonSaveImage);
        buttonTakeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) findViewById(imgView);
                BitmapDrawable draw = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = draw.getBitmap();
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "image_new" + ct , "photo");
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
                imageView.setRotation(rotation);
                rotation += 90;
            }
        });
        Button buttonEdgeDetect = (Button) findViewById(R.id.buttonEdgeDetect);
        buttonEdgeDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),EdgesActivity.class);
                i.putExtra("image",selectedImage);
                startActivity(i);
            }
        });


        final SeekBar SeekBar_contrast=(SeekBar)findViewById(R.id.SeekBar1);
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
                if (progress <= 5) {
                    progress = 1 - progress / 5;
                } else {
                    progress = 1 + (progress / 10) * 9;
                }
                ImageView imageView = (ImageView)findViewById(R.id.imgView);
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                Bitmap new_bm = changeBitmapContrastBrightness(bitmap, (float)(progress-1),(float)SeekBar_contrast.getProgress() - 255);
                imageView.setImageBitmap(new_bm);
            }
        });

    }
    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0,brightness,
                        0, contrast, 0, 0,brightness,
                        0, 0, contrast, 0,brightness,
                        0, 0, 0, 1,0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
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
        }
        if (requestCode == PIC_CROP && data != null) {
            Bundle extras = data.getExtras();
            Bitmap selectedBitmap = extras.getParcelable("data");
            ImageView imageView = (ImageView) findViewById(imgView);
            imageView.setImageBitmap(selectedBitmap);
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
