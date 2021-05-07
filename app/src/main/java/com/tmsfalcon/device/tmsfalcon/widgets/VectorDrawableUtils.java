package com.tmsfalcon.device.tmsfalcon.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by Dell on 3/27/2018.
 */

public class VectorDrawableUtils {

    public static Drawable getDrawable(Context context, int drawableResId) {
        return VectorDrawableCompat.create(context.getResources(), drawableResId, context.getTheme());
    }

    public static Drawable getDrawable(Context context, int drawableResId, int colorFilter) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        drawable.setColorFilter(ContextCompat.getColor( context, colorFilter), PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = getDrawable(context, drawableId);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}

