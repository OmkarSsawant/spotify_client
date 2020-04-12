#import "SpotifyclientPlugin.h"
#if __has_include(<spotifyclient/spotifyclient-Swift.h>)
#import <spotifyclient/spotifyclient-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "spotifyclient-Swift.h"
#endif

@implementation SpotifyclientPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftSpotifyclientPlugin registerWithRegistrar:registrar];
}
@end
