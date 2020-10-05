## 0.2.0

# Spotify Plugin for android


A new Flutter plugin.
##### A new Flutter plugin.Spotidy Sdk which handles authorization as well as playing playlist of your choice free.


## Getting Started
## 2 step  Configurations
#### 1 .  Get client id


This project is a starting point for a Flutter
   Just go to the [spotify developers site](https://developer.spotify.com/dashboard) login and register your application.
[plug-in package](https://flutter.dev/developing-packages/),
   You just need to follow and prepare your environment from [Quick Start](https://developer.spotify.com/documentation/android/quick-start/) .
a specialized package that includes platform-specific implementation code for Android 
   
###### How to register
Set  `redirect uri` as `spotify-sdk://auth` and app name as `your app name`and android section `package name and debug,release fingerprint` in registration.
Your registreation is complete.
you will be provided with `client id`. 


#### 2. Import them as module

  Just goto [this site](https://github.com/spotify/android-sdk/releases) and download  `spotify-app-remote-release-0.7.0.aar` and ` spotify-auth-release-1.2.3.aar` and import them as module in your android app

  ![step1](https://developer.spotify.com/assets/new-module.png)

  ![step2](https://developer.spotify.com/assets/import-jar.png)

  ![step3](https://developer.spotify.com/assets/create-new-module.png)




## Initialize Spotifire

```dart
      await Spotifire.init(clientid: "Your client id");
```
## Connect Remote

```dart
      await Spotifire.connectRemote.then(print);
```
## Play a playlist

```dart 
 if (await Spotifire.isRemoteConnected)
              await Spotifire.playPlaylist(
                  playlistUri: "spotify:playlist:37i9dQZF1DX3rxVfibe1L0");
```

## Disconnect remote


```dart
 if (await Spotifire.isRemoteConnected) await Spotifire.disconnectRemote;
```
### play 
```dart
  await Spotifire.playPlaylist(
                  playlistUri: "spotify:playlist:37i9dQZF1DX3rxVfibe1L0");
```

You can get `playlistUrl` from spotify App and convert it easily with Spotifire's getSpotifyUri method passing it
the url and directly play it.without specifically getting the uris from dekstop app.  

#### directly get uri from mobile 

```dart
  await Spotifire.playPlaylist(
                  playlistUri: Spotifire.getSpotifyUri(spotify_url));
```


### pause
```dart
                await Spotifire.pauseMusic ;
```

### resume
```dart
                await Spotifire.resumeMusic;
```

### skip previous

```dart
                await Spotifire.skipPrevious;
```
### skip next

```dart
                await Spotifire.skipNext;
```

### seekTo

```dart
   await Spotifire.seekTo(Duration position);
   
```


### Listen to music stream

```dart
  Spotifire.musicStream.listen((music) {
      // print("Music" + music.runtimeType.toString());
      if (mounted && music is Music)
        setState(() {
          _music = music;
        });
    }).onError((error) {
      print(error);
    });
```

### Position Stream 

```dart

Spotifire.positonStream.listen(print)

```


## Demo


![example](https://raw.githubusercontent.com/OmkarSsawant/Simple_Solutions/master/Assets/sootify.gif)
