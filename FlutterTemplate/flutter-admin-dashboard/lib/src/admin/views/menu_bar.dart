import 'package:admin_dashboard/src/admin/constant/color.dart';
import 'package:admin_dashboard/src/admin/constant/const.dart';
import 'package:admin_dashboard/src/admin/constant/icons.dart';
import 'package:admin_dashboard/src/admin/constant/string.dart';
import 'package:admin_dashboard/src/admin/constant/theme.dart';
import 'package:admin_dashboard/src/admin/provider/theme/bloc/theme_mode_bloc.dart';
import 'package:admin_dashboard/src/admin/widget/logo.dart';
import 'package:admin_dashboard/src/auth.dart';
import 'package:admin_dashboard/src/routes/routes.gr.dart';
import 'package:admin_dashboard/src/admin/utils/extensions/string_extensions.dart';
import 'package:admin_dashboard/src/admin/utils/hive/hive.dart';
import 'package:admin_dashboard/src/admin/utils/hover.dart';
import 'package:admin_dashboard/src/admin/utils/responsive.dart';
import 'package:admin_dashboard/src/admin/utils/routes.dart';
import 'package:admin_dashboard/src/admin/utils/text_utils.dart';
import 'package:admin_dashboard/src/admin/widget/expansion_tile.dart';
import 'package:admin_dashboard/src/admin/widget/svg_icon.dart';
import 'package:admin_dashboard/src/admin/views/footer.dart';
import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutterx/flutterx.dart';
import 'package:provider/provider.dart';

@RoutePage()
class FMenuBar extends StatefulWidget {
  const FMenuBar({Key? key}) : super(key: key);

  @override
  State<FMenuBar> createState() => _MenuBarState();
}

class _MenuBarState extends State<FMenuBar> {
  final GlobalKey<ScaffoldState> _scaffoldDrawerKey =
      GlobalKey<ScaffoldState>();
  final ScrollController _scrollController = ScrollController();

  ValueNotifier<bool> isOpen = ValueNotifier(true);
  ValueNotifier<bool> isSubListOpen = ValueNotifier(false);

  Map<String, String> mainData = {
    Strings.dashboard: IconlyBroken.home,
//{mainData}
  };

  final List<PageRouteInfo<dynamic>> _routes = const [
    Dashboard(),
//{routes}
  ];

  // for LTR
  final ValueNotifier<TextDirection> _layout =
      ValueNotifier<TextDirection>(TextDirection.ltr);
  final ValueNotifier<bool> _switchlayout = ValueNotifier<bool>(false);

  Color? differenttSkinColor;
  Color navigationColor = ColorConst.drawerBG;

  @override
  Widget build(BuildContext context) {
    return AutoTabsRouter(
      routes: _routes,
      builder: (context, child) {
        final tabsRouter = AutoTabsRouter.of(context);
        autoTabRouter = tabsRouter;
        return ValueListenableBuilder<TextDirection>(
          valueListenable: _layout,
          builder: (context, value, _) {
            return Directionality(
              textDirection: value,
              child: Scaffold(
                backgroundColor: differenttSkinColor,
                appBar: _appBar(tabsRouter),
                body: Stack(
                  children: [
                    Scaffold(
                      backgroundColor: differenttSkinColor,
                      key: _scaffoldDrawerKey,
                      drawerScrimColor: ColorConst.transparent,
                      drawer: _sidebar(tabsRouter),
                      body: Row(
                        mainAxisSize: MainAxisSize.max,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          ValueListenableBuilder<bool>(
                            valueListenable: isOpen,
                            builder: (context, value, child) {
                              return Responsive.isWeb(context)
                                  ? _sidebar(tabsRouter)
                                  : const SizedBox.shrink();
                            },
                          ),
                          Responsive.isWeb(context)
                              ? _globalDiver(true)
                              : const SizedBox.shrink(),
                          Expanded(
                            child: CustomScrollView(
                              controller: _scrollController,
                              slivers: [
                                SliverList(
                                  delegate: SliverChildListDelegate(
                                    [
                                      Responsive.isWeb(context)
                                          ? _globalDiver(false)
                                          : const SizedBox.shrink(),
                                      Container(
                                        padding: const EdgeInsets.symmetric(
                                            horizontal: 24.0),
                                        child: Column(
                                          crossAxisAlignment:
                                              CrossAxisAlignment.start,
                                          children: [
                                            getRouteWidget(
                                                tabsRouter.activeIndex),
                                            FxBox.h20,
                                          ],
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                                const SliverFillRemaining(
                                  hasScrollBody: false,
                                  fillOverscroll: true,
                                  child: Column(
                                    children: <Widget>[
                                      Expanded(
                                        child: SizedBox.shrink(),
                                      ),
                                      Footer(),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            );
          },
        );
      },
    );
  }

  /// appbar
  PreferredSizeWidget _appBar(TabsRouter tabsRouter) => AppBar(
        toolbarHeight: 70,
        elevation: 0.0,
        shadowColor: ColorConst.transparent,
        leadingWidth: 392,
        leading: Row(
          children: [
            ValueListenableBuilder<bool>(
              valueListenable: isOpen,
              builder: (context, value, child) {
                if (Responsive.isWeb(context) && value) {
                  _scaffoldDrawerKey.currentState!.closeDrawer();
                  return InkWell(
                      onTap: () {
                        tabsRouter
                            .setActiveIndex(getRouteIndex(Strings.dashboard));
                      },
                      child: Container(
                          width: 240,
                          padding: const EdgeInsets.symmetric(horizontal: 61),
                          height: double.infinity,
                          color: ColorConst.transparent,
                          child: Logo(
                              color: isDark ? Colors.white : Colors.black)));
                }
                return InkWell(
                  onTap: () {
                    tabsRouter.setActiveIndex(getRouteIndex(Strings.dashboard));
                    _scaffoldDrawerKey.currentState?.closeDrawer();
                  },
                  child: Container(
                      width: 60,
                      height: double.infinity,
                      color:
                          isDark ? ColorConst.transparent : ColorConst.drawerBG,
                      padding: const EdgeInsets.all(8.0),
                      child: Logo(color: isDark ? Colors.white : Colors.black)),
                );
              },
            ),
            MaterialButton(
              height: double.infinity,
              minWidth: 60,
              hoverColor: ColorConst.transparent,
              onPressed: () async {
                if (Responsive.isMobile(context) ||
                    Responsive.isTablet(context)) {
                  if (_scaffoldDrawerKey.currentState!.isDrawerOpen) {
                    _scaffoldDrawerKey.currentState!.closeDrawer();
                  } else {
                    _scaffoldDrawerKey.currentState!.openDrawer();
                  }
                } else if (Responsive.isWeb(context)) {
                  _scaffoldDrawerKey.currentState!.closeDrawer();
                  isOpen.value = !isOpen.value;
                }
              },
              child: const SvgIcon(icon: IconlyBroken.drawer),
            )
          ],
        ),
        actions: [
          if (Responsive.isWeb(context)) ...[
            ValueListenableBuilder<bool>(
              valueListenable: _switchlayout,
              builder: (context, value, _) {
                return Text(value == true ? 'RTL' : 'LTR');
              },
            ),
            ValueListenableBuilder<bool>(
              valueListenable: _switchlayout,
              builder: (context, value, _) {
                return Transform.scale(
                  scale: 0.7,
                  child: Switch(
                    value: value,
                    onChanged: (value) {
                      // languageModel.changeLanguage();
                      _switchlayout.value = value;
                      value == true
                          ? _layout.value = TextDirection.rtl
                          : _layout.value = TextDirection.ltr;
                    },
                  ),
                );
              },
            ),
            BlocBuilder<ThemeModeBloc, ThemeModeState>(
              builder: (context, state) {
                return Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Row(
                    children: [
                      IconButton(
                        onPressed: () {
                          HiveUtils.set(HiveKeys.themeMode, !isDark);
                          themeModeBloc
                              .add(ThemeModeEvent.changeTheme(!isDark));
                        },
                        icon: Icon(
                          isDark
                              ? Icons.light_mode_outlined
                              : Icons.dark_mode_outlined,
                        ),
                      ),
                    ],
                  ),
                );
              },
            ),
          ],
          _logoutButton(),
          if (!Responsive.isWeb(context))
            MenuBar(
              style: MenuStyle(
                backgroundColor:
                    MaterialStateProperty.all(ColorConst.transparent),
                elevation: MaterialStateProperty.all(0.0),
                fixedSize: const MaterialStatePropertyAll(Size(60.0, 60.0)),
              ),
              children: [
                SubmenuButton(
                  style: ButtonStyle(
                    fixedSize: const MaterialStatePropertyAll(Size(40.0, 40.0)),
                    shape: MaterialStatePropertyAll(
                      RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(100.0)),
                    ),
                  ),
                  menuStyle: const MenuStyle(
                    padding: MaterialStatePropertyAll(EdgeInsets.zero),
                  ),
                  menuChildren: [
                    SizedBox(
                      height: 40.0,
                      child: MenuItemButton(
                        onPressed: () {
                          HiveUtils.set(HiveKeys.themeMode, !isDark);
                          themeModeBloc
                              .add(ThemeModeEvent.changeTheme(!isDark));
                        },
                        child: MenuAcceleratorLabel(
                            isDark ? 'Light mode' : 'Dark mode'),
                      ),
                    ),
                    SizedBox(
                      height: 40.0,
                      child: MenuItemButton(
                        onPressed: () {
                          _switchlayout.value = !_switchlayout.value;
                          _switchlayout.value == true
                              ? _layout.value = TextDirection.rtl
                              : _layout.value = TextDirection.ltr;
                        },
                        child: MenuAcceleratorLabel(
                            _switchlayout.value ? 'LTR' : 'RTL'),
                      ),
                    ),
                  ],
                  child: const SvgIcon(icon: IconlyBroken.setting),
                ),
              ],
            ),
        ],
      );
  Widget _logoutButton() {
    return FxDropdownButton(
      focusColor: Colors.transparent,
      underline: FxBox.shrink,
      customButton: MaterialButton(
        height: double.infinity,
        minWidth: 60,
        hoverColor: ColorConst.transparent,
        onPressed: () async {
          // Action on button press
          await Provider.of<Auth>(context, listen: false).logout();
        },
        child: const Text(
          Strings.logout,
          style: TextStyle(
            fontSize: 15,
            fontWeight: FontWeight.bold,
          ),
          overflow: TextOverflow.ellipsis,
        ),
      ),
      customItemsIndexes: const [],
      customItemsHeight: 0,
      onChanged: (value) {},
      items: const [],
      itemHeight: 0,
      itemPadding: const EdgeInsets.only(left: 16, right: 16),
      dropdownWidth: 160,
      dropdownPadding: const EdgeInsets.symmetric(vertical: 6),
      dropdownDecoration: BoxDecoration(
        color: isDark ? ColorConst.cardDark : Colors.white,
        border: Border.all(
          color: isDark
              ? ColorConst.lightGrey.withOpacity(0.1)
              : ColorConst.lightGrey.withOpacity(0.5),
        ),
        borderRadius: BorderRadius.circular(4.0),
      ),
      dropdownElevation: 0,
      offset: const Offset(-108, 0),
    );
  }

  /// drawer / sidebar
  Widget _sidebar(TabsRouter tabsRouter) => ValueListenableBuilder<bool>(
        valueListenable: isSubListOpen,
        builder: (context, value1, child) {
          return ValueListenableBuilder<bool>(
            valueListenable: isOpen,
            builder: (context, value, child) {
              return Container(
                height: MediaQuery.of(context).size.height,
                width: value || value1 ? 240 : 70,
                color: Theme.of(context).scaffoldBackgroundColor,
                child: SingleChildScrollView(
                  controller: ScrollController(),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Responsive.isWeb(context)
                          ? _globalDiver(false)
                          : const SizedBox.shrink(),
                      FxBox.h8,
                      // main
                      if (value) _menuHeading('Main'),
                      _menuList(
                        tabsRouter: tabsRouter,
                        items: mainData,
                        isopen: value,
                        isSubListopen: value1,
                      ),

                      FxBox.h20,
                    ],
                  ),
                ),
              );
            },
          );
        },
      );

  /// menu heading
  Widget _menuHeading(String title) {
    return Container(
      padding: const EdgeInsets.only(left: 18),
      height: 40,
      alignment: Alignment.centerLeft,
      child: Text(
        title.toUpperCase(),
        style: TextStyle(
          color: isDark ? ColorConst.white : ColorConst.black,
          fontSize: 11,
        ),
      ),
    );
  }

  /// menu list
  Widget _menuList({
    required TabsRouter tabsRouter,
    required Map<String, String> items,
    bool isExpanded = false,
    List<List<String>> children = const [],
    required bool isopen,
    bool isSubListopen = false,
  }) {
    return ListView.builder(
      physics: const NeverScrollableScrollPhysics(),
      shrinkWrap: true,
      itemCount: items.length,
      itemBuilder: (context, index) {
        return FxHover(
          builder: (isHover) {
            Color color = isHover
                ? isDark
                    ? ColorConst.chartForgoundColor
                    : ColorConst.primary
                : isDark
                    ? ColorConst.white
                    : ColorConst.black;
            if (isExpanded) {
              return isopen
                  ? FxExpansionTile(
                      leading: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          SvgIcon(
                            icon: items.values.elementAt(index),
                            size: 16,
                            color: children[index]
                                    .contains(upperCase(tabsRouter.currentPath))
                                ? isDark
                                    ? ColorConst.chartForgoundColor
                                    : ColorConst.primary
                                : color,
                          ),
                          const SizedBox(width: 24.0), // Adjust width as needed
                        ],
                      ),
                      title: Text(
                        items.keys.elementAt(index).camelCase(),
                        style: TextStyle(
                          color: children[index]
                                  .contains(upperCase(tabsRouter.currentPath))
                              ? isDark
                                  ? ColorConst.chartForgoundColor
                                  : ColorConst.primary
                              : color,
                          fontSize: 15.7,
                        ),
                      ),
                      // Add other properties for FxExpansionTile as needed
                    )
                  : ListTile(
                      leading: SizedBox(
                        width: 40.0, // Set a fixed width for the leading icon
                        child: SvgIcon(
                          icon: items.values.elementAt(index),
                          size: 16,
                          color: items.keys.elementAt(index) ==
                                  upperCase(tabsRouter.currentPath)
                              ? isDark
                                  ? ColorConst.chartForgoundColor
                                  : ColorConst.primary
                              : color,
                        ),
                      ),
                      title: isopen
                          ? Text(
                              items.keys.elementAt(index).camelCase(),
                              style: TextStyle(
                                color: items.keys.elementAt(index) ==
                                        upperCase(tabsRouter.currentPath)
                                    ? isDark
                                        ? ColorConst.chartForgoundColor
                                        : ColorConst.primary
                                    : color,
                                fontSize: 15.7,
                              ),
                            )
                          : null,
                      onTap: () {
                        isOpen.value = true;
                        _scaffoldDrawerKey.currentState?.closeDrawer();
                      },
                      // Add other properties for ListTile as needed
                    );
            } else {
              return Row(
                children: [
                  if (isopen)
                    Container(
                      width: 6.0,
                      height: 48.0,
                      decoration: BoxDecoration(
                        color: items.keys.elementAt(index) ==
                                upperCase(tabsRouter.currentPath)
                            ? isDark
                                ? ColorConst.chartForgoundColor
                                : ColorConst.primary
                            : ColorConst.transparent,
                        borderRadius: const BorderRadius.only(
                          topRight: Radius.circular(6.0),
                          bottomRight: Radius.circular(6.0),
                        ),
                      ),
                    ),
                  if (isopen) FxBox.w16,
                  Expanded(
                    child: ListTile(
                      leading: SvgIcon(
                        icon: items.values.elementAt(index),
                        size: 12, // Adjust this size as needed
                        color: items.keys.elementAt(index) ==
                                upperCase(tabsRouter.currentPath)
                            ? isDark
                                ? ColorConst.chartForgoundColor
                                : ColorConst.primary
                            : color,
                      ),
                      title: isopen
                          ? Text(
                              items.keys.elementAt(index).camelCase(),
                              style: TextStyle(
                                color: items.keys.elementAt(index) ==
                                        upperCase(tabsRouter.currentPath)
                                    ? isDark
                                        ? ColorConst.chartForgoundColor
                                        : ColorConst.primary
                                    : color,
                                fontSize: 12,
                              ),
                            )
                          : null,
                      mouseCursor: SystemMouseCursors.click,
                      horizontalTitleGap: 0.0,
                      onTap: () {
                        isOpen.value = true;
                        tabsRouter.setActiveIndex(
                            getRouteIndex(items.keys.elementAt(index)));
                        HiveUtils.set(Strings.selectedmenuIndex,
                            getRouteIndex(items.keys.elementAt(index)));
                        _scaffoldDrawerKey.currentState?.closeDrawer();
                      },
                    ),
                  ),
                ],
              );
            }
          },
        );
      },
    );
  }

  Widget _globalDiver(bool? vertical) {
    return vertical == true
        ? VerticalDivider(
            width: 2,
            color: Colors.grey.withOpacity(0.1),
          )
        : const Divider(
            height: 2,
            color: Colors.transparent,
          );
  }

  /// sub menu list
  Widget _subMenuList(List<String> items, TabsRouter tabsRouter) {
    return ListView.builder(
      shrinkWrap: true,
      itemCount: items.length,
      physics: const NeverScrollableScrollPhysics(),
      itemBuilder: (context, index) => FxHover(
        builder: (isHover) {
          Color color = isHover
              ? isDark
                  ? ColorConst.chartForgoundColor
                  : ColorConst.primary
              : upperCase(tabsRouter.currentPath) == items[index]
                  ? isDark
                      ? ColorConst.chartForgoundColor
                      : ColorConst.primary
                  : isDark
                      ? ColorConst.white
                      : ColorConst.black;
          return Row(
            children: [
              Container(
                width: 6.0,
                height: 48.0,
                decoration: BoxDecoration(
                  color: upperCase(tabsRouter.currentPath) == items[index]
                      ? isDark
                          ? ColorConst.chartForgoundColor
                          : ColorConst.primary
                      : ColorConst.transparent,
                  borderRadius: const BorderRadius.only(
                    topRight: Radius.circular(6.0),
                    bottomRight: Radius.circular(6.0),
                  ),
                ),
              ),
              Expanded(
                child: ListTile(
                  dense: true,
                  visualDensity: const VisualDensity(vertical: -3),
                  mouseCursor: SystemMouseCursors.click,
                  contentPadding: _switchlayout.value == false
                      ? const EdgeInsets.only(left: 52.0)
                      : const EdgeInsets.only(right: 52.0),
                  title: Text(
                    // items[index],
                    items[index].camelCase(),
                    style: TextStyle(color: color, fontSize: 15),
                  ),
                  onTap: () {
                    tabsRouter.setActiveIndex(getRouteIndex(items[index]));
                    _scaffoldDrawerKey.currentState?.closeDrawer();
                  },
                ),
              ),
            ],
          );
        },
      ),
    );
  }
}
