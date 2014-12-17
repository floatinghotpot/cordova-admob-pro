//
//  DFPInterstitial.h
//  Google Mobile Ads SDK
//
//  Copyright 2012 Google Inc. All rights reserved.
//

#import "GADInterstitial.h"

@protocol GADAppEventDelegate;

@interface DFPInterstitial : GADInterstitial

@property(nonatomic, weak) id<GADAppEventDelegate> appEventDelegate;

@end
