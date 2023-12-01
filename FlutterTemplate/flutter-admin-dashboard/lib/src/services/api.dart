import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:localstorage/localstorage.dart';

class APIService {
  static const String _apiBaseURL = 'https://api_website_address/api/auth';
  static LocalStorage storage = LocalStorage('auth');
  static var client = http.Client();

  // Helper function to get JWT token and build headers
  static Map<String, String> _getHeaders({bool withAuth = true}) {
    final headers = {
      'Access-Control-Allow-Origin': '*',
      'Content-Type': 'application/json',
    };

    if (withAuth) {
      final jwtToken = storage.getItem('jwtToken');
      if (jwtToken == null || jwtToken.split('.').length != 3) {
        throw Exception('Invalid JWT token');
      }
      headers['Authorization'] = 'Bearer $jwtToken';
    }

    return headers;
  }

  static Future<http.Response> get(String path) async {
    final headers = _getHeaders();
    return client.get(Uri.parse(_apiBaseURL + path), headers: headers);
  }

  static Future<http.Response> post(
      String path, Map<String, dynamic> data) async {
    final headers = _getHeaders();
    return client.post(Uri.parse(_apiBaseURL + path),
        headers: headers, body: jsonEncode(data));
  }

  static Future<http.Response> put(
      String path, Map<String, dynamic> data) async {
    final headers = _getHeaders();
    return client.put(Uri.parse(_apiBaseURL + path),
        headers: headers, body: jsonEncode(data));
  }

  static Future<http.Response> delete(String path) async {
    final headers = _getHeaders();
    return client.delete(Uri.parse(_apiBaseURL + path), headers: headers);
  }

  static Future<http.Response> apiWithPath(String path,
      {Map<String, String>? body}) {
    final headers = _getHeaders(withAuth: true);
    return client.post(Uri.parse(_apiBaseURL + path),
        headers: headers, body: jsonEncode(body));
  }

  static Future<http.Response> apiNoAuth(String path,
      {Map<String, String>? body}) {
    final headers = _getHeaders(withAuth: false);
    return client.post(Uri.parse(_apiBaseURL + path),
        headers: headers, body: jsonEncode(body));
  }
}
