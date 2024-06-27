import 'dart:ui';

import 'package:j_screenshot/j_screenshot.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'j_screenshot_method_channel.dart';

abstract class JScreenshotPlatform extends PlatformInterface {
  /// Constructs a JScreenshotPlatform.
  JScreenshotPlatform() : super(token: _token);

  static final Object _token = Object();

  static JScreenshotPlatform _instance = MethodChannelJScreenshot();

  /// The default instance of [JScreenshotPlatform] to use.
  ///
  /// Defaults to [MethodChannelJScreenshot].
  static JScreenshotPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [JScreenshotPlatform] when
  /// they register themselves.
  static set instance(JScreenshotPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool?> get hasPermissions;

  Future<void> initialize() {
    throw UnimplementedError('initialize() has not been implemented.');
  }

  addListener(ImagePathCallback callback) {
    throw UnimplementedError('addListener() has not been implemented.');
  }

  Future<void> dispose() {
    throw UnimplementedError('dispose() has not been implemented.');
  }
}
