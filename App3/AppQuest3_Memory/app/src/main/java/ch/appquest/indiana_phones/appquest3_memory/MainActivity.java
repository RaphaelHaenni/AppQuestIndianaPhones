package ch.appquest.indiana_phones.appquest3_memory;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View.OnClickListener imgClicker;
    private View.OnClickListener textSelector;
    private int imgTextId = 0;
    private List<ImageText> imgTexts;
    private TableLayout table;
    private ImageButton addButton;
    private ImageView currentImgView;
    private TextView currentTxtView;
    private int currentId;

    private List<String> words;
    private List<String[]> finalWords;
    private String wordString;

    private int getDP(int value)
    {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        table = (TableLayout) findViewById(R.id.tableLayout1);
        addButton = (ImageButton) findViewById(R.id.addImgs);
        imgTexts = new ArrayList<ImageText>();
        words = new ArrayList<String>();

        imgClicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentImgView = (ImageView) v;
                for (ImageText it : imgTexts)
                {
                    if (it.getId() == (int)((ImageView) v).getTag())
                    {
                        currentTxtView = it.getTextView();
                        currentId = it.getId();
                    }
                }
                takeQrCodePicture();
            }
        };

        textSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView currentTextView = (TextView) v;

                TableRow row = (TableRow)((ViewGroup) currentTextView.getParent());
                TextView secondView = (TextView) row.getChildAt(row.getChildCount() - 1);
                TextView firstView = (TextView) row.getChildAt(0);

                if (((ColorDrawable)firstView.getBackground()).getColor() == Color.parseColor("#132189"))
                {
                    firstView.setBackgroundColor(Color.TRANSPARENT);
                    firstView.setTextColor(Color.BLACK);

                    secondView.setBackgroundColor(Color.TRANSPARENT);
                    secondView.setTextColor(Color.BLACK);
                }
                else
                {
                    firstView.setBackgroundColor(Color.parseColor("#132189"));
                    firstView.setTextColor(Color.WHITE);

                    secondView.setBackgroundColor(Color.parseColor("#132189"));
                    secondView.setTextColor(Color.WHITE);
                }
            }
        };

        //creates new pair of ImageView with a TextView
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TableRow row = new TableRow(MainActivity.this);
                row.setLayoutParams(new TableRow.LayoutParams());
                row.getLayoutParams().width = TableRow.LayoutParams.WRAP_CONTENT;
                row.getLayoutParams().height = TableRow.LayoutParams.WRAP_CONTENT;
                TableRow textRow = new TableRow(MainActivity.this);
                textRow.setLayoutParams(new TableRow.LayoutParams());
                textRow.getLayoutParams().width = TableRow.LayoutParams.WRAP_CONTENT;
                textRow.getLayoutParams().height = TableRow.LayoutParams.WRAP_CONTENT;

                table.addView(row);
                table.addView(textRow);

                TableRow.LayoutParams param = new TableRow.LayoutParams();
                param.width = 0;
                param.height = getDP(200);
                param.setMargins(getDP(5),getDP(5),getDP(5),getDP(5));

                TableRow.LayoutParams textParam = new TableRow.LayoutParams();
                textParam.width = 0;
                textParam.height = getDP(50);
                textParam.setMargins(getDP(0),getDP(0),getDP(0),getDP(5));

                for (int i = 0; i < 2; i++) {
                    //set options for the newly generated views
                    ImageView imgView = new ImageView(MainActivity.this);
                    imgView.setLayoutParams(param);
                    imgView.setBackgroundColor(Color.parseColor("#132189"));
                    imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imgView.setImageResource(getResources().getIdentifier("@android:drawable/ic_menu_camera", null, getPackageName()));
                    imgView.setOnClickListener(imgClicker);
                    imgView.setTag(imgTextId);

                    TextView textView = new TextView(MainActivity.this);
                    textView.setLayoutParams(textParam);
                    textView.setText("<empty>");
                    textView.setOnClickListener(textSelector);
                    textView.setBackgroundColor(Color.TRANSPARENT);
                    imgTexts.add(new ImageText(imgTextId, imgView, textView));
                    imgTextId++;
                    row.addView(imgView);
                    textRow.addView(textView);
                }

                ViewGroup vg = (ViewGroup) addButton.getParent();
                vg.removeView(addButton);
                vg.addView(addButton);

                final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollViewWhole));
                scrollview.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        addButton.performClick();
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        loadEverything();
        checkForEmptyRows();
    }

    private void loadEverything()
    {
        try
        {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/appquest_images/memory";
            File directory = new File(path);
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                Log.d("Picture", files[i].getName());
                String absPath = files[i].getAbsolutePath();
                String fileName = files[i].getName(); // 0_Hochschule.png
                int id = Integer.parseInt(fileName.split("\\#:CODE:#")[0]); // 0
                String code = fileName.split("\\#:CODE:#")[1]; // Hochschule.PNG
                code = code.substring(0, code.length() - 4);
                Log.d("PicData", id + " - " + code);
                boolean hasGen = false;
                while (hasGen == false)
                {
                    for (ImageText it : imgTexts)
                    {
                        if (it.getId() == id)
                        {
                            hasGen = true;
                            it.getImgView().setImageURI(Uri.fromFile(new File(absPath)));
                            it.getTextView().setText(code);
                        }
                    }
                    if (hasGen == false)
                    {
                        addButton.performClick();
                    }
                }
            }
        }
        catch (Exception x)
        {
            Log.d("Exceptions", x.getMessage());
        }
    }

    private void clearImgTexts()
    {
        Iterator<ImageText> i = imgTexts.iterator();
        while (i.hasNext())
        {
            ImageText imte = i.next();
            if (imte.getId() == -1)
            {
                i.remove();
            }
        }
        if (imgTexts.size() == 0)
        {
            imgTextId = 0;
            addButton.performClick();
        }
    }

    private void checkForEmptyRows()
    {
        Iterator<ImageText> i = imgTexts.iterator();
        while (i.hasNext())
        {
            ImageText imte = i.next();
            ImageText imteN = i.next();
            if (imte.getTextView().getText() == "<empty>" && imteN.getTextView().getText() == "<empty>")
            {
                Log.d("TAGS", imte.getTextView().getText() + " " + imteN.getTextView().getText());
                TableRow imgRow = (TableRow)((ViewManager)imte.getImgView().getParent());
                TableRow textRow = (TableRow)((ViewManager)imte.getTextView().getParent());
                table.removeView(imgRow);
                table.removeView(textRow);
                imte.changeId(-1);
                imteN.changeId(-1);
            }
        }
        clearImgTexts();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItemAll = menu.add("Alle Auswählen");
        menuItemAll.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                boolean click = true;
                for (ImageText imte : imgTexts)
                {
                    if (click)
                    {
                        imte.getTextView().performClick();
                        click = false;
                    }
                    else
                    {
                        click = true;
                    }
                }
                return false;
            }
        });
        MenuItem menuItemDelete = menu.add("Löschen");
        menuItemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Iterator<ImageText> i = imgTexts.iterator();
                while (i.hasNext())
                {
                    ImageText imte = i.next();
                    if (imte.getId() != -1)
                    {
                        if (((ColorDrawable)imte.getTextView().getBackground()).getColor() == Color.parseColor("#132189"))
                        {

                            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/appquest_images/memory/" + imte.getId() + "#:CODE:#" + imte.getTextView().getText() + ".PNG";
                            File file = new File(path);
                            file.delete();
                            imte.changeId(-1);
                            TableRow imgRow = (TableRow)((ViewManager)imte.getImgView().getParent());
                            TableRow textRow = (TableRow)((ViewManager)imte.getTextView().getParent());
                            table.removeView(imgRow);
                            table.removeView(textRow);
                        }
                    }
                }
                clearImgTexts();
                return false;
            }
        });
        MenuItem menuItemLog = menu.add("Log");
        menuItemLog.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                write_log();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void write_log()
    {
        wordString = "";
        words.clear();
        finalWords = new ArrayList<String[]>();
        int counter = 0;
        String[] wordPair = new String[2];
        for (ImageText imte : imgTexts)
        {
            if (((ColorDrawable)imte.getTextView().getBackground()).getColor() == Color.parseColor("#132189"))
            {
                String newWord = imte.getTextView().getText().toString();
                words.add(newWord);
                wordString += newWord;
                counter++;
                if (counter >= 2)
                {
                    wordString += "\r\n";
                    wordPair[1] = newWord;
                    finalWords.add(wordPair);
                    wordPair = new String[2];
                    counter = 0;
                }
                else
                {
                    wordString += ", ";
                    wordPair[0] = newWord;
                }
            }
        }

        if (!words.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Abschicken");
            builder.setMessage("Wollen Sie die Lösung\r\n" + wordString + " wirklich abschicken?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try
                            {
                                log();
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
        else
        {
            Toast.makeText(MainActivity.this, "Bitte wählen Sie den Text unter den Bildern an die sie zusammen abschicken möchten.", Toast.LENGTH_LONG).show();
        }
    }

    public void log() throws JSONException {
        Intent intent = new Intent("ch.appquest.intent.LOG");

        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject json = new JSONObject();
        json.put("task", "Memory");

        json.put("solution", new JSONArray(finalWords));

        // Achtung, je nach App wird etwas anderes eingetragen
        String logmessage = json.toString();

        intent.putExtra("ch.appquest.logmessage", logmessage);

        startActivity(intent);
    }

    //create Zxing barcode scanner object and set options when clicked on a ImageView
    public void takeQrCodePicture() {
        IntentIntegrator integrator = new IntentIntegrator( MainActivity.this );
        integrator.setCaptureActivity(CameraIntent.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientationLocked(true);
        integrator.addExtra(Intents.Scan.BARCODE_IMAGE_ENABLED, true);
        integrator.initiateScan();

    }

    //Gets called automatically when integrator.initiateScan() is finished.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //Check if the capture is cancelled (back button etc.)
        if( intent != null ) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanResult != null) {
                Bundle extras = intent.getExtras();
                String path = extras.getString(Intents.Scan.RESULT_BARCODE_IMAGE_PATH);

                Bitmap bmp = BitmapFactory.decodeFile(path);

                //set the firstly selected ImageView to the captured picture
                currentImgView.setImageBitmap(bmp);

                Log.e("PATH", "Path: " + path);

                //sets text in the selected field to the QR code result
                String code = extras.getString(Intents.Scan.RESULT);
                Log.e("CODE", "Code: " + code);

                currentTxtView.setText(code);

                SaveImage(bmp, currentId + "#:CODE:#" + code);
            }
        } else {
            return;
        }
    }

    private void SaveImage(Bitmap finalBitmap, String fileName) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(); //Environment.getDataDirectory().getAbsolutePath();
        File myDir = new File(root + "/appquest_images/memory");
        myDir.mkdirs();
        String fname = fileName +".PNG";
        File file = new File(myDir, fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
