# spotifyclient


A new Flutter plugin.
##### A new Flutter plugin.Spotidy Sdk which handles authorization as well as playing playlist of your choice free.


## Getting Started
## 2 step  Configurations
#### 1 .  Get client id


This project is a starting point for a Flutter
   Just go to the [spotify developers site](https://developer.spotify.com/dashboard) login and register your application.
[plug-in package](https://flutter.dev/developing-packages/),
   You just need to follow and prepare your environment from [Quick Start](https://developer.spotify.com/documentation/android/quick-start/) .
a specialized package that includes platform-specific implementation code for

Android and/or iOS.
   ###### How to register
Set  `redirect uri` as `spotify-sdk://auth` and app name as `your app name`and android section `package name and debug,release fingerprint` in registration.
Your registreation is complete.
you will be provided with `client id`. 


#### 2. Paste the files

  Just goto [this site](https://github.com/spotify/android-sdk/releases) and download  `spotify-app-remote-release-0.7.0.aar` and ` spotify-auth-release-1.2.3.aar` and paste it in `project-name/android/`  directory





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

You can get `playlistUri` from spotify App.

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