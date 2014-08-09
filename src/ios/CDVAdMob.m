
#import "CDVAdMob.h"

@interface CDVAdMob()

@property(nonatomic, retain) AdMobAds* _admobApi;

@end

@implementation CDVAdMob
@synthesize _admobApi;

- (UIView*) getView {
    return self.webView;
}

- (UIViewController*) getViewController {
    return self.viewController;
}

- (void) onEvent:(NSString *)eventType withData:(NSString *)jsonString {
    if(jsonString != nil) {
        [self writeJavascript:[NSString stringWithFormat:@"cordova.fireDocumentEvent('%@', %@ );", eventType, jsonString]];
    } else {
        [self writeJavascript:[NSString stringWithFormat:@"cordova.fireDocumentEvent('%@');", eventType]];
    }
}

- (CDVPlugin *)initWithWebView:(UIWebView *)theWebView {
	self = (CDVAdMob *)[super initWithWebView:theWebView];
	if (self) {
        _admobApi = [[AdMobAds alloc] init:self];
	}
    
	return self;
}

- (void) setOptions:(CDVInvokedUrlCommand *)command {
    NSLog(@"setOptions");
    
    if([command.arguments count] > 0) {
        NSDictionary* options = [command argumentAtIndex:0 withDefault:[NSNull null]];
        [_admobApi setOptions:options];
    }
    
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}


- (void)createBanner:(CDVInvokedUrlCommand *)command {
    NSLog(@"createBanner");

    if([command.arguments count] > 0) {
        NSDictionary* options = [command argumentAtIndex:0 withDefault:[NSNull null]];
        if([options count] > 1) {
            [_admobApi setOptions:options];
        }
        
        NSString* adId = [options objectForKey:@"adId"];
        [_admobApi createBanner:adId];
        
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];

    } else {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"bannerId needed"] callbackId:command.callbackId];
    }
}

- (void)removeBanner:(CDVInvokedUrlCommand *)command {
    NSLog(@"removeBanner");

    [_admobApi removeBanner];
    
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

- (void) showBanner:(CDVInvokedUrlCommand *)command {
    
    int position = [[command argumentAtIndex:0 withDefault:@"2"] intValue];
    
    NSLog(@"showBanner:%d", position);
    
    [_admobApi showBanner:position];
    
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

- (void) showBannerAtXY:(CDVInvokedUrlCommand *)command {
    NSDictionary *params = [command argumentAtIndex:0];
    int x = [params integerValueForKey:@"x" defaultValue:0];
    int y = [params integerValueForKey:@"y" defaultValue:0];
    [_admobApi showBannerAtX:x withY:y];
    
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

- (void) hideBanner:(CDVInvokedUrlCommand *)command {
    NSLog(@"hideBanner");
    
    [_admobApi hideBanner];
    
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

- (void) prepareInterstitial:(CDVInvokedUrlCommand *)command {
    NSLog(@"prepareInterstitial");
    
    if([command.arguments count] > 0) {
        NSDictionary* options = [command argumentAtIndex:0 withDefault:[NSNull null]];
        if([options count] > 1) {
            [_admobApi setOptions:options];
        }
        
        NSString* adId = [options objectForKey:@"adId"];
        [_admobApi prepareInterstitial:adId];
        
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
        
    } else {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"interstitialId needed"] callbackId:command.callbackId];
    }
}

- (void)isInterstitialReady:(CDVInvokedUrlCommand *)command {
    BOOL isready = [_admobApi isInterstitialReady];
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:isready] callbackId:command.callbackId];
}


- (void) showInterstitial:(CDVInvokedUrlCommand *)command
{
    NSLog(@"prepareInterstitial");
    
    [_admobApi showInterstitial];
    
    [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
}

@end
