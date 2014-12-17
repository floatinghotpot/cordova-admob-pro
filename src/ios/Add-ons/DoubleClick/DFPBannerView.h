//
//  DFPBannerView.h
//  Google Mobile Ads SDK
//
//  Copyright 2012 Google Inc. All rights reserved.
//

#import "GADBannerView.h"

@protocol GADAdSizeDelegate;
@protocol GADAppEventDelegate;

/// The view that displays DoubleClick For Publishers banner ads.
@interface DFPBannerView : GADBannerView

/// Optional delegate that is notified when creatives send app events. To avoid crashing the app,
/// remember to nil this property before releasing the object that implements the
/// GADAppEventDelegate protocol.
@property(nonatomic, weak) id<GADAppEventDelegate> appEventDelegate;

/// Optional delegate that is notified when creatives cause the banner to change size. To avoid
/// crashing the app, remember to nil this property before releasing the object that implements the
/// GADAdSizeDelegate protocol.
@property(nonatomic, weak) id<GADAdSizeDelegate> adSizeDelegate;

/// Optional array of NSValue encoded GADAdSize structs, specifying all valid sizes that are
/// appropriate for this slot. Never create your own GADAdSize directly. Use one of the predefined
/// standard ad sizes (such as kGADAdSizeBanner), or create one using the GADAdSizeFromCGSize
/// method.
///
/// \see setValidAdSizesWithSizes:
///
/// Example:
///   \code
///   GADAdSize size1 = kGADAdSizeBanner;
///   GADAdSize size2 = kGADAdSizeLargeBanner;
///   NSArray *validSizes = @[
///     [NSValue valueWithBytes:&size1 objCType:@encode(GADAdSize)],
///     [NSValue valueWithBytes:&size2 objCType:@encode(GADAdSize)]
///   ];
///
///   bannerView.validAdSizes = validSizes;
///   \endcode
@property(nonatomic, strong) NSArray *validAdSizes;

/// Indicates that the publisher will record impressions manually when the ad becomes visible to the
/// user.
@property(nonatomic, assign) BOOL enableManualImpressions;

/// If you've set enableManualImpressions to YES, call this method when the ad is visible.
- (void)recordImpression;

/// Use this function to resize the banner view without launching a new ad request.
- (void)resize:(GADAdSize)size;

/// Sets the receiver's valid ad sizes to the values pointed to by the provided NULL terminated list
/// of GADAdSize pointers.
///
/// Example:
///   \code
///   GADAdSize size1 = kGADAdSizeBanner;
///   GADAdSize size2 = kGADAdSizeLargeBanner;
///   [bannerView setValidAdSizesWithSizes:&size1, &size2, NULL];
///   \endcode
- (void)setValidAdSizesWithSizes:(GADAdSize *)firstSize, ... NS_REQUIRES_NIL_TERMINATION;

@end
