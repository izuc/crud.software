// GENERATED CODE - DO NOT MODIFY BY HAND

// **************************************************************************
// AutoRouterGenerator
// **************************************************************************

// ignore_for_file: type=lint
// coverage:ignore-file

// ignore_for_file: no_leading_underscores_for_library_prefixes
import 'package:admin_dashboard/src/admin/views/authentication/login/login_one.dart'
    as _i1;
import 'package:admin_dashboard/src/admin/views/dashboard/dashboard.dart'
    as _i2;
import 'package:admin_dashboard/src/admin/views/menu_bar.dart' as _i3;
import 'package:auto_route/auto_route.dart' as _i4;

abstract class $AppRouter extends _i4.RootStackRouter {
  $AppRouter({super.navigatorKey});

  @override
  final Map<String, _i4.PageFactory> pagesMap = {
    LoginOne.name: (routeData) {
      return _i4.AutoRoutePage<dynamic>(
        routeData: routeData,
        child: const _i1.LoginOne(),
      );
    },
    Dashboard.name: (routeData) {
      return _i4.AutoRoutePage<dynamic>(
        routeData: routeData,
        child: const _i2.Dashboard(),
      );
    },
    FMenuBar.name: (routeData) {
      return _i4.AutoRoutePage<dynamic>(
        routeData: routeData,
        child: const _i3.FMenuBar(),
      );
    },
  };
}

/// generated route for
/// [_i1.LoginOne]
class LoginOne extends _i4.PageRouteInfo<void> {
  const LoginOne({List<_i4.PageRouteInfo>? children})
      : super(
          LoginOne.name,
          initialChildren: children,
        );

  static const String name = 'LoginOne';

  static const _i4.PageInfo<void> page = _i4.PageInfo<void>(name);
}

/// generated route for
/// [_i2.Dashboard]
class Dashboard extends _i4.PageRouteInfo<void> {
  const Dashboard({List<_i4.PageRouteInfo>? children})
      : super(
          Dashboard.name,
          initialChildren: children,
        );

  static const String name = 'Dashboard';

  static const _i4.PageInfo<void> page = _i4.PageInfo<void>(name);
}

/// generated route for
/// [_i3.FMenuBar]
class FMenuBar extends _i4.PageRouteInfo<void> {
  const FMenuBar({List<_i4.PageRouteInfo>? children})
      : super(
          FMenuBar.name,
          initialChildren: children,
        );

  static const String name = 'FMenuBar';

  static const _i4.PageInfo<void> page = _i4.PageInfo<void>(name);
}
