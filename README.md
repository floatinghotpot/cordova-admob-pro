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
10. [Important Tips & Warning](#tips)
11. [Video Tutorial](#video-tutorial)
12. [Screenshots](#screenshots)
13. [License](#license)
14. [Credits](#credits)

## Description

This Cordova / PhoneGap plugin enables displaying mobile Ads with single line of javascript code. Designed for the use in HTML5-based cross-platform hybrid games and other applications.

## History

Community-driven project. Designed and maintained by [Raymond Xie](http://github.com/floatinghotpot/) since August 2014. It also features integration in AngularJS projects via [ngCordova] (http://www.ngcordova.com).

It was published to Cordova registry with id "com.google.cordova.admob" since Aug 2014, and  has been downloaded more than 120,000 times. Now it's the No. 1 monetization plugin for Cordova community.

![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/trend.png)

From May 2015, Cordova team announced the deprecation of Cordova registry, and suggest all plugins to be moved to npm repository. Now, the AdMob plugin is published to npm and renamed as "cordova-plugin-admobpro".

## Features

Platforms supported:
- [x] iOS, via SDK v7.37.0 (see [Release Notes](https://developers.google.com/admob/ios/rel-notes))
- [x] Android, via Android SDK (part of Google Play service, see [Release Notes](https://developers.google.com/admob/android/rel-notes))
- [x] Amazon-FireOS, via Android SDK (part of Google Play service)
- [x] Windows Phone, via SDK v6.5.13 (see [Release Notes](https://developers.google.com/admob/wp/rel-notes))

Ad Types:
- [x] Banner
- [x] Interstitial (text, picture, video), highly recommended. :fire:
- [x] Reward Video, highly recommended. :fire:
- [ ] Native Ads (on roadmap)
- [ ] Native Ads Advanced (on roadmap)

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

cordova plugin add cordova-plugin-admobpro --save --variable PLAY_SERVICES_VERSION=16.0.0 --variable ADMOB_APP_ID="__your_admob_app_id___"
```
Or, if you see conflict when using Firebase, use this one instead:
```bash
cordova plugin add cordova-plugin-admobpro-firebase
```

* If use with PhoneGap Build:
```xml
<preference name="android-build-tool" value="gradle" />
<preference name="phonegap-version" value="cli-7.1.0" />
<plugin name="cordova-plugin-admobpro" source="npm">
<variable name="PLAY_SERVICES_VERSION" value="16.0.0" />
</plugin>
```

If use other tools or online build services, see:
* [x] Apache Cordova CLI, v3.0+ ([How To ...](https://github.com/floatinghotpot/cordova-admob-pro/wiki/01.-How-to-Use-with-Cordova-CLI))
* [x] Intel XDK, r1095+ ([How To ...](https://github.com/floatinghotpot/cordova-admob-pro/wiki/02.-How-to-Use-with-Intel-XDK))
* [x] IBM Worklight, v6.2+ ([How To ...](https://github.com/floatinghotpot/cordova-admob-pro/wiki/04.-How-to-Use-with-IBM-Worklight))
* [x] Google Mobile Chrome App ([How To ...](https://github.com/floatinghotpot/cordova-admob-pro/wiki/05.-How-to-Use-with-Mobile-Chrome-App))
* [x] Adobe PhoneGap Build. ([How To ...](https://github.com/floatinghotpot/cordova-admob-pro/wiki/00.-How-To-Use-with-PhoneGap-Build))
* [x] Meteor ([How To ...](https://github.com/floatinghotpot/cordova-admob-pro/wiki/06.-How-To-Use-with-Meteor))
* [x] Ionic V1, [Ionic V1 Demo](https://github.com/jaivehall/admob-ionic-demo)
* [x] Ionic, [Ionic Demo](https://github.com/jaivehall/admob-ionic2-demo)

What's difference of these plugin IDs, which one shall I use ?
* com.google.cordova.admob
* cordova-plugin-admob
* cordova-plugin-admobpro
* cordova-plugin-admobpro-firebase

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
if(AdMob) AdMob.createBanner({
  adId: admobid.banner,
  position: AdMob.AD_POSITION.TOP_CENTER,
  autoShow: true });
```

Step 3: Want interstitial Ad to earn more money ? Easy, 2 lines of code. 

```javascript
// preppare and load ad resource in background, e.g. at begining of game level
if(AdMob) AdMob.prepareInterstitial( {adId:admobid.interstitial, autoShow:false} );

// show the interstitial later, e.g. at end of game level
if(AdMob) AdMob.showInterstitial();
```

Or, you can just copy this [admob_simple.js](https://github.com/floatinghotpot/cordova-admob-pro/blob/master/test/admob_simple.js) to your project, change the ad unit id to your own, and simply reference it in your index.html, like this:
```html
<script type="text/javascript" src="admob_simple.js"></script>
```

Remember to remove `isTesting:true` if release for production.

## AdMob Mediation Adapters ##

Optional mediations to increase your revenue (Read about [AdMob Mediation Networks](https://developers.google.com/mobile-ads-sdk/docs/admob/android/mediation-networks)):
```bash
cordova plugin add cordova-plugin-admob-facebook
cordova plugin add cordova-plugin-admob-flurry
cordova plugin add cordova-plugin-admob-unityads
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
isInterstitialReady(function(ready){ if(ready){ } });

// use reward video
prepareRewardVideoAd(adId/options, success, fail);
showRewardVideoAd();

// set values for configuration and targeting
setOptions(options, success, fail);

// get user ad settings
getAdSettings(function(inf){ inf.adId; inf.adTrackingEnabled; }, fail);
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
* [App demo for Ionic V1](https://github.com/jaivehall/admob-ionic-demo), by Jaive
* [App demo for Ionic](https://github.com/jaivehall/admob-ionic2-demo), by Jaive
* [Game demo using phaser game engine](https://github.com/floatinghotpot/admob-demo-game-phaser/tree/master/demo)
* [Game demo using PIXI game engine](https://github.com/floatinghotpot/admob-demo-game-pixi/tree/master/demo)

## Video Tutorial

* Using Cordova CLI to Add AdMob Plugin:

[![Video](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/youtube_video0.jpg)](http://youtu.be/dBCRW_swoYU)

* Run AdMob Demo App on Android:

[![Video](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/youtube_video.jpg)](http://youtu.be/GsBI97WjFQo)

More video by developers:
* [How to add banner ads to phonegap apps using AdMob Pro for android](https://youtu.be/VzoukTxnbhc), by pointDeveloper
* [How to add Banner ads To Ionic apps with Admob Pro For android](https://youtu.be/qNg8c4J03dE), by pointDeveloper
* [How To Add Banner Ads To Ionic 2 with AdMob Cordova Plugin](https://youtu.be/dfHPlVvIUR0), by pointDeveloper
* [How to add Interstitial Add on navigation for phonegap using JavaScript and AdMob Pro plugin](https://youtu.be/5YvikM3ySXc), by pointDeveloper
* [How to add banner ads to jQuery Mobile Apps using Phonegap AdMob Pro Plugin](https://youtu.be/ceCHJl0c908), by pointDeveloper
* [Intel XDK - Monetizando seu aplicativo com Admob e intel xdk.](https://youtu.be/Bo_deb1vKYk), in Portuguese, by XDK PLUS
* Interesting [Evolution of cordova-admob-pro (Gource Visualization)](https://youtu.be/yH66cHnY06M), by Landon Wilkins

## Screenshots

iPhone Banner | iPhone Interstitial
-------|---------------
![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/iphone.jpg) | ![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/iphone_interstitial.jpg)
Android Banner | Android Interstitial
![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/android.jpg) | ![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/android_interstitial.jpg)

## Tips

Some important tips, FYI.

- [x] Why Google AdMob is recommended ?

Advertisement is main business and income source of Google, so clients are all around the world. Google is one of the most rialable partners for its high standard service.

* High fill rate, nearly 100% all around world.
* High quality Ad, bring high RPM.
* Stable price, auto pay on time. Pay on every 20th next month.

- [x] Strongly recommend Interstitial, more than 10 times profit than banner Ad. 

Ad Format | Banner | Interstitial
---|-------|--------------
Click Rate | < 1% | 3-15%
RPM (revenue per 1000 impression) |  US$ 0.5~4 | US$ 10~50

- [x] Using SMART_BANNER to auto-fit the screen width, avoid using BANNER or FULL_BANNER (unless you are using DFP)

⚠：Remember Google's slogan: "Don't be evil". Invalid usage violating Google rules, may cause your AdMob account suspended ! 

* AdMob publisher may NOT abuse or encourage abuse any Google products, including Google Play, YouTube or Blogger, incuding allow user downloading YouTube video, or embed YouTube video in your own apps.

* Publishers may NOT click their own ads or use any means to inflate impressions and/or clicks artificially, including manual methods. Testing your own ads by clicking on them is not allowed.

More details, please read [AdMob & AdSense policies](https://support.google.com/admob/answer/6128543?hl=en&ref_topic=2745287)

## License

You can use the plugin for free, or you can also pay to get a license. IMPORTANT!!! Before using the plugin, please read the following content and accept the agreement. THIS WILL AVOID POTENTIAL PROBLEM AND DISPUTE.

There are 3 license options, fully up to you:
1. Free and Open Source, no support
2. Commercial, with email/skype support
3. Win-win partnership, with forum support

If you hope to get a fully open source plugin (either need DIY, or hope to get full control on the code), use this open source one instead. Fork and pull request is welcome, but please mention it's derived source. Simply renaming then publishing to npm is forbidden. Open source project URL:
https://github.com/floatinghotpot/cordova-plugin-admob

If use in commercial project, please [get a license](http://rjfun.github.io/), or, you have monetized more than $1000 using this plugin, you are also required to either get a commercial license ($20). As a commercial customer, you will be supported with high priority, via private email or even Skype chat.

If you don't want to get a license as your apps may not earn too much, or you don't have a PayPal account to pay, here is a compromised option. You don't have to pay, we are also okay if just share 2 percent user traffic, so that we can cover our effort and focus on maintenance and online support. (We have maintained this project since Aug 2014, and closed more than 560 support issues)

Please read the [License Agreement](https://github.com/floatinghotpot/cordova-admob-pro/wiki/License-Agreement) for details.

## Credits

This project is created and maintained by Raymond Xie.

More Cordova/PhoneGap plugins by Raymond Xie, [find them in plugin registry](http://plugins.cordova.io/#/search?search=rjfun), or [find them in npm](https://www.npmjs.com/~floatinghotpot).

Project outsourcing and consulting service is also available. Please [contact us](mailto:rjfun.mobile@gmail.com) if you have the business needs.


