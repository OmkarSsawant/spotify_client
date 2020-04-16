package com.visiondev.spotifyclient;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;


import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;




/** SpotifyclientPlugin */
public class SpotifyclientPlugin implements FlutterPlugin, ActivityAware {



     private Spotifire spotifire;

     private static EventChannel eventChannel;

     private static MethodChannel channel;



  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
     channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "spotifyclient");
       eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(),"musicStream");
      final SpotifyStreamHandler spotifyStreamHandler = new SpotifyStreamHandler(channel);

 spotifire = new Spotifire(flutterPluginBinding.getApplicationContext(),spotifyStreamHandler);

      spotifyStreamHandler.setSpotifire(spotifire);
      eventChannel.setStreamHandler(spotifyStreamHandler);
    final SpotifyCallHandler spotifyCallHandler = new SpotifyCallHandler(spotifire);
      channel.setMethodCallHandler(spotifyCallHandler);

  }

  public static void registerWith(Registrar registrar) {
       channel = new MethodChannel(registrar.messenger(), "spotifyclient");
       eventChannel = new EventChannel(registrar.messenger(),"musicStream");


      final SpotifyStreamHandler spotifyStreamHandler = new SpotifyStreamHandler(channel);

       final SpotifyclientPlugin plugin = new SpotifyclientPlugin();
     plugin.spotifire = new Spotifire(registrar.context(),spotifyStreamHandler);

      spotifyStreamHandler.setSpotifire(plugin.spotifire);
      eventChannel.setStreamHandler(spotifyStreamHandler);

   final SpotifyCallHandler   spotifyCallHandler = new SpotifyCallHandler(plugin.spotifire);
      channel.setMethodCallHandler(spotifyCallHandler);

  }



  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
      this.toreActivity();
      spotifire.disconnectRemote();
      channel.setMethodCallHandler(null);
      eventChannel.setStreamHandler(null);
  }


private ActivityPluginBinding activityPluginBinding;

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
      this.attachActivity(binding);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
       this.attachActivity(binding);
  }

  @Override
  public void onDetachedFromActivity() {
     this.toreActivity();
     spotifire.pause();
     spotifire.disconnectRemote();
  }


  private  void attachActivity(ActivityPluginBinding _activitybinding){
      this.activityPluginBinding = _activitybinding;
      spotifire.setActivity(_activitybinding.getActivity());
   _activitybinding.addActivityResultListener(spotifire);

  }
  private  void toreActivity(){
      activityPluginBinding.removeActivityResultListener(spotifire);
    spotifire.setActivity(null);
    activityPluginBinding= null;
  }


}
