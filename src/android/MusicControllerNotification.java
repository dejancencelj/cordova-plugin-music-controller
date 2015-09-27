package com.filfatstudios.musiccontroller;

import org.apache.cordova.CordovaInterface;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import android.util.Log;
import android.R;
import android.content.Context;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.net.Uri;

public class MusicControllerNotification {
    private Activity cordovaActivity;
    private NotificationManager notificationManager;
    private int notificationID = 0;

    public MusicControllerNotification(Activity cordovaActivity){
        //Set ID to a random ID to make sure we don't collide with another notificationID
        //TODO: Use ms & random number
        Random r = new Random();
        this.notificationID = r.nextInt(100000);
        this.cordovaActivity = cordovaActivity;
        Context context = cordovaActivity;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
	
	/*
		getBitmapFromURL(String URL)
		Stores image from URL in memory as Bitmap.
	*/
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
    private Notification.Builder createBuilder(String track, String artist, String album, String cover, boolean isPlaying){
        Context context = cordovaActivity;
        Notification.Builder builder = new Notification.Builder(context);
        
        //Configure builder
        builder.setContentTitle(track); //Title
		if(album != null && !album.isEmpty()) //Content
			builder.setContentText(artist + " - " + album);
		else
			builder.setContentText(artist);
        builder.setWhen(0); //Active now
        builder.setOngoing(true); //Disable dissmising
        builder.setPriority(Notification.PRIORITY_MAX); //Position controller at top
        
        //For Android 5.0 or higher use MediaStyle
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            builder.setStyle(new Notification.MediaStyle());
            
        //Set SmallIcon
        if (isPlaying){
            builder.setSmallIcon(R.drawable.ic_media_play);
        } else {
            builder.setSmallIcon(R.drawable.ic_media_pause);
        }
        
        //Set LargeIcon
        if (!cover.isEmpty()){
            if(cover.matches("^(https?|ftp)://.*$"))
                //Download image from remote host
                try{
                    builder.setLargeIcon(getBitmapFromURL(cover));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            else {
                //Access local image
                try {
                    Uri uri = Uri.parse(cover);
                    File file = new File(uri.getPath());
                    FileInputStream fileStream = new FileInputStream(file);
                    BufferedInputStream buf = new BufferedInputStream(fileStream);
                    Bitmap image = BitmapFactory.decodeStream(buf);
                    buf.close();
                    builder.setLargeIcon(image);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        //Open app if controller is tapped
        Intent resultIntent = new Intent(context, cordovaActivity.getClass());
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
        builder.setContentIntent(resultPendingIntent);
        
        //Handle Controls
        /* Previous */
        Intent previousIntent = new Intent("music-controller-previous");
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, 1, previousIntent, 0);
        builder.addAction(android.R.drawable.ic_media_rew, "", previousPendingIntent);
        if (isPlaying){
            /* Pause */
            Intent pauseIntent = new Intent("music-controller-pause");
            PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 1, pauseIntent, 0);
            builder.addAction(android.R.drawable.ic_media_pause, "", pausePendingIntent);
        } else {
            /* Play */
            Intent playIntent = new Intent("music-controller-play");
            PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 1, playIntent, 0);
            builder.addAction(android.R.drawable.ic_media_play, "", playPendingIntent);
        }
        /* Next */
        Intent nextIntent = new Intent("music-controller-next");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 1, nextIntent, 0);
        builder.addAction(android.R.drawable.ic_media_ff, "", nextPendingIntent);
        
        //Return the created builder
        return builder;
    }
	
	/*
	   void createMuiscController(String track, String artist, String album, String cover, boolean isPlaying)
       Create the music controller and show it
	*/
    public void createMuiscController(String track, String artist, String album, String cover, boolean isPlaying){
        Notification.Builder builder = this.createBuilder(track, artist, album, cover, isPlaying);
        Notification noti = builder.build();
        this.notificationManager.notify(this.notificationID, noti);
    }
	
	/*
		void destroy()
		Destroy the Notification
	*/
    public void destroy(){
        this.notificationManager.cancel(this.notificationID);
    }
}

