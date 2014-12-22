
# AdMob Plugin Pro 中文说明 #

用 Javascript 呈现 AdMob 广告赚钱，一行代码搞定 ！

亮点：
- [x] 最简单的API，一行代码搞定广告显示。
- [x] 支持广告条（Banner）和全屏广告（Interstitial）。
- [x] 可以通过AdMob中介支持多个广告网络。
- [x] 多种广告条尺寸，甚至可以自定义尺寸。
- [x] 可以固定广告在屏幕顶端或底部，也可以悬浮模式。
- [x] 最灵活的广告呈现，可以在任意指定位置显示。
- [x] 横屏、竖屏，智能广告条自动适应。
- [x] 支持最新的 iOS SDK。
- [x] 支持最新的 Android Google play services。
- [x] 更新维护及时，技术支持到位。

兼容：
- [x] Apache Cordova
- [x] Intel XDK / Crosswalk
- [x] IBM Worklight
- [x] Google Chrome App
- [x] Adobe PhoneGap Build

支持广告网络：
- [x] AdMob
- [x] DoubleClick
- [x] Facebook AudienceNetwork
- [x] Flurry
- [x] iAd
- [x] InMobi
- [x] Millennial Media
- [x] MobFox

友情提示：（根据最近 2 个月的数据统计）
- [x] 使用 AdMob Plugin Pro，比开源版本有更高的填充率（fill rate）。
- [x] 使用全屏广告，比广告条的收益更高，高达 5-10 倍。 

名词：
- 覆盖率（Fill rate）: 在你的App中广告显示的次数，除以广告请求的次数。
- 收益率（RPM）: 显示 1000 次广告的收益。
- Intel XDK: Intel 的 HTML5 移动应用开发环境和云服务，在云端构建打包，并且支持导入网上的第三方插件。
- Adobe PhoneGap Build: Adobe的HTML5构建打包云服务，接受发布插件，但只允许使用经过审核批准的插件。

新闻：
- 获得推荐：Telerik，发布在 Verified Plugins Marketplace. [更多 ...](http://plugins.telerik.com/plugin/admob)
- 获得推荐：William SerGio，发表在 code project (20 Jun 2014), [更多 ...](http://www.codeproject.com/Articles/788304/AdMob-Plugin-for-Latest-Version-of-PhoneGap-Cordov)
- 获得推荐：Arne，发表在 Scirra Game Dev Forum (07 Aug, 2014), [更多 ...](https://www.scirra.com/forum/plugin-admob-ads-for-crosswalk_t111940)
- 获得推荐：Intel XDK团队 (08/22/2014), [更多 ...](https://software.intel.com/en-us/html5/articles/adding-google-play-services-to-your-cordova-application)
- 获得推荐：Scirra Construct 2 (09/12/2014)官方版本发布, [read more ...](https://www.scirra.com/construct2/releases/r180)

## 如何使用 ##

如果使用 [Cordova 命令行工具](https://cordova.apache.org/docs/en/edge/guide_cli_index.md.html#The%20Command-Line%20Interface)，按照如下步骤创建项目，添加插件：
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

如果使用 Intel XDK，则按照如下步骤操作：
Project -> CORDOVA 3.X HYBRID MOBILE APP SETTINGS -> PLUGINS AND PERMISSIONS -> Third-Party Plugins ->
Add a Third-Party Plugin -> Get Plugin from the Web, input:
```
Name: AdMobPluginPro
Plugin ID: com.google.cordova.admob
[x] Plugin is located in the Apache Cordova Plugins Registry
```

## 快速上手的例子 ##

> 步骤 1: 通过 AdMob 网站，创建相应的广告栏位 ID

```javascript
// 根据平台，自动选用相应的广告 ID
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

> 步骤 2: 一行代码，显示广告条

```javascript
// 显示广告条，默认在顶端的智能广告条
if(AdMob) AdMob.createBanner( admobid.banner );

// 或者, 在底部显示广告条
if(AdMob) AdMob.createBanner( {
	adId:admobid.banner, 
	position:AdMob.AD_POSITION.BOTTOM_CENTER, 
	autoShow:true} );

// 或者，已浮动模式，在底部显示方块广告
if(AdMob) AdMob.createBanner( {
	adId:admobid.banner, 
	adSize:'MEDIUM_RECTANGLE', 
	overlap:true, 
	position:AdMob.AD_POSITION.BOTTOM_CENTER, 
	autoShow:true} );

// 或者，在指定的位置，显示指定大小的广告
if(AdMob) AdMob.createBanner( {
	adId:admobid.banner, 
	adSize:'CUSTOM',  width:200, height:200, 
	overlap:true, 
	position:AdMob.AD_POSITION.POS_XY, x:100, y:200, 
	autoShow:true} );

```

> 步骤 3: 准备全屏广告，并在需要的时候显示

```javascript
// 在后台准备广告资源，例如，在某个游戏关卡开始的时候
if(AdMob) AdMob.prepareInterstitial( {adId:admobid.interstitial, autoShow:false} );

// 显示全屏广告，例如，在某个游戏关卡结束的时候
if(AdMob) AdMob.showInterstitial();
```

## 完整的例子 ##
参见源代码 [test/index.html] (https://github.com/floatinghotpot/cordova-admob-pro/blob/master/test/index.html).

## API 概览 ##

### 方法 ###
```javascript```
AdMob.setOptions(options);

AdMob.createBanner(adId/options, success, fail);
AdMob.removeBanner();
AdMob.showBanner(position);
AdMob.showBannerAtXY(x, y);
AdMob.hideBanner();

AdMob.prepareInterstitial(adId/options, success, fail);
AdMob.showInterstitialAd();
```

### 事件 ###
> **语法**: document.addEventListener(event_name, callback);

```javascript
// 以下事件，适用于广告条和全屏广告
'onAdFailLoad'
'onAdLoaded'
'onAdPresent'
'onAdLeaveApp'
'onAdDismiss'

// 以下事件，适用于广告条，但即将作废，建议用上面的事件
'onBannerFailedToReceive'
'onBannerReceive'
'onBannerPresent'
'onBannerLeaveApp'
'onBannerDismiss'
    
// 以下事件，适用于全屏广告，但即将作废，建议用上面的事件  
'onInterstitialFailedToReceive'
'onInterstitialReceive'
'onInterstitialPresent'
'onInterstitialLeaveApp'
'onInterstitialDismiss'
```

## 方法 ##

### AdMob.setOptions(options) ###

> **用途**: 给其他的方法调用设置默认参数，所有的项目都是可选的，如果没有则用默认值。

**参数**:
- **options**, *json object*, mapping key to value.

参数 **options** 的 key/value:
- **license**, *string*, 设置授权码, 移除 2% 的广告流量分享
- **bannerId**, *string*, 设置广告条的默认广告 ID，例如 'ca-app-pub-xxx/xxx'
- **interstitialId**, *string*, 设置全屏广告的默认广告 ID，例如 'ca-app-pub-xxx/xxx'
- **adSize**, *string*, 设置广告条的大小，默认值:'SMART_BANNER'. 可以是以下的某个: (效果参见截图)
```javascript
'SMART_BANNER', // 推荐，自动适应屏幕大小和高度
'BANNER', 
'MEDIUM_RECTANGLE', 
'FULL_BANNER', 
'LEADERBOARD', 
'SKYSCRAPER', 
'CUSTOM', // 用于自定义大小，需要指定参数 'width' 和 'height'
```
- **width**, *integer*, 广告条的宽度, 需要指定 *adSize*:'CUSTOM'. 默认值: 0
- **height**, *integer*, 广告条的高度，需要指定 *adSize*:'CUSTOM'. 默认值: 0
- **overlap**, *boolean@, 浮动模式，允许广告条覆盖在Web内容的上面，否则的话会把Webview向上或者向下推，避免遮挡. 默认值:false
- **position**, *integer*, 广告条的位置，, 默认值:TOP_CENTER （上面居中）. 可选的值可以是: 
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
AdMob.AD_POSITION.POS_XY 		= 10, // 用于指定位置 X 和 Y, 参见 'x' and 'y'
```
- **x**, *integer*, x坐标. 当 *overlap*:true 和 *position*:AdMob.AD_POSITION.POS_XY 的时候有效. 默认值: 0
- **y**, *integer*, y坐标. 当 *overlap*:true 和 *position*:AdMob.AD_POSITION.POS_XY 的时候有效. 默认值: 0
- **isTesting**, *boolean*, 用于测试，当设置为 true 的时候，可以接收测试广告，发布的时候，请务必设置为 false，否则不计算收益。
- **autoShow**, *boolean*, 当广告准备就绪时自动显示，否则需要调用 showBanner 或者 showInterstitial
- **orientationRenew**, *boolean*, 在屏幕方向发生变化时，强制销毁和重新创建广告条，一般情况不用设置。
- **offsetTopBar**, *boolean*, 偏移广告条和WebView，避免被状态条遮挡 (iOS7+)
- **bgColor**, *string*, 设置父窗口的背景色, 可用值：'black', 'white'等等, 或者RGB格式 '#RRGGBB'
- **adExtras**, *json object*, 为广告显示设置额外的色彩风格.
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

例程:
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
	autoShow: true
};
AdMob.setOptions( defaultOptions );
```

### AdMob.createBanner(adId/options, success, fail) ###

> **用途**: 创建广告条. 这个方法可以传入广告的ID字符串，也可以传入Json对象以包含更多的选项。

**参数**
- **adId**, *string*, 广告条的 ID.
- **options**, *json object*, 可以附带参数选项，参见 **AdMob.setOptions**
- **success**, *function*, 成功之后的回调函数，可以为 null 或者 缺失.
- **fail**, *function*, 失败之后的回调函数，可以为 null 或者 缺失.

参数 **options** 可以有额外的选项：
- **adId**, *string*, 广告条的 ID.
- **success**, *function*, 成功之后的回调函数.
- **error**, *function*, 失败之后的回调函数.

例程:
```javascript
// 仅仅传入广告 ID，其他的用默认值
AdMob.createBanner( admobid.banner );

// 附带更多的参数
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

> **用途**: 在指定的位置显示广告条. 也可以用来移动广告条，而无需销毁和重新创建广告条。

参数:
- **position**, *integer*, 参见 **AdMob.setOptions()**

## AdMob.showBannerAtXY(x, y) ##

> **用途**: 在制定的坐标位置 (x,y) 显示广告条. 

参数:
- **x**, *integer*, 像素. 从屏幕左边计算的偏移量.
- **y**, *integer*, 像素. 从屏幕顶端计算的偏移量.

### AdMob.hideBanner() ###

> **用途**: 隐藏广告条。暂时从屏幕上移除，但没有销毁，稍后还可以继续显示. 

### AdMob.removeBanner() ###

> **用途**: 销毁广告条，不再显示时调用，例如用户已经付费，去掉广告。 

## AdMob.prepareInterstitial(adId/options, success, fail) ##

> **用途**: 准备全屏广告资源，用于后续显示。

参数:
- **adId**, *string*, 全屏广告的广告ID.
- **options**, *string*, 参见 **AdMob.setOptions()*
- **success**, *function*, 成功之后的回调函数，可以为 null 或者 缺失.
- **fail**, *function*, 失败之后的回调函数，可以为 null 或者 缺失.

参数 **options** 可以有额外的选项：
- **adId**, *string*, 广告条的 ID.
- **success**, *function*, 成功之后的回调函数.
- **error**, *function*, 失败之后的回调函数.

> 友情提示: 通常全屏广告需要较多的图片资源比广告条稍多，因此流量也会稍多一点，通常需要一点点时间来准备，这样用户无需等待，体验会更好。

## AdMob.showInterstitial() ##

> **用途**: 当全屏广告准备就绪时，显示给用户看。

## AdMob.isInterstitialReady(callback) ##

> **用途**: 检查全屏广告资源是否已经准备就绪。通常无需调用，最佳方式是响应相关的事件。

例程:
```javascript
// 准备，并且自动显示，大约需要0.5-1秒
AdMob.prepareInterstitial({
	adId: admobid.interstitial,
	autoShow: true
});

// 在游戏关卡开始的时候，准备广告资源
AdMob.prepareInterstitial({
	adId: admobid.interstitial,
	autoShow: false
});
// 在游戏关卡结束的时候，检查并且显示广告
AdMob.isInterstitialReady(function(isready){
	if(isready) AdMob.showInterstitial();
});
```

## 事件 ##

以下所有的事件，都附带参数，包括：
* data.adNetwork, 广告网络的名称, 例如 'AdMob', 'Flurry', 'iAd', etc.
* data.adType, 'banner' 或者 'interstitial'
* data.adEvent, 事件的名称
根据这些参数的值，可以做相关的判断。

'onAdFailLoad'
> 当从广告服务器加载广告资源失败的时候触发. 
```javascript
document.addEventListener('onAdFailLoad',function(data){
	console.log( data.error + ',' + data.reason );
	if(data.adType == 'banner') AdMob.hideBanner();
	else if(data.adType == 'interstitial') interstitialIsReady = false;
});
```

'onAdLoaded'
> 当广告资源从服务器成功加载之后触发.
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
> 当广告成功显示出来的时候触发.

'onAdLeaveApp'
> 当用户点击广告的时候，即将跳转到广告链接指向的网站时触发。也许，你可以计算用户点击了多少次。

'onAdDismiss'
> 当广告被关闭，回到应用或者游戏的时候触发。

## 关于使用 Android Proguard 的提醒 （来自谷歌）##

为了避免把需要的类给处理掉，需要在 <project_directory>/platform/android/proguard-project.txt 文件中加入以下内容:

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
