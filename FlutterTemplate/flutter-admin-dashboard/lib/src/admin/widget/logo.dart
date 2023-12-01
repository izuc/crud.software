import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';

class Logo extends StatelessWidget {
  final Color color;

  const Logo({Key? key, required this.color}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    String rawSvg = '''
<svg height="100%" width="100%" version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" 
	 viewBox="0 0 512 512" xml:space="preserve">
<circle style="fill:#CFDCE5;" cx="256" cy="256" r="256"/>
<g>
	<path style="fill:#415E72;" d="M83.928,259.304L201.32,204.8v25.92l-88.776,38.88v0.488l88.8,38.888v25.92l-117.416-54.56
		C83.928,280.336,83.928,259.304,83.928,259.304z"/>
	<path style="fill:#415E72;" d="M219.448,344.656l52.336-177.32h24.696l-52.336,177.32H219.448z"/>
	<path style="fill:#415E72;" d="M428.072,281.064L310.68,334.872v-25.92l90.736-38.888V269.6l-90.736-38.912V204.8l117.392,53.808
		V281.064z"/>
</g>
</svg>
    ''';

    return Scaffold(
      body: Column(
        children: [
          Expanded(
            child: SvgPicture.string(
              rawSvg,
              allowDrawingOutsideViewBox: true,
            ),
          ),
        ],
      ),
    );
  }
}

extension ColorExtension on Color {
  /// Return the color as a hex string.
  String toHex({bool leadingHashSign = true}) => '${leadingHashSign ? '#' : ''}'
      '${alpha.toRadixString(16).padLeft(2, '0')}'
      '${red.toRadixString(16).padLeft(2, '0')}'
      '${green.toRadixString(16).padLeft(2, '0')}'
      '${blue.toRadixString(16).padLeft(2, '0')}';
}
