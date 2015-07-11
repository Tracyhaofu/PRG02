package com.example.haofu.prg02;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by haofu on 7/9/15.
 */
public class WearService extends WearableListenerService {

    private static final String RECEIVER_SERVICE_PATH = "/receiver-service";
    private static final String TAG = "HF";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i(TAG, "receive from watch");
        Intent cameraIntent = new Intent("open camera");
        broadCastHelper(cameraIntent);
        }

    public void broadCastHelper(Intent intent){
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
