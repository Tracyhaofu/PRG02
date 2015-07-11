package com.example.haofu.prg02;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
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
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;


/**
 * Created by haofu on 7/8/15.
 */

public class SensorService extends IntentService {

        private GoogleApiClient mGoogleApiClient;
        private static final String CAPABILITY_NAME = "do_stuff";
        private static final String RECEIVER_SERVICE_PATH = "/receiver-service";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SensorService() {
        super("string");
    }

    @Override
        protected void onHandleIntent(Intent intent) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i("H", "SensorService start");
                    SensorService.this.mGoogleApiClient = new GoogleApiClient.Builder(SensorService.this)
                            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    // Do something
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // Do something
                                }
                            })
                            .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(ConnectionResult connectionResult) {
                                    // Do something
                                }
                            })
                            .addApi(Wearable.API)
                            .build();
                    SensorService.this.mGoogleApiClient.connect();

                    CapabilityApi.GetCapabilityResult capResult =
                            Wearable.CapabilityApi.getCapability(
                                    mGoogleApiClient, CAPABILITY_NAME,
                                    CapabilityApi.FILTER_REACHABLE)
                                    .await();
                    for (Node n :capResult.getCapability().getNodes()){
                        Wearable.MessageApi.sendMessage(
                                mGoogleApiClient, n.getId(),
                                RECEIVER_SERVICE_PATH, new byte[3]);
                    }
                }
            }).start();
        }

        @Override
        public IBinder onBind(Intent intent) {
        // Return a binder to this service
            return null;
        }


    }

