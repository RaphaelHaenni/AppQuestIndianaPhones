package ch.appquest.indiana_phones.appquest5_pixelmaler;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String color;
    private ImageView[][] rpixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        color = "#FFFFFFFF";

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
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setBackgroundColor(Color.parseColor(color));
                        view.setTag(color);
                    }
                });
                rpixels[x][y] = view;
                grid.addView(view);
            }
        }

        GridLayout.LayoutParams buttonParams = new GridLayout.LayoutParams();
        buttonParams.width = width/3;
        buttonParams.columnSpec = GridLayout.spec(0, 13);
        buttonParams.rowSpec = GridLayout.spec(13);

        Button btn = new Button(this);
        btn.setText("Pick Color");
        btn.setLayoutParams(buttonParams);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inti = new Intent(MainActivity.this, ColorPicker.class);
                startActivity(inti);
            }
        });
        grid.addView(btn);
    }
}
