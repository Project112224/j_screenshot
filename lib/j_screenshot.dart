
import 'j_screenshot_platform_interface.dart';

typedef ImagePathCallback = void Function(String?);

class JScreenshot {

  JScreenshot() {
    JScreenshotPlatform.instance.initialize();
  }

  Future<bool?> get hasPermissions =>
      JScreenshotPlatform.instance.hasPermissions;

  addListener(ImagePathCallback callback) {
    return JScreenshotPlatform.instance.addListener(callback);
  }

  Future<void> dispose() {
    return JScreenshotPlatform.instance.dispose();
  }
}
