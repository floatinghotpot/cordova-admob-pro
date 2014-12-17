//
//  DFPExtras.h
//  Google Mobile Ads SDK
//
//  Copyright 2012 Google Inc. All rights reserved.
//
//  To add DFP extras to an ad request:
//    DFPExtras *extras = [[[DFPExtras alloc] init] autorelease];
//    extras.additionalParameters = @{
//      @"key" : @"value"
//    };
//    GADRequest *request = [GADRequest request];
//    [request registerAdNetworkExtras:extras];
//

#import "GADAdMobExtras.h"

@interface DFPExtras : GADAdMobExtras

/// Publisher provided user ID.
@property(nonatomic, copy) NSString *publisherProvidedID;

@end
