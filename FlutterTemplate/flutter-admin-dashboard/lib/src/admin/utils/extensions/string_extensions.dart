extension Range on num {
  bool isBetween(num from, num to) {
    return from <= this && this <= to;
  }
}

extension StringExtension on String {
  String capitalize() {
    try {
      return trim()
          .split(' ')
          .map((e) => e.trim()._capitalizeFirst())
          .join(' ');
    } catch (e) {
      return this;
    }
  }

  String camelCase() {
    try {
      final list = trim().split(' ');
      String remaining = '';
      if (list.length > 1) {
        remaining =
            list.sublist(1).map((e) => e.trim()._capitalizeFirst()).join('');
      }
      return [list.first.toLowerCase(), remaining].join('');
    } catch (e) {
      return toLowerCase();
    }
  }

  String _capitalizeFirst() =>
      this[0].toUpperCase() + substring(1).toLowerCase();
}
