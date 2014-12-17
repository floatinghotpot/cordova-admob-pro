//
//  GADSwipeableBannerViewDelegate.h
//  Google Mobile Ads SDK
//
//  Copyright 2012 Google Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class GADBannerView;

// The delegate will be notified when a user activates and deactivates an ad. If the
// DFPSwipeableBannerView is contained within a UIScrollView, make sure to set scrollEnabled to NO
// when -adViewDidActivateAd: is called and to set back to YES when -adViewDidDeactivateAd: is
// called.
@protocol GADSwipeableBannerViewDelegate<NSObject>

@optional

- (void)adViewDidActivateAd:(GADBannerView *)banner;

- (void)adViewDidDeactivateAd:(GADBannerView *)banner;

@end
