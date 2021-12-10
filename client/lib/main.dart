import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:cryptography/cryptography.dart';
import 'package:flutter/material.dart';

// ignore: constant_identifier_names
const String KEY = "FLAG{5ecret_ke4}";

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  String _input = '';
  String _output = '';

  void _handleText(String s) {
    setState(() {
      _input = s;
    });
  }

  Future<String> _encrypt(String message) async {
    final messageInt = utf8.encode(message);
    final algorithm = AesGcm.with128bits();
    final secretKey = await algorithm.newSecretKeyFromBytes(utf8.encode(KEY));
    final nonce = algorithm.newNonce();

    final secretBox = await algorithm.encrypt(
      messageInt,
      secretKey: secretKey,
      nonce: nonce,
    );

    final encoded = base64Encode(secretBox.concatenation());
    return encoded;
  }

  Future<String> _decrypt(String encoded) async {
    final encrypted = base64Decode(encoded);
    final algorithm = AesGcm.with128bits();
    final secretBox = SecretBox.fromConcatenation(
      encrypted,
      nonceLength: 12,
      macLength: 16,
    );
    final secretKey = await algorithm.newSecretKeyFromBytes(utf8.encode(KEY));
    final clearText = await algorithm.decrypt(
      secretBox,
      secretKey: secretKey,
    );
    return utf8.decode(clearText);
  }

  void _send() async {
    final encodedName = await _encrypt(_input);

    final response = await http.post(
      Uri.parse('https://encrypted-connection-sample.herokuapp.com/greet'),
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      encoding: Encoding.getByName("utf-8"),
      body: {"name": encodedName},
    );
    if (response.statusCode != 200) {
      // ignore: avoid_print
      print("response code is: ${response.statusCode}");
    }

    final got = await _decrypt(response.body);

    setState(() {
      _output = got;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              _output,
              style: const TextStyle(
                color: Colors.blueAccent,
                fontSize: 30.0,
                fontWeight: FontWeight.w500,
              ),
            ),
            TextField(
              enabled: true,
              maxLength: 10,
              style: const TextStyle(color: Colors.grey),
              obscureText: false,
              maxLines: 1,
              onChanged: _handleText,
            ),
            ElevatedButton(
              onPressed: _send,
              child: const Text('送信'),
            ),
          ],
        ),
      ),
    );
  }
}
