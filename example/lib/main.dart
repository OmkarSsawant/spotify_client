import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:spotify_auth_player/spotify_auth_player.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  Music _music;
  int totaldurationinmilli = 0;
  bool ispaused = false;
  @override
  void initState() {
    super.initState();

    initPlatformState();
  }

  Future<void> initPlatformState() async {
    await Spotifire.init(clientid: "Your client id");

    if (!mounted) return;

    Spotifire.musicStream.listen((music) {
      // print("Music" + music.runtimeType.toString());
      if (mounted)
        setState(() {
          totaldurationinmilli = music.duration.inMilliseconds;
          print(music.duration);
          _music = music;
        });
    }).onError((error) {
      print(error);
    });
  }

  double val = 0.01;
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.black87,
          title: const Text('Spotifire'),
        ),
        body: Stack(
          children: <Widget>[
            Container(
              height: 900,
              // width: MediaQuery.of(context).size.width,
              decoration: BoxDecoration(
                  gradient: LinearGradient(
                      begin: AlignmentDirectional.topStart,
                      end: AlignmentDirectional.bottomEnd,
                      colors: <Color>[
                    Color.fromRGBO(29, 185, 84, 1.0),
                    Color.fromRGBO(25, 20, 20, 1.0)
                  ])),
            ),
            ListView(
              padding: const EdgeInsets.all(12),
              children: <Widget>[
                Column(
                  children: <Widget>[
                    if (_music != null)
                      Material(
                          elevation: 7.0,
                          child: Image.memory(_music.musicImage)),
                    Text(_music != null ? _music.name : "Loding ... ",
                        style: Theme.of(context)
                            .textTheme
                            .display1
                            .copyWith(color: Colors.white.withOpacity(0.95))),
                    Text(
                      _music != null ? _music.album : "Loding ... ",
                      style: Theme.of(context)
                          .textTheme
                          .headline
                          .copyWith(color: Colors.white70),
                    ),
                    Padding(
                      padding: const EdgeInsets.only(left: 228.0),
                      child: Text(
                        _music != null
                            ? _music.duration.toString()
                            : "Loding ... ",
                        style: TextStyle(color: Colors.white54),
                      ),
                    ),
                    SizedBox(
                      height: 17,
                    ),
                    StreamBuilder<Duration>(
                        stream: Spotifire.positonStream,
                        // initialData: Duration.zero,
                        builder: (context, snapshot) {
                          if (!snapshot.hasData)
                           return Slider.adaptive(
                              value: 0.0,
                              onChanged: (d) {},
                            );
                            print(snapshot.hasData);
                          val = snapshot.hasData
                              ? _getValue(snapshot.data.inMilliseconds)
                              : val;

                          return Slider.adaptive(
                            value: val,
                            onChanged: (double cv) async {
                              final int skd =
                                  (totaldurationinmilli * cv).floor();
                              final Duration dur = Duration(milliseconds: skd);
                              await Spotifire.seekTo(
                                  seekDuration: dur,
                                  totalDuration: Duration(
                                      milliseconds: totaldurationinmilli));
                            },
                          );
                        }),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: <Widget>[
                        IconButton(
                            icon: Icon(
                              Icons.skip_previous,
                              size: 40,
                              color: Colors.white70,
                            ),
                            onPressed: () async {
                              await Spotifire.skipPrevious;
                            }),
                        AnimatedCrossFade(
                            firstChild: IconButton(
                                icon: Icon(
                                  Icons.play_arrow,
                                  color: Colors.white,
                                  size: 40,
                                ),
                                onPressed: () async {
                                  await Spotifire.resumeMusic.whenComplete(() {
                                    setState(() {
                                      ispaused = false;
                                    });
                                  });
                                }),
                            secondChild: IconButton(
                                icon: Icon(Icons.pause,
                                    size: 40, color: Colors.white),
                                onPressed: () async {
                                  await Spotifire.pauseMusic.whenComplete(() {
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
                            icon: Icon(Icons.skip_next,
                                size: 40, color: Colors.white70),
                            onPressed: () async {
                              await Spotifire.skipNext;
                            }),
                      ],
                    )
                  ],
                )
              ],
            ),
          ],
        ),
        floatingActionButton: FloatingActionButton(
            child: Icon(
              Icons.playlist_play,
              size: 35,
            ),
            onPressed: () async {
              await Spotifire.getAccessToken.then(print);

              await Spotifire.connectRemote
                  .then(print)
                  .whenComplete(() => print("compl"));
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

  double _getValue(int milliseconds) {
    double percentage = (milliseconds / totaldurationinmilli) * 100;

    print(percentage.toString() + " % ");

    double tpo = (percentage / 100) * 1.0;
    return tpo;
  }

  @override
  void dispose() {
    Spotifire.close();

    super.dispose();
  }
}
