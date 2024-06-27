import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:j_screenshot/j_screenshot.dart';

import 'j_screenshot_platform_interface.dart';

class MethodChannelJScreenshot extends JScreenshotPlatform {

  @visibleForTesting
  final methodChannel = const MethodChannel('j_screenshot');

  List<ImagePathCallback> onCallbacks = <ImagePathCallback>[];

  @override
  Future<bool?> get hasPermissions async {
    return await methodChannel.invokeMethod('permission');
  }

  @override
  Future<void> initialize() async {
    methodChannel.setMethodCallHandler(_handleMethod);
    await methodChannel.invokeMethod('initialize');
  }

  // permission

  Future<dynamic> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case 'onCallback':
        for (final callback in onCallbacks) {
          final imagePath = call.arguments as String?;
          callback(imagePath);
        }
        break;
      default:
        throw ('method not defined');
    }
  }

  @override
  addListener(ImagePathCallback callback) {
    onCallbacks.add(callback);
  }

  @override
  Future<void> dispose() async => await methodChannel.invokeMethod('dispose');
}
