package com.example.haofu.prg02;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends Activity implements SensorEventListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    private TextView mTextView;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //startService(new Intent(this, SensorService.class));
    }

    @Override
    public  void onSensorChanged(SensorEvent event) {
        if (event.values[0] > 9){
            Intent cameraIntent = new Intent(this, SensorService.class)
                    .putExtra("extra?", "extra?")
                    .setAction("actions?");
            PendingIntent cameraPendingIntent = PendingIntent.getService(this,0, cameraIntent,0);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.p)
                            .setContentTitle(getString(R.string.title))
                            .setContentText(getString(R.string.content))
                            .addAction(R.drawable.camera, "Take a picture!", cameraPendingIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int a){
    }


}
