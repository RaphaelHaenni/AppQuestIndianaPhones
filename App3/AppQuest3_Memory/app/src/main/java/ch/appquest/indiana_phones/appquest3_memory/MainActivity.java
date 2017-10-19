package ch.appquest.indiana_phones.appquest3_memory;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
                qrScan.takeQrCodePicture( MainActivity.this );

            }
        });

    }
}
