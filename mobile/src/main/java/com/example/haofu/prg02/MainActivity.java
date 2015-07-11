package com.example.haofu.prg02;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import android.content.Intent;
import android.widget.ImageView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "mSNKtPntGN1GNOHGMm3UDnkK1";
    private static final String TWITTER_SECRET = "jm4kfRiiHLkY6HIVnnnLQcG7pH76rRwbXbsqX6bEp2CpDFvyxf";

    private TwitterLoginButton loginButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_COMPOSER = 100;
    private Uri photo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("open camera"));
        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());
        Fabric.with(this, new TwitterCore(authConfig));
    }


    public void composeTweet(){
        Intent intent = new TweetComposer.Builder(this)
                .text("#cs160excited").image(photo)
                .createIntent();
        startActivityForResult(intent, REQUEST_IMAGE_COMPOSER);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loginButton.onActivityResult(requestCode, resultCode, data);
        Log.i("F", "before compose");
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            composeTweet();
        } else if (requestCode == REQUEST_IMAGE_COMPOSER ){
            Log.i("F", "after compose");
            getNextTweet();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
    String url;
    protected void getNextTweet() {
        new Thread() {
            @Override
            public void run() {
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                SearchService searchService = twitterApiClient.getSearchService();
                searchService.tweets("#cs160excited", null, null, null, "recent", 5, null, null, null, true, new Callback<Search>(){
                    @Override
                    public void success(final Result<Search> result) {
                        for (Tweet tweet : result.data.tweets) {
                            if (tweet.entities.media != null) {
                                url = tweet.entities.media.get(0).mediaUrl;
                                break;
                            }
                        }
                        try {
                            URL i = new URL(url);
                            Bitmap bmp = BitmapFactory.decodeStream(i.openConnection().getInputStream());
                            createNotification(bmp);
                        } catch (IOException ex) {
                        }
                    }

                    public void failure(TwitterException exception) {
                    }
                });
            }
        }.start();
    }

    protected void createNotification(Bitmap p){
        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent view = PendingIntent.getActivity(this, 0, mainIntent, 0);

        NotificationCompat.WearableExtender extender = new NotificationCompat.WearableExtender();
        extender.setBackground(p);
        extender.setHintHideIcon(true);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Someone else ")
                        .setContentText("excited see it")
                        .setContentIntent(view);
        notificationBuilder.extend(extender);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_LOW);
        notificationBuilder.setLargeIcon(p);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            photo = Uri.fromFile(photoFile);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }



    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dispatchTakePictureIntent();
        }
    };

}
