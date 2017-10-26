package ch.appquest.indiana_phones.appquest3_memory;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by simon.chiarot on 26.10.2017.
 */

public class ImageText {
    private ImageView img;
    private TextView text;

    public ImageText(ImageView imgView, TextView textView)
    {
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
}
