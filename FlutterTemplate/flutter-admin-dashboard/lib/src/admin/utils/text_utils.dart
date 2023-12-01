String upperCase(String text) {
  if (text == '/') {
    return 'Dashboard';
  } else {
    return text.replaceAll('/', '').substring(0, 1).toUpperCase() +
        text.replaceAll('/', '').substring(1).toLowerCase();
  }
}
