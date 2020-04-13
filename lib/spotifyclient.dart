import 'dart:async';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

enum Repeat{
  one,
  all,
  off
}



class Spotifyclient {
  static const MethodChannel _channel = const MethodChannel('spotifyclient');
 static EventChannel _eventChannel  = EventChannel('musicStream');

  static Stream stream;

  static Stream get musicStream {
    stream??= _eventChannel.receiveBroadcastStream();
    return stream.map<Music>((music)=> Music.fromNativeMusic(music));
  }
 

  static Future<void> login({@required String clientid}) async {
  Map configs;
     configs.putIfAbsent("client_id", ()=> clientid);
      await _channel.invokeMethod('loginSpotify',configs);
  }
  static Future<bool> get logout async =>
      await _channel.invokeMethod('logoutSpotify');


  static Future<bool> get isLoggedIn async =>
      await _channel.invokeMethod('isLoggedIn');

  static Future<String> get getAccessToken async =>
      await _channel.invokeMethod('getAccessToken');

  static Future<bool> get isRemoteConnected async =>
      await _channel.invokeMethod('isRemoteConnected');

  static Future<bool> get connectRemote async =>
      await _channel.invokeMethod('connectRemote');

  static Future<bool> get disconnectRemote async =>
      await _channel.invokeMethod('disconnectRemote');

  static Future<bool> playPlaylist({@required String playlistUri}) async {
    Map config;
    config.putIfAbsent("playListUri", ()=> playlistUri);
      await _channel.invokeMethod('playPlaylist',config);
  }

  static Future<bool> get pauseMusic async =>
      await _channel.invokeMethod('pauseMusic');

  static Future<bool> get resumeMusic async =>
      await _channel.invokeMethod('resumeMusic');

  static Future<bool> get skipNext async =>
      await _channel.invokeMethod('skipNext');

  static Future<bool> get skipPrevious async =>
      await _channel.invokeMethod('skipPrevious');
  static Future<String> get test async => await _channel.invokeMethod('test');


  static Future<void> seekTo({@required Duration duration})async{
    Map config;
    config.putIfAbsent(duration, ()=> duration.inMilliseconds);
    await _channel.invokeMethod("seekTo",config);
  }


 static Future<void> setRepeat({@required Repeat repeat})async{
   int _repeat;
   if(repeat ==Repeat.all){
     _repeat =2;
   }else if(repeat==Repeat.one){
     _repeat =1;
   }else{
     _repeat = 0;
   }
     
   Map config;
   config.putIfAbsent("repeat", ()=> _repeat);

  await _channel.invokeMethod("setRepeat",config);
 }

 static Future<void> queue({@required String playlistUri})async{
   Map config;
   config.putIfAbsent("nqueue", ()=> playlistUri);
 }
 
}



class Music {
  final String name;
  final String album;
  final String uri;
  final List<Artist> artists;
  final Duration duration;
  final Uint8List musicImage;

  Music({this.name, this.album, this.uri, this.artists, this.duration, this.musicImage});



 factory Music.fromNativeMusic(Map map){
        return Music(
          name: map['name'],
          album: map['album'],
          artists: map['artists'].map<Artist>((artist)=>Artist.fromNativeArtist(artist)).toList(),
          duration: Duration(milliseconds: map['duration']),
          musicImage: _getsongImage(List<int>.from(map['image_bytes'])),
          uri: map['uri']
        );
 }

 static  Uint8List _getsongImage (List<int> bytes){
    return Uint8List.fromList(bytes);
 }

}

class Artist{
  final String name;
  final String uri;

  Artist({this.name, this.uri});

  factory Artist.fromNativeArtist(Map map){
    return Artist(
      name: map['name'],
      uri: map['uri']
    );
  }
}