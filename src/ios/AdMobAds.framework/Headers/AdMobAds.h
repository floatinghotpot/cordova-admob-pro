#import <Foundation/Foundation.h>
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "GADBannerViewDelegate.h"
#import "GADInterstitialDelegate.h"
#import "GADBannerView.h"
#import "GADInterstitial.h"
#import "GADAdMobExtras.h"
#import "GADAdSize.h"


#define OPT_BANNER_ID       @"bannerId"
#define OPT_AD_SIZE         @"adSize"
#define OPT_AD_WIDTH        @"width"
#define OPT_AD_HEIGHT       @"height"
#define OPT_POSITION        @"position"
#define OPT_X               @"x"
#define OPT_Y               @"y"

#define OPT_INTERSTITIAL_ID @"interstitialId"

#define OPT_IS_TESTING      @"isTesting"
#define OPT_AUTO_SHOW       @"autoShow"
#define OPT_AD_EXTRAS       @"adExtras"

#define OPT_ORIENTATION_RENEW   @"orientationRenew"

enum {
    POS_NO_CHANGE       = 0,
    POS_TOP_LEFT        = 1,
    POS_TOP_CENTER      = 2,
    POS_TOP_RIGHT       = 3,
    POS_LEFT            = 4,
    POS_CENTER          = 5,
    POS_RIGHT           = 6,
    POS_BOTTOM_LEFT     = 7,
    POS_BOTTOM_CENTER   = 8,
    POS_BOTTOM_RIGHT    = 9,
    POS_XY              = 10
};

@protocol AdMobEventDelegate <NSObject>
@required
-(UIView*) getView;
-(UIViewController*) getViewController;
-(void) onEvent:(NSString*) eventType withData:(NSString*) jsonString;
@end

@interface AdMobAds : NSObject<GADBannerViewDelegate, GADInterstitialDelegate>

- (AdMobAds*) init:(id<AdMobEventDelegate>) theDelegate;

- (void) setOptions:(NSDictionary*) options;

- (void) createBanner:(NSString*)adId;
- (void) showBanner:(int)position;
- (void) showBannerAtX:(int)x withY:(int)y;
- (void) hideBanner;
- (void) removeBanner;

- (void) prepareInterstitial:(NSString*)adId;
- (BOOL) isInterstitialReady;
- (void) showInterstitial;

@end
