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

#define TEST_BANNER_ID           @"ca-app-pub-3940256099942544/4480807092"
#define TEST_INTERSTITIALID      @"ca-app-pub-3940256099942544/4411468910"
#define TEST_REWARDVIDEOID       @"ca-app-pub-3940256099942544/3995920692"

#define OPT_ADCOLONY        @"AdColony"
#define OPT_ADCOLONY        @"AdColony"
#define OPT_FLURRY          @"Flurry"
#define OPT_MMEDIA          @"mMedia"
#define OPT_INMOBI          @"InMobi"
#define OPT_FACEBOOK        @"Facebook"
#define OPT_MOBFOX          @"MobFox"
#define OPT_IAD             @"iAd"

#define OPT_GENDER          @"gender"
#define OPT_LOCATION        @"location"
#define OPT_FORCHILD        @"forChild"
#define OPT_CONTENTURL      @"contentURL"
#define OPT_CUSTOMTARGETING @"customTargeting"
#define OPT_EXCLUDE         @"exclude"

@interface CDVAdMobPlugin()<GADBannerViewDelegate, GADInterstitialDelegate, GADRewardBasedVideoAdDelegate>

@property (assign) GADAdSize adSize;
@property (nonatomic, retain) NSDictionary* adExtras;
@property (nonatomic, retain) NSMutableDictionary* mediations;

@property (nonatomic, retain) NSString* mGender;
@property (nonatomic, retain) NSArray* mLocation;
@property (nonatomic, retain) NSString* mForChild;
@property (nonatomic, retain) NSString* mContentURL;

@property (nonatomic, retain) NSDictionary* mCustomTargeting;
@property (nonatomic, retain) NSArray* mExclude;

@property (nonatomic, retain) NSString* rewardVideoAdId;

- (GADAdSize)__AdSizeFromString:(NSString *)str;
- (GADRequest*) __buildAdRequest:(BOOL)forBanner forDFP:(BOOL)fordfp;
- (NSString *) __getAdMobDeviceId;

@end

@implementation CDVAdMobPlugin

- (void)pluginInitialize
{
    [super pluginInitialize];
    
    self.adSize = [self __AdSizeFromString:@"SMART_BANNER"];
    self.mediations = [[NSMutableDictionary alloc] init];
    
    self.mGender = nil;
    self.mLocation = nil;
    self.mForChild = nil;
    self.mContentURL = nil;

    self.mCustomTargeting = nil;
    self.mExclude = nil;

    self.rewardVideoAdId = nil;
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
        self.mLocation = arr;
    }
    NSString* n = [options objectForKey:OPT_FORCHILD];
    if(n != nil) {
        self.mForChild = n;
    }
    str = [options objectForKey:OPT_CONTENTURL];
    if(str != nil){
        self.mContentURL = str;
    }
    str = [options objectForKey:OPT_GENDER];
    if(str != nil){
        self.mGender = str;
    }
    NSDictionary* dict = [options objectForKey:OPT_CUSTOMTARGETING];
    if(dict != nil) {
        self.mCustomTargeting = dict;
    }
    arr = [options objectForKey:OPT_EXCLUDE];
    if(arr != nil) {
        self.mExclude = arr;
    }
}

- (UIView*) __createAdView:(NSString*)adId {
    
    if(GADAdSizeEqualToSize(self.adSize, kGADAdSizeInvalid)) {
        self.adSize = GADAdSizeFromCGSize( CGSizeMake(self.adWidth, self.adHeight) );
    }
    if(GADAdSizeEqualToSize(self.adSize, kGADAdSizeInvalid)) {
        self.adSize = [self __isLandscape] ? kGADAdSizeSmartBannerLandscape : kGADAdSizeSmartBannerPortrait;
    }

    // safety check to avoid crash if adId is empty
    if(adId==nil || [adId length]==0) adId = TEST_BANNER_ID;

    GADBannerView* ad = nil;
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

- (GADRequest*) __buildAdRequest:(BOOL)forBanner forDFP:(BOOL)fordfp
{
    GADRequest *request = nil;

    if(fordfp) {
        DFPRequest * req = [DFPRequest request];
        if(self.mCustomTargeting) {
            req.customTargeting = self.mCustomTargeting;
        }
        if(self.mExclude) {
            req.categoryExclusions = self.mExclude;
        }
        request = req;

    } else {
        request = [GADRequest request];
    }
    if (self.isTesting) {
        NSString* deviceId = [self __getAdMobDeviceId];
        request.testDevices = [NSArray arrayWithObjects:deviceId, kGADSimulatorID, nil];
        NSLog(@"request.testDevices: %@, <Google> tips handled", deviceId);
    }
    if(self.mGender) {
        if( [self.mForChild caseInsensitiveCompare:@"male"] == NSOrderedSame ) request.gender = kGADGenderMale;
        else if( [self.mForChild caseInsensitiveCompare:@"female"] == NSOrderedSame ) request.gender = kGADGenderFemale;
        else  request.gender = kGADGenderMale;
    }
    if(self.mLocation) {
        double lat = [[self.mLocation objectAtIndex:0] doubleValue];
        double lng = [[self.mLocation objectAtIndex:1] doubleValue];
        [request setLocationWithLatitude:lat longitude:lng accuracy:kCLLocationAccuracyBest];
    }
    if(self.mForChild) {
        BOOL forChild = NO;
        if( [self.mForChild caseInsensitiveCompare:@"yes"] == NSOrderedSame ) forChild = YES;
        else if( [self.mForChild intValue] != 0 ) forChild = YES;
        [request tagForChildDirectedTreatment:forChild];
    }
    if(self.mContentURL) {
        request.contentURL = self.mContentURL;
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
        [ad loadRequest:[self __buildAdRequest:true forDFP:true]];

    } else if([view class] == [GADBannerView class]) {
        GADBannerView* ad = (GADBannerView*) view;
        [ad loadRequest:[self __buildAdRequest:true forDFP:false]];
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
    self.interstitialReady = false;
    // safety check to avoid crash if adId is empty
    if(adId==nil || [adId length]==0) adId = TEST_INTERSTITIALID;

    GADInterstitial* ad = nil;
    if(* [adId UTF8String] == '/') {
        ad = [[DFPInterstitial alloc] initWithAdUnitID:adId];
    } else {
        ad = [[GADInterstitial alloc] initWithAdUnitID:adId];
    }
    ad.delegate = self;
    return ad;
}

- (void) __loadInterstitial:(NSObject*)interstitial {
    if([interstitial class] == [DFPInterstitial class]) {
        DFPInterstitial* ad = (DFPInterstitial*) interstitial;
        [ad loadRequest:[self __buildAdRequest:true forDFP:true]];

    } else if([interstitial class] == [GADInterstitial class]) {
        GADInterstitial* ad = (GADInterstitial*) interstitial;
        if(ad) {
            [ad loadRequest:[self __buildAdRequest:false forDFP:false]];
        }
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

- (NSObject*) __prepareRewardVideoAd:(NSString*)adId {
    [GADRewardBasedVideoAd sharedInstance].delegate = self;
    [[GADRewardBasedVideoAd sharedInstance] loadRequest:[GADRequest request]
                                           withAdUnitID:adId];
    return nil;
}

- (BOOL) __showRewardVideoAd:(NSObject*)rewardvideo {
    if ([[GADRewardBasedVideoAd sharedInstance] isReady]) {
        [[GADRewardBasedVideoAd sharedInstance] presentFromRootViewController:[self getViewController]];
        return true;
    }
    return false;
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
    self.interstitialReady = true;
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

#pragma mark GADRewardBasedVideoAdDelegate

/**
 * document.addEventListener('onAdLoaded', function(data));
 * document.addEventListener('onAdFailLoad', function(data));
 * document.addEventListener('onAdPresent', function(data)); // data.rewardType, data.rewardAmount
 * document.addEventListener('onAdDismiss', function(data));
 * document.addEventListener('onAdLeaveApp', function(data));
 */

- (void)rewardBasedVideoAd:(GADRewardBasedVideoAd *)rewardBasedVideoAd
   didRewardUserWithReward:(GADAdReward *)reward {
    NSString* obj = [self __getProductShortName];
    NSString* json = [NSString stringWithFormat:@"{'adNetwork':'%@','adType':'%@','adEvent':'%@','rewardType':'%@','rewardAmount':%lf}",
                      obj, ADTYPE_REWARDVIDEO, EVENT_AD_PRESENT, reward.type, [reward.amount doubleValue]];
    [self fireEvent:obj event:EVENT_AD_PRESENT withData:json];
}

- (void)rewardBasedVideoAdDidReceiveAd:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
    [self fireAdEvent:EVENT_AD_LOADED withType:ADTYPE_REWARDVIDEO];
}

- (void)rewardBasedVideoAdDidOpen:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
    [self fireAdEvent:EVENT_AD_WILLPRESENT withType:ADTYPE_REWARDVIDEO];
}

- (void)rewardBasedVideoAdDidStartPlaying:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
    [self fireAdEvent:EVENT_AD_WILLPRESENT withType:ADTYPE_REWARDVIDEO];
}

- (void)rewardBasedVideoAdDidClose:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
    [self fireAdEvent:EVENT_AD_DISMISS withType:ADTYPE_REWARDVIDEO];
}

- (void)rewardBasedVideoAdWillLeaveApplication:(GADRewardBasedVideoAd *)rewardBasedVideoAd {
    [self fireAdEvent:EVENT_AD_LEAVEAPP withType:ADTYPE_REWARDVIDEO];
}

- (void)rewardBasedVideoAd:(GADRewardBasedVideoAd *)rewardBasedVideoAd
    didFailToLoadWithError:(NSError *)error {
    [self fireAdErrorEvent:EVENT_AD_FAILLOAD withCode:(int)error.code withMsg:[error localizedDescription] withType:ADTYPE_REWARDVIDEO];
}

@end
