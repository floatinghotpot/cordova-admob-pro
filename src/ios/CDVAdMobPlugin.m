//
//  CDVAdMobPlugin.m
//  TestAdMobCombo
//
//  Created by Xie Liming on 14-10-20.
//
//

#import <CoreLocation/CLLocation.h>
#import <AppTrackingTransparency/AppTrackingTransparency.h>
#import <AdSupport/AdSupport.h>
#import <AdSupport/ASIdentifierManager.h>

@import GoogleMobileAds;

#import "CDVAdMobPlugin.h"
#import "AdMobMediation.h"

#define TEST_BANNER_ID           @"ca-app-pub-3940256099942544/2934735716"
#define TEST_INTERSTITIALID      @"ca-app-pub-3940256099942544/4411468910"
#define TEST_REWARDVIDEOID       @"ca-app-pub-3940256099942544/1712485313"

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

@interface CDVAdMobPlugin()<GADBannerViewDelegate, GADFullScreenContentDelegate>

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
- (NSString*) __getTestRewardVideoId {
  return TEST_REWARDVIDEOID;
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
    
    if(GADAdSizeEqualToSize(self.adSize, GADAdSizeInvalid)) {
        self.adSize = GADAdSizeBanner;
    }

    // safety check to avoid crash if adId is empty
    if(adId==nil || [adId length]==0) adId = TEST_BANNER_ID;

    GADBannerView* ad = nil;
    if(* [adId UTF8String] == '/') {
        ad = [[GAMBannerView alloc] initWithAdSize:self.adSize];
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
        GAMRequest * req = [GAMRequest request];
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
        GADMobileAds.sharedInstance.requestConfiguration.testDeviceIdentifiers =[NSArray arrayWithObjects:deviceId, GADSimulatorID, nil];
        NSLog(@"request.testDevices: %@, <Google> tips handled", deviceId);
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
        [GADMobileAds.sharedInstance.requestConfiguration tagForChildDirectedTreatment:forChild];
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
    if ([str isEqualToString:@"SMART_BANNER"]) { // smart banner, deprecated
        return GADAdSizeBanner;
    } else if ([str isEqualToString:@"BANNER"]) {
        return GADAdSizeBanner;
    } else if ([str isEqualToString:@"MEDIUM_RECTANGLE"]) {
        return GADAdSizeMediumRectangle;
    } else if ([str isEqualToString:@"FULL_BANNER"]) {
        return GADAdSizeFullBanner;
    } else if ([str isEqualToString:@"LEADERBOARD"]) {
        return GADAdSizeLeaderboard;
    } else if ([str isEqualToString:@"SKYSCRAPER"]) {
        return GADAdSizeSkyscraper;
    } else if ([str isEqualToString:@"LARGE_BANNER"]) {
        return GADAdSizeLargeBanner;
    } else {
        return GADAdSizeInvalid;
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
    CGRect frame = ad.frame;
    // Here safe area is taken into account, hence the view frame is used after
    // the view has been laid out.
    if (@available(iOS 11.0, *)) {
        frame = UIEdgeInsetsInsetRect(ad.frame, ad.safeAreaInsets);
    }
    // Determine the view width to use for the ad width.
    CGFloat viewWidth = frame.size.width;
    if([self __isLandscape]) {
        if(GADAdSizeEqualToSize(ad.adSize, GADAdSizeBanner)) {
            if(self.isTesting) NSLog(@"change banner to landscape mode");
            ad.adSize = GADCurrentOrientationAnchoredAdaptiveBannerAdSizeWithWidth(viewWidth);
        }
    } else {
        if( ad.adSize.size.width > viewWidth) {
            if(self.isTesting) NSLog(@"change banner to portrait mode");
            ad.adSize = GADCurrentOrientationAnchoredAdaptiveBannerAdSizeWithWidth(viewWidth);
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

    if (@available(iOS 14, *)) {
        [ATTrackingManager requestTrackingAuthorizationWithCompletionHandler:^(ATTrackingManagerAuthorizationStatus status) {
            // Tracking authorization completed. Start loading ads here.
            dispatch_async(dispatch_get_main_queue(), ^{
                if([view class] == [GAMBannerView class]) {
                    GAMBannerView* ad = (GAMBannerView*) view;
                    [ad loadRequest:[self __buildAdRequest:true forDFP:true]];
                } else if([view class] == [GADBannerView class]) {
                    GADBannerView* ad = (GADBannerView*) view;
                    [ad loadRequest:[self __buildAdRequest:true forDFP:false]];
                }
            });
        }];
    } else {
        // Fallback on earlier versions
        if([view class] == [GAMBannerView class]) {
            GAMBannerView* ad = (GAMBannerView*) view;
            [ad loadRequest:[self __buildAdRequest:true forDFP:true]];
        } else if([view class] == [GADBannerView class]) {
            GADBannerView* ad = (GADBannerView*) view;
            [ad loadRequest:[self __buildAdRequest:true forDFP:false]];
        }
    }
}

- (void) __pauseAdView:(UIView*)view {
}

- (void) __resumeAdView:(UIView*)view {
}

- (void) __destroyAdView:(UIView*)view {
    if(! view) return;
    [view removeFromSuperview];
    
    if([view class] == [GAMBannerView class]) {
        GAMBannerView* ad = (GAMBannerView*) view;
        ad.delegate = nil;
    } else if([view class] == [GADBannerView class]) {
        GADBannerView* ad = (GADBannerView*) view;
        ad.delegate = nil;
    }
}
- (NSObject*) __createInterstitial:(NSString*)adId {

    if (@available(iOS 14, *)) {
        [ATTrackingManager requestTrackingAuthorizationWithCompletionHandler:^(ATTrackingManagerAuthorizationStatus status) {
            // Tracking authorization completed. Start loading ads here.
            dispatch_async(dispatch_get_main_queue(), ^{
                [self __createInterstitialInternal:adId];
            });
        }];
    } else {
        return [self __createInterstitialInternal:adId];
    }
    return nil;
}

- (NSObject*) __createInterstitialInternal:(NSString*)adId {
    if (self.interstitial)
        return  nil;

    self.interstitialReady = false;
    // safety check to avoid crash if adId is empty
    if(adId==nil || [adId length]==0) adId = TEST_INTERSTITIALID;
    if(* [adId UTF8String] == '/') {
        GAMRequest *request = [GAMRequest request];
        [GAMInterstitialAd loadWithAdManagerAdUnitID:adId
                                             request:request
                                   completionHandler:^(GAMInterstitialAd * _Nullable ad, NSError * _Nullable error) {
            if (error) {
                NSLog(@"Failed to load interstitial ad with error: %@", [error localizedDescription]);
               [self fireAdEvent:EVENT_AD_LOADED withType:ADTYPE_INTERSTITIAL];
                return;
            }
            self.interstitial = ad;
            self.interstitialReady= YES;
            ad.fullScreenContentDelegate = self;
            [self fireAdEvent:EVENT_AD_LOADED withType:ADTYPE_INTERSTITIAL];
         }];
        if (self.interstitial && self.autoShowInterstitial) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self __showInterstitial:self.interstitial];
            });
        }
    } else {
        GADRequest *request = [GADRequest request];
        [GADInterstitialAd loadWithAdUnitID:adId
                                    request:request
                          completionHandler:^(GADInterstitialAd *ad, NSError *error) {
            if (error) {
                [self fireAdEvent:EVENT_AD_LOADED withType:ADTYPE_INTERSTITIAL];
                return;
            }
            if (self.interstitial)
                return;
            self.interstitial = ad;
            self.interstitialReady= YES;
            ad.fullScreenContentDelegate = self;
                [self fireAdEvent:EVENT_AD_LOADED withType:ADTYPE_INTERSTITIAL];
                if (self.interstitial && self.autoShowInterstitial) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [self __showInterstitial:self.interstitial];
                });
            }
        }];
    }
    return nil;
}

- (void) __loadInterstitial:(NSObject*)interstitial {
}

- (void) __showInterstitial:(NSObject*)interstitial {
    GADInterstitialAd * ad = (GADInterstitialAd *) interstitial;
    if(ad  ) {
        [ad presentFromRootViewController:[self getViewController]];
    }
}

- (void) __destroyInterstitial:(NSObject*)interstitial {
    GADInterstitialAd* ad = (GADInterstitialAd*) interstitial;
    if(ad) {
        ad.fullScreenContentDelegate = nil;
    }
}

- (NSObject*) __prepareRewardVideoAd:(NSString*)adId {
    if (@available(iOS 14, *)) {
        [ATTrackingManager requestTrackingAuthorizationWithCompletionHandler:^(ATTrackingManagerAuthorizationStatus status) {
            // Tracking authorization completed. Start loading ads here.
            dispatch_async(dispatch_get_main_queue(), ^{
                [self __prepareRewardVideoAdInternal:adId];
            });
        }];
    } else {
        return [self __prepareRewardVideoAdInternal:adId];
    }
    return nil;
}

- (NSObject*) __prepareRewardVideoAdInternal:(NSString*)adId {
    GADRequest *request = [GADRequest request];
    [GADRewardedAd loadWithAdUnitID:adId
                            request:request
                  completionHandler:^(GADRewardedAd *ad, NSError *error) {
        if (error) {
            NSLog(@"Rewarded ad fail load");
            /// document.addEventListener('onAdFailLoad', function(data));
            [self fireAdErrorEvent:EVENT_AD_FAILLOAD withCode:(int)error.code withMsg:[error localizedDescription] withType:ADTYPE_REWARDVIDEO];
            return;
        }
        NSLog(@"Rewarded ad loaded");
        self.rewardvideo = ad;
        ad.fullScreenContentDelegate = self;

        // document.addEventListener('onAdLoaded', function(data));
        [self fireAdEvent:EVENT_AD_LOADED withType:ADTYPE_REWARDVIDEO];

        // if want auto show, then show it immediately
        if (self.rewardvideo && self.autoShowRewardVideo) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self __showRewardVideoAd:self.rewardvideo];
            });
         }
     }];
   return nil;
}

- (BOOL) __showRewardVideoAd:(NSObject*)rewardvideo {
    GADRewardedAd* ad = (GADRewardedAd*) rewardvideo;
    if (ad) {
        [ad presentFromRootViewController:[self getViewController] userDidEarnRewardHandler:^{
            GADAdReward *reward = ad.adReward;
            NSString* obj = [self __getProductShortName];
            NSString* json = [NSString stringWithFormat:@"{'adNetwork':'%@','adType':'%@','adEvent':'%@','rewardType':'%@','rewardAmount':%lf}",
                obj, ADTYPE_REWARDVIDEO, EVENT_AD_PRESENT, reward.type, [reward.amount doubleValue]];
            [self fireEvent:obj event:EVENT_AD_PRESENT withData:json];
        }];
        return true;
    }
    return false;
}

#pragma mark GADBannerViewDelegate implementation

/**
 * document.addEventListener('onAdLoaded', function(data));
 * document.addEventListener('onAdFailLoad', function(data));
 * document.addEventListener('onAdWillPresent', function(data));
 * document.addEventListener('onAdPresent', function(data));
 * document.addEventListener('onAdDismiss', function(data));
 * document.addEventListener('onAdLeaveApp', function(data));
 */
- (void)bannerViewDidReceiveAd:(GADBannerView *)adView {
    if((! self.bannerVisible) && self.autoShowBanner) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self __showBanner:self.adPosition atX:self.posX atY:self.posY];
        });
    }
    [self fireAdEvent:EVENT_AD_LOADED withType:ADTYPE_BANNER];
}

- (void)bannerView:(GADBannerView *)view didFailToReceiveAdWithError:(NSError *)error  {
    [self fireAdErrorEvent:EVENT_AD_FAILLOAD withCode:(int)error.code withMsg:[error localizedDescription] withType:ADTYPE_BANNER];
}

- (void)bannerViewWillPresentScreen:(GADBannerView *)adView {
    [self fireAdEvent:EVENT_AD_WILLPRESENT withType:ADTYPE_BANNER];
}

- (void)bannerViewDidRecordImpression:(GADBannerView *)bannerView {
    [self fireAdEvent:EVENT_AD_PRESENT withType:ADTYPE_BANNER];
}

- (void)banerViewWillDismissScreen:(GADBannerView *)adView {
    [self fireAdEvent:EVENT_AD_WILLDISMISS withType:ADTYPE_BANNER];
}

- (void)bannerViewDidDismissScreen:(GADBannerView *)adView {
    [self fireAdEvent:EVENT_AD_DISMISS withType:ADTYPE_BANNER];
}

#pragma mark GADFullScreenContentDelegate implementation

/**
 * document.addEventListener('onAdPresent', function(data));
 * document.addEventListener('onAdDismiss', function(data));
 * document.addEventListener('onAdLeaveApp', function(data));
 */

/// Tells the delegate that the ad failed to present full screen content.
- (void)ad:(nonnull id<GADFullScreenPresentingAd>)ad
    didFailToPresentFullScreenContentWithError:(nonnull NSError *)error {
    if ([ad class] == [GADInterstitialAd class])
        [self fireAdErrorEvent:EVENT_AD_FAILLOAD withCode:(int)error.code withMsg:[error localizedDescription] withType:ADTYPE_INTERSTITIAL];
    if ([ad class] == [GADRewardedAd class])
        [self fireAdErrorEvent:EVENT_AD_FAILLOAD withCode:(int)error.code withMsg:[error localizedDescription] withType:ADTYPE_REWARDVIDEO];
}

/// Tells the delegate that the ad presented full screen content.
- (void)adDidPresentFullScreenContent:(nonnull id<GADFullScreenPresentingAd>)ad {
    if ([ad class] == [GADInterstitialAd class])
        [self fireAdEvent:EVENT_AD_PRESENT withType:ADTYPE_INTERSTITIAL];
    else if ([ad class] == [GADRewardedAd class])
        [self fireAdEvent:EVENT_AD_PRESENT withType:ADTYPE_REWARDVIDEO];
}

/// Tells the delegate that the ad dismissed full screen content.
- (void)adDidDismissFullScreenContent:(nonnull id<GADFullScreenPresentingAd>)ad {
    if ( [ad class] == [GADInterstitialAd class]){
        [self fireAdEvent:EVENT_AD_DISMISS withType:ADTYPE_INTERSTITIAL];
        if (self.interstitial)
        {
            [self __destroyInterstitial:self.interstitial];
            self.interstitial = nil;
        }
    }else if ([ad class] == [GADRewardedAd class]){
        [self fireAdEvent:EVENT_AD_DISMISS withType:ADTYPE_REWARDVIDEO];
        if (self.rewardvideo)
            {
                [self __destroyInterstitial:self.rewardvideo];
                self.rewardvideo = nil;
            }
        }
}

@end
