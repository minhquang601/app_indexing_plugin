import Flutter
import UIKit

public class SwiftAppIndexingB2bPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "app_indexing_b2b_plugin", binaryMessenger: registrar.messenger())
    let instance = SwiftAppIndexingB2bPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion)
  }
}
