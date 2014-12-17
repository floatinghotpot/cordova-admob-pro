//
//  GADAppEventDelegate.h
//  Google Mobile Ads SDK
//
//  Copyright 2012 Google Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class GADBannerView;
@class GADInterstitial;

@protocol GADAppEventDelegate<NSObject>

// Implement your app event within these methods. The delegate will be notified when the SDK
// receives an app event message from the ad.
@optional

- (void)adView:(GADBannerView *)banner
    didReceiveAppEvent:(NSString *)name
              withInfo:(NSString *)info;

- (void)interstitial:(GADInterstitial *)interstitial
    didReceiveAppEvent:(NSString *)name
              withInfo:(NSString *)info;

@end
