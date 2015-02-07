//
//  AdMobMediation.h
//  TestAdMobCombo
//
//  Created by Xie Liming on 14-10-20.
//
//

#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>

#import <GoogleMobileAds/GoogleMobileAds.h>

@interface AdMobMediation : NSObject

- (AdMobMediation*) initWithOptions:(NSDictionary*)options;
- (void) joinAdRequest:(GADRequest*)req;
- (void) onPause;
- (void) onResume;
- (void) onDestroy;

@end

// -------------------------------------------------------------

// AdMobMediationMMedia

// AdMobMediationiAd, no need extras

// AdMobMediationInMobi, no need extras

// AdMobMediationFacebook, no need extras

// AdMobMediationMobFox, no need extras

// AdMobMediationFlurry, no need extras
