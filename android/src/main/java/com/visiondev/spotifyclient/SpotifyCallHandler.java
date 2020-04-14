package com.visiondev.spotifyclient;


import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;


public class SpotifyCallHandler implements MethodChannel.MethodCallHandler {


    private Spotifire spotifire;


    SpotifyCallHandler(Spotifire _spotifire) {
        this.spotifire = _spotifire;
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {


        switch (call.method) {
            case "loginSpotify":
                spotifire.login((String) call.argument("client_id"));
                result.success(null);
                break;
            case "getAccessToken":
                Map<String, String> res = spotifire.getmResponce();
                if (res.containsKey("accessToken")) {
                    result.success(res.get("accessToken"));
                } else if (res.containsKey("error")) {
                    result.error("AuthError", res.get("error"), res);
                } else {
                    result.success(res.toString());
                }
                break;
            case "logoutSpotify":
                spotifire.logout(result);
                break;
            case "isRemoteConnected":
                //TODO:just for test
                result.success(spotifire.isConnected());
                break;
            case "connectRemote":
                if (spotifire.isConnected()) {
                    result.success(true);
                } else {
                    spotifire.connectRemote(result);
                }
                break;
            case "disconnectRemote":
                spotifire.disconnectRemote();
                result.success(true);
                break;
            case "playPlaylist":
                spotifire.startPlaylist((String) call.argument("playListUri"), result);
                result.success(null);
                break;
            case "pauseMusic":
                if(spotifire.isConnected()){
                    spotifire.pause();
                    result.success(true);
                }else{
                    result.success(false);
                }
                break;
            case "resumeMusic":
                if(spotifire.isConnected()){
                    spotifire.resume();
                    result.success(true);
                }else{
                    result.success(false);
                }
                break;
            case "skipNext":
                if(spotifire.isConnected()){
                    spotifire.skipNext();
                    result.success(true);
                }else{
                    result.success(false);
                }
                break;
            case "skipPrevious":
                if(spotifire.isConnected()){
                    spotifire.skipPrevious();
                    result.success(true);
                }else{
                    result.success(false);
                }
                break;
            case "seekTo":
                if(spotifire.isConnected())
                spotifire.seekTo((int) call.argument("duration"));
                result.success(null);
                break;
            case "setRepeat":
                if(spotifire.isConnected())
                spotifire.setRepeat((int) call.argument("repeat"));
                result.success(null);

                break;
            case "queue":
                if(spotifire.isConnected())
                spotifire.queue((String) call.argument("nqueue"));
                result.success(null);

                break;
            default:
                result.notImplemented();
                break;
        }
    }


}
