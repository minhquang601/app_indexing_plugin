#import "AppIndexingB2bPlugin.h"
#if __has_include(<app_indexing_b2b_plugin/app_indexing_b2b_plugin-Swift.h>)
#import <app_indexing_b2b_plugin/app_indexing_b2b_plugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "app_indexing_b2b_plugin-Swift.h"
#endif

@implementation AppIndexingB2bPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAppIndexingB2bPlugin registerWithRegistrar:registrar];
}
@end
