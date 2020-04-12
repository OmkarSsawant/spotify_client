package com.visiondev.spotifyclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;


import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;


import io.flutter.plugin.common.MethodChannel;



class Spotifire  {

    private Activity activity;
    private Context context;

    private String CLIENT_ID = "";

    private SpotifyAppRemote mspotifyAppRemote;

     static final String LOGSTATE_KEY = "isLoggedIn";
    private static final String CLIENTID_KEY = "clientId";

    private SharedPreferences sharedPreferences ;

    String test = "sink not init";

    Spotifire(Context _context) {
        this.context = _context;
        sharedPreferences =context.getSharedPreferences(null,Context.MODE_PRIVATE);

    }

    private static final String REDIRECT_URI = "spotify-sdk://auth";
    private static final int REQUEST_CODE = 1337;

    private static MethodChannel.Result mRemoteresult;


    void setActivity(Activity _activity) {
        this.activity = _activity;
    }

    void login(String _CLIENT_ID) {
        this.CLIENT_ID = _CLIENT_ID;
        if (activity != null) {
            AuthorizationRequest.Builder builder =
                    new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

            builder.setScopes(new String[]{"streaming"});
            AuthorizationRequest request = builder.build();
            AuthorizationClient.openLoginActivity(this.activity, REQUEST_CODE, request);
        }
    }


    void logout(MethodChannel.Result result) {

        if (this.activity != null && this.context != null) {
            AuthorizationClient.clearCookies(this.context);
            result.success(true);
        } else {
            result.success(false);
        }

    }


    void connectRemote(MethodChannel.Result result) {
        mRemoteresult = result;
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(getRedirectUri().toString())
                .showAuthView(true)
                .build();


        SpotifyAppRemote.connect(this.context, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mspotifyAppRemote = spotifyAppRemote;
                mRemoteresult.success(true);
                Log.d("MainActivity", "Connected! Yay!");
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (throwable instanceof NotLoggedInException || throwable instanceof UserNotAuthorizedException) {
                    // Show login button and trigger the login flow from auth library when clicked
                    if (CLIENT_ID != null) {
                        login(CLIENT_ID);
                    }
                } else if (throwable instanceof CouldNotFindSpotifyApp) {
                    // Show button to download Spotify
                    mRemoteresult.error("NoSpotifyError", throwable.getMessage(), throwable);
                }
            }
        });

    }


    void save(String val,String key){

        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(val,key);
    }

    String getSaved(String key){
    return   sharedPreferences.getString(key,"default");
    }

    void disconnectRemote() {
        if (isConnected())
            SpotifyAppRemote.disconnect(mspotifyAppRemote);
    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    boolean isConnected() {
        if (mspotifyAppRemote != null) return mspotifyAppRemote.isConnected();

        return false;
    }

    void startPlaylist(String playlistid, MethodChannel.Result result) {
        if (playlistid == null) playlistid = "spotify:playlist:37i9dQZF1DX3rxVfibe1L0";

        if (isConnected()) {
            // Play a playlist
            mspotifyAppRemote.getPlayerApi().play(playlistid);

            // Subscribe to PlayerState
            mspotifyAppRemote.getPlayerApi()
                    .subscribeToPlayerState()
                    .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                        @Override
                        public void onEvent(PlayerState playerState) {
                            final Track track = playerState.track;
                            if (track != null) {
                                Log.d("MainActivity", track.name + " by " + track.artist.name);
                                Log.d("PlayerStateURI", track.uri);
                                Log.d("PlayerStateImgUri", track.imageUri.raw);
                            }
                        }
                    });
            result.success(true);
        } else {
            connectRemote(result);
        }
    }

    void pause() {
        if (mspotifyAppRemote != null) mspotifyAppRemote.getPlayerApi().pause();
    }

    void resume() {
        if (mspotifyAppRemote != null) mspotifyAppRemote.getPlayerApi().resume();

    }

    void skipNext() {
        if (mspotifyAppRemote != null) mspotifyAppRemote.getPlayerApi().skipNext();
    }

    void skipPrevious() {
        if (mspotifyAppRemote != null) mspotifyAppRemote.getPlayerApi().skipPrevious();

    }


    String testSP() {
        return test;
    }
    //TODO: implement repeat





private int testCount=0;

     int getTestCount() {
        return testCount;
    }

}
