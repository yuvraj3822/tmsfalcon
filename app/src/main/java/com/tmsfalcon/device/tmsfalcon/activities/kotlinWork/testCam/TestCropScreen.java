package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scanlibrary.Utils;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.directUploadModule.DirectUploadCameraScreen;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestCropScreen extends NavigationBaseActivity  implements ComponentCallbacks2 {

    private static final String TAG = TestCropScreen.class.getSimpleName();
    int REQUEST_CODE = 99;
    int degree = 0;
    PermissionManager permissionsManager = new PermissionManager();
    static final Integer WRITE_EXTERNAL_STORAGE = 0x6;
    int crop_screen = 0;
    int has_denied_permission = 0;

    private ImageView cropImageView;
    private FrameLayout cropLayout;
    Bitmap copyBitmap;
    int actual_screen_height;
    int screen_width;
    boolean file_imported_from_gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.test_activity_direct_upload_crop_screen, null, false);
        drawer.addView(contentView, 0);
        //setContentView(R.layout.activity_direct_upload_crop_screen);
        ButterKnife.bind(this);
        file_imported_from_gallery = getIntent().getBooleanExtra("imported_from_gallery",false);
        cropRotate.setVisibility(View.VISIBLE);
        if(crop_screen == 0){
            if (!permissionsManager.checkPermission(TestCropScreen.this, TestCropScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if(has_denied_permission == 0){ //To prevent continous asking permission alert dialog
                    permissionsManager.askForPermission(TestCropScreen.this, TestCropScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
                }
            }
            else {

                if(file_imported_from_gallery){
//                    init_for_imported_file();
                }
                else{
                    init();
                }

            }
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) back_btn.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.setMargins(0, 0, 20, 0);

        back_btn.setLayoutParams(params);

        cropRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRotate();
            }
        });
    }

    public void setRotate(){
        degree = degree % 360;
        float s;
        Bitmap bitmap = AppController.nowbitmap;

        if(bitmap != null){
            s = (cropLayout.getMeasuredWidth() - 0.0f)
                    / bitmap.getHeight();
        }
        else{
            s = (cropLayout.getMeasuredWidth() - 0.0f)
                    / cropLayout.getHeight();
        }

        //AppController.nowbitmap = rotateImage(AppController.nowbitmap,degree);
        if (degree == 0 || degree == 180) {
            ValueAnimator anim = ObjectAnimator.ofFloat(cropLayout,
                    "rotation", degree, degree + 90);
            ValueAnimator anim2 = ObjectAnimator.ofFloat(
                    cropLayout, "scaleX", 1f, s);
            ValueAnimator anim3 = ObjectAnimator.ofFloat(
                    cropLayout, "scaleY", 1f, s);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(anim).with(anim2).with(anim3);
            animatorSet.start();
        } else {

            ValueAnimator anim = ObjectAnimator.ofFloat(cropLayout,
                    "rotation", degree, degree + 90);
            ValueAnimator anim2 = ObjectAnimator.ofFloat(
                    cropLayout, "scaleX", s, 1f);
            ValueAnimator anim3 = ObjectAnimator.ofFloat(
                    cropLayout, "scaleY", s, 1f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(anim).with(anim2).with(anim3);
            animatorSet.start();
        }
        degree += 90;
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private void setBitmap(Bitmap original) {
        Bitmap scaledBitmap = scaledBitmap(original, cropLayout.getWidth(), cropLayout.getHeight());
        cropImageView.setImageBitmap(scaledBitmap);
        Bitmap tempBitmap = ((BitmapDrawable) cropImageView.getDrawable()).getBitmap();
//        Map<Integer, PointF> pointFs = getEdgePoints(tempBitmap);
//        polygonView.setPoints(pointFs);
//        polygonView.setVisibility(View.VISIBLE);
        int padding = (int) getResources().getDimension(com.scanlibrary.R.dimen.scanPadding);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;
//        polygonView.setLayoutParams(layoutParams);
    }


//    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) {
//        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
//        Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
//        return orderedPoints;
//    }



    private List<PointF> getContourEdgePoints(Bitmap tempBitmap) {
        float[] points = getPoints(tempBitmap);
        float x1 = points[0];
        float x2 = points[1];
        float x3 = points[2];
        float x4 = points[3];

        float y1 = points[4];
        float y2 = points[5];
        float y3 = points[6];
        float y4 = points[7];

        List<PointF> pointFs = new ArrayList<>();
        pointFs.add(new PointF(x1, y1));
        pointFs.add(new PointF(x2, y2));
        pointFs.add(new PointF(x3, y3));
        pointFs.add(new PointF(x4, y4));
        return pointFs;
    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }

//    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
//        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
//        if (!polygonView.isValidShape(orderedPoints)) {
//            orderedPoints = getOutlinePoints(tempBitmap);
//        }
//        return orderedPoints;
//    }

//    public native float[] getPoints(Bitmap bitmap);




//    public void init_for_imported_file(){
//        crop_screen = 1;
//
//        polygonView = findViewById(R.id.polygon_view);
//        cropImageView = findViewById(R.id.crop_image_view);
//        cropLayout = findViewById(R.id.crop_layout);
//
//        copyBitmap = AppController.nowbitmap;
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        screen_width = displayMetrics.widthPixels;
//
//        //get action bar height
//        TypedValue tv = new TypedValue();
//        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
//        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);
//
//        //get height of bottom layout
//        int bottom_layout_height = getHeightOfView(bottom_layout);
//
//        actual_screen_height = height - (actionBarHeight+bottom_layout_height);
//
//        Bitmap temp_copyBitmap = copyBitmap;
//
//        Mat originalMat = new Mat(temp_copyBitmap.getHeight(), temp_copyBitmap.getWidth(), CvType.CV_8UC1);
//        org.opencv.android.Utils.bitmapToMat(temp_copyBitmap, originalMat);
//        ArrayList<PointF> points;
//        Map<Integer, PointF> pointFs = new HashMap<>();
//
//        try {
//            points = ScanUtils.getPolygonDefaultPoints(temp_copyBitmap);
//
//            int index = -1;
//            for (PointF pointF : points) {
//                pointFs.put(++index, pointF);
//            }
//
//            polygonView.setPoints(pointFs);
//            int padding = (int) getResources().getDimension(R.dimen.scan_padding);
//            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(temp_copyBitmap.getWidth() + 2 * padding, temp_copyBitmap.getHeight() + 2 * padding);
//            layoutParams.gravity = Gravity.CENTER;
//            polygonView.setLayoutParams(layoutParams);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//                TransitionManager.beginDelayedTransition(cropContainer);
//            cropLayout.setVisibility(View.VISIBLE);
//
//            cropImageView.setImageBitmap(temp_copyBitmap);
//            cropImageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage(), e);
//        }
//    }

    private int getHeightOfView(View contentview) {
        contentview.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        //contentview.getMeasuredWidth();
        return contentview.getMeasuredHeight();
    }

    private Bitmap getBitmap() {
        Uri uri = AppController.nowUri;
        try {
            Bitmap bitmap = Utils.getBitmap(this, uri);
            getContentResolver().delete(uri, null, null);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void init(){

        crop_screen = 1;

//        polygonView = findViewById(R.id.polygonView);
        cropImageView = findViewById(R.id.sourceImageView);
        cropLayout = findViewById(R.id.sourceFrame);

        copyBitmap = AppController.nowbitmap;


//        setBitmap(copyBitmap);

        cropLayout.post(new Runnable() {
            @Override
            public void run() {
             Bitmap   original = getBitmap();
                if (original != null) {


                    setBitmap(original);
                }
            }
        });

/*
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        screen_width = displayMetrics.widthPixels;

        //get action bar height
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);

        //get height of bottom layout
        //int bottom_layout_height = bottom_layout.getHeight();

        int bottom_layout_height = getHeightOfView(bottom_layout);

        Log.e("screen_height"," is "+height);
        Log.e("actionBarHeight"," is "+actionBarHeight);
        Log.e("bottom_layout_height"," is "+bottom_layout_height);

        actual_screen_height = height - (actionBarHeight+bottom_layout_height);

        Log.e("actual_screen_height"," is "+actual_screen_height);
        Log.e("screen_width"," is "+screen_width);

        if (copyBitmap==null){
            Log.e("null","null");
            finish();
            return;
        }

         Bitmap temp_copyBitmap = ScanUtils.resizeToScreenContentSize(copyBitmap, screen_width, actual_screen_height);

         Log.e("temp_copyBitmap height "," is "+temp_copyBitmap.getHeight());
         Log.e("temp_copyBitmap width "," is "+temp_copyBitmap.getWidth());

         Mat originalMat = new Mat(temp_copyBitmap.getHeight(), temp_copyBitmap.getWidth(), CvType.CV_8UC1);
            org.opencv.android.Utils.bitmapToMat(temp_copyBitmap, originalMat);
            ArrayList<PointF> points;
            Map<Integer, PointF> pointFs = new HashMap<>();

            try {
                Quadrilateral quad = ScanUtils.detectLargestQuadrilateral(originalMat);
                if (null != quad) {
                    double resultArea = Math.abs(Imgproc.contourArea(quad.contour));
                    double previewArea = originalMat.rows() * originalMat.cols();
                    if (resultArea > previewArea * 0.08) {
                        points = new ArrayList<>();
                        points.add(new PointF((float) quad.points[0].x, (float) quad.points[0].y));
                        points.add(new PointF((float) quad.points[1].x, (float) quad.points[1].y));
                        points.add(new PointF((float) quad.points[3].x, (float) quad.points[3].y));
                        points.add(new PointF((float) quad.points[2].x, (float) quad.points[2].y));
                    } else {
                        points = ScanUtils.getPolygonDefaultPoints(temp_copyBitmap);
                    }

                } else {
                    points = ScanUtils.getPolygonDefaultPoints(temp_copyBitmap);
                }


                int index = -1;
                for (PointF pointF : points) {
                    pointFs.put(++index, pointF);
                }

                polygonView.setPoints(pointFs);
                int padding = (int) getResources().getDimension(R.dimen.scan_padding);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(temp_copyBitmap.getWidth() + 2 * padding, temp_copyBitmap.getHeight() + 2 * padding);
                layoutParams.gravity = Gravity.CENTER;
                polygonView.setLayoutParams(layoutParams);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    TransitionManager.beginDelayedTransition(cropContainer);
                cropLayout.setVisibility(View.VISIBLE);

                cropImageView.setImageBitmap(temp_copyBitmap);
                cropImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }*/
        /*Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("is_direct_crop_activity",1);
        Bundle bundle = new Bundle();
        bundle.putParcelable("AppBitmap", Utils.getUri(DirectUploadCropScreen.this,AppController.nowbitmap));
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE);*/
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Bitmap rotatedImg = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        if(img != null){
            rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            /*rotatedImg.recycle();
            img = null;*/
        }
        return rotatedImg;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            /*int action = data.getIntExtra("intent_action",0);
            if(action == ScanActivity.CROP_YES){
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    getContentResolver().delete(uri, null, null);
                    degree = data.getExtras().getInt("degree");
                    AppController.nowbitmap = rotateImage(bitmap,degree);
                    Intent next = new Intent(DirectUploadCropScreen.this,DirectUploadPreviewScreen.class);
                    startActivity(next);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(action == ScanActivity.CROP_BACK){
                Intent i = new Intent(DirectUploadCropScreen.this,DirectUploadCameraScreen.class);
                startActivity(i);
            }
            else if(action == ScanActivity.CROP_TOOLBAR_LOGO){
                Intent i = new Intent(DirectUploadCropScreen.this,Dashboard.class);
                startActivity(i);
            }
           *//* else if(action == ScanActivity.CROP_NOTIFICATION){
                Intent i = new Intent(DirectUploadCropScreen.this,NotificationActivity.class);
                startActivity(i);
            }*//*
            else if(action == ScanActivity.CROP_NO){
                Intent i = new Intent(DirectUploadCropScreen.this,DirectUploadCameraScreen.class);
                startActivity(i);
            }*/
        }

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(TestCropScreen.this,DirectUploadCameraScreen.class);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {

                //Camera
                case 6:
                  crop_screen = 1;
                  init();
                  break;
            }

            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
        else{
            has_denied_permission = 1;
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Bind(R.id.crop_container)
    RelativeLayout cropContainer;

    @Bind(R.id.bottom_layout)
    LinearLayout bottom_layout;
//
//    @OnClick(R.id.yes_crop_layout)
//    void performCropFunction(){
//        Map<Integer, PointF> points = polygonView.getPoints();
//
//        Bitmap croppedBitmap;
//
//        if (ScanUtils.isScanPointsValid(points)) {
//
//
//            /*Point point1 = new Point(points.get(0).x, points.get(0).y);
//            Point point2 = new Point(points.get(1).x, points.get(1).y);
//            Point point3 = new Point(points.get(2).x, points.get(2).y);
//            Point point4 = new Point(points.get(3).x, points.get(3).y);*/
//
//            HashMap<Integer, Point> actualPoints = ScanUtils.getActualPoints(points,screen_width,actual_screen_height,copyBitmap.getWidth(),copyBitmap.getHeight());
//            croppedBitmap = ScanUtils.enhanceReceipt(copyBitmap, actualPoints.get(0), actualPoints.get(1), actualPoints.get(2), actualPoints.get(3));
//        } else {
//            croppedBitmap = copyBitmap;
//        }
//
//        AppController.nowbitmap = rotateImage(croppedBitmap,degree);
//        Intent next = new Intent(TestCropScreen.this,DirectUploadPreviewScreen.class);
//        startActivity(next);
//        /*String path = ScanUtils.saveToInternalMemory(croppedBitmap, com.tmsfalcon.device.tmsfalcon.customtools.ScanConstants.IMAGE_DIR,
//                com.tmsfalcon.device.tmsfalcon.customtools.ScanConstants.IMAGE_NAME, DirectUploadCropScreen.this, 90)[0];
//        setResult(Activity.RESULT_OK, new Intent().putExtra(com.tmsfalcon.device.tmsfalcon.customtools.ScanConstants.SCANNED_RESULT, path));*/
//        //bitmap.recycle();
//        System.gc();
//        //finish();
//    }

    @OnClick(R.id.no_redo_layout)
    void performRedoFunction(){
        Intent i = new Intent(TestCropScreen.this,DirectUploadCameraScreen.class);
        startActivity(i);
    }


    public native Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4);

    public native Bitmap getGrayBitmap(Bitmap bitmap);

    public native Bitmap getMagicColorBitmap(Bitmap bitmap);

    public native Bitmap getBWBitmap(Bitmap bitmap);

    public native float[] getPoints(Bitmap bitmap);
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("Scanner");
    }

}
