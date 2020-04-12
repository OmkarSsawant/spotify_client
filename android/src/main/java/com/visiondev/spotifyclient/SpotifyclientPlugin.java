package com.visiondev.spotifyclient;

import androidx.annotation.NonNull;


import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;




/** SpotifyclientPlugin */
public class SpotifyclientPlugin implements FlutterPlugin, ActivityAware {



   private static Spotifire spotifire;


    private static   SpotifyCallHandler spotifyCallHandler;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "spotifyclient");
      spotifire = new Spotifire(flutterPluginBinding.getApplicationContext());

      spotifyCallHandler = new SpotifyCallHandler(spotifire);
      channel.setMethodCallHandler(spotifyCallHandler);


  }

  public static void registerWith(Registrar registrar) {
      final MethodChannel channel = new MethodChannel(registrar.messenger(), "spotifyclient");


      spotifire = new Spotifire(registrar.context());
      spotifyCallHandler = new SpotifyCallHandler(spotifire);
      channel.setMethodCallHandler(spotifyCallHandler);

  }



  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
      this.toreActivity();
      spotifire.disconnectRemote();
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
