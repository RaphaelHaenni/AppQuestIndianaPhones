package ch.appquest.indiana_phones.appquest5_pixelmaler;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ColorPicker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);
    }

    public void chooseColour(View view) {
        String colourStr = view.getTag().toString();
        Log.e("CLICKED", colourStr );
        MainActivity.color = colourStr;

        Intent intent = new Intent();
        setResult(RESULT_OK,intent );
        finish();
    }
}