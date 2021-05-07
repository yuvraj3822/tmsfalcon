package com.scanlibrary;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanActivity extends AppCompatActivity implements ComponentCallbacks2, AsyncResponse {

    PolygonViewB polygonView;
//        float[] points = getPoints(tempBitmap);
//        float x1 = points[0];
//        float x2 = points[1];
//        float x3 = points[2];
//        float x4 = points[3];
//
//        float y1 = points[4];
//        float y2 = points[5];
//        float y3 = points[6];
//        float y4 = points[7];

    List<PointF> pointFs = new ArrayList<>();

    ImageView cropImageView;
    FrameLayout cropLayout;
    Bitmap copyBitmap;
    private RelativeLayout no_redo_layout,scanButton;

    Bitmap original;
//    private val REQUEST_CODE = 99

    Boolean fromNotification = false;
//
//        float[] points = getPoints(tempBitmap);
//        float x1 = points[0];
//        float x2 = points[1];
//        float x3 = points[2];
//        float x4 = points[3];
//
//        float y1 = points[4];
//        float y2 = points[5];
//        float y3 = points[6];
//        float y4 = points[7];
//
//        List<PointF> pointFs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_activiy);

        polygonView =  findViewById(R.id.polygonView);
        cropImageView = findViewById(R.id.sourceImageView);
        cropLayout = findViewById(R.id.sourceFrame);
        no_redo_layout = findViewById(R.id.no_redo_layout);
        scanButton  = findViewById(R.id.scanButton);
        copyBitmap = ScanAppController.nowbitmap;

//        setBitmap(copyBitmap);
        fromNotification = fetchIntent();

        cropLayout.post(new Runnable() {
            @Override
            public void run() {
                original = getBitmap();
                if (original != null) {
                    setBitmap(original);
                }
            }
        });

        clickListeners();

    }



    private void manageData(){

    }


    private void clickListeners(){
        no_redo_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<Integer, PointF> points = polygonView.getPoints();
                if (isScanPointsValid(points)) {
                    new ScanAsyncTask(points,ScanActivity.this).execute();
                } else {
                    showErrorDialog();
                }

            }
        });
    }

    String intent_doc_belongs_to,intent_is_expired,intent_key,intent_document_request_id,intent_document_type,intent_document_name,intent_load_number,intent_due_date,intent_comment,intent_status;


    private Boolean fetchIntent() {
        boolean dataAvailable = false;
        if (getIntent().getExtras()!=null) {
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
            dataAvailable = true;
        }else {
            intent_document_request_id = "";
            intent_document_type = "";
            intent_document_name = "";
            intent_load_number = "";
            intent_due_date = "";
            intent_comment = "";
            intent_status = "";
            intent_key = "";
            intent_is_expired = "";
            intent_doc_belongs_to = "";
            dataAvailable = false;
        }
        return dataAvailable;
    }


    private void showErrorDialog() {
        SingleButtonDialogFragment fragment = new SingleButtonDialogFragment(R.string.ok, getString(R.string.cantCrop), "Error", true);
        FragmentManager fm = getFragmentManager();
        fragment.show(fm, SingleButtonDialogFragment.class.toString());
    }


    private boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
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
        Map<Integer, PointF> pointFs = getEdgePoints(tempBitmap);
        polygonView.setPoints(pointFs);

//        polygonView.onCorners2Crops(pointFs,tempBitmap);
        polygonView.setVisibility(View.VISIBLE);
        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;
        polygonView.setLayoutParams(layoutParams);

    }




    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) {
        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
        Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
        return orderedPoints;
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
    }



    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }



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


    private Bitmap getBitmap() {
//        Uri uri = ScanAppController.nowUri;
//        try {
//            Bitmap bitmap = Utils.getBitmap(this, uri);
//            getContentResolver().delete(uri, null, null);
//            return bitmap;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;


//        fromCamera
//      byte[] p0 =   ScanAppController.nowByteArray;
//        Bitmap bitmap = BitmapFactory.decodeByteArray(p0, 0, p0.length);
//                return rotateImage(bitmap,90f);


        Bitmap finalBm = null;

        if (ScanAppController.isCameraOpen){

            byte[] p0 =   ScanAppController.nowByteArray;
            Bitmap bitmap = BitmapFactory.decodeByteArray(p0, 0, p0.length);
            finalBm = rotateImage(bitmap,90f);

        } else {

            Bitmap rotatedBitmap = null;
            Bitmap bitmap = null;

            try {
                ExifInterface ei = new ExifInterface(ScanAppController.nowUri.getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);


                bitmap =  MediaStore.Images.Media.getBitmap(getContentResolver(), ScanAppController.nowUri);

                switch (orientation){
                    case ExifInterface.ORIENTATION_ROTATE_90:{
                        rotatedBitmap = rotateImage(bitmap, 90f);
                        break;
                    }
                    case ExifInterface.ORIENTATION_ROTATE_180:{
                        rotatedBitmap = rotateImage(bitmap, 180f);
                        break;
                    }
                    case ExifInterface.ORIENTATION_ROTATE_270:{
                        rotatedBitmap = rotateImage(bitmap, 270f);
                        break;
                    }
                    case ExifInterface.ORIENTATION_NORMAL:{
                        rotatedBitmap = bitmap;
                        break;
                    }
                    case 0:{
                        rotatedBitmap = bitmap;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            finalBm =  rotatedBitmap;


        }


//        val ei = ExifInterface(data.data.path)
//                    val orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                            ExifInterface.ORIENTATION_UNDEFINED)
//
//                    var rotatedBitmap: Bitmap? = null
//                    var bitmap: Bitmap? = null
//                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
//                    rotatedBitmap = when (orientation) {
//                        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
//                        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
//                        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
//                        ExifInterface.ORIENTATION_NORMAL -> bitmap
//                        else -> bitmap
//                    }
//
//

        return finalBm;

    }



    private Bitmap rotateImage(Bitmap source, Float angle) {
        Matrix matrix =new  Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private Bitmap getScannedBitmap(Bitmap original, Map<Integer, PointF> points) {
        int width = original.getWidth();
        int height = original.getHeight();
        float xRatio = (float) original.getWidth() / cropImageView.getWidth();
        float yRatio = (float) original.getHeight() / cropImageView.getHeight();

        float x1 = (points.get(0).x) * xRatio;
        float x2 = (points.get(1).x) * xRatio;
        float x3 = (points.get(2).x) * xRatio;
        float x4 = (points.get(3).x) * xRatio;
        float y1 = (points.get(0).y) * yRatio;
        float y2 = (points.get(1).y) * yRatio;
        float y3 = (points.get(2).y) * yRatio;
        float y4 = (points.get(3).y) * yRatio;
        Log.d("", "POints(" + x1 + "," + y1 + ")(" + x2 + "," + y2 + ")(" + x3 + "," + y3 + ")(" + x4 + "," + y4 + ")");
        Bitmap _bitmap = getScannedBitmap(original, x1, y1, x2, y2, x3, y3, x4, y4);
        return _bitmap;
    }

    @Override
    public void processFinish(String output) {

//        Intent intent  = new Intent();
//        intent.putExtra("result","result");
//        setResult(Activity.RESULT_OK,intent);
//        finish();



        if (fromNotification){
            Log.e("checkNot","withdata");
            Intent next = new Intent();
            next.setClassName(this, "com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam.TestPreviewScreen");

            next.putExtra("document_request_id", intent_document_request_id);
            next.putExtra("document_type", intent_document_type);
            next.putExtra("document_name", intent_document_name);
            next.putExtra("load_number", intent_load_number);
            next.putExtra("due_date", intent_due_date);
            next.putExtra("comment", intent_comment);
            next.putExtra("status", intent_status);
            next.putExtra("key", intent_key);
            next.putExtra("is_expired", intent_is_expired);
            next.putExtra("document_belongs_to", intent_doc_belongs_to);
            startActivity(next);
            finish();
        }
        else {
            Log.e("checkNot","withoutdata-----");
            Intent intent = new Intent();
            intent.setClassName(this, "com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam.TestPreviewScreen");
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onStop() {
        dismissDialog();
        super.onStop();

    }

    private class ScanAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        private Map<Integer, PointF> points;
        private AsyncResponse response;

        public ScanAsyncTask(Map<Integer, PointF> points,AsyncResponse response) {
            this.points = points;
            this.response = response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(getString(R.string.scanning));
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap =  getScannedBitmap(original, points);
//            Uri uri = Utils.getUri(ScanActivity.this, bitmap);
//            ScanAppController.nowUri = uri;
            ScanAppController.nowbitmap = bitmap;

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
//            bitmap.recycle();
//            dismissDialog();
//
//            Intent next = new Intent(ScanActivity.this,TestPreviewScreen.class);
//            startActivity(next);


            original.recycle();
            System.gc();


            response.processFinish("result");
            // finish();

//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    getActivity().finish();
//                }
//            });


        }
    }
    protected void showProgressDialog(String message) {
        progressDialogFragment = new ProgressDialogFragment(message);
        FragmentManager fm = getFragmentManager();
        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    protected void dismissDialog() {
        if (progressDialogFragment!=null)
            progressDialogFragment.dismissAllowingStateLoss();
    }

    private ProgressDialogFragment progressDialogFragment;

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