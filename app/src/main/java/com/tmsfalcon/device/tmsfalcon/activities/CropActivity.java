package com.tmsfalcon.device.tmsfalcon.activities;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.transition.TransitionManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.ScanUtils;
import com.tmsfalcon.device.tmsfalcon.widgets.PolygonView;
import com.tmsfalcon.device.tmsfalcon.widgets.Quadrilateral;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CropActivity extends NavigationBaseActivity {

    String intent_doc_belongs_to,intent_is_expired,intent_key,intent_document_request_id,intent_document_type,intent_document_name,intent_load_number,intent_due_date,intent_comment,intent_status;
    int REQUEST_CODE = 99;
    int degree = 0;
    PermissionManager permissionsManager = new PermissionManager();
    static final Integer WRITE_EXTERNAL_STORAGE = 0x6;
    int crop_screen = 0;
    int has_denied_permission = 0;

    private PolygonView polygonView;
    private ImageView cropImageView;
    private FrameLayout cropLayout;
    Bitmap copyBitmap;
    private static final String TAG = CropActivity.class.getSimpleName();
    int actual_screen_height;
    int screen_width;
    boolean file_imported_from_gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_crop, null, false);
        drawer.addView(contentView, 0);
        //setContentView(R.layout.activity_crop);
        ButterKnife.bind(this);
        cropRotate.setVisibility(View.VISIBLE);
        file_imported_from_gallery = getIntent().getBooleanExtra("imported_from_gallery",false);
        if(crop_screen == 0){
            if (!permissionsManager.checkPermission(CropActivity.this, CropActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if(has_denied_permission == 0){ //To prevent continous asking permission alert dialog
                    permissionsManager.askForPermission(CropActivity.this, CropActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
                }
            }
            else {
                if(file_imported_from_gallery){
                    init_for_imported_file();
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

    @Override
    protected void onResume() {
        super.onResume();
        if(crop_screen == 0){
            if (!permissionsManager.checkPermission(CropActivity.this, CropActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if(has_denied_permission == 0){ //To prevent continous asking permission alert dialog
                    permissionsManager.askForPermission(CropActivity.this, CropActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
                }
            }
            else {
                init();
            }
        }
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

    public void init(){
        fetchIntent();
        crop_screen = 1;

        polygonView = findViewById(R.id.polygon_view);
        cropImageView = findViewById(R.id.crop_image_view);
        cropLayout = findViewById(R.id.crop_layout);


        copyBitmap = AppController.nowbitmap;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        screen_width = displayMetrics.widthPixels;

        //get action bar height
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);

        //get height of bottom layout
        int bottom_layout_height = bottom_layout.getHeight();

        actual_screen_height = height - (actionBarHeight+bottom_layout_height);


        Bitmap temp_copyBitmap = ScanUtils.resizeToScreenContentSize(copyBitmap, screen_width, actual_screen_height);

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
        }
        /*Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("is_direct_crop_ activity",1);
        Bundle bundle = new Bundle();
        bundle.putParcelable("AppBitmap", Utils.getUri(DirectUploadCropScreen.this,AppController.nowbitmap));
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE);*/
    }

    public void init_for_imported_file(){
        fetchIntent();
        crop_screen = 1;

        polygonView = findViewById(R.id.polygon_view);
        cropImageView = findViewById(R.id.crop_image_view);
        cropLayout = findViewById(R.id.crop_layout);


        copyBitmap = AppController.nowbitmap;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        screen_width = displayMetrics.widthPixels;

        //get action bar height
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);

        //get height of bottom layout
        int bottom_layout_height = bottom_layout.getHeight();

        actual_screen_height = height - (actionBarHeight+bottom_layout_height);


        Bitmap temp_copyBitmap = copyBitmap;

        Mat originalMat = new Mat(temp_copyBitmap.getHeight(), temp_copyBitmap.getWidth(), CvType.CV_8UC1);
        org.opencv.android.Utils.bitmapToMat(temp_copyBitmap, originalMat);
        ArrayList<PointF> points;
        Map<Integer, PointF> pointFs = new HashMap<>();
        try {
            points = ScanUtils.getPolygonDefaultPoints(temp_copyBitmap);

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
        }
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
    private void fetchIntent() {
        intent_document_request_id = getIntent().getExtras().getString("document_request_id");
        intent_document_type = getIntent().getExtras().getString("document_type");
        intent_document_name = getIntent().getExtras().getString("document_name");
        intent_load_number = getIntent().getExtras().getString("load_number");
        intent_due_date = getIntent().getExtras().getString("due_date");
        intent_comment = getIntent().getExtras().getString("comment");
        intent_status = getIntent().getExtras().getString("status");
        intent_key = getIntent().getExtras().getString("key");
        intent_is_expired = getIntent().getExtras().getString("is_expired");
        intent_doc_belongs_to = getIntent().getExtras().getString("document_belongs_to");
    }

    @Bind(R.id.crop_container)
    RelativeLayout cropContainer;

    @Bind(R.id.bottom_layout)
    LinearLayout bottom_layout;

    @OnClick(R.id.yes_crop_layout)
    void performCropFunction(){
        Map<Integer, PointF> points = polygonView.getPoints();

        Bitmap croppedBitmap;

        if (ScanUtils.isScanPointsValid(points)) {
            /*Point point1 = new Point(points.get(0).x, points.get(0).y);
            Point point2 = new Point(points.get(1).x, points.get(1).y);
            Point point3 = new Point(points.get(2).x, points.get(2).y);
            Point point4 = new Point(points.get(3).x, points.get(3).y);*/
            HashMap<Integer, Point> actualPoints = ScanUtils.getActualPoints(points,screen_width,actual_screen_height,copyBitmap.getWidth(),copyBitmap.getHeight());
            croppedBitmap = ScanUtils.enhanceReceipt(copyBitmap, actualPoints.get(0), actualPoints.get(1), actualPoints.get(2), actualPoints.get(3));
        } else {
            croppedBitmap = copyBitmap;
        }

        AppController.nowbitmap = rotateImage(croppedBitmap,degree);
        Intent next = new Intent(CropActivity.this,PreviewActivity.class);
        next.putExtra("document_request_id",intent_document_request_id);
        next.putExtra("document_type",intent_document_type);
        next.putExtra("document_name",intent_document_name);
        next.putExtra("load_number",intent_load_number);
        next.putExtra("due_date",intent_due_date);
        next.putExtra("comment",intent_comment);
        next.putExtra("status",intent_status);
        next.putExtra("key",intent_key);
        next.putExtra("is_expired",intent_is_expired);
        next.putExtra("document_belongs_to",intent_doc_belongs_to);
        startActivity(next);
        System.gc();
    }

    @OnClick(R.id.no_redo_layout)
    void performRedoFunction(){
        Intent i = new Intent(CropActivity.this,CaptureDocument.class);
        startActivity(i);
    }

    @OnClick(R.id.back_btn)
    void PreviousScreen(){
        //super.onBackPressed();
        Intent next = new Intent(CropActivity.this,CaptureDocument.class);
        next.putExtra("document_request_id",intent_document_request_id);
        next.putExtra("document_type",intent_document_type);
        next.putExtra("document_name",intent_document_name);
        next.putExtra("load_number",intent_load_number);
        next.putExtra("due_date",intent_due_date);
        next.putExtra("comment",intent_comment);
        next.putExtra("status",intent_status);
        next.putExtra("key",intent_key);
        next.putExtra("is_expired",intent_is_expired);
        next.putExtra("document_belongs_to",intent_doc_belongs_to);
        startActivity(next);
    }


    @Override
    public void onBackPressed() {
        Intent next = new Intent(CropActivity.this,CaptureDocument.class);
        next.putExtra("document_request_id",intent_document_request_id);
        next.putExtra("document_type",intent_document_type);
        next.putExtra("document_name",intent_document_name);
        next.putExtra("load_number",intent_load_number);
        next.putExtra("due_date",intent_due_date);
        next.putExtra("comment",intent_comment);
        next.putExtra("status",intent_status);
        next.putExtra("key",intent_key);
        next.putExtra("is_expired",intent_is_expired);
        next.putExtra("document_belongs_to",intent_doc_belongs_to);
        startActivity(next);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissions != null){
            if(permissions.length > 0 && permissions[0] != null){
                if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
                    switch (requestCode) {

                        //Camera
                        case 6:
                            crop_screen = 1;
                            init();
                            break;

                    }

                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }else{
                    has_denied_permission = 1;
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

}
