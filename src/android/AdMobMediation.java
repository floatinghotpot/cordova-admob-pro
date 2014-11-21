package com.rjfun.cordova.admob;

import org.json.JSONObject;
import android.app.Activity;
import com.google.android.gms.ads.AdRequest;

public abstract class AdMobMediation {
	public AdMobMediation(Activity act, JSONObject options) {}
	public abstract AdRequest.Builder joinAdRequest(AdRequest.Builder builder);
	public void onPause(){}
	public void onResume(){}
	public void onDestroy(){}
}
