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
      await Spotifyclient.login.whenComplete(() {
        print("Completed login");
      });
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: <Widget>[
           
           Center(child: IconButton(icon: Icon(Icons.play_arrow,size: 50,), onPressed: ()async{
             await Spotifyclient.resumeMusic;
           })),
           Center(child: IconButton(icon: Icon(Icons.pause,size: 50), onPressed: ()async{
             await Spotifyclient.pauseMusic;
           })),
           Center(child: IconButton(icon: Icon(Icons.skip_next,size: 50), onPressed: ()async{
             await Spotifyclient.skipNext;
             
           })),
           Center(child: IconButton(icon: Icon(Icons.skip_previous,size: 50), onPressed: ()async{
             await Spotifyclient.skipPrevious;           
           })),

           
            
          ],
        ),
        floatingActionButton: FloatingActionButton(onPressed: () async {
          await Spotifyclient.getAccessToken.then((token) {
            print("Access Token : " + token);
          }).catchError(print);
          // await Spotifyclient.test.then(print);

          await Spotifyclient.isRemoteConnected.then(print);
          await Spotifyclient.connectRemote.then(print);

          try {
            if (await Spotifyclient.isRemoteConnected)
              await Spotifyclient.playPlaylist.then(print);
              else
              print("Remote is not connected");
          } catch (e) {
            print(e);
          }
        }),
      ),
    );
  }

  @override
  void dispose() {
    super.dispose();
  }

  Future<void> _closeMusic()async{
    await Spotifyclient.disconnectRemote;
    
  }
}
