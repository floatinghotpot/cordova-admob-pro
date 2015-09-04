//
//  CDVAdMobPlugin.m
//  TestAdMobCombo
//
//  Created by Xie Liming on 14-10-20.
//
//

#import <AdSupport/ASIdentifierManager.h>

#import <GoogleMobileAds/GoogleMobileAds.h>

#import <CoreLocation/CLLocation.h>

#import "CDVAdMobPlugin.h"
#import "AdMobMediation.h"

#define TEST_BANNER_ID           @"ca-app-pub-6869992474017983/1794817552"
#define TEST_INTERSTITIALID      @"ca-app-pub-6869992474017983/3271550750"

#define OPT_ADCOLONY        @"AdColony"
#define OPT_ADCOLONY        @"AdColony"
#define OPT_FLURRY          @"Flurry"
#define OPT_MMEDIA          @"mMedia"
#define OPT_INMOBI          @"InMobi"
#define OPT_FACEBOOK        @"Facebook"
#define OPT_MOBFOX          @"MobFox"
#define OPT_IAD             @"iAd"

#define OPT_LOCATION        @"location"

@interface CDVAdMobPlugin()<GADBannerViewDelegate, GADInterstitialDelegate>

@property (assign) GADAdSize adSize;
@property (nonatomic, retain) NSDictionary* adExtras;
@property (nonatomic, retain) NSMutableDictionary* mediations;

@property (assign) BOOL mLocationSet;
@property (assign) double mLatitude;
@property (assign) double mLongitude;

- (GADAdSize)__AdSizeFromString:(NSString *)str;
- (GADRequest*) __buildAdRequest:(BOOL)forBanner;
- (NSString *) __getAdMobDeviceId;

@end

@implementation CDVAdMobPlugin

- (void)pluginInitialize
{
    [super pluginInitialize];
    
    self.adSize = [self __AdSizeFromString:@"SMART_BANNER"];
    self.mediations = [[NSMutableDictionary alloc] init];
    
    self.mLocationSet = NO;
    self.mLatitude = 0.0;
    self.mLongitude = 0.0;
}

- (NSString*) __getProductShortName { return @"AdMob"; }

- (NSString*) __getTestBannerId {
    return TEST_BANNER_ID;
}
- (NSString*) __getTestInterstitialId {
    return TEST_INTERSTITIALID;
}

- (void) parseOptions:(NSDictionary *)options
{
    [super parseOptions:options];
    
    NSString* str = [options objectForKey:OPT_AD_SIZE];
    if(str) self.adSize = [self __AdSizeFromString:str];
    
    self.adExtras = [options objectForKey:OPT_AD_EXTRAS];
    
    if(self.mediations) {
        // TODO: if mediation need code in, add here
    }
    
    NSArray* arr = [options objectForKey:OPT_LOCATION];
    if(arr != nil) {
        self.mLatitude = [[arr objectAtIndex:0] doubleValue];
        self.mLongitude = [[arr objectAtIndex:1] doubleValue];
        self.mLocationSet = YES;
    }
}

- (UIView*) __createAdView:(NSString*)adId {
    
    if(GADAdSizeEqualToSize(self.adSize, kGADAdSizeInvalid)) {
        self.adSize = GADAdSizeFromCGSize( CGSizeMake(self.adWidth, self.adHeight) );
    }
    if(GADAdSizeEqualToSize(self.adSize, kGADAdSizeInvalid)) {
        self.adSize = [self __isLandscape] ? kGADAdSizeSmartBannerLandscape : kGADAdSizeSmartBannerPortrait;
    }
    
    GADBannerView* ad;
    
    if(* [adId UTF8String] == '/') {
        ad = [[DFPBannerView alloc] initWithAdSize:self.adSize];
    } else {
        ad = [[GADBannerView alloc] initWithAdSize:self.adSize];
    }
    
    ad.rootViewController = [self getViewController];
    ad.delegate = self;
    ad.adUnitID = adId;

    return ad;
}

- (GADRequest*) __buildAdRequest:(BOOL)forBanner
{
    GADRequest *request = [GADRequest request];
    
    if (self.isTesting) {
        NSString* deviceId = [self __getAdMobDeviceId];
        request.testDevices = [NSArray arrayWithObjects:deviceId, kGADSimulatorID, nil];
        NSLog(@"request.testDevices: %@, <Google> tips handled", deviceId);
    }
    
    if (self.adExtras) {
        GADExtras *extras = [[GADExtras alloc] init];
        NSMutableDictionary *modifiedExtrasDict = [[NSMutableDictionary alloc] initWithDictionary:self.adExtras];
        [modifiedExtrasDict removeObjectForKey:@"cordova"];
        [modifiedExtrasDict setValue:@"1" forKey:@"cordova"];
        extras.additionalParameters = modifiedExtrasDict;
        [request registerAdNetworkExtras:extras];
    }
    
    [self.mediations enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL *stop) {
        AdMobMediation* adMed = (AdMobMediation*) obj;
        if(adMed) {
            [adMed joinAdRequest:request];
        }
    }];
    
    if(self.mLocationSet) {
        [request setLocationWithLatitude:self.mLatitude longitude:self.mLongitude accuracy:kCLLocationAccuracyBest];
    }
    
    return request;
}

- (GADAdSize)__AdSizeFromString:(NSString *)str
{
    if ([str isEqualToString:@"BANNER"]) {
        return kGADAdSizeBanner;
    } else if ([str isEqualToString:@"SMART_BANNER"]) {
        // Have to choose the right Smart Banner constant according to orientation.
        if([self __isLandscape]) {
            return kGADAdSizeSmartBannerLandscape;
        }
        else {
            return kGADAdSizeSmartBannerPortrait;
        }
    } else if ([str isEqualToString:@"MEDIUM_RECTANGLE"]) {
        return kGADAdSizeMediumRectangle;
    } else if ([str isEqualToString:@"FULL_BANNER"]) {
        return kGADAdSizeFullBanner;
    } else if ([str isEqualToString:@"LEADERBOARD"]) {
        return kGADAdSizeLeaderboard;
    } else if ([str isEqualToString:@"SKYSCRAPER"]) {
        return kGADAdSizeSkyscraper;
    } else {
        return kGADAdSizeInvalid;
    }
}

- (NSString *) __getAdMobDeviceId
{
    NSUUID* adid = [[ASIdentifierManager sharedManager] advertisingIdentifier];
    return [self md5:adid.UUIDString];
}

- (void) __showBanner:(int) position atX:(int)x atY:(int)y
{
    GADBannerView* ad = (GADBannerView*) self.banner;
    if([self __isLandscape]) {
        if(GADAdSizeEqualToSize(ad.adSize, kGADAdSizeSmartBannerPortrait)) {
            if(self.isTesting) NSLog(@"change smart banner to landscape mode");
            ad.adSize = kGADAdSizeSmartBannerLandscape;
        }
    } else {
        if(GADAdSizeEqualToSize(ad.adSize, kGADAdSizeSmartBannerLandscape)) {
            if(self.isTesting) NSLog(@"change smart banner to portrait mode");
            ad.adSize = kGADAdSizeSmartBannerPortrait;
        }
    }

    [super __showBanner:position atX:x atY:y];
}

- (int) __getAdViewWidth:(UIView*)view {
    return view.frame.size.width;
}

- (int) __getAdViewHeight:(UIView*)view {
    return view.frame.size.height;
}

- (void) __loadAdView:(UIView*)view {
    if(! view) return;
    
    if([view class] == [DFPBannerView class]) {
        DFPBannerView* ad = (DFPBannerView*) view;
        [ad loadRequest:[self __buildAdRequest:true]];
    } else if([view class] == [GADBannerView class]) {
        GADBannerView* ad = (GADBannerView*) view;
        [ad loadRequest:[self __buildAdRequest:true]];
    }
}

- (void) __pauseAdView:(UIView*)view {
}

- (void) __resumeAdView:(UIView*)view {
}

- (void) __destroyAdView:(UIView*)view {
    if(! view) return;
    [view removeFromSuperview];
    
    if([view class] == [DFPBannerView class]) {
        DFPBannerView* ad = (DFPBannerView*) view;
        ad.delegate = nil;
    } else if([view class] == [GADBannerView class]) {
        GADBannerView* ad = (GADBannerView*) view;
        ad.delegate = nil;
    }
}

- (NSObject*) __createInterstitial:(NSString*)adId {
    GADInterstitial* ad = [[GADInterstitial alloc] initWithAdUnitID:adId];
    ad.delegate = self;
    return ad;
}

- (void) __loadInterstitial:(NSObject*)interstitial {
    GADInterstitial* ad = (GADInterstitial*) interstitial;
    if(ad) {
        [ad loadRequest:[self __buildAdRequest:false]];
    }
}

- (void) __showInterstitial:(NSObject*)interstitial {
    GADInterstitial* ad = (GADInterstitial*) interstitial;
    if(ad && ad.isReady) {
        [ad presentFromRootViewController:[self getViewController]];
    }
}

- (void) __destroyInterstitial:(NSObject*)interstitial {
    GADInterstitial* ad = (GADInterstitial*) interstitial;
    if(ad) {
        ad.delegate = nil;
    }
}

#pragma mark GADBannerViewDelegate implementation

/**
 * document.addEventListener('onAdLoaded', function(data));
 * document.addEventListener('onAdFailLoad', function(data));
 * document.addEventListener('onAdPresent', function(data));
 * document.addEventListener('onAdDismiss', function(data));
 * document.addEventListener('onAdLeaveApp', function(data));
 */
- (void)adViewDidReceiveAd:(GADBannerView *)adView {
    if((! self.bannerVisible) && self.autoShowBanner) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self __showBanner:self.adPosition atX:self.posX atY:self.posY];
        });
    }
    [self fireAdEvent:EVENT_AD_LOADED withType:ADTYPE_BANNER];
}

- (void)adView:(GADBannerView *)view didFailToReceiveAdWithError:(GADRequestError *)error {
    [self fireAdErrorEvent:EVENT_AD_FAILLOAD withCode:(int)error.code withMsg:[error localizedDescription] withType:ADTYPE_BANNER];
}

- (void)adViewWillLeaveApplication:(GADBannerView *)adView {
    [self fireAdEvent:EVENT_AD_LEAVEAPP withType:ADTYPE_BANNER];
}

- (void)adViewWillPresentScreen:(GADBannerView *)adView {
    [self fireAdEvent:EVENT_AD_PRESENT withType:ADTYPE_BANNER];
}

- (void)adViewDidDismissScreen:(GADBannerView *)adView {
    [self fireAdEvent:EVENT_AD_DISMISS withType:ADTYPE_BANNER];
}

/**
 * document.addEventListener('onAdLoaded', function(data));
 * document.addEventListener('onAdFailLoad', function(data));
 * document.addEventListener('onAdPresent', function(data));
 * document.addEventListener('onAdDismiss', function(data));
 * document.addEventListener('onAdLeaveApp', function(data));
 */
- (void)interstitial:(GADInterstitial *)ad didFailToReceiveAdWithError:(GADRequestError *)error {
    [self fireAdErrorEvent:EVENT_AD_FAILLOAD withCode:(int)error.code withMsg:[error localizedDescription] withType:ADTYPE_INTERSTITIAL];
}

- (void)interstitialDidReceiveAd:(GADInterstitial *)interstitial {
    if (self.interstitial && self.autoShowInterstitial) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self __showInterstitial:self.interstitial];
        });
    }
    [self fireAdEvent:EVENT_AD_LOADED withType:ADTYPE_INTERSTITIAL];
}

- (void)interstitialWillPresentScreen:(GADInterstitial *)interstitial {
    [self fireAdEvent:EVENT_AD_PRESENT withType:ADTYPE_INTERSTITIAL];
}

- (void)interstitialDidDismissScreen:(GADInterstitial *)interstitial {
    [self fireAdEvent:EVENT_AD_DISMISS withType:ADTYPE_INTERSTITIAL];
    
    if(self.interstitial) {
        [self __destroyInterstitial:self.interstitial];
        self.interstitial = nil;
    }
}

- (void)interstitialWillLeaveApplication:(GADInterstitial *)ad {
    [self fireAdEvent:EVENT_AD_LEAVEAPP withType:ADTYPE_INTERSTITIAL];
}

@end
