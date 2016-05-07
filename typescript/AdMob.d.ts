/**
*	TypeScript Definition for the cordova-admob-pro Cordova Plugin
*	https://github.com/floatinghotpot/cordova-admob-pro
*/
declare class AdMob {
	
	/**
	* See google doc: http://developer.android.com/google/play-services/id.html
	* See apple doc: https://developer.apple.com/library/ios/documentation/AdSupport/Reference/ASIdentifierManager_Ref/
	*
	* getAdSettings(function(data){
	*   data.adId;  // UUID string
	*   data.adTrackingEnabled; // boolean
	* }, function(){
	*   // fail to get user ad settings
	* });
	*/
	static getAdSettings(successCallback?:Function, failureCallback?:Function):void;
	
	/**
	* Set ad options
	* Expects an instance of the AdMobOptions class
	*/
	static setOptions(options:Object, successCallback?:Function, failureCallback?:Function):void;
	
	/** Create and display a banner ad */
	static createBanner(args:Object, successCallback?:Function, failureCallback?:Function):void;
	
	/** Remove the currently displayed banner ad */
	static removeBanner(successCallback?:Function, failureCallback?:Function):void;
	
	/** Hide the currently displayed banner ad */
	static hideBanner(successCallback?:Function, failureCallback?:Function):void;
	
	/** Show the currently hidden banner ad */
	static showBanner(successCallback?:Function, failureCallback?:Function):void;
	
	/** Show the current banner ad at a specific (x, y) location */
	static showBannerAtXY(x:Number, y:Number, successCallback?:Function, failureCallback?:Function):void;
	
	/** Prepare an interstitial ad in the background */
	static prepareInterstitial(args:Object, successCallback?:Function, failureCallback?:Function):void;
	
	/** Show an interstitial ad that has been prepared */
	static showInterstitial(successCallback?:Function, failureCallback?:Function):void;
	
	/** Check if an interstitial ad is ready yet or not */
	static isInterstitialReady(successCallback?:Function, failureCallback?:Function):void;
	
	/** Prepare a reward video ad */
	static prepareRewardVideoAd(args:Object, successCallback?:Function, failureCallback?:Function):void;
	
	/** Show a prepared reward video ad */
	static showRewardVideoAd(successCallback?:Function, failureCallback?:Function):void;
}

declare module AdMob {

	/** Constants for the various ad sizes */
	class AD_SIZE {
		static SMART_BANNER:String;
		static BANNER:String;
		static MEDIUM_RECTANGLE:String;
		static FULL_BANNER:String;
		static LEADERBOARD:String;
		static SKYSCRAPER:String;
	}

	/** Constants for the various ad positions */
	class AD_POSITION {
		static NO_CHANGE:Number;
		static TOP_LEFT:Number;
		static TOP_CENTER:Number;
		static TOP_RIGHT:Number;
		static LEFT:Number;
		static CENTER:Number;
		static RIGHT:Number;
		static BOTTOM_LEFT:Number;
		static BOTTOM_CENTER:Number;
		static BOTTOM_RIGHT:Number;
		static POS_XY:Number;
	}

	/**
	*	Class used to pass in all ad options to be used by default for all ads
	*	Unfortunately the extension uses generic objects, so this class can't be used :-(
	*	Keeping here commented out for reference sake (and a nudge to hopefully use it in the extension)
	*/
	/*class AdMobOptions {
		// The ID of the ad to show
		public adId:String;
		// The ID of the specific banner ad to show
		public bannerId:String;
		// The ID of the specific interstitial ad to show
		public interstitialId:String;
		// Banner type size
		public adSize:String;
		// Banner width, if set adSize to 'CUSTOM'
		public width:Number;
		// Banner height, if set adSize to 'CUSTOM'
		public height:Number;
		// Default position
		public position:Number;
		// Default X of banner
		public x:Number
		// Default Y of banner
		public y:Number;
		// If set to true, to receive test ads
		public isTesting:Boolean;
		// If set to true, no need call showBanner or showInterstitial
		public autoShow:Boolean;
		// Extra ad setting options
		public adExtra:any;
		// Whether or not to output verbose logs
		public logVerbose:Boolean;
		// Whether or not ads can overlap
		public overlap:Boolean;
		// Refresh the render of the ad if the orientation changes
		public orientationRenew:Boolean;
	}*/
}