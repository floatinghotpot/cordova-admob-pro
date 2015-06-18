# AdMob Plugin Pro #

### Quick Demo ###

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

### Show Mobile Ad with single line of javascript code ###

Step 1: Create Ad Unit Id for your banner and interstitial, in [AdMob portal](http://www.admob.com/), then write it in your javascript code.

```javascript
// select the right Ad Id according to platform
    var admobid = {};
    if( /(android)/i.test(navigator.userAgent) ) { // for android
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

Step 2: Want a banner? single line of javascript code.

```javascript
// it will display smart banner at top center, using the default options
if(AdMob) AdMob.createBanner( {
	adId: admobid.banner, 
	position: AdMob.AD_POSITION.TOP_CENTER, 
	autoShow: true } );
```

Step 3: Want full screen Ad? Easy, 2 lines of code. 

```javascript
// preppare and load ad resource in background, e.g. at begining of game level
if(AdMob) AdMob.prepareInterstitial( {adId:admobid.interstitial, autoShow:false} );

// show the interstitial later, e.g. at end of game level
if(AdMob) AdMob.showInterstitial();
```

Or, you can just copy this [admob_simple.js](https://github.com/floatinghotpot/cordova-admob-pro/blob/master/test/admob_simple.js) to your project, and ref in your index.html.

### Features ###

Platforms supported:
- [x] Android
- [x] iOS
- [x] Windows Phone

Maximize your revenue with mediation adapters:
* [x] AdMob (built-in)
* [x] DFP (DoubleClick for Publisher, built-in)
* [x] Facebook Audience Network
* [x] Flurry
* [x] iAd
* [x] InMobi
* [x] Millennial Media
* [x] MobFox

## How to use? ##

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

Notice:
* Cordova team announce that the plugin registry is being migrated to npm, and recommended name rule is: cordova-plugin-xxx
* The plugin id in old cordova registry is ```com.google.cordova.admob```, and now in npm is ```cordova-plugin-admobpro```
* Read: [Difference of com.google.cordova.admob and cordova-plugin-admobpro](https://github.com/floatinghotpot/cordova-admob-pro/wiki/Difference-of-Plugin-IDs)
* If build locally using ```cordova-plugin-admobpro```, to avoid build error, you need install some extras in Android SDK manager (type ```android sdk``` to launch it):
![android extra](https://cloud.githubusercontent.com/assets/2339512/8176143/20533ec0-1429-11e5-8e17-a748373d5110.png)

## Quick start with cordova CLI ##
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

## Javascript API Overview ##

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

### Detailed Wiki ###

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
* [FAQ](https://github.com/floatinghotpot/cordova-admob-pro/wiki/FAQ)
* [Notice for Android Proguard](https://github.com/floatinghotpot/cordova-admob-pro/wiki/Notice-for-Android-Proguard)

Demo projects:
* [App demo using Ionic framework](https://github.com/floatinghotpot/admob-demo-app-ionic/tree/master/demo)
* [Game demo using phaser game engine](https://github.com/floatinghotpot/admob-demo-game-phaser/tree/master/demo)
* [Game demo using PIXI game engine](https://github.com/floatinghotpot/admob-demo-game-pixi/tree/master/demo)

## Video Tutorial ##

* Using Cordova CLI to Add AdMob Plugin:

[![Video](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/youtube_video0.jpg)](http://youtu.be/dBCRW_swoYU)

* Run AdMob Demo App on Android:

[![Video](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/youtube_video.jpg)](http://youtu.be/GsBI97WjFQo)

## Screenshots ##

iPhone Banner | iPhone Medium Rect
-------|---------------
![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/iphone.jpg) | ![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/medium_rect.jpg)
iPad Medium Rect | iPad SkyScraper
![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/ipad_rect.jpg) | ![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/ipad_skyscraper.jpg)
iPad interstitial | Any given X,Y:
![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/ipad_interstitial.jpg) | ![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/any_position.jpg)
Android Banner | Android Medium Rect
![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/android.jpg) | ![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/android_rect.jpg)
Android Interstitial | 
![ScreenShot](https://github.com/floatinghotpot/cordova-admob-pro/raw/master/docs/android_interstitial.jpg) |

## Tips ##

Some tips from recent stat data, FYI.

- [x] Using AdMob Plugin Pro, higher and more stable fill rate. 
- [x] Using Interstitial, 5-10 times profit than banner Ad. 
- [x] Using SMART_BANNER, avoid using BANNER or FULL_BANNER.

## See Also ##

Ad PluginPro series for the world leading Mobile Ad services:

* [GoogleAds PluginPro](https://github.com/floatinghotpot/cordova-admob-pro), for Google AdMob/DoubleClick.
* [iAd PluginPro](https://github.com/floatinghotpot/cordova-iad-pro), for Apple iAd. 
* [FacebookAds PluginPro](https://github.com/floatinghotpot/cordova-plugin-facebookads), for Facebook Audience Network.
* [FlurryAds PluginPro](https://github.com/floatinghotpot/cordova-plugin-flurry), for Flurry Ads.
* [mMedia PluginPro](https://github.com/floatinghotpot/cordova-plugin-mmedia), for Millennial Meida.
* [MobFox PluginPro](https://github.com/floatinghotpot/cordova-mobfox-pro), for MobFox.
* [MoPub PluginPro](https://github.com/floatinghotpot/cordova-plugin-mopub), for MoPub.

More Cordova/PhoneGap plugins by Raymond Xie, [find them in plugin registry](http://plugins.cordova.io/#/search?search=rjfun), or [find them in npm](https://www.npmjs.com/~floatinghotpot).

If use in commercial project and need email/skype support, please [buy a license](http://rjfun.github.io/), you will be supported with high priority.

Project outsourcing and consulting service is also available. Please [contact us](mailto:rjfun.mobile@gmail.com) if you have the business needs.

