import 'package:admin_dashboard/src/services/api.dart';
import 'package:flutter/foundation.dart';
import 'dart:convert';
import 'package:localstorage/localstorage.dart';

class Auth with ChangeNotifier {
  bool _isAuthenticated = false;
  final LocalStorage storage = LocalStorage('auth');

  bool get isAuthenticated => _isAuthenticated;

  final ValueNotifier<bool> authStatusNotifier = ValueNotifier<bool>(false);

  Auth() {
    checkAuthStatus();
  }

  Future<bool> login(String username, String password) async {
    await storage.ready;
    try {
      final response = await APIService.apiNoAuth('/login',
          body: {"username": username, "password": password});

      if (response.statusCode == 200) {
        Map<String, dynamic> data = jsonDecode(response.body);
        final now = DateTime.now().millisecondsSinceEpoch;
        final expiryDate = now + data['data']['expires_in'];

        // save token and expiryDate to local storage
        await storage.setItem('jwtToken', data['data']['access_token']);
        await storage.setItem('expiryDate', expiryDate.toString());

        _isAuthenticated = true;
        authStatusNotifier.value = _isAuthenticated;
        notifyListeners();
        return true; // return true to indicate successful login
      } else {
        _isAuthenticated = false;
        authStatusNotifier.value = _isAuthenticated;
        notifyListeners();
        return false; // return false to indicate unsuccessful login
      }
    } catch (error) {
      _isAuthenticated = false;
      authStatusNotifier.value = _isAuthenticated;
      notifyListeners();
      return false; // return false to indicate unsuccessful login
    }
  }

  Future<void> logout() async {
    _isAuthenticated = false;
    // Remove from local storage
    await storage.deleteItem('jwtToken');
    await storage.deleteItem('expiryDate');
    authStatusNotifier.value = _isAuthenticated;
    notifyListeners();
  }

  Future<bool> checkAuthStatus() async {
    await storage.ready;
    final jwtToken = storage.getItem('jwtToken');
    final expiryDateString = storage.getItem('expiryDate');

    // If the token exists and it is not expired, the user is authenticated
    if (jwtToken != null && expiryDateString != null) {
      final expiryDate =
          DateTime.fromMillisecondsSinceEpoch(int.parse(expiryDateString));
      final now = DateTime.now();
      if (now.isBefore(expiryDate)) {
        _isAuthenticated = true;
      } else {
        _isAuthenticated = false;
        // If the token is expired, logout the user and remove the token from local storage
        await logout();
      }
    } else {
      _isAuthenticated = false;
    }
    authStatusNotifier.value = _isAuthenticated;
    notifyListeners();
    return Future.value(_isAuthenticated);
  }

  ValueNotifier<bool> getValueNotifier() {
    return authStatusNotifier;
  }
}
