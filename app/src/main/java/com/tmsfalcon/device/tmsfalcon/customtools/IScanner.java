package com.tmsfalcon.device.tmsfalcon.customtools;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Interface between activity and surface view
 */

public interface IScanner {
    void displayHint(ScanHint scanHint);
    void onPictureClicked(Bitmap bitmap, byte[] uri);
}
