import 'dart:io';
import 'package:admin_dashboard/src/auth.dart';
import 'package:admin_dashboard/src/my_app.dart';
import 'package:flutter/foundation.dart' show kIsWeb, kReleaseMode;
import 'package:flutter/material.dart';
import 'package:flutterx/flutterx.dart';
import 'package:provider/provider.dart';
import 'package:window_size/window_size.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  if (!kIsWeb) {
    setWindowTitle('Crud App');
    setWindowMinSize(const Size(480, 360));
    setWindowMaxSize(Size.infinite);
  }
  setPathUrlStrategy();
  FlutterError.onError = (FlutterErrorDetails details) {
    FlutterError.dumpErrorToConsole(details);
    if (kReleaseMode) {
      exit(1);
    }
  };

  try {
    runApp(ChangeNotifierProvider<Auth>(
      create: (context) => Auth(),
      child: const MyApp(),
    ));
  } catch (e) {
    // ignore: avoid_print
    print(e);
  }
}
