package com.rjfun.cordova.admob;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.ads.mediation.admob.AdMobAdapter;

import java.lang.reflect.Method;
import java.lang.NoSuchMethodException;
import java.util.ArrayList;

import com.rjfun.cordova.ad.GenericAdPlugin;

public class AdMobPlugin extends GenericAdPlugin {
  private static final String LOGTAG = "AdMobPlugin";

  // options
  private static final String OPT_ADCOLONY = "AdColony";
  private static final String OPT_FLURRY = "Flurry";
  private static final String OPT_MMEDIA = "mMedia";
  private static final String OPT_INMOBI = "InMobi";
  private static final String OPT_FACEBOOK = "Facebook";
  private static final String OPT_MOBFOX = "MobFox";

  private static final String TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111";
  private static final String TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712";
  private static final String TEST_REWARDVIDEO_ID = "ca-app-pub-3940256099942544/1042454297";

  private AdSize adSize = AdSize.SMART_BANNER;

  public static final String OPT_AD_EXTRAS = "adExtras";
  private JSONObject adExtras = null;

  public static final String OPT_LOCATION = "location";
  private Location mLocation = null;

  public static final String OPT_GENDER = "gender";
  public static final String OPT_FORCHILD = "forChild";
  public static final String OPT_FORFAMILY = "forFamily";
  public static final String OPT_CONTENTURL = "contentUrl";
  public static final String OPT_CUSTOMTARGETING = "customTargeting";
  public static final String OPT_EXCLUDE = "exclude";

  protected String mGender = null;
  protected String mForChild = null;
  protected String mForFamily = null;
  protected String mContentURL = null;
  protected JSONObject mCustomTargeting = null;
  protected JSONArray mExclude = null;

  private boolean mIsRewardedVideoLoading = false;
  private final Object mLock = new Object();

  private HashMap<String, AdMobMediation> mediations = new HashMap<String, AdMobMediation>();

  @Override
  protected void pluginInitialize() {
    super.pluginInitialize();
    
    // TODO: any init code
  }

  @Override
  protected String __getProductShortName() {
    return "AdMob";
  }

  @Override
  protected String __getTestBannerId() {
    return TEST_BANNER_ID;
  }

  @Override
  protected String __getTestInterstitialId() {
    return TEST_INTERSTITIAL_ID;
  }

  @Override
  protected String __getTestRewardVideoId() { return TEST_REWARDVIDEO_ID; }

  @Override
  public void setOptions(JSONObject options) {
    super.setOptions(options);

    if(options.has(OPT_AD_SIZE)) adSize = adSizeFromString(options.optString(OPT_AD_SIZE));
    if(adSize == null) {
      adSize = new AdSize(adWidth, adHeight);
    }

    if(options.has(OPT_AD_EXTRAS)) adExtras = options.optJSONObject(OPT_AD_EXTRAS);

    if(options.has(OPT_LOCATION)) {
      JSONArray location = options.optJSONArray(OPT_LOCATION);
      if(location != null) {
        mLocation = new Location("dummyprovider");
        mLocation.setLatitude( location.optDouble(0, 0.0) );
        mLocation.setLongitude( location.optDouble(1, 0) );
      }
    }

    if(options.has(OPT_GENDER)) {
      mGender = options.optString(OPT_GENDER);
    }
    if(options.has(OPT_FORCHILD)) {
      mForChild = options.optString(OPT_FORCHILD);
    }
    if(options.has(OPT_FORFAMILY)) {
      mForFamily = options.optString(OPT_FORFAMILY);
    }
    if(options.has(OPT_CONTENTURL)) {
      mContentURL = options.optString(OPT_CONTENTURL);
    }
    if(options.has(OPT_CUSTOMTARGETING)) {
      mCustomTargeting = options.optJSONObject(OPT_CUSTOMTARGETING);
    }
    if(options.has(OPT_EXCLUDE)) {
      mExclude = options.optJSONArray(OPT_EXCLUDE);
    }
  }

  @Override
  protected View __createAdView(String adId) {
    // safety check to avoid exception when adId is null or empty
    if(adId==null || adId.length()==0) adId = TEST_BANNER_ID;

    // Tip: The format for the DFP ad unit ID is: /networkCode/adUnitName
    // example: "/6253334/dfp_example_ad"
    if(adId.charAt(0) == '/') {
      PublisherAdView ad = new PublisherAdView(getActivity());
      ad.setAdUnitId(adId);
      ad.setAdSizes(adSize);
      ad.setAdListener(new BannerListener());
      return ad;
    } else {
      AdView ad = new AdView(getActivity());
      ad.setAdUnitId(adId);
      ad.setAdSize(adSize);
      ad.setAdListener(new BannerListener());
      return ad;
    }
  }

  @Override
  protected void __loadAdView(View view) {
    if(view instanceof PublisherAdView) {
      PublisherAdView ad = (PublisherAdView) view;
      ad.loadAd(buildPublisherAdRequest());
    } else {
      AdView ad = (AdView) view;
      ad.loadAd(buildAdRequest());
    }
  }

  protected AdSize getAdViewSize(View view) {
    if(view instanceof PublisherAdView) {
      PublisherAdView dfpView = (PublisherAdView) view;
      return dfpView.getAdSize();
    } else {
      AdView admobView = (AdView) view;
      return admobView.getAdSize();
    }
  }

  @Override
  protected int __getAdViewWidth(View view) {
    AdSize sz = getAdViewSize(view);
    return sz.getWidthInPixels(getActivity());
  }

  @Override
  protected int __getAdViewHeight(View view) {
    AdSize sz = getAdViewSize(view);
    return sz.getHeightInPixels(getActivity());
  }

  @Override
  protected void __pauseAdView(View view) {
    if(view == null) return;
    
    if(view instanceof PublisherAdView) {
      PublisherAdView dfpView = (PublisherAdView)view;
      dfpView.pause();
    } else {
      AdView admobView = (AdView)view;
      admobView.pause();
    }
  }

  @Override
  protected void __resumeAdView(View view) {
    if(view == null) return;

    if(view instanceof PublisherAdView) {
      PublisherAdView dfpView = (PublisherAdView)view;
      dfpView.resume();
    } else {
      AdView admobView = (AdView)view;
      admobView.resume();
    }
  }

  @Override
  protected void __destroyAdView(View view) {
    if(view == null) return;

    if(view instanceof PublisherAdView) {
      PublisherAdView dfpView = (PublisherAdView)view;
      dfpView.setAdListener(null);
      dfpView.destroy();
    } else {
      AdView admobView = (AdView)view;
      admobView.setAdListener(null);
      admobView.destroy();
    }
  }

  @Override
  protected Object __createInterstitial(String adId) {
    interstitialReady = false;
    // safety check to avoid exceptoin in case adId is null or empty
    if(adId==null || adId.length()==0) adId = TEST_INTERSTITIAL_ID;

    if(adId.charAt(0) == '/') {
      PublisherInterstitialAd ad = new PublisherInterstitialAd(getActivity());
      ad.setAdUnitId(adId);
      ad.setAdListener(new InterstitialListener());
      return ad;
    } else {
      InterstitialAd ad = new InterstitialAd(getActivity());
      ad.setAdUnitId(adId);
      ad.setAdListener(new InterstitialListener());
      return ad;
    }
  }

  @Override
  protected void __loadInterstitial(Object interstitial) {
    if(interstitial == null) return;

    if(interstitial instanceof PublisherInterstitialAd) {
     PublisherInterstitialAd ad = (PublisherInterstitialAd) interstitial;
      ad.loadAd(buildPublisherAdRequest());
    } else if(interstitial instanceof InterstitialAd) {
      InterstitialAd ad = (InterstitialAd) interstitial;
      ad.loadAd( buildAdRequest() );
    }
  }

  @Override
protected void __showInterstitial(Object interstitial) {
    if(interstitial == null) return;

    if(interstitial instanceof PublisherInterstitialAd) {
      PublisherInterstitialAd ad = (PublisherInterstitialAd) interstitial;
       if(ad.isLoaded()) {
           ad.show();
        }
  
    }else if(interstitial instanceof InterstitialAd){
        InterstitialAd ad = (InterstitialAd) interstitial;
         if(ad.isLoaded()) {
           ad.show();
        }
    }
   
  } 

  @Override
  protected void __destroyInterstitial(Object interstitial) {
    if(interstitial == null) return;

    if(interstitial instanceof InterstitialAd) {
      InterstitialAd ad = (InterstitialAd) interstitial;
      ad.setAdListener(null);
    }
  }

  @Override
  protected Object __prepareRewardVideoAd(String adId) {
    // safety check to avoid exceptoin in case adId is null or empty
    if(adId==null || adId.length()==0) adId = TEST_REWARDVIDEO_ID;

    RewardedVideoAd ad = MobileAds.getRewardedVideoAdInstance(getActivity());
    ad.setRewardedVideoAdListener(new RewardVideoListener());

    synchronized (mLock) {
      if (!mIsRewardedVideoLoading) {
        mIsRewardedVideoLoading = true;
        Bundle extras = new Bundle();
        extras.putBoolean("_noRefresh", true);
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        ad.loadAd(adId, adRequest);
      }
    }

    return ad;
  }

  @Override
  protected void __showRewardVideoAd(Object rewardvideo) {
    if(rewardvideo == null) return;

    if(rewardvideo instanceof RewardedVideoAd) {
      RewardedVideoAd ad = (RewardedVideoAd) rewardvideo;
      if(ad.isLoaded()){
        ad.show();
      }
    }
  }

  @SuppressLint("DefaultLocale")
  private AdRequest buildAdRequest() {
    final Activity activity = getActivity();
    AdRequest.Builder builder = new AdRequest.Builder();
    
    if (isTesting) {
      // This will request test ads on the emulator and deviceby passing this hashed device ID.
      String ANDROID_ID = Settings.Secure.getString(activity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
      String deviceId = md5(ANDROID_ID).toUpperCase();
      builder = builder.addTestDevice(deviceId).addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
    }

    if(adExtras != null) {
      Bundle bundle = new Bundle();
      bundle.putInt("cordova", 1);
      Iterator<String> it = adExtras.keys();
      while (it.hasNext()) {
        String key = it.next();
        try {
          bundle.putString(key, adExtras.get(key).toString());
        } catch (JSONException exception) {
          Log.w(LOGTAG, String.format("Caught JSON Exception: %s", exception.getMessage()));
        }
      }
      builder = builder.addNetworkExtras( new AdMobExtras(bundle) );
    }
    
    Iterator<String> it = mediations.keySet().iterator();
    while(it.hasNext()) {
      String key = it.next();
      AdMobMediation m = mediations.get(key);
      if(m != null) {
        builder = m.joinAdRequest(builder);
      }
    }

    if(mGender != null) {
      if("male".compareToIgnoreCase(mGender) != 0) builder.setGender(AdRequest.GENDER_MALE);
      else if("female".compareToIgnoreCase(mGender) != 0) builder.setGender(AdRequest.GENDER_FEMALE);
      else builder.setGender(AdRequest.GENDER_UNKNOWN);
    }
    if(mLocation != null) builder.setLocation(mLocation);
    if(mForFamily != null) {
      Bundle extras = new Bundle();
      extras.putBoolean("is_designed_for_families", true);
      builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
    }
    if(mForChild != null) {
      builder.tagForChildDirectedTreatment(true);
    }
    if(mContentURL != null) {
      builder.setContentUrl(mContentURL);
    }

    return builder.build();
  }

  @SuppressLint("DefaultLocale")
  private PublisherAdRequest buildPublisherAdRequest() {
    final Activity activity = getActivity();
    PublisherAdRequest.Builder builder = new PublisherAdRequest.Builder();

    if (isTesting) {
      // This will request test ads on the emulator and deviceby passing this hashed device ID.
      String ANDROID_ID = Settings.Secure.getString(activity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
      String deviceId = md5(ANDROID_ID).toUpperCase();
      builder = builder.addTestDevice(deviceId).addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
    }

    if(adExtras != null) {
      Bundle bundle = new Bundle();
      bundle.putInt("cordova", 1);
      Iterator<String> it = adExtras.keys();
      while (it.hasNext()) {
        String key = it.next();
        try {
          bundle.putString(key, adExtras.get(key).toString());
        } catch (JSONException exception) {
          Log.w(LOGTAG, String.format("Caught JSON Exception: %s", exception.getMessage()));
        }
      }
      builder = builder.addNetworkExtras(new AdMobExtras(bundle));
    }

    if(mGender != null) {
      if("male".compareToIgnoreCase(mGender) != 0) builder.setGender(AdRequest.GENDER_MALE);
      else if("female".compareToIgnoreCase(mGender) != 0) builder.setGender(AdRequest.GENDER_FEMALE);
      else builder.setGender(AdRequest.GENDER_UNKNOWN);
    }
    if(mLocation != null) builder.setLocation(mLocation);
    if(mForFamily != null) {
      Bundle extras = new Bundle();
      extras.putBoolean("is_designed_for_families", ("yes".compareToIgnoreCase(mForChild) == 0));
      builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
    }
    if(mForChild != null) {
      builder.tagForChildDirectedTreatment("yes".compareToIgnoreCase(mForChild) == 0);
    }
    if(mContentURL != null) {
      builder.setContentUrl(mContentURL);
    }

    // DFP extra targeting options
    if(mCustomTargeting != null) {
      Iterator<String> iter = mCustomTargeting.keys();
      while(iter.hasNext()){
        String key = iter.next();
        String str = mCustomTargeting.optString(key);
        if(str!=null && str.length()>0) {
          builder.addCustomTargeting(key, str);
        } else {
          JSONArray strs = mCustomTargeting.optJSONArray(key);
          if(strs!=null && strs.length()>0) {
            ArrayList<String> strlist = new ArrayList<String>();
            for(int i=0; i<strs.length(); i++)
              strlist.add(strs.optString(i));
            builder.addCustomTargeting(key, strlist);
          }
        }
      }
    }
    if(mExclude != null) {
      int n = mExclude.length();
      if(n > 0) {
        try { // new method after SDK v7.0
          Method method = null;
          method = builder.getClass().getMethod("addCategoryExclusion", String.class);
          if (method != null) {
            try {
              for (int i = 0; i < n; i++) {
                method.invoke(builder, mExclude.optString(i, ""));
              }
            } catch (Exception e) {
            }
          }
        } catch (NoSuchMethodException e) {
          // old method before SDK v7.0
          Bundle bundle = new Bundle();
          String str = "";
          for(int i=0; i<n; i++) {
            if(i > 0) str += ",";
            str += mExclude.optString(i, "");
          }
          bundle.putString("excl_cat", str);
          builder.addNetworkExtras(new AdMobExtras(bundle));
        }
      }
    }

    return builder.build();
  }

  @Override
  public void onPause(boolean multitasking) {
    Iterator<String> it = mediations.keySet().iterator();
    while(it.hasNext()) {
      String key = it.next();
      AdMobMediation m = mediations.get(key);
      if(m != null) m.onPause();
    }

    super.onPause(multitasking);
  }

  @Override
  public void onResume(boolean multitasking) {
    super.onResume(multitasking);
    Iterator<String> it = mediations.keySet().iterator();
    while(it.hasNext()) {
      String key = it.next();
      AdMobMediation m = mediations.get(key);
      if(m != null) m.onResume();
    }
  }

  @Override
  public void onDestroy() {
    Iterator<String> it = mediations.keySet().iterator();
    while(it.hasNext()) {
      String key = it.next();
      AdMobMediation m = mediations.get(key);
      if(m != null) m.onDestroy();
    }
    super.onDestroy();
  }

  /**
   * Gets an AdSize object from the string size passed in from JavaScript.
   * Returns null if an improper string is provided.
   *
   * @param size The string size representing an ad format constant.
   * @return An AdSize object used to create a banner.
   */
  public static AdSize adSizeFromString(String size) {
    if ("BANNER".equals(size)) {
      return AdSize.BANNER;
    } else if ("SMART_BANNER".equals(size)) {
      return AdSize.SMART_BANNER;
    } else if ("MEDIUM_RECTANGLE".equals(size)) {
      return AdSize.MEDIUM_RECTANGLE;
    } else if ("FULL_BANNER".equals(size)) {
      return AdSize.FULL_BANNER;
    } else if ("LEADERBOARD".equals(size)) {
      return AdSize.LEADERBOARD;
    } else if ("SKYSCRAPER".equals(size)) {
      return AdSize.WIDE_SKYSCRAPER;
    } else {
      return null;
    }
  }

  /**
   * document.addEventListener('onAdLoaded', function(data));
   * document.addEventListener('onAdFailLoad', function(data));
   * document.addEventListener('onAdPresent', function(data));
   * document.addEventListener('onAdDismiss', function(data));
   * document.addEventListener('onAdLeaveApp', function(data));
   */
  private class BannerListener extends AdListener {
    @SuppressLint("DefaultLocale")
    @Override
    public void onAdFailedToLoad(int errorCode) {
      fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_BANNER);
    }

    @Override
    public void onAdLeftApplication() {
      fireAdEvent(EVENT_AD_LEAVEAPP, ADTYPE_BANNER);
    }

    @Override
    public void onAdLoaded() {
      if(autoShowBanner && (!bannerVisible)) {
        showBanner(adPosition, posX, posY);
      }
      fireAdEvent(EVENT_AD_LOADED, ADTYPE_BANNER);
    }

    @Override
    public void onAdOpened() {
      fireAdEvent(EVENT_AD_PRESENT, ADTYPE_BANNER);
    }

    @Override
    public void onAdClosed() {
      fireAdEvent(EVENT_AD_DISMISS, ADTYPE_BANNER);
    }
  }

  /**
   * document.addEventListener('onAdLoaded', function(data));
   * document.addEventListener('onAdFailLoad', function(data));
   * document.addEventListener('onAdPresent', function(data));
   * document.addEventListener('onAdDismiss', function(data));
   * document.addEventListener('onAdLeaveApp', function(data));
   */
   private class InterstitialListener extends AdListener {
    @SuppressLint("DefaultLocale")
    @Override
    public void onAdFailedToLoad(int errorCode) {
      fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_INTERSTITIAL);
    }

    @Override
    public void onAdLeftApplication() {
      fireAdEvent(EVENT_AD_LEAVEAPP, ADTYPE_INTERSTITIAL);
    }

    @Override
    public void onAdLoaded() {
      interstitialReady = true;
      if(autoShowInterstitial) {
        showInterstitial();
      }

      fireAdEvent(EVENT_AD_LOADED, ADTYPE_INTERSTITIAL);
    }

    @Override
    public void onAdOpened() {
      fireAdEvent(EVENT_AD_PRESENT, ADTYPE_INTERSTITIAL);
    }

    @Override
    public void onAdClosed() {
      fireAdEvent(EVENT_AD_DISMISS, ADTYPE_INTERSTITIAL);
      removeInterstitial();

      // if focus on webview of banner, press back button will quit
      // force focus on main view, so that 'backbutton' override will work
      View mainView = getView();
      if (mainView != null) {
        mainView.requestFocus();
      }
    }
  }

  /**
   * document.addEventListener('onAdLoaded', function(data));
   * document.addEventListener('onAdFailLoad', function(data));
   * document.addEventListener('onAdPresent', function(data));
   * document.addEventListener('onAdDismiss', function(data));
   * document.addEventListener('onAdLeaveApp', function(data));
   */
  private class RewardVideoListener implements RewardedVideoAdListener {
    @SuppressLint("DefaultLocale")
    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
      synchronized (mLock) {
        mIsRewardedVideoLoading = false;
      }
      fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_REWARDVIDEO);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
      fireAdEvent(EVENT_AD_LEAVEAPP, ADTYPE_REWARDVIDEO);
    }

    @Override
    public void onRewardedVideoAdLoaded() {
      synchronized (mLock) {
        mIsRewardedVideoLoading = false;
      }
      fireAdEvent(EVENT_AD_LOADED, ADTYPE_REWARDVIDEO);

      if(autoShowRewardVideo) {
        showRewardVideoAd();
      }
    }

    @Override
    public void onRewardedVideoAdOpened() {
      fireAdEvent(EVENT_AD_WILLPRESENT, ADTYPE_REWARDVIDEO);
    }

    @Override
    public void onRewardedVideoStarted() {
      fireAdEvent(EVENT_AD_WILLPRESENT, ADTYPE_REWARDVIDEO);
    }

    @Override
    public void onRewardedVideoAdClosed() {
      fireAdEvent(EVENT_AD_DISMISS, ADTYPE_REWARDVIDEO);

      // if focus on webview of banner, press back button will quit
      // force focus on main view, so that 'backbutton' override will work
      View mainView = getView();
      if (mainView != null) {
        mainView.requestFocus();
      }
    }

    @Override
    public void onRewarded(RewardItem reward) {
      String obj = __getProductShortName();
      String json = String.format("{'adNetwork':'%s','adType':'%s','adEvent':'%s','rewardType':'%s','rewardAmount':%d}",
              obj, ADTYPE_REWARDVIDEO, EVENT_AD_PRESENT, reward.getType(), reward.getAmount());
      fireEvent(obj, EVENT_AD_PRESENT, json);
    }
  }

   /** Gets a string error reason from an error code. */
   public String getErrorReason(int errorCode) {
     String errorReason = "";
     switch(errorCode) {
       case AdRequest.ERROR_CODE_INTERNAL_ERROR:
         errorReason = "Internal error";
         break;
       case AdRequest.ERROR_CODE_INVALID_REQUEST:
         errorReason = "Invalid request";
         break;
       case AdRequest.ERROR_CODE_NETWORK_ERROR:
         errorReason = "Network Error";
         break;
       case AdRequest.ERROR_CODE_NO_FILL:
         errorReason = "No fill";
         break;
     }
     return errorReason;
   }
}
