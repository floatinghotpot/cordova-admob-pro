


# FAQ #

Here are some FAQ related with plugin.

* I see error in LogCat "E/GooglePlayServicesUtil(13237): The Google Play services resources were not found."

Answer: It can be safely ignored, you will still get Ad.

See: 

- [AdMob official FAQ](https://developers.google.com/mobile-ads-sdk/android-legacy/docs/admob/faq#resourcesnotfound)

- [Stackoverflow discussion](http://stackoverflow.com/questions/18068627/logcat-message-the-google-play-services-resources-were-not-found-check-your-pr)

* Why I see black/blank Ad, with log "W/Ads (13237): Failed to load ad: 3"?

Answer: Error code 3 means "No fill", it happens when server cannot find suitable Ad resources in the inventory for your Ad request. 

Advice: Check your Ad Unit Id. 

1. Though the argument key is "publisherId" for historical reason, the real purpose is Ad Unit Id, so do not use "pub-xxx", use "pub-xxx/xxx".

2. Banner and interstitial Ad require different Ad unit Id, which is set in AdMob web console (http://www.admob.com/). If use banner Id for interstitial, it won't work.

3. Some developers use video-only Ad for interstitial, the video inventory is not rich enough according to AdMob support team, and sometimes will not find suitable video Ad for you. You should void using video-only if hope to achieve a high fillrate.

* What shall I do with log "Use AdRequest.Builder.addTestDevice("xxxxxxxxxxxxxxx") to get test ads on this device"?

Answer: 

- If you are using cordova-plugin-admob, copy the code from log, and add to the java or obj-C code.

- If you are using AdMob Plugin Pro (cordova-admob-pro), you can just ignore it, as the plugin code already get it done.

* I see no Ads, with log "W/Ads(5038): Could not parse mediation config"

Answer: Check your Ad Unit Id. If a valid Ad unit Id is not given, it will cause such a error. 

Sometimes in testing mode, the Ad server may also trigger such a error message. See [AdMob official support](https://groups.google.com/forum/#!searchin/google-admob-ads-sdk/Could$20not$20parse$20mediation$20config/google-admob-ads-sdk/Rcdb0py6qKs/GDVa_l9mOeoJ)

* I see log "W/Ads ( 1799): JS: The page displayed insecure content!"

It's a warning message when the AdMob SDK switching request over HTTP and HTTPS protocol.

Answer from AdMob support team: 

> Both BANNER and SMART_BANNER should work. The "insecure content" could be related to some experiments we're running with making the ad request in https. It's possible that certain creatives are loading assets over http. That should be just a warning though and not cause a no fill. Can you try SMART_BANNER again and confirm?

See [AdMob official support forum](https://groups.google.com/forum/#!topic/google-admob-ads-sdk/g6tSDG4QOhE).

# Where to seek more support ? #

* AdMob official support forum

If your questions are related with logs with prefix "W/Ads", it's printed by the AdMob SDK, and related with Ad service.

Basically, please ask in AdMob official support forum](https://groups.google.com/forum/#!forum/google-admob-ads-sdk)

* Ask in StackOverflow.com

It's a good knowledge base, detailed explanation and source code examples.


*updated on 9/15/2014*


