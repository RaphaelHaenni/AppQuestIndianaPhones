package ch.appquest.indiana_phones.appquest3_memory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by raphael.haenni on 19.10.2017.
 */

public class QRScan {
    private static final int RESULT_OK = 1;
    MainActivity ma = new MainActivity();
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
        IntentIntegrator integrator = new IntentIntegrator( ma );
        //integrator.setCaptureActivity(MainActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientationLocked(false);
        integrator.addExtra(Intents.Scan.BARCODE_IMAGE_ENABLED, true);
        integrator.initiateScan();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            Bundle extras = intent.getExtras();
            setPath( extras.getString( Intents.Scan.RESULT_BARCODE_IMAGE_PATH ) );

            // Ein Bitmap zur Darstellung erhalten wir so:
            // Bitmap bmp = BitmapFactory.decodeFile(path)

            setCode( extras.getString( Intents.Scan.RESULT ) );
            Log.w( "CODE", "Code: "+getCode() );
        }
        // else continue with any other code you need in the method
    }


}
