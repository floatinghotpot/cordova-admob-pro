//
//  GADSearchRequest.h
//  Google Mobile Ads SDK
//
//  Copyright 2011 Google Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@class GADRequest;

// Types of borders for search ads.
typedef NS_ENUM(NSUInteger, GADSearchBorderType) {
  kGADSearchBorderTypeNone,
  kGADSearchBorderTypeDashed,
  kGADSearchBorderTypeDotted,
  kGADSearchBorderTypeSolid
};

typedef NS_ENUM(NSUInteger, GADSearchCallButtonColor) {
  kGADSearchCallButtonLight,
  kGADSearchCallButtonMedium,
  kGADSearchCallButtonDark
};

// Specifies parameters and controls for search ads.
@interface GADSearchRequest : NSObject

@property(nonatomic, copy) NSString *query;
@property(nonatomic, strong, readonly) UIColor *backgroundColor;
@property(nonatomic, strong, readonly) UIColor *gradientFrom;
@property(nonatomic, strong, readonly) UIColor *gradientTo;
@property(nonatomic, strong) UIColor *headerColor;
@property(nonatomic, strong) UIColor *descriptionTextColor;
@property(nonatomic, strong) UIColor *anchorTextColor;
@property(nonatomic, copy) NSString *fontFamily;
@property(nonatomic, assign) NSUInteger headerTextSize;
@property(nonatomic, strong) UIColor *borderColor;
@property(nonatomic, assign) GADSearchBorderType borderType;
@property(nonatomic, assign) NSUInteger borderThickness;
@property(nonatomic, copy) NSString *customChannels;
@property(nonatomic, assign) GADSearchCallButtonColor callButtonColor;

// The request object used to request ad. Pass the value returned by the method
// to GADSearchBannerView to get the ad in the format specified.
- (GADRequest *)request;

// A solid background color for rendering the ad. The background of the ad
// can either be a solid color, or a gradient, which can be specified through
// setBackgroundGradientFrom:toColor: method. If both solid and gradient
// background is requested, only the latter is considered.
- (void)setBackgroundSolid:(UIColor *)color;

// A linear gradient background color for rendering the ad. The background of
// the ad can either be a linear gradient, or a solid color, which can be
// specified through setBackgroundSolid method. If both solid and gradient
// background is requested, only the latter is considered.
- (void)setBackgroundGradientFrom:(UIColor *)from toColor:(UIColor *)toColor;

@end
