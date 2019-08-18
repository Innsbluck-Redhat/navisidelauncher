package com.innsbluck.navisidelauncher.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import java.util.Locale;

public class NavTextView extends android.support.v7.widget.AppCompatTextView {

    public NavTextView(Context context) {
        super(context);
        setup();
    }

    public NavTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        Typeface tf = Typeface.createFromAsset(getContext().getApplicationContext().getAssets(),
                String.format(Locale.US, "fonts/%s", "COMPUTER.ttf"));
        setTypeface(tf);
    }
}
