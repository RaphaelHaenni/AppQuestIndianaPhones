package ch.appquest.indiana_phones.appquest2_dechiffrierer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePicture extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        scanEverything();

        MainActivity.countReset++;
        if (MainActivity.countReset >= 2)
        {
            kill_activity();
        }
        else {
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.i("EXI", "IOException");
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file path
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(); //Environment.getDataDirectory().getAbsolutePath();
        File myDir = new File(root + "/appquest_images");
        myDir.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Image-"+ timeStamp +".PNG";
        File image = new File (myDir, fname);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void scanMedia(String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        sendBroadcast(scanFileIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                Bitmap newOne = rotating(Uri.parse(mCurrentPhotoPath).getPath());
                File file = new File(Uri.parse(mCurrentPhotoPath).getPath());
                file.delete();
                SaveImage(newOne);
                scanEverything();
                kill_activity();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void kill_activity()
    {
        MainActivity.countReset = 0;
        finish();
    }

    private void SaveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(); //Environment.getDataDirectory().getAbsolutePath();
        File myDir = new File(root + "/appquest_images");
        myDir.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Image-"+ timeStamp +".PNG";
        File file = new File (myDir, fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap rotating(String file)
    {
        try {
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file, bounds);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(file, opts);
            ExifInterface exif = new ExifInterface(file);
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
            return rotatedBitmap;
        }
        catch (Exception x)
        {
            Log.d("Exceptions", x.getMessage());
        }
        return null;
    }

    private void scanEverything()
    {
        try
        {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/appquest_images";
            File directory = new File(path);
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                scanMedia(files[i].getAbsolutePath());
            }
        }
        catch (Exception x)
        {
            Log.d("Exceptions", x.getMessage());
        }
    }
}
