package com.example.haofu.prg02;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by haofu on 7/10/15.
 */
public class sendingService extends IntentService {


    private GoogleApiClient mGoogleApiClient;
    private static final String CAPABILITY_NAME = "send_stuff";
    private static final String RECEIVER_SERVICE_PATH = "/send-service";

    public sendingService() {
        super("send back");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
                Log.i("H", "SendService start");
                this.mGoogleApiClient = new GoogleApiClient.Builder(this)
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

                this.mGoogleApiClient.connect();

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
        }

