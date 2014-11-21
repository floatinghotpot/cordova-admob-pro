//
//  CDVAdMobPlugin.h
//  TestAdMobCombo
//
//  Created by Xie Liming on 14-10-20.
//
//

#import "GenericAdPlugin.h"

@interface CDVAdMobPlugin : GenericAdPlugin

- (void)pluginInitialize;

- (void) parseOptions:(NSDictionary *)options;
- (void) __showBanner:(int) position atX:(int)x atY:(int)y;

- (NSString*) __getProductShortName;
- (NSString*) __getTestBannerId;
- (NSString*) __getTestInterstitialId;

- (UIView*) __createAdView:(NSString*)adId;
- (int) __getAdViewWidth:(UIView*)view;
- (int) __getAdViewHeight:(UIView*)view;
- (void) __loadAdView:(UIView*)view;
- (void) __pauseAdView:(UIView*)view;
- (void) __resumeAdView:(UIView*)view;
- (void) __destroyAdView:(UIView*)view;

- (NSObject*) __createInterstitial:(NSString*)adId;
- (void) __loadInterstitial:(NSObject*)interstitial;
- (void) __showInterstitial:(NSObject*)interstitial;
- (void) __destroyInterstitial:(NSObject*)interstitial;

@end
