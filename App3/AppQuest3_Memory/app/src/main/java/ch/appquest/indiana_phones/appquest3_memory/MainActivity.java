package ch.appquest.indiana_phones.appquest3_memory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity {
    public static int countReset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.BtnChoose);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                QRScan qrScan = new QRScan();
                takeQrCodePicture();

            }
        });

    }

    public void takeQrCodePicture() {
        Log.e("TAKEQRCODEPICTURE", "takeQrCodePicture");
        IntentIntegrator integrator = new IntentIntegrator( MainActivity.this );
        integrator.setCaptureActivity(CameraIntent.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientationLocked(true);
        integrator.addExtra(Intents.Scan.BARCODE_IMAGE_ENABLED, true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            Log.e( "ONACTIVITYTESULT", "It started" );
            Bundle extras = intent.getExtras();
            String path = extras.getString( Intents.Scan.RESULT_BARCODE_IMAGE_PATH );

            // Ein Bitmap zur Darstellung erhalten wir so:
            // Bitmap bmp = BitmapFactory.decodeFile(path)
            Log.e( "PATH", "Path: "+path );

            String code = extras.getString( Intents.Scan.RESULT );
            Log.e( "CODE", "Code: "+code );
        }
        // else continue with any other code you need in the method
    }
}
