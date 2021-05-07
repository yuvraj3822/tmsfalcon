package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.adapters.NewFeaturesAdapter;
import com.tmsfalcon.device.tmsfalcon.entities.NewFeatureModel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AppNewFeatures extends NavigationBaseActivity {

    NewFeaturesAdapter adapter;
    ArrayList<NewFeatureModel> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_app_new_features, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        arrayList.add(new NewFeatureModel("Added option so you can create comcheck express code in \"Get Cash\" option"));
        arrayList.add(new NewFeatureModel("Updated load listing page and so it syncs status and updates."));
        arrayList.add(new NewFeatureModel("Created new dispatch screen for each stop."));

        arrayList.add(new NewFeatureModel("Updated driver profile screen."));
        arrayList.add(new NewFeatureModel("Fix error in maintenance balance screen."));
        arrayList.add(new NewFeatureModel("Updated alerts and notifications."));
        arrayList.add(new NewFeatureModel("Fix other bugs and improved screen performance in document listing pages."));

        adapter = new NewFeaturesAdapter(AppNewFeatures.this,arrayList);
        listView.setAdapter(adapter);

    }

    @Bind(R.id.listViewFeatures)
    ListView listView;
}
