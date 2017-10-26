package ch.appquest.indiana_phones.appquest3_memory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by raphael.haenni on 19.10.2017.
 */

public class QRScan extends Activity {
    private String path;
    private String code;

    public void setPath( String p ) {
        this.path = p;
    }

    public String getPath() {
        return this.path;
    }

    public void setCode( String c ) {
        this.code = c;
    }

    public String getCode() {
        return this.code;
    }

    public void takeQrCodePicture( MainActivity ma ) {
        Log.e("TAKEQRCODEPICTURE", "takeQrCodePicture");
        IntentIntegrator integrator = new IntentIntegrator( ma );
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
            setPath( path );

            // Ein Bitmap zur Darstellung erhalten wir so:
            // Bitmap bmp = BitmapFactory.decodeFile(path)
            Log.d( "PATH", "Path: "+path );

            String code = extras.getString( Intents.Scan.RESULT );
            Log.d( "CODE", "Code: "+code );
        }
        // else continue with any other code you need in the method
    }

}
