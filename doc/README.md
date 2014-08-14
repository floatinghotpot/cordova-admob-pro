
# AdMob Plugin Pro #

Present AdMob Ads in Mobile App/Games natively from JavaScript. 

Highlights:
- [x] Support Banner Ad and Interstitial Ad.
- [x] Most flexible, put banner at any position.
- [x] Multiple banner size, also support custom size.
- [x] Latest SDK, iOS SDK v6.10.0, Android Google play services v19
- [x] Easy-to-use APIs. Can display Ad with single line of Js code.
- [x] Actively maintained, prompt support.

## How to Use ##

Add the plugin to your cordova project with [Cordova CLI](https://cordova.apache.org/docs/en/edge/guide_cli_index.md.html#The%20Command-Line%20Interface):
```c
cordova create <project_folder> com.<company_name>.<app_name> <AppName>
cd <project_folder>
cordova platform add android
cordova platform add ios

cordova plugin add com.google.cordova.admob

// copy the demo html file to www
rm -r www/*; cp plugins/com.google.cordova.admob/test/index.html www/

// connect device or run in emulator
cordova prepare; cordova run android; cordova run ios;

// or import into Xcode / eclipse
```

## Quick Start Example Code ##

>Step 1: Prepare your AdMob Ad Unit Id for your banner and interstitial

```javascript
var ad_units = {
	ios : {
		banner: 'ca-app-pub-6869992474017983/4806197152',
		interstitial: 'ca-app-pub-6869992474017983/7563979554'
	},
	android : {
		banner: 'ca-app-pub-6869992474017983/9375997553',
		interstitial: 'ca-app-pub-6869992474017983/1657046752'
	}
};
// select the right Ad Id according to platform
var admobid = ( /(android)/i.test(navigator.userAgent) ) ? ad_units.android : ad_units.ios;
```

> Step 2: Create a banner with single line of javascript

```javascript
// it will display smart banner at top center, using the default options
if(AdMob) AdMob.createBanner( admobid.banner );
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
```javascript```
AdMob.setOptions(options);

AdMob.createBanner(adId/options, success, fail);
AdMob.removeBanner();
AdMob.showBanner(position);
AdMob.showBannerAtXY(x, y);
AdMob.hideBanner();

AdMob.prepareInterstitial(adId/options, success, fail);
AdMob.showInterstitialAd();
AdMob.isInterstitialReady(callback);
```

### Events ###
> **Syntax**: document.addEventListener(event_name, callback);

```javascript
// for banner Ad
'onBannerFailedToReceive'
'onBannerReceive'
'onBannerPresent'
'onBannerLeaveApp'
'onBannerDismiss'
    
// for interstitial Ad    
'onInterstitialFailedToReceive'
'onInterstitialReceive'
'onInterstitialPresent'
'onInterstitialLeaveApp'
'onInterstitialDismiss'
```

## Methods ##

### AdMob.setOptions(options) ###

> **Purpose**: Set default values for other methods. All the option items are optional, will use default value if missing.

**Params**:
- **options**, *json object*, mapping key to value.

key/value for param **options**:
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
- **width**, *integer*, banner width if set adSize:'CUSTOM'. Default: 0
- **height**, *integer*, banner height if set adSize:'CUSTOM'. Default: 0
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
AdMob.AD_POSITION.BOTTOM_RIGHT 	= 8,
AdMob.AD_POSITION.BOTTOM_RIGHT 	= 9,
AdMob.AD_POSITION.POS_XY 		= 10, // use the given X and Y, see params 'x' and 'y'
```
- **x**, *integer*, x in pixels. Valid when position:0 or AdMob.AD_POSITION.POS_XY. Default: 0
- **y**, *integer*, y in pixels. Valid when position:0 or AdMob.AD_POSITION.POS_XY. Default: 0
- **isTesting**, *boolean*, set to true, to receiving test ad for testing purpose
- **autoShow**, *boolean*, auto show interstitial ad when loaded, set to false if hope to control the show timing with prepareInterstitial/showInterstitial
- **orientationRenew**, *boolean*, re-create the banner on web view orientation change (not screen orientation), or else just move the banner. Default:true.
- **adExtras**, *json object*, set extra color style for Ad.
```javascript
{
	color_bg: 'AAAAFF',
	color_bg_top: 'FFFFFF',
	color_border: 'FFFFFF',
	color_link: '000080',
	color_text: '808080',
	color_url: '008000'
}
```

Example Code:
```javascript
var defaultOptions = {
	bannerId: admobid.banner,
	interstitialAdId: admobid.interstitial,
	adSize: 'SMART_BANNER',
	width: 360, // valid when set adSize 'CUSTOM'
	height: 90, // valid when set adSize 'CUSTOM'
	position: AdMob.AD_POSITION.BOTTOM_CENTER,
	x: 0,		// valid when set position to POS_XY
	y: 0,		// valid when set position to POS_XY
	isTesting: true,
	autoShow: true
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

> Note: it will take some time to get Ad resource before it can be showed. You may buffer the Ad by calling **requestInterstitial**, and show it later.

## AdMob.showInterstitial() ##

> **Purpose**: show interstitial Ad when it's ready.

## AdMob.isInterstitialReady(callback) ##

> **Purpose**: check to confirm interstitial Ad is ready to show.

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
AdMob.isInterstitialReady(function(isready){
	if(isready) AdMob.showInterstitial();
});
```

## Events ##

### Banner Events ###

'onBannerFailedToReceive'
> Triggered when failed to receive Ad. 
```javascript
document.addEventListener('onBannerFailedToReceive',function(data){
	console.log( data.error + ',' + data.reason );
	AdMob.hideBanner();
});
```

'onBannerReceive'
> Triggered when Ad received.
```javascript
document.addEventListener('onBannerReceive',function(data){
	AdMob.showBanner();
});
AdMob.createBanner({
	adId: admobid.banner,
	autoShow: false
});
```

'onBannerPresent'
> Triggered when Ad will be showed on screen.

'onBannerLeaveApp'
> Triggered when user click the Ad, and will jump out of your App.

'onBannerDismiss'
> Triggered when dismiss the Ad and back to your App.

### Interstitial Events ###

- 'onInterstitialFailedToReceive'
- 'onInterstitialReceive'
- 'onInterstitialPresent'
- 'onInterstitialLeaveApp'
- 'onInterstitialDismiss'

They are quite similiar to the events of banner Ad.

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
```

## Credit ##

This plugin is created and maintained by [Raymond Xie](http://floatinghotpot.github.io/).

You can use this cordova plugin for free. To support this project, donation is welcome. 

Donation can be accepted in either of following ways:
* Share 2% Ad traffic. 
* [Donate directly via Paypal](http://floatinghotpot.github.io/#donate)


