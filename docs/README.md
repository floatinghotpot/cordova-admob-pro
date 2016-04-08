
# AdMob Plugin Pro #

Present AdMob Ads in Mobile App/Games natively from JavaScript. 

## How to use? ##

Notice: 
* Cordova team announce that the plugin registry is being migrated to npm, and recommended name rule is: cordova-plugin-xxx
* The plugin id in old cordova registry is ```com.google.cordova.admob```, and now in npm is ```cordova-plugin-admobpro```.

* If use with Cordova CLI:
```bash
cordova plugin add cordova-plugin-admobpro
```

* If use with PhoneGap Buid, just configure in config.xml:

```javascript
<gap:plugin name="cordova-plugin-admobpro" source="npm"/>
```

* If use with Intel XDK (not support npm yet):
Project -> CORDOVA 3.X HYBRID MOBILE APP SETTINGS -> PLUGINS AND PERMISSIONS -> Third-Party Plugins ->
Add a Third-Party Plugin -> Get Plugin from the Web, input:
```
Name: AdMobPluginPro
Plugin ID: com.google.cordova.admob
[x] Plugin is located in the Apache Cordova Plugins Registry
```

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

## Quick Start Example Code ##

>Step 1: Prepare your AdMob Ad Unit Id for your banner and interstitial

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

> Step 2: Create a banner with single line of javascript

```javascript
// it will display smart banner at top center, using the default options
if(AdMob) AdMob.createBanner( admobid.banner );

// or, show a banner at bottom
if(AdMob) AdMob.createBanner( {
	adId:admobid.banner, 
	position:AdMob.AD_POSITION.BOTTOM_CENTER, 
	autoShow:true} );

// or, show a rect ad at bottom in overlap mode
if(AdMob) AdMob.createBanner( {
	adId:admobid.banner, 
	adSize:'MEDIUM_RECTANGLE', 
	overlap:true, 
	position:AdMob.AD_POSITION.BOTTOM_CENTER, 
	autoShow:true} );

// or, show any size at any position
if(AdMob) AdMob.createBanner( {
	adId:admobid.banner, 
	adSize:'CUSTOM',  width:200, height:200, 
	overlap:true, 
	position:AdMob.AD_POSITION.POS_XY, x:100, y:200, 
	autoShow:true} );

```

> Step 3: Prepare an interstitial, and show it when needed

```javascript
// preppare and load ad resource in background, e.g. at begining of game level
if(AdMob) AdMob.prepareInterstitial( {adId:admobid.interstitial, autoShow:false} );

// show the interstitial later, e.g. at end of game level
if(AdMob) AdMob.showInterstitial();
```

## Full Example Code ##
Check the [test/index.html] (https://github.com/floatinghotpot/cordova-admob-pro/blob/master/test/index.html).

## API Overview ##

### Methods ###
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

// set default value for other methods
setOptions(options, success, fail);

// get user ad settings
getAdSettings(function(inf){ inf.adId; inf.adTrackingEnabled; }, fail);
```

### Events ###
> **Syntax**: document.addEventListener(event_name, callback);

```javascript
// for both banner and interstitial
'onAdFailLoad'
'onAdLoaded'
'onAdPresent'
'onAdLeaveapp'
'onAdDismiss'
```

## Methods ##

### AdMob.setOptions(options) ###

> **Purpose**: Set default values for other methods. All the option items are optional, will use default value if missing.

**Params**:
- **options**, *json object*, mapping key to value.

key/value for param **options**:
- **license**, *string*, set the license key, to remove the 2% Ad traffic sharing
- **bannerId**, *string*, set the default Ad unit Id for banner, like 'ca-app-pub-xxx/xxx'
- **interstitialId**, *string*, set the defualt Ad unit Id for interstitial, like 'ca-app-pub-xxx/xxx'
- **adSize**, *string*, banner Ad size, Default:'SMART_BANNER'. it can be: (see the screenshots for effects)
```javascript
'SMART_BANNER', // recommended. auto fit the screen width, auto decide the banner height
'BANNER', 
'MEDIUM_RECTANGLE', 
'FULL_BANNER', 
'LEADERBOARD', 
'SKYSCRAPER', 
'CUSTOM', // custom banner size with given width and height, see param 'width' and 'height'
```
- **width**, *integer*, banner width, valid when set *adSize*:'CUSTOM'. Default: 0
- **height**, *integer*, banner height, valid when set *adSize*:'CUSTOM'. Default: 0
- **overlap**, *boolean@, allow banner overlap webview, or else will push webview up or down to avoid overlap. Default:false
- **position**, *integer*, position of banner Ad, Default:TOP_CENTER. Value can be one of: 
```javascript
AdMob.AD_POSITION.NO_CHANGE  	= 0,
AdMob.AD_POSITION.TOP_LEFT 		= 1,
AdMob.AD_POSITION.TOP_CENTER 	= 2,
AdMob.AD_POSITION.TOP_RIGHT 	= 3,
AdMob.AD_POSITION.LEFT 			= 4,
AdMob.AD_POSITION.CENTER 		= 5,
AdMob.AD_POSITION.RIGHT 		= 6,
AdMob.AD_POSITION.BOTTOM_LEFT 	= 7,
AdMob.AD_POSITION.BOTTOM_CENTER	= 8,
AdMob.AD_POSITION.BOTTOM_RIGHT 	= 9,
AdMob.AD_POSITION.POS_XY 		= 10, // use the given X and Y, see params 'x' and 'y'
```
- **x**, *integer*, x in pixels. Valid when *overlap*:true and *position*:AdMob.AD_POSITION.POS_XY. Default: 0
- **y**, *integer*, y in pixels. Valid when *overlap*:true and *position*:AdMob.AD_POSITION.POS_XY. Default: 0
- **isTesting**, *boolean*, set to true, to receiving test ad for testing purpose, see [Google docs](https://developers.google.com/admob/ios/targeting#test_ads), No need to handle it in your javascript code, as the plugin has handled it for you.
- **autoShow**, *boolean*, auto show interstitial ad when loaded, set to false if hope to control the show timing with prepareInterstitial/showInterstitial
- **orientationRenew**, *boolean*, re-create the banner on web view orientation change (not screen orientation), or else just move the banner. Default:true.
- **offsetTopBar**, *boolean*, offset position of banner and webview to avoid overlap by status bar (iOS7+)

- Notice: the bgcolor and other styles can be customized in your admob portal. See: [issue #322](https://github.com/floatinghotpot/cordova-admob-pro/issues/322)


### Targetting Options

The following options can be used to show Ad to target end-user more accurately.

- **location**, *array*, set location for Ad, [latitude, longitude], see [Google docs](https://developers.google.com/admob/ios/targeting#location), you can get the location with navigator API, then pass to ad plugin.
- **gender**, *string*, valid options: "male", "female", "unknown", see [Google docs](https://developers.google.com/admob/ios/targeting#gender)
- **forChild**, *string*, Child-directed setting, value options: "yes", "no", see [Google docs](https://developers.google.com/admob/ios/targeting#child-directed_setting)
- **forFamily**, *string*, Designed for Families setting, valid options: "yes", "no", see [Google docs](https://developers.google.com/mobile-ads-sdk/docs/dfp/android/targeting#designed_for_families_setting)
- **contentUrl**, *string*, set content url, see [Google docs](https://developers.google.com/admob/ios/targeting#content_url)

- **customTargeting**, *json*, DFP only, set custom targeting, see [Google docs](https://developers.google.com/mobile-ads-sdk/docs/dfp/ios/targeting#custom_targeting), example:
```javascript
AdMob.setOptions({
  customTargeting: {
    job: "sailor",
    age: "23",
    interest: ["boats","ports"],
  },
});
```
- **exclude**, *array*, DFP only, set exclude category, example: ["cars", "pets"]

Example Code:
```javascript
var defaultOptions = {
    license: 'username@gmail.com/xxxxxxxxxxxxxxx',
	bannerId: admobid.banner,
	interstitialId: admobid.interstitial,
	adSize: 'SMART_BANNER',
	width: 360, // valid when set adSize 'CUSTOM'
	height: 90, // valid when set adSize 'CUSTOM'
	position: AdMob.AD_POSITION.BOTTOM_CENTER,
	x: 0,		// valid when set position to POS_XY
	y: 0,		// valid when set position to POS_XY
	isTesting: true,
	autoShow: true,
    forChild: "yes"
};
AdMob.setOptions( defaultOptions );
```

### AdMob.createBanner(adId/options, success, fail) ###

> **Purpose**: create a banner Ad.

**Param**
- **adId**, *string*, the Ad unit Id for banner.
- **options**, *json object*, see the keys in **AdMob.setOptions**
- **success**, *function*, callback when success, can be null or missing.
- **fail**, *function*, callback when fail, can be null or missing.

Extra key/value for param **options**
- **adId**, *string*, Ad unit Id for this banner.
- **success**, *function*, callback when success.
- **error**, *function*, call back when fail.

Example Code:
```javascript
AdMob.createBanner( admobid.banner );

AdMob.createBanner({
	adId: admobid.banner,
	position: AdMob.AD_POSITION.BOTTOM_CENTER,
	autoShow: true,
	success: function(){
	},
	error: function(){
		alert('failed to create banner');
	}
});
```
## AdMob.showBanner(position) ##

> **Purpose**: show banner at given position. It can also be used to move banner to given position.  It's not needed to removeBannr and create a new one.

Params:
- **position**, *integer*, see description in **AdMob.setOptions()**

## AdMob.showBannerAtXY(x, y) ##

> **Purpose**: show banner at given position with (x,y). 

Params:
- **x**, *integer*, in pixels. Offset from screen left.
- **y**, *integer*, in pixels. Offset from screen top.

### AdMob.hideBanner() ###

> **Purpose**: hide the banner, remove it from screen, but can show it later. 

### AdMob.removeBanner() ###

> **Purpose**: destroy the banner, remove it from screen. 

You can create another banner if different size, need remove the old one.

## AdMob.prepareInterstitial(adId/options, success, fail) ##

> **Purpose**: prepare an interstitial Ad for showing.

Params:
- **adId**, *string*, Ad unit Id for the full screen Ad.
- **options**, *string*, see **AdMob.setOptions()*
- **success**, *function*, callback when success, can be null or missing.
- **fail**, *function*, callback when fail, can be null or missing.

Extra key/value for param **options**
- **adId**, *string*, Ad unit Id for this interstitial.
- **success**, *function*, callback when success.
- **error**, *function*, call back when fail.

> Note: it will take some time to get Ad resource before it can be showed. You may buffer the Ad by calling **prepareInterstitial**, and show it later.

## AdMob.isInterstitialReady() ##

## AdMob.showInterstitial() ##

> **Purpose**: show interstitial Ad when it's ready.

Example Code:
```javascript
// prepare and aut show
AdMob.prepareInterstitial({
	adId: admobid.interstitial,
	autoShow: true
});

// prepare at beginning of a game level
AdMob.prepareInterstitial({
	adId: admobid.interstitial,
	autoShow: false
});

// check and show it at end of a game level
AdMob.isInterstitialReady(function(ready){
  if(ready) AdMob.showInterstitial();
});
```

## AdMob.prepareRewardVideoAd(adId/options, success, fail) ##

> **Purpose**: prepare an revard video Ad for showing.

Params:
- **adId**, *string*, Ad unit Id for the reward video Ad. You need configure mediation in AdMob portal.
- **options**, *string*, see **AdMob.setOptions()*
- **success**, *function*, callback when success, can be null or missing.
- **fail**, *function*, callback when fail, can be null or missing.

Extra key/value for param **options**
- **adId**, *string*, Ad unit Id for this interstitial.
- **success**, *function*, callback when success.
- **error**, *function*, call back when fail.

> Note: it will take some time to get Ad resource before it can be showed. You may buffer the Ad by calling **prepareRewardVideoAd**, and show it later.

## AdMob.showRewardVideoAd() ##

> **Purpose**: show reward video Ad when it's ready.

To give user reward when he/she watched the video, you need listen to event 'onAdPresent', check the adType 'rewardvideo'.

document.addEventListener('onAdPresent', function(data){
  if(data.adType == 'rewardvideo') {
    console.log( data.rewardType );
    console.log( data.rewardAmount );
  }
});

## AdMob.getAdSettings() ##

Get advertising Id and isTrackingEnabled.

See:
[Google Docs](http://developer.android.com/google/play-services/id.html)
[Apple Docs](https://developer.apple.com/library/ios/documentation/AdSupport/Reference/ASIdentifierManager_Ref/)

## Events ##

All following events will come with a data param, with properties:
* data.adNetwork, the Ad network name, like 'AdMob', 'Flurry', 'iAd', etc.
* data.adType, 'banner' or 'interstitial'
* data.adEvent, the event name

'onAdFailLoad'
> Triggered when failed to receive Ad. 
```javascript
document.addEventListener('onAdFailLoad',function(data){
	console.log( data.error + ',' + data.reason );
	if(data.adType == 'banner') AdMob.hideBanner();
	else if(data.adType == 'interstitial') interstitialIsReady = false;
});
```

'onAdLoaded'
> Triggered when Ad received.
```javascript
document.addEventListener('onAdLoaded',function(data){
	AdMob.showBanner();
});
AdMob.createBanner({
	adId: admobid.banner,
	autoShow: false
});
```

'onAdPresent'
> Triggered when Ad will be showed on screen.

'onAdLeaveApp'
> Triggered when user click the Ad, and will jump out of your App.

'onAdDismiss'
> Triggered when dismiss the Ad and back to your App.

## Notice for Android Proguard ##

To prevent ProGuard from stripping away required classes, add the following lines in the <project_directory>/platform/android/proguard-project.txt file:

```
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep public class com.google.cordova.admob.**

```
