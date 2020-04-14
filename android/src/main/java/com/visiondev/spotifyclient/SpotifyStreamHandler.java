package com.visiondev.spotifyclient;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

public class SpotifyStreamHandler implements EventChannel.StreamHandler, Subscription.EventCallback<PlayerState> {



    private int posmilli=0;

    private Spotifire spotifire;

    private EventChannel.EventSink mEventSink;

private  Map<String ,Integer> map = new HashMap<>();

    private  MethodChannel methodChannel;

   boolean isrunnablerunning=false;
    private boolean ispaused=false;
    private final Handler handler = new Handler();

    SpotifyStreamHandler(MethodChannel channel){
       this.methodChannel = channel;
    }


    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
          if(spotifire.isConnected() && !ispaused)
          {
              map.clear();
              map.put("d",posmilli);
              methodChannel.invokeMethod("music.position",map);
              handler.postDelayed(this,1000);
          }
        }

    };

    void startPositionRunnable(){

        runnable.run();
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
//        Log.d("Listening",events.toString());
        this.mEventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        Log.d("cancelled", "stream");
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
        posmilli = (int)  playerState.playbackPosition;
        ispaused = playerState.isPaused;
        Log.d("milliseconds",String.valueOf(posmilli));
        if (track != null) {
            spotifire.getImage(track.imageUri).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                @Override
                public void onResult(Bitmap bitmap) {
               Log.d("Track Map",getMap(track,bitmap).toString());
                    mEventSink.success(getMap(track, bitmap));
                }

            });
        }
    }
}


