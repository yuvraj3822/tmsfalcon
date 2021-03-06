package com.tmsfalcon.device.tmsfalcon;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;

/**
 * Implementation of App Widget functionality.
 */
public class TmsAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.e("TmsAppWidget","in updateAppWidget method");
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        String pickup_location = SessionManager.getInstance().getKeyWidgetLoadPickupLocation();
        String delivery_location = SessionManager.getInstance().getKeyWidgetLoadDeliveryLocation();
        String settlement_amount = SessionManager.getInstance().getKeyWidgetSettlementAmount();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tms_app_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        if(pickup_location != null){
            views.setTextViewText(R.id.pickup_location,pickup_location);
        }
        if(delivery_location != null){
            views.setTextViewText(R.id.delivery_location,delivery_location);
        }
        if(settlement_amount != null){
            views.setTextViewText(R.id.settlement_amount,settlement_amount);
        }

        /** PendingIntent to launch the MainActivity when the widget was clicked **/
        /*Intent launchMain = new Intent(context, MainActivity.class);
        PendingIntent pendingMainIntent = PendingIntent.getActivity(context, 0, launchMain, 0);
        views.setOnClickPendingIntent(R.id.wid, pendingMainIntent);*/

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

