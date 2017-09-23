package ch.appquest.indiana_phones.appquest1_magnetsensor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements SensorEventListener {

    private static final int SCAN_QR_CODE_REQUEST_CODE = 0;
    private SensorManager mSensorManager;
    private Sensor mSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        try {
            mSensor = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);
        } catch (Exception x) {
            try {
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            } catch (Exception x2) {
                Toast.makeText(this, "Dein Device besitzt keinen MagnetSensor", Toast.LENGTH_LONG).show();
            }
        }
        mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_NORMAL);

        SeekBar genauigkeit = (SeekBar) findViewById(R.id.genauigkeit);
        genauigkeit.setMax((int)(mSensor.getMaximumRange()));
        genauigkeit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                ProgressBar hellworld = (ProgressBar) findViewById(R.id.hellworld);
                hellworld.setMax(progress);
                TextView genauigkeitsText = (TextView) findViewById(R.id.genauigkeitsText);
                genauigkeitsText.setText("Genauigkeit: " + String.valueOf(progress));
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        ProgressBar hellworld = (ProgressBar) findViewById(R.id.hellworld);
        float[] mag = event.values;
        double betrag = Math.sqrt(mag[0] * mag[0] + mag[1] * mag[1] + mag[2] * mag[2]);
        hellworld.setProgress((int) betrag);

        TextView strahlungsText = (TextView) findViewById(R.id.strahlung);
        strahlungsText.setText("Strahlung: " + String.valueOf(betrag));

        TextView schatzmeter = (TextView) findViewById(R.id.schatzmeter);

        if (betrag > 0 && betrag < hellworld.getMax() / 3 )
        {
            schatzmeter.setText("Kein Schatz");
            hellworld.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        }
        else if (betrag > hellworld.getMax() / 3 && betrag < ((hellworld.getMax() / 3) * 2))
        {
            schatzmeter.setText("Schatz in der Nähe");
            hellworld.getProgressDrawable().setColorFilter(Color.parseColor("#F49E42") , android.graphics.PorterDuff.Mode.SRC_IN);
        }
        else
        {
            schatzmeter.setText("Schatz gefunden!");
            hellworld.getProgressDrawable().setColorFilter(Color.parseColor("#00BA03"), android.graphics.PorterDuff.Mode.SRC_IN);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add("Log");
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SCAN_QR_CODE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String logMsg = intent.getStringExtra("SCAN_RESULT");
                try {
                    log(logMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void log(String qrCode) throws JSONException {
        Intent intent = new Intent("ch.appquest.intent.LOG");

        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject json = new JSONObject();
        json.put("task", "Metalldetektor");
        json.put("solution", qrCode);

        // Achtung, je nach App wird etwas anderes eingetragen
        String logmessage = json.toString();

        intent.putExtra("ch.appquest.logmessage", logmessage);

        startActivity(intent);
    }
}
