
var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec');

var admobExport = {};

admobExport.AD_SIZE = {
  SMART_BANNER: 'SMART_BANNER',
  BANNER: 'BANNER',
  MEDIUM_RECTANGLE: 'MEDIUM_RECTANGLE',
  FULL_BANNER: 'FULL_BANNER',
  LEADERBOARD: 'LEADERBOARD',
  SKYSCRAPER: 'SKYSCRAPER'
};

admobExport.AD_POSITION = {
  NO_CHANGE: 0,
  TOP_LEFT: 1,
  TOP_CENTER: 2,
  TOP_RIGHT: 3,
  LEFT: 4,
  CENTER: 5,
  RIGHT: 6,
  BOTTOM_LEFT: 7,
  BOTTOM_CENTER: 8,
  BOTTOM_RIGHT: 9,
  POS_XY: 10
};

/*
* see google doc: http://developer.android.com/google/play-services/id.html
* see apple doc: https://developer.apple.com/library/ios/documentation/AdSupport/Reference/ASIdentifierManager_Ref/
*
* getAdSettings(function(data){
*   data.adId;  // UUID string
*   data.adTrackingEnabled; // boolean
* }, function(){
*   // fail to get user ad settings
* });
*/
admobExport.getAdSettings = function(successCallback, failureCallback){
  cordova.exec(successCallback, failureCallback, 'AdMob', 'getAdSettings', []);
};

/*
 * set options:
 *  {
 *    adSize: string, // banner type size
 *    width: integer, // banner width, if set adSize to 'CUSTOM'
 *    height: integer, // banner height, if set adSize to 'CUSTOM'
 *    position: integer, // default position
 *    x: integer, // default X of banner
 *    y: integer, // default Y of banner
 *    isTesting: boolean, // if set to true, to receive test ads
 *    autoShow: boolean, // if set to true, no need call showBanner or showInterstitial
 *    adExtra: {
 *    }
 *   }
 */
admobExport.setOptions = function(options, successCallback, failureCallback) {
  if(typeof options === 'object') {
    cordova.exec( successCallback, failureCallback, 'AdMob', 'setOptions', [options] );
  } else {
    if(typeof failureCallback === 'function') {
      failureCallback('options should be specified.');
    }
  }
};

admobExport.createBanner = function(args, successCallback, failureCallback) {
  var options = {};
  if(typeof args === 'object') {
    for(var k in args) {
      if(k === 'success') { if(typeof args[k] === 'function') successCallback = args[k]; }
      else if(k === 'error') { if(typeof args[k] === 'function') failureCallback = args[k]; }
      else {
        options[k] = args[k];
      }
    }
  } else if(typeof args === 'string') {
    options = { adId: args };
  }
  cordova.exec( successCallback, failureCallback, 'AdMob', 'createBanner', [ options ] );
};

admobExport.removeBanner = function(successCallback, failureCallback) {
  cordova.exec( successCallback, failureCallback, 'AdMob', 'removeBanner', [] );
};

admobExport.hideBanner = function(successCallback, failureCallback) {
  cordova.exec( successCallback, failureCallback, 'AdMob', 'hideBanner', [] );
};

admobExport.showBanner = function(position, successCallback, failureCallback) {
  if(typeof position === 'undefined') position = 0;
  cordova.exec( successCallback, failureCallback, 'AdMob', 'showBanner', [ position ] );
};

admobExport.showBannerAtXY = function(x, y, successCallback, failureCallback) {
  if(typeof x === 'undefined') x = 0;
  if(typeof y === 'undefined') y = 0;
  cordova.exec( successCallback, failureCallback, 'AdMob', 'showBannerAtXY', [{x:x, y:y}] );
};

admobExport.prepareInterstitial = function(args, successCallback, failureCallback) {
  var options = {};
  if(typeof args === 'object') {
    for(var k in args) {
      if(k === 'success') { if(typeof args[k] === 'function') successCallback = args[k]; }
      else if(k === 'error') { if(typeof args[k] === 'function') failureCallback = args[k]; }
      else {
        options[k] = args[k];
      }
    }
  } else if(typeof args === 'string') {
    options = { adId: args };
  }
  cordova.exec( successCallback, failureCallback, 'AdMob', 'prepareInterstitial', [ args ] );
};

admobExport.showInterstitial = function(successCallback, failureCallback) {
  cordova.exec( successCallback, failureCallback, 'AdMob', 'showInterstitial', [] );
};

admobExport.isInterstitialReady = function(successCallback, failureCallback) {
  cordova.exec( successCallback, failureCallback, 'AdMob', 'isInterstitialReady', [] );
};

admobExport.prepareRewardVideoAd = function(args, successCallback, failureCallback) {
  var options = {};
  if(typeof args === 'object') {
    for(var k in args) {
      if(k === 'success') { if(typeof args[k] === 'function') successCallback = args[k]; }
      else if(k === 'error') { if(typeof args[k] === 'function') failureCallback = args[k]; }
      else {
        options[k] = args[k];
      }
    }
  } else if(typeof args === 'string') {
    options = { adId: args };
  }
  cordova.exec( successCallback, failureCallback, 'AdMob', 'prepareRewardVideoAd', [ args ] );
};

admobExport.showRewardVideoAd = function(successCallback, failureCallback) {
  cordova.exec( successCallback, failureCallback, 'AdMob', 'showRewardVideoAd', [] );
};

module.exports = admobExport;
