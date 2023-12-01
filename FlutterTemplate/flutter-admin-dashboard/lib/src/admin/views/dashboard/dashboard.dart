import 'package:flutter/material.dart';
import 'package:auto_route/annotations.dart';

@RoutePage()
class Dashboard extends StatefulWidget {
  const Dashboard({Key? key}) : super(key: key);

  @override
  _DashboardState createState() => _DashboardState();
}

class _DashboardState extends State<Dashboard> {
  @override
  Widget build(BuildContext context) {
    // Define the dashboard content
    Widget dashboardContent = Scaffold(
      appBar: AppBar(
        title: const Text('Dashboard'),
      ),
      body: const Center(
        child: Text('Dashboard here'),
      ),
    );

    // Get the screen height
    double screenHeight = MediaQuery.of(context).size.height;

    return Center(
      child: Container(
        padding: const EdgeInsets.only(right: 10, top: 50),
        // Set a fixed height for the container based on the screen height
        height: screenHeight * 0.8, // 80% of screen height, adjust as needed
        child: dashboardContent,
      ),
    );
  }
}
