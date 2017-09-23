package ch.appquest.indiana_phones.appquest1_magnetsensor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static ch.appquest.indiana_phones.appquest1_magnetsensor.R.id.txtViewExample;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager sm;
    private Sensor s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sm = (SensorManager) getSystemService( SENSOR_SERVICE );
        s = sm.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD );
        float[] mag = event.values;
        double betrag = Math.sqrt(mag[0] * mag[0] + mag[1] * mag[1] + mag[2] * mag[2]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private static final int SCAN_QR_CODE_REQUEST_CODE = 0;

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
        TextView tv = (TextView) findViewById(txtViewExample);

        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject json = new JSONObject();
        json.put("task", "Metalldetektor IndianaPhones");
        json.put("solution", qrCode);

        // Achtung, je nach App wird etwas anderes eingetragen
        String logmessage = json.toString();

        tv.setText(logmessage);
        intent.putExtra("ch.appquest.logmessage", logmessage);

        startActivity(intent);
    }
}
