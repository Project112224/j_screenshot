// import 'package:flutter_test/flutter_test.dart';
// import 'package:j_screenshot/j_screenshot.dart';
// import 'package:j_screenshot/j_screenshot_platform_interface.dart';
// import 'package:j_screenshot/j_screenshot_method_channel.dart';
// import 'package:plugin_platform_interface/plugin_platform_interface.dart';
//
// class MockJScreenshotPlatform
//     with MockPlatformInterfaceMixin
//     implements JScreenshotPlatform {
//
//   @override
//   Future<String?> getPlatformVersion() => Future.value('42');
// }
//
// void main() {
//   final JScreenshotPlatform initialPlatform = JScreenshotPlatform.instance;
//
//   test('$MethodChannelJScreenshot is the default instance', () {
//     expect(initialPlatform, isInstanceOf<MethodChannelJScreenshot>());
//   });
//
//   test('getPlatformVersion', () async {
//     JScreenshot jScreenshotPlugin = JScreenshot();
//     MockJScreenshotPlatform fakePlatform = MockJScreenshotPlatform();
//     JScreenshotPlatform.instance = fakePlatform;
//
//     expect(await jScreenshotPlugin.getPlatformVersion(), '42');
//   });
// }
