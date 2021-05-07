package com.tmsfalcon.device.tmsfalcon.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.tmsfalcon.device.tmsfalcon.R;

/**
 * Created by Android on 11/1/2017.
 */

public class TextProgressBar extends ProgressBar {
    private String text;
    private Paint textPaint;

    public TextProgressBar(Context context) {
        super(context);
        text = "0";
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        text = "0";
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        text = "0";
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // First draw the regular progress bar, then custom draw our text
        super.onDraw(canvas);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        int sizeInPx = getResources().getDimensionPixelSize(R.dimen.text_14);
        textPaint.setTextSize(sizeInPx);
        textPaint.setColor(Color.WHITE);
        int x = getWidth() / 2 - bounds.centerX();
        int y = getHeight() / 2 - bounds.centerY();
        canvas.drawText(text, x, y, textPaint);
    }

    public synchronized void setText(String text) {
        this.text = text;
        drawableStateChanged();
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
        drawableStateChanged();
    }
}
