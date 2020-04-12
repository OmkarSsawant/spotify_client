import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

enum LogState { login, logout, getAccessToken }

class Spotifyclient {
  static const MethodChannel _channel = const MethodChannel('spotifyclient');


  static Future<void> get login async =>
      await _channel.invokeMethod('loginSpotify');

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

  static Future<bool> get playPlaylist async =>
      await _channel.invokeMethod('playPlaylist');

  static Future<bool> get pauseMusic async =>
      await _channel.invokeMethod('pauseMusic');

  static Future<bool> get resumeMusic async =>
      await _channel.invokeMethod('resumeMusic');

  static Future<bool> get skipNext async =>
      await _channel.invokeMethod('skipNext');

  static Future<bool> get skipPrevious async =>
      await _channel.invokeMethod('skipPrevious');
  static Future<String> get test async => await _channel.invokeMethod('test');
}
