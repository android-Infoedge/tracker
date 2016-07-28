package com.example.tracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.infoedge.trackerBinder.CapturedEventsContainer;
import com.infoedge.trackerBinder.TracePath;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    @TracePath
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(this);

        CapturedEventsContainer.getInstance().setEventListSize(30);
    }

    @Override
    @TracePath
    public void onClick(View view) {

    }
}
