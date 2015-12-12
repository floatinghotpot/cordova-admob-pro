//
//  GADMobileAds.h
//  Google Mobile Ads SDK
//
//  Copyright 2015 Google Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface GADMobileAds : NSObject

/// Returns the shared GADMobileAds instance.
+ (GADMobileAds *)sharedInstance;

/// Disables automated in app purchase (IAP) reporting. Must be called before any IAP transaction is
/// initiated. IAP reporting is used to track IAP ad conversions. Do not disable reporting if you
/// use IAP ads.
+ (void)disableAutomatedInAppPurchaseReporting;

/// Disables automated SDK crash reporting. If not called, the SDK records the original exception
/// handler if available and registers a new exception handler. The new exception handler only
/// reports SDK related exceptions and calls the recorded original exception handler.
+ (void)disableSDKCrashReporting;

/// The application's audio volume. Affects audio volumes of all ads relative to other audio output.
/// Valid ad volume values range from 0.0 (silent) to 1.0 (current device volume). Use this method
/// only if your application has its own volume controls (e.g., custom music or sound effect
/// volumes).
@property(nonatomic, assign) float applicationVolume;

@end
