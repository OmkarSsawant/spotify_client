package com.visiondev.spotifyclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;


import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.ImageUri;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

import static com.spotify.sdk.android.auth.AuthorizationResponse.Type.ERROR;
import static com.spotify.sdk.android.auth.AuthorizationResponse.Type.TOKEN;


class Spotifire implements PluginRegistry.ActivityResultListener {

    private Activity activity;
    private Context context;

    private String CLIENT_ID = "";

    private SpotifyAppRemote mspotifyAppRemote;

    private SpotifyStreamHandler mSpotifyStreamHandler;


    Spotifire(Context _context, SpotifyStreamHandler mStreamHandler) {
        this.context = _context;
        this.mSpotifyStreamHandler = mStreamHandler;

    }

    private static final String REDIRECT_URI = "spotify-sdk://auth";
    private static final int REQUEST_CODE = 1337;


    private Map<String, String> mResponce = new HashMap<>();


     Map<String, String> getmResponce() {
        return mResponce;
    }

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

    void disconnectRemote() {
        if (isConnected()) {
            SpotifyAppRemote.disconnect(mspotifyAppRemote);
            if (mSpotifyStreamHandler.isrunnablerunning) {
                mSpotifyStreamHandler.stopPositionRunnable();
            }
        }
    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    boolean isConnected() {
        if (mspotifyAppRemote != null) return mspotifyAppRemote.isConnected();

        return false;
    }



    List<Integer> getFlutterImagebits(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        ArrayList<Integer> bytesInt = new ArrayList<>();
        for (Byte b : byteArrayOutputStream.toByteArray()
        ) {
            bytesInt.add(b.intValue());
        }
        return bytesInt;
    }

    void seekTo(int pos) {

        mspotifyAppRemote.getPlayerApi().seekTo((long) pos);
    }

    void setRepeat(int repeat) {
        mspotifyAppRemote.getPlayerApi().setRepeat(repeat);
    }

    void queue(String nplaylistURI) {
        mspotifyAppRemote.getPlayerApi().queue(nplaylistURI);
    }

    CallResult<Bitmap> getImage(ImageUri imageuri) {
        return mspotifyAppRemote.getImagesApi().getImage(imageuri);
    }

    void startPlaylist(String playlistid, MethodChannel.Result result) {
        if (playlistid == null) playlistid = "spotify:playlist:37i9dQZF1DX3rxVfibe1L0";


        if (isConnected()) {
            // Play a playlist
            mspotifyAppRemote.getPlayerApi().play(playlistid).setResultCallback(new CallResult.ResultCallback<Empty>() {
                @Override
                public void onResult(Empty empty) {
                    mSpotifyStreamHandler.startPositionRunnable();
                }
            });

            // Subscribe to PlayerState
            mspotifyAppRemote.getPlayerApi()
                    .subscribeToPlayerState()
                    .setEventCallback(mSpotifyStreamHandler);
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


    //TODO: implement repeat


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {

            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
            if (response.getType() == TOKEN) {
                // Handle successful response
                String accessToken = response.getAccessToken();
                mResponce.clear();
                mResponce.put("accessToken", accessToken);

                return false;
            } else if (response.getType() == ERROR) {
                mResponce.clear();
                mResponce.put("error", response.getError());

                return false;
            } else {
                mResponce.clear();
                mResponce.put("error", "Authentication Cancelled");

            }
        }
        return false;
    }
}
