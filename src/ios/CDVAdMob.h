#import <Cordova/CDV.h>
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import <AdMobAds/AdMobAds.h>

#pragma mark AdMob Plugin

@interface CDVAdMob : CDVPlugin <AdMobEventDelegate> {
}

- (void) setOptions:(CDVInvokedUrlCommand *)command;

- (void)createBanner:(CDVInvokedUrlCommand *)command;
- (void)showBanner:(CDVInvokedUrlCommand *)command;
- (void)showBannerAtXY:(CDVInvokedUrlCommand *)command;
- (void)hideBanner:(CDVInvokedUrlCommand *)command;
- (void)removeBanner:(CDVInvokedUrlCommand *)command;

- (void)prepareInterstitial:(CDVInvokedUrlCommand *)command;
- (void)showInterstitial:(CDVInvokedUrlCommand *)command;
- (void)isInterstitialReady:(CDVInvokedUrlCommand *)command;

@end
