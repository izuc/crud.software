import 'package:admin_dashboard/src/admin/constant/color.dart';
import 'package:admin_dashboard/src/admin/constant/image.dart';
import 'package:admin_dashboard/src/admin/constant/theme.dart';
import 'package:admin_dashboard/src/routes/routes.gr.dart';

import 'package:admin_dashboard/src/admin/utils/responsive.dart';
import 'package:admin_dashboard/src/admin/views/authentication/constant_auth.dart';
import 'package:admin_dashboard/src/admin/constant/custom_text.dart';
import 'package:admin_dashboard/src/admin/widget/custom_text_field.dart';
import 'package:admin_dashboard/src/admin/constant/string.dart';
import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';
import 'package:flutterx/flutterx.dart';
import 'package:provider/provider.dart';

import '../../../../auth.dart';
import '../../../widget/logo.dart';

@RoutePage()
class LoginOne extends StatefulWidget {
  const LoginOne({Key? key}) : super(key: key);

  @override
  State<LoginOne> createState() => _LoginOneState();
}

class _LoginOneState extends State<LoginOne> {
  late final TextEditingController _usernameController;
  late final TextEditingController _passwordController;

  final FocusNode _passwordFocusNode = FocusNode();

  @override
  void initState() {
    super.initState();
    _usernameController = TextEditingController();
    _passwordController = TextEditingController();
  }

  @override
  void dispose() {
    // Always remember to dispose of your resources when they are no longer needed
    _passwordFocusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final auth = Provider.of<Auth>(context, listen: false);

    Future.microtask(() async {
      if (await auth.checkAuthStatus()) {
        // ignore: use_build_context_synchronously
        context.router.replaceAll([const FMenuBar()]);
      }
    });

    return Scaffold(
      //resizeToAvoidBottomInset: false,
      body: SingleChildScrollView(
        child: Stack(
          children: [
            SelectionArea(
              child: Stack(
                alignment: Alignment.center,
                children: [
                  Image.asset(
                    Images.authBG,
                    height: MediaQuery.of(context).size.height,
                    width: MediaQuery.of(context).size.width,
                    fit: BoxFit.cover,
                  ),
                  Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    // mainAxisSize: MainAxisSize.min,
                    children: [
                      FxBox.h20,
                      Center(
                        child: Container(
                          constraints: const BoxConstraints(
                            maxWidth: 460,
                          ),
                          padding: Responsive.isMobile(context)
                              ? const EdgeInsets.all(32)
                              : const EdgeInsets.all(40),
                          decoration: BoxDecoration(
                            color: isDark
                                ? ColorConst.scaffoldDark
                                : ColorConst.white,
                            border: Border.all(
                              color: isDark
                                  ? ColorConst.scaffoldDark
                                  : ColorConst.white,
                            ),
                            borderRadius: BorderRadius.circular(24),
                          ),
                          child: Column(
                            children: [
                              Container(
                                  width: 240,
                                  height: 100,
                                  color: ColorConst.transparent,
                                  child: Logo(
                                      color: isDark
                                          ? ColorConst.white
                                          : ColorConst.black)),
                              FxBox.h16,
                              ConstantAuth.headerView('Sign In', '', context),
                              _bottomView(),
                            ],
                          ),
                        ),
                      ),
                      FxBox.h20,
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _bottomView() {
    return Column(
      mainAxisAlignment: MainAxisAlignment.start,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        FxBox.h28,
        ConstantAuth.labelView('Username'),
        FxBox.h8,
        _usernameTextBoxWidget(),
        FxBox.h16,
        ConstantAuth.labelView('Password'),
        FxBox.h8,
        _passwordTextBoxWidget(),
        FxBox.h16,
        _loginButton(),
        FxBox.h20,
        _serviceText(),
      ],
    );
  }

  Widget _serviceText() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceAround,
      children: [
        CustomText(
          title: Strings.copyright,
          fontSize: 14,
          fontWeight: FontWeight.w600,
          textColor: isDark ? ColorConst.white : ColorConst.black,
        ),
      ],
    );
  }

  Widget _usernameTextBoxWidget() {
    return CustomTextField(
      hintText: Strings.enterUser,
      onChanged: (String value) {},
      textCapitalization: TextCapitalization.none,
      textInputAction: TextInputAction.done,
      controller: _usernameController,
    );
  }

  Widget _passwordTextBoxWidget() {
    return CustomTextField(
      obsecureText: true,
      hintText: Strings.enterPassword,
      onChanged: (String value) {},
      textCapitalization: TextCapitalization.none,
      textInputAction: TextInputAction.done,
      controller: _passwordController,
      onSubmitted: (value) {
        _loginAction();
      },
    );
  }

  Future<void> _loginAction() async {
    // Simple validation, replace with your own logic
    if (_usernameController.text.isNotEmpty &&
        _passwordController.text.isNotEmpty) {
      // Update the Auth status and Navigate to dashboard after successful login
      bool loginSuccess = await Provider.of<Auth>(context, listen: false)
          .login(_usernameController.text, _passwordController.text);
      if (loginSuccess) {
        // ignore: use_build_context_synchronously
        context.router.replaceAll([const FMenuBar()]);
      } else {
        // Show error message for unsuccessful login
        // ignore: use_build_context_synchronously
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Login failed. Invalid username or password.'),
          ),
        );
      }
    } else {
      // Show error message
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content:
              Text('Login failed. Please enter both username and password.'),
        ),
      );
    }
  }

  Widget _loginButton() {
    return FxButton(
      onPressed: _loginAction,
      text: 'Sign In',
      borderRadius: 8.0,
      height: 40,
      minWidth: MediaQuery.of(context).size.width,
      color: Theme.of(context).colorScheme.primary,
    );
  }
}
