package ch.appquest.indiana_phones.appquest3_memory;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by simon.chiarot on 26.10.2017.
 */

public class ImageText {
    private ImageView img;
    private TextView text;
    private int id;

    public ImageText(int id, ImageView imgView, TextView textView)
    {
        this.id = id;
        this.img = imgView;
        this.text = textView;
    }

    public ImageView getImgView()
    {
        return this.img;
    }

    public TextView getTextView()
    {
        return this.text;
    }

    public int getId()
    {
        return this.id;
    }
}
