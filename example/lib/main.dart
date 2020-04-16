import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:spotifyclient/spotifyclient.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  Music _music;

  bool ispaused = false;
  @override
  void initState() {
    super.initState();

    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      //TODO: change it
      await Spotifire.init(clientid: "155e080c3b0d482683a8a088b4a5779e");
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    Spotifire.musicStream.listen((music) {
      // print("Music" + music.runtimeType.toString());
      if (mounted && music is Music)
        setState(() {
          _music = music;
        });
    }).onError((error) {
      print(error);
    });


   Spotifire.positonStream.listen((d)=> print(d.inMilliseconds));
    // Spotifire.positonStream.listen((Duration position) {
    //   print(position.inMilliseconds);
    // });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: ListView(
          padding: const EdgeInsets.all(12),
          children: <Widget>[
            Column(
              children: <Widget>[
                if (_music != null)
                  Material(
                      elevation: 7.0, child: Image.memory(_music.musicImage)),
                Text(_music != null ? _music.name : "Loding ... ",style: Theme.of(context).textTheme.display1),
                Text(_music != null ? _music.album : "Loding ... ",style: Theme.of(context).textTheme.headline.copyWith(
                  color: Colors.black38
                ),),
                Padding(
                  padding: const EdgeInsets.only(left:228.0),
                  child: Text(_music != null
                      ? _music.duration.toString()
                      : "Loding ... "),
                ),
                SizedBox(
           height: 37,
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: <Widget>[
                    IconButton(
                        icon: Icon(Icons.skip_previous, size: 50),
                        onPressed: () async {
                          await Spotifire.skipPrevious;
                        }),
                    AnimatedCrossFade(
                        firstChild: IconButton(
                            icon: Icon(
                              Icons.play_arrow,
                              size: 50,
                            ),
                            onPressed: () async {
                              await Spotifire.resumeMusic.whenComplete((){
                                setState(() {
                                  ispaused= false;
                                });
                              });
                            }),
                        secondChild: IconButton(
                            icon: Icon(Icons.pause, size: 50),
                            onPressed: () async {
                              await Spotifire.pauseMusic.whenComplete((){
                                setState(() {
                                  ispaused = true;
                                });
                              });
                            }),
                        crossFadeState: ispaused
                            ? CrossFadeState.showFirst
                            : CrossFadeState.showSecond,
                        duration: const Duration(milliseconds: 700)),
                    IconButton(
                        icon: Icon(Icons.skip_next, size: 50),
                        onPressed: () async {
                          await Spotifire.skipNext;
                        }),
                  ],
                )
              ],
            )
          ],
        ),
        floatingActionButton: FloatingActionButton(
          child: Icon(Icons.playlist_play,size: 35,),
          onPressed: () async {
              await Spotifire.getAccessToken.then(print);

          await Spotifire.connectRemote.then(print).whenComplete(()=>print("compl"));
          try {
            if (await Spotifire.isRemoteConnected)
              await Spotifire.playPlaylist(
                  playlistUri: "spotify:playlist:37i9dQZF1DX3rxVfibe1L0");
          } catch (e) {
            print(e);
          }
        }),
      ),
    );
  }

  @override
  void dispose() {
  Spotifire.close();

    super.dispose();
  }

  
}
