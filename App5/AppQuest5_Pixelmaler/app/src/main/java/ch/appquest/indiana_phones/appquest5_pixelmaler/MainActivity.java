package ch.appquest.indiana_phones.appquest5_pixelmaler;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static String color;
    private ImageView[][] rpixels;
    private Context that;
    private Intent shareIntent;
    private File file;
    private boolean hasPerms;
    private boolean wantsToShare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        that = this;

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        color = "#FF000000";

        rpixels = new ImageView[13][13];

        GridLayout grid = (GridLayout) findViewById(R.id.theGrid);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        double panelWidth = (width / 13) - 4;



        for (int y = 0; y < 13; y++)
        {
            for (int x = 0; x < 13; x++)
            {
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = (int)panelWidth;
                params.height = (int)panelWidth;
                params.columnSpec = GridLayout.spec(x);
                params.rowSpec = GridLayout.spec(y);
                params.topMargin = 2;
                params.bottomMargin = 2;
                params.rightMargin = 2;
                params.leftMargin = 2;

                ImageView view = new ImageView(this);
                view.setLayoutParams(params);
                view.setScaleType(ImageView.ScaleType.FIT_XY);
                view.setAdjustViewBounds(true);
                view.setBackgroundColor(Color.WHITE);
                view.setTag(color);
                rpixels[x][y] = view;
                grid.addView(view);
            }
        }

        ScrollView scrolli = findViewById(R.id.scrollViewWhole);
        scrolli.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                for (int y = 0; y < 13; y++)
                {
                    for (int x = 0; x < 13; x++)
                    {
                        int[] location = new int[2];
                        rpixels[x][y].getLocationOnScreen(location);
                        int x1 = location[0];
                        int y1 = location[1];

                        int x2 = x1 + rpixels[x][y].getWidth();
                        int y2 = y1 + rpixels[x][y].getHeight();

                        if (motionEvent.getX() > x1 && motionEvent.getX() < x2)
                        {
                            if (motionEvent.getY() > y1 && motionEvent.getY() < y2)
                            {
                                colorThis(rpixels[x][y]);
                            }
                        }
                    }
                }
                return false;
            }
        });

        GridLayout.LayoutParams colorBtnParams = new GridLayout.LayoutParams();
        colorBtnParams.columnSpec = GridLayout.spec(1, 2);
        colorBtnParams.rowSpec = GridLayout.spec(13, 2);
        colorBtnParams.width = (int)panelWidth * 2;
        colorBtnParams.height = (int)panelWidth * 2;
        colorBtnParams.topMargin = 2;
        colorBtnParams.bottomMargin = 2;
        colorBtnParams.rightMargin = 2;
        colorBtnParams.leftMargin = 2;

        ImageView colorBtnView = new ImageView(this);
        colorBtnView.setLayoutParams(colorBtnParams);
        colorBtnView.setScaleType(ImageView.ScaleType.FIT_XY);
        colorBtnView.setAdjustViewBounds(true);
        colorBtnView.setBackgroundColor(Color.BLUE);
        colorBtnView.setBackgroundResource(R.drawable.colorico);
        colorBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inti = new Intent(MainActivity.this, ColorPicker.class);
                startActivity(inti);
            }
        });
        grid.addView(colorBtnView);

        GridLayout.LayoutParams clearBtnParams = new GridLayout.LayoutParams();
        clearBtnParams.columnSpec = GridLayout.spec(4, 2);
        clearBtnParams.rowSpec = GridLayout.spec(13, 2);
        clearBtnParams.width = (int)panelWidth * 2;
        clearBtnParams.height = (int)panelWidth * 2;
        clearBtnParams.topMargin = 2;
        clearBtnParams.bottomMargin = 2;
        clearBtnParams.rightMargin = 2;
        clearBtnParams.leftMargin = 2;

        ImageView clearBtnView = new ImageView(this);
        clearBtnView.setLayoutParams(clearBtnParams);
        clearBtnView.setScaleType(ImageView.ScaleType.FIT_XY);
        clearBtnView.setAdjustViewBounds(true);
        clearBtnView.setBackgroundColor(Color.BLUE);
        clearBtnView.setBackgroundResource(R.drawable.clear);
        clearBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(that)
                        .setTitle("Clear")
                        .setMessage("Do you really want to clear the whole canvas?")
                        .setIcon(R.drawable.warning_icon)
                        .setPositiveButton("Heck Yes!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                clearImage();
                            }})
                        .setNegativeButton("NOOOOOOOOOOOO!", null).show();
            }
        });
        grid.addView(clearBtnView);

        GridLayout.LayoutParams shareBtnParams = new GridLayout.LayoutParams();
        shareBtnParams.columnSpec = GridLayout.spec(7, 2);
        shareBtnParams.rowSpec = GridLayout.spec(13, 2);
        shareBtnParams.width = (int)panelWidth * 2;
        shareBtnParams.height = (int)panelWidth * 2;
        shareBtnParams.topMargin = 2;
        shareBtnParams.bottomMargin = 2;
        shareBtnParams.rightMargin = 2;
        shareBtnParams.leftMargin = 2;

        ImageView shareBtnView = new ImageView(this);
        shareBtnView.setLayoutParams(shareBtnParams);
        shareBtnView.setScaleType(ImageView.ScaleType.FIT_XY);
        shareBtnView.setAdjustViewBounds(true);
        shareBtnView.setBackgroundColor(Color.BLUE);
        shareBtnView.setBackgroundResource(R.drawable.share);
        shareBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPerms)
                {
                    saveImage();
                }
                else
                {
                    wantsToShare = true;
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
            }
        });
        grid.addView(shareBtnView);

        GridLayout.LayoutParams logBtnParams = new GridLayout.LayoutParams();
        logBtnParams.columnSpec = GridLayout.spec(10, 2);
        logBtnParams.rowSpec = GridLayout.spec(13, 2);
        logBtnParams.width = (int)panelWidth * 2;
        logBtnParams.height = (int)panelWidth * 2;
        logBtnParams.topMargin = 2;
        logBtnParams.bottomMargin = 2;
        logBtnParams.rightMargin = 2;
        logBtnParams.leftMargin = 2;

        ImageView logBtnView = new ImageView(this);
        logBtnView.setLayoutParams(logBtnParams);
        logBtnView.setScaleType(ImageView.ScaleType.FIT_XY);
        logBtnView.setAdjustViewBounds(true);
        logBtnView.setBackgroundColor(Color.BLUE);
        logBtnView.setBackgroundResource(R.drawable.log);
        logBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ask_log();
            }
        });
        grid.addView(logBtnView);

        clearImage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "You won't be able to share or save the image if you don't grant us the permission!", Toast.LENGTH_LONG).show();
            hasPerms = false;
        }
        else
        {
            hasPerms = true;
            if (wantsToShare)
            {
                wantsToShare = false;
                saveImage();
            }
        }
    }

    public void clearImage()
    {
        for (int y = 0; y < 13; y++)
        {
            for (int x = 0; x < 13; x++)
            {
                rpixels[x][y].setBackgroundColor(Color.parseColor("#FFFFFFFF"));
                rpixels[x][y].setTag("#FFFFFFFF");
            }
        }
    }

    public void colorThis(ImageView img)
    {
        img.setBackgroundColor(Color.parseColor(color));
        img.setTag(color);
    }

    private void ask_log()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Abschicken");
        builder.setMessage("Wollen Sie die Lösung wirklich abschicken?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try
                        {
                            write_log();
                        }
                        catch(JSONException x)
                        {
                            Toast.makeText(MainActivity.this, x.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void write_log() throws JSONException {
        Intent intent = new Intent("ch.appquest.intent.LOG");

        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject json = new JSONObject();
        json.put("'task'", "'Pixelmaler'");
        String jString = "[";
        for( int y = 0; y < 13; y++ ) {
            for( int x = 0; x < 13; x++ ) {
                String colour = rpixels[x][y].getTag().toString();
                if( colour != "#FFFFFFFF" ) {
                    jString += "{'y': '"+y+"', 'x': '" + x + "', 'color':'" + colour + "'},";
                }

            }
        }
        jString = jString.substring(0, jString.length() - 1);
        jString += "]";
        json.put("pixels", jString);
        String logmessage = json.toString();
        logmessage = logmessage.replaceAll("\"", "");
        intent.putExtra("ch.appquest.logmessage", logmessage);
        startActivity(intent);
    }

    public void saveImage()
    {
        int multiplier = 50;
        int newXlength = rpixels.length * multiplier;
        int newYlength = rpixels[0].length * multiplier;
        int[][] stretched = new int[newXlength][newYlength];

        for (int yS = 0; yS < stretched[0].length; yS++)
        {
            for (int xS = 0; xS < stretched.length; xS++)
            {
                int xO = xS/multiplier;
                int yO = yS/multiplier;
                stretched[yS][xS] = Color.parseColor(rpixels[xO][yO].getTag().toString());
            }
        }

        Bitmap bm = bitmapFromArray(stretched);
        saveImage(bm, generateString());
    }

    public static Bitmap bitmapFromArray(int[][] pixels2d){
        int width = pixels2d.length;
        int height = pixels2d[0].length;
        int[] pixels = new int[width * height];
        int pixelsIndex = 0;
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                pixels[pixelsIndex] = pixels2d[i][j];
                pixelsIndex ++;
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    private void saveImage(Bitmap finalBitmap, String fileName) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(); //Environment.getDataDirectory().getAbsolutePath();
        File myDir = new File(root + "/appquest_images/pixelmaler");
        myDir.mkdirs();
        String fname = fileName +".png";
        file = new File(myDir, fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.e("WOO", "IIII");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Uri bitmapUri = Uri.fromFile(file);
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri );
        startActivityForResult(Intent.createChooser(shareIntent , "Share"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        new AlertDialog.Builder(that)
                .setTitle("Clear")
                .setMessage("Do you want to keep the picture in your gallery?")
                .setIcon(R.drawable.warning_icon)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
                        sendBroadcast(scanFileIntent);
                    }})
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                file.delete();
                            }
                        }
                ).show();
    }
}
