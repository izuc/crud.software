import 'dart:convert';
import 'package:http/http.dart' as http;

class Response<T> {
  final String? status;
  final int? code;
  final T? data;
  final String? errorMessage;

  Response({this.status, this.code, this.data, this.errorMessage});

  factory Response.fromJson(
      Map<String, dynamic> json, T Function(dynamic) fromJson) {
    if (json['status'] == 'success' && json['code'] == 1) {
      return Response(
        status: json['status'],
        code: json['code'],
        data: fromJson(json['data']),
      );
    } else {
      return Response.error(json['message'] ?? 'Unknown error');
    }
  }

  factory Response.error(String message) {
    return Response(
      status: 'error',
      code: 0,
      errorMessage: message,
    );
  }

  bool get isSuccess => status == 'success' && code == 1;
  bool get isError => status == 'error' || code != 1;

  static Future<Response<T>> processSingleResponse<T>(
    http.Response response,
    T Function(Map<String, dynamic>) fromJson,
  ) async {
    return processResponse(response, (data) => fromJson(data));
  }

  static Future<Response<List<T>>> processListResponse<T>(
    http.Response response,
    T Function(Map<String, dynamic>) fromJson,
  ) async {
    return processResponse(response, (data) {
      if (data is List) {
        return data.map((item) => fromJson(item)).toList();
      } else {
        throw FormatException('Expected list but found ${data.runtimeType}');
      }
    });
  }

  static Future<Response<T>> processResponse<T>(
    http.Response response,
    T Function(dynamic) fromJson,
  ) async {
    try {
      final decodedResponse = json.decode(response.body);

      if (response.statusCode == 200) {
        return Response<T>.fromJson(decodedResponse, fromJson);
      } else if (response.statusCode == 400) {
        return Response<T>.error(
            'Bad request: ${decodedResponse['message'] ?? 'Unknown error'}');
      } else if (response.statusCode >= 500) {
        return Response<T>.error(
            'Server error: ${decodedResponse['message'] ?? 'Unknown error'}');
      } else {
        return Response<T>.error(
            'Unknown error: ${decodedResponse['message'] ?? 'Unknown error'}');
      }
    } catch (e) {
      return Response<T>.error('Failed to process response: $e');
    }
  }

  static Future<Response<PagedData<T>>> processPagedResponse<T>(
    http.Response response,
    T Function(Map<String, dynamic>) fromJson,
  ) async {
    return processResponse(
        response, (data) => PagedData<T>.fromJson(data, fromJson));
  }
}

class PagedResponse<T> {
  final String status;
  final int code;
  final PagedData<T>? data;
  final String? errorMessage;

  PagedResponse(
      {required this.status, required this.code, this.data, this.errorMessage});

  factory PagedResponse.fromJson(
      Map<String, dynamic> json, T Function(Map<String, dynamic>) fromJson) {
    if (json['status'] == 'success' && json['code'] == 1) {
      return PagedResponse(
        status: json['status'],
        code: json['code'],
        data: PagedData.fromJson(json['data'], fromJson),
      );
    } else {
      return PagedResponse.error(json['message'] ?? 'Unknown error');
    }
  }

  factory PagedResponse.error(String message) {
    return PagedResponse(
      status: 'error',
      code: 0,
      errorMessage: message,
    );
  }

  bool get isSuccess => status == 'success' && code == 1;
  bool get isError => status == 'error' || code != 1;
}

class PagedData<T> {
  final int currentPage;
  final List<T> data;
  final String firstPageUrl;
  final int from;
  final int lastPage;
  final String lastPageUrl;
  final List<Link> links;
  final String? nextPageUrl;
  final String path;
  final int perPage;
  final String? prevPageUrl;
  final int to;
  final int total;

  PagedData({
    required this.currentPage,
    required this.data,
    required this.firstPageUrl,
    required this.from,
    required this.lastPage,
    required this.lastPageUrl,
    required this.links,
    required this.path,
    required this.perPage,
    required this.to,
    required this.total,
    this.nextPageUrl,
    this.prevPageUrl,
  });

  factory PagedData.fromJson(
      Map<String, dynamic> json, T Function(Map<String, dynamic>) fromJson) {
    final data = (json['data'] as List? ?? [])
        .map((item) => fromJson(item))
        .toList()
        .cast<T>();
    final links = (json['links'] as List? ?? [])
        .map((item) => Link.fromJson(item))
        .toList()
        .cast<Link>();
    return PagedData(
      currentPage: json['current_page'] ?? 1,
      data: data,
      firstPageUrl: json['first_page_url'] ?? '',
      from: json['from'] ?? 0,
      lastPage: json['last_page'] ?? 1,
      lastPageUrl: json['last_page_url'] ?? '',
      links: links,
      nextPageUrl: json['next_page_url'],
      path: json['path'] ?? '',
      perPage: json['per_page'] ?? 10,
      prevPageUrl: json['prev_page_url'],
      to: json['to'] ?? 0,
      total: json['total'] ?? 0,
    );
  }
}

class Link {
  final String? url;
  final String label;
  final bool active;

  Link({required this.url, required this.label, required this.active});

  factory Link.fromJson(Map<String, dynamic> json) {
    return Link(
      url: json['url'],
      label: json['label'],
      active: json['active'],
    );
  }
}
