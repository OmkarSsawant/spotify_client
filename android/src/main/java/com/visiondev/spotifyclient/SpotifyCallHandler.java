package com.visiondev.spotifyclient;


import android.content.Intent;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

import static com.spotify.sdk.android.auth.AuthorizationResponse.Type.ERROR;
import static com.spotify.sdk.android.auth.AuthorizationResponse.Type.TOKEN;
import static com.spotify.sdk.android.auth.LoginActivity.REQUEST_CODE;

public class SpotifyCallHandler implements MethodChannel.MethodCallHandler, PluginRegistry.ActivityResultListener {


    private Spotifire spotifire;


    SpotifyCallHandler(Spotifire _spotifire) {
        this.spotifire = _spotifire;
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {


        switch (call.method) {
            case "loginSpotify":
                spotifire.login("155e080c3b0d482683a8a088b4a5779e");
                break;
            case "getAccessToken":
                result.success(spotifire.getSaved(Spotifire.LOGSTATE_KEY)
                );
                break;
            case "logoutSpotify":
                spotifire.logout(result);
                break;
            case "isRemoteConnected":
                //TODO:just for test
                result.success(spotifire.isConnected());
                break;
            case "connectRemote":
                spotifire.connectRemote(result);
                break;
            case "disconnectRemote":
                spotifire.disconnectRemote();
                result.success(true);
                break;
            case "playPlaylist":
                spotifire.startPlaylist(null, result);
                break;
            case "pauseMusic":
                spotifire.pause();
                result.success(true);
                break;
            case "resumeMusic":
                spotifire.resume();
                result.success(true);
                break;
            case "skipNext":
                spotifire.skipNext();
                break;
            case "skipPrevious":
                spotifire.skipPrevious();
                break;
            case "test":
                result.success(spotifire.testSP());
                break;

            default:
                result.notImplemented();
                break;
        }
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {

            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
            if (response.getType() == TOKEN) {
                // Handle successful response
                final String accessToken = response.getAccessToken();
                spotifire.test = accessToken;
                spotifire.save(Spotifire.LOGSTATE_KEY, accessToken);
                return false;
            } else if (response.getType() == ERROR) {
                spotifire.test = response.getError();
                spotifire.save(Spotifire.LOGSTATE_KEY, response.getError());
                return false;
            }
        }
        return false;
    }
}
