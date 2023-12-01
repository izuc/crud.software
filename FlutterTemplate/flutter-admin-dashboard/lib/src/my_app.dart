import 'dart:async';

import 'package:admin_dashboard/src/admin/constant/theme.dart';
import 'package:admin_dashboard/src/admin/utils/hive/hive.dart';
import 'package:admin_dashboard/src/auth.dart';

import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:provider/provider.dart';

import 'admin/provider/theme/bloc/theme_mode_bloc.dart';
import 'routes/routes.dart';
import 'routes/routes.gr.dart';

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late final AppRouter _appRouter;
  late Timer _authTimer; // New field for the Timer

  @override
  void initState() {
    super.initState();
    _appRouter = AppRouter();
    init();

    _authTimer = Timer.periodic(
      const Duration(seconds: 5),
      (timer) => checkAuthenticationStatus(),
    );
  }

  Future<void> init() async {
    await HiveUtils.init();
    themeModeBloc.add(const ThemeModeEvent.changeTheme(null));
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    // Adding the listener for auth changes
    Provider.of<Auth>(context, listen: false)
        .getValueNotifier()
        .addListener(checkAuthenticationStatus);
  }

  // A method that will handle navigation based on authentication status
  void checkAuthenticationStatus() {
    if (Provider.of<Auth>(context, listen: false).getValueNotifier().value) {
      _appRouter.replaceAll([const FMenuBar()]);
    } else {
      _appRouter.replaceAll([const LoginOne()]);
    }
  }

  @override
  void dispose() {
    Provider.of<Auth>(context, listen: false)
        .getValueNotifier()
        .removeListener(checkAuthenticationStatus);
    _authTimer.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
        providers: [
          BlocProvider(create: (context) => themeModeBloc),
        ],
        child: BlocBuilder<ThemeModeBloc, ThemeModeState>(
          builder: (context, state) {
            return state.when(
                initial: () => const SizedBox.shrink(),
                success: (themeMode) {
                  // Removed the Auth provider from here
                  return ValueListenableBuilder(
                    valueListenable: Provider.of<Auth>(context, listen: false)
                        .getValueNotifier(),
                    builder: (BuildContext context, bool isAuthenticated,
                        Widget? child) {
                      return MaterialApp.router(
                          routerDelegate: _appRouter.delegate(),
                          routeInformationParser:
                              _appRouter.defaultRouteParser(),
                          debugShowCheckedModeBanner: false,
                          theme: ThemeClass.themeData(themeMode, context),
                          scrollBehavior:
                              const MaterialScrollBehavior().copyWith(
                            dragDevices: {
                              PointerDeviceKind.mouse,
                              PointerDeviceKind.touch,
                              PointerDeviceKind.stylus,
                              PointerDeviceKind.trackpad,
                              PointerDeviceKind.unknown
                            },
                          ),
                          title: 'Crud App');
                    },
                  );
                });
          },
        ));
  }
}
