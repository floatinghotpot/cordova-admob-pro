package com.rjfun.cordova.admob;

import java.util.Arrays;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.admanager.AppEventListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationAdConfiguration;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback ;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
//import com.google.android.gms.ads.rewarded.FullScreenContentCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.ads.mediation.admob.AdMobAdapter;

import java.lang.reflect.Method;
import java.lang.NoSuchMethodException;
import java.util.ArrayList;
import java.util.List;

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
  private static final String TEST_REWARDVIDEO_ID = "ca-app-pub-3940256099942544/5224354917";
  private static final String TAG = LOGTAG;

  private AdSize adSize = AdSize.BANNER;

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
      RequestConfiguration conf= new RequestConfiguration.Builder().setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE).build();

      MobileAds.setRequestConfiguration(conf);
      MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
        @Override
        public void onInitializationComplete(InitializationStatus initializationStatus) {
        }
      });


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
      AdManagerAdView ad = new AdManagerAdView(getActivity());
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
    if(view instanceof AdManagerAdView) {
      AdManagerAdView ad = (AdManagerAdView) view;
      ad.loadAd(buildPublisherAdRequest());
    } else {
      AdView ad = (AdView) view;
      ad.loadAd(buildAdRequest());
    }
  }

  protected AdSize getAdViewSize(View view) {
    if(view instanceof AdManagerAdView) {
      AdManagerAdView dfpView = (AdManagerAdView) view;
      return dfpView.getAdSize();
    } else if(view instanceof AdView) {
      AdView admobView = (AdView) view;
      return admobView.getAdSize();
    } else {
      return new AdSize(0,0);
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

    if(view instanceof AdManagerAdView) {
      AdManagerAdView dfpView = (AdManagerAdView)view;
      dfpView.pause();
    } else {
      AdView admobView = (AdView)view;
      admobView.pause();
    }
  }

  @Override
  protected void __resumeAdView(View view) {
    if(view == null) return;

    if(view instanceof AdManagerAdView) {
      AdManagerAdView dfpView = (AdManagerAdView)view;
      dfpView.resume();
    } else {
      AdView admobView = (AdView)view;
      admobView.resume();
    }
  }

  @Override
  protected void __destroyAdView(View view) {
    if(view == null) return;

    if(view instanceof AdManagerAdView) {
      AdManagerAdView dfpView = (AdManagerAdView)view;
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
    final String _adId= adId;

    if(adId.charAt(0) == '/') {
      // Create AdManagerAdRequest builder
      AdManagerAdRequest.Builder adRequestBuilder = new AdManagerAdRequest.Builder();
      AdManagerInterstitialAd.load(getActivity(),adId, adRequestBuilder.build(), new AdManagerInterstitialAdLoadCallback() {
        @Override
        public void onAdLoaded(@NonNull AdManagerInterstitialAd Ad) {
          // an ad is loaded.
          interstitialAd = Ad;
          interstitialReady = true;
          if(autoShowInterstitial) {
            showInterstitial();
          }
          fireAdEvent(EVENT_AD_LOADED, ADTYPE_INTERSTITIAL);
          Ad.setFullScreenContentCallback(new FullScreenContentCallback() {
            /** Called when the ad failed to show full screen content. */
            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
              int errorCode= adError.getCode();
              fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_INTERSTITIAL);
            }

            /** Called when ad showed the full screen content. */
            @Override
            public void onAdShowedFullScreenContent() {

              fireAdEvent(EVENT_AD_PRESENT, ADTYPE_INTERSTITIAL);
            }

            /** Called when full screen content is dismissed. */
            @Override
            public void onAdDismissedFullScreenContent() {
              fireAdEvent(EVENT_AD_DISMISS, ADTYPE_INTERSTITIAL);
              removeInterstitial();

              // if focus on webview of banner, press back button will quit
              // force focus on main view, so that 'backbutton' override will work
              View mainView = getView();
              if (mainView != null) {
                mainView.requestFocus();
              }
            }
          });
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
          // Handle the error
          int errorCode = loadAdError.getCode();
          fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_INTERSTITIAL);
          interstitialAd = null;
        }
      } );

      return null;
    } else {
      AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
     InterstitialAd.load(getActivity(),adId, adRequestBuilder.build(), new InterstitialAdLoadCallback() {
        @Override
        public void onAdLoaded(@NonNull InterstitialAd Ad ) {
          // an ad is loaded.
          interstitialAd = Ad;
          interstitialReady = true;
          if(autoShowInterstitial) {
            showInterstitial();
          }
          fireAdEvent(EVENT_AD_LOADED, ADTYPE_INTERSTITIAL);
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
          // Handle the error
          int errorCode = loadAdError.getCode();
          fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_INTERSTITIAL);
          interstitialAd = null;

        }
      } );

      return null;
    }
  }

  @Override
  protected void __loadInterstitial(Object interstitial) {
  }

  @Override
  protected void __showInterstitial(Object interstitial) {
    if(interstitial == null) return;

    if(interstitial instanceof AdManagerInterstitialAd) {
      AdManagerInterstitialAd ad = (AdManagerInterstitialAd) interstitial;
      ad.show(getActivity());

    }else if(interstitial instanceof InterstitialAd){
      InterstitialAd ad = (InterstitialAd) interstitial;
      ad.show(getActivity());
    }
  }

  @Override
  protected void __destroyInterstitial(Object interstitial) {
    if(interstitial == null) return;
    else
      interstitial = null;
    if(interstitial instanceof InterstitialAd) {
      InterstitialAd ad = (InterstitialAd) interstitial;
      ad.setFullScreenContentCallback(null);
    }
  }

  @Override
  protected Object __prepareRewardVideoAd(String adId) {
    // safety check to avoid exceptoin in case adId is null or empty
    if(adId==null || adId.length()==0) adId = TEST_REWARDVIDEO_ID;

    AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
    RewardedAd.load(getActivity(), adId, adRequest, new RewardedAdLoadCallback(){
      @Override
      public void onAdFailedToLoad(LoadAdError loadAdError) {
        // Handle the error.
        synchronized (mLock) {
          mIsRewardedVideoLoading = false;
        }
        rewardVideoAd = null; //<-- Added line before the fireAdEvent
        int errorCode = loadAdError.getCode();
        fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_REWARDVIDEO);
        Log.d(TAG, loadAdError.getMessage());
      }

      @Override
      public void onAdLoaded(RewardedAd rewardedAd) {
        rewardVideoAd = rewardedAd;

        synchronized (mLock) {
          mIsRewardedVideoLoading = true;
        }
        fireAdEvent(EVENT_AD_LOADED, ADTYPE_REWARDVIDEO);

        if(autoShowRewardVideo) {
          showRewardVideoAd();
        }

        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
          /** Called when the ad failed to show full screen content. */
          @Override
          public void onAdFailedToShowFullScreenContent(AdError adError) {
            int errorCode= adError.getCode();
            fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_REWARDVIDEO);
          }

          /** Called when ad showed the full screen content. */
          @Override
          public void onAdShowedFullScreenContent() {
            fireAdEvent(EVENT_AD_WILLPRESENT, ADTYPE_REWARDVIDEO);
          }

          /** Called when full screen content is dismissed. */
          @Override
          public void onAdDismissedFullScreenContent() {
            rewardVideoAd = null; //<-- Added line before the fireAdEvent
            fireAdEvent(EVENT_AD_DISMISS, ADTYPE_REWARDVIDEO);

            // if focus on webview of banner, press back button will quit
            // force focus on main view, so that 'backbutton' override will work
            View mainView = getView();
            if (mainView != null) {
              mainView.requestFocus();
            }
          }
        });

      }
    });

    return null;
  }

  @Override
  protected void __showRewardVideoAd(Object rewardvideo) {
    if(rewardvideo == null) return;

    if(rewardvideo instanceof RewardedAd) {
      RewardedAd ad = (RewardedAd) rewardvideo;
      //if(ad.isLoaded())
      {
        ad.show(getActivity(), new OnUserEarnedRewardListener() {
          @Override
          public void onUserEarnedReward(@NonNull RewardItem reward) {
            String obj = __getProductShortName();
            String json = String.format("{'adNetwork':'%s','adType':'%s','adEvent':'%s','rewardType':'%s','rewardAmount':%d}",
                    obj, ADTYPE_REWARDVIDEO, EVENT_AD_PRESENT, reward.getType(), reward.getAmount());
            fireEvent(obj, EVENT_AD_PRESENT, json);
          }
        });
      }
    }
  }

  @SuppressLint("DefaultLocale")
  private AdRequest buildAdRequest() {
    final Activity activity = getActivity();
    AdRequest.Builder builder = new AdRequest.Builder();

    if (isTesting) {
      String ANDROID_ID = Settings.Secure.getString(activity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
      String deviceId = md5(ANDROID_ID).toUpperCase();
      List<String> testDeviceIds = Arrays.asList(deviceId);
      RequestConfiguration configuration =
              new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
      MobileAds.setRequestConfiguration(configuration);
      // This will request test ads on the emulator and deviceby passing this hashed device ID.
     /* String ANDROID_ID = Settings.Secure.getString(activity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
      String deviceId = md5(ANDROID_ID).toUpperCase();
      builder = builder.addTestDevice(deviceId).addTestDevice(AdRequest.DEVICE_ID_EMULATOR);*/
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

    if(mLocation != null) builder.setLocation(mLocation);
    if(mForFamily != null) {
      Bundle extras = new Bundle();
      extras.putBoolean("is_designed_for_families", true);
      builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
    }
    if(mForChild != null) {
      /*RequestConfiguration conf= new RequestConfiguration.Builder().setTagForChildDirectedTreatment(MediationAdConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE).build();

      MobileAds.setRequestConfiguration(conf);
      MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
        @Override
        public void onInitializationComplete(InitializationStatus initializationStatus) {
        }
      });*/

      //builder.tagForChildDirectedTreatment(true);
    }
    if(mContentURL != null) {
      builder.setContentUrl(mContentURL);
    }

    return builder.build();
  }

  @SuppressLint("DefaultLocale")
  private AdManagerAdRequest buildPublisherAdRequest() {
    final Activity activity = getActivity();
    AdManagerAdRequest.Builder builder = new AdManagerAdRequest.Builder();

    if (isTesting) {
      String ANDROID_ID = Settings.Secure.getString(activity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
      String deviceId = md5(ANDROID_ID).toUpperCase();
      List<String> testDeviceIds = Arrays.asList(deviceId);
      RequestConfiguration configuration =
              new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
      MobileAds.setRequestConfiguration(configuration);
      // This will request test ads on the emulator and deviceby passing this hashed device ID.

     // builder = builder.addTestDevice(deviceId).addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
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

      builder.addNetworkExtrasBundle(AdMobAdapter.class, bundle  ).build();
     // builder = builder.addNetworkExtras(new AdMobExtras(bundle));
    }

    if(mLocation != null) builder.setLocation(mLocation);
    if(mForFamily != null) {
      Bundle extras = new Bundle();
      extras.putBoolean("is_designed_for_families", ("yes".compareToIgnoreCase(mForFamily) == 0));
      builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
    }
    if(mForChild != null) {
      /*RequestConfiguration conf= new RequestConfiguration.Builder().setTagForChildDirectedTreatment(MediationAdConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE ).build();

      MobileAds.setRequestConfiguration(conf);
      MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
        @Override
        public void onInitializationComplete(InitializationStatus initializationStatus) {
        }
      });*/
     // builder.tagForChildDirectedTreatment("yes".compareToIgnoreCase(mForChild) == 0);
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
      return AdSize.BANNER;
    } else if ("MEDIUM_RECTANGLE".equals(size)) {
      return AdSize.MEDIUM_RECTANGLE;
    } else if ("FULL_BANNER".equals(size)) {
      return AdSize.FULL_BANNER;
    } else if ("LEADERBOARD".equals(size)) {
      return AdSize.LEADERBOARD;
    } else if ("SKYSCRAPER".equals(size)) {
      return AdSize.WIDE_SKYSCRAPER;
    } else if ("LARGE_BANNER".equals(size)) {
      return AdSize.LARGE_BANNER;
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
   class BannerListener extends AdListener {
    @SuppressLint("DefaultLocale")
    @Override
    public void onAdFailedToLoad(LoadAdError var1) {
      fireAdErrorEvent(EVENT_AD_FAILLOAD, var1.getCode(), getErrorReason(var1.getCode()), ADTYPE_BANNER);
    }

   // @Override
   // public void onAdLeftApplication() {
    // fireAdEvent(EVENT_AD_LEAVEAPP, ADTYPE_BANNER);
    //}

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

  /**
   * document.addEventListener('onAdLoaded', function(data));
   * document.addEventListener('onAdFailLoad', function(data));
   * document.addEventListener('onAdPresent', function(data));
   * document.addEventListener('onAdDismiss', function(data));
   * document.addEventListener('onAdLeaveApp', function(data));
   */

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
