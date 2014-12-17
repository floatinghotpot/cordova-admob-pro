//
//  DFPSwipeableBannerView.h
//  Google Mobile Ads SDK
//
//  Copyright 2012 Google Inc. All rights reserved.
//

#import "DFPBannerView.h"

/// Deprecated swipeable banner view. Use DFPBannerView.
__attribute__((deprecated("Use DFPBannerView.")))
@interface DFPSwipeableBannerView  : DFPBannerView

/// Set a delegate to be notified when the user activates and deactivates an ad. Remember to nil out
/// the delegate before releasing this banner.
@property(nonatomic, weak) id swipeDelegate __attribute__((deprecated("Use DFPBannerView.")));

@end
