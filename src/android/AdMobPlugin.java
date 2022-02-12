package com.rjfun.cordova.admob;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback ;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.ads.mediation.admob.AdMobAdapter;

import com.rjfun.cordova.ad.GenericAdPlugin;

public class AdMobPlugin extends GenericAdPlugin {
  private static final String TAG = "AdMobPlugin";

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

  private AdSize adSize = AdSize.BANNER;

  private boolean mInited = false;
  private final Object mLock = new Object();

  public static final String OPT_GENDER = "gender";
  public static final String OPT_FORCHILD = "forChild";
  public static final String OPT_FORFAMILY = "forFamily";
  public static final String OPT_CONTENTURL = "contentUrl";
  public static final String OPT_CUSTOMTARGETING = "customTargeting";
  public static final String OPT_EXCLUDE = "exclude";

  protected String mGender = null;
  protected boolean mForChild = false;
  protected boolean mForFamily = false;
  protected String mContentURL = null;
  protected JSONObject mCustomTargeting = null;
  protected JSONArray mExclude = null;

  // cache the private objects here
  private AdManagerInterstitialAd mAdManagerInterstitialAd = null;
  private InterstitialAd mInterstitialAd = null;
  private RewardedAd mRewardedAd = null;

  private HashMap<String, AdMobMediation> mediations = new HashMap<String, AdMobMediation>();

  @Override
  protected void pluginInitialize() {
    super.pluginInitialize();
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

  private void ensureInited() {
    synchronized (mLock) {
      if (!mInited) {
        MobileAds.initialize(cordova.getContext(), new OnInitializationCompleteListener() {
          @Override
          public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            mInited = true;
          }
        });
      }
    }
  }

  private boolean optBool(JSONObject options, String key) {
    if(options.optBoolean(key)) return true;
    String str = options.optString(key);
    if("yes".equalsIgnoreCase(str) || "true".equalsIgnoreCase(str)) return true;
    int n = options.optInt(key);
    if(n != 0) return true;
    return false;
  }

  @Override
  public void setOptions(JSONObject options) {
    super.setOptions(options);

    if(options.has(OPT_AD_SIZE)) adSize = adSizeFromString(options.optString(OPT_AD_SIZE));
    if(adSize == null) {
      adSize = new AdSize(adWidth, adHeight);
    }

    mForFamily = optBool(options, OPT_FORFAMILY);
    mForChild = optBool(options, OPT_FORCHILD);

    if(options.has(OPT_GENDER)) {
      mGender = options.optString(OPT_GENDER);
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

    ensureInited();

    RequestConfiguration.Builder builder = new RequestConfiguration.Builder();
    boolean configChanged = false;
    if(isTesting) {
      String ANDROID_ID = Settings.Secure.getString(cordova.getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
      String deviceId = md5(ANDROID_ID).toUpperCase();
      builder.setTestDeviceIds(Arrays.asList(deviceId));
      configChanged = true;
    }
    if(mForChild) {
      builder.setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE);
      configChanged = true;
    }
    if(configChanged)
      MobileAds.setRequestConfiguration(builder.build());
  }

  @Override
  protected View __createAdView(String adId) {
    ensureInited();

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
      ad.loadAd(buildAdManagerAdRequest());
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
    ensureInited();

    interstitialReady = false;
    // safety check to avoid exception in case adId is null or empty
    if(adId==null || adId.length()==0) adId = TEST_INTERSTITIAL_ID;

    // we return the adId instead of real ad object
    return adId;
  }

  @Override
  protected void __loadInterstitial(Object interstitial) {
    if(interstitial == null) return;
    if(! (interstitial instanceof String)) return;
    String adId = (String) interstitial;

    if(adId.charAt(0) == '/') {
      AdManagerInterstitialAd.load(getActivity(),adId, buildAdManagerAdRequest(), new AdManagerInterstitialAdLoadCallback() {
        @Override
        public void onAdLoaded(@NonNull AdManagerInterstitialAd Ad) {
          // an ad is loaded.
          mAdManagerInterstitialAd = Ad;
          interstitialReady = true;
          fireAdEvent(EVENT_AD_LOADED, ADTYPE_INTERSTITIAL);

          if(autoShowInterstitial) {
            showInterstitial();
          }
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
          // Handle the error
          int errorCode = loadAdError.getCode();
          fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_INTERSTITIAL);
          mAdManagerInterstitialAd = null;
          interstitialReady = false;
        }
      });

    } else {
      InterstitialAd.load(getActivity(),adId, buildAdRequest(), new InterstitialAdLoadCallback() {
        @Override
        public void onAdLoaded(@NonNull InterstitialAd Ad ) {
          // an ad is loaded.
          mInterstitialAd = Ad;
          interstitialReady = true;
          fireAdEvent(EVENT_AD_LOADED, ADTYPE_INTERSTITIAL);

          if(autoShowInterstitial) {
            showInterstitial();
          }
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
          // Handle the error
          int errorCode = loadAdError.getCode();
          fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_INTERSTITIAL);
          mInterstitialAd = null;
          interstitialReady = false;
        }
      });
    }
  }

  @Override
  protected void __showInterstitial(Object interstitial) {
    if(interstitial == null) return;
    if(! (interstitial instanceof String)) return;
    String adId = (String) interstitial;

    if(adId.charAt(0) == '/') {
      if(mAdManagerInterstitialAd != null) {
        mAdManagerInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
          @Override
          public void onAdFailedToShowFullScreenContent(AdError adError) {
            int errorCode = adError.getCode();
            fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_INTERSTITIAL);
          }

          @Override
          public void onAdShowedFullScreenContent() {
            fireAdEvent(EVENT_AD_PRESENT, ADTYPE_INTERSTITIAL);
            mAdManagerInterstitialAd = null;
            interstitialReady = false;
          }

          @Override
          public void onAdDismissedFullScreenContent() {
            fireAdEvent(EVENT_AD_DISMISS, ADTYPE_INTERSTITIAL);

            // if focus on webview of banner, press back button will quit
            // force focus on main view, so that 'backbutton' override will work
            View mainView = getView();
            if (mainView != null) {
              mainView.requestFocus();
            }
          }

          @Override
          public void onAdImpression() {
            //fireAdEvent(EVENT_AD_PRESENT, ADTYPE_INTERSTITIAL);
          }

          @Override
          public void onAdClicked() {
            fireAdEvent(EVENT_AD_LEAVEAPP, ADTYPE_INTERSTITIAL);
          }
        });
        mAdManagerInterstitialAd.show(getActivity());
      }

    } else {
      if(mInterstitialAd != null) {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
          @Override
          public void onAdFailedToShowFullScreenContent(AdError adError) {
            int errorCode = adError.getCode();
            fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_INTERSTITIAL);
          }

          @Override
          public void onAdShowedFullScreenContent() {
            fireAdEvent(EVENT_AD_PRESENT, ADTYPE_INTERSTITIAL);
            mInterstitialAd = null;
            interstitialReady = false;
          }

          @Override
          public void onAdDismissedFullScreenContent() {
            fireAdEvent(EVENT_AD_DISMISS, ADTYPE_INTERSTITIAL);

            // if focus on webview of banner, press back button will quit
            // force focus on main view, so that 'backbutton' override will work
            View mainView = getView();
            if (mainView != null) {
              mainView.requestFocus();
            }
          }

          @Override
          public void onAdImpression() {
            //fireAdEvent(EVENT_AD_PRESENT, ADTYPE_INTERSTITIAL);
          }

          @Override
          public void onAdClicked() {
            fireAdEvent(EVENT_AD_LEAVEAPP, ADTYPE_INTERSTITIAL);
          }
        });
        mInterstitialAd.show(getActivity());
      }
    }
  }

  @Override
  protected void __destroyInterstitial(Object interstitial) {
    if(interstitial == null) return;
    if(! (interstitial instanceof String)) return;
    String adId = (String) interstitial;

    if(adId.charAt(0) == '/') {
      if( mAdManagerInterstitialAd != null) {
        mAdManagerInterstitialAd.setFullScreenContentCallback(null);
        mAdManagerInterstitialAd = null;
      }
    } else {
      if( mInterstitialAd != null) {
        mInterstitialAd.setFullScreenContentCallback(null);
        mInterstitialAd = null;
      }
    }
    interstitialReady = false;
  }

  @Override
  protected Object __prepareRewardVideoAd(String adId) {
    ensureInited();

    // safety check to avoid exceptoin in case adId is null or empty
    if(adId==null || adId.length()==0) adId = TEST_REWARDVIDEO_ID;

    if(adId.charAt(0) == '/') {
      RewardedAd.load(getActivity(), adId, buildAdManagerAdRequest(), new RewardedAdLoadCallback() {
        @Override
        public void onAdLoaded(RewardedAd rewardedAd) {
          mRewardedAd = rewardedAd;
          fireAdEvent(EVENT_AD_LOADED, ADTYPE_REWARDVIDEO);
          if (autoShowRewardVideo) {
            showRewardVideoAd();
          }
        }

        @Override
        public void onAdFailedToLoad(LoadAdError loadAdError) {
          mRewardedAd = null; //<-- Added line before the fireAdEvent
          int errorCode = loadAdError.getCode();
          fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_REWARDVIDEO);
          Log.d(TAG, loadAdError.getMessage());
        }
      });
    } else {
      RewardedAd.load(getActivity(), adId, buildAdRequest(), new RewardedAdLoadCallback() {
        @Override
        public void onAdLoaded(RewardedAd rewardedAd) {
          mRewardedAd = rewardedAd;
          fireAdEvent(EVENT_AD_LOADED, ADTYPE_REWARDVIDEO);

          if (autoShowRewardVideo) {
            showRewardVideoAd();
          }
        }

        @Override
        public void onAdFailedToLoad(LoadAdError loadAdError) {
          mRewardedAd = null; //<-- Added line before the fireAdEvent
          int errorCode = loadAdError.getCode();
          fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_REWARDVIDEO);
          Log.d(TAG, loadAdError.getMessage());
        }
      });
    }

    // we return the adId instead of real ad object
    return adId;
  }

  @Override
  protected void __showRewardVideoAd(Object rewardvideo) {
    if(rewardvideo == null) return;
    if(! (rewardvideo instanceof String)) return;
    String adId = (String) rewardvideo;

    if(mRewardedAd == null) return;

    mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
      @Override
      public void onAdFailedToShowFullScreenContent(AdError adError) {
        int errorCode= adError.getCode();
        fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_REWARDVIDEO);
      }

      @Override
      public void onAdShowedFullScreenContent() {
        fireAdEvent(EVENT_AD_WILLPRESENT, ADTYPE_REWARDVIDEO);
        mRewardedAd = null;
      }

      @Override
      public void onAdDismissedFullScreenContent() {
        fireAdEvent(EVENT_AD_DISMISS, ADTYPE_REWARDVIDEO);

        // if focus on webview of banner, press back button will quit
        // force focus on main view, so that 'backbutton' override will work
        View mainView = getView();
        if (mainView != null) {
          mainView.requestFocus();
        }
      }

      @Override
      public void onAdImpression() {
        //fireAdEvent(EVENT_AD_PRESENT, ADTYPE_REWARDVIDEO);
      }

      @Override
      public void onAdClicked() {
        fireAdEvent(EVENT_AD_LEAVEAPP, ADTYPE_REWARDVIDEO);
      }
    });

    mRewardedAd.show(getActivity(), new OnUserEarnedRewardListener() {
        @Override
        public void onUserEarnedReward(@NonNull RewardItem reward) {
          String obj = __getProductShortName();
          String json = String.format("{'adNetwork':'%s','adType':'%s','adEvent':'%s','rewardType':'%s','rewardAmount':%d}",
                  obj, ADTYPE_REWARDVIDEO, EVENT_AD_PRESENT, reward.getType(), reward.getAmount());
          fireEvent(obj, EVENT_AD_PRESENT, json);
        }
    });
  }

  @SuppressLint("DefaultLocale")
  private AdRequest buildAdRequest() {
    AdRequest.Builder builder = new AdRequest.Builder();

    for(String key: mediations.keySet()) {
      AdMobMediation m = mediations.get(key);
      if(m != null) {
        builder = m.joinAdRequest(builder);
      }
    }

    //if(mLocation != null) builder.setLocation(mLocation);

    if(mForFamily) {
      Bundle extras = new Bundle();
      extras.putBoolean("is_designed_for_families", true);
      builder = builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
    }
    if(mContentURL != null) {
      builder = builder.setContentUrl(mContentURL);
    }

    return builder.build();
  }

  @SuppressLint("DefaultLocale")
  private AdManagerAdRequest buildAdManagerAdRequest() {
    AdManagerAdRequest.Builder builder = new AdManagerAdRequest.Builder();

    //if(mLocation != null) builder.setLocation(mLocation);

    if(mForFamily) {
      Bundle extras = new Bundle();
      extras.putBoolean("is_designed_for_families", true);
      builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
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
      for(int i=0; i<n; i++) {
        builder.addCategoryExclusion(mExclude.optString(i, ""));
      }
    }

    return builder.build();
  }

  @Override
  public void onPause(boolean multitasking) {
    for(String key: mediations.keySet()) {
      AdMobMediation m = mediations.get(key);
      if(m != null) m.onPause();
    }

    super.onPause(multitasking);
  }

  @Override
  public void onResume(boolean multitasking) {
    super.onResume(multitasking);
    for(String key: mediations.keySet()) {
      AdMobMediation m = mediations.get(key);
      if(m != null) m.onResume();
    }
  }

  @Override
  public void onDestroy() {
    for(String key: mediations.keySet()) {
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

    @Override
    public void onAdClicked() {
      fireAdEvent(EVENT_AD_LEAVEAPP, ADTYPE_BANNER);
    }

    @Override
    public void onAdImpression() {
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
