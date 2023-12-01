import 'package:admin_dashboard/src/admin/constant/color.dart';
import 'package:admin_dashboard/src/admin/constant/string.dart';
import 'package:admin_dashboard/src/admin/provider/theme/bloc/theme_mode_bloc.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:admin_dashboard/src/admin/utils/responsive.dart';

class Footer extends StatelessWidget {
  const Footer({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<ThemeModeBloc, ThemeModeState>(
      builder: (context, state) {
        return state.maybeWhen(
          success: (isDarkTheme) => Container(
            color: isDarkTheme ? ColorConst.footerDark : ColorConst.drawerBG,
            height: 60,
            width: Responsive.isWeb(context)
                ? MediaQuery.of(context).size.width - 240
                : MediaQuery.of(context).size.width,
            alignment: Alignment.center,
            child: const Text(Strings.footerText),
          ),
          orElse: () =>
              const SizedBox.shrink(), // Handle other states if needed
        );
      },
    );
  }
}
