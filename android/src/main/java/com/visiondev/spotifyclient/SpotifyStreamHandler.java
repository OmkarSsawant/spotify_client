package com.visiondev.spotifyclient;

import android.graphics.Bitmap;
import android.os.Handler;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.flutter.Log;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

public class SpotifyStreamHandler implements EventChannel.StreamHandler, Subscription.EventCallback<PlayerState> {



    private PlayerState mPlayerState;

    private Spotifire spotifire;

    private EventChannel.EventSink mEventSink;

private  Map<String ,Integer> map = new HashMap<>();

    private  MethodChannel methodChannel;

  private  long prepos=0;

   private int millisecs=0;
   boolean isrunnablerunning=false;
    private boolean ispaused=false;
    private final Handler handler = new Handler();


    SpotifyStreamHandler(MethodChannel channel){
       this.methodChannel = channel;
    }


    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
         try{

             if(spotifire.isConnected() && !ispaused && mPlayerState!=null)
             {
                 if(prepos!=mPlayerState.playbackPosition){
                     //is seeked
                     map.clear();
                     millisecs =(int) mPlayerState.playbackPosition;
                     map.put("d",millisecs);
                     methodChannel.invokeMethod("music.position",map);
                 }else{
                     map.clear();
                     millisecs+=1000;
                     map.put("d",millisecs);
                     methodChannel.invokeMethod("music.position",map);
                 }

             }
             if(mPlayerState!=null) prepos = mPlayerState.playbackPosition;
             handler.postDelayed(this,1000);
         }catch (Exception e){
             e.printStackTrace();
         }

        }

    };

    void startPositionRunnable(){
        handler.post(runnable);
        isrunnablerunning = true;
    }

    void stopPositionRunnable(){
        handler.removeCallbacks(runnable);
        isrunnablerunning= false;
    }

    void setSpotifire(Spotifire spotifire) {
        this.spotifire = spotifire;
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        this.mEventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        this.mEventSink = null;
    }

    private Map<String, Object> getMap(Track track, Bitmap bitmap) {
        final Map<String, Object> musicTrack = new HashMap<>();

        musicTrack.put("name", track.name != null ? track.name : "Unknown");
        musicTrack.put("uri", track.uri != null ? track.uri : "Unknown");
        musicTrack.put("album", track.album.name != null ? track.album.name : "Unknown");
        ArrayList<Map<String, String>> _artists = new ArrayList<>();
        for (Artist artist : track.artists) {
            Map<String, String> mapartist = new HashMap<>();
            mapartist.put("name", artist.name);
            mapartist.put("uri", artist.uri);
            _artists.add(mapartist);
        }
        musicTrack.put("artists", _artists);
        musicTrack.put("duration", track.duration);
        musicTrack.put("image_bytes", spotifire.getFlutterImagebits(bitmap));

        return musicTrack;
    }


    @Override
    public void onEvent(PlayerState playerState) {


        final Track track = playerState.track;
        mPlayerState =   playerState;
        ispaused = playerState.isPaused;
        if (track != null) {
            spotifire.getImage(track.imageUri).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                @Override
                public void onResult(Bitmap bitmap) {
                    mEventSink.success(getMap(track, bitmap));
                }

            });
        }
    }
}


