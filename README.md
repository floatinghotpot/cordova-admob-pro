## AdMob Plugin Pro 

Cordova / PhoneGap Plugin for Google Ads, including AdMob / DFP (doubleclick for publisher) and mediations to other Ad networks.

## Contents

1. [Description](#description)
2. [History](#history)
3. [Features](#features)
4. [Demo](#quick-demo)
5. [Quick Start](#quick-start)
6. [Installation](#installation)
7. [Usage](#usage)
8. [API](#api)
9. [Wiki and Docs](#wiki-and-docs)
10. [Important Tips](#tips)
11. [Video Tutorial](#video-tutorial)
12. [Screenshots](#screenshots)
13. [Credits](#credits)

## Description

This Cordova / PhoneGap plugin enables displaying mobile Ads with single line of javascript code. Designed for the use in HTML5-based cross-platform hybrid games and other applications.

## History

Community-driven project. Designed and maintained by [Raymond Xie](http://github.com/floatinghotpot/) since August 2014. It also features integration in AngularJS projects via [ngCordova] (http://www.ngcordova.com).

It was published to Cordova registry with id "com.google.cordova.admob" since Aug 2014, and  has been downloaded more than 120,000 times. Now it's the No. 1 monetization plugin for Cordova community.

![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/trend.png)

From May 2015, Cordova team announced the deprecation of Cordova registry, and suggest all plugins to be moved to npm repository. Now, the AdMob plugin is published to npm and renamed as "cordova-plugin-admobpro".

## Features

Platforms supported:
- [x] Android, via SDK v7.8 (part of Google Play service platform)
- [x] iOS, via SDK v7.6.0
- [x] Windows Phone, via SDK v6.5.13
- [x] Amazon-FireOS, via Android SDK (part of Google Play service platform)

Ad Types:
- [x] Banner
- [x] Interstitial (text, picture, video)
- [x] IAP Ad
- [x] Native Ad (Google new product, on roadmap)

Mediation to other Ad networks:
* [x] AdMob (built-in)
* [x] DFP (DoubleClick for Publisher, built-in)
* [x] Facebook Audience Network
* [x] Flurry
* [x] iAd
* [x] InMobi
* [x] Millennial Media
* [x] MobFox

## Quick Demo

Wanna quickly see the mobile ad on your simulator or device? Try the following commands.

```bash
    # install cordova CLI
    [sudo] npm install cordova -g

    # install a small utility to run all the commands for you
    [sudo] npm install plugin-verify -g

    # Demo 1: run admob demo with sample index.html
    plugin-verify cordova-plugin-admobpro

    # Demo 2/3: run admob demo in game powered by PIXI/phaser HTML5 engine
    plugin-verify admob-demo-game-pixi ios --landscape
    plugin-verify admob-demo-game-phaser ios --landscape

    # Demo 4: run admob demo in app powered by ionic/angular framework
    plugin-verify admob-demo-app-ionic ios --portrait
```

## Quick start
```bash
	# create a demo project
    cordova create test1 com.rjfun.test1 Test1
    cd test1
    cordova platform add android
    cordova platform add ios

    # now add the plugin, cordova CLI will handle dependency automatically
    cordova plugin add cordova-plugin-admobpro

    # now remove the default www content, copy the demo html file to www
    rm -r www/*;
    cp plugins/cordova-plugin-admobpro/test/* www/;

	# now build and run the demo in your device or emulator
    cordova prepare; 
    cordova run android; 
    cordova run ios;
    # or import into Xcode / eclipse
```

## Installation

* If use with Cordova CLI:
```bash
cordova plugin add cordova-plugin-admobpro
```

If use other tools or online build services, see:
* [x] Apache Cordova CLI, v3.0+ ([How To ...](https://github.com/floatinghotpot/cordova-admob-pro/wiki/01.-How-to-Use-with-Cordova-CLI))
* [x] Intel XDK, r1095+ ([How To ...](https://github.com/floatinghotpot/cordova-admob-pro/wiki/02.-How-to-Use-with-Intel-XDK))
* [x] IBM Worklight, v6.2+ ([How To ...](https://github.com/floatinghotpot/cordova-admob-pro/wiki/04.-How-to-Use-with-IBM-Worklight))
* [x] Google Mobile Chrome App ([How To ...](https://github.com/floatinghotpot/cordova-admob-pro/wiki/05.-How-to-Use-with-Mobile-Chrome-App))
* [x] Adobe PhoneGap Build. ([How To ...](https://github.com/floatinghotpot/cordova-admob-pro/wiki/00.-How-To-Use-with-PhoneGap-Build))
* [x] Meteor ([How To ...](https://github.com/floatinghotpot/cordova-admob-pro/wiki/06.-How-To-Use-with-Meteor))
* [x] Ionic/AngularJS ([In ng-cordova ...](https://github.com/driftyco/ng-cordova/blob/master/src/plugins/googleAds.js))

What's difference of the 3 plugin IDs, which one shall I use ?
* com.google.cordova.admob
* cordova-plugin-admobpro
* cordova-plugin-admob

Read: [Difference of Plugin ID](https://github.com/floatinghotpot/cordova-admob-pro/wiki/Difference-of-Plugin-IDs)

Notice:
* If build locally using ```cordova-plugin-admobpro```, to avoid build error, you need install some extras in Android SDK manager (type ```android sdk``` to launch it):
![android extra](https://cloud.githubusercontent.com/assets/2339512/8176143/20533ec0-1429-11e5-8e17-a748373d5110.png)

## Usage

Show Mobile Ad with single line of javascript code.

Step 1: Create Ad Unit Id for your banner and interstitial, in [AdMob portal](http://www.admob.com/), then write it in your javascript code.

```javascript
// select the right Ad Id according to platform
    var admobid = {};
    if( /(android)/i.test(navigator.userAgent) ) { // for android & amazon-fireos
		admobid = {
			banner: 'ca-app-pub-xxx/xxx', // or DFP format "/6253334/dfp_example_ad"
			interstitial: 'ca-app-pub-xxx/yyy'
        };
    } else if(/(ipod|iphone|ipad)/i.test(navigator.userAgent)) { // for ios
		admobid = {
			banner: 'ca-app-pub-xxx/zzz', // or DFP format "/6253334/dfp_example_ad"
			interstitial: 'ca-app-pub-xxx/kkk'
		};
    } else { // for windows phone
		admobid = {
			banner: 'ca-app-pub-xxx/zzz', // or DFP format "/6253334/dfp_example_ad"
			interstitial: 'ca-app-pub-xxx/kkk'
		};
    }
```

Step 2: Want cheap and basic banner? single line of javascript code.

```javascript
// it will display smart banner at top center, using the default options
if(AdMob) AdMob.createBanner( {
	adId: admobid.banner, 
	position: AdMob.AD_POSITION.TOP_CENTER, 
	autoShow: true } );
```

Step 3: Want interstitial Ad to earn more money ? Easy, 2 lines of code. 

```javascript
// preppare and load ad resource in background, e.g. at begining of game level
if(AdMob) AdMob.prepareInterstitial( {adId:admobid.interstitial, autoShow:false} );

// show the interstitial later, e.g. at end of game level
if(AdMob) AdMob.showInterstitial();
```

Or, you can just copy this [admob_simple.js](https://github.com/floatinghotpot/cordova-admob-pro/blob/master/test/admob_simple.js) to your project, and ref in your index.html.

## AdMob Mediation Adapters ##

Optional mediations to increase your revenue (Read about [AdMob Mediation Networks](https://developers.google.com/mobile-ads-sdk/docs/admob/android/mediation-networks)):
```bash
cordova plugin add cordova-plugin-admob-facebook
cordova plugin add cordova-plugin-admob-flurry
cordova plugin add cordova-plugin-admob-iad
cordova plugin add cordova-plugin-admob-inmobi
cordova plugin add cordova-plugin-admob-mmedia
cordova plugin add cordova-plugin-admob-mobfox
```

Notice: If you want to add multiple mediations, please balance flexibility and binary size.

## API

Methods:
```javascript
// use banner
createBanner(adId/options, success, fail);
removeBanner();
showBanner(position);
showBannerAtXY(x, y);
hideBanner();

// use interstitial
prepareInterstitial(adId/options, success, fail);
showInterstitial();

// set default value for other methods
setOptions(options, success, fail);
```

Events:
```javascript
// onAdLoaded
// onAdFailLoad
// onAdPresent
// onAdDismiss
// onAdLeaveApp
document.addEventListener('onAdFailLoad', function(e){
    // handle the event
});
```

## Wiki and Docs

Quick start, simply copy & paste:
* [Example Code](https://github.com/floatinghotpot/cordova-admob-pro/wiki/1.0-Quick-Start-Example-Code)
* [Complete Demo index.html](https://github.com/floatinghotpot/cordova-admob-pro/blob/master/test/index.html)

API Reference:
* [API Overview](https://github.com/floatinghotpot/cordova-admob-pro/wiki/1.1-API-Overview)
* [How to Use Banner](https://github.com/floatinghotpot/cordova-admob-pro/wiki/1.3-Methods-for-Banner)
* [How to Use Interstitial](https://github.com/floatinghotpot/cordova-admob-pro/wiki/1.4-Methods-for-Interstitial)
* [How to Handle Ad Events](https://github.com/floatinghotpot/cordova-admob-pro/wiki/1.5-Events)
* [Chinese/中文文档](https://github.com/floatinghotpot/cordova-admob-pro/wiki/%E4%B8%AD%E6%96%87%E6%96%87%E6%A1%A3)

Other Documentations:
* [ChangeLog](https://github.com/floatinghotpot/cordova-admob-pro/wiki/ChangeLog)
* [Known issues & solutions/workarounds](https://github.com/floatinghotpot/cordova-admob-pro/wiki/Known-Issues-&-Solution---Workaround)
* [FAQ](https://github.com/floatinghotpot/cordova-admob-pro/wiki/FAQ)
* [Notice for Android Proguard](https://github.com/floatinghotpot/cordova-admob-pro/wiki/Notice-for-Android-Proguard)

Demo projects:
* [App demo using Ionic framework](https://github.com/floatinghotpot/admob-demo-app-ionic/tree/master/demo)
* [Game demo using phaser game engine](https://github.com/floatinghotpot/admob-demo-game-phaser/tree/master/demo)
* [Game demo using PIXI game engine](https://github.com/floatinghotpot/admob-demo-game-pixi/tree/master/demo)

## Tips

Some important tips, FYI.

- [x] Strongly recommend Interstitial, more than 10 times profit than banner Ad. 

Ad Format | Banner | Interstitial
---|-------|--------------
Click Rate | < 1% | 3-15%
RPM (revenue per 1000 impression) |  US$ 0.5~4 | US$ 10~100

- [x] Using SMART_BANNER to auto-fit the screen width, avoid using BANNER or FULL_BANNER (unless you are using DFP)

- [x] Why Chooese Google AdMob ?

Advertisement is main business and income source of Google, so the clients are all around the world. Google is one of the most realable partners for its high quality service.

* High fill rate, nearly 100% all around world.
* High quality Ad, bring high RPM.
* Stable price, auto pay on time. Pay on every 20th next month.

## Video Tutorial

* Using Cordova CLI to Add AdMob Plugin:

[![Video](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/youtube_video0.jpg)](http://youtu.be/dBCRW_swoYU)

* Run AdMob Demo App on Android:

[![Video](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/youtube_video.jpg)](http://youtu.be/GsBI97WjFQo)

## Screenshots

iPhone Banner | iPhone Interstitial
-------|---------------
![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/iphone.jpg) | ![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/iphone_interstitial.jpg)
Android Banner | Android Interstitial
![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/android.jpg) | ![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/android_interstitial.jpg)

## Credits

This project is created and maintained by Raymond Xie.

More Cordova/PhoneGap plugins by Raymond Xie, [find them in plugin registry](http://plugins.cordova.io/#/search?search=rjfun), or [find them in npm](https://www.npmjs.com/~floatinghotpot).

If use in commercial project and need email/skype support, please [get a license](http://rjfun.github.io/), you will be supported with high priority.

Project outsourcing and consulting service is also available. Please [contact us](mailto:rjfun.mobile@gmail.com) if you have the business needs.

