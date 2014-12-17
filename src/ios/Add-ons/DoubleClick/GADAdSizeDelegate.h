//
//  GADAdSizeDelegate.h
//  Google Mobile Ads SDK
//
//  Copyright 2012 Google Inc. All rights reserved.
//
//  The class implementing this protocol will be notified when the DFPBannerView
//  changes ad size. Any views that may be affected by the banner size change
//  will have time to adjust.
//

#import <Foundation/Foundation.h>

#import "GADAdSize.h"

@class GADBannerView;

@protocol GADAdSizeDelegate<NSObject>

- (void)adView:(GADBannerView *)view willChangeAdSizeTo:(GADAdSize)size;

@end
