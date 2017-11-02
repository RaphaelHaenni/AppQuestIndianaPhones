package ch.appquest.indiana_phones.appquest3_memory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private View.OnClickListener imgClicker;
    private View.OnClickListener textSelector;
    private int imgTextId = 0;
    private List<ImageText> imgTexts;
    private TableLayout table;
    private ImageButton addButton;
    private ImageView currentImgView;
    private TextView currentTxtView;

    private TextView text1;
    private TextView text2;

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

        imgClicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentImgView = (ImageView) v;
                for (ImageText it : imgTexts)
                {
                    if (it.getId() == (int)((ImageView) v).getTag())
                    {
                        currentTxtView = it.getTextView();
                    }
                }
                takeQrCodePicture();
            }
        };

        textSelector = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView currentTextView = (TextView) v;

                if (text1 != null && text2 != null)
                {
                    text1.setBackgroundColor(Color.TRANSPARENT);
                    text1.setTextColor(Color.BLACK);
                    text2.setBackgroundColor(Color.TRANSPARENT);
                    text2.setTextColor(Color.BLACK);
                }

                TableRow row = (TableRow)((ViewGroup) currentTextView.getParent());
                TextView secondView = (TextView) row.getChildAt(row.getChildCount() - 1);
                TextView firstView = (TextView) row.getChildAt(0);

                firstView.setBackgroundColor(Color.parseColor("#132189"));
                firstView.setTextColor(Color.WHITE);

                secondView.setBackgroundColor(Color.parseColor("#132189"));
                secondView.setTextColor(Color.WHITE);

                text1 = firstView;
                text2 = secondView;
            }
        };

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
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add("Log");
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

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
        if (text1 != null && text2 != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Abschicken");
            builder.setMessage("Wollen Sie die Lösung " + text1.getText() + " " + text2.getText() + " wirklich abschicken?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "OK!", Toast.LENGTH_LONG).show();
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

    public void takeQrCodePicture() {
        IntentIntegrator integrator = new IntentIntegrator( MainActivity.this );
        integrator.setCaptureActivity(CameraIntent.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientationLocked(true);
        integrator.addExtra(Intents.Scan.BARCODE_IMAGE_ENABLED, true);
        integrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if( intent != null ) {
            Log.e( "INTENT", "It works just fine" );
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanResult != null) {
                Log.e("ONACTIVITYTESULT", "It started");
                Bundle extras = intent.getExtras();
                String path = extras.getString(Intents.Scan.RESULT_BARCODE_IMAGE_PATH);

                // Ein Bitmap zur Darstellung erhalten wir so:
                Bitmap bmp = BitmapFactory.decodeFile(path);

                currentImgView.setImageBitmap(bmp);
                Log.e("PATH", "Path: " + path);

                String code = extras.getString(Intents.Scan.RESULT);
                Log.e("CODE", "Code: " + code);
            }
            // else continue with any other code you need in the method
        } else {
            return;

        }
    }
}
