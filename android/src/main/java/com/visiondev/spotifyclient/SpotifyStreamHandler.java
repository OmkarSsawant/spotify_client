package com.visiondev.spotifyclient;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.PluginRegistry;

public class SpotifyStreamHandler implements EventChannel.StreamHandler {


    private Spotifire spotifire;

    SpotifyStreamHandler(Spotifire _spofire){
        this.spotifire = _spofire;
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {

    }

    @Override
    public void onCancel(Object arguments) {

    }
}

