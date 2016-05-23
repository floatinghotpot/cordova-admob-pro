//
//  GADVideoOptions.h
//  Google Mobile Ads SDK
//
//  Copyright 2016 Google Inc. All rights reserved.
//

#import <GoogleMobileAds/GADAdLoader.h>

/// Video ad options.
@interface GADVideoOptions : GADAdLoaderOptions

/// Indicates if videos should start muted.
@property(nonatomic, assign) BOOL startMuted;

@end
