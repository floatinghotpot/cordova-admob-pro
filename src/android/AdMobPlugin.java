package com.rjfun.cordova.admob;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;

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

    private static final String TEST_BANNER_ID = "ca-app-pub-6869992474017983/4748283957";
    private static final String TEST_INTERSTITIAL_ID = "ca-app-pub-6869992474017983/6225017153";

    private AdSize adSize = AdSize.SMART_BANNER;
    
    public static final String OPT_AD_EXTRAS = "adExtras";
	private JSONObject adExtras = null;
	
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
	public void setOptions(JSONObject options) {
		super.setOptions(options);
		
		if(options.has(OPT_AD_SIZE)) adSize = adSizeFromString(options.optString(OPT_AD_SIZE));
		if(adSize == null) {
			adSize = new AdSize(adWidth, adHeight);
		}
		
		if(options.has(OPT_AD_EXTRAS)) adExtras = options.optJSONObject(OPT_AD_EXTRAS);
	}
	
	@Override
	protected View __createAdView(String adId) {
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
        	ad.loadAd(new PublisherAdRequest.Builder().build());
		} else {
			AdView ad = (AdView) view;
        	ad.loadAd( buildAdRequest() );
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
		InterstitialAd ad = new InterstitialAd(getActivity());
        ad.setAdUnitId(adId);
        ad.setAdListener(new InterstitialListener());
        return ad;
	}
	
	@Override
	protected void __loadInterstitial(Object interstitial) {
		if(interstitial == null) return;
		
		if(interstitial instanceof InterstitialAd) {
			InterstitialAd ad = (InterstitialAd) interstitial;
			ad.loadAd( buildAdRequest() );
		}
	}
	
	@Override
	protected void __showInterstitial(Object interstitial) {
		if(interstitial == null) return;
		
		if(interstitial instanceof InterstitialAd) {
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
        	String jsonData = String.format("{ 'error': %d, 'reason':'%s' }", errorCode, getErrorReason(errorCode));
        	fireEvent(LOGTAG, EVENT_BANNER_FAILRECEIVE, jsonData);
        	
        	fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_BANNER);
        }
        
        @Override
        public void onAdLeftApplication() {
        	fireEvent(LOGTAG, EVENT_BANNER_LEAVEAPP, null);
        	
        	fireAdEvent(EVENT_AD_LEAVEAPP, ADTYPE_BANNER);
        }
        
        @Override
        public void onAdLoaded() {
            if((! bannerVisible) && autoShowBanner) {
            	showBanner(adPosition, posX, posY);
            }
        	fireEvent(LOGTAG, EVENT_BANNER_RECEIVE, null);

        	fireAdEvent(EVENT_AD_LOADED, ADTYPE_BANNER);
        }

        @Override
        public void onAdOpened() {
        	fireEvent(LOGTAG, EVENT_BANNER_PRESENT, null);
        	
        	fireAdEvent(EVENT_AD_PRESENT, ADTYPE_BANNER);
        }
        
        @Override
        public void onAdClosed() {
        	fireEvent(LOGTAG, EVENT_BANNER_DISMISS, null);
        	
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
        	String jsonData = String.format("{ 'error': %d, 'reason':'%s' }", errorCode, getErrorReason(errorCode));
        	fireEvent(LOGTAG, EVENT_INTERSTITIAL_FAILRECEIVE, jsonData);
        	
        	fireAdErrorEvent(EVENT_AD_FAILLOAD, errorCode, getErrorReason(errorCode), ADTYPE_INTERSTITIAL);
        }
        
        @Override
        public void onAdLeftApplication() {
        	fireEvent(LOGTAG, EVENT_INTERSTITIAL_LEAVEAPP, null);
        	
        	fireAdEvent(EVENT_AD_LEAVEAPP, ADTYPE_INTERSTITIAL);
        }
        
        @Override
        public void onAdLoaded() {
            if(autoShowInterstitial) {
            	showInterstitial();
            }
        	fireEvent(LOGTAG, EVENT_INTERSTITIAL_RECEIVE, null);
        	
        	fireAdEvent(EVENT_AD_LOADED, ADTYPE_INTERSTITIAL);
        }

        @Override
        public void onAdOpened() {
        	fireEvent(LOGTAG, EVENT_INTERSTITIAL_PRESENT, null);
        	
        	fireAdEvent(EVENT_AD_PRESENT, ADTYPE_INTERSTITIAL);
        }
        
        @Override
        public void onAdClosed() {
        	fireEvent(LOGTAG, EVENT_INTERSTITIAL_DISMISS, null);
        	
        	fireAdEvent(EVENT_AD_DISMISS, ADTYPE_INTERSTITIAL);

        	removeInterstitial();
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
