import 'package:auto_route/auto_route.dart';

import '../auth.dart';
import 'routes.gr.dart';

@AutoRouterConfig(
  replaceInRouteName: 'Page,Route',
)
class AppRouter extends $AppRouter {
  @override
  RouteType get defaultRouteType => const RouteType.material();

  @override
  final List<AutoRoute> routes = [
    AutoRoute(path: '/', page: LoginOne.page, initial: true),
    AutoRoute(
      path: '/admin',
      page: FMenuBar.page,
      guards: [AuthGuard(GlobalProviders.auth)],
      children: [
        AutoRoute(path: '', page: Dashboard.page),
//flutter packages pub run build_runner watch
//{routes}
      ],
    ),
  ];
}

class GlobalProviders {
  static final Auth auth = Auth();
}

class AuthGuard extends AutoRouteGuard {
  final Auth auth;

  AuthGuard(this.auth);

  @override
  Future<void> onNavigation(
      NavigationResolver resolver, StackRouter router) async {
    if (await auth.checkAuthStatus()) {
      resolver.next(true);
    } else {
      router.replaceAll([const LoginOne()]);
    }
  }
}
