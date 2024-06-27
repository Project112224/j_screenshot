import Flutter
import UIKit

public class JScreenshotPlugin: NSObject, FlutterPlugin {

  static var channel: FlutterMethodChannel?
  static var observer: NSObjectProtocol?;

  deinit {
      if(JScreenshotPlugin.observer != nil) {
          NotificationCenter.default.removeObserver(JScreenshotPlugin.observer!);
          JScreenshotPlugin.observer = nil;
      }
  }

    public static func register(with registrar: FlutterPluginRegistrar) {
        channel = FlutterMethodChannel(name: "j_screenshot", binaryMessenger: registrar.messenger())
        observer = nil
        let instance = JScreenshotPlugin()
        if let channel = channel {
            registrar.addMethodCallDelegate(instance, channel: channel)
        }
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch(call.method) {
        case "permission" : handleHasPermission(result: result)
        case "initialize": handleInitialize(result: result)
        case "dispose": handleDispose(result: result)
        default: result("")
        }
    }
    
    func handleHasPermission(result: @escaping FlutterResult) {
        result("initialize")
    }
    
    
    func handleInitialize(result: @escaping FlutterResult) {
        if(JScreenshotPlugin.observer != nil) {
            NotificationCenter.default.removeObserver(JScreenshotPlugin.observer!);
            JScreenshotPlugin.observer = nil;
        }
        JScreenshotPlugin.observer = NotificationCenter.default.addObserver(
            forName: UIApplication.userDidTakeScreenshotNotification,
            object: nil,
            queue: .main) 
        { notification in
            if let channel = JScreenshotPlugin.channel {
                channel.invokeMethod("onCallback", arguments: nil)
            }
            result("screen shot called")
        }
        result("initialize")
    }
    
    func handleDispose(result: @escaping FlutterResult) {
        if(JScreenshotPlugin.observer != nil) {
            NotificationCenter.default.removeObserver(JScreenshotPlugin.observer!);
            JScreenshotPlugin.observer = nil;
        }
        result("dispose")
    }
}
