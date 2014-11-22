
Change Log:

2014.11.22
	- re-write code for showing banner, now compatible with status bar plugin
	- add new option, bgColor
	- upgrade to iOS sdk v6.12.2

2014.11.3
	- bugfix license validation in iOS code.
	
2014.10.30
	- add mediation adapter for other Ad networks, including facebook audience network, flurry, iad, inmobi, millennial media, mobfox, etc.
	
2014.10.28
	- adding offsetTopBar to avoid banner and webview overlapped by status bar
	
2014.10.21
	- bugfix. Xcode 6 add Metal.framework when build library with default flag 'Link Frameworks Automatically', causing it cannot be used for Xcode 5. rebuild with 'Link Frameworks Automatically' set to false.
	
2014.10.8
	- add support for DFPBannerView (#28), bugfix (#25), separate plugin and SDK binary, remove SDK header files
	
2014.9.14
	- autoShow for banner and interstitial separated
	
2014.9.13
	- when resize iOS UIWebView, the window resize event will not be auto triggered, now trigger it with js code
	
2014.8.24
	- Tested compatible with Intel XDK
	
2014.8.22
	- smart banner width not change on ios orientation change, now fixed
	
2014.8.19
	- set overlap:true to allow banner overlap webview, by default overlap:false and push webview up or down
	
2014.8.12
	- auto calc hash id for admob, to get test ad on device
	
2014.8.11
	- add auto fit orientation change
	
2014.8.9
	- admob plugin totally re-written, tested on android and ios
	- allow create banner and interstitial with options as arg
	
	
	