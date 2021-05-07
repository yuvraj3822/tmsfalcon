package com.tmsfalcon.device.tmsfalcon;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tmsfalcon.device.tmsfalcon.activities.AboutUsActivity;
import com.tmsfalcon.device.tmsfalcon.activities.CompanyDispatcherActivity;
import com.tmsfalcon.device.tmsfalcon.activities.DashboardActivity;
import com.tmsfalcon.device.tmsfalcon.activities.DayOffRequestActivity;
import com.tmsfalcon.device.tmsfalcon.activities.EscrowAccountsActivity;
import com.tmsfalcon.device.tmsfalcon.activities.FuelRebateActivity;
import com.tmsfalcon.device.tmsfalcon.activities.GetCashActivity;
import com.tmsfalcon.device.tmsfalcon.activities.LoadBaordActivity;
import com.tmsfalcon.device.tmsfalcon.activities.LoanActivity;
import com.tmsfalcon.device.tmsfalcon.activities.NotificationActivity;
import com.tmsfalcon.device.tmsfalcon.activities.PhoneActivity;
import com.tmsfalcon.device.tmsfalcon.activities.ReportIssueActivity;
import com.tmsfalcon.device.tmsfalcon.activities.ReportedAccidentList;
import com.tmsfalcon.device.tmsfalcon.activities.SettlementListActivity;
import com.tmsfalcon.device.tmsfalcon.activities.TrailerActivity;
import com.tmsfalcon.device.tmsfalcon.activities.TruckActivity;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.ReportedAccidentListK;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.accidentIntro.AccidentIntro;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam.TestCameraScreen;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ConnectivityReceiver;
import com.tmsfalcon.device.tmsfalcon.customtools.GpsApiCalls;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.database.DirectUploadTable;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.RequestBody;

public class NavigationBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ConnectivityReceiver.ConnectivityReceiverListener {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    SessionManager session;
    NetworkValidator networkValidator;
    String driver_name;
    int driver_id;
    TextView driver_nameEditText;
    CircleImageView driver_imageView,header_image;
    String image_name,root,full_image_path;
    Button go_to_profile;
    TextView header_driverNameTextView,header_driverEmailTextView;
    AppBarLayout appBarLayout;
    ImageView bell_icon,back_button;
    public FrameLayout frameLayoutBell;
    DirectUploadTable db;
    PermissionManager permissionsManager = new PermissionManager();
    static final Integer CALL = 0x2;
    static final int FINE_LOCATION = 0x3;
    static final int COARSE_LOCATION = 0x4;
    Drawable drawable;
    public DrawerLayout drawer;
    NavigationView navigationView;
    Menu menuNavigationActivity;
    public ImageView cropRotate;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_base);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // ButterKnife.bind(NavigationBaseActivity.this);
        initIdS();
        //cropRotate.setVisibility(View.GONE);
        /*Log.e("back button", String.valueOf(back_button.getVisibility()));
        if(back_button.getVisibility() == View.GONE || back_button.getVisibility() == View.INVISIBLE){
            Log.e("in","back button gone");
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)frameLayoutBell.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);

            frameLayoutBell.setLayoutParams(params);
            frameLayoutBell.setVisibility(View.GONE);
        }*/
        if(session.getDriverGender() == "male"){
            drawable = getResources().getDrawable(R.drawable.driver_male);
        }
        else{
            drawable = getResources().getDrawable(R.drawable.driver_female);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(appBarLayout, "elevation", 0.1f));
            appBarLayout.setStateListAnimator(stateListAnimator);
        }

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if( navigationView != null ){
            LinearLayout mParent = ( LinearLayout ) navigationView.getHeaderView( 0 );

            if( mParent != null ){
                header_image = mParent.findViewById(R.id.profile_header_image);
                header_driverNameTextView = mParent.findViewById(R.id.driver_name);
                header_driverEmailTextView = mParent.findViewById(R.id.driver_email);
                header_driverNameTextView.setText(session.get_driver_fullname());
                header_driverEmailTextView.setText("#"+session.get_driver_uid());
            }
        }
       /* if(networkValidator.isNetworkConnected()){
            Log.e("in","Navigation base activity "+AppController.LOAD_ACCIDENT_DATA);
            AccidentModuleCalls accidentModuleCalls = new AccidentModuleCalls(NavigationBaseActivity.this);
            accidentModuleCalls.sendAccidentData();
        }*/
    }

    public void setVisibilityForLoan(int loan_count){
        if(loan_count <= 0){
            Log.e("in","loan count 0 : "+loan_count);
            navigationView.getMenu().findItem(R.id.loans).setVisible(false);
        }
        else {
            Log.e("in","loan count not 0 : "+loan_count);
            navigationView.getMenu().findItem(R.id.loans).setVisible(true);
        }


    }

    @SuppressWarnings("unused")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    File img_file = new File(full_image_path);

                    /*if(img_file.exists()){
                        displayDriverThumb(img_file);
                    }
                    else{
                        Log.e(TAG,"image not exists");
                        downloadDriverThumb();
                    }*/

                } else {
                    Toast.makeText(NavigationBaseActivity.this,"You need Storage Permission to Access Files.",Toast.LENGTH_LONG).show();
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request

        }
    }

    public void displayDriverPic(){

        String image_url = UrlController.HOST+session.get_driver_thumb();
      /*  Picasso.with(NavigationBaseActivity.this)
                .load(image_url)
                .error(drawable)
                .into(header_image);*/
        Glide.with(NavigationBaseActivity.this)
                .load(image_url)
                .error(drawable)
                .into(header_image);
    }

    private void initIdS() {

        session = new SessionManager(NavigationBaseActivity.this);
        driver_id = session.get_driver_id();
        image_name = driver_id+"_thumb"+ ".jpg";
        root = Environment.getExternalStorageDirectory().toString();
        full_image_path = root+"/tmsfalcon/"+image_name;
        networkValidator = new NetworkValidator(NavigationBaseActivity.this);
        appBarLayout = findViewById(R.id.app_bar);
        back_button = findViewById(R.id.back_btn);
        frameLayoutBell = findViewById(R.id.bell_icon_custom);
        cropRotate =findViewById(R.id.crop_rotate);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds gitems to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.navigation_base, menu);

        return true;
    }

    @Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        //Log.e("count ",""+SessionManager.getInstance().getNotificationCount());
        String count = session.getNotificationCount();
        if(count != null && ((Integer.parseInt(count)) > 0)) {
            cartBadgeTextView.setText(""+count);
        }
        else{
            cartBadgeTextView.setVisibility(View.GONE);
            //textview.setText("");
        }
        //Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());
        if(networkValidator.isNetworkConnected()){
            displayDriverPic();
        }
        AppController.getInstance().setConnectivityListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        item.setChecked(true);
        int id = item.getItemId();
        displaySelectedScreen(id);
        return true;
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.resources_trucks:
                Intent startTruckActivity = new Intent(NavigationBaseActivity.this, TruckActivity.class);
                startActivity(startTruckActivity);
                finish();
                break;
            case R.id.resources_trailers:
                Intent startTrailerActivity = new Intent(NavigationBaseActivity.this, TrailerActivity.class);
                startActivity(startTrailerActivity);
                finish();
                break;
            case R.id.profile:
                Intent profile = new Intent(NavigationBaseActivity.this, Profile.class);
                startActivity(profile);
                finish();
                break;
            case R.id.dashboard:
                Intent dashboard = new Intent(NavigationBaseActivity.this, DashboardActivity.class);
                startActivity(dashboard);
                finish();
                break;
            case R.id.driver_settlements:
                Intent driver_settlements = new Intent(NavigationBaseActivity.this, SettlementListActivity.class);
                startActivity(driver_settlements);
                finish();
                break;
            /* case R.id.company:
                Intent startCompanyActivity = new Intent(Dashboard.this, CompanyActivity.class);
                startActivity(startCompanyActivity);
                break;*/
            case R.id.dispatcher:
                /*Intent startDispatcherActivity = new Intent(Dashboard.this, DispatcherActivity.class);
                startActivity(startDispatcherActivity);*/
                Intent startDispatcherActivity = new Intent(NavigationBaseActivity.this, CompanyDispatcherActivity.class);
                startDispatcherActivity.putExtra("open_fragment",0);
                startActivity(startDispatcherActivity);
                finish();
                break;
//            case R.id.day_off_request:
//                Intent startDayOffActivity = new Intent(NavigationBaseActivity.this, DayOffRequestActivity.class);
//                startActivity(startDayOffActivity);
//                finish();
//                break;
            case R.id.loans:
                Intent i = new Intent(NavigationBaseActivity.this, LoanActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.report_accident:
                Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show();
//                Intent reported_accidents_intent = new Intent(NavigationBaseActivity.this, ReportedAccidentListK.class);
//                startActivity(reported_accidents_intent);
//                finish();
                break;
            case R.id.fuel_rebates:
                Intent startFuelRebartActivity = new Intent(NavigationBaseActivity.this,FuelRebateActivity.class);
                startActivity(startFuelRebartActivity);
                finish();
                break;
           /* case R.id.document_requests:
                Intent documentRequestActivity = new Intent(NavigationBaseActivity.this, RequestedDocumentsActivity.class);
                startActivity(documentRequestActivity);
                break;
            case R.id.uploaded_documents:
                Intent uploadedDocumentIntent = new Intent(NavigationBaseActivity.this, UploadedDocumentsActivity.class);
                startActivity(uploadedDocumentIntent);
                break;*/
//            case R.id.direct_upload:
//                db = new DirectUploadTable(NavigationBaseActivity.this);
//                db.deleteAllRecords();
//                Intent directUpload = new Intent(NavigationBaseActivity.this, TestCameraScreen.class);
//                startActivity(directUpload);
//                finish();
//                break;
            case R.id.logout:
                //Log.e("in","logout case");
                Utils.logoutApi(NavigationBaseActivity.this);
               /* SessionManager sessionManager = new SessionManager(Dashboard.this);
                sessionManager.logoutUser();*/
                break;
            case R.id.phone:
                Intent phoneScreen = new Intent(NavigationBaseActivity.this,PhoneActivity.class);
                startActivity(phoneScreen);
                finish();
                break;
//            case R.id.call_log_detail:
//                Intent call_log_detail = new Intent(NavigationBaseActivity.this,CallLogDetail.class);
//                startActivity(call_log_detail);
//                break;
//            case R.id.load_board:
//                Intent load_board = new Intent(NavigationBaseActivity.this, LoadBaordActivity.class);
//                startActivity(load_board);
//                finish();
//                break;

                /*if(AppController.first_time_login == 1){
                    Intent load_board = new Intent(NavigationBaseActivity.this, LoadBoardPreferences.class);
                    startActivity(load_board);
                }
                else{
                    Intent load_board = new Intent(NavigationBaseActivity.this, LoadBaordActivity.class);
                    startActivity(load_board);
                }*/

            case R.id.escrow_accounts:
                Intent escrow_accounts = new Intent(NavigationBaseActivity.this, EscrowAccountsActivity.class);
                startActivity(escrow_accounts);
                finish();
                break;
            case R.id.get_cash:
                 Intent get_cash = new Intent(NavigationBaseActivity.this, GetCashActivity.class);
                startActivity(get_cash);
                finish();
                break;
           /*   case R.id.rc_embed:
                Intent rc_embed = new Intent(NavigationBaseActivity.this, RcEmbed.class);
                startActivity(rc_embed);
                break;*/
            case R.id.about_us:
                Intent about_us = new Intent(NavigationBaseActivity.this, AboutUsActivity.class);
                startActivity(about_us);
                finish();
                break;
//            case R.id.report_issue:
//                Intent report_issue = new Intent(NavigationBaseActivity.this, ReportIssueActivity.class);
//                startActivity(report_issue);
//                finish();
//                break;

            /*case R.id.offline_reported_accidents:
                Intent offline_reported_accidents = new Intent(NavigationBaseActivity.this, OfflineReportedAccident.class);
                startActivity(offline_reported_accidents);
                break;*/
           /* case R.id.chat_design:
                Intent chatScreen = new Intent(NavigationBaseActivity.this,ChatScreen.class);
                startActivity(chatScreen);
                break;*/

           /* case R.id.contacts:
                Intent contactScreen = new Intent(NavigationBaseActivity.this,ContactsActivity.class);
                startActivity(contactScreen);
                break;*/
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Bind(R.id.cart_badge)
    public TextView cartBadgeTextView;
    @Bind(R.id.back_btn)
    public ImageView back_btn;

    @Bind(R.id.bell_layout)
    public RelativeLayout bell_layout;

    @OnClick(R.id.bell_icon_custom)
    void bell_functionality(){
        Intent i = new Intent(NavigationBaseActivity.this, NotificationActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.toolbar_logo)
    void dashboard_func(){
        Log.e("in","dashboard_fun");
        Utils.goToDashboard(NavigationBaseActivity.this);
    }

    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            AppController.data_load = false;
            message = "Good! Connected to Internet";


            color = Color.WHITE;
            RequestBody requestBody = GpsApiCalls.convertMultipleLocations(NavigationBaseActivity.this);
            //GpsApiCalls.sendLocation(NavigationBaseActivity.this,requestBody,true);


        } else {
            AppController.data_load = true;
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

        /*Snackbar snackbar = Snackbar
                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();*/
    }

}
